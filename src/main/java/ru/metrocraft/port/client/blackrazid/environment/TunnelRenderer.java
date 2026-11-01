package ru.metrocraft.port.client.blackrazid.environment;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.blackrazid.environment.*;
import ru.metrocraft.port.client.BlackrazidModel;
import ru.metrocraft.port.mcmod.McmodObjMesh;
/** All 19 original render paths: exact native ordered transforms, material bindings and double-sided groups. */
public final class TunnelRenderer extends TileEntitySpecialRenderer<TunnelTile> {
    private static List<ModelRenderer> boxes;
    private static final class Mesh {McmodObjMesh geometry;Map<String,ResourceLocation> materials=new HashMap<>();}
    private static final Map<String,Mesh> MESHES=new HashMap<>();
    public static void reload(){boxes=null;MESHES.clear();}
    private static Reader reader(String path)throws IOException{return new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:"+path)).getInputStream(),StandardCharsets.UTF_8);}
    private static void loadBoxes(){
        if(boxes!=null)return;
        try(Reader r=reader("geometry/blackrazid/tunnel.json")){
            BlackrazidModel.Data data=new Gson().fromJson(r,BlackrazidModel.Data.class);ModelBase model=new ModelBase(){};model.textureWidth=data.textureSize[0];model.textureHeight=data.textureSize[1];List<ModelRenderer> result=new ArrayList<>();
            for(BlackrazidModel.Part p:data.parts){ModelRenderer box=new ModelRenderer(model,p.uv[0],p.uv[1]);box.setTextureSize(p.size[0],p.size[1]);
                for(BlackrazidModel.Box b:p.boxes){float[] v=b.values;box.mirror=b.mirror;box.addBox(v[0],v[1],v[2],(int)v[3],(int)v[4],(int)v[5],v.length>6?v[6]:0);}
                box.setRotationPoint(p.point[0],p.point[1],p.point[2]);box.rotateAngleX=p.rotation[0];box.rotateAngleY=p.rotation[1];box.rotateAngleZ=p.rotation[2];result.add(box);
            }boxes=result;
        }catch(IOException e){throw new IllegalStateException("Cannot load original tunnel cubes",e);}
    }
    private static Mesh mesh(String name){
        if(MESHES.containsKey(name))return MESHES.get(name);
        try(InputStream in=Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("metroport:models/blackrazid/tunnels/"+name)).getInputStream();Reader r=reader("blackrazid_tunnel_mesh_materials.json")){
            Mesh data=new Mesh();data.geometry=McmodObjMesh.readMaterials(in);
            JsonObject maps=new JsonParser().parse(r).getAsJsonObject().getAsJsonObject(name);
            for(Map.Entry<String,JsonElement> entry:maps.entrySet())data.materials.put(entry.getKey(),new ResourceLocation(entry.getValue().getAsString()));
            if(!data.materials.keySet().containsAll(data.geometry.groups.keySet()))throw new IllegalStateException("Unmapped native tunnel material: "+name);
            MESHES.put(name,data);return data;
        }catch(IOException e){throw new IllegalStateException("Cannot read original tunnel "+name,e);}
    }
    private static void transform(TunnelRules.Operation op){
        float[] v=op.values;
        switch(op.type){
            case "translate":GlStateManager.translate(v[0],v[1],v[2]);break;
            case "scale":GlStateManager.scale(v[0],v[1],v[2]);break;
            case "rotate":GlStateManager.rotate(v[0],v[1],v[2],v[3]);break;
            default:throw new IllegalStateException("Unknown native transform "+op.type);
        }
    }
    @Override public void render(TunnelTile tile,double x,double y,double z,float partial,int destroyStage,float alpha){
        if(!tile.primary())return;
        int texture=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D),shade=GL11.glGetInteger(GL11.GL_SHADE_MODEL);boolean normalize=GL11.glIsEnabled(GL11.GL_NORMALIZE),cull=GL11.glIsEnabled(GL11.GL_CULL_FACE);FloatBuffer color=BufferUtils.createFloatBuffer(4);GL11.glGetFloat(GL11.GL_CURRENT_COLOR,color);
        GlStateManager.pushMatrix();
        try{
            GlStateManager.translate(x,y,z);GlStateManager.enableNormalize();GlStateManager.color(1,1,1,alpha);
            int angle=TunnelRules.renderAngle(tile.shape(),tile.direction());
            if(tile.shape()==0){
                GlStateManager.translate(.5,1.5,.5);loadBoxes();GlStateManager.rotate(180,0,0,1);GlStateManager.rotate(angle,0,1,0);GlStateManager.scale(1.4,1.4,1);GlStateManager.translate(.3,3.9,0);
                bindTexture(new ResourceLocation("metroport:textures/blackrazid/tunnels/tunnel.png"));for(ModelRenderer box:boxes)box.render(1/16F);
            }else{
                TunnelRules.Shape profile=TunnelRules.profile(tile.shape());Mesh mesh=mesh(profile.mesh);GlStateManager.shadeModel(GL11.GL_SMOOTH);
                if(profile.doubleSided)GlStateManager.disableCull();
                for(TunnelRules.Operation op:profile.transforms[tile.direction()])transform(op);
                for(Map.Entry<String,List<float[]>> entry:mesh.geometry.groups.entrySet()){
                    bindTexture(mesh.materials.get(entry.getKey()));BufferBuilder b=Tessellator.getInstance().getBuffer();b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
                    for(float[] v:entry.getValue())b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();Tessellator.getInstance().draw();
                }
            }
        }finally{
            GlStateManager.color(color.get(0),color.get(1),color.get(2),color.get(3));GlStateManager.bindTexture(texture);GlStateManager.shadeModel(shade);if(cull)GlStateManager.enableCull();else GlStateManager.disableCull();if(normalize)GlStateManager.enableNormalize();else GlStateManager.disableNormalize();GlStateManager.popMatrix();
        }
    }
}
