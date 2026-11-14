#!/usr/bin/env python3
"""Statically disassemble selected non-weapon classes of the exact supplied client.
Dependency: jawa==2.2.0. Does not execute Java or read client credentials/configs.
"""
import argparse
import hashlib
import io
import json
from pathlib import Path
import zipfile
from jawa.cf import ClassFile

EXPECTED = '66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc'
TARGETS = {
    'filter_item': 'null/ilIjjjjlliIIIIliIijiIljjlliIIIil',
    'mask_item': 'null/IljIjijIIlljjlilIillIjIllIjjljjj',
    'watch_controller': 'null/jiIijIIliijijljjlIililjlljIljIji',
    'flask_item': 'null/IjIjIIIijIjlljljIljiljjiliilIlll',
    'gate_base': 'null/lIjijlilIlIIIiIiIlIIIIlIijljllij',
    'kikimora_entity': 'null/IiliiljljjiljIljiIjiIjlIjjjIIjli',
    'nosach_entity': 'null/IIIjIiIIjIiiiliIijjIIIlliIliljlj',
}

def resolved(cf, index):
    c = cf.constants[index]
    if hasattr(c, 'class_'):
        return c.class_.name.value + '.' + c.name_and_type.name.value + c.name_and_type.descriptor.value
    if hasattr(c, 'string'):
        return repr(c.string.value)
    if hasattr(c, 'name'):
        return c.name.value
    return repr(c.value) if hasattr(c, 'value') else str(c)


def inspect(jar, output):
    data = jar.read_bytes()
    digest = hashlib.sha256(data).hexdigest()
    if digest != EXPECTED:
        raise ValueError('Donor SHA256 mismatch; obfuscated class map is build-specific')
    output.mkdir(parents=True, exist_ok=True)
    index = {'sha256': digest, 'bytes': len(data), 'mode': 'static_only', 'classes': []}
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        bad = archive.testzip()
        if bad:
            raise ValueError('ZIP CRC failure: ' + bad)
        for label, name in TARGETS.items():
            raw = archive.read(name + '.class')
            cf = ClassFile(io.BytesIO(raw))
            lines = ['CLASS ' + name, 'SUPER ' + cf.super_.name.value]
            methods = []
            for m in cf.methods:
                signature = m.name.value + m.descriptor.value
                methods.append(signature)
                lines.append('\nMETHOD ' + signature)
                if not m.code:
                    continue
                for ins in m.code.disassemble():
                    operands = []
                    for operand in ins.operands:
                        if isinstance(operand, dict):
                            operands.append(json.dumps(operand, sort_keys=True))
                        elif operand.op_type.name == 'CONSTANT_INDEX':
                            operands.append(resolved(cf, operand.value))
                        else:
                            operands.append(str(operand.value))
                    lines.append((f'{ins.pos:05d} {ins.mnemonic} ' + ' '.join(operands)).rstrip())
            text = '\n'.join(lines) + '\n'
            (output / (label + '.txt')).write_text(text, encoding='utf-8')
            index['classes'].append({'label': label, 'class': name, 'methods': methods,
                'class_sha256': hashlib.sha256(raw).hexdigest(),
                'dump_sha256': hashlib.sha256(text.encode()).hexdigest()})
    (output / 'index.json').write_text(json.dumps(index, ensure_ascii=False, indent=2) + '\n')
    print('STATIC_INSPECTION_OK', digest, len(index['classes']))


if __name__ == '__main__':
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('--jar', type=Path, required=True)
    p.add_argument('--output', type=Path, required=True)
    args = p.parse_args()
    inspect(args.jar, args.output)
