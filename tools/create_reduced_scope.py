#!/usr/bin/env python3
"""Build-package exclusions and explicit removed IDs for the user's reduced content decision.
Original references/assets remain in Git for provenance/tests, NOT in the distributable JAR.
"""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/metroport'
def read(name):return json.loads((A/name).read_text())
def report():
 removed=set(['testpickaxe','blackrazid_wide_rails','blackrazid_wide_rail','blackrazid_rail_proxy','blackrazid_tunnels','blackrazid_tunnel','blackrazid_tunnel_proxy','blackrazid_faction_teleport'])
 geometry=set()
 for e in read('content.json'):
  if e['kind'] in ['gun','ammo','attachment'] or e.get('repairGun'):
   removed.add(e['id'])
   if e.get('model'):geometry.add('assets/metroport/geometry/'+e['model']+'.json')
 for name in ['mcmod_guns.json','mcmod_firearms.json','blackrazid_weapons.json','blackrazid_modules.json','mcmod_decor.json']:
  removed.update(e['id'] for e in read(name))
 removed.update(e['id'] for e in read('mcmod_support.json') if e['kind']=='ammo')
 removed.update(e['id'] for e in read('exodus.json') if e['kind']=='decor' or e['id'] in ['exodus_gasmask_gold','exodus_gasmask_diamond'])
 exclude=set(geometry)
 for id in removed:
  exclude.update('assets/metroport/'+p+id+'.json' for p in ['models/item/','blockstates/'])
 for e in read('exodus.json'):
  if e['id'] in removed:exclude.add('assets/metroport/models/'+e['model']+'.json')
 for p in ['models/blackrazid/weapons','textures/blackrazid/weapons','models/mcmod/guns','textures/mcmod/guns','models/mcmod/decor','models/block/mcmod_decor','textures/mcmod/decor','models/blackrazid/rails','textures/blackrazid/rails','models/blackrazid/tunnels','textures/blackrazid/tunnels','textures/mcmod/mods_gui','textures/scopes']:
  exclude.add('assets/metroport/'+p+'/**')
 return dict(decision='Own firearms/ammo/magazines/modules removed; retain machete/grenades, cube blocks/gates, both watches and independent lamp/native-backpack slots.',removedIds=sorted(removed),jarExcludes=sorted(exclude),retainedSpecialItems=['mcmod_machete','blackrazid_grenade','blackrazid_grenade_big','mcmod_watch','blackrazid_watch','blackrazid_lamp','blackrazid_power','blackrazid_flyashka'],decorCubeIds=['block'+str(i) for i in range(58)])
def main():
 data=report();(A/'reduced_scope.json').write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n');print(len(data['removedIds']),'retired IDs;',len(data['jarExcludes']),'JAR exclusions')
if __name__=='__main__':main()
