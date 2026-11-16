package ru.metrocraft.port.structures;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.metrocraft.port.MetroPort;
import java.io.IOException;
import java.util.*;

/** Explicit operator-only placement, not worldgen, an editor, or an automatic map overwrite. */
@Mod.EventBusSubscriber(modid=MetroPort.ID)
public final class MetroNetworkCommand extends CommandBase {
    private static final Map<UUID,Preview> PREVIEWS=new HashMap<>();
    private static MetroNetworkJob active;
    private static final class Preview {
        final MetroNetworkPlan plan;final BlockPos origin;final int dimension;final long expires;final String token;
        Preview(MetroNetworkPlan plan,BlockPos origin,int dimension,long now) {
            this.plan=plan;this.origin=origin;this.dimension=dimension;expires=now+12000;token=UUID.randomUUID().toString().substring(0,8);
        }
    }
    @Override public String getName() {return "metronetwork";}
    @Override public String getUsage(ICommandSender sender) {return "/metronetwork plan [Y основания] | build <код> | status | cancel | undo <UUID>";}
    @Override public int getRequiredPermissionLevel() {return 2;}
    public static void reset() {PREVIEWS.clear();active=null;}
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if(event.phase==TickEvent.Phase.END && active!=null) {
            active.tick();if(active.phase()==MetroNetworkJob.Phase.DONE)active=null;
        }
    }
    private void say(ICommandSender sender,String text) {sender.sendMessage(new TextComponentString("[MinETRO] "+text));}
    @Override public void execute(MinecraftServer server,ICommandSender sender,String[] args) throws CommandException {
        if(!checkPermission(server,sender))throw new CommandException("commands.generic.permission");
        if(args.length==0 || !(Arrays.asList("plan","build","status","cancel","undo").contains(args[0]))
                || (args[0].equals("plan")?args.length>2:(args[0].equals("build") || args[0].equals("undo"))?args.length!=2:args.length!=1))
            throw new WrongUsageException(getUsage(sender));
        EntityPlayerMP player=getCommandSenderAsPlayer(sender);WorldServer world=player.getServerWorld();
        switch(args[0]) {
            case "plan": {
                if(args.length>2)throw new WrongUsageException(getUsage(sender));
                int y=args.length==2?parseInt(args[1],4,180):player.getPosition().getY()-25;
                // Surface vestibule is anchored 24 blocks above track bed, not a terrain-seeking digger.
                if(y<4 || y>180)throw new CommandException("Выберите основание Y=4..180: /metronetwork plan <Y>");
                PREVIEWS.entrySet().removeIf(e->e.getValue().expires<world.getTotalWorldTime());
                Preview preview=new Preview(MetroNetworkPlan.create(24),new BlockPos(player.posX,y,player.posZ),world.provider.getDimension(),world.getTotalWorldTime());
                PREVIEWS.put(player.getUniqueID(),preview);
                say(sender,"ПЛАН (без изменения мира): 4 станции × 49 блоков, 2 замкнутых пути, переходы, 4 служебных комнаты, лестничный выход.");
                say(sender,"Область: "+preview.plan.min().add(preview.origin)+" — "+preview.plan.max().add(preview.origin)+"; вход "+preview.plan.entrance().add(preview.origin)+".");
                say(sender,"ВНИМАНИЕ: выкапывает тоннели и заменяет природные блоки/руды. Не может отличить поставленные игроком камень/землю от природных. Используйте отдельную свободную область и копию мира.");
                say(sender,"Только загруженные чанки; жидкости, контейнеры и блоки вне списка природных запрещены. Подтвердить за 10 минут: /metronetwork build "+preview.token);
                return;
            }
            case "build": {
                if(args.length!=2)throw new WrongUsageException(getUsage(sender));ensureIdle();
                Preview p=PREVIEWS.get(player.getUniqueID());
                if(p==null || p.expires<world.getTotalWorldTime() || !p.token.equals(args[1]) || p.dimension!=world.provider.getDimension())throw new CommandException("План отсутствует/устарел/другое измерение. Выполните /metronetwork plan");
                PREVIEWS.remove(player.getUniqueID());active=MetroNetworkJob.build(world,player,p.plan,p.origin);
                say(sender,"Проверяю область без загрузки новых чанков. До завершения проверки и сохранения резервной копии блоки не меняются.");return;
            }
            case "status":
                if(args.length!=1)throw new WrongUsageException(getUsage(sender));
                say(sender,active==null?"Нет активной операции.":"Операция "+active.id+": "+active.phase()+", установлено "+active.written()+", восстановлено "+active.restored()+".");return;
            case "cancel":
                if(args.length!=1)throw new WrongUsageException(getUsage(sender));
                if(active==null)throw new CommandException("Нет активной операции.");
                active.cancel();say(sender,"Запрошена отмена. Если установка уже началась — будет поэтапный откат.");return;
            case "undo":
                if(args.length!=2)throw new WrongUsageException(getUsage(sender));ensureIdle();
                try {active=MetroNetworkJob.undo(world,player,UUID.fromString(args[1]));}
                catch(IllegalArgumentException | IOException e) {throw new CommandException("Не удалось открыть копию: "+e.getMessage());}
                say(sender,"Восстанавливаю только блоки, оставшиеся в установленном сетью состоянии. Чужие изменения и изменённые таблички пропускаются.");return;
            default:throw new WrongUsageException(getUsage(sender));
        }
    }
    private void ensureIdle() throws CommandException {if(active!=null)throw new CommandException("Уже выполняется операция "+active.id+". /metronetwork status");}
    @Override public List<String> getTabCompletions(MinecraftServer server,ICommandSender sender,String[] args,BlockPos pos) {
        return args.length==1?getListOfStringsMatchingLastWord(args,"plan","build","status","cancel","undo"):Collections.emptyList();
    }
}
