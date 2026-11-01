package ru.metrocraft.port.client.blackrazid.transport;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.client.GlStateBuffers;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.blackrazid.transport.*;
import ru.metrocraft.port.mcmod.McmodObjMesh;

/** Native models/materials. Empty old switch draw loops are explicitly filled with original rail segments. */
public final class WideRailRenderer extends TileEntitySpecialRenderer<RailTile> {
    private static final Map<String,McmodObjMesh> MESHES=new HashMap<>();
    private static final Map<String,Map<String,ResourceLocation>> MATERIALS=new HashMap<>();
    public static void reload(){MESHES.clear();MATERIALS.clear();}
    private static McmodObjMesh mesh(String name){
        if(MESHES.containsKey(name))return MESHES.get(name);
        try(InputStream in=Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:models/blackrazid/rails/"+name+".obj")).getInputStream();
            Reader r=new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:blackrazid_rail_materials.json")).getInputStream(),StandardCharsets.UTF_8)){
            McmodObjMesh mesh=McmodObjMesh.readMaterials(in);JsonObject all=new JsonParser().parse(r).getAsJsonObject();Map<String,ResourceLocation> maps=new HashMap<>();
            for(Map.Entry<String,JsonElement> e:all.getAsJsonObject(name).entrySet())maps.put(e.getKey(),new ResourceLocation(e.getValue().getAsString()));
            if(!maps.keySet().containsAll(mesh.groups.keySet()))throw new IllegalStateException("Unmapped rail material "+name);
            MESHES.put(name,mesh);MATERIALS.put(name,maps);return mesh;
        }catch(Exception e){throw new IllegalStateException("Cannot read original BlackRazid rail "+name,e);}
    }
    private void draw(String name){
        McmodObjMesh mesh=mesh(name);
        for(Map.Entry<String,List<float[]>> part:mesh.groups.entrySet()){
            bindTexture(MATERIALS.get(name).get(part.getKey()));BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
            for(float[] v:part.getValue())b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();Tessellator.getInstance().draw();
        }
    }
    private void straight(){
        GlStateManager.pushMatrix();try{GlStateManager.translate(-.422,0,-.01);GlStateManager.scale(1,1,.55);draw("rails");}finally{GlStateManager.popMatrix();}
    }
    private void straightRun(int count){for(int i=0;i<count;i++){straight();GlStateManager.translate(0,0,1);}}
    private void turn(int id){
        float sign=id==3?1:-1;GlStateManager.pushMatrix();
        try{
            GlStateManager.translate(0,0,-.5);GlStateManager.translate(sign*15.5,0,0);GlStateManager.rotate(sign*90/35/2,0,1,0);GlStateManager.translate(sign*-15.5,0,0);
            if(id==4){GlStateManager.translate(-.6,-.001,-1.25);GlStateManager.scale(.98,1,1.015);}
            else{GlStateManager.translate(-.6,-.001,-1.38);GlStateManager.scale(1.015,1,.98);}
            draw("rails90");
        }finally{GlStateManager.popMatrix();}
    }
    private void composite(int id){
        float sign=id==7||id==9?1:-1;
        for(int branch=0;branch<(id==11?2:1);branch++){
            GlStateManager.pushMatrix();
            try{for(int half=0;half<2;half++){
                GlStateManager.pushMatrix();
                try{GlStateManager.translate(0,0,-.15);
                    for(int step=0;step<13;step++){
                        straight();GlStateManager.translate(sign*12.95,0,0);GlStateManager.rotate(sign*90/31,0,1,0);GlStateManager.translate(sign*-12.95,0,0);
                    }
                }finally{GlStateManager.popMatrix();}
                GlStateManager.translate(sign*5,0,15);GlStateManager.rotate(180,0,1,0);
            }}finally{GlStateManager.popMatrix();}sign*=-1;
        }
    }
    @Override public void render(RailTile tile,double x,double y,double z,float partial,int destroyStage,float alpha){
        if(!tile.primary())return;
        int texture=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D),shade=GL11.glGetInteger(GL11.GL_SHADE_MODEL);boolean normalize=GL11.glIsEnabled(GL11.GL_NORMALIZE);
        FloatBuffer color=GlStateBuffers.colorQuery();GL11.glGetFloat(GL11.GL_CURRENT_COLOR,color);
        GlStateManager.pushMatrix();
        try{
            GlStateManager.translate(x+.5,y,z+.5);GlStateManager.rotate(-90*tile.direction(),0,1,0);GlStateManager.shadeModel(GL11.GL_SMOOTH);GlStateManager.enableNormalize();GlStateManager.color(1,1,1,alpha);
            int shape=tile.shape();
            if(shape==0||shape==2)straight(); // No original buffer-stop OBJ: collision is native, marker is an explicit adaptation.
            else if(shape==1){GlStateManager.translate(-.01,0,0);draw("railsx");}
            else if(shape==3||shape==4)turn(shape);
            else if(shape==5||shape==6){
                float tint=tile.active()?1:.55F;GlStateManager.color(tint,tint,tint,alpha);turn(shape==5?3:4);
                tint=tile.active()?.55F:1;GlStateManager.color(tint,tint,tint,alpha);straightRun(11);
            }else{
                composite(shape);
                if(shape==9||shape==10){GlStateManager.pushMatrix();try{straightRun(9);}finally{GlStateManager.popMatrix();}}
            }
        }finally{
            GlStateManager.color(color.get(0),color.get(1),color.get(2),color.get(3));GlStateManager.bindTexture(texture);GlStateManager.shadeModel(shade);
            if(normalize)GlStateManager.enableNormalize();else GlStateManager.disableNormalize();GlStateManager.popMatrix();
        }
    }
}
