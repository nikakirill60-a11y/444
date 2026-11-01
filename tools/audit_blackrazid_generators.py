#!/usr/bin/env python3
"""Audit names against executed behavior: ItemSpawnTunnel is not necessarily a tunnel generator."""
import hashlib,re
from import_content import ROOT,write_json
S=ROOT/'tools/blackrazid-reference/java/mods/provoda'
def report():
    registration=(S/'ModBase.java').read_text();blocks={int(id_):(field,cls) for field,cls,id_ in re.findall(r'(\w+) = new (Block\w+)\((\d+)\)',registration)}
    rows=[]
    for path in sorted((S/'items').glob('ItemSpawnTunnel*.java')):
        text=path.read_text();target=int(re.search(r'world.c\(i, j \+ 1, k, (\d+)\)',text)[1]);field,cls=blocks[target]
        rows.append(dict(sourceId=path.stem,source='items/'+path.name,sha256=hashlib.sha256(text.encode()).hexdigest(),registered=bool(re.search(r'new '+path.stem+r'\(',registration)),targetLegacyId=target,targetField=field,targetClass=cls,nativeRayDistance=float(re.search(r'd3 = ([\d.]+)',text)[1]),generatesTunnel='Tunnel' in cls))
    assert len(rows)==6
    return dict(status='SOURCE NAME IS MISLEADING: SIX UNREGISTERED SINGLE-BLOCK FURNITURE HELPERS, NOT TUNNEL GENERATORS',helpers=rows,
        runtime='Not silently activated or relabeled as working tunnel generators. Native registered tunnel sections are implemented separately.',
        safety='Original helpers ray trace 5000 blocks and blindly overwrite target+UP without orientation, permissions, server-only checks or costs; not safe to copy verbatim.')
def main():write_json(ROOT/'docs/blackrazid-generator-audit.json',report())
if __name__=='__main__':main()
