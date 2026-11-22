-- Separate, explicitly granted publisher capability. Being an inbox admin does
-- NOT grant write access. No users are granted by this migration.
begin;
create table public.launcher_publishers(user_id uuid primary key references auth.users(id) on delete cascade);
alter table public.launcher_publishers enable row level security;
revoke all on public.launcher_publishers from public,anon,authenticated;
create function public.is_launcher_publisher() returns boolean
language sql stable security definer set search_path='' as $$
 select exists(select 1 from public.launcher_publishers p join auth.users u on u.id=p.user_id
 where p.user_id=auth.uid() and u.email_confirmed_at is not null and u.deleted_at is null
 and (u.banned_until is null or u.banned_until<=now()));
$$;
revoke all on function public.is_launcher_publisher() from public,anon;
grant execute on function public.is_launcher_publisher() to authenticated;
create function public.owner_distribution_state() returns jsonb
language plpgsql stable security definer set search_path='' as $$
begin
 if not public.is_launcher_publisher() then return jsonb_build_object('publisher',false); end if;
 return jsonb_build_object('publisher',true,
  'version',(select manifest->>'version' from public.launcher_dev_release where singleton),
  'objectLimitBytes',(select file_size_limit from storage.buckets where id='dev-packs'),
  'usedBytes',(select coalesce(sum((metadata->>'size')::bigint),0) from storage.objects where bucket_id='dev-packs'),
  'providerQuotaBytes',null);
end; $$;
create function public.owner_chunk_inventory(requested jsonb) returns jsonb
language plpgsql stable security definer set search_path='' as $$
declare result jsonb;
begin
 if not public.is_launcher_publisher() then raise exception 'Publisher required' using errcode='42501'; end if;
 if jsonb_typeof(requested) is distinct from 'array' or jsonb_array_length(requested)>200
 then raise exception 'Invalid chunk query'; end if;
 if exists(select 1 from jsonb_array_elements(requested) c where (c->>'sha256' ~ '^[a-f0-9]{64}$') is not true
  or (c->>'bytes' ~ '^[0-9]{1,8}$') is not true) then raise exception 'Invalid chunk query'; end if;
 select coalesce(jsonb_agg(distinct c->>'sha256'),'[]'::jsonb) into result
 from jsonb_array_elements(requested) c join storage.objects o
 on o.bucket_id='dev-packs' and o.name='chunks/'||(c->>'sha256') and o.metadata->>'size'=c->>'bytes';
 return result;
end; $$;
create function public.publish_own_dev_files(new_manifest jsonb,expected_version text,approval text) returns void
language plpgsql security definer set search_path='' as $$
begin
 if not public.is_launcher_publisher() then raise exception 'Publisher required' using errcode='42501'; end if;
 if approval is distinct from 'Я подтверждаю публикацию '||(new_manifest->>'version')
 then raise exception 'Explicit owner acceptance required'; end if;
 perform public.publish_dev_file_release(new_manifest,expected_version,approval);
end; $$;
revoke all on function public.owner_distribution_state(),public.owner_chunk_inventory(jsonb),public.publish_own_dev_files(jsonb,text,text) from public,anon;
grant execute on function public.owner_distribution_state(),public.owner_chunk_inventory(jsonb),public.publish_own_dev_files(jsonb,text,text) to authenticated;
-- Immutable writes: no UPDATE/DELETE/upsert policy. Drafts remain unreadable to
-- players until a manifest referencing these exact hashes is explicitly activated.
create policy publisher_read_chunks on storage.objects for select to authenticated
 using(bucket_id='dev-packs' and name ~ '^chunks/[a-f0-9]{64}$' and (select public.is_launcher_publisher()));
create policy publisher_insert_chunks on storage.objects for insert to authenticated
 with check(bucket_id='dev-packs' and name ~ '^chunks/[a-f0-9]{64}$' and (select public.is_launcher_publisher()));
-- Size of an individual chunk, not a claim about the provider's total quota.
update storage.buckets set file_size_limit=8388608 where id='dev-packs' and public=false;
commit;
