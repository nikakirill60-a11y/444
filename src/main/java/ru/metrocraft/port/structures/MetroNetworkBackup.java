package ru.metrocraft.port.structures;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Durable undo BEFORE edits. Only exact plan-authored states may be restored. */
public final class MetroNetworkBackup {
    public static final class Change {
        public final BlockPos pos;public final IBlockState before,after;public final String[] text;
        public Change(BlockPos pos,IBlockState before,IBlockState after,String[] text) {this.pos=pos.toImmutable();this.before=before;this.after=after;this.text=text==null?null:text.clone();}
    }
    public final UUID id;public final int dimension;public final List<Change> changes;
    public MetroNetworkBackup(UUID id,int dimension,List<Change> changes) {this.id=id;this.dimension=dimension;this.changes=new ArrayList<>(changes);}
    public File file(File worldDirectory) {return new File(new File(worldDirectory,"minetro/network-backups"),id+".nbt");}
    public void save(File worldDirectory) throws IOException {
        File file=file(worldDirectory);Files.createDirectories(file.getParentFile().toPath());
        Path tmp=file.toPath().resolveSibling(id+".tmp");
        try(OutputStream out=Files.newOutputStream(tmp)) {CompressedStreamTools.writeCompressed(tag(),out);}
        try {Files.move(tmp,file.toPath(),StandardCopyOption.ATOMIC_MOVE);}
        catch(AtomicMoveNotSupportedException e) {Files.move(tmp,file.toPath());}
    }
    public NBTTagCompound tag() {
        NBTTagCompound root=new NBTTagCompound();root.setInteger("Version",1);root.setUniqueId("Id",id);root.setInteger("Dimension",dimension);
        NBTTagList list=new NBTTagList();
        for(Change c:changes) {
            NBTTagCompound entry=new NBTTagCompound();entry.setLong("Pos",c.pos.toLong());
            entry.setTag("Before",NBTUtil.writeBlockState(new NBTTagCompound(),c.before));entry.setTag("After",NBTUtil.writeBlockState(new NBTTagCompound(),c.after));
            if(c.text!=null)for(int i=0;i<4;i++)entry.setString("Text"+i,c.text[i]);
            list.appendTag(entry);
        }
        root.setTag("Changes",list);return root;
    }
    public static MetroNetworkBackup load(File directory,UUID id) throws IOException {
        File file=new File(new File(directory,"minetro/network-backups"),id+".nbt");
        // UUID filenames only; no arbitrary paths accepted from a command.
        if(!file.isFile() || file.length()>32*1024*1024)throw new IOException("Backup missing or too large");
        NBTTagCompound tag;try(InputStream in=new FileInputStream(file)) {tag=CompressedStreamTools.readCompressed(in);}
        if(tag.getInteger("Version")!=1 || !id.equals(tag.getUniqueId("Id")))throw new IOException("Invalid backup header");
        NBTTagList entries=tag.getTagList("Changes",10);if(entries.tagCount()>200000)throw new IOException("Invalid backup size");
        List<Change> changes=new ArrayList<>();Set<BlockPos> seen=new HashSet<>();
        for(int i=0;i<entries.tagCount();i++) {
            NBTTagCompound entry=entries.getCompoundTagAt(i);BlockPos pos=BlockPos.fromLong(entry.getLong("Pos"));
            if(pos.getY()<0 || pos.getY()>255 || !seen.add(pos))throw new IOException("Invalid backup position");
            String[] text=entry.hasKey("Text0")?new String[]{entry.getString("Text0"),entry.getString("Text1"),entry.getString("Text2"),entry.getString("Text3")}:null;
            changes.add(new Change(pos,NBTUtil.readBlockState(entry.getCompoundTag("Before")),NBTUtil.readBlockState(entry.getCompoundTag("After")),text));
        }
        return new MetroNetworkBackup(id,tag.getInteger("Dimension"),changes);
    }
}
