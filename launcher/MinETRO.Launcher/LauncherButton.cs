namespace MinETRO.Launcher;
internal sealed class LauncherButton : Button
{
    public bool Accent { get; set; }
    public bool Selected { get; set; }
    public bool Quiet { get; set; }
    public string Glyph { get; set; } = "";
    public bool Nav { get; set; }
    private bool hover;
    public LauncherButton()
    {
        FlatStyle = FlatStyle.Flat; FlatAppearance.BorderSize = 0; Cursor = Cursors.Hand;
        SetStyle(ControlStyles.OptimizedDoubleBuffer | ControlStyles.AllPaintingInWmPaint | ControlStyles.UserPaint, true);
    }
    protected override void OnMouseEnter(EventArgs e) { hover = true; Invalidate(); base.OnMouseEnter(e); }
    protected override void OnMouseLeave(EventArgs e) { hover = false; Invalidate(); base.OnMouseLeave(e); }
    protected override void OnEnabledChanged(EventArgs e) { Invalidate(); base.OnEnabledChanged(e); }
    protected override void OnPaint(PaintEventArgs e)
    {
        var g = e.Graphics; float s = DeviceDpi / 96f; Ui.Canvas(g, s); float w = Width / s, h = Height / s;
        using (var bg = new SolidBrush(Parent?.BackColor ?? Ui.Bg)) g.FillRectangle(bg, 0, 0, w, h);
        var fill = Accent && Enabled ? (hover ? Color.FromArgb(246, 190, 111) : Ui.Amber) : Selected ? Color.FromArgb(44, 37, 29) : hover && Enabled ? Color.FromArgb(37, 46, 55) : Quiet ? (Parent?.BackColor ?? Ui.Bg) : Ui.Card;
        var text = !Enabled ? Color.FromArgb(123, 137, 149) : Accent ? Color.FromArgb(31, 25, 18) : Selected ? Ui.Amber : Ui.Text;
        Ui.CardBox(g, new(0, 0, w - 1, h - 1), fill, 8, Accent || Quiet || Selected ? null : Ui.Line);
        if (Nav)
        {
            if (Selected) { using var strip = new SolidBrush(Ui.Amber); g.FillRectangle(strip, 0, 12, 2, h - 24); }
            Ui.Icon(g, Glyph, 17, (h - 20) / 2, 20, Selected ? Ui.Amber : text);
            Ui.TextAt(g, Text, 13, 49, 0, w - 61, h, text, Selected);
        }
        else
        {
            if (Glyph.Length > 0) Ui.Icon(g, Glyph, Text.Length == 0 ? (w - 20) / 2 : 16, (h - 20) / 2, 20, text);
            Ui.TextAt(g, Text, Accent ? 14 : 12, Glyph.Length > 0 && Text.Length > 0 ? 43 : 6, 0, w - (Glyph.Length > 0 && Text.Length > 0 ? 49 : 12), h, text, Accent, align: StringAlignment.Center);
        }
        if (Focused && ShowFocusCues) { using var pen = new Pen(Ui.Amber); g.DrawRectangle(pen, 4, 4, w - 9, h - 9); }
    }
}
