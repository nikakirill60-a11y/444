package ru.metrocraft.port.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.mcmod.*;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

/** All fifteen original gun meshes. Source arms/watch never become part of a held gun. */
public final class McmodAkRenderer extends TileEntityItemStackRenderer {
    public static final McmodAkRenderer INSTANCE=new McmodAkRenderer();
    private static final Map<String,Map<String,Integer>> LISTS=new HashMap<>();
    public static String profile(String id){return id.equals("mcmod_rev")||id.equals("mcmod_ashot")?"revolver":"ak74";}
    public static void reload(){for(Map<String,Integer> gun:LISTS.values())for(int list:gun.values())GLAllocation.deleteDisplayLists(list);LISTS.clear();}
    private static Map<String,Integer> mesh(String name){
        Map<String,Integer> cached=LISTS.get(name);if(cached!=null)return cached;
        Map<String,Integer> result=new HashMap<>();
        String path="models/mcmod/guns/"+name+".obj"+(name.equals("ak74")?"":".gz");
        try(InputStream raw=Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:"+path)).getInputStream();
            InputStream in=name.equals("ak74")?raw:new GZIPInputStream(raw)){
            McmodObjMesh mesh=McmodObjMesh.read(in);
            for(Map.Entry<String,List<float[]>> part:mesh.groups.entrySet()){
                String key=part.getKey();
                if(key.startsWith("arm_")||key.startsWith("perch_")||key.startsWith("num")||key.equals("clock"))continue;
                int list=GLAllocation.generateDisplayLists(1);result.put(key,list);GlStateManager.glNewList(list,GL11.GL_COMPILE);
                BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
                for(float[] v:part.getValue())b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
                Tessellator.getInstance().draw();GlStateManager.glEndList();
            }
        }catch(Exception e){for(int list:result.values())GLAllocation.deleteDisplayLists(list);throw new IllegalStateException("Cannot read MCMOD gun "+name,e);}
        LISTS.put(name,result);return result; // Do not retain the expanded OBJ/Java vertex arrays.
    }
    private static void group(Map<String,Integer> lists,String... names){
        GlStateManager.pushMatrix();
        try{
            GlStateManager.translate(McmodAkPose.X,McmodAkPose.Y,McmodAkPose.Z);GlStateManager.rotate(-90,0,1,0);
            GlStateManager.scale(McmodAkPose.SCALE,McmodAkPose.SCALE,McmodAkPose.SCALE);
            for(String name:names){Integer list=lists.get(name);if(list!=null)GlStateManager.callList(list);}
        }finally{GlStateManager.popMatrix();}
    }
    private static void texture(String name){Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("metroport:textures/mcmod/guns/"+name+".png"));}
    @Override public void renderByItem(ItemStack stack,float partial){
        if(!(stack.getItem() instanceof McmodAkItem))return;
        McmodAkItem item=(McmodAkItem)stack.getItem();String name=item.getDefinition().id.substring(6);
        Map<String,Integer> parts=mesh(name);Minecraft mc=Minecraft.getMinecraft();TransformType type=LegacyItemModel.context();
        boolean first=type==TransformType.FIRST_PERSON_LEFT_HAND||type==TransformType.FIRST_PERSON_RIGHT_HAND;
        double now=mc.world==null?0:mc.world.getTotalWorldTime()+partial;float kick=0,reload=0;
        if(stack.hasTagCompound()){
            double since=now-stack.getTagCompound().getLong("MetroLastShot");
            if(stack.getTagCompound().hasKey("MetroLastShot")&&since>=0&&since<4)kick=(float)(1-since/4);
            int duration=stack.getTagCompound().getInteger("MetroReloadDuration");
            if(duration>0&&item.reloading(stack)){
                float p=(float)((now-stack.getTagCompound().getLong("MetroReloadStart"))/duration);
                reload=Math.max(0,Math.min(1,Math.min(p/.25F,(1-p)/.25F)));
            }
        }
        GlStateManager.pushMatrix();GlStateManager.disableCull();GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();GlStateManager.blendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        try{
            GlStateManager.translate(.5-.035*kick,.5,.5);GlStateManager.color(1,1,1,1);
            if(name.equals("railgun")){
                texture("railgun");group(parts,"body2","zatv","ammo");texture("railgun_body2");group(parts,"body1");
                texture("railgun_volt");group(parts,"volt");texture("mods_p");group(parts,"pric");texture("railgun_gen");group(parts,"gen");
            }else{
                texture(name+(first?"":"_low"));group(parts,"body","handa");
                GlStateManager.pushMatrix();try{
                    GlStateManager.translate(0,-.14*reload,0);GlStateManager.rotate(-12*reload,0,0,1);
                    if(name.equals("rpk"))texture("ak74"+(first?"":"_low"));
                    group(parts,McmodAkItem.module(stack,"modMag")==1&&parts.containsKey("mag2")?"mag2":"mag");
                    texture(name+(first?"":"_low"));
                    if(!item.reloading(stack))for(int n=1;n<=item.ammo(stack);n++)group(parts,"ammo"+n);
                    if(item.ammo(stack)>0)group(parts,"revolver_bullet");
                }finally{GlStateManager.popMatrix();}
                GlStateManager.pushMatrix();try{GlStateManager.translate(-.045*kick,0,0);group(parts,"zatv");}finally{GlStateManager.popMatrix();}
            }
            int scope=McmodAkItem.module(stack,"modScope");
            if(scope==1){texture("mods");group(parts,"scope_a","scope_b","scope_g2");texture("mods_p");group(parts,"scope_g1");}
            if(scope==2){texture("mods_c");group(parts,"col");}
            if(scope==3){texture("mods_n");group(parts,"scope2");}
            texture("mods_s");if(McmodAkItem.module(stack,"modStvol")==1)group(parts,"slicer");
            if(McmodAkItem.module(stack,"modRessiver")==1)group(parts,"laser","laser1");
            if(item.getDefinition().mcmodAir){texture(name);group(parts,"strelka"+McmodAkItem.gauge(McmodAkItem.pressure(stack)));}
            if(first){
                boolean pistol=name.equals("rev")||name.equals("ashot");
                float[] trigger=name.equals("ak74")?McmodAkPose.handAnchor(false):new float[]{-.07F,.14F,-.175F};
                float[] support=pistol?new float[]{-.01F,.12F,-.175F}:McmodAkPose.handAnchor(true);
                float hands=reload;
                if(item.getDefinition().mcmodAir&&stack.hasTagCompound()&&stack.getTagCompound().hasKey("MetroLastPump")){
                    double since=now-stack.getTagCompound().getLong("MetroLastPump");
                    if(since>=0&&since<8)hands=Math.max(hands,(float)Math.sin(since/8*Math.PI)*.6F);
                }
                GunHands.render(profile(item.getDefinition().id),type,hands,trigger,support);
                if(kick>.5F&&McmodAkItem.module(stack,"modStvol")==0){
                    texture("ak74_flash");GlStateManager.disableLighting();try{group(parts,"fire1");}finally{GlStateManager.enableLighting();}
                }
            }
        }finally{GlStateManager.color(1,1,1,1);GlStateManager.disableBlend();GlStateManager.enableCull();GlStateManager.disableRescaleNormal();GlStateManager.popMatrix();}
    }
}
