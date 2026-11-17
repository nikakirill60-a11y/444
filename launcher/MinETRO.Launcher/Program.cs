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
            using var form = new MainForm(manifest);
            if (args.Contains("--self-test")) { form.SmokeTest(args.Length > 1 ? args[1] : null); return 0; }
            Application.Run(form); return 0;
        }
        catch (Exception ex) { if (!args.Contains("--self-test")) MessageBox.Show(ex.Message, "MinETRO · запуск", MessageBoxButtons.OK, MessageBoxIcon.Error); return 1; }
    }
}
