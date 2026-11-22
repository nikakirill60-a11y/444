-- Protocol preparation only. Does not publish files or enable a release.
begin;
create table public.launcher_file_release_history (
 version text primary key check(version ~ '^[A-Za-z0-9][A-Za-z0-9._-]{0,39}$'),
 manifest jsonb not null,
 owner_approval text not null check(length(trim(owner_approval))>0),
 created_at timestamptz not null default now()
);
alter table public.launcher_file_release_history enable row level security;
revoke all on public.launcher_file_release_history from public,anon,authenticated;
grant select,insert on public.launcher_file_release_history to service_role;
grant select,insert,update on public.launcher_dev_release to service_role;
create function public.file_distribution_protocol() returns integer language sql immutable as $$ select 2; $$;
revoke all on function public.file_distribution_protocol() from public,anon,authenticated;
grant execute on function public.file_distribution_protocol() to service_role;
create function public.publish_dev_file_release(new_manifest jsonb,expected_version text,approval text) returns void
language plpgsql security definer set search_path='' as $$
declare current_version text;
begin
 perform pg_catalog.pg_advisory_xact_lock(193701,2);
 select manifest->>'version' into current_version from public.launcher_dev_release where singleton for update;
 if current_version is distinct from expected_version then raise exception 'Active release changed'; end if;
 if approval is null or length(trim(approval))=0 or new_manifest->>'schema' is distinct from '2'
 or new_manifest->>'version' is null or jsonb_typeof(new_manifest->'files') is distinct from 'array'
 or pg_catalog.octet_length(new_manifest::text)>524288 then raise exception 'Invalid file release/owner approval'; end if;
 if jsonb_array_length(new_manifest->'files') not between 2 and 5000
 or (select count(*) from jsonb_array_elements(new_manifest->'files') f where f->>'path' like 'mods/%.jar')<2
 or not exists(select 1 from jsonb_array_elements(new_manifest->'files') f where f->>'path' like 'mods/metrocraft-port-%.jar')
 then raise exception 'Incomplete pack manifest'; end if;
 if exists(select 1 from jsonb_array_elements(new_manifest->'files') f
 where (f->>'mode'='seed' and f->>'path' like 'config/%') is not true
 and (f->>'mode'='managed' and f->>'path' ~ '^(mods|ModularWarfare|resourcepacks|scripts)/') is not true)
 or exists(select 1 from jsonb_array_elements(new_manifest->'files') f
 where jsonb_typeof(f->'chunks') is distinct from 'array'
 or (f->>'sha256' ~ '^[a-f0-9]{64}$') is not true
 or lower(f->>'path') ~ '(^|/)packv4\.json$|^modularwarfare/mod_config\.json$|(^|/)\.\.(/|$)')
 then raise exception 'Invalid/protected file entries'; end if;
 if exists(select 1 from jsonb_array_elements(new_manifest->'files') f,
   jsonb_array_elements(f->'chunks') c where not exists(select 1 from storage.objects o
    where o.bucket_id='dev-packs' and o.name='chunks/'||(c->>'sha256')
      and o.metadata->>'size'=c->>'bytes')) then raise exception 'Release chunks not fully uploaded'; end if;
 insert into public.launcher_file_release_history(version,manifest,owner_approval) values(new_manifest->>'version',new_manifest,approval);
 insert into public.launcher_dev_release(singleton,manifest,object_key,owner_approval)
 values(true,new_manifest,'files/'||(new_manifest->>'version'),approval)
 on conflict(singleton) do update set manifest=excluded.manifest,object_key=excluded.object_key,owner_approval=excluded.owner_approval;
end; $$;
revoke all on function public.publish_dev_file_release(jsonb,text,text) from public,anon,authenticated;
grant execute on function public.publish_dev_file_release(jsonb,text,text) to service_role;
create or replace function public.can_read_dev_pack(object_name text) returns boolean
language sql stable security definer set search_path='' as $$
 select exists(select 1 from public.dev_access_requests r,public.launcher_dev_release v
 where r.user_id=auth.uid() and r.status='approved' and v.singleton and (
  (v.manifest->>'schema'='1' and v.object_key=object_name) or
  (v.manifest->>'schema'='2' and object_name ~ '^chunks/[a-f0-9]{64}$' and
   jsonb_path_exists(v.manifest,'$.files[*].chunks[*] ? (@.sha256 == $sha)',
    jsonb_build_object('sha',split_part(object_name,'/',2))))));
$$;
commit;
