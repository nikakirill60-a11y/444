using MinETRO.Core;
namespace MinETRO.Launcher;
internal static class Program
{
    [STAThread]
    static int Main(string[] args)
    {
        ApplicationConfiguration.Initialize();
        try
        {
            var manifest = PackManifest.Parse(File.ReadAllText(Path.Combine(AppContext.BaseDirectory, "channel.test.json")));
            if (!args.Contains("--self-test")) {
                try { manifest = new ChannelStore(MainForm.SettingsDirectory).Load(manifest); }
                catch (Exception) { MessageBox.Show("Сохранённый канал повреждён. Подключи доверенный JSON заново. Установленные сборки и миры не изменены.", "MinETRO · канал"); }
            }
            using var form = new MainForm(manifest);
            if (args.Contains("--self-test")) { form.SmokeTest(args.Length > 1 ? args[1] : null); return 0; }
            Application.Run(form); return 0;
        }
        catch (Exception ex) { if (!args.Contains("--self-test")) MessageBox.Show(ex.Message, "MinETRO · запуск", MessageBoxButtons.OK, MessageBoxIcon.Error); return 1; }
    }
}
