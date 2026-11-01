#!/usr/bin/env python3
"""Complete source-level OBJ dependency inventory. Finding a reference/asset is not runtime completion."""
import re
from import_content import ROOT,write_json
from audit_blackrazid_expansion import GUNS
S=ROOT/'tools/blackrazid-reference';J=S/'java/mods/provoda/guns/renderers';A=S/'assets/weapons'
def report():
    weapons=[]
    for name in GUNS:
        source=J/('Render_'+name+'.java');text=source.read_text()
        models=[dict(field=field,path=path,sourceRecovered=(A/path).is_file(),runtimeMesh=(ROOT/('src/main/resources/assets/metroport/models/blackrazid/weapons/'+path+'.gz')).is_file())
            for field,path in re.findall(r'GLModel (\w+) = new GLModel\("/mods/provoda/guns/models/([^"\n]+)"\)',text)]
        assert models,name
        weapons.append(dict(sourceId=name,renderer='guns/renderers/'+source.name,models=models,
            completeObjAssets=all(row['runtimeMesh'] for row in models),
            runtime='multipart Gatling/equipment implemented; complete original animations and visual acceptance pending' if name=='Gatling' else 'native gun mechanics implemented; multipart renderer pending'))
    missing=sorted({m['path'] for w in weapons for m in w['models'] if not m['sourceRecovered']})
    return dict(status='SOURCE INVENTORY, NOT A CLAIM OF COMPLETE RUNTIME',weapons=weapons,missingUniqueObj=missing,
        warning='Covers declared GLModel fields only. MTLs and referenced PNGs must also be recovered per folder; old Java model/sprite/skin animation branches require separate migration. General handlers and embedded legacy watch/air overlays are not imported.')
def main():
    data=report();write_json(ROOT/'docs/blackrazid-weapon-mesh-gaps.json',data)
    print('Native renderers:',len(data['weapons']),'Missing unique OBJ:',len(data['missingUniqueObj']))
if __name__=='__main__':main()
