package ru.metrocraft.port.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** OBJ geometry reader for the bundled models; external MTL files are not needed when binding the original atlas. */
public final class LegacyObjModel {
    private static final Map<String,LegacyObjModel> CACHE=new HashMap<>();
    private final List<float[]> triangles=new ArrayList<>();
    public static LegacyObjModel get(String id){return CACHE.computeIfAbsent(id,LegacyObjModel::new);}
    private LegacyObjModel(String id) {
        List<float[]> positions=new ArrayList<>(), uv=new ArrayList<>();
        ResourceLocation file=new ResourceLocation("metroport:models/legacy/"+id+".obj");
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager()
                .getResource(file).getInputStream(),StandardCharsets.UTF_8))) {
            String line;
            while((line=reader.readLine())!=null) {
                String[] p=line.trim().split("\\s+");
                if(p.length<2)continue;
                if(p[0].equals("v"))positions.add(new float[]{Float.parseFloat(p[1]),Float.parseFloat(p[2]),Float.parseFloat(p[3])});
                else if(p[0].equals("vt"))uv.add(new float[]{Float.parseFloat(p[1]),1-Float.parseFloat(p[2])});
                else if(p[0].equals("f"))for(int j=2;j<p.length-1;j++) {
                    float[][] v={vertex(p[1],positions,uv),vertex(p[j],positions,uv),vertex(p[j+1],positions,uv)};
                    Vec3d normal=new Vec3d(v[1][0]-v[0][0],v[1][1]-v[0][1],v[1][2]-v[0][2])
                            .crossProduct(new Vec3d(v[2][0]-v[0][0],v[2][1]-v[0][1],v[2][2]-v[0][2])).normalize();
                    for(float[] point:v)triangles.add(new float[]{point[0],point[1],point[2],point[3],point[4],(float)normal.x,(float)normal.y,(float)normal.z});
                }
            }
        }catch(Exception e){throw new IllegalStateException("Cannot load OBJ "+file,e);}
    }
    private static float[] vertex(String text,List<float[]> positions,List<float[]> uv) {
        String[] pieces=text.split("/");int index=Integer.parseInt(pieces[0]);
        float[] p=positions.get(index>0?index-1:positions.size()+index);
        float[] t=pieces.length>1&&!pieces[1].isEmpty()?uv.get(Integer.parseInt(pieces[1])-1):new float[2];
        return new float[]{p[0],p[1],p[2],t[0],t[1]};
    }
    public void render() {
        BufferBuilder b=Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_TEX_NORMAL);
        for(float[] v:triangles)b.pos(v[0],v[1],v[2]).tex(v[3],v[4]).normal(v[5],v[6],v[7]).endVertex();
        Tessellator.getInstance().draw();
    }
}
