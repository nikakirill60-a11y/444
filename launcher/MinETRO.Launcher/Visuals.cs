using System.Drawing.Drawing2D;
using System.Drawing.Text;

namespace MinETRO.Launcher;
internal static class Ui
{
    public static readonly Color Bg = Color.FromArgb(13, 17, 22), Side = Color.FromArgb(17, 22, 28), Card = Color.FromArgb(22, 29, 36), Line = Color.FromArgb(39, 48, 57), Text = Color.FromArgb(235, 239, 242), Muted = Color.FromArgb(139, 152, 164), Amber = Color.FromArgb(232, 173, 91);
    public static GraphicsPath Round(RectangleF r, float radius = 12)
    {
        float d = radius * 2; var p = new GraphicsPath();
        p.AddArc(r.X, r.Y, d, d, 180, 90); p.AddArc(r.Right - d, r.Y, d, d, 270, 90);
        p.AddArc(r.Right - d, r.Bottom - d, d, d, 0, 90); p.AddArc(r.X, r.Bottom - d, d, d, 90, 90); p.CloseFigure(); return p;
    }
    public static void CardBox(Graphics g, RectangleF r, Color color, float radius = 12, Color? border = null)
    {
        using var path = Round(r, radius); using var b = new SolidBrush(color); g.FillPath(b, path);
        if (border is Color c) { using var p = new Pen(c); g.DrawPath(p, path); }
    }
    public static void TextAt(Graphics g, string text, float size, float x, float y, float w, float h, Color color, bool bold = false, bool wrap = false, StringAlignment align = StringAlignment.Near)
    {
        using var f = new Font("Segoe UI", size, bold ? FontStyle.Bold : FontStyle.Regular, GraphicsUnit.Pixel);
        using var b = new SolidBrush(color); using var format = new StringFormat { Alignment = align, LineAlignment = StringAlignment.Center, Trimming = StringTrimming.EllipsisCharacter };
        if (!wrap) format.FormatFlags = StringFormatFlags.NoWrap;
        g.DrawString(text, f, b, new RectangleF(x, y, w, h), format);
    }
    public static void Icon(Graphics g, string kind, float x, float y, float size, Color color)
    {
        var state = g.Save(); g.TranslateTransform(x, y); g.ScaleTransform(size / 24, size / 24);
        using var p = new Pen(color, 1.6f) { StartCap = LineCap.Round, EndCap = LineCap.Round, LineJoin = LineJoin.Round };
        switch (kind)
        {
            case "play": using (var b = new SolidBrush(color)) g.FillPolygon(b, (PointF[])[new(8, 5), new(19, 12), new(8, 19)]); break;
            case "settings": g.DrawEllipse(p, 7, 7, 10, 10); g.DrawEllipse(p, 10, 10, 4, 4); for (int n = 0; n < 8; n++) { var s = g.Save(); g.TranslateTransform(12, 12); g.RotateTransform(n * 45); g.DrawLine(p, 0, -8, 0, -10); g.Restore(s); } break;
            case "info": g.DrawEllipse(p, 3, 3, 18, 18); g.DrawLine(p, 12, 11, 12, 17); g.DrawEllipse(p, 11.5f, 6.5f, 1, 1); break;
            case "folder": g.DrawLines(p, (PointF[])[new(3, 7), new(3, 19), new(21, 19), new(21, 8), new(11, 8), new(9, 5), new(3, 5), new(3, 7)]); break;
            case "shield": g.DrawPolygon(p, (PointF[])[new(12, 2), new(21, 6), new(19, 16), new(12, 22), new(5, 16), new(3, 6)]); g.DrawLines(p, (PointF[])[new(8, 12), new(11, 15), new(16, 9)]); break;
            case "key": g.DrawEllipse(p, 3, 3, 10, 10); g.DrawLine(p, 12, 12, 21, 21); g.DrawLine(p, 17, 17, 20, 14); g.DrawLine(p, 19, 19, 22, 16); break;
            case "arrow": g.DrawLines(p, (PointF[])[new(9, 6), new(15, 12), new(9, 18)]); break;
            case "close": g.DrawLine(p, 7, 7, 17, 17); g.DrawLine(p, 17, 7, 7, 17); break;
            case "min": g.DrawLine(p, 6, 15, 18, 15); break;
            case "metro": p.Width = 2.5f; g.DrawLines(p, (PointF[])[new(3, 19), new(7, 5), new(12, 15), new(17, 5), new(21, 19)]); break;
        }
        g.Restore(state);
    }
    public static void Canvas(Graphics g, float scale)
    {
        g.SmoothingMode = SmoothingMode.AntiAlias; g.TextRenderingHint = TextRenderingHint.AntiAliasGridFit; g.ScaleTransform(scale, scale);
    }
}
internal sealed class Sidebar : Panel
{
    private readonly Image logo = Image.FromFile(Path.Combine(AppContext.BaseDirectory,"Assets","minetro.png"));
    public Sidebar() { DoubleBuffered = true; BackColor = Ui.Side; }
    protected override void Dispose(bool disposing) {if(disposing)logo.Dispose();base.Dispose(disposing);}
    protected override void OnPaint(PaintEventArgs e)
    {
        base.OnPaint(e); float s = DeviceDpi / 96f; var g = e.Graphics; Ui.Canvas(g, s); float h = Height / s;
        g.DrawImage(logo,new RectangleF(24,26,36,36));
        Ui.TextAt(g, "MinETRO", 22, 71, 24, 130, 33, Ui.Text, true);
        Ui.TextAt(g, "ЛАУНЧЕР", 9, 73, 57, 100, 15, Ui.Muted);
        using var line = new Pen(Ui.Line); g.DrawLine(line, 24, 100, 192, 100);
        Ui.TextAt(g, "БИБЛИОТЕКА", 10, 25, 119, 150, 20, Ui.Muted, true);
        g.DrawLine(line, 24, h - 56, 192, h - 56);
        using var dot = new SolidBrush(Ui.Amber); g.FillEllipse(dot, 26, h - 32, 5, 5);
        Ui.TextAt(g, "MINETRO", 10, 39, h - 39, 110, 19, Ui.Muted, true);
        Ui.TextAt(g, "x64", 10, 157, h - 39, 34, 19, Ui.Muted, align: StringAlignment.Far);
    }
}
internal sealed class Surface : Panel
{
    public Surface() { DoubleBuffered = true; BackColor = Ui.Bg; }
    protected override void OnPaint(PaintEventArgs e) { base.OnPaint(e); Ui.Canvas(e.Graphics, DeviceDpi / 96f); Ui.CardBox(e.Graphics, new(0, 0, Width * 96f / DeviceDpi - 1, Height * 96f / DeviceDpi - 1), Ui.Card, 12, Ui.Line); }
}
internal sealed class ThinProgress : Control
{
    public int? Percent { get; set; }
    public bool Active { get; set; }
    private int tick;
    private readonly System.Windows.Forms.Timer timer = new() { Interval = 45 };
    public ThinProgress() { DoubleBuffered = true; BackColor = Ui.Bg; timer.Tick += (_, _) => { if (Active && Percent == null) { tick = (tick + 4) % 140; Invalidate(); } }; timer.Start(); }
    protected override void OnPaint(PaintEventArgs e)
    {
        var g = e.Graphics; using var track = new SolidBrush(Ui.Line); g.FillRectangle(track, ClientRectangle);
        if (!Active && Percent == null) return;
        using var fill = new SolidBrush(Ui.Amber);
        if (Percent is int n) g.FillRectangle(fill, 0, 0, Width * Math.Clamp(n, 0, 100) / 100f, Height);
        else g.FillRectangle(fill, Width * (tick - 30) / 100f, 0, Width * .3f, Height);
    }
    protected override void Dispose(bool disposing) { if (disposing) timer.Dispose(); base.Dispose(disposing); }
}
internal sealed class HomeCanvas : Panel
{
    private readonly Image art;
    public string PackName { get; set; } = "MinETRO";
    public string VersionName { get; set; } = "ожидается релиз";
    public bool HasArchive { get; set; }
    public bool Installed { get; set; }
    public float Fit => Math.Min(1f, Height * 96f / DeviceDpi / 584f);
    public HomeCanvas() { DoubleBuffered = true; BackColor = Ui.Bg; art = Image.FromFile(Path.Combine(AppContext.BaseDirectory, "Assets", "metro-station.jpg")); }
    protected override void OnPaint(PaintEventArgs e)
    {
        base.OnPaint(e); var g = e.Graphics; float s = DeviceDpi / 96f; Ui.Canvas(g, s); g.ScaleTransform(Fit, Fit); float w = Width / s / Fit;
        var hero = new RectangleF(0, 0, w, 310); using (var clip = Ui.Round(hero, 14))
        {
            var save = g.Save(); g.SetClip(clip);
            float zoom = Math.Max(w / art.Width, hero.Height / art.Height); float iw = art.Width * zoom, ih = art.Height * zoom;
            g.DrawImage(art, new RectangleF((w - iw) / 2, (310 - ih) / 2, iw, ih));
            using var shade = new LinearGradientBrush(hero, Color.FromArgb(195, 7, 12, 16), Color.FromArgb(5, 7, 12, 16), 0f);
            g.FillRectangle(shade, hero);
            using var bottom = new LinearGradientBrush(hero, Color.Transparent, Color.FromArgb(185, 7, 12, 16), 90f);
            g.FillRectangle(bottom, hero); g.Restore(save);
        }
        Ui.CardBox(g, new(28, 25, 157, 25), Color.FromArgb(185, 23, 28, 31), 6);
        Ui.TextAt(g, "МИР ПОД ПОВЕРХНОСТЬЮ", 9, 39, 25, 141, 25, Ui.Amber, true);
        Ui.TextAt(g, "Спустись в метро.", 37, 27, 81, w - 50, 53, Ui.Text, true);
        Ui.TextAt(g, "Создай свою историю.", 32, 27, 128, w - 50, 50, Ui.Text, true);
        Ui.TextAt(g, "Станции, гермоворота и жизнь под землёй.\nТвой мир начинается здесь.", 13, 30, 185, 420, 48, Color.FromArgb(191, 201, 208), wrap: true);
        Ui.TextAt(g, "MINECRAFT 1.12.2     /     MINETRO PROJECT", 10, 30, 267, 500, 20, Color.FromArgb(180, 192, 200), true);
        Ui.CardBox(g, new(0, 326, w, 132), Ui.Card, 12, Ui.Line);
        Ui.TextAt(g, "ВЫБРАННАЯ СБОРКА", 9, 22, 342, 290, 16, Ui.Muted, true);
        Ui.TextAt(g, "MinETRO", 25, 21, 364, 145, 33, Ui.Text, true);

        Ui.TextAt(g, "Forge 1.12.2  ·  Java 8 x64  ·  " + VersionName, 11, 23, 404, w - 295, 19, Ui.Muted);
        Ui.TextAt(g, HasArchive ? (Installed ? "Файлы установлены · готово к проверке и запуску" : "Релиз доступен · можно установить сборку") : "Ожидается полная сборка в GitHub Releases", 10, 23, 429, w - 295, 17, HasArchive ? Ui.Muted : Ui.Amber);
        Ui.TextAt(g, HasArchive ? (Installed ? "ТВОЙ ЭКЗЕМПЛЯР" : "НОВАЯ УСТАНОВКА") : "GITHUB RELEASES", 9, w - 252, 347, 228, 18, Ui.Muted, true, align: StringAlignment.Center);
        float cw = (w - 28) / 3;
        Feature(g, 0, cw, "shield", "Проверенные файлы", "SHA256 перед установкой");
        Feature(g, cw + 14, cw, "folder", "Миры в сохранности", "Каждая версия отдельно");
        Feature(g, (cw + 14) * 2, cw, "key", "Безопасный вход", "Твой аккаунт MinETRO");
    }
    private static void Feature(Graphics g, float x, float w, string icon, string title, string note)
    {
        Ui.CardBox(g, new(x, 476, w, 88), Ui.Card, 10);
        Ui.CardBox(g, new(x + 15, 496, 34, 34), Color.FromArgb(31, 39, 46), 9);
        Ui.Icon(g, icon, x + 23, 504, 18, Ui.Amber);
        Ui.TextAt(g, title, 12, x + 60, 494, w - 71, 22, Ui.Text, true);
        Ui.TextAt(g, note, 10, x + 60, 519, w - 71, 25, Ui.Muted, wrap: true);
    }
    protected override void Dispose(bool disposing) { if (disposing) art.Dispose(); base.Dispose(disposing); }
}

