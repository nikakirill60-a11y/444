import {prepareFiles,inventory,uploadPrepared,CHUNK_BYTES} from './publisher-core.js';
const $=id=>document.getElementById(id);
const volume=n=>(n/1024**2).toFixed(1)+' МиБ';
export function mountPublisher(accounts,{run,busy}) {
  let allowed=false,prepared=null,uploaded=false,controller=null,expected=null,owner=null;
  const message=text=>{$('publisher-status').textContent=text;};
  function render() {
    $('owner-publisher').hidden=!allowed;
    $('publisher-folder').disabled=$('publisher-version').disabled=busy();
    $('publisher-prepare').disabled=busy()||!allowed||!$('publisher-folder').files.length;
    $('publisher-upload').disabled=busy()||!allowed||!prepared||uploaded;
    $('publisher-accept').disabled=busy()||!uploaded;
    $('publisher-activate').disabled=busy()||!allowed||!uploaded||!$('publisher-accept').checked;
    $('publisher-cancel').hidden=!controller;$('publisher-cancel').disabled=!controller;
  }
  function clearPrepared() {prepared=null;uploaded=false;owner=null;$('publisher-accept').checked=false;$('publisher-progress').value=0;render();}
  function reset() {controller?.abort();controller=null;allowed=false;clearPrepared();$('publisher-folder').value='';message('');render();}
  async function load() {
    const session=accounts.requireSession(),info=await accounts.rpc('owner_distribution_state');
    if(accounts.session!==session)throw Error('Войди снова.');
    allowed=info?.publisher===true;
    if(allowed) {
      $('publisher-current').textContent=info.version?'Сейчас в раздаче: '+info.version:'В раздаче пока нет сборки.';
      $('publisher-capacity').textContent='Занято блоками: '+volume(Number(info.usedBytes)||0)+'. Квота тарифа провайдера не раскрывается этим API — вместимость всей сборки заранее не подтверждена. Тариф и платные опции автоматически не меняются.';
      if(!$('publisher-version').value)$('publisher-version').value='dev-'+new Date().toISOString().replace(/[-:]/g,'').slice(0,15);
    } else clearPrepared();
    render();
  }
  async function operation(action) {
    controller=new AbortController();render();
    try {await action(controller.signal);}
    catch(error){
      if(controller?.signal.aborted){message('Операция остановлена. Принятые блоки сохранены.');throw Error('Операция остановлена. Принятые блоки сохранены.');}
      message('Операция не завершена. '+error.message);throw error;
    }
    finally {controller=null;render();}
  }
  function progress(p) {
    $('publisher-progress').value=p.total?100*p.done/p.total:0;
    message(p.phase==='hash'?'Проверка файлов: '+volume(p.done)+' / '+volume(p.total)+'. Пока ничего не отправляется. Файл: '+p.file:
      'Отправлено: '+volume(p.sent)+'. Уже есть на сервере: '+volume(p.reused)+'. Не закрывай вкладку.');
  }
  $('publisher-folder').onchange=()=>{clearPrepared();message('Выбрано файлов: '+$('publisher-folder').files.length+'. Нажми «Проверить папку».');};
  $('publisher-version').oninput=()=>{clearPrepared();message('Версия изменена. Проверь папку заново.');};
  $('publisher-accept').onchange=render;
  $('publisher-cancel').onclick=()=>controller?.abort();
  $('publisher-prepare').onclick=()=>run(()=>operation(async signal=>{
    clearPrepared();owner=accounts.requireSession().id;
    const state=await accounts.rpc('owner_distribution_state');
    if(state?.publisher!==true)throw Error('Нет права загружать сборку.');
    if(state.objectLimitBytes!==null&&Number(state.objectLimitBytes)<CHUNK_BYTES)throw Error('Лимит одного объекта хранилища меньше блока обновлений. Ничего не отправлено.');
    expected=state.version??null;
    const result=await prepareFiles($('publisher-folder').files,$('publisher-version').value.trim(),{signal,onProgress:progress});
    const existing=await inventory(accounts,result.chunks,signal);
    if(accounts.requireSession().id!==owner)throw Error('Аккаунт изменился. Выбери папку заново.');
    const total=Array.from(result.chunks.values()).reduce((n,c)=>n+c.bytes,0);
    const reused=Array.from(result.chunks.values()).filter(c=>existing.has(c.sha256)).reduce((n,c)=>n+c.bytes,0);
    prepared=result;uploaded=false;$('publisher-progress').value=0;
    message('Проверено '+result.manifest.files.length+' файлов. Нужно отправить '+volume(total-reused)+', уже есть '+volume(reused)+'. Миры и личные настройки не отправляются. Нажми «Загрузить изменения».');
  }));
  $('publisher-upload').onclick=()=>run(()=>operation(async signal=>{
    if(!prepared||accounts.requireSession().id!==owner)throw Error('Сначала проверь папку.');
    const existing=await inventory(accounts,prepared.chunks,signal);
    const result=await uploadPrepared(accounts,prepared,existing,{signal,onProgress:progress});
    uploaded=true;$('publisher-progress').value=100;
    message('Файлы приняты. Отправлено '+volume(result.sent)+', повторно использовано '+volume(result.reused)+'. В раздачу ещё не включены. Для включения отметь подтверждение и нажми «Разрешить скачивание этой версии».');
  }));
  $('publisher-activate').onclick=()=>run(async()=>{
    if(!prepared||!uploaded||!$('publisher-accept').checked||accounts.requireSession().id!==owner)throw Error('Нужно явное подтверждение проверенной версии.');
    await accounts.rpc('publish_own_dev_files',{new_manifest:prepared.manifest,expected_version:expected,approval:'Я подтверждаю публикацию '+prepared.manifest.version});
    const version=prepared.manifest.version;clearPrepared();await load();
    message('Версия '+version+' включена в раздачу. В лаунчере 0.7 нажми «Обновить статус». Скачать её смогут только одобренные аккаунты.');
  });
  return {render,reset,load};
}
