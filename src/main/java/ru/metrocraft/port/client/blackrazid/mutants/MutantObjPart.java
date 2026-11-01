package ru.metrocraft.port.client.blackrazid.mutants;

import java.io.InputStream;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.mcmod.McmodObjMesh;

/** Original articulated OBJs with their explicit native atlas. Never includes a stock Minecraft mob mesh. */
public final class MutantObjPart {
    private static final Map<ResourceLocation,McmodObjMesh> CACHE=new HashMap<>();
    private final ResourceLocation path;
    public MutantObjPart(String path){this.path=new ResourceLocation(path);}
    public static void reload(){CACHE.clear();}
    private McmodObjMesh mesh(){
        McmodObjMesh result=CACHE.get(path);if(result!=null)return result;
        try(InputStream in=Minecraft.getMinecraft().getResourceManager().getResource(path).getInputStream()){
            result=McmodObjMesh.read(in);CACHE.put(path,result);return result;
        }catch(Exception e){throw new IllegalStateException("Cannot read BlackRazid mutant part "+path,e);}
    }
    public void render(){
        McmodObjMesh mesh=mesh();BufferBuilder b=Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
        for(List<float[]> part:mesh.groups.values())for(float[] v:part)b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
        Tessellator.getInstance().draw();
    }
}
