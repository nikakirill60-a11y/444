"""Restricted offline reader for donor render transforms. Does not run Java/OpenGL.

Interprets only scalar/vector arithmetic, local branches, constructors and the
allowlisted model-render entry point. Unknown instructions/calls are errors.
The input client SHA is checked by the importer. Results describe authored preview
poses, NOT a claim that the donor server or shader pipeline has been reproduced.
"""
import math
from functools import lru_cache
from tools.audit_metromod_decor import read_class, BEHAVIOR, PROFILE
MODEL='null/iIIiiiIiiIlIjiiIIIjIIiijlijiljij'
STATE='null/llIjIlllliiiIiililjjjIjiIijijiiI'
VECTOR='org/lwjgl/util/vector/Vector3f'
RENDER='jjlliijIIIiIjIjiiIjijjIjIljjiiII'
DESC='(L'+MODEL+';L'+STATE+';)V'
IDENTITY=[1.,0.,0.,0.,0.,1.,0.,0.,0.,0.,1.,0.,0.,0.,0.,1.]

def multiply(a,b):return [sum(a[i*4+k]*b[k*4+j] for k in range(4)) for i in range(4) for j in range(4)]
def transform(m,v):return [sum(m[i*4+j]*v[j] for j in range(3))+m[i*4+3] for i in range(3)]
def obj(cls):return {'class':cls,'fields':{}}
def vec(x=0,y=0,z=0):return {'class':VECTOR,'fields':{'x':x,'y':y,'z':z}}


