using MinETRO.Core;

namespace MinETRO.Launcher;

internal sealed class AccountPage : Panel
{
    private readonly AccountsConfig config;
    private readonly Accounts service;
    private readonly CancellationTokenSource lifetime = new();
    private readonly Label notice = TextLabel("", 10, Ui.Amber);
    private readonly Label result = TextLabel("", 10, Ui.Amber);
    private readonly TextBox email = Input(), password = Input(true), repeat = Input(true), nickname = Input(), code = Input(), profileNick = Input();
    private readonly LauncherButton loginTab = Button("Вход"), registerTab = Button("Регистрация"), recoveryTab = Button("Восстановление"), skinTab = Button("Скин");
    private readonly LauncherButton submit = Button("Войти", true), confirmEmail = Button("У меня есть код"), sendCode = Button("Отправить код"), saveNick = Button("Сохранить ник"), chooseSkin = Button("Выбрать PNG"), uploadSkin = Button("Загрузить скин", true), logout = Button("Выйти"), reload = Button("Обновить профиль");
    private readonly Panel authBox = new() { AutoSize = true, Dock = DockStyle.Top, BackColor = Ui.Card };
    private readonly Panel profileBox = new() { AutoSize = true, Dock = DockStyle.Top, BackColor = Ui.Card };
    private readonly ComboBox model = new() { DropDownStyle = ComboBoxStyle.DropDownList, BackColor = Ui.Card, ForeColor = Ui.Text, Width = 170 };
    private readonly SkinPreview preview = new();
    private readonly Label skinState = TextLabel("Скин не выбран", 9, Ui.Muted);
    private readonly List<Control> operations = [];
    private Control profileNickRow = null!, profileActions = null!;
    private Control emailRow = null!, passwordRow = null!, repeatRow = null!, nickRow = null!, codeRow = null!;
    private byte[]? selectedSkin;
    private string mode = "login";
    private bool busy;
    public event Action<string?>? ProfileChanged;
    public AccountPage()
    {
        AutoScroll = true; BackColor = Ui.Bg;
        string error = "";
        try { config = AccountsConfig.Load(Path.Combine(AppContext.BaseDirectory, "accounts.public.json")); }
        catch (Exception e) when (e is IOException or InvalidDataException or System.Text.Json.JsonException) { config = new(); error = "Конфигурация аккаунтов некорректна. Подключение отключено."; }
        service = new Accounts(config);
        var stack = Stack(Ui.Bg, 0); Controls.Add(stack);
        stack.Controls.Add(TextLabel("Твой профиль", 24, Ui.Text, true));
        stack.Controls.Add(TextLabel("Аккаунт MinETRO · ник · собственный скин", 10, Ui.Muted));
        notice.Text = error != "" ? error : config.Enabled ? "Аккаунты MinETRO подключены. Подтверди почту для входа." : "Сервис аккаунтов пока не подключён. Регистрация и вход станут доступны после настройки Supabase.";
        notice.Margin = new Padding(0, 14, 0, 12); stack.Controls.Add(notice);
        var tabs = new FlowLayoutPanel { AutoSize = true, Dock = DockStyle.Top, Margin = new Padding(0,0,0,14) };
        tabs.Controls.AddRange([loginTab, registerTab, recoveryTab, skinTab]); stack.Controls.Add(tabs);
        stack.Controls.Add(authBox); stack.Controls.Add(profileBox);
        result.MaximumSize = new Size(650, 0); result.Margin = new Padding(0,12,0,12); result.AccessibleRole = AccessibleRole.StaticText; stack.Controls.Add(result);
        stack.Controls.Add(TextLabel("MinETRO — отдельный профиль, не замена аккаунту Microsoft.\nСкин пока хранится в профиле: показ в Minecraft и другим игрокам ещё не подключён.\nПароли не сохраняются. После закрытия лаунчера потребуется вход заново.", 9, Ui.Muted));
        var auth = Stack(Ui.Card, 20); authBox.Controls.Add(auth);
        emailRow = Field("Электронная почта", email); nickRow = Field("Ник · 3–16 латинских букв, цифр или _", nickname);
        passwordRow = Field("Пароль · при создании не меньше 12 символов", password); repeatRow = Field("Повтори новый пароль", repeat); codeRow = Field("Код из письма", code);
        auth.Controls.AddRange([emailRow, nickRow, passwordRow, repeatRow, codeRow]);
        var actions = new FlowLayoutPanel { AutoSize = true, Dock = DockStyle.Top }; actions.Controls.AddRange([submit, sendCode, confirmEmail]); auth.Controls.Add(actions);
        var profile = Stack(Ui.Card, 20); profileBox.Controls.Add(profile);
        profileNickRow=Field("Твой ник", profileNick);profile.Controls.Add(profileNickRow);
        var profileButtons = new FlowLayoutPanel { AutoSize=true, Dock=DockStyle.Top }; profileButtons.Controls.AddRange([saveNick,reload,logout]); profileActions=profileButtons;profile.Controls.Add(profileActions);
        var skin = new FlowLayoutPanel { AutoSize=true, Dock=DockStyle.Top, Margin=new Padding(0,16,0,0), WrapContents=true };
        skin.Controls.Add(preview);
        var skinActions = Stack(Ui.Card, 10); skinActions.Dock=DockStyle.None; skinActions.Width=250; skinActions.AutoSizeMode=AutoSizeMode.GrowAndShrink;
        skinActions.Controls.Add(TextLabel("Свой скин",16,Ui.Text,true)); skinActions.Controls.Add(TextLabel("PNG · 64×64 / 64×32 · до 256 КБ\n2D-предпросмотр спереди и сзади",9,Ui.Muted));
        model.Items.AddRange(["Steve · обычные руки", "Alex · тонкие руки"]); model.SelectedIndex=0;
        skinActions.Controls.Add(model); skinActions.Controls.Add(chooseSkin); skinActions.Controls.Add(uploadSkin); skinActions.Controls.Add(skinState); skin.Controls.Add(skinActions); profile.Controls.Add(skin);
        operations.AddRange([email,password,repeat,nickname,code,profileNick,loginTab,registerTab,recoveryTab,skinTab,submit,confirmEmail,sendCode,saveNick,reload,logout,chooseSkin,uploadSkin,model]);
        loginTab.Click += (_,_) => SetMode("login"); registerTab.Click += (_,_) => SetMode("register"); recoveryTab.Click += (_,_) => SetMode("recovery");
        skinTab.Click += (_,_) => SetMode("skin");
        model.SelectedIndexChanged += (_,_) => { preview.Model = Model; preview.Invalidate(); };
        chooseSkin.Click += (_,_) => ChooseSkin();
        submit.Click += async (_,_) => await Run(Submit);
        confirmEmail.Click += (_,_) => SetMode("confirm");
        sendCode.Click += async (_,_) => await Run(async ct => {
            if(mode=="confirm") await service.ResendConfirmation(email.Text,ct); else await service.SendRecovery(email.Text, ct);
            result.Text="Если адрес подходит для операции, на него отправлен код. Проверь почту и спам.";
        });
        saveNick.Click += async (_,_) => await Run(async ct => { await service.Rename(profileNick.Text.Trim(),ct); await LoadProfile(ct); result.Text="Ник сохранён."; });
        uploadSkin.Click += async (_,_) => await Run(async ct => { if(selectedSkin == null) throw new AccountException("Сначала выбери PNG-скин."); await service.UploadSkin(selectedSkin,Model,ct); await LoadProfile(ct); result.Text="Скин сохранён в профиле MinETRO. Показ в игре ещё не подключён."; });
        reload.Click += async (_,_) => await Run(LoadProfile);
        logout.Click += async (_,_) => await Run(async ct => { try { await service.SignOut(ct); } finally { ClearProfile(); SetMode("login"); } result.Text="Выполнен выход."; });
        SetMode("login");
    }
    private string Model => model.SelectedIndex == 1 ? "alex" : "steve";
    private static LauncherButton Button(string text, bool accent=false) => new() { Text=text, Accent=accent, Size=new Size(150,38), Margin=new Padding(0,4,8,4) };
    private static Label TextLabel(string text, float size, Color color, bool bold=false) => new() { Text=text, ForeColor=color, Font=new Font("Segoe UI",size,bold?FontStyle.Bold:FontStyle.Regular), AutoSize=true, MaximumSize=new Size(650,0), Margin=new Padding(0,4,0,6) };
    private static TextBox Input(bool secret=false) => new() { BackColor=Color.FromArgb(29,37,46),ForeColor=Ui.Text,BorderStyle=BorderStyle.FixedSingle,UseSystemPasswordChar=secret,Width=350,MaxLength=secret?128:254,Margin=new Padding(0,0,0,9) };
    private static TableLayoutPanel Stack(Color bg, int pad) => new() { ColumnCount=1,AutoSize=true,Dock=DockStyle.Top,BackColor=bg,Padding=new Padding(pad),Margin=new Padding(0) };
    private static Control Field(string label, Control control)
    { var p=Stack(Ui.Card,0); p.Margin=new Padding(0,0,0,5);p.Controls.Add(TextLabel(label,9,Ui.Muted));p.Controls.Add(control);return p; }
    private void SetMode(string next)
    {
        mode=next; password.Clear();repeat.Clear();code.Clear();result.Text=""; Render();
    }
    private void Render()
    {
        bool profile=service.SignedIn && !service.RecoverySession;
        authBox.Visible=!profile && mode!="skin"; profileBox.Visible=profile || mode=="skin";
        profileNickRow.Visible=profile;profileActions.Visible=profile;
        loginTab.Selected=mode=="login";registerTab.Selected=mode is "register" or "confirm";recoveryTab.Selected=mode is "recovery" or "reset";
        skinTab.Selected=mode=="skin";
        foreach(var tab in new[]{loginTab,registerTab,recoveryTab,skinTab}) tab.Invalidate();
        emailRow.Visible=mode!="reset"; nickRow.Visible=mode=="register";
        passwordRow.Visible=mode is "login" or "register" or "reset"; repeatRow.Visible=mode is "register" or "reset";
        codeRow.Visible=mode is "confirm" or "recovery"; sendCode.Visible=mode is "confirm" or "recovery";
        sendCode.Text=mode=="confirm"?"Отправить снова":"Отправить код"; confirmEmail.Visible=mode is "register" or "login";
        submit.Text=mode switch {"register"=>"Создать аккаунт","confirm"=>"Подтвердить почту","recovery"=>"Подтвердить код","reset"=>"Сохранить пароль",_=>"Войти"};
        foreach(var c in operations) c.Enabled=!busy;
        foreach(var c in new Control[]{submit,sendCode,saveNick,reload,logout,uploadSkin}) c.Enabled=!busy && config.Enabled && (c==submit || c==sendCode || profile);
        uploadSkin.Enabled=uploadSkin.Enabled && selectedSkin!=null;
        profileNick.Enabled=!busy && profile;
    }
    private async Task Submit(CancellationToken ct)
    {
        if(mode is "register" or "reset") { if(password.Text!=repeat.Text) throw new AccountException("Пароли не совпадают."); Accounts.ValidatePassword(password.Text); }
        switch(mode)
        {
            case "register":
                await service.SignUp(email.Text,password.Text,nickname.Text.Trim(),ct);SetMode("confirm");result.Text="Проверь почту: если регистрация доступна, придёт код подтверждения. Введи его здесь.";break;
            case "confirm":
                await service.VerifyCode(email.Text,code.Text,false,ct);password.Clear();code.Clear();await LoadProfile(ct);result.Text="Почта подтверждена. Добро пожаловать в MinETRO.";break;
            case "recovery":
                await service.VerifyCode(email.Text,code.Text,true,ct);SetMode("reset");result.Text="Код принят. Задай новый пароль.";break;
            case "reset":
                await service.SetRecoveredPassword(password.Text,ct);ClearProfile();SetMode("login");result.Text="Пароль изменён. Войди с новым паролем.";break;
            default:
                await service.SignIn(email.Text,password.Text,ct);password.Clear();await LoadProfile(ct);result.Text="Вход выполнен.";break;
        }
    }
    private async Task LoadProfile(CancellationToken ct)
    {
        var profile=await service.Profile(ct); profileNick.Text=profile.Nickname;model.SelectedIndex=profile.SkinModel=="alex"?1:0;
        selectedSkin=null;preview.SetSkin(null);skinState.Text="Скин не загружен";
        var bytes=await service.DownloadSkin(profile,ct);
        if(bytes!=null) { selectedSkin=SkinPreview.Normalize(bytes);preview.SetSkin(selectedSkin);skinState.Text="Скин из твоего профиля"; }
        ProfileChanged?.Invoke(profile.Nickname);
    }
    private void ChooseSkin()
    {
        using var d=new OpenFileDialog {Filter="Minecraft PNG-скин|*.png",Title="Выбери скин 64×64 или 64×32"};
        if(d.ShowDialog(this)!=DialogResult.OK)return;
        try
        {
            if(new FileInfo(d.FileName).Length>256*1024)throw new AccountException("Скин больше 256 КБ.");
            var normalized=SkinPreview.Normalize(File.ReadAllBytes(d.FileName));preview.SetSkin(normalized);selectedSkin=normalized;
            if(System.Buffers.Binary.BinaryPrimitives.ReadInt32BigEndian(normalized.AsSpan(20,4))==32) model.SelectedIndex=0;
            skinState.Text="Выбран локально · ещё не загружен";result.Text=config.Enabled?"Нажми «Загрузить скин», чтобы сохранить его в профиле.":"Предпросмотр доступен. Для сохранения нужен подключённый Supabase.";
        }
        catch(Exception ex) when(ex is IOException or UnauthorizedAccessException) { result.Text=ex.Message; }
        Render();
    }
    private void ClearProfile() { selectedSkin=null;preview.SetSkin(null);profileNick.Clear();ProfileChanged?.Invoke(null); }
    private async Task Run(Func<CancellationToken,Task> action)
    {
        if(busy)return;busy=true;Render();result.Text="Подожди…";
        try { await action(lifetime.Token); }
        catch(ObjectDisposedException) when(IsDisposed) { }
        catch(OperationCanceledException) { if(!IsDisposed) result.Text="Операция отменена или истекло время ожидания."; }
        catch(AccountException ex) { if(!IsDisposed)result.Text=ex.Message; }
        catch(Exception ex) when(ex is HttpRequestException or IOException or System.Text.Json.JsonException) { if(!IsDisposed)result.Text="Не удалось связаться с сервисом аккаунтов. Проверь подключение и настройку Supabase."; }
        finally { busy=false;if(!IsDisposed) {password.Clear();repeat.Clear();if(!service.SignedIn) ClearProfile();Render();} }
    }
    public void ShowRegistration() => SetMode("register");
    public void ShowSkin() => SetMode("skin");
    public void SmokeTest()
    {
        SkinPreview.SmokeTest();
        if(!config.Enabled && (submit.Enabled || uploadSkin.Enabled)) throw new InvalidOperationException("Unconfigured accounts must not pretend to register/upload.");
        SetMode("register");if(!nickRow.Visible || !repeatRow.Visible)throw new InvalidOperationException("Registration fields missing.");
        SetMode("recovery");if(!codeRow.Visible)throw new InvalidOperationException("Recovery code field missing.");SetMode("login");
    }
    protected override void Dispose(bool disposing) { if(disposing) {lifetime.Cancel();service.Dispose();lifetime.Dispose();}base.Dispose(disposing); }
}
