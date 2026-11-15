#!/usr/bin/env python3
"""Static, client-only medical item reference audit. Requires jawa==2.2.0; never executes Java."""
import hashlib
import io
import json
from pathlib import Path
import sys
import zipfile
from jawa.cf import ClassFile
SOURCE_SHA='66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc'
REGISTRY='null/iIIiiIIiljijlllljiljiIlilllIlliI'
BASE='null/IjllllIljljijijiIliIijjiIjiiIlij'


def audit(path):
    raw=Path(path).read_bytes()
    if hashlib.sha256(raw).hexdigest()!=SOURCE_SHA:raise ValueError('Unexpected donor client')
    with zipfile.ZipFile(io.BytesIO(raw)) as z:
        def load(name):return ClassFile(io.BytesIO(z.read(name+'.class')))
        registry=load(REGISTRY);items={}
        for method in registry.methods:
            if not method.code:continue
            ins=list(method.code.disassemble())
            for k,i in enumerate(ins):
                if i.mnemonic!='ldc':continue
                value=registry.constants.get(i.operands[0].value)
                if type(value).__name__!='String' or value.string.value not in ('bint','shpric','medspirt'):continue
                preceding=ins[max(0,k-4):k]
                if not any(t.mnemonic=='new' for t in preceding):continue
                size=ins[k-1].operands[0].value
                constructor=next(registry.constants.get(t.operands[0].value) for t in ins[k+1:k+8] if t.mnemonic=='invokespecial')
                field=next(registry.constants.get(t.operands[0].value) for t in ins[k+1:k+12] if t.mnemonic=='putstatic')
                items[value.string.value]={'field':field.name_and_type.name.value,'constructor':constructor.class_.name.value,
                                           'stack_size':size,'registration_string_offset':i.pos,'external_field_references':[]}
        fields={row['field']:name for name,row in items.items()};classes=0
        for name in z.namelist():
            if not name.endswith('.class'):continue
            classes+=1
            if name==REGISTRY+'.class':continue
            c=load(name[:-6]);refs={v.index:fields[v.name_and_type.name.value] for v in c.constants
                if type(v).__name__=='FieldReference' and v.class_.name.value==REGISTRY and v.name_and_type.name.value in fields}
            if not refs:continue
            for method in c.methods:
                if not method.code:continue
                for i in method.code.disassemble():
                    if i.mnemonic in ('getstatic','putstatic') and i.operands[0].value in refs:
                        items[refs[i.operands[0].value]]['external_field_references'].append(dict(class_name=name,method=method.name.value,descriptor=method.descriptor.value,offset=i.pos))
        base=load(BASE)
        return {'source':'client.jar','sha256':SOURCE_SHA,'classes_scanned':classes,'items':items,
                'base_class':BASE,'base_class_sha256':hashlib.sha256(z.read(BASE+'.class')).hexdigest(),
                'base_methods':[{'name':m.name.value,'descriptor':m.descriptor.value} for m in base.methods],
                'conclusion':'No standalone healing handler confirmed; import only as materials, not healing consumables.',
                'limits':'Static client audit only. External server plugins/scripts and generic numeric-ID logic are not proven absent.'}


if __name__=='__main__':
    path,output=sys.argv[1:];report=audit(path)
    Path(output).write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print(json.dumps({k:v for k,v in report.items() if k!='base_methods'},ensure_ascii=False,indent=2))
