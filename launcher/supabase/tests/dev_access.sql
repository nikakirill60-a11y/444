-- Negative permission smoke; creates no accounts, grants no administrator roles,
-- changes no requests. Run after 202610300002_dev_access.sql.
begin;
set local role authenticated;
do $$ begin
 if public.is_launcher_admin() then raise exception 'Unexpected administrator'; end if;
 if (select count(*) from public.dev_access_requests) <> 0 then raise exception 'RLS leak'; end if;
 begin perform public.request_dev_access(); raise exception 'Unverified request accepted';
 exception when insufficient_privilege then null; end;
 begin perform public.dev_access_inbox(); raise exception 'Inbox leaked';
 exception when insufficient_privilege then null; end;
 begin perform public.review_dev_access(auth.uid(),true); raise exception 'Unauthorized approval';
 exception when insufficient_privilege then null; end;
end; $$;
rollback;
