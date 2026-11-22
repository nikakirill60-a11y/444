-- The owner/administrator may review their own dev request. This does not grant
-- administrator status to anyone: every decision still requires the server role.
begin;
create or replace function public.review_dev_access(target uuid,approve boolean) returns void
language plpgsql security definer set search_path = '' as $$
begin
 if not public.is_launcher_admin() then raise exception 'Administrator required' using errcode='42501'; end if;
 if approve is null then raise exception 'Decision required'; end if;
 update public.dev_access_requests set status=case when approve then 'approved' else 'rejected' end,
 reviewed_at=now(),reviewed_by=auth.uid() where user_id=target and status='pending';
 if not found then raise exception 'No pending request' using errcode='P0002'; end if;
end;
$$;
revoke all on function public.review_dev_access(uuid,boolean) from public,anon;
grant execute on function public.review_dev_access(uuid,boolean) to authenticated;
commit;