internal sealed class AuthArtwork : Panel
{
    private readonly Image art=Image.FromFile(Path.Combine(AppContext.BaseDirectory,"Assets","metro-station.jpg"));
    private readonly Image logo=Image.FromFile(Path.Combine(AppContext.BaseDirectory,"Assets","minetro.png"));
    public AuthArtwork(){DoubleBuffered=true;BackColor=Ui.Bg;}
    protected override void OnPaint(PaintEventArgs e)
    {
        base.OnPaint(e);var g=e.Graphics;float s=DeviceDpi/96f;Ui.Canvas(g,s);float w=Width/s,h=Height/s;
        var r=new RectangleF(0,0,w,h);float zoom=Math.Max(w/art.Width,h/art.Height);
        g.DrawImage(art,new RectangleF((w-art.Width*zoom)*.65f,0,art.Width*zoom,art.Height*zoom));
        using var shade=new LinearGradientBrush(r,Color.FromArgb(65,7,12,16),Color.FromArgb(245,7,12,16),90f);g.FillRectangle(shade,r);
        g.DrawImage(logo,new RectangleF(32,34,64,64));Ui.TextAt(g,"MinETRO",30,32,106,w-64,46,Ui.Text,true);
        Ui.TextAt(g,"ТВОЙ МИР ПОД ПОВЕРХНОСТЬЮ",10,34,155,w-64,25,Ui.Amber,true);
        Ui.TextAt(g,"Следующая\nстанция —\nтвоя история.",32,30,h-280,w-60,145,Ui.Text,true,wrap:true);
        Ui.TextAt(g,"Один аккаунт. Твой ник. Твой скин.",12,34,h-104,w-64,44,Ui.Muted,wrap:true);
        Ui.TextAt(g,"MINECRAFT 1.12.2  /  MINETRO",9,34,h-40,w-64,22,Ui.Muted,true);
    }
    protected override void Dispose(bool disposing){if(disposing){art.Dispose();logo.Dispose();}base.Dispose(disposing);}
}
