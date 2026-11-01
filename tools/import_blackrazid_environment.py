#!/usr/bin/env python3
"""Read native tunnel literals and MTLs as data; never run the legacy mod."""
import json,re
from import_content import ROOT,ASSETS,put,write_json
from import_legacy import numbers
from mcmod_languages import update
S=ROOT/'tools/blackrazid-reference';J=S/'java/mods/provoda'
def geometry():
    text=(J/'models/ModelTunnel.java').read_text();parts={};dims=[int(re.search(r'this\.'+f+r' = (\d+)',text)[1]) for f in ['t','u']]
    for line in text.splitlines():
        m=re.search(r'this\.(\w+) = new bdi\(\(bcd\)this, (\d+), (\d+)\)',line)
        if m:parts[m[1]]=dict(name=m[1],uv=[int(m[2]),int(m[3])],size=dims[:],point=[0,0,0],rotation=[0,0,0],boxes=[],mirror=False)
        m=re.search(r'this\.(\w+)\.a\(([-\d.f, ]+)\);',line)
        if m and m[1] in parts:
            v=numbers(m[2]);p=parts[m[1]]
            if len(v)==3:p['point']=v
            elif len(v) in [6,7]:p['boxes'].append(dict(values=v,mirror=p['mirror']))
            else:raise ValueError(line)
        m=re.search(r'this\.(\w+)\.b\((\d+), (\d+)\)',line)
        if m and m[1] in parts:parts[m[1]]['size']=[int(m[2]),int(m[3])]
        m=re.search(r'this\.(\w+)\.i = (true|false)',line)
        if m and m[1] in parts:parts[m[1]]['mirror']=m[2]=='true'
        m=re.search(r'this.setRotation\(this\.(\w+), ([^;]+)\)',line)
        if m:parts[m[1]]['rotation']=numbers(m[2])
    rendered=re.findall(r'this\.(\w+)\.a\(var7\)',text)
    assert len(parts)==159 and len(set(rendered))==159
    assert set(rendered)==set(parts) and all(p['boxes'] for p in parts.values())
    return dict(textureSize=dims,parts=[parts[name] for name in rendered])
def profiles():
    text=(J/'model_blocks/BlockTunnel.java').read_text();start=text.index('if (var7 == 2)');end=text.index('if (var7 == 3)',start)
    cells=[]
    for args in re.findall(r'var1.c\((.*?), ModBase.Empty.cz\)',text[start:end]):
        result=[]
        for axis,expr in zip(['var2','var3','var4'],args.split(',')):
            expr=expr.strip();assert expr.startswith(axis);result.append(int(expr[len(axis):].replace(' ','' ) or '0'))
        cells.append(result)
    assert len(cells)==17 and len({tuple(c) for c in cells})==17
    entries=[dict(id=0,nameRu='Секция круглого тоннеля',nameEn='Round tunnel section',offsets=cells,sourceBlock='BlockTunnel',angles=[90,180,270,0])]
    from blackrazid_tunnel_sources import transforms,bounds
    names={
        '90':('Бетонный тоннель — поворот','Concrete tunnel — curve'),
        'Beton1':('Бетонный тоннель — прямая секция','Concrete tunnel — straight section'),
        'BetonDerevo':('Бетонный тоннель — деревянная обшивка','Concrete tunnel — wood lining'),
        'BetonDerevo1':('Бетонный тоннель — деревянная обшивка 2','Concrete tunnel — wood lining 2'),
        'BetonDouble1':('Бетонный тоннель — двойная секция','Concrete tunnel — double section'),
        'BetonExit':('Бетонный тоннель — выход','Concrete tunnel — exit'),
        'BetonProvoda':('Бетонный тоннель — провода','Concrete tunnel — wiring'),
        'BetonTrub':('Бетонный тоннель — трубы','Concrete tunnel — pipes'),
        'BetonX':('Бетонный тоннель — перекрёсток','Concrete tunnel — crossing'),
        'New':('Новая секция тоннеля','New tunnel section'),
        'Zelezo1':('Металлический тоннель — прямая секция','Metal tunnel — straight section'),
        'Zelezo90':('Металлический тоннель — поворот','Metal tunnel — curve'),
        'ZelezoDerevo':('Металлический тоннель — деревянная обшивка','Metal tunnel — wood lining'),
        'ZelezoDerevo1':('Металлический тоннель — деревянная обшивка 2','Metal tunnel — wood lining 2'),
        'ZelezoDouble':('Металлический тоннель — двойная секция','Metal tunnel — double section'),
        'ZelezoExit':('Металлический тоннель — выход','Metal tunnel — exit'),
        'ZelezoProvoda':('Металлический тоннель — провода','Metal tunnel — wiring'),
        'ZelezoTrub':('Металлический тоннель — трубы','Metal tunnel — pipes')}
    order=['Beton1']+sorted(n for n in names if n!='Beton1')
    for suffix in order:
        block='BlockTunnel'+suffix;renderer='RenderTunnel'+suffix
        native=(J/('objblocks/tunnels/'+block+'.java')).read_text()
        # Every recovered OBJ tunnel has a single native block, no remote shell helpers or custom bounds.
        assert 'ModBase.Empty' not in native and not re.search(r'this.a\([^;]*?f[,)]',native)
        source=(J/('renderers/tunnels/'+renderer+'.java')).read_text()
        mesh=re.search(r'new GLModel\("/mods/provoda/obj/tunnels/([^"/]+)"',source)[1]
        ops=[transforms(source,d) for d in range(4)]
        entries.append(dict(id=len(entries),nameRu=names[suffix][0],nameEn=names[suffix][1],sourceBlock=block,sourceRenderer=renderer,offsets=[],mesh=mesh,
            doubleSided='GL11.glDisable((int)2884)' in source,transforms=ops,angles=[int(next(o['values'][0] for o in t if o['type']=='rotate')) for t in ops],
            bounds=[bounds(S/('assets/tunnels/'+mesh),t) for t in ops]))
    assert len(entries)==19
    return entries

