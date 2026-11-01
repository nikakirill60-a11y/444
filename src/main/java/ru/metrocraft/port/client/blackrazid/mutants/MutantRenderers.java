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
        add(DemonEntity.class,new ModelDemon(),"demon");
        marker(MutantSpawner.DemonNest.class);
        add(KikimoraEntity.class,new ModelKikimora(),"kikimora");
        add(StrazhEntity.class,new ModelStrazh(),"strazh");
        add(NosachEntity.class,new ModelNosach(),"nosach");
        add(DarkEntity.class,new ModelDark(),"dark");
        add(BearEntity.class,new ModelBear(),"bear");
        add(MobusEntity.class,new ModelMobus(),"mobus");
        add(AquaFemaleEntity.class,new ModelAquaf(),"aqua_female");
        add(AquaMaleEntity.class,new ModelAquam(),"aqua_male");
        marker(MutantSpawner.SpiderNest.class);marker(MutantSpawner.LibrarianNest.class);
        marker(MutantSpawner.KikimoraNest.class);
        marker(MutantSpawner.StrazhNest.class);
        marker(MutantSpawner.NosachNest.class);
        marker(MutantSpawner.DarkNest.class);
        marker(MutantSpawner.BearNest.class);
        marker(MutantSpawner.MobusNest.class);
        marker(MutantSpawner.AquaFemaleNest.class);
        marker(MutantSpawner.AquaMaleNest.class);
    }
    private static <T extends EntityLiving> void add(Class<T> type,ModelBase model,String texture){
        ResourceLocation atlas=new ResourceLocation("metroport:textures/blackrazid/mutants/"+texture+".png");
        RenderingRegistry.registerEntityRenderingHandler(type,manager->new RenderLiving<T>(manager,model,0){
            @Override protected ResourceLocation getEntityTexture(T e){return atlas;}
            @Override public void doRender(T e,double x,double y,double z,float yaw,float partial){
                int shade=GL11.glGetInteger(GL11.GL_SHADE_MODEL);boolean cull=GL11.glIsEnabled(GL11.GL_CULL_FACE);
                try{super.doRender(e,x,y,z,yaw,partial);}finally{GL11.glShadeModel(shade);restoreCull(cull);}
            }
        });
    }
    // Do not restore culling through raw GL: Minecraft caches this capability.
    private static void restoreCull(boolean enabled){
        if(enabled)net.minecraft.client.renderer.GlStateManager.enableCull();
        else net.minecraft.client.renderer.GlStateManager.disableCull();
    }
    private static <T extends MutantSpawner> void marker(Class<T> type){
        ResourceLocation atlas=new ResourceLocation("metroport:textures/blackrazid/mutants/rat.png");ModelRat model=new ModelRat();
        RenderingRegistry.registerEntityRenderingHandler(type,manager->new Render<T>(manager){
            @Override protected ResourceLocation getEntityTexture(T e){return atlas;}
            @Override public void doRender(T e,double x,double y,double z,float yaw,float partial){
                bindEntityTexture(e);net.minecraft.client.renderer.GlStateManager.pushMatrix();
                int shade=GL11.glGetInteger(GL11.GL_SHADE_MODEL);boolean cull=GL11.glIsEnabled(GL11.GL_CULL_FACE);
                try{net.minecraft.client.renderer.GlStateManager.translate(x,y,z);net.minecraft.client.renderer.GlStateManager.rotate(180-yaw,0,1,0);
                    net.minecraft.client.renderer.GlStateManager.scale(-1,-1,1);net.minecraft.client.renderer.GlStateManager.translate(0,-1.5,0);
                    model.render(e,0,0,e.ticksExisted+partial,0,0,.0625F);
                }finally{GL11.glShadeModel(shade);restoreCull(cull);net.minecraft.client.renderer.GlStateManager.popMatrix();}
            }
        });
    }
}
