"""Verify dev33 runtime-test JAR and clean JUnit results before release.
This is packaging/CPU verification, never evidence of an actual Minecraft run.
"""
import hashlib
import json
from pathlib import Path
import sys
import xml.etree.ElementTree as ET
import zipfile
from tools.verify_reduced_jar import verify as verify_reduced

VERSION='1.13.0-dev.33'
PREFIX='assets/metroport/'
REQUIRED_CLASSES=['OriginalMenuClient','OriginalMenuScreen','OriginalMenuState','OriginalMenuControls',
                  'OriginalMenuScene','OriginalMenuGeometry','OriginalMenuMeshes','OriginalMenuAudio',
                  'OriginalMenuLighting','OriginalMenuOverlay','OriginalMenuEffects','MenuGlScope']

def verify(jar,results,source,run_url):
    jar,results=Path(jar),Path(results)
    suites=[ET.parse(p).getroot() for p in results.glob('TEST-*.xml')]
    totals={key:sum(int(s.get(key,'0')) for s in suites) for key in ('tests','failures','errors','skipped')}
    if totals['tests']<579 or any(totals[k] for k in ('failures','errors','skipped')):
        raise ValueError('JUnit incomplete/unclean: '+repr(totals))
    names={s.get('name'):int(s.get('tests','0')) for s in suites}
    for suffix,count in [('OriginalMenuSceneTest',1),('OriginalMenuAudioTest',3),('OriginalMenuControlsTest',3)]:
        if names.get('ru.metrocraft.port.client.menu.'+suffix)!=count:raise ValueError('Missing menu suite '+suffix)
    verify_reduced(jar)
    with zipfile.ZipFile(jar) as archive:
        if archive.testzip() is not None:raise ValueError('JAR CRC failure')
        if 'Implementation-Version: '+VERSION not in archive.read('META-INF/MANIFEST.MF').decode():raise ValueError('Wrong version')
        for name in REQUIRED_CLASSES:archive.getinfo('ru/metrocraft/port/client/menu/'+name+'.class')
        manifest=json.loads(archive.read(PREFIX+'metromod_menu.json'))
        for row in manifest['assets']:
            if hashlib.sha256(archive.read(PREFIX+row['path'])).hexdigest()!=row['sha256']:raise ValueError('Changed original asset '+row['path'])
        effects=json.loads((Path(__file__).resolve().parents[1]/'reports/METRO_MOD/menu_effects_verified.json').read_text())
        for row in effects['files']:
            if hashlib.sha256(archive.read(PREFIX+row['path'])).hexdigest()!=row['sha256']:raise ValueError('Changed original effect '+row['path'])
        audio=json.loads((Path(__file__).resolve().parents[1]/'reports/METRO_MOD/menu_audio_verified.json').read_text())
        for row in audio['files']:
            if hashlib.sha256(archive.read(PREFIX+row['target_path'])).hexdigest()!=row['sha256']:raise ValueError('Changed original sound')
        # These retained systems/classes must not disappear in a menu increment.
        for required in ['client/GunHands','client/gates/OriginalGateRenderer','metromod/decor/OriginalCollisionCuts',
                         'inventory/EquipmentRules','structures/MetroNetworkCommand']:
            archive.getinfo('ru/metrocraft/port/'+required+'.class')
    return {'version':VERSION,'stage':'original menu runtime TEST; visual parity NOT verified',
            'source_commit':source,'ci_run_url':run_url,'bytes':jar.stat().st_size,
            'sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'junit':totals,
            'original_meshes':35,'original_mesh_textures':97,'original_audio_events':6,'original_audio_files':21,
            'effect_textures':2,'network_target':'localhost:25565','connect_only_on_activation':True,
            'singleplayer_test_button':True,'singleplayer_screen':'GuiWorldSelection','singleplayer_shortcut':'F6',
            'project_website':'https://minetro-beta.vercel.app/','website_only_on_activation':True,
            'menu_enabled':True,'minecraft_client_launched':False,'visual_parity_verified':False,
            'known_differences':['Minecraft 1.12.2 font, donor font parity not verified',
                                 'Target-version GuiOptions instead of donor custom options board',
                                 'Donor player-body rendering and camera shake not yet ported',
                                 'Normal/specular/emission material behavior not yet validated',
                                 'Donor website/auth/skin/status endpoints absent; beta site uses vanilla HTTPS-link action']}

if __name__=='__main__':
    if len(sys.argv)!=6:raise SystemExit('Usage: python -m tools.verify_metromod_menu_build JAR JUNIT_DIR SOURCE RUN_URL REPORT')
    report=verify(*sys.argv[1:5]);Path(sys.argv[5]).write_text(json.dumps(report,indent=2)+'\n');print(json.dumps(report,indent=2))
