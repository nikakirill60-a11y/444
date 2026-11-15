#!/usr/bin/env python3
"""Install only four selected, byte-identical donor icons from the decoded archive."""
import hashlib
import json
from pathlib import Path
import zipfile

ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/metroport'
ARCHIVE_SHA='79aff5c64d12778fc826a53a4ee24e80a35934830812d08cdaad6ad9ba62d3ba'


def run():
    archive=ROOT/'downloads/METRO_MOD_Content_decoded.zip'
    if hashlib.sha256(archive.read_bytes()).hexdigest()!=ARCHIVE_SHA:
        raise ValueError('Unexpected decoded source archive')
    manifest=json.loads((ROOT/'reports/METRO_MOD/content_decoded_manifest.json').read_text())
    records={r['output']:r for r in manifest['records']}
    path=A/'metromod_consumables.json';rows=json.loads(path.read_text())
    with zipfile.ZipFile(archive) as z:
        for row in rows:
            source='assets/metro2033server/textures/items/512/'+row['sourceId']+'.png'
            data=z.read(source)
            if hashlib.sha256(data).hexdigest()!=records[source]['sha256']:raise ValueError('Wrong icon digest')
            target=A/'textures/metromod/items'/Path(source).name;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(data)
            row.update(icon='metroport:metromod/items/'+row['sourceId'],visualStatus='original decoded METRO_MOD icon',
                       iconSource=source,iconSha256=records[source]['sha256'])
            (A/'models/item'/ (row['id']+'.json')).write_text(json.dumps({'parent':'minecraft:item/generated','textures':{'layer0':row['icon']}},indent=2)+'\n')
    path.write_text(json.dumps(rows,ensure_ascii=False,indent=2)+'\n')


if __name__=='__main__':run()