class PoseReader:
    def __init__(self,archive):
        self.archive=archive;self.matrix=IDENTITY[:];self.matrices=[];self.draws=[]
    @lru_cache(None)
    def cf(self,name):return read_class(self.archive,name)
    def field(self,o,owner,name,desc):
        if o is None:raise ValueError('Null field '+owner+'.'+name)
        key=name if owner==VECTOR else owner+'.'+name
        if key in o['fields']:return o['fields'][key]
        # javac may reference an inherited field through the subclass owner.
        if owner.startswith('null/') and owner in self.allowed:
            parent=self.cf(owner).super_.name.value
            if parent in self.allowed:return self.field(o,parent,name,desc)
        if o['class']==MODEL and desc=='Z':return False
        raise ValueError('Uninitialized field '+key+' on '+o['class'])
    def call(self,owner,name,desc,o,args,depth=0):
        from jawa.util.descriptor import method_descriptor
        returns=method_descriptor(desc).returns_descriptor
        if depth>20:raise ValueError('Too much call depth')
        if owner=='java/lang/Object' and name=='<init>':return
        if owner==VECTOR and name=='<init>':
            o['fields'].update(dict(zip(['x','y','z'],args or [0,0,0])));return
        if owner==MODEL and name=='<init>' and args and isinstance(args[0],str):o['model']=args[0];return
        if owner==PROFILE and name=='<init>' and len(args)==1 and isinstance(args[0],dict) and args[0]['class']==MODEL:o['model']=args[0]['model'];return
        if owner==PROFILE and name==RENDER and desc=='()V':self.draws.append((o['model'],self.matrix[:]));return
        if owner==MODEL and desc=='(L'+PROFILE+';)V':self.draws.append((o['model'],self.matrix[:]));return
        if owner=='java/lang/StringBuilder':
            if name=='<init>':o['text']='';return
            if name=='append':o['text']+=str(args[0]);return o
            if name=='toString':return o['text']
        if owner=='org/lwjgl/opengl/GL11':
            if name in ('glEnable','glDisable','glBlendFunc','glColor4f','glDepthMask','glAlphaFunc'):return
            if name=='glPushMatrix':self.matrices.append(self.matrix[:]);return
            if name=='glPopMatrix':self.matrix=self.matrices.pop();return
            m=IDENTITY[:]
            if name in ('glTranslatef','glTranslated'):m[3],m[7],m[11]=args
            elif name=='glScalef':m[0],m[5],m[10]=args
            elif name=='glRotatef':
                angle,x,y,z=args;length=math.sqrt(x*x+y*y+z*z)
                if length==0:raise ValueError('Zero rotation axis')
                x/=length;y/=length;z/=length;c=math.cos(math.radians(angle));s=math.sin(math.radians(angle));q=1-c
                m=[x*x*q+c,x*y*q-z*s,x*z*q+y*s,0,y*x*q+z*s,y*y*q+c,y*z*q-x*s,0,z*x*q-y*s,z*y*q+x*s,z*z*q+c,0,0,0,0,1]
            else:raise ValueError('Non-transform GL call '+name)
            self.matrix=multiply(self.matrix,m);return
        # Sounds are inert constructor metadata; no sound or other donor code runs.
        if name=='<init>' and owner.startswith('null/') and owner not in self.allowed:
            o['args']=args;return
        if owner not in self.allowed:raise ValueError('Unsupported call '+owner+'.'+name+desc)
        cf=self.cf(owner)
        method=next((m for m in cf.methods if m.name.value==name and m.descriptor.value==desc),None)
        if method is None:return self.call(cf.super_.name.value,name,desc,o,args,depth+1)
        if not (name=='<init>' or (name==RENDER and desc==DESC)):raise ValueError('Non-render method refused')
        ins=list(method.code.disassemble());at={v.pos:i for i,v in enumerate(ins)}
        locals_={0:o};locals_.update({i+1:v for i,v in enumerate(args)})
        stack=[];pc=0;steps=0
        while pc<len(ins):
            i=ins[pc];op=i.mnemonic;v=[a.value for a in i.operands];pc+=1;steps+=1
            if steps>20000:raise ValueError('Instruction budget exceeded')
            constant=cf.constants[v[0]] if i.operands and i.operands[0].op_type.name=='CONSTANT_INDEX' else None
            def jump():return at[i.pos+v[0]]
            if op=='new':stack.append(obj(constant.name.value))
            elif op=='dup':stack.append(stack[-1])
            elif op=='pop':stack.pop()
            elif op=='aconst_null':stack.append(None)
            elif op.startswith(('iconst_','fconst_','dconst_')):stack.append(-1 if op.endswith('_m1') else float(op.split('_')[1]))
            elif op in ('bipush','sipush'):stack.append(v[0])
            elif op in ('ldc','ldc_w','ldc2_w'):stack.append(constant.string.value if hasattr(constant,'string') else constant.value)
            elif op.startswith(('aload','iload','fload','dload')):stack.append(locals_[int(op.split('_')[1]) if '_' in op else v[0]])
            elif op.startswith(('astore','istore','fstore','dstore')):locals_[int(op.split('_')[1]) if '_' in op else v[0]]=stack.pop()
            elif op=='getfield':
                a=stack.pop();stack.append(self.field(a,constant.class_.name.value,constant.name_and_type.name.value,constant.name_and_type.descriptor.value))
            elif op=='putfield':
                value=stack.pop();a=stack.pop();own=constant.class_.name.value;n=constant.name_and_type.name.value
                a['fields'][n if own==VECTOR else own+'.'+n]=value
            elif op in ('fadd','fsub','fmul','fdiv','dadd','dsub','dmul','ddiv','iadd','isub','imul'):
                b=stack.pop();a=stack.pop();stack.append({'add':lambda:a+b,'sub':lambda:a-b,'mul':lambda:a*b,'div':lambda:a/b}[op[1:]]())
            elif op in ('fneg','dneg','ineg'):stack.append(-stack.pop())
            elif op in ('i2f','f2d','d2f'):pass
            elif op=='f2i':stack.append(int(stack.pop()))
            elif op in ('fcmpg','fcmpl','dcmpg','dcmpl'):
                b=stack.pop();a=stack.pop();stack.append((a>b)-(a<b))
            elif op.startswith('if'):
                if op in ('ifnull','ifnonnull'):a=stack.pop();take=(a is None) if op=='ifnull' else (a is not None)
                else:
                    b=stack.pop() if op.startswith('if_icmp') else 0;a=stack.pop();cond=op.split('cmp')[-1] if op.startswith('if_icmp') else op[2:]
                    take={'eq':a==b,'ne':a!=b,'lt':a<b,'le':a<=b,'gt':a>b,'ge':a>=b}[cond]
                if take:pc=jump()
            elif op=='goto':pc=jump()
            elif op=='newarray':stack.append([0]*int(stack.pop()))
            elif op in ('iaload','faload','baload'):
                n=int(stack.pop());a=stack.pop();stack.append(a[n])
            elif op in ('iastore','fastore','bastore'):
                value=stack.pop();n=int(stack.pop());a=stack.pop();a[n]=value
            elif op=='iinc':locals_[v[0]]+=v[1]
            elif op.startswith('invoke'):
                d=constant.name_and_type.descriptor.value;argc=len(method_descriptor(d).args)
                params=stack[-argc:] if argc else []
                if argc:del stack[-argc:]
                receiver=None if op=='invokestatic' else stack.pop()
                result=self.call(constant.class_.name.value,constant.name_and_type.name.value,d,receiver,params,depth+1)
                if method_descriptor(d).returns_descriptor!='V':stack.append(result)
            elif op=='return':return
            else:raise ValueError('Unsupported render opcode '+op+' at '+owner+':'+str(i.pos))
        if returns!='V':raise ValueError('Missing return')
    def construct(self,component,model):
        main=obj(PROFILE);main['model']=model
        behavior=obj(component['class']);self.allowed={BEHAVIOR,component['class']}
        args=[main]+component.get('args',[])[1:]
        args=[vec(*a['args']) if isinstance(a,dict) and a.get('class')==VECTOR else a for a in args]
        cf=self.cf(component['class'])
        candidates=[m for m in cf.methods if m.name.value=='<init>']
        from jawa.util.descriptor import method_descriptor
        candidates=[m for m in candidates if len(method_descriptor(m.descriptor.value).args)==len(args)]
        if len(candidates)!=1:raise ValueError('Ambiguous behavior constructor')
        self.call(component['class'],'<init>',candidates[0].descriptor.value,behavior,args)
        return behavior
    def pose(self,component,model,t):
        self.matrix=IDENTITY[:];self.matrices=[];self.draws=[]
        behavior=self.construct(component,model)
        state=obj(STATE);state['fields'][STATE+'.jiiliIIIljilIljijjIjijilIilIijll']=True
        state['fields'][STATE+'.iijljjIIlljIIjliljiilIjiiIiilIIl']=vec(t,t,t)
        state['fields'][STATE+'.IIiIIljjiiijIliIljjIiiijjjiillii']=bool(t)
        main=obj(MODEL);main['model']=model
        self.call(component['class'],RENDER,DESC,behavior,[main,state])
        if self.matrices:raise ValueError('Unbalanced matrix stack')
        return self.draws
