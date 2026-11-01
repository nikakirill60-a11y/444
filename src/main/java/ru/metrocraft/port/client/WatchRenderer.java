package ru.metrocraft.port.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.mcmod.*;
import java.io.InputStream;
import java.util.*;
/** Reuses the exact clock and four digit meshes/atlases from the original weapon arm. */
public final class WatchRenderer extends TileEntityItemStackRenderer {
    public static final WatchRenderer INSTANCE=new WatchRenderer();
    private static final int GL12_RESCALE_NORMAL=0x803A;
    private static McmodObjMesh mesh;
    private static final Map<String,Integer> LISTS=new HashMap<>();
    private static void group(String name){
        if(mesh==null)try(InputStream in=Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:models/mcmod/watch.obj")).getInputStream()){
            mesh=McmodObjMesh.read(in);
        }catch(Exception e){throw new IllegalStateException("Cannot load original wristwatch",e);}
        int list=LISTS.computeIfAbsent(name,key->{
            List<float[]> vertices=mesh.groups.get(key);if(vertices==null)throw new IllegalStateException("Missing watch group "+key);
            int id=GLAllocation.generateDisplayLists(1);GlStateManager.glNewList(id,GL11.GL_COMPILE);
            BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
            for(float[] v:vertices)b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
            Tessellator.getInstance().draw();GlStateManager.glEndList();return id;
        });
        GlStateManager.callList(list);
    }
    private static void texture(String file){Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("metroport:textures/mcmod/support/"+file+".png"));}
    private static void draw(long time,int radiation){
        boolean cull=GL11.glIsEnabled(GL11.GL_CULL_FACE),blend=GL11.glIsEnabled(GL11.GL_BLEND),normal=GL11.glIsEnabled(GL12_RESCALE_NORMAL);
        GlStateManager.pushMatrix();GlStateManager.disableCull();GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();GlStateManager.blendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        try{
            GlStateManager.color(1,1,1,1);
            GlStateManager.pushMatrix();
            try {
            // Original clock is rotated diagonally on an arm; put its face in a readable XY plane.
            GlStateManager.rotate(-48,0,0,1);GlStateManager.rotate(56,1,0,0);
            GlStateManager.scale(.0009,.0009,.0009);GlStateManager.translate(184.4965,249.688,116.4095);
            texture("clock-cr");group("clock");
            int minutes=WatchItem.minutes(time);int[] digits={minutes/600,minutes/60%10,minutes%60/10,minutes%10};
            for(int i=0;i<4;i++){texture(Integer.toString(digits[i]));group("num"+(i+1));}
            } finally {GlStateManager.popMatrix();}
            // A separate dosimeter line under the four original time digits; no invented Sv units.
            boolean lighting=GL11.glIsEnabled(GL11.GL_LIGHTING);
            GlStateManager.disableLighting();GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(-.0375,-.048,.006);GlStateManager.scale(.0025,-.0025,.0025);
                Minecraft.getMinecraft().fontRenderer.drawString(radiation>0?"RAD !":"RAD 0",0,0,radiation>0?0xFF5544:0x88DD77);
            } finally {GlStateManager.popMatrix();if(lighting)GlStateManager.enableLighting();}
        }finally{GlStateManager.color(1,1,1,1);if(blend)GlStateManager.enableBlend();else GlStateManager.disableBlend();if(cull)GlStateManager.enableCull();else GlStateManager.disableCull();if(normal)GlStateManager.enableRescaleNormal();else GlStateManager.disableRescaleNormal();GlStateManager.popMatrix();}
    }
    public static void onArm(boolean slim,long time,int radiation){
        GlStateManager.pushMatrix();
        try{GlStateManager.translate((slim?.5:1)/16,.39,-.137);GlStateManager.rotate(180,0,1,0);draw(time,radiation);}
        finally{GlStateManager.popMatrix();}
    }
    @Override public void renderByItem(ItemStack stack,float partial){
        GlStateManager.pushMatrix();
        try{GlStateManager.translate(.5,.5,.5);GlStateManager.scale(2,2,2);draw(Minecraft.getMinecraft().world==null?0:Minecraft.getMinecraft().world.getWorldTime(),Minecraft.getMinecraft().player==null?0:ru.metrocraft.port.inventory.WatchEquipment.reading(Minecraft.getMinecraft().player));}
        finally{GlStateManager.popMatrix();}
    }
}
