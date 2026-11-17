namespace MinETRO.Launcher;

/** Keep disabled captions legible on the dark background (native Flat buttons
 * otherwise draw near-black disabled text on some Windows themes). */
internal sealed class LauncherButton : Button
{
    protected override void OnPaint(PaintEventArgs e)
    {
        e.Graphics.Clear(Enabled ? BackColor : Color.FromArgb(35, 42, 46));
        using var border = new Pen(Focused && Enabled ? Color.FromArgb(219, 169, 89) : Color.FromArgb(77, 89, 94));
        e.Graphics.DrawRectangle(border, 0, 0, Math.Max(0, Width - 1), Math.Max(0, Height - 1));
        TextRenderer.DrawText(e.Graphics, Text, Font, ClientRectangle, Enabled ? ForeColor : Color.FromArgb(153, 163, 168),
            TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter | TextFormatFlags.SingleLine | TextFormatFlags.EndEllipsis);
        if (Focused && ShowFocusCues) ControlPaint.DrawFocusRectangle(e.Graphics, Rectangle.Inflate(ClientRectangle, -4, -4));
    }
}
