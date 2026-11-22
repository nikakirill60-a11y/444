-- No real source files or releases created. Positive Cotuna checks are in the
-- verification receipt; this portable script does not assume any user UUID.
begin;
set local role authenticated;
do $$ begin
 if public.is_launcher_publisher() then raise exception 'Missing identity became publisher'; end if;
 if public.owner_distribution_state() <> '{"publisher":false}'::jsonb then raise exception 'Owner metadata leak'; end if;
 begin perform public.owner_chunk_inventory('[]');raise exception 'Inventory leak';
 exception when insufficient_privilege then null;end;
 begin perform public.publish_own_dev_files('{}',null,'attempt');raise exception 'Unauthorized publish';
 exception when insufficient_privilege then null;end;
 if has_function_privilege('anon','public.owner_distribution_state()','execute')
 or has_function_privilege('authenticated','public.publish_dev_file_release(jsonb,text,text)','execute')
 or has_table_privilege('authenticated','public.launcher_publishers','insert') then raise exception 'ACL leak';end if;
end $$;
rollback;
