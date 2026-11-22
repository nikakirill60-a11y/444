-- Empty until the uploaded pack has been inspected, safely hosted, and explicitly
-- accepted by the owner. Administrators of requests cannot publish releases.
begin;
create table public.launcher_dev_release (
 singleton boolean primary key default true check(singleton),
 manifest jsonb not null check(jsonb_typeof(manifest)='object'),
 object_key text not null unique,
 owner_approval text not null check(length(trim(owner_approval))>0)
);
alter table public.launcher_dev_release enable row level security;
revoke all on public.launcher_dev_release from public,anon,authenticated;
create function public.get_dev_release() returns jsonb
language plpgsql stable security definer set search_path='' as $$
declare result jsonb;
begin
 if not exists(select 1 from public.dev_access_requests where user_id=auth.uid() and status='approved') then
  raise exception 'Dev approval required' using errcode='42501';
 end if;
 select jsonb_build_object('manifest',manifest,'objectKey',object_key) into result
 from public.launcher_dev_release where singleton;
 return result;
end;
$$;
create function public.can_read_dev_pack(object_name text) returns boolean
language sql stable security definer set search_path='' as $$
 select exists(select 1 from public.dev_access_requests r,public.launcher_dev_release v
 where r.user_id=auth.uid() and r.status='approved' and v.singleton and v.object_key=object_name);
$$;
revoke all on function public.get_dev_release(),public.can_read_dev_pack(text) from public,anon;
grant execute on function public.get_dev_release(),public.can_read_dev_pack(text) to authenticated;
insert into storage.buckets(id,name,public) values('dev-packs','dev-packs',false);
create policy approved_dev_pack_read on storage.objects for select to authenticated
 using(bucket_id='dev-packs' and (select public.can_read_dev_pack(name)));
commit;
