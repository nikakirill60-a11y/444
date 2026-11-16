"""Restricted static evaluator of donor block callbacks for differential fixtures.

No JVM/client execution, world access, reflection, file access or arbitrary calls.
All unexpected bytecodes and call targets are errors. Separate from the source
translator: fixtures come straight from class bytecode, NOT generated Java.
"""
import math
from functools import lru_cache
from jawa.util.descriptor import method_descriptor
from tools.audit_metromod_decor import read_class
from tools.import_metromod_door_collision import f32
from tools.import_metromod_germo_collision import BASE, STATE, WORLD, PUT, REMOVE, TICK, HELPER, FIELDS

VECTOR='org/lwjgl/util/vector/Vector3f'
MATH='null/jIjijIiIiililljjjiIljilliIijlliI'
MODE='null/jjjjIiijiiIlljllilIjliijlijIIIiI$IjiIliIlIllillljIIIiIjiljlIljlIl'
BLOCK='null/jlIjiliIjjiIiillillilijjjjiIljjI'
DESC='(Lnull/'+STATE+';Lnull/'+WORLD+';III)V'
WRITE_DESC='(Lnull/'+WORLD+';IIILnull/jlIIiljjljllljjIIiiijliljiijIili;)V'


class GermoReader:
    def __init__(self,archive,component):
        self.archive=archive;self.component=component;self.trace=[]
    @lru_cache(None)
    def cf(self,name):return read_class(self.archive,name)
    def evaluate(self,method,animation,previous,yaw=0,block_mode=True):
        self.trace=[]
        state={FIELDS_INV['animation']:list(map(f32,animation)),
               FIELDS_INV['previous']:list(map(f32,previous)),
               PUT:[0.,f32(yaw),0.],FIELDS_INV['blockMode']:block_mode}
        self.call(self.component,method,DESC,{},[state,'world',0,0,0])
        return self.trace,state[FIELDS_INV['animation']],state[FIELDS_INV['previous']]
    def call(self,owner,name,desc,receiver,args,depth=0):
        if depth>8:raise ValueError('Call depth')
        if owner==VECTOR and name=='<init>':
            receiver.extend([f32(v) for v in args] if len(args)==3 else args[0][:]);return
        if owner=='java/lang/Math':
            if name=='round' and desc=='(F)I':return math.floor(args[0]+.5)
            if name=='toRadians' and desc=='(D)D':return args[0]/180.0*math.pi
            raise ValueError('Unexpected Math call')
        if owner==MATH and name in (PUT,REMOVE,TICK) and desc=='(L'+VECTOR+';D)V':
            v,a=args;c=math.cos(a);s=math.sin(a)
            if name==PUT:v[0],v[2]=f32(c*v[0]+s*v[2]),f32(-s*v[0]+c*v[2])
            elif name==REMOVE:v[1],v[2]=f32(c*v[1]-s*v[2]),f32(s*v[1]+c*v[2])
            else:v[0],v[1]=f32(c*v[0]-s*v[1]),f32(s*v[0]+c*v[1])
            return
        if owner in (self.component,'null/'+BASE) and name in (PUT,REMOVE) and desc==WRITE_DESC:
            if args[0]!='world' or args[-1]!='block':raise ValueError('Unexpected world write')
            self.trace.append(('+' if name==PUT else '-',*args[1:4]));return
        if owner!=self.component or name not in (TICK,'ljIillIiijijlIIijlilliljllijljlI',HELPER) or desc!=DESC:
            raise ValueError('Unexpected callback '+owner+'.'+name+desc)
        cf=self.cf(owner);m=next(m for m in cf.methods if m.name.value==name and m.descriptor.value==desc)
        ins=list(m.code.disassemble());at={i.pos:n for n,i in enumerate(ins)}
        loc={0:receiver,**{n+1:a for n,a in enumerate(args)}};stack=[];pc=0;steps=0
        while pc<len(ins):
            i=ins[pc];op=i.mnemonic;v=[a.value for a in i.operands];pc+=1;steps+=1
            if steps>100000:raise ValueError('Instruction budget')
            c=cf.constants[v[0]] if i.operands and i.operands[0].op_type.name=='CONSTANT_INDEX' else None
            if op=='new':
                if c.name.value!=VECTOR:raise ValueError('Unexpected allocation')
                stack.append([])
            elif op=='dup':stack.append(stack[-1])
            elif op.startswith(('iconst_','fconst_','dconst_')):stack.append(-1 if op.endswith('_m1') else int(op.split('_')[1]))
            elif op in ('bipush','sipush'):stack.append(v[0])
            elif op in ('ldc','ldc_w','ldc2_w'):stack.append(c.value)
            elif op.startswith(('aload','iload','fload','dload')):stack.append(loc[int(op.split('_')[1]) if '_' in op else v[0]])
            elif op.startswith(('astore','istore','fstore','dstore')):loc[int(op.split('_')[1]) if '_' in op else v[0]]=stack.pop()
            elif op=='getstatic':
                owner_=c.class_.name.value;name_=c.name_and_type.name.value
                if (owner_,name_)==(MODE,REMOVE):stack.append(True)
                elif (owner_,name_)==(BLOCK,'ljIillIiijijlIIijlilliljllijljlI'):stack.append('block')
                else:raise ValueError('Unexpected static field')
            elif op=='getfield':
                a=stack.pop();name_=c.name_and_type.name.value;owner_=c.class_.name.value
                if owner_==VECTOR:stack.append(a['xyz'.index(name_)])
                elif owner_=='null/'+STATE:stack.append(a[name_])
                else:raise ValueError('Unexpected field')
            elif op=='putfield':
                value=stack.pop();a=stack.pop();name_=c.name_and_type.name.value;owner_=c.class_.name.value
                if owner_==VECTOR:a['xyz'.index(name_)]=f32(value)
                elif owner_=='null/'+STATE:a[name_]=value
                else:raise ValueError('Unexpected field write')
            elif op in ('fadd','fsub','fmul','fdiv','dadd','dsub','dmul','ddiv','iadd','isub','imul'):
                b=stack.pop();a=stack.pop();value={'add':lambda:a+b,'sub':lambda:a-b,'mul':lambda:a*b,'div':lambda:a/b}[op[1:]]()
                stack.append(f32(value) if op.startswith('f') else value)
            elif op in ('fneg','dneg','ineg'):stack.append(-stack.pop())
            elif op in ('f2d','i2f','d2f'):
                if op!='f2d':stack.append(f32(stack.pop()))
            elif op in ('fcmpg','fcmpl','dcmpg','dcmpl'):
                b=stack.pop();a=stack.pop();stack.append((a>b)-(a<b))
            elif op.startswith('if'):
                comparison=op.startswith(('if_icmp','if_acmp'));b=stack.pop() if comparison else 0;a=stack.pop()
                cond=op.split('cmp')[-1] if comparison else op[2:]
                if {'eq':a==b,'ne':a!=b,'lt':a<b,'le':a<=b,'gt':a>b,'ge':a>=b}[cond]:pc=at[i.pos+v[0]]
            elif op=='goto':pc=at[i.pos+v[0]]
            elif op=='iinc':loc[v[0]]+=v[1]
            elif op.startswith('invoke'):
                d=c.name_and_type.descriptor.value;argc=len(method_descriptor(d).args);params=stack[-argc:] if argc else []
                if argc:del stack[-argc:]
                receiver_=None if op=='invokestatic' else stack.pop()
                result=self.call(c.class_.name.value,c.name_and_type.name.value,d,receiver_,params,depth+1)
                if method_descriptor(d).returns_descriptor!='V':stack.append(result)
            elif op=='return':return
            else:raise ValueError('Unexpected opcode '+op)
        raise ValueError('Missing return')


