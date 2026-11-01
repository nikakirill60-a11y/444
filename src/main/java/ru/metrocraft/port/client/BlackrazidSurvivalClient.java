package ru.metrocraft.port.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import ru.metrocraft.port.MetroPort;
import ru.metrocraft.port.blackrazid.survival.*;
import java.util.*;

@Mod.EventBusSubscriber(modid=MetroPort.ID,value=Side.CLIENT)
public final class BlackrazidSurvivalClient {
    private static final KeyBinding LAMP=new KeyBinding("key.metroport.blackrazid_lamp",Keyboard.KEY_L,"key.categories.metroport");
    private static float water=100,energy=0;
    private static boolean lamp,ready;
    private static WorldClient world;
    private static final Map<BlockPos,int[]> previous=new LinkedHashMap<>();
    private BlackrazidSurvivalClient(){}
    public static float energyPercent(){return energy;}
    public static void initialize(){ClientRegistry.registerKeyBinding(LAMP);}
    public static void receive(int dimension,int entity,float w,float e,boolean on){
        Minecraft mc=Minecraft.getMinecraft();WorldClient expected=mc.world;
        mc.addScheduledTask(()->{
            if(expected==null || mc.world!=expected || mc.player==null || mc.player.dimension!=dimension || mc.player.getEntityId()!=entity)return;
            if(world!=expected){clearLight();world=expected;}
            water=SurvivalState.percent(w,100);energy=SurvivalState.percent(e,0);lamp=on && energy>0;ready=true;
        });
    }
    @SubscribeEvent public static void key(InputEvent.KeyInputEvent event){
        Minecraft mc=Minecraft.getMinecraft();while(LAMP.isPressed())if(mc.player!=null && mc.currentScreen==null)MetroPort.NETWORK.sendToServer(new LampPacket());
    }
    private static void clearLight(){
        if(world!=null && !previous.isEmpty()){
            List<BlockPos> restored=new ArrayList<>();
            for(Map.Entry<BlockPos,int[]> entry:previous.entrySet()){
                BlockPos p=entry.getKey();int[] light=entry.getValue();
                if(!world.isBlockLoaded(p) || world.getLightFor(EnumSkyBlock.BLOCK,p)!=light[1])continue;
                world.setLightFor(EnumSkyBlock.BLOCK,p,light[0]);restored.add(p);
            }
            // Restore vanilla lighting after intervening torch/block updates, not stale cached values.
            for(BlockPos p:restored){world.checkLightFor(EnumSkyBlock.BLOCK,p);world.markBlockRangeForRenderUpdate(p.add(-1,-1,-1),p.add(1,1,1));}
        }previous.clear();
    }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;Minecraft mc=Minecraft.getMinecraft();
        if(mc.world!=world){clearLight();world=mc.world;ready=false;lamp=false;water=100;energy=0;}
        if(world==null || mc.player==null || !mc.player.isEntityAlive() || !ready || !lamp || !SurvivalSystem.hasLamp(mc.player) || mc.player.isSpectator()){
            clearLight();return;
        }
        if(mc.player.ticksExisted%5!=0)return;
        clearLight();Vec3d eyes=mc.player.getPositionEyes(1),look=mc.player.getLookVec();BlockPos anchor=new BlockPos(eyes);
        for(int x=-8;x<=8;x++)for(int y=-8;y<=8;y++)for(int z=-8;z<=8;z++){
            BlockPos p=anchor.add(x,y,z);if(p.getY()<0 || p.getY()>=256 || !world.isBlockLoaded(p))continue;
            Vec3d center=new Vec3d(p).add(.5,.5,.5),delta=center.subtract(eyes);double d=delta.length();
            int brightness=LampRules.brightness(d,d<1e-6?1:delta.dotProduct(look)/d);if(brightness==0)continue;
            if(world.getBlockState(p).getLightOpacity(world,p)>0)continue;
            RayTraceResult obstruction=world.rayTraceBlocks(eyes,center,false,true,false);if(obstruction!=null)continue;
            int old=world.getLightFor(EnumSkyBlock.BLOCK,p);if(old>=brightness)continue;
            previous.put(p,new int[]{old,brightness});world.setLightFor(EnumSkyBlock.BLOCK,p,brightness);
            world.markBlockRangeForRenderUpdate(p.add(-1,-1,-1),p.add(1,1,1));
        }
    }
    @SubscribeEvent public static void unload(net.minecraftforge.event.world.WorldEvent.Unload event){
        if(event.getWorld()==world){clearLight();world=null;ready=false;lamp=false;}
    }
    @SubscribeEvent public static void hud(RenderGameOverlayEvent.Post event){
        Minecraft mc=Minecraft.getMinecraft();if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL || mc.player==null || !ready || mc.gameSettings.hideGUI || mc.player.isSpectator())return;
        if(mc.player.capabilities.isCreativeMode && !SurvivalSystem.hasLamp(mc.player) && !(mc.player.getHeldItemMainhand().getItem() instanceof SurvivalItem))return;
        int y=event.getResolution().getScaledHeight()-92;
        mc.fontRenderer.drawStringWithShadow(I18n.format("metroport.blackrazid.water",(int)Math.ceil(water)),8,y,water<=15?0xFF6655:0x80CFFF);
        mc.fontRenderer.drawStringWithShadow(I18n.format("metroport.blackrazid.energy",(int)Math.ceil(energy),I18n.format(lamp?"metroport.blackrazid.on":"metroport.blackrazid.off")),8,y+11,0xE5D792);
    }
}
