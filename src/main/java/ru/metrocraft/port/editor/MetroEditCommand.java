package ru.metrocraft.port.editor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import ru.metrocraft.port.MetroPort;
import java.io.IOException;
import java.util.*;

/** Server-side, creative + permission 2 only. This is NOT a remote-server/map downloader. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class MetroEditCommand extends CommandBase {
    private static final Map<UUID,State> sessions=new HashMap<>();
    private static final class State {
        final WorldServer world;final EditSession session=new EditSession();long next,previewDeadline;
        State(WorldServer world){this.world=world;}
    }
    public static void clear(){sessions.clear();}
    @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent event){sessions.remove(event.player.getUniqueID());}
    @SubscribeEvent public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event){sessions.remove(event.player.getUniqueID());}
    @SubscribeEvent public static void respawn(PlayerEvent.PlayerRespawnEvent event){sessions.remove(event.player.getUniqueID());}
    @Override public String getName(){return "metroedit";}
    @Override public int getRequiredPermissionLevel(){return 2;}
    @Override public String getUsage(ICommandSender s){return "/metroedit pos1|pos2 [x y z] / copy / paste [x y z] / outline / preview [x y z] / confirm / cancel / rotate 90|180|270 / mirror x|z / undo / save <name> / load <name> / status / reset";}
    private static void require(boolean condition,String message){if(!condition)throw new IllegalArgumentException(message);}
    private static void location(MinecraftServer server,EntityPlayerMP p,BlockPos pos){
        WorldServer w=p.getServerWorld();
        require(pos.getY()>=0 && pos.getY()<256,"Height must be 0..255");
        require(Math.abs((long)pos.getX())<30000000 && Math.abs((long)pos.getZ())<30000000,"Outside world limits");
        require(p.getDistanceSqToCenter(pos)<=64D*64D,"Maximum edit distance is 64 blocks");
        require(w.getWorldBorder().contains(pos) && w.isBlockLoaded(pos,false),"Outside border or chunk not loaded");
        require(!server.isBlockProtected(w,pos,p) && w.isBlockModifiable(p,pos) && p.canPlayerEdit(pos,EnumFacing.UP,ItemStack.EMPTY),"Protected location");
    }
    static EditSession.Access access(MinecraftServer server,EntityPlayerMP player){
        WorldServer world=player.getServerWorld();
        return new EditSession.Access(){
            public Blueprint.Spec read(BlockPos pos){
                require(player.getServerWorld()==world,"World changed during edit");
                location(server,player,pos);require(world.getTileEntity(pos)==null,"Tile entities are not supported");
                return EditorBlocks.encode(world.getBlockState(pos));
            }
            public void authorize(BlockPos pos,Blueprint.Spec before,Blueprint.Spec after){
                require(player.getServerWorld()==world,"World changed during edit");
                location(server,player,pos);IBlockState old=EditorBlocks.decode(before),next=EditorBlocks.decode(after);
                if(old.equals(next))return;
                if(!before.air() && MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world,pos,old,player)))throw new IllegalArgumentException("Break protection denied this edit");
                if(!after.air()){
                    // Preflight without transiently placing blocks: expose proposed state to PlaceEvent.
                    BlockSnapshot snapshot=new BlockSnapshot(world,pos,old){@Override public IBlockState getCurrentBlock(){return next;}};
                    if(MinecraftForge.EVENT_BUS.post(new BlockEvent.PlaceEvent(snapshot,world.getBlockState(pos.down()),player,EnumHand.MAIN_HAND)))
                        throw new IllegalArgumentException("Place protection denied this edit");
                }
            }
            public void write(BlockPos pos,Blueprint.Spec spec){
                IBlockState next=EditorBlocks.decode(spec);
                if(!world.getBlockState(pos).equals(next) && !world.setBlockState(pos,next,2))throw new IllegalStateException("Block write failed");
                if(!world.getBlockState(pos).equals(next))throw new IllegalStateException("Block write rejected");
            }
        };
    }
    private static void hide(EntityPlayerMP player){
        MetroPort.NETWORK.sendTo(EditorPreviewPacket.clear(player.dimension,player.getEntityId()),player);
    }
    @Override public void execute(MinecraftServer server,ICommandSender sender,String[] args)throws CommandException{
        EntityPlayerMP p=getCommandSenderAsPlayer(sender);
        if(!p.canUseCommand(2,getName()) || !p.capabilities.isCreativeMode || p.isSpectator() || !p.isEntityAlive())
            throw new CommandException("MetroEdit: нужны Creative и права оператора уровня 2.");
        if(args.length==0){sender.sendMessage(new TextComponentString(getUsage(sender)));return;}
        State state=sessions.get(p.getUniqueID());
        if(state==null || state.world!=p.getServerWorld()){state=new State(p.getServerWorld());sessions.put(p.getUniqueID(),state);}
        if(System.nanoTime()<state.next)throw new CommandException("MetroEdit: подождите секунду между командами.");
        state.next=System.nanoTime()+1000000000L;EditSession session=state.session;EditSession.Access access=access(server,p);
        String result;
        try{
            switch(args[0]){
                case "pos1":case "pos2":{
                    require(args.length==1 || args.length==4,"Use pos1/pos2 [x y z]");
                    BlockPos pos=args.length==4?parseBlockPos(p,args,1,false):p.getPosition();location(server,p,pos);
                    session.select(args[0].equals("pos1"),pos);result=args[0]+" = "+pos.getX()+" "+pos.getY()+" "+pos.getZ();break;
                }
                case "copy":require(args.length==1,"Use copy");session.copy(access);result="Скопировано блоков: "+session.clipboard().size();break;
                case "paste":{
                    require(args.length==1 || args.length==4,"Use paste [x y z]");
                    BlockPos pos=args.length==4?parseBlockPos(p,args,1,false):p.getPosition();
                    result="Изменено блоков: "+session.paste(access,pos)+". Воздух пропущен.";break;
                }
                case "outline":{
                    require(args.length==1,"Use outline");EditSession.Box box=session.selection();
                    location(server,p,box.origin);location(server,p,box.origin.add(box.width-1,box.height-1,box.depth-1));
                    session.cancelPreview();state.previewDeadline=0;
                    MetroPort.NETWORK.sendTo(new EditorPreviewPacket(p.dimension,p.getEntityId(),1,box,Collections.emptyList()),p);
                    result="Выделение показано голубой рамкой на 10 секунд.";break;
                }
                case "preview":{
                    require(args.length==1 || args.length==4,"Use preview [x y z]");
                    session.cancelPreview();state.previewDeadline=0;hide(p);
                    BlockPos pos=args.length==4?parseBlockPos(p,args,1,false):p.getPosition();
                    Blueprint b=session.clipboard();location(server,p,pos);location(server,p,pos.add(b.width-1,b.height-1,b.depth-1));
                    EditSession.Preview preview=session.preview(access,pos);state.previewDeadline=PreviewRules.deadline(System.nanoTime());
                    MetroPort.NETWORK.sendTo(new EditorPreviewPacket(p.dimension,p.getEntityId(),2,preview.box,preview.changed),p);
                    result="Предпросмотр: "+preview.changed.size()+" изменений. Показано до 256 ячеек и общий габарит. /metroedit confirm в течение 10 секунд.";break;
                }
                case "confirm":{
                    require(args.length==1,"Use confirm");
                    if(!PreviewRules.alive(System.nanoTime(),state.previewDeadline)){session.cancelPreview();hide(p);throw new IllegalStateException("Preview expired; create a new preview");}
                    result="Подтверждено изменений: "+session.confirm(access)+". Координаты зафиксированы предпросмотром.";state.previewDeadline=0;break;
                }
                case "cancel":require(args.length==1,"Use cancel");session.cancelPreview();state.previewDeadline=0;result="Предпросмотр отменён. Буфер и undo сохранены.";break;
                case "mirror":{
                    require(args.length==2 && (args[1].equals("x") || args[1].equals("z")),"Use mirror x|z");
                    session.clipboard(session.clipboard().mirror(args[1].equals("x"),(spec,xAxis)->EditorBlocks.encode(EditorBlocks.decode(spec).withMirror(xAxis?Mirror.FRONT_BACK:Mirror.LEFT_RIGHT))));
                    result="Буфер отражён по оси "+args[1]+".";break;
                }
                case "undo":require(args.length==1,"Use undo");result="Отменено блоков: "+session.undo(access);break;
                case "rotate":{
                    require(args.length==2,"Use rotate 90|180|270");int degrees=parseInt(args[1]);require(degrees==90 || degrees==180 || degrees==270,"Use 90, 180 or 270");
                    session.clipboard(session.clipboard().rotate(degrees/90,(spec,turns)->EditorBlocks.encode(EditorBlocks.decode(spec).withRotation(Rotation.values()[turns]))));
                    result="Буфер повёрнут на "+degrees+" градусов.";break;
                }
                case "save":case "load":{
                    require(args.length==2,"Use save/load <name>");
                    BlueprintStore store=new BlueprintStore(p.getServerWorld().getSaveHandler().getWorldDirectory().toPath(),p.getUniqueID());
                    if(args[0].equals("save")){store.save(args[1],session.clipboard());result="Постройка сохранена: "+args[1];}
                    else{
                        Blueprint b=store.load(args[1]);
                        for(int y=0;y<b.height;y++)for(int z=0;z<b.depth;z++)for(int x=0;x<b.width;x++)EditorBlocks.decode(b.get(x,y,z));
                        session.clipboard(b);result="Постройка загружена в буфер: "+args[1];
                    }break;
                }
                case "status":require(args.length==1,"Use status");{
                    Blueprint b=session.clipboard();result="Буфер: "+b.width+" × "+b.height+" × "+b.depth+"; максимум 4096 блоков, 32 по стороне, радиус 64.";break;
                }
                case "reset":require(args.length==1,"Use reset");sessions.remove(p.getUniqueID());result="Выделение, буфер и отмена очищены.";break;
                default:throw new IllegalArgumentException(getUsage(sender));
            }
        }catch(IOException|IllegalArgumentException|IllegalStateException failure){throw new CommandException("MetroEdit: "+failure.getMessage());}
        if(Arrays.asList("pos1","pos2","copy","paste","confirm","cancel","rotate","mirror","undo","load","reset").contains(args[0])){
            session.cancelPreview();state.previewDeadline=0;hide(p);
        }
        sender.sendMessage(new TextComponentString("MetroEdit: "+result));
    }
    @Override public List<String> getTabCompletions(MinecraftServer s,ICommandSender p,String[] a,BlockPos pos){
        if(a.length==1)return getListOfStringsMatchingLastWord(a,"pos1","pos2","copy","paste","outline","preview","confirm","cancel","rotate","mirror","undo","save","load","status","reset");
        if(a.length==2 && a[0].equals("rotate"))return getListOfStringsMatchingLastWord(a,"90","180","270");
        if(a.length==2 && a[0].equals("mirror"))return getListOfStringsMatchingLastWord(a,"x","z");
        return Collections.emptyList();
    }
}
