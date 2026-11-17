"""Extract ONLY original mesh transforms from the preserved static menu reference.

No donor code is executed. Deliberately excludes text, player body, lighting,
particles, networking and GUI. Those require separate adapters/parity checks.
Rejects unknown statements instead of silently approximating them.
"""
from pathlib import Path
import hashlib
import re

ROOT = Path(__file__).resolve().parents[1]
REFERENCE = ROOT / 'reports/METRO_MOD/menu_reference/scene.java.txt'
OUTPUT = ROOT / 'src/main/java/ru/metrocraft/port/client/menu/OriginalMenuGeometry.java'
SHA = 'a6a5c835ffd7277aa57996eff780aed11df1d61bab9e1644296689276f9d0cad'
FIELDS = {
    'iiljIlIijljjllIiIiIiIIjIjiijjjji.z': 's.sceneZ',
    'IljIIlIjlijlllilijjljiiljjjijjIj': 's.gate',
    'ilijlliilIilillIIiIlijIjIjjIiIIl': 's.needle',
    'lIIjjiiIIjjIIIljllIljlIjIlIjjlIl': 's.multiplayerSwitch',
    'lIijIlijIliiIijjjiIlIllljiijIlli': 's.siteSwitch',
    'ijliIIIlilIijjIiIiiljlliliijjlIl': 's.optionsSwitch',
    'jijIjiliiIljIliIIjIIljjilljiIIji': 's.quitSwitch',
    'IIljiIIjIllIljljIlIjIiiIjillIiII': 's.time',
}


def generate():
    raw = REFERENCE.read_bytes()
    if hashlib.sha256(raw).hexdigest() != SHA:
        raise ValueError('Static reference changed; review extraction boundaries')
    text = raw.decode('utf-8')
    models = dict(re.findall(r'private iIIiiiIiiIlIjiiIIIjIIiijlijiljij (\w+) = new \w+\("([^"]+)"', text))
    if len(models) != 35:
        raise ValueError('Expected exactly 35 original meshes')
    lines = text.splitlines()
    # Inclusive one-based source ranges. Regina's text/UI and random-state mutation
    # are intentionally excluded, NOT regenerated or replaced by guessed UI.
    ranges = [
        ('roots(float f, float f2, int n)', [(1073, 1087)]),
        ('webs(float f, float f2, int n)', [(1091, 1106)]),
        ('tunnel(int n)', [(1147, 1178)]),
        ('intro()', [(1182, 1182), (1189, 1259)]),
        ('regina()', [(1263, 1297), (1348, 1356), (1367, 1424)]),
    ]
    methods = []
    for signature, sections in ranges:
        body = []
        for start, end in sections:
            body.extend(lines[start-1:end])
            if signature == 'regina()' and start == 1263:
                body.append('        out.dashboard();')
        translated = []
        for line in body:
            line = re.sub(r'\((?:float|int|boolean)\)', '', line)
            for old, new in FIELDS.items():
                line = line.replace('this.' + old, new)
            line = re.sub(r'this\.(\w+)\.jIIlljIIIjijjlljiIiIiljjjiIIlIIl\(\);',
                          lambda m: 'out.mesh("' + models[m[1]] + '");', line)
            for old, new in [('glPushMatrix','push'),('glPopMatrix','pop'),('glTranslatef','translate'),('glRotatef','rotate'),('glScalef','scale'),('glColor3f','color')]:
                line = line.replace('GL11.' + old, 'out.' + new)
            # Fixed-function lighting is managed by the caller, outside this mesh pass.
            if re.fullmatch(r'\s*GL11\.gl(?:Enable|Disable)\(2896\);', line):
                continue
            if not re.fullmatch(r'\s*(?:out\.(?:push|pop|translate|rotate|scale|color|mesh|dashboard)\(.*\);|int n;|switch \(n\) \{|case \d+: \{|break;|\}|for \(.*\) \{)\s*', line):
                raise ValueError('Unrecognised geometry statement: ' + line)
            if line.strip() == 'out.push();':
                line += ' try {'
            elif line.strip() == 'out.pop();':
                line = line.replace('out.pop();', '} finally { out.pop(); }')
            translated.append(line)
        methods.append('    public void ' + signature + ' {\n' + '\n'.join(translated) + '\n    }\n')
    return '''package ru.metrocraft.port.client.menu;

/** Generated mesh-only transform adapter; see tools/generate_metromod_menu_scene.py.
 * Not a complete GUI or proof of visual parity. No Minecraft/GL dependency.
 * Source SHA256: ''' + SHA + '''
 */
public final class OriginalMenuGeometry {
    public interface Sink {
        void push();
        void pop();
        void translate(float x, float y, float z);
        void rotate(float angle, float x, float y, float z);
        void scale(float x, float y, float z);
        void color(float r, float g, float b);
        void mesh(String sourceReference);
        default void cameraLights() {}
        default void dashboard() {}
        default void particles() {}
        default void glows() {}
    }
    private final Sink out;
    private final OriginalMenuState s;
    public OriginalMenuGeometry(Sink out, OriginalMenuState state) {
        this.out = out;
        this.s = state;
    }
''' + '\n'.join(methods) + '}\n'


if __name__ == '__main__':
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text(generate(), encoding='utf-8')
    print(OUTPUT.relative_to(ROOT))
