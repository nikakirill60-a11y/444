-- Manual integration test: run as postgres in an isolated Supabase project AFTER migration.
-- Never points to or modifies production users. All fixtures roll back.
-- Requires the usual Supabase authenticated role and auth/storage schemas.
begin;
insert into auth.users(id, raw_user_meta_data) values
 ('11111111-1111-4111-8111-111111111111', '{"nickname":"PolicyAlice"}'),
 ('22222222-2222-4222-8222-222222222222', '{"nickname":"PolicyBob"}');
insert into storage.objects(bucket_id,name) values
 ('skins','11111111-1111-4111-8111-111111111111/skin.png'),
 ('skins','22222222-2222-4222-8222-222222222222/skin.png');
set local role authenticated;
select set_config('request.jwt.claim.sub','11111111-1111-4111-8111-111111111111',true);
select set_config('request.jwt.claims','{"sub":"11111111-1111-4111-8111-111111111111","role":"authenticated"}',true);
do $$
declare n integer;
begin
 select count(*) into n from public.profiles;
 if n <> 1 then raise exception 'FAIL profiles RLS: expected only Alice'; end if;
 update public.profiles set nickname='NewAlice' where id='11111111-1111-4111-8111-111111111111';
 get diagnostics n = row_count;
 if n <> 1 then raise exception 'FAIL cannot update own nickname'; end if;
 update public.profiles set nickname='StolenBob' where id='22222222-2222-4222-8222-222222222222';
 get diagnostics n = row_count;
 if n <> 0 then raise exception 'FAIL updated another profile'; end if;
 begin
   update public.profiles set skin_path='22222222-2222-4222-8222-222222222222/skin.png'
     where id='11111111-1111-4111-8111-111111111111';
   raise exception 'FAIL foreign skin reference accepted';
 exception when check_violation then null;
 end;
 begin
   update public.profiles set id='33333333-3333-4333-8333-333333333333';
   raise exception 'FAIL identity reassignment allowed';
 exception when insufficient_privilege then null;
 end;
 begin
   update public.profiles set nickname='policybob' where id='11111111-1111-4111-8111-111111111111';
   raise exception 'FAIL case-insensitive nickname duplicate accepted';
 exception when unique_violation then null;
 end;
 select count(*) into n from storage.objects where bucket_id='skins';
 if n <> 1 then raise exception 'FAIL skin RLS: expected only Alice object'; end if;
 update storage.objects set metadata='{"test":true}' where bucket_id='skins' and name='11111111-1111-4111-8111-111111111111/skin.png';
 get diagnostics n = row_count;
 if n <> 1 then raise exception 'FAIL cannot update own skin'; end if;
 delete from storage.objects where bucket_id='skins' and name='22222222-2222-4222-8222-222222222222/skin.png';
 get diagnostics n = row_count;
 if n <> 0 then raise exception 'FAIL deleting another skin allowed'; end if;
 begin
   insert into storage.objects(bucket_id,name) values ('skins','22222222-2222-4222-8222-222222222222/foreign.png');
   raise exception 'FAIL upload into foreign folder accepted';
 exception when insufficient_privilege then null;
 end;
end;
$$;
reset role;
set local role anon;
select set_config('request.jwt.claim.sub','',true);
select set_config('request.jwt.claims','{"role":"anon"}',true);
do $$
declare n integer;
begin
 begin
   perform * from public.profiles;
   raise exception 'FAIL anonymous profile access accepted';
 exception when insufficient_privilege then null;
 end;
 select count(*) into n from storage.objects where bucket_id='skins';
 if n <> 0 then raise exception 'FAIL anonymous skin access accepted'; end if;
end;
$$;
reset role;
rollback;
-- Success = no FAIL exception and final ROLLBACK. Also test actual HTTP upload/download as two users.
