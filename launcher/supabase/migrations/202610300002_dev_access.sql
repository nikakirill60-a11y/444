-- No administrator is provisioned here. Only a trusted operator may assign an
-- existing, verified account UUID after the owner identifies their account.
begin;
create table public.launcher_admins (
 user_id uuid primary key references auth.users(id) on delete cascade
);
alter table public.launcher_admins enable row level security;
revoke all on public.launcher_admins from public, anon, authenticated;
create function public.is_launcher_admin() returns boolean
language sql stable security definer set search_path = '' as $$
 select exists(select 1 from public.launcher_admins where user_id=auth.uid());
$$;
revoke all on function public.is_launcher_admin() from public, anon;
grant execute on function public.is_launcher_admin() to authenticated;
create table public.dev_access_requests (
 user_id uuid primary key references public.profiles(id) on delete cascade,
 status text not null default 'pending' check(status in ('pending','approved','rejected')),
 requested_at timestamptz not null default now(),
 reviewed_at timestamptz,
 reviewed_by uuid references auth.users(id),
 check ((status='pending' and reviewed_at is null and reviewed_by is null)
     or (status<>'pending' and reviewed_at is not null and reviewed_by is not null))
);
alter table public.dev_access_requests enable row level security;
revoke all on public.dev_access_requests from public, anon, authenticated;
grant select on public.dev_access_requests to authenticated;
create policy dev_read on public.dev_access_requests for select to authenticated
 using(user_id=(select auth.uid()) or (select public.is_launcher_admin()));
create function public.request_dev_access() returns text
language plpgsql security definer set search_path = '' as $$
declare result text;
begin
 if auth.uid() is null or not exists(select 1 from auth.users where id=auth.uid() and email_confirmed_at is not null) then
  raise exception 'Verified account required' using errcode='42501';
 end if;
 insert into public.dev_access_requests(user_id) values(auth.uid()) on conflict(user_id) do nothing;
 select status into result from public.dev_access_requests where user_id=auth.uid();
 return result;
end;
$$;
create function public.dev_access_inbox() returns table(user_id uuid,nickname text,status text,requested_at timestamptz)
language plpgsql stable security definer set search_path = '' as $$
begin
 if not public.is_launcher_admin() then raise exception 'Administrator required' using errcode='42501'; end if;
 return query select r.user_id,p.nickname,r.status,r.requested_at
 from public.dev_access_requests r join public.profiles p on p.id=r.user_id order by r.requested_at;
end;
$$;
create function public.review_dev_access(target uuid,approve boolean) returns void
language plpgsql security definer set search_path = '' as $$
begin
 if not public.is_launcher_admin() then raise exception 'Administrator required' using errcode='42501'; end if;
 if approve is null then raise exception 'Decision required'; end if;
 if target=auth.uid() then raise exception 'Self approval forbidden' using errcode='42501'; end if;
 update public.dev_access_requests set status=case when approve then 'approved' else 'rejected' end,
 reviewed_at=now(),reviewed_by=auth.uid() where user_id=target and status='pending';
 if not found then raise exception 'No pending request'; end if;
end;
$$;
-- These RPCs manage access only. No archive URL is exposed by this migration.
revoke all on function public.request_dev_access(),public.dev_access_inbox(),public.review_dev_access(uuid,boolean) from public,anon;
grant execute on function public.request_dev_access(),public.dev_access_inbox(),public.review_dev_access(uuid,boolean) to authenticated;
commit;
