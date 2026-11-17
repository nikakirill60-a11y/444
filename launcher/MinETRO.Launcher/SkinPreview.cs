using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using MinETRO.Core;

namespace MinETRO.Launcher;

internal sealed class SkinPreview : Control
{
    private Bitmap? skin;
    public string Model { get; set; } = "steve";
    public SkinPreview() { DoubleBuffered = true; BackColor = Ui.Card; Size = new Size(250, 238); AccessibleName = "Двумерный предпросмотр скина спереди и сзади"; }
    public static byte[] Normalize(byte[] data)
    {
        Accounts.ValidateSkin(data);
        try
        {
            using var input = new MemoryStream(data); using var image = Image.FromStream(input, true, true);
            if (image.Width != 64 || image.Height is not (32 or 64) || image.RawFormat.Guid != ImageFormat.Png.Guid)
                throw new AccountException("Неверный формат PNG-скина.");
            using var clean = new Bitmap(image.Width, image.Height, PixelFormat.Format32bppArgb);
            using (var g = Graphics.FromImage(clean)) { g.CompositingMode = CompositingMode.SourceCopy; g.DrawImageUnscaled(image, 0, 0); }
            // Base head/body/limbs must be opaque; outer layers retain transparency.
            {
                // No artwork is invented: keep the user's RGB while normalizing base opacity.
                foreach (var r in new[] { new Rectangle(0,0,32,16), new Rectangle(0,16,64,16) }.Concat(image.Height == 64 ? new[] {new Rectangle(16,48,32,16)} : []))
                    for (int y=r.Top; y<r.Bottom; y++) for(int x=r.Left; x<r.Right; x++) { var c=clean.GetPixel(x,y); clean.SetPixel(x,y,Color.FromArgb(255,c.R,c.G,c.B)); }
            }
            using var output = new MemoryStream(); clean.Save(output, ImageFormat.Png); return output.ToArray();
        }
        catch (Exception e) when (e is ArgumentException or OutOfMemoryException or System.Runtime.InteropServices.ExternalException)
        { throw new AccountException("PNG повреждён или не поддерживается."); }
    }
    public static void SmokeTest()
    {
        foreach (int height in new[] {32,64})
        {
            using var original = new Bitmap(64,height,PixelFormat.Format32bppArgb);
            using var stream = new MemoryStream(); original.Save(stream,ImageFormat.Png);
            var bytes = Normalize(stream.ToArray()); Accounts.ValidateSkin(bytes);
            using var input = new MemoryStream(bytes); using var image = new Bitmap(input);
            if (image.GetPixel(8,8).A != 255 || image.GetPixel(40,8).A != 0)
                throw new InvalidOperationException("Skin base/overlay transparency normalization failed.");
            using var preview = new SkinPreview(); preview.SetSkin(bytes);
            foreach(var model in new[]{"steve","alex"}) {preview.Model=model; using var bitmap=new Bitmap(250,238);preview.DrawToBitmap(bitmap,new Rectangle(0,0,250,238));}
        }
        try { Normalize(new byte[33]); } catch(AccountException) {return;}
        throw new InvalidOperationException("Malformed skin must be rejected.");
    }
    public void SetSkin(byte[]? bytes)
    {
        Bitmap? next = null;
        if (bytes != null) { var clean = Normalize(bytes); using var stream = new MemoryStream(clean); using var image = Image.FromStream(stream); next = new Bitmap(image); }
        var old = skin; skin = next; old?.Dispose(); Invalidate();
    }
    protected override void OnPaint(PaintEventArgs e)
    {
        base.OnPaint(e); var g = e.Graphics; float s = DeviceDpi / 96f; Ui.Canvas(g,s); float w = Width/s, h = Height/s;
        Ui.CardBox(g,new(0,0,w-1,h-1),Ui.Card,10,Ui.Line);
        if (skin == null) { Ui.TextAt(g,"Выбери PNG-скин\n64×64 / 64×32",13,10,35,w-20,h-55,Ui.Muted,wrap:true,align:StringAlignment.Center); return; }
        g.InterpolationMode=InterpolationMode.NearestNeighbor; g.PixelOffsetMode=PixelOffsetMode.Half;
        float zoom=Math.Min(5f,(h-45)/32f); bool slim=Model=="alex" && skin.Height==64; int arm=slim?3:4;
        for(int view=0;view<2;view++)
        {
            float cx=w*(view==0?.27f:.73f), x=cx-8*zoom, y=12;
            void Part(int sx,int sy,int sw,int sh,float dx,float dy) => g.DrawImage(skin,new RectangleF(x+dx*zoom,y+dy*zoom,sw*zoom,sh*zoom),new RectangleF(sx,sy,sw,sh),GraphicsUnit.Pixel);
            bool back=view==1;
            Part(back?24:8,8,8,8,4,0); Part(back?32:20,20,8,12,4,8);
            Part(back?48+arm:44,20,arm,12,4-arm,8);
            Part(back?12:4,20,4,12,4,20);
            if(skin.Height==64) { Part(back?40+arm:36,52,arm,12,12,8); Part(back?28:20,52,4,12,8,20); }
            else { Part(back?48+arm:44,20,arm,12,12,8); Part(back?12:4,20,4,12,8,20); }
            Part(back?56:40,8,8,8,4,0);
            if(skin.Height==64)
            {
                Part(back?32:20,36,8,12,4,8); Part(back?48+arm:44,36,arm,12,4-arm,8);
                Part(back?56+arm:52,52,arm,12,12,8); Part(back?12:4,36,4,12,4,20); Part(back?12:4,52,4,12,8,20);
            }
            Ui.TextAt(g,back?"СЗАДИ":"СПЕРЕДИ",9,cx-45,h-24,90,18,Ui.Muted,align:StringAlignment.Center);
        }
    }
    protected override void Dispose(bool disposing) { if(disposing) skin?.Dispose(); base.Dispose(disposing); }
}
