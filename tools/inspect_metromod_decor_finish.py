#!/usr/bin/env python3
"""Verify finish collection hashes, decode assets and check material closure.

Never executes donor code or extracts donor paths to the filesystem. Decoded
assets can optionally be written into a new cache ZIP; this is NOT a runtime port.
"""
import argparse
import io
from PIL import Image
import json
from pathlib import Path
import stat
import zipfile
from tools.decode_metromod_content import decode, validate
from tools.decor_finish_template import material_text, texture_roots
from tools.inspect_metromod_decor_models import obj_metadata, safe_path, sha, PREFIX

TEXTURES = 'assets/metro2033server/textures/obj/'
LIMIT = 256 * 1024 * 1024


def material_bindings(text):
    """Donor material labels are case sensitive; diffuse basename before first dot."""
    texture_roots(text)  # validate ordering, references and supported map options
    result = {}; current = None
    for line in text.splitlines():
        line = line.strip()
        if line.startswith('newmtl '):
            current = line[7:].strip()
            if not current or current in result:raise ValueError('Duplicate/empty material label')
            result[current] = None
        elif line.startswith('map_Kd '):
            result[current] = next(iter(texture_roots('newmtl x\n'+line)))
    return result


def inspect(source, inventory, geometry_report, report_path, decoded_zip=None):
    source, inventory, geometry_report, report_path = map(Path, (source, inventory, geometry_report, report_path))
    paths = [source, inventory, geometry_report, report_path]+([Path(decoded_zip)] if decoded_zip else [])
    if len({p.resolve() for p in paths}) != len(paths):raise ValueError('Paths must be distinct')
    if report_path.exists() or (decoded_zip and Path(decoded_zip).exists()):raise FileExistsError('Output already exists')
    if source.stat().st_size > LIMIT or inventory.stat().st_size > 2*1024*1024:raise ValueError('Input too large')
    given = json.loads(inventory.read_text(encoding='utf-8-sig'))
    raw = source.read_bytes()
    part = [p for p in given['parts'] if p['name'] == source.name]
    if len(part)!=1 or part[0]['bytes']!=len(raw) or part[0]['sha256']!=sha(raw):raise ValueError('Part hash/size mismatch')
    rows = [r for r in given['files'] if r['part']==source.name]
    expected = {r['path']:r for r in rows}
    if len(rows)!=len(expected):raise ValueError('Duplicate inventory path')
    materials = {}; textures = {}; models = []; records = []; decoded = {}
    with zipfile.ZipFile(source) as archive:
        entries = archive.infolist()
        if len(entries)>4096 or len(entries)!=len(expected) or {e.filename for e in entries}!=set(expected):raise ValueError('ZIP/inventory member mismatch')
        total = 0
        for entry in entries:
            name = entry.filename; safe_path(name)
            if stat.S_ISLNK(entry.external_attr>>16) or entry.flag_bits&1 or entry.is_dir():raise ValueError('Unsafe ZIP member')
            total += entry.file_size
            if entry.file_size>64*1024*1024 or total>LIMIT:raise ValueError('Decoded collection too large')
            data = archive.read(entry); row = expected[name]
            if len(data)!=row['bytes'] or sha(data)!=row['sha256']:raise ValueError('Member hash/size mismatch: '+name)
            plain = decode(data)
            if name.startswith(PREFIX) and name.endswith('.m2033gl4'):
                bindings = material_bindings(material_text(data)); materials[name] = bindings
                meta = {'type':'material','bindings':bindings}; output = name[:-9]+'.mtl'
            elif name.startswith(PREFIX) and name.endswith('.m2033model'):
                meta = obj_metadata(plain,name); models.append({'source_path':name,**meta})
                meta = {'type':'model',**meta}; output = name[:-11]+'.obj'
            elif name.startswith(TEXTURES) and name.endswith('.m2033texture'):
                if not plain.startswith(b'DDS '):raise ValueError('Expected DDS texture')
                with Image.open(io.BytesIO(plain)) as image:
                    if image.width*image.height>32*1024*1024:raise ValueError('Texture too large')
                    image.load()
                    buffer = io.BytesIO(); image.convert('RGBA').save(buffer,format='PNG')
                    converted = buffer.getvalue()
                meta = {'type':'texture','source_format':'DDS','dds_sha256':sha(plain),**validate(converted,'.png')}
                plain = converted; textures[name] = meta
                output = name[:-len('.m2033texture')]+'.png'
            else:raise ValueError('Unexpected asset: '+name)
            decoded[output] = plain
            records.append({'source_path':name,'source_bytes':len(data),'source_sha256':sha(data),
                            'decoded_path':output,'decoded_bytes':len(plain),'decoded_sha256':sha(plain),**meta})
    geometry = json.loads(geometry_report.read_text())['models'] + models
    missing_materials = []; missing_labels = []; missing_diffuse = []
    for model in geometry:
        libraries = model['material_libraries']
        for library in libraries:
            if library not in materials:missing_materials.append(library)
        labels = {label for library in libraries for label in materials.get(library,{})}
        for label in model['usemtl']:
            if label not in labels:missing_labels.append({'model':model['source_path'],'label':label})
    for name, bindings in materials.items():
        for label, root in bindings.items():
            if root and not any(n.startswith(TEXTURES+root+'.') for n in textures):
                missing_diffuse.append({'material':name,'label':label,'root':root})
    report = {'source_zip':source.name,'source_zip_bytes':len(raw),'source_zip_sha256':sha(raw),
              'inventory_sha256':sha(inventory.read_bytes()),'validated_files':len(records),
              'material_count':len(materials),'texture_count':len(textures),'additional_model_count':len(models),
              'total_geometry_count':len(geometry),'files':records,
              'missing_materials':sorted(set(missing_materials)),'missing_material_labels':missing_labels,
              'missing_diffuse_textures':missing_diffuse,'original_missing_models':given['missing_models'],
              'resolved_collector_material_errors':given['material_errors'],
              'runtime_port_implemented':False,'donor_java_executed':False,
              'complete_pack_verified':False}
    if decoded_zip:
        with zipfile.ZipFile(decoded_zip,'x',compression=zipfile.ZIP_DEFLATED) as writer:
            for name,data in sorted(decoded.items()):writer.writestr(name,data)
    report_path.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    return report


if __name__=='__main__':
    p=argparse.ArgumentParser(description=__doc__)
    for name in ('source','inventory','geometry_report','report'):p.add_argument(name)
    p.add_argument('--decoded-zip');a=p.parse_args()
    r=inspect(a.source,a.inventory,a.geometry_report,a.report,a.decoded_zip)
    for k in ('validated_files','material_count','texture_count','additional_model_count','missing_materials','missing_material_labels','missing_diffuse_textures','original_missing_models'):print(k,r[k])
