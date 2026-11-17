using System.Diagnostics;
using MinETRO.Core;

namespace MinETRO.Launcher;
public sealed class MainForm : Form
{
    private PackManifest manifest;
    private readonly HttpClient http = Transfer.CreateClient();
    private readonly TextBox folder = new() { Dock = DockStyle.Fill };
    private readonly TextBox source = new() { Dock = DockStyle.Fill, PlaceholderText = "HTTPS-ссылка на манифест или локальный JSON" };
    private readonly NumericUpDown memory = new() { Minimum = 2048, Maximum = 16384, Increment = 1024, Value = 4096, Width = 120 };
    private readonly Label state = new() { AutoSize = true, MaximumSize = new Size(690, 0), ForeColor = Color.FromArgb(215, 223, 225) };
    private readonly Label packInfo = new() { AutoSize = true };
    private readonly ProgressBar bar = new() { Dock = DockStyle.Fill, Height = 12 };
    private readonly LauncherButton install = new() { Text = "УСТАНОВИТЬ / ПРОВЕРИТЬ", AutoSize = true };
    private readonly LauncherButton play = new() { Text = "ИГРАТЬ", AutoSize = true };
    private readonly LauncherButton cancel = new() { Text = "Отмена", AutoSize = true, Enabled = false };
    private readonly List<Control> busyControls = [];
    private CancellationTokenSource? cancellation;
    private bool busy;
    public static string SettingsDirectory => Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "MinETRO-Test-Launcher");
    private readonly string settings = Path.Combine(SettingsDirectory, "folder.txt");
    public MainForm(PackManifest initial)
    {
        manifest = initial; Text = "MinETRO Launcher · TEST 0.1";
        Size = new Size(820, 760); MinimumSize = new Size(780, 640); StartPosition = FormStartPosition.CenterScreen;
        AutoScaleMode = AutoScaleMode.Dpi; Font = new Font("Segoe UI", 10); BackColor = Color.FromArgb(20, 25, 28); ForeColor = Color.FromArgb(230, 235, 236);
        var layout = new TableLayoutPanel { Dock = DockStyle.Fill, Padding = new Padding(28), ColumnCount = 1, RowCount = 13, AutoScroll = true };
        Controls.Add(layout);
        layout.Controls.Add(new Label { Text = "MinETRO", Font = new Font("Segoe UI", 30, FontStyle.Bold), AutoSize = true, ForeColor = Color.FromArgb(219, 169, 89) });
        layout.Controls.Add(new Label { Text = "ТЕСТОВЫЙ КАНАЛ   /   WINDOWS x64", AutoSize = true, ForeColor = Color.Silver, Margin = new Padding(0, 0, 0, 16) });
        layout.Controls.Add(packInfo);
        layout.Controls.Add(new Label { Text = "Папка лаунчера и сборок · не выбирай свою рабочую .minecraft", AutoSize = true, Margin = new Padding(0, 14, 0, 5) });
        var choose = new LauncherButton { Text = "Папка…", AutoSize = true }; choose.Click += (_, _) => ChooseFolder();
        layout.Controls.Add(Row(folder, choose));
        layout.Controls.Add(new Label { Text = "Канал сборки · только доверенный манифест", AutoSize = true, Margin = new Padding(0, 14, 0, 5) });
        var load = new LauncherButton { Text = "Подключить", AutoSize = true }; load.Click += async (_, _) => await Run(LoadManifest);
        var browse = new LauncherButton { Text = "JSON…", AutoSize = true }; browse.Click += (_, _) => { using var d = new OpenFileDialog { Filter = "Манифест MinETRO|*.json" }; if (d.ShowDialog() == DialogResult.OK) source.Text = d.FileName; };
        layout.Controls.Add(Row(source, browse, load));
        var ram = new FlowLayoutPanel { AutoSize = true, Dock = DockStyle.Fill, Margin = new Padding(0, 15, 0, 10) };
        ram.Controls.Add(new Label { Text = "ОЗУ при установке, МБ", AutoSize = true, Padding = new Padding(0, 4, 10, 0) }); ram.Controls.Add(memory);
        ram.Controls.Add(new Label { Text = "  Java 8 x64 выбирается в Prism", AutoSize = true, Padding = new Padding(0, 4, 0, 0) }); layout.Controls.Add(ram);
        var actions = new FlowLayoutPanel { AutoSize = true, Dock = DockStyle.Fill, WrapContents = false };
        actions.Controls.Add(install); actions.Controls.Add(play); actions.Controls.Add(cancel); layout.Controls.Add(actions);
        var engine = new LauncherButton { Text = "Подготовить Prism / войти в Microsoft", AutoSize = true };
        engine.Click += async (_, _) => await Run(async ct => { var service = Service(); await service.PrepareEngine(manifest, Progress(), ct); ct.ThrowIfCancellationRequested(); Process.Start(service.StartInfo(manifest, false)); Status("Prism открыт. Добавь учётную запись Microsoft в его окне."); });
        var open = new LauncherButton { Text = "Открыть папку", AutoSize = true };
        open.Click += (_, _) => { Directory.CreateDirectory(folder.Text); Process.Start(new ProcessStartInfo("explorer.exe") { ArgumentList = { Path.GetFullPath(folder.Text) }, UseShellExecute = true }); };
        var extra = new FlowLayoutPanel { AutoSize = true, Dock = DockStyle.Fill }; extra.Controls.Add(engine); extra.Controls.Add(open); layout.Controls.Add(extra);
        layout.Controls.Add(bar); layout.Controls.Add(state);
        layout.Controls.Add(new Label { Text = "Изолированные версии. Миры и настроенные руки не перезаписываются.\nОбновление создаёт новый экземпляр; старые миры остаются в прежнем.\nВход и загрузка Minecraft — через официальный Prism. Подписка/лицензия Minecraft требуется.", AutoSize = true, MaximumSize = new Size(700, 0), ForeColor = Color.Gray, Margin = new Padding(0, 14, 0, 0) });
        busyControls.AddRange([folder, source, memory, choose, load, browse, engine, open]);
        install.Click += async (_, _) => await Run(async ct => { await Service().Install(manifest, (int)memory.Value, Progress(), ct); Status("Готово. Нажми «Играть». При первом запуске настрой учётную запись и Java 8 x64 в Prism."); });
        play.Click += async (_, _) => await Run(async ct => { var service = Service(); await service.Verify(manifest, ct); await service.PrepareEngine(manifest, Progress(), ct); ct.ThrowIfCancellationRequested(); Process.Start(service.StartInfo(manifest, true)); Status("Запуск передан в Prism. Загрузка Forge и библиотек отображается в его окне."); });
        cancel.Click += (_, _) => cancellation?.Cancel();
        folder.Text = File.Exists(settings) ? File.ReadAllText(settings) : Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "MinETRO-Test");
        folder.TextChanged += (_, _) => RefreshButtons();
        FormClosing += (_, e) => { if (busy) { e.Cancel = true; cancellation?.Cancel(); Status("Отмена операции перед закрытием. После завершения нажми крестик ещё раз."); } };
        PaintButtons(this); UpdateManifest();
    }
    private static void PaintButtons(Control root)
    {
        foreach (Control c in root.Controls) {
            if (c is Button b) { b.FlatStyle = FlatStyle.Flat; b.BackColor = Color.FromArgb(43, 52, 57); b.ForeColor = Color.White; b.Padding = new Padding(5); b.FlatAppearance.BorderColor = Color.FromArgb(77, 89, 94); }
            PaintButtons(c);
        }
    }
    private static TableLayoutPanel Row(Control fill, params Control[] buttons)
    {
        var row = new TableLayoutPanel { Dock = DockStyle.Top, AutoSize = true, ColumnCount = buttons.Length + 1, Margin = new Padding(0) };
        row.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100)); row.Controls.Add(fill, 0, 0);
        for (var i = 0; i < buttons.Length; i++) { row.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize)); row.Controls.Add(buttons[i], i + 1, 0); }
        return row;
    }
    private void ChooseFolder()
    {
        using var dialog = new FolderBrowserDialog { Description = "Отдельная папка MinETRO (миры в других папках не меняются)", UseDescriptionForTitle = true, SelectedPath = folder.Text };
        if (dialog.ShowDialog(this) == DialogResult.OK) folder.Text = dialog.SelectedPath;
    }
    private Installer Service()
    {
        if (string.IsNullOrWhiteSpace(folder.Text) || !Path.IsPathFullyQualified(folder.Text)) throw new IOException("Выбери полный путь к отдельной папке.");
        return new Installer(folder.Text, new Transfer(http));
    }
    private IProgress<TransferProgress> Progress() => new Progress<TransferProgress>(p => { Status(p.Message); bar.Style = p.Percent == null ? ProgressBarStyle.Marquee : ProgressBarStyle.Continuous; if (p.Percent is int n) bar.Value = Math.Clamp(n, 0, 100); });
    private void Status(string message) { state.Text = message; }
    private void UpdateManifest() { packInfo.Text = $"{manifest.Name}  ·  {manifest.Version}  ·  Minecraft {manifest.Minecraft} / Forge {manifest.Forge}"; Status(manifest.Archive == null ? manifest.Notice : "Канал подключён. Устанавливай только архивы, источнику которых доверяешь."); RefreshButtons(); }
    private void RefreshButtons()
    {
        install.Enabled = !busy && manifest.Archive != null;
        play.Enabled = !busy && manifest.Archive != null && Path.IsPathFullyQualified(folder.Text) && Directory.Exists(Path.Combine(folder.Text, "prism-data", "instances", manifest.InstanceId));
    }
    private async Task LoadManifest(CancellationToken ct)
    {
        var incoming = PackManifest.Parse(await new Transfer(http).ReadManifest(source.Text.Trim(), ct));
        if (MessageBox.Show($"Подключить «{incoming.Name}», версия {incoming.Version}?\nАрхив содержит моды — Java-код. SHA256 проверяет целостность, но не безопасность незнакомых модов.", "Доверие к источнику", MessageBoxButtons.YesNo, MessageBoxIcon.Warning) != DialogResult.Yes) return;
        await new ChannelStore(SettingsDirectory).Save(incoming, ct);
        manifest = incoming; UpdateManifest();
    }
    private async Task Run(Func<CancellationToken, Task> action)
    {
        if (busy) return; busy = true; cancellation = new();
        foreach (var c in busyControls) c.Enabled = false; cancel.Enabled = true; RefreshButtons();
        try
        {
            // Keep WinForms continuations on the UI; the installer offloads ZIP extraction.
            var ct = cancellation.Token; var task = action(ct); await task;
            Directory.CreateDirectory(Path.GetDirectoryName(settings)!); await File.WriteAllTextAsync(settings, folder.Text);
        }
        catch (OperationCanceledException) { Status("Операция отменена. Готовые сборки и миры не изменены."); }
        catch (Exception ex) { Status("Ошибка: " + ex.Message); MessageBox.Show(ex.Message, "MinETRO · операция не завершена", MessageBoxButtons.OK, MessageBoxIcon.Warning); }
        finally { cancellation?.Dispose(); cancellation = null; busy = false; cancel.Enabled = false; bar.Style = ProgressBarStyle.Continuous; foreach (var c in busyControls) c.Enabled = true; RefreshButtons(); }
    }
    public void SmokeTest(string? screenshot = null)
    {
        CreateControl();
        if (manifest.Archive == null && (install.Enabled || play.Enabled)) throw new InvalidOperationException("Unconfigured pack must never be launchable.");
        if (screenshot != null) {
            Show(); Application.DoEvents(); PerformLayout(); Refresh();
            using var bitmap = new Bitmap(Width, Height); DrawToBitmap(bitmap, new Rectangle(0, 0, Width, Height));
            bitmap.Save(screenshot, System.Drawing.Imaging.ImageFormat.Png); Hide();
        }
    }
    protected override void Dispose(bool disposing) { if (disposing) { http.Dispose(); cancellation?.Dispose(); } base.Dispose(disposing); }
}
