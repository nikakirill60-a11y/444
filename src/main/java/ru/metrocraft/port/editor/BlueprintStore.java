package ru.metrocraft.port.editor;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Own JSON format, not a donor .m2033 model/schematic decoder. No absolute paths or client file downloads. */
public final class BlueprintStore {
    public static final int MAX_BYTES=1048576, MAX_FILES=32;
    private final Path directory;
    public BlueprintStore(Path world,UUID player)throws IOException{
        // Resolve every component individually and refuse symlinks, including ancestors.
        Path current=world.toAbsolutePath().normalize();
        for(Path p=current;p!=null;p=p.getParent())if(Files.isSymbolicLink(p))throw new IOException("Symlinked storage is not supported");
        if(!Files.isDirectory(current,LinkOption.NOFOLLOW_LINKS))throw new IOException("World directory missing");
        for(String component:new String[]{"metroport-blueprints",player.toString()}){
            current=current.resolve(component);
            if(Files.isSymbolicLink(current))throw new IOException("Symlinked storage refused");
            Files.createDirectories(current);
        }
        directory=current;
    }
    private Path file(String name)throws IOException{
        if(name==null || !name.matches("[a-zA-Z0-9_-]{1,40}"))throw new IOException("Name: 1-40 letters, digits, _ or -");
        Path p=directory.resolve(name+".json");if(Files.isSymbolicLink(p))throw new IOException("Symlink refused");return p;
    }
    public void save(String name,Blueprint b)throws IOException{
        Path target=file(name);if(Files.exists(target,LinkOption.NOFOLLOW_LINKS))throw new IOException("Name already exists; choose another name");
        try(java.util.stream.Stream<Path> files=Files.list(directory)){
            if(files.count()>=MAX_FILES)throw new IOException("Maximum 32 saved blueprints per player");
        }
        JsonObject root=new JsonObject();root.addProperty("version",1);root.addProperty("width",b.width);root.addProperty("height",b.height);root.addProperty("depth",b.depth);
        JsonArray cells=new JsonArray();
        for(int y=0;y<b.height;y++)for(int z=0;z<b.depth;z++)for(int x=0;x<b.width;x++){
            Blueprint.Spec s=b.get(x,y,z);JsonObject cell=new JsonObject();cell.addProperty("block",s.block);cell.addProperty("meta",s.meta);cells.add(cell);
        }
        root.add("cells",cells);byte[] bytes=root.toString().getBytes(StandardCharsets.UTF_8);
        if(bytes.length>MAX_BYTES)throw new IOException("Blueprint too large");
        // Publish only a fully written file; never replace existing user data.
        Path temp=Files.createTempFile(directory,".saving-",".tmp");
        try{Files.write(temp,bytes);Files.move(temp,target);}finally{Files.deleteIfExists(temp);}
    }
    public Blueprint load(String name)throws IOException{
        Path p=file(name);
        if(!Files.isRegularFile(p,LinkOption.NOFOLLOW_LINKS) || Files.size(p)>MAX_BYTES)throw new IOException("Missing or oversized blueprint");
        byte[] bytes;
        try(InputStream input=Files.newInputStream(p);ByteArrayOutputStream buffer=new ByteArrayOutputStream()){
            byte[] chunk=new byte[8192];int n;while((n=input.read(chunk))!=-1){if(buffer.size()+n>MAX_BYTES)throw new IOException("Blueprint too large");buffer.write(chunk,0,n);}bytes=buffer.toByteArray();
        }
        try{
            JsonObject root=new JsonParser().parse(new String(bytes,StandardCharsets.UTF_8)).getAsJsonObject();
            if(integer(root,"version")!=1)throw new IllegalArgumentException("Unsupported format");
            int w=integer(root,"width"),h=integer(root,"height"),d=integer(root,"depth");int volume=Blueprint.volume(w,h,d);
            JsonArray a=root.getAsJsonArray("cells");if(a.size()!=volume)throw new IllegalArgumentException("Wrong cell count");
            Blueprint.Spec[] data=new Blueprint.Spec[volume];
            for(int i=0;i<volume;i++){JsonObject c=a.get(i).getAsJsonObject();data[i]=new Blueprint.Spec(c.get("block").getAsString(),integer(c,"meta"));}
            return new Blueprint(w,h,d,data);
        }catch(RuntimeException bad){throw new IOException("Invalid blueprint",bad);}
    }
    private static int integer(JsonObject o,String key){return new java.math.BigDecimal(o.get(key).getAsString()).intValueExact();}
}
