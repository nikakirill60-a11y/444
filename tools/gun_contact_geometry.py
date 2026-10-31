"""CPU-only TMT vertex reconstruction for hand-contact regression checks (no GL).
Matches LegacyGeometry and ModelRendererTurbo's default Y-Z-X render rotation.
Part AABBs are a coarse contact check, not a substitute for rendered grip inspection.
"""
import json
import math
from pathlib import Path

ASSETS = Path(__file__).resolve().parents[1] / 'src/main/resources/assets/metroport/geometry'
CORNERS = [(0, 0, 0), (1, 0, 0), (1, 0, 1), (0, 0, 1),
           (0, 1, 0), (1, 1, 0), (1, 1, 1), (0, 1, 1)]


def rotate(point, axis, angle):
    c, s = math.cos(angle), math.sin(angle)
    i, j = (axis + 1) % 3, (axis + 2) % 3
    result = list(point)
    result[i], result[j] = c*point[i] - s*point[j], s*point[i] + c*point[j]
    return result


def meshes(id_):
    data = json.loads((ASSETS / (id_ + '.json')).read_text())
    scale = json.loads((ASSETS / 'catalogue.json').read_text())[id_]['scale'] / 16
    result = []
    for part in data['parts']:
        if part['group'] != 'gunModel':
            continue
        pivot, angles, vertices = [0, 0, 0], [0, 0, 0], []
        for op in part['ops']:
            values, name = op['values'], op['op']
            if name == 'point':
                pivot = values.copy()
            elif name in ('px', 'py', 'pz'):
                pivot['xyz'.index(name[1])] = values[0]
            elif name in ('rx', 'ry', 'rz'):
                angles['xyz'.index(name[1])] = values[0]
            elif name in ('box', 'shape'):
                for k, bits in enumerate(CORNERS):
                    delta = values[7+3*k:10+3*k] if name == 'shape' else [0, 0, 0]
                    vertices.append([values[i] + bits[i]*values[i+3]
                                     + (1 if bits[i] else -1)*(values[6] + delta[i]) for i in range(3)])
        for transform in data['transforms']:
            if transform['op'] == 'translateShoot':
                continue
            if transform['op'] == 'flipAll':
                vertices = [[p[0], -p[1], -p[2]] for p in vertices]
                pivot = [pivot[0], -pivot[1], -pivot[2]]
            else:
                pivot = [pivot[i] + transform['values'][i] for i in range(3)]
        points = []
        for vertex in vertices:
            # GL builds RY * RZ * RX; apply to vertices in reverse order.
            for axis in (0, 2, 1):
                vertex = rotate(vertex, axis, angles[axis])
            points.append([(vertex[i] + pivot[i])*scale for i in range(3)])
        if points:
            result.append((part['index'], points))
    return result


def contact_distance(contact, parts):
    """Minimum distance to a transformed part's AABB, in native scaled metres."""
    distances = []
    for _, points in parts:
        low = [min(p[i] for p in points) for i in range(3)]
        high = [max(p[i] for p in points) for i in range(3)]
        distances.append(math.sqrt(sum(max(low[i]-contact[i], 0, contact[i]-high[i])**2 for i in range(3))))
    return min(distances)
