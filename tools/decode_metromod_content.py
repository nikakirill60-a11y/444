#!/usr/bin/env python3
"""Decode supplied METRO_MOD resources without executing donor Java.
Resource handler jjijllIljljjlijilijiIiiljjiIiiII reads 16 key bytes via
IiijIlIlillIliIilijlillliIljilil. IIljIlIiIIjiiIIjjjiiiIiiIIjiijlj builds
'A'+'E'+'S' and invokes Cipher.doFinal: Java AES defaults to ECB/PKCS5Padding.
"""
import argparse
import hashlib
import io
import json
from pathlib import Path, PurePosixPath
import stat
import zipfile
from Crypto.Cipher import AES
from Crypto.Util.Padding import unpad
from PIL import Image
import soundfile as sf

EXTENSIONS={'.m2033texture':'.png','.m2033sound':'.ogg',
            '.m2033shaderfs':'.frag','.m2033shadervs':'.vert','.m2033shadergs':'.geom'}
MAX_FILE=64*1024*1024
MAX_TOTAL=512*1024*1024


def digest(data):
    return hashlib.sha256(data).hexdigest()


def safe_name(name):
    path=PurePosixPath(name)
    if not name or '\\' in name or '\x00' in name or path.is_absolute() or '..' in path.parts or str(path)!=name.rstrip('/'):
        raise ValueError('Unsafe/noncanonical resource path: '+repr(name))
    if not name.startswith('assets/metro2033server/'):
        raise ValueError('Unexpected resource namespace: '+name)
    return path


def decode(data):
    if len(data)<32 or len(data)%16:
        raise ValueError('Invalid resource envelope length')
    return unpad(AES.new(data[:16],AES.MODE_ECB).decrypt(data[16:]),16)


def validate(data,suffix):
    if suffix=='.png':
        if not data.startswith(b'\x89PNG\r\n\x1a\n'):raise ValueError('Not a PNG')
        with Image.open(io.BytesIO(data)) as image:
            if image.width*image.height>32*1024*1024:raise ValueError('Image too large')
            image.verify()
        with Image.open(io.BytesIO(data)) as image:
            image.load()
            return {'format':'PNG','width':image.width,'height':image.height,'mode':image.mode}
    if suffix=='.ogg':
        if not data.startswith(b'OggS'):raise ValueError('Not Ogg')
        with sf.SoundFile(io.BytesIO(data)) as sound:
            if sound.format!='OGG' or sound.subtype!='VORBIS':raise ValueError('Not Ogg/Vorbis')
            if sound.frames>sound.samplerate*600 or not 1<=sound.channels<=8:raise ValueError('Audio too large')
            frames=0
            while True:
                block=sound.read(8192,dtype='int16',always_2d=True)
                if not len(block):break
                frames+=len(block)
            if frames!=sound.frames:raise ValueError('Truncated Vorbis decode')
            return {'format':'OGG/VORBIS','frames':frames,'samplerate':sound.samplerate,'channels':sound.channels}
    if suffix in ('.frag','.vert','.geom'):
        text=data.decode('utf-8-sig')
        if '\x00' in text or 'void main' not in text:raise ValueError('Not a GLSL source')
        return {'format':'GLSL source','runtime_compiled':False}
    return {'format':'copied unchanged'}


def extract(source,output,report_path):
    source=Path(source);output=Path(output);report_path=Path(report_path)
    if len({source.resolve(),output.resolve(),report_path.resolve()})!=3:
        raise ValueError('Source, output and report must be distinct')
    records=[];seen=set();total=0;skipped=[]
    output.parent.mkdir(parents=True,exist_ok=True)
    temporary=output.with_name(output.name+'.partial')
    try:
        with zipfile.ZipFile(source) as archive, zipfile.ZipFile(temporary,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=6) as result:
            if len(archive.infolist())>10000:raise ValueError('Too many entries')
            for info in archive.infolist():
                path=safe_name(info.filename)
                if stat.S_ISLNK(info.external_attr>>16):raise ValueError('Symlink entry')
                if info.is_dir():continue
                if info.filename=='assets/metro2033server/textures/weapon' and info.file_size==0:
                    skipped.append(info.filename);continue
                if info.file_size>MAX_FILE:raise ValueError('Resource too large')
                total+=info.file_size
                if total>MAX_TOTAL:raise ValueError('Archive too large')
                raw=archive.read(info)
                decoded=path.suffix in EXTENSIONS
                data=decode(raw) if decoded else raw
                name=str(path.with_suffix(EXTENSIONS[path.suffix])) if decoded else str(path)
                if name in seen:raise ValueError('Duplicate output resource: '+name)
                seen.add(name)
                details=validate(data,PurePosixPath(name).suffix) if decoded else {'format':'copied unchanged'}
                entry=zipfile.ZipInfo(name,(1980,1,1,0,0,0));entry.compress_type=zipfile.ZIP_DEFLATED;entry.external_attr=0o100644<<16
                result.writestr(entry,data)
                records.append({'source':info.filename,'output':name,'encrypted_bytes':len(raw),'bytes':len(data),
                                'source_sha256':digest(raw),'sha256':digest(data),'decoded':decoded,**details})
        with zipfile.ZipFile(temporary) as result:
            if result.testzip():raise ValueError('Output archive CRC failed')
        temporary.replace(output)
    except Exception:
        temporary.unlink(missing_ok=True)
        raise
    counts={}
    for record in records:counts[record['format']]=counts.get(record['format'],0)+1
    report={'source':source.name,'source_sha256':digest(source.read_bytes()),'source_bytes':source.stat().st_size,
            'algorithm':'first 16 bytes = AES-128 key; remainder AES/ECB/PKCS7',
            'counts':counts,'files':len(records),'skipped_empty_markers':skipped,
            'output':output.name,'output_bytes':output.stat().st_size,'output_sha256':digest(output.read_bytes()),
            'records':records,'minecraft_runtime_verified':False}
    report_path.parent.mkdir(parents=True,exist_ok=True);report_path.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    return report


if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('source');parser.add_argument('output');parser.add_argument('report')
    args=parser.parse_args();report=extract(args.source,args.output,args.report)
    print(json.dumps({k:v for k,v in report.items() if k!='records'},indent=2))
