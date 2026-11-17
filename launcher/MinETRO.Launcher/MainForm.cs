using System.Diagnostics;
using System.Runtime.InteropServices;
using MinETRO.Core;

namespace MinETRO.Launcher;
public sealed class MainForm : Form
{
    private PackManifest manifest;
    private readonly HttpClient http = Transfer.CreateClient();
    private readonly TextBox folder = Input();
    private readonly TextBox source = Input("HTTPS-ссылка на JSON или путь к файлу");
    private readonly NumericUpDown memory = new() { Minimum = 2048, Maximum = 16384, Increment = 1024, Value = 4096, BackColor = Ui.Card, ForeColor = Ui.Text, Width = 120 };
    private readonly Label state = Label("", 9, Ui.Muted);
    private readonly Label pageTitle = Label("ГЛАВНАЯ", 9, Ui.Muted, true);
    private readonly Label infoStatus = Label("", 11, Ui.Amber);
    private readonly ThinProgress bar = new();
    private readonly HomeCanvas home = new();
    private readonly Panel settingsPage = new() { AutoScroll = true, BackColor = Ui.Bg };
    private readonly Panel infoPage = new() { AutoScroll = true, BackColor = Ui.Bg };
    private readonly AccountPage accountPage = new();
    private readonly LauncherButton profileNav = new() { Text = "Мой профиль", Glyph = "info", Nav = true, Quiet = true };
    private readonly Sidebar sidebar = new();
    private readonly Panel titleBar = new() { BackColor = Ui.Bg };
    private readonly LauncherButton mainAction = new() { Accent = true, Text = "Подключить сборку" };
    private readonly LauncherButton install = new() { Text = "Установить / проверить", Accent = true };
    private readonly LauncherButton cancel = new() { Text = "Отменить", Visible = false };
    private readonly LauncherButton homeNav = new() { Text = "Играть", Glyph = "play", Nav = true, Quiet = true };
    private readonly LauncherButton settingsNav = new() { Text = "Настройки", Glyph = "settings", Nav = true, Quiet = true };
    private readonly LauncherButton infoNav = new() { Text = "О сборке", Glyph = "info", Nav = true, Quiet = true };
    private readonly LauncherButton account = new() { Text = "Войти через Prism", Glyph = "key" };
    private readonly LauncherButton open = new() { Text = "Папка сборок", Glyph = "folder", Quiet = true };
    private readonly List<Control> busyControls = [];
    private CancellationTokenSource? cancellation;
    private bool busy, initialized;
    private string currentPage = "home";
    public static string SettingsDirectory => Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "MinETRO-Test-Launcher");
    private readonly string settings = Path.Combine(SettingsDirectory, "folder.txt");
    [DllImport("user32.dll")] private static extern bool ReleaseCapture();
    [DllImport("user32.dll")] private static extern nint SendMessage(nint hWnd, int msg, nint wParam, nint lParam);
    public MainForm(PackManifest initial)
    {
        manifest = initial; Text = "MinETRO Launcher · TEST 0.3";
        FormBorderStyle = FormBorderStyle.None; AutoScaleMode = AutoScaleMode.Dpi; AutoScaleDimensions = new SizeF(96, 96);
        Font = new Font("Segoe UI", 10); BackColor = Ui.Bg; ForeColor = Ui.Text; DoubleBuffered = true;
        ClientSize = new Size(1120, 740); MinimumSize = new Size(980, 690); StartPosition = FormStartPosition.CenterScreen;
        var area = Screen.PrimaryScreen?.WorkingArea ?? new Rectangle(0, 0, 1160, 800);
        ClientSize = new Size(Math.Min(1120, Math.Max(980, area.Width - 32)), Math.Min(740, Math.Max(690, area.Height - 32)));
        Controls.AddRange([sidebar, titleBar, home, settingsPage, infoPage, accountPage, state, bar, cancel]);
        sidebar.Controls.AddRange([homeNav, settingsNav, infoNav, profileNav, account, open]);
        var minimize = new LauncherButton { Glyph = "min", Quiet = true, AccessibleName = "Свернуть" };
        var close = new LauncherButton { Glyph = "close", Quiet = true, AccessibleName = "Закрыть" };
        titleBar.Controls.AddRange([pageTitle, minimize, close]);
        minimize.Click += (_, _) => WindowState = FormWindowState.Minimized; close.Click += (_, _) => Close();
        titleBar.MouseDown += DragWindow; pageTitle.MouseDown += DragWindow;
        titleBar.Resize += (_, _) => { float s = DeviceDpi / 96f; minimize.SetBounds(titleBar.Width - (int)(96 * s), 0, (int)(42 * s), titleBar.Height); close.SetBounds(titleBar.Width - (int)(50 * s), 0, (int)(42 * s), titleBar.Height); };
        home.Controls.Add(mainAction);
        homeNav.Click += (_, _) => ShowPage("home"); settingsNav.Click += (_, _) => ShowPage("settings"); infoNav.Click += (_, _) => ShowPage("info");
        profileNav.Click += (_, _) => ShowPage("profile");
        accountPage.ProfileChanged += nick => { profileNav.Text = nick ?? "Мой профиль"; profileNav.Invalidate(); };
        BuildSettings(); BuildInfo();
        mainAction.Click += async (_, _) => {
            if (manifest.Archive == null) { ShowPage("settings"); source.Focus(); }
            else if (Installed()) await Run(Play);
            else await Run(Install);
        };
        install.Click += async (_, _) => await Run(Install);
        account.Click += async (_, _) => await Run(async ct => { var service = Service(); await service.PrepareEngine(manifest, Progress(), ct); ct.ThrowIfCancellationRequested(); Process.Start(service.StartInfo(manifest, false)); Status("Prism открыт. Войди в Microsoft в его окне."); });
        open.Click += (_, _) => { try { var root = Service().Root; Directory.CreateDirectory(root); Process.Start(new ProcessStartInfo("explorer.exe") { ArgumentList = { root }, UseShellExecute = true }); } catch (Exception ex) { MessageBox.Show(this, ex.Message, "MinETRO"); } };
        cancel.Click += (_, _) => cancellation?.Cancel();
        folder.Text = File.Exists(settings) ? File.ReadAllText(settings) : Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "MinETRO-Test");
        folder.TextChanged += (_, _) => RefreshButtons();
        FormClosing += (_, e) => { if (busy) { e.Cancel = true; cancellation?.Cancel(); Status("Отменяем операцию. После завершения можно закрыть окно."); } };
        initialized = true; Arrange(); UpdateManifest(); ShowPage("home");
    }
    private void DragWindow(object? sender, MouseEventArgs e) { if (e.Button == MouseButtons.Left) { ReleaseCapture(); SendMessage(Handle, 0xA1, 2, 0); } }
    private static TextBox Input(string placeholder = "") => new() { PlaceholderText = placeholder, BackColor = Color.FromArgb(29, 37, 46), ForeColor = Ui.Text, BorderStyle = BorderStyle.FixedSingle, Dock = DockStyle.Fill };
    private static Label Label(string text, float size, Color color, bool bold = false) => new() { Text = text, Font = new Font("Segoe UI", size, bold ? FontStyle.Bold : FontStyle.Regular), ForeColor = color, AutoSize = true, MaximumSize = new Size(760, 0), BackColor = Color.Transparent };
    private static TableLayoutPanel Stack() => new() { AutoSize = true, Dock = DockStyle.Top, ColumnCount = 1, Padding = new Padding(0, 0, 0, 24), BackColor = Ui.Bg };
    private static TableLayoutPanel Row(Control fill, params Control[] buttons)
    {
        var row = new TableLayoutPanel { Dock = DockStyle.Top, AutoSize = true, ColumnCount = buttons.Length + 1, Margin = new Padding(0, 12, 0, 4) };
        row.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100)); row.Controls.Add(fill, 0, 0);
        for (var i = 0; i < buttons.Length; i++) { row.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize)); buttons[i].Size = new Size(i == buttons.Length - 1 ? 146 : 100, 38); row.Controls.Add(buttons[i], i + 1, 0); }
        return row;
    }
    private void BuildSettings()
    {
        var stack = Stack(); settingsPage.Controls.Add(stack);
        stack.Controls.Add(Label("Настройки", 24, Ui.Text, true));
        stack.Controls.Add(Label("Всё необходимое для первого запуска — в одном месте.", 10, Ui.Muted));
        var data = new TableLayoutPanel { AutoSize = true, Dock = DockStyle.Top, ColumnCount = 1, Padding = new Padding(22), BackColor = Ui.Card, Margin = new Padding(0, 24, 0, 14) };
        data.Controls.Add(Label("Папка сборок", 13, Ui.Text, true));
        data.Controls.Add(Label("Выбери отдельную папку. Твоя рабочая .minecraft останется нетронутой.", 9, Ui.Muted));
        var choose = new LauncherButton { Text = "Выбрать папку" }; choose.Click += (_, _) => ChooseFolder(); data.Controls.Add(Row(folder, choose)); stack.Controls.Add(data);
        var channel = new TableLayoutPanel { AutoSize = true, Dock = DockStyle.Top, ColumnCount = 1, Padding = new Padding(22), BackColor = Ui.Card, Margin = new Padding(0, 0, 0, 14) };
        channel.Controls.Add(Label("Канал сборки", 13, Ui.Text, true));
        channel.Controls.Add(Label("Подключи доверенный JSON-манифест. Это не ссылка на ZIP или отдельный мод.", 9, Ui.Muted));
        var load = new LauncherButton { Text = "Подключить", Accent = true }; load.Click += async (_, _) => await Run(LoadManifest);
        var browse = new LauncherButton { Text = "Файл JSON" }; browse.Click += (_, _) => { using var d = new OpenFileDialog { Filter = "Манифест MinETRO|*.json" }; if (d.ShowDialog(this) == DialogResult.OK) source.Text = d.FileName; };
        channel.Controls.Add(Row(source, browse, load)); stack.Controls.Add(channel);
        var ram = new FlowLayoutPanel { AutoSize = true, Dock = DockStyle.Top, BackColor = Ui.Card, Padding = new Padding(22), Margin = new Padding(0, 0, 0, 18) };
        ram.Controls.Add(Label("Память при установке, МБ   ", 11, Ui.Text, true)); ram.Controls.Add(memory);
        ram.Controls.Add(Label("   Для готовой версии — в настройках Prism.", 9, Ui.Muted)); stack.Controls.Add(ram);
        install.Size = new Size(238, 44); stack.Controls.Add(install);
        stack.Controls.Add(Label("Java 8 x64 и учётная запись Minecraft настраиваются в Prism.\nНовая версия устанавливается отдельно: миры и packv4 не перезаписываются.", 9, Ui.Muted));
        busyControls.AddRange([folder, source, memory, choose, load, browse]);
    }
    private void BuildInfo()
    {
        var stack = Stack(); infoPage.Controls.Add(stack);
        stack.Controls.Add(Label("О сборке", 24, Ui.Text, true));
        stack.Controls.Add(Label("Твой мир под поверхностью.", 12, Ui.Muted));
        var card = new TableLayoutPanel { AutoSize = true, Dock = DockStyle.Top, ColumnCount = 1, Padding = new Padding(24), BackColor = Ui.Card, Margin = new Padding(0, 26, 0, 18) };
        card.Controls.Add(Label("MinETRO  /  тестовый канал", 17, Ui.Text, true));
        card.Controls.Add(Label("Minecraft 1.12.2  ·  Forge 14.23.5.2860  ·  Java 8 x64", 10, Ui.Muted));
        infoStatus.Margin = new Padding(0, 20, 0, 8); card.Controls.Add(infoStatus);
        card.Controls.Add(Label("Архив полного модпака ещё нужен: один JAR MinETRO не заменяет MWF, packv4 и остальные ресурсы. Это тестовая оболочка, не готовая игровая сборка.", 10, Ui.Muted)); stack.Controls.Add(card);
        stack.Controls.Add(Label("Сохраняем твоё", 14, Ui.Text, true));
        stack.Controls.Add(Label("Каждая версия хранится в отдельном экземпляре Prism. Обновление не стирает старые миры и настроенные руки. Миры не переносятся автоматически: перед переносом сделай резервную копию.", 10, Ui.Muted));
        stack.Controls.Add(Label("Вход без лишних посредников", 14, Ui.Text, true));
        stack.Controls.Add(Label("Пароли и токены вводятся только в Prism / Microsoft. Эта оболочка не читает accounts.json и не обходит проверку лицензии Minecraft Java. Prism — независимый open-source проект.", 10, Ui.Muted));
        stack.Controls.Add(Label("Launcher TEST 0.3  ·  Windows x64  ·  неподписанная тестовая версия\nПроверки интерфейса и установщика не равны запуску полного модпака.", 9, Ui.Muted));
    }
    protected override void OnResize(EventArgs e) { base.OnResize(e); if (initialized) Arrange(); }
    private void Arrange()
    {
        float s = DeviceDpi / 96f; int P(float n) => (int)Math.Round(n * s); float w = ClientSize.Width / s, h = ClientSize.Height / s;
        sidebar.SetBounds(0, 0, P(216), ClientSize.Height); titleBar.SetBounds(P(216), 0, P(w - 216), P(60)); pageTitle.SetBounds(P(32), P(23), P(500), P(22));
        homeNav.SetBounds(P(16), P(155), P(184), P(46)); settingsNav.SetBounds(P(16), P(209), P(184), P(46)); infoNav.SetBounds(P(16), P(263), P(184), P(46)); profileNav.SetBounds(P(16), P(317), P(184), P(46));
        open.SetBounds(P(16), P(h - 173), P(184), P(42)); account.SetBounds(P(16), P(h - 118), P(184), P(44));
        foreach (var page in new Control[] { home, settingsPage, infoPage, accountPage }) page.SetBounds(P(248), P(78), P(w - 280), P(h - 156));
        float fit = home.Fit; mainAction.SetBounds(home.Width - P(252 * fit), P(383 * fit), P(228 * fit), P(48 * fit));
        state.SetBounds(P(252), P(h - 61), P(w - (cancel.Visible ? 418 : 292)), P(32)); state.AutoSize = false; state.AutoEllipsis = true;
        bar.SetBounds(P(248), P(h - 20), P(w - 280), P(3)); cancel.SetBounds(P(w - 145), P(h - 63), P(112), P(34));
        home.Invalidate(); sidebar.Invalidate();
    }
    private void ShowPage(string page)
    {
        currentPage = page; home.Visible = page == "home"; settingsPage.Visible = page == "settings"; infoPage.Visible = page == "info"; accountPage.Visible = page == "profile";
        homeNav.Selected = page == "home"; settingsNav.Selected = page == "settings"; infoNav.Selected = page == "info"; profileNav.Selected = page == "profile";
        pageTitle.Text = page switch { "profile" => "ЛАУНЧЕР   /   МОЙ ПРОФИЛЬ", "settings" => "ЛАУНЧЕР   /   НАСТРОЙКИ", "info" => "ЛАУНЧЕР   /   О СБОРКЕ", _ => "ЛАУНЧЕР   /   ГЛАВНАЯ" };
        homeNav.Invalidate(); settingsNav.Invalidate(); infoNav.Invalidate(); profileNav.Invalidate();
    }
    private void ChooseFolder()
    {
        using var dialog = new FolderBrowserDialog { Description = "Отдельная папка MinETRO", UseDescriptionForTitle = true, SelectedPath = folder.Text };
        if (dialog.ShowDialog(this) == DialogResult.OK) folder.Text = dialog.SelectedPath;
    }
    private Installer Service()
    {
        if (string.IsNullOrWhiteSpace(folder.Text) || !Path.IsPathFullyQualified(folder.Text)) throw new IOException("Выбери полный путь к отдельной папке.");
        return new Installer(folder.Text, new Transfer(http));
    }
    private bool Installed() => manifest.Archive != null && Path.IsPathFullyQualified(folder.Text) && Directory.Exists(Path.Combine(folder.Text, "prism-data", "instances", manifest.InstanceId));
    private async Task Install(CancellationToken ct) { await Service().Install(manifest, (int)memory.Value, Progress(), ct); Status("Сборка установлена. Можно запускать; при первом входе настрой аккаунт и Java 8 в Prism."); }
    private async Task Play(CancellationToken ct) { var service = Service(); await service.Verify(manifest, ct); await service.PrepareEngine(manifest, Progress(), ct); ct.ThrowIfCancellationRequested(); Process.Start(service.StartInfo(manifest, true)); Status("Запуск передан в Prism. Прогресс Forge и библиотек — в его окне."); }
    private IProgress<TransferProgress> Progress() => new Progress<TransferProgress>(p => { Status(p.Message); bar.Percent = p.Percent; bar.Invalidate(); });
    private void Status(string message) { state.Text = message; }
    private void UpdateManifest()
    {
        home.HasArchive = manifest.Archive != null; home.VersionName = manifest.Version; home.PackName = manifest.Name;
        infoStatus.Text = manifest.Archive == null ? "Полный модпак пока не подключён" : "Канал подключён: " + manifest.Name + " / " + manifest.Version;
        Status(manifest.Archive == null ? "Подключи канал в настройках, чтобы установить сборку." : "Канал подключён. Используй только моды из доверенного источника."); RefreshButtons();
    }
    private void RefreshButtons()
    {
        install.Enabled = !busy && manifest.Archive != null; mainAction.Enabled = !busy;
        home.Installed = Installed(); mainAction.Text = busy ? "Подожди…" : manifest.Archive == null ? "Подключить сборку" : home.Installed ? "Играть" : "Установить сборку";
        mainAction.Glyph = manifest.Archive != null && home.Installed ? "play" : "";
        home.Invalidate();
    }
    private async Task LoadManifest(CancellationToken ct)
    {
        var incoming = PackManifest.Parse(await new Transfer(http).ReadManifest(source.Text.Trim(), ct));
        if (MessageBox.Show(this, $"Подключить «{incoming.Name}», версия {incoming.Version}?\nМоды содержат Java-код. SHA256 проверяет целостность, но не безопасность незнакомых модов.", "Доверие к источнику", MessageBoxButtons.YesNo, MessageBoxIcon.Warning) != DialogResult.Yes) return;
        await new ChannelStore(SettingsDirectory).Save(incoming, ct); manifest = incoming; UpdateManifest();
    }
    private async Task Run(Func<CancellationToken, Task> action)
    {
        if (busy) return; busy = true; cancellation = new();
        foreach (var c in busyControls) c.Enabled = false; account.Enabled = open.Enabled = false; cancel.Visible = true; bar.Active = true; bar.Percent = null; Arrange(); RefreshButtons();
        try { await action(cancellation.Token); Directory.CreateDirectory(Path.GetDirectoryName(settings)!); await File.WriteAllTextAsync(settings, folder.Text); }
        catch (OperationCanceledException) { Status("Операция отменена. Готовые сборки и миры не изменены."); }
        catch (Exception ex) { Status("Ошибка: " + ex.Message); MessageBox.Show(this, ex.Message, "MinETRO · операция не завершена", MessageBoxButtons.OK, MessageBoxIcon.Warning); }
        finally { cancellation?.Dispose(); cancellation = null; busy = false; cancel.Visible = false; bar.Active = false; bar.Percent = null; bar.Invalidate(); foreach (var c in busyControls) c.Enabled = true; account.Enabled = open.Enabled = true; Arrange(); RefreshButtons(); }
    }
    public void SmokeTest(string? screenshot = null)
    {
        CreateControl(); Show(); Application.DoEvents();
        if (manifest.Archive == null && (install.Enabled || mainAction.Text != "Подключить сборку")) throw new InvalidOperationException("Unconfigured pack must only navigate to configuration, never install or launch.");
        mainAction.PerformClick(); if (manifest.Archive == null && currentPage != "settings") throw new InvalidOperationException("Connect action must open Settings.");
        ShowPage("info"); if (infoPage.Visible && !infoNav.Selected) throw new InvalidOperationException("About navigation failed."); ShowPage("home");
        if (screenshot != null)
        {
            // Keep the real, screen-constrained window size. Forcing a wider ClientSize
            // than the CI desktop can leave WinForms' cached client width out of sync
            // with its native bounds and crop both the UI and DrawToBitmap output.
            Show(); Application.DoEvents(); Arrange(); Refresh();
            if (!home.ClientRectangle.Contains(mainAction.Bounds) || home.Right > ClientSize.Width || titleBar.Right > ClientSize.Width)
                throw new InvalidOperationException("Home controls must fit inside the visible window.");
            using (var bitmap = new Bitmap(Width, Height)) { DrawToBitmap(bitmap, new Rectangle(0, 0, Width, Height)); bitmap.Save(screenshot, System.Drawing.Imaging.ImageFormat.Png); }
            ShowPage("settings"); Application.DoEvents(); Refresh();
            using (var bitmap = new Bitmap(Width, Height)) { DrawToBitmap(bitmap, new Rectangle(0, 0, Width, Height)); bitmap.Save(Path.ChangeExtension(screenshot, ".settings.png"), System.Drawing.Imaging.ImageFormat.Png); }
            ShowPage("profile"); Application.DoEvents(); accountPage.SmokeTest(); accountPage.ShowRegistration(); Application.DoEvents(); Refresh();
            using (var bitmap = new Bitmap(Width, Height)) { DrawToBitmap(bitmap, new Rectangle(0, 0, Width, Height)); bitmap.Save(Path.ChangeExtension(screenshot, ".profile.png"), System.Drawing.Imaging.ImageFormat.Png); }
            accountPage.ShowSkin(); Application.DoEvents(); Refresh();
            using (var bitmap = new Bitmap(Width, Height)) { DrawToBitmap(bitmap, new Rectangle(0, 0, Width, Height)); bitmap.Save(Path.ChangeExtension(screenshot, ".skin.png"), System.Drawing.Imaging.ImageFormat.Png); }
            Hide();
        }
    }
    protected override void Dispose(bool disposing) { if (disposing) { http.Dispose(); cancellation?.Dispose(); } base.Dispose(disposing); }
}
