#!/usr/bin/env python3
"""Author a pinned MinETRO channel from an already assembled game-data ZIP. Does not upload it."""
import argparse,hashlib,json,zipfile
from pathlib import Path,PurePosixPath
from urllib.parse import urlsplit
BASE=Path(__file__).parent

def create(archive,url,version,root=''):
    u=urlsplit(url)
    if u.scheme!='https' or not u.netloc or u.username or u.password or u.fragment:raise ValueError('A direct HTTPS archive URL is required')
    if not version or len(version)>40 or not version[0].isalnum() or any(c not in 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-' for c in version):raise ValueError('Unsafe version ID')
    root=root.strip('/')
    if root and (any(p in ('','..','.') for p in root.split('/')) or '\\' in root or ':' in root):raise ValueError('Unsafe archive root')
    prefix=root+'/' if root else ''
    m=json.loads((BASE/'channel.test.json').read_text(encoding='utf-8'));required=[];seen=set();has_minetro=False
    with zipfile.ZipFile(archive) as z:
        for f in z.infolist():
            if not f.filename.startswith(prefix) or f.is_dir():continue
            p=f.filename[len(prefix):]
            if p.lower() in seen:raise ValueError('Duplicate Windows path: '+p)
            seen.add(p.lower())
            if p.startswith('mods/') and p.endswith('.jar') and len(PurePosixPath(p).parts)==2:
                with z.open(f) as data: digest=hashlib.file_digest(data,'sha256').hexdigest()
                required.append({'path':p,'sha256':digest})
                if PurePosixPath(p).name.startswith('metrocraft-port-'):has_minetro=True
    if len(required)<2 or not has_minetro:raise ValueError('Need the complete modpack with MinETRO and other mods; a single mod JAR is not a pack')
    with open(archive,'rb') as f:digest=hashlib.file_digest(f,'sha256').hexdigest()
    m.update(version=version,archiveRoot=root,notice='Тестовая сборка. Перед обновлением сохраните миры.',archive={'url':url,'sha256':digest,'bytes':Path(archive).stat().st_size},requiredFiles=sorted(required,key=lambda f:f['path']))
    return m
if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('archive',type=Path);p.add_argument('--url',required=True);p.add_argument('--version',required=True);p.add_argument('--root',default='');p.add_argument('--out',type=Path,required=True);a=p.parse_args()
    a.out.write_text(json.dumps(create(a.archive,a.url,a.version,a.root),ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
