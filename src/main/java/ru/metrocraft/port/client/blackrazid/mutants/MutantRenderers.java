package ru.metrocraft.port.client.blackrazid.mutants;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.blackrazid.mutants.*;

public final class MutantRenderers {
    private MutantRenderers(){}
    public static void register(){
        add(RatEntity.class,new ModelRat(),"rat");add(SpiderEntity.class,new ModelSpider(),"spider");add(BiblioEntity.class,new ModelBiblio(),"bibliotecar");
        marker(MutantSpawner.SpiderNest.class);marker(MutantSpawner.LibrarianNest.class);
    }
    private static <T extends EntityLiving> void add(Class<T> type,ModelBase model,String texture){
        ResourceLocation atlas=new ResourceLocation("metroport:textures/blackrazid/mutants/"+texture+".png");
        RenderingRegistry.registerEntityRenderingHandler(type,manager->new RenderLiving<T>(manager,model,0){
            @Override protected ResourceLocation getEntityTexture(T e){return atlas;}
            @Override public void doRender(T e,double x,double y,double z,float yaw,float partial){
                GL11.glPushAttrib(GL11.GL_LIGHTING_BIT|GL11.GL_ENABLE_BIT|GL11.GL_POLYGON_BIT|GL11.GL_CURRENT_BIT);
                try{super.doRender(e,x,y,z,yaw,partial);}finally{GL11.glPopAttrib();}
            }
        });
    }
    private static <T extends MutantSpawner> void marker(Class<T> type){
        ResourceLocation atlas=new ResourceLocation("metroport:textures/blackrazid/mutants/rat.png");ModelRat model=new ModelRat();
        RenderingRegistry.registerEntityRenderingHandler(type,manager->new Render<T>(manager){
            @Override protected ResourceLocation getEntityTexture(T e){return atlas;}
            @Override public void doRender(T e,double x,double y,double z,float yaw,float partial){
                bindEntityTexture(e);net.minecraft.client.renderer.GlStateManager.pushMatrix();
                GL11.glPushAttrib(GL11.GL_LIGHTING_BIT|GL11.GL_ENABLE_BIT|GL11.GL_POLYGON_BIT|GL11.GL_CURRENT_BIT);
                try{net.minecraft.client.renderer.GlStateManager.translate(x,y,z);net.minecraft.client.renderer.GlStateManager.rotate(180-yaw,0,1,0);
                    net.minecraft.client.renderer.GlStateManager.scale(-1,-1,1);net.minecraft.client.renderer.GlStateManager.translate(0,-1.5,0);
                    model.render(e,0,0,e.ticksExisted+partial,0,0,.0625F);
                }finally{GL11.glPopAttrib();net.minecraft.client.renderer.GlStateManager.popMatrix();}
            }
        });
    }
}
