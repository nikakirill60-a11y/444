-- Apply once in the target Supabase project before publishing accounts.public.json.
-- Test on a disposable project first. No passwords or tokens are stored in public tables.
begin;
create table public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  nickname text not null check (nickname ~ '^[A-Za-z0-9_]{3,16}$'),
  skin_path text check (skin_path is null or skin_path = id::text || '/skin.png'),
  skin_model text not null default 'steve' check (skin_model in ('steve', 'alex')),
  created_at timestamptz not null default now()
);
create unique index profiles_nickname_lower_key on public.profiles (lower(nickname));
alter table public.profiles enable row level security;
revoke all on public.profiles from anon, authenticated;
grant select on public.profiles to authenticated;
grant update (nickname, skin_path, skin_model) on public.profiles to authenticated;
create policy profiles_read_self on public.profiles for select to authenticated
  using ((select auth.uid()) = id);
create policy profiles_update_self on public.profiles for update to authenticated
  using ((select auth.uid()) = id) with check ((select auth.uid()) = id);

create function public.create_player_profile() returns trigger
language plpgsql security definer set search_path = '' as $$
begin
  insert into public.profiles(id, nickname) values (new.id, new.raw_user_meta_data ->> 'nickname');
  return new;
end;
$$;
revoke all on function public.create_player_profile() from public, anon, authenticated;
create trigger minetro_create_profile after insert on auth.users
  for each row execute function public.create_player_profile();

-- Private, owner-only bucket. Not a public Minecraft skin provider.
insert into storage.buckets(id, name, public, file_size_limit, allowed_mime_types)
values ('skins', 'skins', false, 262144, array['image/png']);
create policy skins_read_self on storage.objects for select to authenticated
  using (bucket_id = 'skins' and name = (select auth.uid())::text || '/skin.png');
create policy skins_insert_self on storage.objects for insert to authenticated
  with check (bucket_id = 'skins' and name = (select auth.uid())::text || '/skin.png');
create policy skins_update_self on storage.objects for update to authenticated
  using (bucket_id = 'skins' and name = (select auth.uid())::text || '/skin.png')
  with check (bucket_id = 'skins' and name = (select auth.uid())::text || '/skin.png');
create policy skins_delete_self on storage.objects for delete to authenticated
  using (bucket_id = 'skins' and name = (select auth.uid())::text || '/skin.png');
commit;
