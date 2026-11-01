"""Strict data compiler for native EQUIPPED geometry, not executable legacy Java.
Keeps conditional socket/magazine/shell branches and local matrix order. The six
legacy camera operations are removed; the port supplies isolated hand-space poses.
Non-GLModel sprite overlays are audited, not imported (legacy laser/reticle hands).
First-person use of this resting assembly is an explicit animation adaptation.
"""
import ast,hashlib,re
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
J=ROOT/'tools/blackrazid-reference/java/mods/provoda/guns/renderers'
SPECIAL={'Gatling','Abzac','Vyhlop','Velopal','Ognemet'}

def condition(text):
    text=re.sub(r'revinv.a\((\d)\).b\(\) (==|!=) ModBase\.(\w+)',lambda m:'S'+m[1]+('EQ' if m[2]=='==' else 'NE')+m[3],text)
    text=re.sub(r'revinv.a\((\d)\) (==|!=) null',lambda m:'S'+m[1]+('EMPTY' if m[2]=='==' else 'PRESENT'),text)
    text=text.replace('item.q().n("HasAmmo")','MAG').replace('item.q().n("BigAmmo")','BIG')
    text=re.sub(r'z > (\d+)',lambda m:'R'+str(int(m[1])+1),text)
    for old,new in [('this.isUnloading','UNLOAD'),('this.isReloading','RELOAD'),('this.rend','INSERTED'),('this.SkopePush','AIM')]:text=text.replace(old,new)
    text=text.replace('&&',' and ').replace('||',' or ');text=re.sub(r'!(?!=)',' not ',text)
    def convert(n):
        if isinstance(n,ast.Name):
            if n.id=='false':return dict(op='false')
            assert re.fullmatch(r'MAG|BIG|AIM|UNLOAD|RELOAD|INSERTED|R\d+|S\d(?:EMPTY|PRESENT|(?:EQ|NE)\w+)',n.id),n.id
            return dict(op=n.id)
        if isinstance(n,ast.BoolOp):return dict(op='and' if isinstance(n.op,ast.And) else 'or',terms=[convert(x) for x in n.values])
        if isinstance(n,ast.UnaryOp) and isinstance(n.op,ast.Not):return dict(op='not',terms=[convert(n.operand)])
        raise ValueError(ast.dump(n))
    return convert(ast.parse(text.strip(),mode='eval').body)

def linear(text):
    text=re.sub(r'\((?:float|double)\)','',text).strip()
    # These are old per-frame variables; resting EQUIPPED geometry uses their zero pose.
    text=re.sub(r'this\.(hand6CurRot1|reloadCurTransX)','0',text)
    text=re.sub(r'(?<=\d)f\b','',text)
    def visit(n):
        if isinstance(n,ast.Constant) and isinstance(n.value,(int,float)):return float(n.value),0.
        if isinstance(n,ast.Name) and n.id=='z':return 0.,1.
        if isinstance(n,ast.UnaryOp) and isinstance(n.op,ast.USub):
            a,b=visit(n.operand);return -a,-b
        if isinstance(n,ast.BinOp):
            a,b=visit(n.left);c,d=visit(n.right)
            if isinstance(n.op,ast.Add):return a+c,b+d
            if isinstance(n.op,ast.Sub):return a-c,b-d
            if isinstance(n.op,ast.Mult) and not b*d:return a*c,a*d+b*c
        raise ValueError('Unsupported native transform: '+text)
    return visit(ast.parse(text,mode='eval').body)

