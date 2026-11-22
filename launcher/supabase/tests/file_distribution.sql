-- Permission tests after 202610300005. Does not publish fixtures or modify users.
begin;
set local role authenticated;
do $$ begin
 if has_function_privilege('authenticated','public.publish_dev_file_release(jsonb,text,text)','execute')
 or has_function_privilege('anon','public.publish_dev_file_release(jsonb,text,text)','execute')
 or has_function_privilege('authenticated','public.file_distribution_protocol()','execute') then raise exception 'Publisher ACL leak'; end if;
 if public.can_read_dev_pack('chunks/'||repeat('a',64)) then raise exception 'Unsigned download'; end if;
 begin perform public.publish_dev_file_release('{}',null,'attempt');raise exception 'Unauthorized publication';
 exception when insufficient_privilege then null; end;
 begin perform 1 from public.launcher_file_release_history; raise exception 'Private history leaked';
 exception when insufficient_privilege then null; end;
end $$;
rollback;
