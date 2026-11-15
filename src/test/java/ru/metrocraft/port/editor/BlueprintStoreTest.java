package ru.metrocraft.port.editor;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import static org.junit.Assert.*;

public class BlueprintStoreTest {
    @Rule public TemporaryFolder temp=new TemporaryFolder();
    private final UUID player=UUID.fromString("11111111-1111-1111-1111-111111111111");
    private Blueprint bp(){return new Blueprint(1,1,1,new Blueprint.Spec[]{new Blueprint.Spec("minecraft:stone",0)});}
    private Path path(String name){return temp.getRoot().toPath().resolve("metroport-blueprints").resolve(player.toString()).resolve(name+".json");}
    private BlueprintStore store()throws IOException{return new BlueprintStore(temp.getRoot().toPath(),player);}
    private void refuses(IO action)throws Exception{try{action.run();fail("Should refuse");}catch(IOException expected){}}
    private interface IO{void run()throws Exception;}
    @Test public void saveLoadPreservesStableBlockIdentifier()throws Exception{BlueprintStore s=store();s.save("room",bp());assertEquals(bp().get(0,0,0),s.load("room").get(0,0,0));}
    @Test public void noOverwriteOfExistingBlueprint()throws Exception{BlueprintStore s=store();s.save("room",bp());byte[] old=Files.readAllBytes(path("room"));refuses(()->s.save("room",bp()));assertArrayEquals(old,Files.readAllBytes(path("room")));}
    @Test public void rejectsTraversalAndAbsolutePaths()throws Exception{BlueprintStore s=store();for(String n:new String[]{"../world","/tmp/world","a/b","a\\b",".","","x.json"}){refuses(()->s.save(n,bp()));refuses(()->s.load(n));}}
    @Test public void missingFileDoesNotGeneratePlaceholder()throws Exception{BlueprintStore s=store();refuses(()->s.load("missing"));assertFalse(Files.exists(path("missing")));}
    @Test public void invalidAndUnsupportedVersionsAreRefused()throws Exception{BlueprintStore s=store();Files.write(path("bad"),"{\"version\":2}".getBytes("UTF-8"));refuses(()->s.load("bad"));Files.write(path("bad"),"not json".getBytes("UTF-8"));refuses(()->s.load("bad"));}
    @Test public void fractionalDimensionsAreRefused()throws Exception{BlueprintStore s=store();s.save("room",bp());String text=new String(Files.readAllBytes(path("room")),"UTF-8").replace("\"width\":1","\"width\":1.5");Files.write(path("room"),text.getBytes("UTF-8"));refuses(()->s.load("room"));}
    @Test public void oversizedInputIsRefused()throws Exception{BlueprintStore s=store();Files.write(path("bad"),new byte[BlueprintStore.MAX_BYTES+1]);refuses(()->s.load("bad"));}
    @Test public void foreignPlayerCannotLoadAnotherPlayersFile()throws Exception{BlueprintStore s=store();s.save("room",bp());BlueprintStore other=new BlueprintStore(temp.getRoot().toPath(),UUID.randomUUID());refuses(()->other.load("room"));}
    @Test public void fileCountQuotaIsEnforced()throws Exception{BlueprintStore s=store();for(int i=0;i<32;i++)s.save("room"+i,bp());refuses(()->s.save("room32",bp()));}
    @Test public void fileSymlinkCannotBeReadOrOverwritten()throws Exception{BlueprintStore s=store();Path outside=temp.newFile("outside").toPath();Files.createSymbolicLink(path("link"),outside);refuses(()->s.load("link"));refuses(()->s.save("link",bp()));assertEquals(0,Files.size(outside));}
    @Test public void directorySymlinkIsRefused()throws Exception{Path world=temp.newFolder("world").toPath(),outside=temp.newFolder("outside").toPath();Files.createSymbolicLink(world.resolve("metroport-blueprints"),outside);refuses(()->new BlueprintStore(world,player));}
    @Test public void fullMaximumBlueprintFitsByteLimit()throws Exception{BlueprintStore s=store();Blueprint.Spec[] a=new Blueprint.Spec[4096];Arrays.fill(a,bp().get(0,0,0));s.save("big",new Blueprint(16,16,16,a));assertEquals(4096,s.load("big").size());}
}
