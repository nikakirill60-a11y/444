#!/usr/bin/env python3
"""Static menu dependency inventory. Requires jawa==2.2.0; never loads donor Java."""
import hashlib
import io
import json
from pathlib import Path
import sys
import zipfile
from jawa.cf import ClassFile
from jawa.constants import String

CLIENT_SHA = '66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc'
MENU_CLASS = 'null/iilIjliijlIIjilillIjjjlIliIjijiI'


def code_inventory(cf):
    """Symbolic call/constant index, not an interpretation of camera or UI logic.

    Preserve overloaded descriptors and bytecode offsets. Switch operands in jawa
    can be dictionaries; do not mistake them for constant-pool references.
    """
    methods = []
    for method in cf.methods:
        row = {'name': method.name.value, 'descriptor': method.descriptor.value}
        if method.code is None:
            row['has_code'] = False
            methods.append(row)
            continue
        bytecode = method.code._code
        instructions = list(method.code.disassemble())
        row.update(has_code=True, bytes=len(bytecode),
                   sha256=hashlib.sha256(bytecode).hexdigest(),
                   instruction_count=len(instructions), literals=[], calls=[], switches=[])
        for ins in instructions:
            if ins.mnemonic in ('lookupswitch', 'tableswitch'):
                if ins.mnemonic == 'lookupswitch':
                    cases = {str(k): ins.pos + v for k, v in ins.operands[0].items()}
                    default = ins.pos + ins.operands[1].value
                else:
                    default = ins.pos + ins.operands[0].value
                    low, high = ins.operands[1].value, ins.operands[2].value
                    cases = {str(low + i): ins.pos + operand.value
                             for i, operand in enumerate(ins.operands[3:])}
                    if len(cases) != high - low + 1:
                        raise ValueError('Invalid tableswitch')
                targets = {i.pos for i in instructions}
                if not set(cases.values()).union({default}).issubset(targets):
                    raise ValueError('Switch target is not an instruction')
                row['switches'].append({'offset': ins.pos, 'cases': cases, 'default': default})
            if not ins.operands or getattr(getattr(ins.operands[0], 'op_type', None), 'name', None) != 'CONSTANT_INDEX':
                continue
            constant = cf.constants[ins.operands[0].value]
            if isinstance(constant, String):
                row['literals'].append({'offset': ins.pos, 'value': constant.string.value})
            elif ins.mnemonic.startswith('invoke'):
                row['calls'].append({'offset': ins.pos, 'opcode': ins.mnemonic,
                                     'owner': constant.class_.name.value,
                                     'name': constant.name_and_type.name.value,
                                     'descriptor': constant.name_and_type.descriptor.value})
        methods.append(row)
    return {'class_sha256_scope': 'each method Code byte array, not decompiled source',
            'semantics_inferred': False, 'methods': methods}


def audit(client):
    data = Path(client).read_bytes()
    if hashlib.sha256(data).hexdigest() != CLIENT_SHA:
        raise ValueError('Unexpected donor client SHA256')
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        cf = ClassFile(io.BytesIO(archive.read(MENU_CLASS + '.class')))
    strings = sorted({c.string.value for c in cf.constants.find(type_=String)})
    models = [s for s in strings if s.endswith('.m2033model')]
    return {
        'source': 'user-supplied METRO_MOD client.jar', 'source_sha256': CLIENT_SHA,
        'menu_class': MENU_CLASS, 'superclass': cf.super_.name.value,
        'code_inventory': code_inventory(cf),
        'model_references': models,
        'sound_events': [s for s in strings if s.startswith('metro2033server:menu.')],
        'additional_texture_literals': [s for s in strings if s.endswith('.png')],
        'original_server_literal': 'play.metro2033server.ru:25565',
        'requested_port_target': 'localhost:25565',
        'target_implemented': False,
        'status': 'blocked: original scene model bytes not available; no substitute menu installed',
        'limitations': [
            'Constant-pool references are not extracted model files or a completed menu port.',
            'Model-internal materials, textures, animations and recursive dependencies remain unaudited.',
            'Original website/skin/status endpoints must not be activated by the port.',
            'localhost means the computer running the Minecraft client, not a hosted remote server.',
            'The original 1.7.10 rendering code needs adaptation for Forge 1.12.2.'
        ]
    }


if __name__ == '__main__':
    if len(sys.argv) != 3:
        raise SystemExit('Usage: python tools/audit_metromod_menu.py client.jar report.json')
    result = audit(sys.argv[1])
    Path(sys.argv[2]).write_text(json.dumps(result, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print('Inventoried', len(result['model_references']), 'model references; no donor code executed.')
