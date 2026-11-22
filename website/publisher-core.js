import {sha256} from './vendor/sha256.js';
export const PUBLISHER_BUILD = 'UP-08';
export const CHUNK_BYTES = 8 * 1024 * 1024;
export const MAX_FILES = 100000;
export const MAX_PACK_BYTES = 128 * 1024 ** 3;
export const MAX_MANIFEST_BYTES = 48 * 1024 * 1024; // Server/launcher allow 64 MiB including JSONB expansion.
const hash = bytes => Array.from(bytes,b=>b.toString(16).padStart(2,'0')).join('');
export const digest = bytes => hash(sha256(bytes));
export function safePath(path) {
  if(typeof path!=='string'||!path||path.length>230||/[\\:*?"<>|\x00-\x1f]/.test(path))throw Error('Недопустимый путь файла сборки.');
  for(const part of path.split('/')) if(!part||part==='.'||part==='..'||/[. ]$/.test(part)||/^(CON|PRN|AUX|NUL|COM[0-9]|LPT[0-9])$/i.test(part.split('.')[0]))throw Error('Недопустимый путь Windows: '+path);
  return path;
}
export function modeFor(path) {
  const top=path.split('/')[0];
  if(!['mods','ModularWarfare','resourcepacks','scripts','config'].includes(top))return null;
  safePath(path);
  if(path.split('/').some(p=>p.toLowerCase()==='packv4.json')||path.toLowerCase()==='modularwarfare/mod_config.json')return null;
  return top==='config'?'seed':'managed';
}
export function selectGameFiles(files) {
  if(!files.length)throw Error('Выбери папку сборки с папкой mods внутри.');
  const first=files[0].webkitRelativePath?.split('/')[0];
  if(!first)throw Error('Нужен выбор папки, а не отдельных файлов. Используй Chrome или Edge на компьютере.');
  let paths=Array.from(files,file=>{
    const p=file.webkitRelativePath||'';
    if(!p.startsWith(first+'/'))throw Error('Выбери одну папку сборки.');
    return {file,path:p.slice(first.length+1)};
  });
  // Also accept a selected instance with a directly nested .minecraft folder.
  if(!paths.some(p=>p.path.startsWith('mods/'))&&paths.some(p=>p.path.startsWith('.minecraft/mods/')))
    paths=paths.filter(p=>p.path.startsWith('.minecraft/')).map(p=>({...p,path:p.path.slice(11)}));
  const seen=new Set(), selected=[];
  for(const item of paths) {
    const mode=modeFor(item.path);if(!mode)continue;
    const key=item.path.toLowerCase();if(seen.has(key))throw Error('Повтор пути Windows: '+item.path);seen.add(key);
    if(!Number.isSafeInteger(item.file.size)||item.file.size<0||item.file.size>8*1024**3)throw Error('Файл слишком большой: '+item.path);
    selected.push({...item,mode});
  }
  for(const path of seen){const parts=path.split('/');for(let i=1;i<parts.length;i++)if(seen.has(parts.slice(0,i).join('/')))throw Error('Конфликт файла и папки.');}
  const mods=selected.filter(p=>p.path.startsWith('mods/')&&p.path.endsWith('.jar'));
  if(mods.length<2||!mods.some(p=>p.path.startsWith('mods/metrocraft-port-')))throw Error('Выбери папку данных полной сборки: нужны mods/metrocraft-port-*.jar и остальные моды. Родительскую папку или ZIP выбирать не нужно.');
  if(selected.length>MAX_FILES)throw Error('В папке '+selected.length.toLocaleString('ru-RU')+' файлов сборки; поддерживается до '+MAX_FILES.toLocaleString('ru-RU')+'. Ничего не отправлено.');
  const bytes=selected.reduce((n,p)=>n+p.file.size,0);
  if(bytes>MAX_PACK_BYTES)throw Error('Объём файлов сборки '+(bytes/1024**3).toFixed(1)+' ГиБ; поддерживается до 128 ГиБ. Ничего не отправлено.');
  return selected.sort((a,b)=>a.path<b.path?-1:a.path>b.path?1:0);
}
async function readSource(file,path,offset,length,signal) {
  signal?.throwIfAborted();
  let bytes;
  try { bytes=new Uint8Array(await file.slice(offset,offset+length).arrayBuffer()); }
  catch(cause) {
    signal?.throwIfAborted();
    const error=new Error('Не удалось прочитать локальный файл «'+path+'» (позиция '+offset+' байт). Закрой Minecraft и лаунчер, дождись скачивания файла из облака, если он хранится там, и проверь доступ к нему. Затем заново выбери папку и нажми «Проверить папку». Не удаляй файл из сборки. Уже принятые блоки на сервере сохраняются.');
    error.name='SourceReadError';throw error;
  }
  signal?.throwIfAborted();
  if(bytes.length!==length)throw Error('Исходный файл «'+path+'» изменился или прочитан не полностью. Заново выбери папку и проверь её.');
  return bytes;
}
export async function prepareFiles(files,version,{signal,onProgress=()=>{}}={}) {
  if(!/^[A-Za-z0-9][A-Za-z0-9._-]{0,39}$/.test(version))throw Error('Версия: до 40 латинских букв, цифр, точек, дефисов или _.');
  const selected=selectGameFiles(files), manifest={schema:2,version,files:[]},chunks=new Map();
  const total=selected.reduce((n,f)=>n+f.file.size,0);let read=0,lastYield=0;
  for(const item of selected) {
    signal?.throwIfAborted();const hasher=sha256.create(), parts=[];
    for(let offset=0;offset<item.file.size;offset+=CHUNK_BYTES) {
      signal?.throwIfAborted();onProgress({phase:'hash',done:read,total,file:item.path});
      const bytes=await readSource(item.file,item.path,offset,Math.min(CHUNK_BYTES,item.file.size-offset),signal);
      signal?.throwIfAborted();hasher.update(bytes);const sha=digest(bytes),chunk={sha256:sha,bytes:bytes.length};parts.push(chunk);
      if(!chunks.has(sha))chunks.set(sha,{...chunk,file:item.file,path:item.path,offset});
      read+=bytes.length;onProgress({phase:'hash',done:read,total,file:item.path});
      // Yield to input/cancel between chunks rather than hashing GBs in one task.
      if(Date.now()-lastYield>=16){await new Promise(resolve=>setTimeout(resolve,0));lastYield=Date.now();}
    }
    manifest.files.push({path:item.path,mode:item.mode,bytes:item.file.size,sha256:hash(hasher.digest()),chunks:parts});
  }
  if(new TextEncoder().encode(JSON.stringify(manifest)).length>MAX_MANIFEST_BYTES)throw Error('Описание файлов превышает 48 МиБ. Ничего не отправлено.');
  return {manifest,chunks,total};
}
export async function inventory(accounts,chunks,signal) {
  const list=Array.from(chunks.values()),existing=new Set();
  for(let i=0;i<list.length;i+=200){signal?.throwIfAborted();
    const batch=list.slice(i,i+200).map(c=>({sha256:c.sha256,bytes:c.bytes}));
    const response=await accounts.rpc('owner_chunk_inventory',{requested:batch});
    if(!Array.isArray(response)||response.some(s=>!batch.some(c=>c.sha256===s)))throw Error('Неверный ответ хранилища.');
    for(const sha of response)existing.add(sha);
  }
  return existing;
}
export async function uploadPrepared(accounts,prepared,existing,{signal,onProgress=()=>{}}={}) {
  let sent=0,reused=0;const total=Array.from(prepared.chunks.values()).reduce((n,c)=>n+c.bytes,0);
  for(const chunk of prepared.chunks.values()) {
    signal?.throwIfAborted();
    if(existing.has(chunk.sha256)){reused+=chunk.bytes;continue;}
    const bytes=await readSource(chunk.file,chunk.path,chunk.offset,chunk.bytes,signal);
    signal?.throwIfAborted();
    if(bytes.length!==chunk.bytes||digest(bytes)!==chunk.sha256)throw Error('Исходный файл изменился во время загрузки. Выбери папку и проверь заново.');
    await accounts.uploadDevChunk(chunk.sha256,bytes,signal);sent+=bytes.length;
    onProgress({phase:'upload',done:sent+reused,total,sent,reused});
  }
  const verified=await inventory(accounts,prepared.chunks,signal);
  if(verified.size!==prepared.chunks.size)throw Error('Не все блоки приняты хранилищем. Повтори загрузку; готовые блоки не передаются заново.');
  return {sent,reused,total};
}