def main():
    write_json(ASSETS/'blackrazid_tunnels.json',profiles());write_json(ASSETS/'geometry/blackrazid/tunnel.json',geometry())
    put(ASSETS/'textures/blackrazid/tunnels/tunnel.png',(S/'assets/tunnels/tunnel.png').read_bytes());put(ASSETS/'textures/items/blackrazid_tunnels.png',(S/'assets/tunnels/block/tunnel.png').read_bytes())
    put(ASSETS/'models/blackrazid/tunnels/tunnel_beton_1.obj',(S/'assets/tunnels/tunnel_beton_1.obj').read_bytes())
    text=(S/'assets/tunnels/tunnel_beton_1.mtl').read_text();maps={}
    for name,body in re.findall(r'newmtl (\S+)(.*?)(?=newmtl |\Z)',text,re.S):
        png=re.search(r'map_Kd (\S+)',body)[1];maps[name]='metroport:textures/blackrazid/tunnels/'+png;put(ASSETS/('textures/blackrazid/tunnels/'+png),(S/('assets/tunnels/'+png)).read_bytes())
    write_json(ASSETS/'blackrazid_tunnel_materials.json',maps)
    write_json(ASSETS/'models/item/blackrazid_tunnels.json',dict(parent='item/generated',textures=dict(layer0='metroport:items/blackrazid_tunnels')))
    write_json(ASSETS/'models/block/blackrazid_tunnel_hidden.json',dict(textures=dict(particle='metroport:items/blackrazid_tunnels'),elements=[]))
    for block in ['blackrazid_tunnel','blackrazid_tunnel_proxy']:
        write_json(ASSETS/('blockstates/'+block+'.json'),dict(variants={'direction='+str(i):dict(model='metroport:blackrazid_tunnel_hidden') for i in range(4)}))
    for i in range(4):write_json(ASSETS/('blockstates/blackrazid_transient_light_'+str(i)+'.json'),dict(variants={'normal':dict(model='metroport:blackrazid_tunnel_hidden')}))
    put(ASSETS/'textures/blackrazid/tunnels/tpblock.png',(S/'assets/tunnels/block/tpblock.png').read_bytes())
    write_json(ASSETS/'models/block/blackrazid_faction_teleport.json',dict(parent='block/cube_all',textures=dict(all='metroport:blackrazid/tunnels/tpblock')))
    write_json(ASSETS/'models/item/blackrazid_faction_teleport.json',dict(parent='metroport:block/blackrazid_faction_teleport'))
    write_json(ASSETS/'blockstates/blackrazid_faction_teleport.json',dict(variants={'normal':dict(model='metroport:blackrazid_faction_teleport')}))
    mesh_maps={}
    for entry in profiles()[1:]:
        mesh=entry['mesh'];put(ASSETS/('models/blackrazid/tunnels/'+mesh),(S/('assets/tunnels/'+mesh)).read_bytes());maps={}
        text=(S/('assets/tunnels/'+mesh[:-4]+'.mtl')).read_text()
        for name,body in re.findall(r'newmtl (\S+)(.*?)(?=newmtl |\Z)',text,re.S):
            png=re.search(r'map_Kd (\S+)',body)[1];maps[name]='metroport:textures/blackrazid/tunnels/'+png
            put(ASSETS/('textures/blackrazid/tunnels/'+png),(S/('assets/tunnels/'+png)).read_bytes())
        mesh_maps[mesh]=maps
    write_json(ASSETS/'blackrazid_tunnel_mesh_materials.json',mesh_maps)
    update();print('Imported all 19 native tunnel profiles: 159 native tunnel cubes, concrete OBJ / 5 material atlases and 17 ring-helper offsets.')
if __name__=='__main__':main()
