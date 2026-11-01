package ru.metrocraft.port.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.blackrazid.survival.*;
import ru.metrocraft.port.mcmod.McmodObjMesh;

/** Original survival meshes. No firearm classes, state, camera transforms or gun atlas dependencies. */
public final class BlackrazidGadgetRenderer extends TileEntityItemStackRenderer {
    public static final BlackrazidGadgetRenderer INSTANCE=new BlackrazidGadgetRenderer();
    private static final class Mesh { Map<String,String> materials;float[] bounds,center; }
    private static Map<String,Mesh> meshes;
    private static final Map<String,Map<String,Integer>> LISTS=new HashMap<>();
    private static Mesh mesh(String name){
        if(meshes==null)try(Reader r=new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:blackrazid_gadget_meshes.json")).getInputStream(),StandardCharsets.UTF_8)){
            meshes=new Gson().fromJson(r,new TypeToken<Map<String,Mesh>>(){}.getType());
        }catch(IOException e){throw new IllegalStateException("Cannot read survival mesh catalogue",e);}
        return meshes.get(name);
    }
    public static void reload(){for(Map<String,Integer> groups:LISTS.values())for(int id:groups.values())GLAllocation.deleteDisplayLists(id);LISTS.clear();meshes=null;}
    private static void draw(String name){
        Map<String,Integer> lists=LISTS.get(name);
        if(lists==null){
            lists=new LinkedHashMap<>();
            try(InputStream raw=Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:models/blackrazid/gadgets/"+name+".obj.gz")).getInputStream();InputStream in=new GZIPInputStream(raw)){
                McmodObjMesh obj=McmodObjMesh.readMaterials(in);
                for(Map.Entry<String,List<float[]>> group:obj.groups.entrySet()){
                    if(!mesh(name).materials.containsKey(group.getKey()))throw new IOException("Unmapped original gadget material "+group.getKey());
                    int id=GLAllocation.generateDisplayLists(1);lists.put(group.getKey(),id);GlStateManager.glNewList(id,GL11.GL_COMPILE);
                    try{BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
                        for(float[] v:group.getValue())b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();Tessellator.getInstance().draw();
                    }finally{GlStateManager.glEndList();}
                }
            }catch(Exception e){for(int id:lists.values())GLAllocation.deleteDisplayLists(id);throw new IllegalStateException("Cannot load original gadget "+name,e);}
            LISTS.put(name,lists);
        }
        for(Map.Entry<String,Integer> group:lists.entrySet()){
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(mesh(name).materials.get(group.getKey())));GlStateManager.callList(group.getValue());
        }
    }
    private static void rotating(String name,float angle){
        float[] pivot=mesh(name).center;GlStateManager.pushMatrix();
        try{GlStateManager.translate(pivot[0],pivot[1],pivot[2]);GlStateManager.rotate(angle,0,0,1);GlStateManager.translate(-pivot[0],-pivot[1],-pivot[2]);draw(name);}
        finally{GlStateManager.popMatrix();}
    }
    @Override public void renderByItem(ItemStack stack,float partial){
        if(!(stack.getItem() instanceof SurvivalItem))return;
        SurvivalItem.Type type=((SurvivalItem)stack.getItem()).type;
        String name=type==SurvivalItem.Type.LAMP?"flashlight":type==SurvivalItem.Type.FLASK?"flyashka":"dynamo";
        Mesh m=mesh(name);float extent=Math.max(m.bounds[3]-m.bounds[0],Math.max(m.bounds[4]-m.bounds[1],m.bounds[5]-m.bounds[2]));
        float size=type==SurvivalItem.Type.LAMP?.48F:type==SurvivalItem.Type.FLASK?.32F:.40F;
        int texture=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        boolean cull=GL11.glIsEnabled(GL11.GL_CULL_FACE),normal=GL11.glIsEnabled(GL11.GL_NORMALIZE);
        FloatBuffer color=GlStateBuffers.colorQuery();GL11.glGetFloat(GL11.GL_CURRENT_COLOR,color);
        GlStateManager.pushMatrix();
        try{
            GlStateManager.translate(.5,.5,.5);GlStateManager.color(1,1,1,1);GlStateManager.disableCull();GlStateManager.enableNormalize();
            GlStateManager.scale(size/extent,size/extent,size/extent);GlStateManager.translate(-m.center[0],-m.center[1],-m.center[2]);draw(name);
            if(type==SurvivalItem.Type.POWER){
                Minecraft mc=Minecraft.getMinecraft();boolean active=mc.player!=null && mc.player.isHandActive() && mc.player.getActiveItemStack()==stack;
                float elapsed=active?72000-mc.player.getItemInUseCount()+partial:0;
                rotating("meh",elapsed*24);
                rotating("strelka",mc.player==null?0:BlackrazidSurvivalClient.energyPercent()*2.4F);
            }
        }finally{
            GlStateManager.popMatrix();GlStateManager.bindTexture(texture);GlStateManager.color(color.get(0),color.get(1),color.get(2),color.get(3));
            if(cull)GlStateManager.enableCull();else GlStateManager.disableCull();if(normal)GlStateManager.enableNormalize();else GlStateManager.disableNormalize();
        }
    }
}
