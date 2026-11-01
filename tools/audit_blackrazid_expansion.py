#!/usr/bin/env python3
"""Inventory recovered behavior. Source recovery != implemented/accepted game content."""
import hashlib,json,re
from import_content import ROOT,write_json
from import_blackrazid_weapons import BASIC_GUNS
from import_blackrazid_mutants import MOBS
from blackrazid_weapon_profiles import GUN_MODELS
NATIVE_MUTANTS={m["sourceId"] for m in MOBS}
S=ROOT/'tools/blackrazid-reference'
J=S/'java/mods/provoda'
GUNS='Ubludok AK2012 AK AKSU Ashot Duplet Gatling Helsing Tihar Podonok Preved Railgun Revolver Rpk Saiga Abzac Uboinik Ventil Vsv Vyhlop Velopal Ognemet'.split()
def report():
    registration=(J/'ModBase.java').read_text()
    weapons=[]
    for name in GUNS:
        path='guns/'+name+'.java';text=(J/path).read_text()
        args=re.search(r'\b'+name+r' = new '+name+r'\((\d+), (\d+), (\d+)\)',registration)
        weapons.append(dict(sourceId=name,source=path,runtimeStatus='native multipart mesh/equipment with tick-based visual adaptation; full native animation/gameplay acceptance pending' if name in GUN_MODELS else 'basic-firearm with native attachment effects; native-mesh/animations pending' if name in BASIC_GUNS else 'pending',constructorAmmoArgument=int(args[2]),baseDamage=int(args[3]),
            dependencies=sorted(set(re.findall(r'ModBase\.(\w+)',text))),
            shootDelays=sorted(set(map(int,re.findall(r'ep\.upST\((\d+)\)',text)))),
            stateKeys=sorted(set(re.findall(r'\.q\(\)\.[a-z]\("(\w+)"',text))),
            sourceHash=hashlib.sha256(text.encode()).hexdigest()))
    mobs=[]
    for p in sorted((J/'mobs/mobs').glob('*.java')):
        text=p.read_text();health=re.search(r'int aW\(\)\s*{\s*return (\d+)',text)
        damage=re.search(r'int c\(mp var1\)\s*{\s*return (\d+)',text)
        mobs.append(dict(sourceId=p.stem,source='mobs/mobs/'+p.name,runtimeStatus='native behavior, mesh and available sounds; gameplay acceptance pending' if p.stem in NATIVE_MUTANTS else 'pending',health=int(health[1]) if health else None,
            damage=int(damage[1]) if damage else None,sounds=sorted(set(re.findall(r'"([a-z]+_(?:alive|hit1?|death))"',text)))))
    magazines=[dict(sourceId=n,roundItem=round_,capacity=200 if n=='OgnemetAmmo' else int(capacity),sourceCapacityArgument=int(capacity),runtimeStatus='outside selected scope' if n=='AKKAmmo' else 'native fuel logic and basic flame runtime; native mesh pending' if n=='OgnemetAmmo' else 'magazine mechanics implemented; native mesh pending') for n,round_,capacity in
        re.findall(r'\b(\w+Ammo\w*) = new \w+\(\d+, ModBase\.(\w+)\.cp, (\d+),',registration)]
    return dict(status='INCOMPLETE DEVELOPMENT — NOT A FULL-PORT RELEASE',
        excluded=['AirSetup','general EventHandler','PlayerTracker','general ExtendetPlayer implementation','CommandDefence'],
        implementedInDevelopment=['thirst/flask','lamp/charger (documented light adaptation)','ordinary/big grenade mechanics','9 native round items and 20 magazines/canisters','28 native attachment items with server-owned five-slot fitting/removal and actual source-based gun effects; zoom/laser are documented client adaptations','22 basic native firearm profiles, not a complete gun/module/model port','native Gatling multipart mesh with three/five barrels, crank/gyro/motor, stock, belt and pressure dial; tick-based recoil/reload/pump adaptation; seven original standalone module item meshes','native Abzac/Vyhlop/Velopal/Ognemet meshes with original magazines, equipment and first-person pivots; detached read-only incoming-magazine preview','native AK/AKSU/AK2012/Rpk/Saiga/Ubludok/Revolver equipped scene graphs with source socket branches and source-local matrices; first-person action trajectories are adaptations','12 native mutants including flying multipart Demon, native OBJ animation and available sounds; 11 bounded persistent map spawners','12 native wide-rail footprints, owned transactional multiblocks and pure tangent API; reconstructed empty draw branches','all 19 native tunnel sections with 159 original cubes, 18 native OBJ / materials / per-direction transforms, owned ring helpers and mesh-derived render bounds','4 internal expiring light levels; selected faction teleport adapted to server scoreboard teams'],
        pending=['native meshes for the other 10 weapons and standalone ammunition items; remaining mounted attachment meshes and complete native animation paths; visual-only Ashot stock, auxiliary flame effects and full gameplay acceptance','Regina/Drezina entities, coupling, fuel, controls and missing native models/audio; complete source-faithful rail visuals and gameplay acceptance',
                 'full mutant gameplay/visual acceptance','remaining building/decor blocks and full environment gameplay/visual acceptance; unregistered ItemSpawnTunnel furniture helpers are audited separately'],
        weapons=weapons,magazines=magazines,mutants=mobs,
        warnings=['Constructor ammo arguments are NOT necessarily the actual magazine capacity.',
                  'OgnemetAmmo uses Ammo+200, not its unused constructor capacity 5; no invented bullet refill.',
                  'Twelve guns have native multipart geometry/source equipment branches (five dedicated, seven resting equipped scene adaptations); the other 10 guns still use inventory icons, not native multipart meshes. Native old-frame animation paths and visual acceptance are not complete.',
                  'AKK exists in the static initializer, but is outside the 22-gun selection; registration availability needs a separate audit.',
                  'ExtendetPlayer/PacketHandler were recovered to READ selected rules only. They are not copied to runtime.',
                  'Original Lamp handler has an empty lighting body; client illumination in the port is an explicit adaptation.',
                  'Original custom grenade explosion does not run block destruction. Do not substitute world.createExplosion.',
                  'Original rail renderer has no draw calls for shapes 2 and 5–11; segment composites and redstone-controlled switch state are explicit adaptations.',
                  'Faction teleport uses configured server scoreboard teams, not the excluded general faction data or cosmetic armor patches; destinations are disabled until configured.',
                  'Native temporary light tile has an always-true ID mismatch condition; port uses a bounded five-tick lifetime and owner-tile checks.',
                  'All six ItemSpawnTunnel classes are unregistered legacy helpers that place furniture/gate IDs 3002 and 3004-3008, not tunnel sections; see blackrazid-generator-audit.json.',
                  'Recovered source files do not establish a working or visually accepted port.'])
def main():
    data=report();write_json(ROOT/'docs/blackrazid-expansion-report.json',data)
    print('Audited',len(data['weapons']),'weapon classes and',len(data['mutants']),'mutant classes; full expansion NOT complete.')
if __name__=='__main__':main()
