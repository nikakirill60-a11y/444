package ru.metrocraft.port.structures;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/** Server tick budgeted transaction. Preflight never writes/loads chunks. No force mode. */
public final class MetroNetworkJob {
    public enum Phase {CHECK,BUILD,RESTORE,DONE}
    interface Access {
        boolean loaded(BlockPos p);boolean inside(BlockPos p);boolean editable(BlockPos p);
        IBlockState state(BlockPos p);TileEntity tile(BlockPos p);
        boolean set(BlockPos p,IBlockState state);void sign(BlockPos p,String[] text);
    }
    private static final Set<Block> NATURAL=Collections.newSetFromMap(new IdentityHashMap<Block,Boolean>());
    private static void initializeNatural() {
        if(!NATURAL.isEmpty())return;
        Collections.addAll(NATURAL,Blocks.AIR,Blocks.STONE,Blocks.DIRT,Blocks.GRASS,Blocks.GRAVEL,Blocks.SAND,
                Blocks.SANDSTONE,Blocks.RED_SANDSTONE,Blocks.CLAY,Blocks.HARDENED_CLAY,Blocks.STAINED_HARDENED_CLAY,
                Blocks.MYCELIUM,Blocks.SNOW,Blocks.SNOW_LAYER,Blocks.TALLGRASS,Blocks.YELLOW_FLOWER,Blocks.RED_FLOWER,
                Blocks.DEADBUSH,Blocks.BROWN_MUSHROOM,Blocks.RED_MUSHROOM,
                Blocks.COAL_ORE,Blocks.IRON_ORE,Blocks.GOLD_ORE,Blocks.LAPIS_ORE,Blocks.REDSTONE_ORE,Blocks.LIT_REDSTONE_ORE,Blocks.DIAMOND_ORE,Blocks.EMERALD_ORE);
    }
    public static boolean natural(IBlockState state) {initializeNatural();return NATURAL.contains(state.getBlock());}
    private final Access world;private final int dimension;private final File directory;private final Consumer<String> message;
    private final List<Map.Entry<BlockPos,MetroNetworkPlan.Kind>> scan;
    private final MetroNetworkPlan plan;private final BlockPos origin;
    private final List<MetroNetworkBackup.Change> changes=new ArrayList<>();
    private Phase phase;private int cursor,written,restored,skipped;private boolean cancel;
    private MetroNetworkBackup backup;
    public UUID id;public final UUID player;
    public Phase phase() {return phase;}
    public int written() {return written;}
    public int restored() {return restored;}
    public int skipped() {return skipped;}
    public void cancel() {cancel=true;}
    MetroNetworkJob(Access world,MetroNetworkPlan plan,BlockPos origin,int dimension,File directory,UUID player,Consumer<String> message) {
        this.world=world;this.plan=plan;this.origin=origin;this.dimension=dimension;this.directory=directory;this.player=player;this.message=message;
        scan=new ArrayList<>(plan.cells().entrySet());phase=Phase.CHECK;id=UUID.randomUUID();
    }
    MetroNetworkJob(Access world,MetroNetworkBackup backup,int dimension,File directory,UUID player,Consumer<String> message) {
        if(backup.dimension!=dimension)throw new IllegalArgumentException("Нужно находиться в измерении "+backup.dimension);
        this.world=world;this.backup=backup;this.dimension=dimension;this.directory=directory;this.player=player;this.message=message;
        plan=null;origin=BlockPos.ORIGIN;scan=Collections.emptyList();changes.addAll(backup.changes);id=backup.id;cursor=changes.size()-1;phase=Phase.RESTORE;
    }
    public static MetroNetworkJob build(WorldServer world,EntityPlayerMP player,MetroNetworkPlan plan,BlockPos origin) {
        return new MetroNetworkJob(access(world,player),plan,origin,world.provider.getDimension(),world.getSaveHandler().getWorldDirectory(),player.getUniqueID(),message(player));
    }
    public static MetroNetworkJob undo(WorldServer world,EntityPlayerMP player,UUID id) throws IOException {
        File dir=world.getSaveHandler().getWorldDirectory();
        return new MetroNetworkJob(access(world,player),MetroNetworkBackup.load(dir,id),world.provider.getDimension(),dir,player.getUniqueID(),message(player));
    }
    private static Consumer<String> message(EntityPlayerMP player) {return text->player.sendMessage(new TextComponentString("[MinETRO] "+text));}
    private static Access access(WorldServer world,EntityPlayerMP player) {return new Access() {
        public boolean loaded(BlockPos p) {return world.isBlockLoaded(p,false);}
        public boolean inside(BlockPos p) {return p.getY()>=1 && p.getY()<world.getHeight()-1 && world.getWorldBorder().contains(p);}
        public boolean editable(BlockPos p) {return !player.isDead && player.getServerWorld()==world && world.isBlockModifiable(player,p) && player.canPlayerEdit(p,EnumFacing.UP,ItemStack.EMPTY);}
        public IBlockState state(BlockPos p) {return world.getBlockState(p);}
        public TileEntity tile(BlockPos p) {return world.getTileEntity(p);}
        public boolean set(BlockPos p,IBlockState state) {return world.setBlockState(p,state,2);}
        public void sign(BlockPos p,String[] lines) {
            TileEntity te=world.getTileEntity(p);if(!(te instanceof TileEntitySign))throw new IllegalStateException("Не создана табличка "+p);
            TileEntitySign sign=(TileEntitySign)te;for(int i=0;i<4;i++)sign.signText[i]=new TextComponentString(lines[i]);
            sign.markDirty();IBlockState state=world.getBlockState(p);world.notifyBlockUpdate(p,state,state,2);
        }
    };}
    /** At most 512 placements / 2048 checks and ~8ms per tick (one write may cost more). */
    public void tick() {
        long deadline=System.nanoTime()+8_000_000L;int budget=phase==Phase.CHECK?2048:512;
        for(int i=0;i<budget && phase!=Phase.DONE && System.nanoTime()<deadline;i++) {
            try {step();}catch(Exception e) {fail("Ошибка: "+e.getMessage());}
        }
    }
    void step() throws IOException {
        if(phase==Phase.DONE)return;
        if(cancel && phase!=Phase.RESTORE) {
            cancel=false;if(phase==Phase.CHECK) {finish("Проверка отменена; мир не изменён.");return;}
            startRestore("Отмена: возвращаю уже установленные блоки.");return;
        }
        switch(phase) {
            case CHECK:check();break;
            case BUILD:place();break;
            case RESTORE:restore();break;
            default:break;
        }
    }
    private String unavailable(BlockPos pos) {
        if(!world.inside(pos))return "За высотой/границей мира: "+pos;
        if(!world.loaded(pos))return "Чанк не загружен: "+pos+". Приблизьтесь к области / увеличьте дистанцию загрузки.";
        if(!world.editable(pos))return "Нет права изменять "+pos;
        return null;
    }
    private void check() throws IOException {
        if(cursor>=scan.size()) {
            // Supports first, rails/signs/ladder last. No drops, neighbor physics or item spawning.
            changes.sort(Comparator.comparingInt((MetroNetworkBackup.Change c)->fragile(c.after)?1:0).thenComparingInt(c->c.pos.getY()));
            backup=new MetroNetworkBackup(id,dimension,changes);backup.save(directory);
            cursor=0;phase=Phase.BUILD;message.accept("Проверка пройдена. Резервная копия: "+id+". Устанавливаю "+changes.size()+" блоков.");return;
        }
        Map.Entry<BlockPos,MetroNetworkPlan.Kind> entry=scan.get(cursor++);BlockPos pos=entry.getKey().add(origin);
        String error=unavailable(pos);if(error!=null) {finish(error+" Мир не изменён.");return;}
        IBlockState old=world.state(pos),next=entry.getValue().state();
        // Even a geometrically identical tile entity may contain someone else's inventory/text.
        if(world.tile(pos)!=null || !natural(old)) {finish("Отказ: постройка/жидкость/блок вне списка природных в "+pos+" ("+old.getBlock().getRegistryName()+"). Мир не изменён.");return;}
        if(old!=next)changes.add(new MetroNetworkBackup.Change(pos,old,next,plan.signs().get(entry.getKey())));
    }
    private static boolean fragile(IBlockState s) {Block b=s.getBlock();return b instanceof net.minecraft.block.BlockRailBase || b==Blocks.WALL_SIGN || b==Blocks.LADDER;}
    private void place() {
        if(cursor>=changes.size()) {finish("Готово: 4 станции, 2 кольцевых пути. Вход: "+plan.entrance().add(origin)+". Откат: /metronetwork undo "+id);return;}
        MetroNetworkBackup.Change c=changes.get(cursor);String error=unavailable(c.pos);
        if(error!=null || world.state(c.pos)!=c.before || world.tile(c.pos)!=null) {startRestore(error!=null?error:"Мир изменился во время строительства: "+c.pos);return;}
        if(!world.set(c.pos,c.after)) {startRestore("Не удалось установить блок: "+c.pos);return;}
        cursor++;written++;
        if(c.text!=null)world.sign(c.pos,c.text);
    }
    private void startRestore(String reason) {
        if(written==0) {finish(reason+" Мир не изменён.");return;}
        message.accept(reason+" Откат; копия "+id);cursor=written-1;phase=Phase.RESTORE;
    }
    private void restore() {
        if(cursor<0) {finish("Откат завершён: "+restored+" восстановлено, "+skipped+" пропущено (изменены игроками / недоступны). Копия сохранена: "+id);return;}
        MetroNetworkBackup.Change c=changes.get(cursor--);
        if(unavailable(c.pos)!=null || world.state(c.pos)!=c.after || !authoredTile(world.tile(c.pos),c.text)) {skipped++;return;}
        if(world.set(c.pos,c.before))restored++;else skipped++;
    }
    static boolean authoredTile(TileEntity te,String[] text) {
        if(text==null)return te==null;
        if(!(te instanceof TileEntitySign))return false;TileEntitySign sign=(TileEntitySign)te;
        for(int i=0;i<4;i++)if(!text[i].equals(sign.signText[i].getUnformattedText()))return false;
        return true;
    }
    private void fail(String reason) {
        if(phase==Phase.RESTORE || phase==Phase.CHECK)finish(reason+(written>0?" Используйте /metronetwork undo "+id:" Мир не изменён."));
        else startRestore(reason);
    }
    private void finish(String text) {phase=Phase.DONE;message.accept(text);}
}
