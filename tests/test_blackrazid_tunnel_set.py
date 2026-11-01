import json,math,re,sys,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT/'tools'))
from import_blackrazid_environment import profiles
from blackrazid_tunnel_sources import transforms,matrix,scalar
A=ROOT/'src/main/resources/assets/metroport';S=ROOT/'tools/blackrazid-reference';J=S/'java/mods/provoda'
class NativeTunnelSetTests(unittest.TestCase):
    def test_all19_source_types_are_exposed_without_reindexing_dev8_worlds(self):
        rows=profiles();self.assertEqual(list(range(19)),[r['id'] for r in rows]);self.assertEqual(['BlockTunnel','BlockTunnelBeton1'],[r['sourceBlock'] for r in rows[:2]])
        registration=(J/'ModBase.java').read_text();registered=set(re.findall(r'new (BlockTunnel\w*)\(',registration));self.assertEqual(registered,{r['sourceBlock'] for r in rows})
        for r in rows:
            for lang in ['ru_ru','en_us']:self.assertEqual(1,(A/('lang/'+lang+'.lang')).read_text().count('item.metroport.blackrazid_tunnels_'+str(r['id'])+'.name='))
    def test_all18_obj_meshes_all_materials_and19_atlases_are_native(self):
        maps=json.loads((A/'blackrazid_tunnel_mesh_materials.json').read_text());self.assertEqual(18,len(maps));textures=set()
        for mesh,materials in maps.items():
            original=(S/('assets/tunnels/'+mesh)).read_bytes();self.assertEqual(original,(A/('models/blackrazid/tunnels/'+mesh)).read_bytes())
            used={name.strip() for name in re.findall(r'^usemtl (.+)$',original.decode(),re.M)};self.assertTrue(used<=set(materials))
            for value in materials.values():
                target=A/value.split(':')[1];textures.add(target.name);self.assertEqual(target.read_bytes(),(S/('assets/tunnels/'+target.name)).read_bytes())
        self.assertEqual(19,len(textures))
    def test_every_source_render_branch_preserves_operation_order_and_double_siding(self):
        for row in profiles()[1:]:
            source=(J/('renderers/tunnels/'+row['sourceRenderer']+'.java')).read_text();self.assertEqual(row['doubleSided'],'GL11.glDisable((int)2884)' in source)
            for d in range(4):self.assertEqual(transforms(source,d),row['transforms'][d])
        self.assertEqual(4,sum(row.get('doubleSided',False) for row in profiles()))
    def test_curve_double_and_cross_have_distinct_original_rotation_tables(self):
        bysource={r['sourceBlock']:r for r in profiles()}
        self.assertEqual([90,360,270,180],bysource['BlockTunnel90']['angles'])
        self.assertEqual([180,90,360,270],bysource['BlockTunnelZelezoDouble']['angles'])
        self.assertEqual([360]*4,bysource['BlockTunnelBetonX']['angles'])
        ops=bysource['BlockTunnel90']['transforms'][0]
        self.assertEqual(['translate','translate','translate','scale','translate','rotate'],[o['type'] for o in ops])
        self.assertEqual([-17.15,0,-.05],ops[2]['values']);self.assertEqual([.991,1,.993],ops[3]['values'])
    def test_all_transformed_mesh_vertices_fit_generated_render_bounds(self):
        for row in profiles()[1:]:
            vertices=[list(map(float,line.split()[1:4]))+[1] for line in (S/('assets/tunnels/'+row['mesh'])).read_text().splitlines() if line.startswith('v ')]
            for d,ops in enumerate(row['transforms']):
                m=matrix(ops);b=row['bounds'][d];self.assertTrue(all(math.isfinite(x) for x in b));self.assertTrue(all(b[i]<b[i+3] for i in range(3)))
                for v in vertices:
                    for i in range(3):
                        value=sum(m[i][j]*v[j] for j in range(4));self.assertTrue(b[i]<=value<=b[i+3],(row['sourceBlock'],d,i,value,b))
    def test_native_obj_blocks_have_only_anchor_collision_not_invented_solid_meshes(self):
        for row in profiles()[1:]:
            self.assertEqual([],row['offsets']);source=(J/('objblocks/tunnels/'+row['sourceBlock']+'.java')).read_text();self.assertNotIn('ModBase.Empty',source)
    def test_restricted_arithmetic_never_executes_source_text(self):
        self.assertEqual(270,scalar('(float)((float)rotation + 180.0f)',{'rotation':90}))
        for expression in ["__import__('os')",'a.b','1/0','[1][0]','True']:
            with self.assertRaises(ValueError):scalar(expression,{})
    def test_all_metadata_models_and_bounds_are_driven_by_complete_catalogue(self):
        code=(ROOT/'src/main/java/ru/metrocraft/port/ClientEvents.java').read_text();self.assertNotIn('meta<ru.metrocraft.port.blackrazid.environment.TunnelRules.count()',code)
        renderer=(ROOT/'src/main/java/ru/metrocraft/port/client/blackrazid/environment/TunnelRenderer.java').read_text();self.assertIn('profile.transforms[tile.direction()]',renderer);self.assertIn('MESHES.clear()',renderer);self.assertIn('if(cull)GlStateManager.enableCull()',renderer)
        tile=(ROOT/'src/main/java/ru/metrocraft/port/blackrazid/environment/TunnelTile.java').read_text();self.assertIn('TunnelRules.renderBounds(shape,direction).offset(pos)',tile)
