#!/usr/bin/env python3
"""Statically read the donor's straight-line decor catalogue. Never runs donor Java.

Optional audit dependency: jawa==2.2.0. Refuse unknown bytecode, do not silently
infer behaviour from Russian names, category labels or model filenames.
"""
import argparse
import copy
from collections import Counter
import hashlib
import io
import json
from pathlib import Path
import zipfile

CLIENT_SHA='66f5a71817356cfd7397ff5513fcc0baa34f7eac5a530c5d1d15b470d921e8dc'
CATALOG='null/jiiIjjjliIljllljIIIlijjijljliljj'
PROFILE='null/IijIjjillljIIIiiiIilIljilIiIIIii'
KINDS=PROFILE+'$jjlliijIIIiIjIjiiIjijjIjIljjiiII'
BEHAVIOR='null/lIjijlilIlIIIiIiIlIIIIlIijljllij'
REGISTER='(Ljava/lang/String;Ljava/lang/String;L'+PROFILE+';Z)V'


def read_class(archive,name):
    from jawa.cf import ClassFile
    return ClassFile(io.BytesIO(archive.read(name+'.class')))


def kind_fields(cf):
    from jawa.constants import String
    fields={};last=None
    for method in cf.methods:
        if method.name.value!='<clinit>':continue
        for ins in method.code.disassemble():
            if ins.mnemonic in ('ldc','ldc_w'):
                c=cf.constants[ins.operands[0].value]
                if isinstance(c,String):last=c.string.value
            if ins.mnemonic=='putstatic':
                c=cf.constants[ins.operands[0].value]
                if c.class_.name.value==KINDS and c.name_and_type.descriptor.value=='L'+KINDS+';':
                    fields[c.name_and_type.name.value]=last
    return fields


def instructions(cf):
    from jawa.constants import String
    # Largest method is the catalogue initializer. It has no jumps/branches;
    # unsupported opcodes cause refusal instead of an inaccurate partial result.
    methods=[m for m in cf.methods if m.code]
    method=max(methods,key=lambda m:len(m.code._code))
    result=[]
    for ins in method.code.disassemble():
        row={'op':ins.mnemonic,'offset':ins.pos,'values':[o.value for o in ins.operands]}
        if ins.operands and ins.operands[0].op_type.name=='CONSTANT_INDEX':
            c=cf.constants[ins.operands[0].value]
            if isinstance(c,String):row['literal']=c.string.value
            elif hasattr(c,'class_'):
                row.update(owner=c.class_.name.value,name=c.name_and_type.name.value,desc=c.name_and_type.descriptor.value)
            elif hasattr(c,'name'):row['class']=c.name.value
            elif hasattr(c,'value'):row['literal']=c.value
            else:raise ValueError('Unsupported constant at '+str(ins.pos))
        result.append(row)
    return method.name.value,method.descriptor.value,result


def catalogue(rows,kinds):
    from jawa.util.descriptor import method_descriptor
    stack=[];locals_={};entries=[]
    for r in rows:
        op=r['op'];v=r['values'];offset=r['offset']
        if op=='new':stack.append({'class':r['class'],'args':[],'kind':'None','components':[]})
        elif op=='dup':stack.append(stack[-1])
        elif op in ('ldc','ldc_w'):stack.append(r.get('literal',{'class_literal':r.get('class')}))
        elif op.startswith(('iconst_','fconst_')):
            suffix=op.split('_')[1];stack.append(-1 if suffix=='m1' else int(suffix))
        elif op in ('bipush','sipush'):stack.append(v[0])
        elif op.startswith(('aload','iload')):
            i=int(op.split('_')[1]) if '_' in op else v[0];stack.append(locals_.get(i,{'unknown_local':i}))
        elif op.startswith(('astore','istore')):
            i=int(op.split('_')[1]) if '_' in op else v[0];locals_[i]=stack.pop()
        elif op=='getstatic':
            stack.append({'enum':kinds[r['name']]} if r['owner']==KINDS else {'static':r['owner']+'.'+r['name']})
        elif op=='putstatic':stack.pop()
        elif op=='putfield':
            value=stack.pop();obj=stack.pop()
            if isinstance(obj,dict):obj.setdefault('fields',{})[r['name']]=value
        elif op=='pop':stack.pop()
        elif op.startswith('invoke'):
            desc=method_descriptor(r['desc']);n=len(desc.args)
            args=stack[-n:] if n else []
            if n:del stack[-n:]
            obj=None if op=='invokestatic' else stack.pop()
            if r['name']=='<init>' and isinstance(obj,dict):obj['args']=args
            elif r['owner']==PROFILE and isinstance(obj,dict):
                if n==1 and isinstance(args[0],dict) and 'enum' in args[0]:obj['kind']=args[0]['enum']
                elif n==1 and isinstance(args[0],dict) and 'class' in args[0]:obj['components'].append(args[0])
            if op=='invokestatic' and r['owner']==CATALOG and r['desc']==REGISTER:
                model,title,profile,flag=args
                if not isinstance(model,str) or not model.endswith('.m2033model') or not isinstance(title,str) or not isinstance(profile,dict) or profile.get('class')!=PROFILE:
                    raise ValueError('Unresolved catalogue registration at '+str(offset))
                entries.append({'model':model,'name_ru':title,'kind':profile['kind'],'profile':copy.deepcopy(profile),
                                'registration_offset':offset,'flag':flag})
            if desc.returns_descriptor!='V':stack.append({'return_of':r['owner']+'.'+r['name']})
        elif op=='return':
            if stack:raise ValueError('Non-empty stack at end of catalogue')
        else:raise ValueError('Unsupported opcode '+op+' at '+str(offset))
    return entries


