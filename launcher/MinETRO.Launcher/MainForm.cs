using System.Diagnostics;
using System.Runtime.InteropServices;
using MinETRO.Core;

namespace MinETRO.Launcher;
internal sealed class MainForm : Form
{
    private PackManifest manifest;
    private readonly HttpClient http = Transfer.CreateClient();
    private readonly TextBox folder = new() { BackColor=Ui.Card, ForeColor=Ui.Text, BorderStyle=BorderStyle.FixedSingle, Dock=DockStyle.Fill };
    private readonly NumericUpDown memory = new() { Minimum=2048,Maximum=16384,Increment=1024,Value=4096,BackColor=Ui.Card,ForeColor=Ui.Text,Width=120 };
    private readonly Label state = Label("",9,Ui.Muted), pageTitle=Label("MINETRO",9,Ui.Muted,true), releaseState=Label("GitHub Releases · проверка после входа",10,Ui.Muted);
    private readonly ThinProgress bar = new();
    private readonly HomeCanvas home = new();
    private readonly AuthArtwork authArt = new();
    private readonly Panel settingsPage = new() { AutoScroll=true,BackColor=Ui.Bg };
    private readonly AccountPage accountPage;
    private readonly Sidebar sidebar = new();
    private readonly Panel titleBar = new() {BackColor=Ui.Bg};
    private readonly LauncherButton mainAction=new() {Accent=true,Text="Проверить релизы"}, install=new() {Text="Установить / проверить",Accent=true}, cancel=new() {Text="Отменить",Visible=false};
    private readonly LauncherButton homeNav=new(){Text="Играть",Glyph="play",Nav=true,Quiet=true}, settingsNav=new(){Text="Настройки",Glyph="settings",Nav=true,Quiet=true}, profileNav=new(){Text="Мой профиль",Glyph="info",Nav=true,Quiet=true};
    private readonly LauncherButton open=new(){Text="Папка сборок",Glyph="folder",Quiet=true}, refresh=new(){Text="Проверить обновления",Glyph="arrow"};
    private readonly List<Control> busyControls=[];
    private CancellationTokenSource? cancellation;
    private bool busy,initialized;
    private string currentPage="auth";
    public static string SettingsDirectory=>Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),"MinETRO-Test-Launcher");
    private readonly string settings=Path.Combine(SettingsDirectory,"folder.txt");
    private bool HasAccess=>accountPage.Authenticated;
    [DllImport("user32.dll")] private static extern bool ReleaseCapture();
    [DllImport("user32.dll")] private static extern nint SendMessage(nint hWnd,int msg,nint wParam,nint lParam);
    public MainForm(PackManifest initial,AccountPage? accounts=null,bool checkUpdates=true)
    {
        manifest=initial; accountPage=accounts??new AccountPage();Text="MinETRO Launcher";
        FormBorderStyle=FormBorderStyle.None;AutoScaleMode=AutoScaleMode.Dpi;AutoScaleDimensions=new SizeF(96,96);
        Font=new Font("Segoe UI",10);BackColor=Ui.Bg;ForeColor=Ui.Text;DoubleBuffered=true;
        Icon=new Icon(Path.Combine(AppContext.BaseDirectory,"Assets","minetro.ico"));
        ClientSize=new Size(1120,740);MinimumSize=new Size(980,690);StartPosition=FormStartPosition.CenterScreen;
        var area=Screen.PrimaryScreen?.WorkingArea??new Rectangle(0,0,1160,800);
        ClientSize=new Size(Math.Min(1120,Math.Max(980,area.Width-32)),Math.Min(740,Math.Max(690,area.Height-32)));
        Controls.AddRange([sidebar,authArt,titleBar,home,settingsPage,accountPage,state,bar,cancel]);
        sidebar.Controls.AddRange([homeNav,settingsNav,profileNav,open]);
        var minimize=new LauncherButton{Glyph="min",Quiet=true,AccessibleName="Свернуть"};var close=new LauncherButton{Glyph="close",Quiet=true,AccessibleName="Закрыть"};
        titleBar.Controls.AddRange([pageTitle,minimize,close]);minimize.Click+=(_,_)=>WindowState=FormWindowState.Minimized;close.Click+=(_,_)=>Close();
        titleBar.MouseDown+=DragWindow;pageTitle.MouseDown+=DragWindow;
        titleBar.Resize+=(_,_)=>{float s=DeviceDpi/96f;minimize.SetBounds(titleBar.Width-(int)(96*s),0,(int)(42*s),titleBar.Height);close.SetBounds(titleBar.Width-(int)(50*s),0,(int)(42*s),titleBar.Height);};
        home.Controls.Add(mainAction);
        homeNav.Click+=(_,_)=>ShowPage("home");settingsNav.Click+=(_,_)=>ShowPage("settings");profileNav.Click+=(_,_)=>ShowPage("profile");
        accountPage.ProfileChanged+=nick=>
        {
            var wasLocked=currentPage=="auth";profileNav.Text=nick??"Мой профиль";profileNav.Invalidate();
            if(!HasAccess){cancellation?.Cancel();ShowPage("auth");}
            else if(wasLocked){ShowPage("home");if(checkUpdates)BeginInvoke((Action)(async()=>await RefreshReleaseAfterLogin()));}
            RefreshButtons();
        };
        BuildSettings();
        mainAction.Click+=async(_,_)=>await Run(async ct=>{if(manifest.Archive==null)await CheckReleases(ct);else if(Installed())await Play(ct);else await Install(ct);});
        install.Click+=async(_,_)=>await Run(Install);refresh.Click+=async(_,_)=>await Run(CheckReleases);
        open.Click+=async(_,_)=>await Run(ct=>{ct.ThrowIfCancellationRequested();var root=Service().Root;Directory.CreateDirectory(root);Process.Start(new ProcessStartInfo("explorer.exe"){ArgumentList={root},UseShellExecute=true});return Task.CompletedTask;});
        cancel.Click+=(_,_)=>cancellation?.Cancel();
        folder.Text=File.Exists(settings)?File.ReadAllText(settings):Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),"MinETRO-Test");
        folder.TextChanged+=(_,_)=>RefreshButtons();
        FormClosing+=(_,e)=>{if(busy){e.Cancel=true;cancellation?.Cancel();Status("Отменяем операцию. После завершения можно закрыть окно.");}};
        initialized=true;UpdateManifest();ShowPage("auth");
    }
    private void DragWindow(object? sender,MouseEventArgs e){if(e.Button==MouseButtons.Left){ReleaseCapture();SendMessage(Handle,0xA1,2,0);}}
    private static Label Label(string text,float size,Color color,bool bold=false)=>new(){Text=text,Font=new Font("Segoe UI",size,bold?FontStyle.Bold:FontStyle.Regular),ForeColor=color,AutoSize=true,MaximumSize=new Size(650,0),BackColor=Color.Transparent};
    private static TableLayoutPanel Stack()=>new(){AutoSize=true,Dock=DockStyle.Top,ColumnCount=1,Padding=new Padding(0,0,0,24),BackColor=Ui.Bg};
    private void BuildSettings()
    {
        var stack=Stack();settingsPage.Controls.Add(stack);stack.Controls.Add(Label("Настройки",24,Ui.Text,true));stack.Controls.Add(Label("Всё необходимое для запуска — в одном месте.",10,Ui.Muted));
        var data=new TableLayoutPanel{AutoSize=true,Dock=DockStyle.Top,ColumnCount=1,Padding=new Padding(22),BackColor=Ui.Card,Margin=new Padding(0,24,0,14)};
        data.Controls.Add(Label("Папка сборок",13,Ui.Text,true));data.Controls.Add(Label("Отдельная папка. Твоя рабочая .minecraft останется нетронутой.",9,Ui.Muted));
        var choose=new LauncherButton{Text="Выбрать папку",Size=new Size(146,38)};choose.Click+=(_,_)=>{if(!HasAccess)return;using var d=new FolderBrowserDialog{Description="Отдельная папка MinETRO",UseDescriptionForTitle=true,SelectedPath=folder.Text};if(d.ShowDialog(this)==DialogResult.OK)folder.Text=d.SelectedPath;};
        var row=new TableLayoutPanel{AutoSize=true,Dock=DockStyle.Top,ColumnCount=2,Margin=new Padding(0,12,0,4)};row.ColumnStyles.Add(new ColumnStyle(SizeType.Percent,100));row.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize));row.Controls.Add(folder);row.Controls.Add(choose);data.Controls.Add(row);stack.Controls.Add(data);
        var updates=new TableLayoutPanel{AutoSize=true,Dock=DockStyle.Top,ColumnCount=1,Padding=new Padding(22),BackColor=Ui.Card,Margin=new Padding(0,0,0,14)};
        updates.Controls.Add(Label("Обновления сборки",13,Ui.Text,true));updates.Controls.Add(Label("Полная сборка загружается автоматически из GitHub Releases MinETRO.\nВводить ссылки и подключать каналы не нужно.",9,Ui.Muted));releaseState.Margin=new Padding(0,12,0,8);updates.Controls.Add(releaseState);refresh.Size=new Size(240,40);updates.Controls.Add(refresh);stack.Controls.Add(updates);
        var ram=new FlowLayoutPanel{AutoSize=true,Dock=DockStyle.Top,BackColor=Ui.Card,Padding=new Padding(22),Margin=new Padding(0,0,0,18)};ram.Controls.Add(Label("Память новой установки, МБ   ",11,Ui.Text,true));ram.Controls.Add(memory);stack.Controls.Add(ram);
        install.Size=new Size(240,44);stack.Controls.Add(install);stack.Controls.Add(Label("Каждая версия устанавливается отдельно: миры и packv4 не перезаписываются.\nЛицензия Minecraft и Java 8 x64 настраиваются в окне игрового движка при первом запуске.",9,Ui.Muted));
        busyControls.AddRange([folder,memory,choose,refresh]);
    }
    protected override void OnResize(EventArgs e){base.OnResize(e);if(initialized)Arrange();}
    private void Arrange()
    {
        float s=DeviceDpi/96f;int P(float n)=>(int)Math.Round(n*s);float w=ClientSize.Width/s,h=ClientSize.Height/s;bool unlocked=HasAccess;
        sidebar.SetBounds(0,0,P(216),ClientSize.Height);titleBar.SetBounds(unlocked?P(216):0,0,unlocked?ClientSize.Width-P(216):ClientSize.Width,P(60));pageTitle.SetBounds(P(32),P(23),P(500),P(22));
        homeNav.SetBounds(P(16),P(155),P(184),P(46));settingsNav.SetBounds(P(16),P(209),P(184),P(46));profileNav.SetBounds(P(16),P(263),P(184),P(46));open.SetBounds(P(16),P(h-118),P(184),P(44));
        foreach(var page in new Control[]{home,settingsPage,accountPage})page.SetBounds(P(248),P(78),P(w-280),P(h-156));
        if(!unlocked){float left=w*.39f;authArt.SetBounds(0,P(60),P(left),P(h-60));accountPage.SetBounds(P(left+28),P(78),P(w-left-60),P(h-102));}
        float fit=home.Fit;mainAction.SetBounds(home.Width-P(252*fit),P(383*fit),P(228*fit),P(48*fit));
        state.SetBounds(P(252),P(h-61),P(w-(cancel.Visible?418:292)),P(32));state.AutoSize=false;state.AutoEllipsis=true;
        bar.SetBounds(P(248),P(h-20),P(w-280),P(3));cancel.SetBounds(P(w-145),P(h-63),P(112),P(34));home.Invalidate();sidebar.Invalidate();
    }
    private void ShowPage(string page)
    {
        if(!HasAccess)page="auth";else if(page=="auth")page="home";
        currentPage=page;bool unlocked=HasAccess;
        sidebar.Visible=state.Visible=bar.Visible=unlocked;authArt.Visible=!unlocked;
        home.Visible=unlocked&&page=="home";settingsPage.Visible=unlocked&&page=="settings";accountPage.Visible=!unlocked||page=="profile";
        accountPage.SetGateMode(!unlocked);homeNav.Selected=page=="home";settingsNav.Selected=page=="settings";profileNav.Selected=page=="profile";
        pageTitle.Text=page switch{"auth"=>"MINETRO   /   ДОБРО ПОЖАЛОВАТЬ","settings"=>"ЛАУНЧЕР   /   НАСТРОЙКИ","profile"=>"ЛАУНЧЕР   /   МОЙ ПРОФИЛЬ",_=>"ЛАУНЧЕР   /   ГЛАВНАЯ"};
        foreach(var nav in new[]{homeNav,settingsNav,profileNav}){nav.Enabled=unlocked;nav.Invalidate();}Arrange();
    }
    private Installer Service(){if(string.IsNullOrWhiteSpace(folder.Text)||!Path.IsPathFullyQualified(folder.Text))throw new IOException("Выбери полный путь к отдельной папке.");return new Installer(folder.Text,new Transfer(http));}
    private bool Installed()=>manifest.Archive!=null&&Path.IsPathFullyQualified(folder.Text)&&Directory.Exists(Path.Combine(folder.Text,"prism-data","instances",manifest.InstanceId));
    private async Task Install(CancellationToken ct){await Service().Install(manifest,(int)memory.Value,Progress(),ct);Status("Сборка установлена. Можно запускать.");}
    private async Task Play(CancellationToken ct){var service=Service();await service.Verify(manifest,ct);await service.PrepareEngine(manifest,Progress(),ct);await accountPage.RequireAccess(ct);ct.ThrowIfCancellationRequested();Process.Start(service.StartInfo(manifest,true));Status("Запуск передан игровому движку.");}
    private IProgress<TransferProgress> Progress()=>new Progress<TransferProgress>(p=>{Status(p.Message);bar.Percent=p.Percent;bar.Invalidate();});
    private void Status(string message)=>state.Text=message;
    private void UpdateManifest()
    {
        home.HasArchive=manifest.Archive!=null;home.VersionName=manifest.Archive==null?"ожидается релиз":manifest.Version;home.PackName=manifest.Name;
        releaseState.Text=manifest.Archive==null?"Полная сборка пока не опубликована в GitHub Releases.":"Доступна версия: "+manifest.Version;
        Status(releaseState.Text);RefreshButtons();
    }
    private void RefreshButtons()
    {
        install.Enabled=!busy&&HasAccess&&manifest.Archive!=null;mainAction.Enabled=!busy&&HasAccess;open.Enabled=!busy&&HasAccess;
        home.Installed=Installed();mainAction.Text=busy?"Подожди…":manifest.Archive==null?"Проверить релизы":home.Installed?"Играть":"Установить сборку";mainAction.Glyph=manifest.Archive!=null&&home.Installed?"play":"";home.Invalidate();
    }
    private async Task CheckReleases(CancellationToken ct)
    {
        Status("Проверка GitHub Releases…");var latest=await new GitHubReleases(new Transfer(http)).Latest(ct);
        if(latest==null){releaseState.Text="Полная сборка пока не опубликована в GitHub Releases.";Status(releaseState.Text);return;}
        await new ChannelStore(Path.Combine(SettingsDirectory,"github-release")).Save(latest,ct);manifest=latest;UpdateManifest();
    }
    public async Task RefreshReleaseAfterLogin(){if(HasAccess)await Run(CheckReleases);}
    private async Task Run(Func<CancellationToken,Task> action)
    {
        if(!HasAccess){ShowPage("auth");return;}if(busy)return;busy=true;cancellation=new();
        foreach(var c in busyControls)c.Enabled=false;accountPage.Enabled=false;cancel.Visible=true;bar.Active=true;bar.Percent=null;Arrange();RefreshButtons();
        try{await accountPage.RequireAccess(cancellation.Token);await action(cancellation.Token);Directory.CreateDirectory(SettingsDirectory);await File.WriteAllTextAsync(settings,folder.Text,cancellation.Token);}
        catch(OperationCanceledException){Status("Операция отменена. Готовые сборки и миры сохранены.");}
        catch(Exception ex){Status("Ошибка: "+ex.Message);if(HasAccess)MessageBox.Show(this,ex.Message,"MinETRO · операция не завершена",MessageBoxButtons.OK,MessageBoxIcon.Warning);}
        finally{cancellation?.Dispose();cancellation=null;busy=false;cancel.Visible=false;bar.Active=false;bar.Percent=null;bar.Invalidate();foreach(var c in busyControls)c.Enabled=true;accountPage.Enabled=true;if(!HasAccess)ShowPage("auth");Arrange();RefreshButtons();}
    }
    public async Task SmokeTest(string? screenshot)
    {
        Application.DoEvents();ShowPage("home");
        if(currentPage!="auth"||home.Visible||sidebar.Visible||mainAction.Enabled||open.Enabled)throw new InvalidOperationException("Launcher must remain locked before login.");
        bool executed=false;await Run(_=>{executed=true;return Task.CompletedTask;});if(executed)throw new InvalidOperationException("Locked actions executed.");
        accountPage.SmokeTest();accountPage.ShowRegistration();Capture(screenshot);
        await accountPage.SmokeSignIn();if(!HasAccess||currentPage!="home")throw new InvalidOperationException("Valid profile must unlock launcher.");
        RefreshButtons();Capture(screenshot==null?null:Path.ChangeExtension(screenshot,".home.png"));
        ShowPage("settings");Capture(screenshot==null?null:Path.ChangeExtension(screenshot,".settings.png"));
        ShowPage("profile");Capture(screenshot==null?null:Path.ChangeExtension(screenshot,".profile.png"));
        accountPage.ShowSkin();Capture(screenshot==null?null:Path.ChangeExtension(screenshot,".skin.png"));
        await accountPage.SmokeSignOut();if(HasAccess||currentPage!="auth"||home.Visible||open.Enabled)throw new InvalidOperationException("Logout must lock all launcher actions.");
    }
    private void Capture(string? path){if(path==null)return;Application.DoEvents();Arrange();Refresh();using var bitmap=new Bitmap(Width,Height);DrawToBitmap(bitmap,new Rectangle(0,0,Width,Height));bitmap.Save(path,System.Drawing.Imaging.ImageFormat.Png);}
    protected override void Dispose(bool disposing){if(disposing){http.Dispose();cancellation?.Dispose();Icon?.Dispose();}base.Dispose(disposing);}
}
