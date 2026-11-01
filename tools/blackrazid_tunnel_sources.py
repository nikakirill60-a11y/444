"""Strict data-only extraction of the native GL transforms. No legacy code execution or eval()."""
import ast,math,re
from pathlib import Path

def scalar(expr,env):
    expr=re.sub(r'\((?:float|double|int)\)','',expr)
    expr=re.sub(r'(?<=\d)[fFdD]\b','',expr).strip()
    def value(n):
        if isinstance(n,ast.Constant) and type(n.value) in [int,float]:return n.value
        if isinstance(n,ast.Name) and n.id in env:return env[n.id]
        if isinstance(n,ast.UnaryOp) and isinstance(n.op,(ast.UAdd,ast.USub)):return value(n.operand)*(1 if isinstance(n.op,ast.UAdd) else -1)
        if isinstance(n,ast.BinOp) and isinstance(n.op,(ast.Add,ast.Sub)):return value(n.left)+(1 if isinstance(n.op,ast.Add) else -1)*value(n.right)
        raise ValueError('Unsupported native arithmetic: '+expr)
    return float(value(ast.parse(expr,mode='eval').body))

def transforms(source,direction):
    before,body=source.split('GL11.glPushMatrix();',1);body=body.split('GL11.glPopMatrix();')[0]
    env={'d1':0,'d2':0,'d3':0}
    for name,expr in re.findall(r'(?:int|float) (rotation|md1|trans) = ([^;]+);',before):env[name]=scalar(expr,env)
    for md,block in re.findall(r'if \(md == (\d+)\) \{([^}]+)\}',before):
        if int(md)==direction:
            for name,expr in re.findall(r'(rotation|md1|trans) = ([^;]+);',block):env[name]=scalar(expr,env)
    operations=[];active=True;matched=False
    for line in body.splitlines():
        line=line.strip()
        m=re.fullmatch(r'(?:\} else )?if \((rotation|md1) == (-?\d+)\) \{',line)
        if m:
            if not line.startswith('}'):matched=False
            active=not matched and env[m[1]]==int(m[2]);matched=matched or active;continue
        if line=='} else {':active=not matched;matched=True;continue
        if line=='}':active=True;matched=False;continue
        m=re.fullmatch(r'GL11.gl(Translate|Rotate|Scale)f\((.*)\);',line)
        if m and active:
            values=[scalar(x,env) for x in m[2].split(',')];assert len(values)==(4 if m[1]=='Rotate' else 3)
            operations.append(dict(type=m[1].lower(),values=values))
        elif line and not m and not line.startswith(('GL11.glShadeModel','ModelBar.render','GL11.glDisable((int)2884);','GL11.glEnable((int)2884);')):raise ValueError('Unrecognized native render statement '+line)
    assert len(operations)>=3
    return operations

def matrix(operations):
    result=[[float(i==j) for j in range(4)] for i in range(4)]
    for op in operations:
        m=[[float(i==j) for j in range(4)] for i in range(4)];v=op['values']
        if op['type']=='translate':
            for i in range(3):m[i][3]=v[i]
        elif op['type']=='scale':
            for i in range(3):m[i][i]=v[i]
        else:
            assert v[1:]==[0,1,0];a=math.radians(v[0]);c=math.cos(a);s=math.sin(a);m[0][0]=c;m[0][2]=s;m[2][0]=-s;m[2][2]=c
        result=[[sum(result[i][k]*m[k][j] for k in range(4)) for j in range(4)] for i in range(4)]
    return result

def bounds(mesh,operations):
    m=matrix(operations);points=[]
    for line in Path(mesh).read_text().splitlines():
        if line.startswith('v '):
            p=list(map(float,line.split()[1:4]))+[1];points.append([sum(m[i][j]*p[j] for j in range(4)) for i in range(3)])
    assert points
    # Quantize below the conservative 1mm margin: Python 3.12 changed sum(float) rounding.
    return [round(min(p[i] for p in points)-.001,6) for i in range(3)]+[round(max(p[i] for p in points)+.001,6) for i in range(3)]