def compile_scene(name):
    path=J/('Render_'+name+'.java');source=path.read_text()
    fields=dict(re.findall(r'GLModel (\w+) = new GLModel\("/mods/provoda/guns/models/([^"\n]+)"\)',source))
    render=source.split('public void renderItem',1)[1];start=render.index('case 2:');end=render.index('case 3:',start)
    lines=[s.strip() for s in render[start:end].replace('} else if','}\nelse if').replace('} else {','}\nelse {').splitlines() if s.strip() and not s.strip().startswith('GL11.glShadeModel')]
    assert lines[0]=='case 2: {' and lines[1]=='GL11.glPushMatrix();'
    assert len(lines)>8 and '90.0f' in lines[6] and lines[6].startswith('GL11.glRotatef'),(name,lines[:7])
    # Drop only the common legacy item camera transform, not any attachment pivot.
    lines=lines[7:];assert lines[-3:]==['GL11.glPopMatrix();','break;','}'],(name,lines[-3:]);lines=lines[:-3]
    skipped=set();i=0
    def parse_if():
        nonlocal i
        line=lines[i];match=re.fullmatch(r'(?:else )?if \((.*)\) \{',line);assert match,(name,line)
        i+=1;yes=block(True);no=[]
        if i<len(lines) and lines[i].startswith('else if '):no=[parse_if()]
        elif i<len(lines) and lines[i]=='else {':i+=1;no=block(True)
        return dict(op='if',condition=condition(match[1]),yes=yes,no=no)
    def block(nested):
        nonlocal i
        out=[]
        while i<len(lines):
            line=lines[i]
            if line=='}':assert nested;i+=1;return out
            if line.startswith('if '):out.append(parse_if());continue
            i+=1
            if line.startswith('//'):continue
            if line in ['GL11.glPushMatrix();','GL11.glPopMatrix();']:out.append(dict(op='push' if 'Push' in line else 'pop'));continue
            m=re.fullmatch(r'GL11.gl(Translate|Rotate|Scale)[fd]\((.*)\);',line)
            if m:
                values=[linear(x) for x in m[2].split(',')];out.append(dict(op=m[1].lower(),values=[v[0] for v in values],rounds=[v[1] for v in values]));continue
            m=re.fullmatch(r'this\.(\w+)\.render\(\);',line)
            if m:
                if m[1] in fields:
                    field=m[1].lower();role='magazine' if field.startswith('mag') else 'bolt' if field=='zatvor' else 'shell' if field=='bullet' else 'body'
                    out.append(dict(op='draw',mesh=fields[m[1]].lower(),role=role))
                else:
                    assert m[1] in {'laserline','shoot'},(name,'Unmigrated non-OBJ geometry',m[1])
                    skipped.add(m[1])
                continue
            if re.fullmatch(r'GL11.gl(?:Enable|Disable|BindTexture)\(.*\);|mc.p.b\(.*\);',line):continue
            raise ValueError((name,line))
        assert not nested,name
        return out
    nodes=block(False)
    assert i==len(lines)
    def validate(nodes,depth=0):
        for n in nodes:
            if n['op']=='push':depth+=1
            elif n['op']=='pop':depth-=1;assert depth>=0,name
            elif n['op']=='if':assert validate(n['yes'],depth)==validate(n['no'],depth)==depth,name
        return depth
    repairs=[]
    if name=='Ubludok':
        # Original HasAmmo branch leaks one pushed matrix. Balance it locally,
        # retaining exactly the same relative geometry without contaminating callers.
        branch=next(n for n in nodes if n['op']=='if')
        branch['yes'].append(dict(op='pop'));repairs.append('Close one leaked HasAmmo branch matrix; original Java is preserved unchanged')
    assert validate(nodes)==0,name
    return dict(source='guns/renderers/'+path.name,sha256=hashlib.sha256(source.encode()).hexdigest(),nodes=nodes,excludedNonObjModels=sorted(skipped),matrixRepairs=repairs,firstPerson='Resting equipped assembly; tick-based action/skin-hand adaptation, not original frame trajectories')

def scenes():
    return {p.stem[7:]:compile_scene(p.stem[7:]) for p in sorted(J.glob('Render_*.java')) if p.stem[7:] not in SPECIAL}

if __name__=='__main__':
    import json
    data=scenes();print('Compiled',len(data),'native equipped scene graphs');(ROOT/'.cache/native-scenes.json').write_text(json.dumps(data,indent=2)+'\n')
