-- Transactional denial checks; does not publish any archive or change requests.
begin;
set local role authenticated;
do $$ begin
 if public.can_read_dev_pack('baseline/MinETRO.zip') then raise exception 'Unapproved download allowed'; end if;
 begin perform public.get_dev_release(); raise exception 'Unapproved release exposed';
 exception when insufficient_privilege then null; end;
 begin perform 1 from public.launcher_dev_release; raise exception 'Private release table exposed';
 exception when insufficient_privilege then null; end;
 if has_table_privilege(current_user,'public.launcher_dev_release','insert') then raise exception 'Client can publish release'; end if;
 if has_function_privilege('anon','public.get_dev_release()','execute') then raise exception 'Anonymous release RPC'; end if;
end $$;
rollback;
