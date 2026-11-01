#!/usr/bin/env python3
"""Optional public-Drive bulk recovery on CI. No credentials, no placeholder assets.
Existing provenance-checked originals are never overwritten. A failed download is
reported as a gap; only verified successful bytes enter the source manifest.
"""
import concurrent.futures, hashlib, json, re, tempfile
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
SOURCE=ROOT/'tools/blackrazid-reference'
FOLDER='1Hs6nCtb1GK-lbMH1o7169aD6j8fmHqPT'

def main():
    import gdown
    manifest=SOURCE/'weapons-model-source-files.json'
    data=json.loads(manifest.read_text()); existing={r['path']:r for r in data['files']}
    wanted=set()
    for p in (SOURCE/'java/mods/provoda/guns/renderers').glob('Render_*.java'):
        wanted.update(re.findall(r'new GLModel\("/mods/provoda/guns/models/([^"\n]+)"\)',p.read_text()))
    folders={p.split('/')[0] for p in wanted}
    with tempfile.TemporaryDirectory() as tmp:
        files=gdown.download_folder(id=FOLDER,output=tmp,skip_download=True,use_cookies=False)
        if files is None:raise RuntimeError('Public folder unavailable; use the authorized Drive connector, not invented resources.')
        selected=[]
        for f in files:
            path=Path(f.path).as_posix()
            if path.split('/')[0] not in folders:continue
            if Path(path).suffix.lower() not in {'.obj','.mtl','.png'}:continue
            key='assets/weapons/'+path
            if key in existing:continue
            selected.append((f,key))
        if len(selected)>600:raise RuntimeError('Unexpected source inventory size')
        def fetch(row):
            f,key=row;target=Path(tmp)/key;target.parent.mkdir(parents=True,exist_ok=True)
            try:
                if gdown.download(id=f.id,output=str(target),quiet=True,use_cookies=False) is None:raise RuntimeError('Download failed')
                raw=target.read_bytes()
                if not raw or len(raw)>12*1024*1024 or raw.lstrip().lower().startswith((b'<!doctype html',b'<html')):raise RuntimeError('Not an asset')
                if target.suffix.lower()=='.png' and not raw.startswith(b'\x89PNG\r\n\x1a\n'):raise RuntimeError('Invalid PNG signature')
                if target.suffix.lower()=='.obj' and not re.search(rb'^v ',raw,re.M):raise RuntimeError('Invalid OBJ')
                if target.suffix.lower()=='.mtl' and b'newmtl ' not in raw:raise RuntimeError('Invalid MTL')
                return dict(path=key,driveId=f.id,bytes=len(raw),sha256=hashlib.sha256(raw).hexdigest()),raw
            except Exception as e:
                print('GAP',key,str(e));return None
        recovered=[]
        with concurrent.futures.ThreadPoolExecutor(max_workers=6) as pool:
            for result in pool.map(fetch,selected):
                if result is not None:recovered.append(result)
        if sum(r['bytes'] for r,b in recovered)>65*1024*1024:raise RuntimeError('Source batch exceeds review limit')
        for row,raw in recovered:
            target=SOURCE/row['path'];target.parent.mkdir(parents=True,exist_ok=True)
            if target.exists():raise RuntimeError('Untracked source collision: '+str(target))
            target.write_bytes(raw);data['files'].append(row)
        manifest.write_text(json.dumps(data,indent=2)+'\n')
        print('Recovered',len(recovered),'originals;',len(selected)-len(recovered),'download gaps')
        report=ROOT/'docs/blackrazid-weapon-source-recovery.json'
        report.write_text(json.dumps(dict(requested=len(selected),recovered=len(recovered),failed=len(selected)-len(recovered),runtimeImplemented=False),indent=2)+'\n')

if __name__=='__main__':main()