FIELDS_INV={v:k for k,v in FIELDS.items()}


def write_fixtures(client,output):
    """Reproduce the committed oracle independently of the Java translator."""
    import hashlib
    import json
    import struct
    import zipfile
    from pathlib import Path
    from tools.import_metromod_germo_collision import REPORT, CLOSE, SHA
    if hashlib.sha256(Path(client).read_bytes()).hexdigest()!=SHA:raise ValueError('Unexpected donor client')
    report=json.loads(REPORT.read_text());rows=[]
    with zipfile.ZipFile(client) as archive:
        for p in report['profiles']:
            if hashlib.sha256(archive.read(p['component']+'.class')).hexdigest()!=report['class_sha256'][p['component']+'.class']:
                raise ValueError('Unexpected component class')
            reader=GermoReader(archive,p['component'])
            for yaw in (0,90,180,270):
                for a in (0,-.4,-.5,-.75,-1.25,-1.75,-2.25,-2.75,-3.75,-7.15,-45,-80,.1,.15,.5,.85,.9,1):
                    for method in (TICK,CLOSE):
                        anim=[f32(a),f32(.35),0.];prev=[f32(a+.2),f32(.4),0.]
                        trace,out,previous=reader.evaluate(method,anim,prev,yaw)
                        text=''.join('%s%d,%d,%d;' % e for e in trace)
                        text+='|'+','.join('%08x'%struct.unpack('>I',struct.pack('>f',v))[0] for v in out+previous)
                        rows.append('\t'.join([p['id'],'closed' if method==TICK else 'open',str(yaw),str(a),str(.35),str(a+.2),str(.4),hashlib.sha256(text.encode()).hexdigest()]))
    data=('# bytecode oracle client SHA256 '+SHA+'\n'+'\n'.join(rows)+'\n').encode()
    Path(output).write_bytes(data)
    return {'cases':len(rows),'sha256':hashlib.sha256(data).hexdigest()}


if __name__=='__main__':
    import argparse
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('client');p.add_argument('output')
    a=p.parse_args();print(write_fixtures(a.client,a.output))