def all_strings(value,seen=None):
    if seen is None:seen=set()
    if isinstance(value,(dict,list)):
        if id(value) in seen:return
        seen.add(id(value))
    if isinstance(value,str):yield value
    elif isinstance(value,dict):
        for v in value.values():yield from all_strings(v,seen)
    elif isinstance(value,list):
        for v in value:yield from all_strings(v,seen)


def audit(path):
    from jawa.constants import String
    data=Path(path).read_bytes()
    if hashlib.sha256(data).hexdigest()!=CLIENT_SHA:raise ValueError('Unexpected donor SHA256')
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        enum=kind_fields(read_class(archive,KINDS));cf=read_class(archive,CATALOG)
        method,desc,rows=instructions(cf);entries=catalogue(rows,enum)
        # Hierarchy walk only; do not recurse through the Minecraft world/client.
        def behavior(cls):
            while cls not in ('java/lang/Object',BEHAVIOR):
                try:cls=read_class(archive,cls).super_.name.value
                except KeyError:return False
            return cls==BEHAVIOR
        def component_refs(profile):
            models=set();classes=[]
            for component in profile['components']:
                cls=component['class']
                if behavior(cls):
                    classes.append(cls)
                    cf=read_class(archive,cls)
                    models.update(c.string.value for c in cf.constants.find(type_=String) if c.string.value.endswith('.m2033model'))
            return classes,models
        interactive=[]
        for entry in entries:
            profile=entry.pop('profile');classes,refs=component_refs(profile)
            if entry['kind']=='None' and not classes:continue
            refs.update(s for s in all_strings(profile) if s.endswith('.m2033model'))
            refs.add(entry['model'])
            entry.update(behavior_classes=classes,direct_model_references=sorted(refs),runtime_ported=False)
            interactive.append(entry)
    return {'source_sha256':CLIENT_SHA,'catalogue_class':CATALOG,'method':method,'descriptor':desc,
            'enum_fields':enum,'registered_models':len(entries),'tagged_or_behavior_entries':len(interactive),
            'kind_counts':dict(sorted(Counter(e['kind'] for e in interactive).items())),
            'entries':interactive,'model_references':sorted({m for e in interactive for m in e['direct_model_references']}),
            'runtime_port_implemented':False,
            'limits':['Type tags and client handlers are not verified server behaviour.',
                      'None+handler means a custom behaviour, not proof of an inventory/electricity system.',
                      'References are not extracted geometry. Model-internal textures/materials/animations remain unresolved.',
                      'Generic dynamically constructed model paths, if any, are not resolved by string collection.',
                      'Existing six MetroPort gates have separate legacy assets; they are not these nine donor gates.']}


if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('client');parser.add_argument('output');args=parser.parse_args()
    report=audit(args.client)
    with Path(args.output).open('x',encoding='utf-8') as f:f.write(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps({k:report[k] for k in ('registered_models','tagged_or_behavior_entries','kind_counts')},ensure_ascii=False,indent=2))
