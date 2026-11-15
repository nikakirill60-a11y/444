package ru.metrocraft.port.editor;
import net.minecraft.util.math.BlockPos;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class EditSessionTest {
    private static final Blueprint.Spec AIR=new Blueprint.Spec("minecraft:air",0),STONE=new Blueprint.Spec("minecraft:stone",0),DIRT=new Blueprint.Spec("minecraft:dirt",0);
    private final BlockPos a=BlockPos.ORIGIN,b=new BlockPos(1,0,0),target=new BlockPos(10,0,0);
    private static class Memory implements EditSession.Access {
        final Map<BlockPos,Blueprint.Spec> blocks=new HashMap<>();BlockPos denied,unloaded,fail;boolean failOnce=true;int writes;
        public Blueprint.Spec read(BlockPos p){if(p.equals(unloaded))throw new IllegalArgumentException("unloaded");return blocks.getOrDefault(p,AIR);}
        public void authorize(BlockPos p,Blueprint.Spec old,Blueprint.Spec next){if(p.equals(denied))throw new IllegalArgumentException("protected");}
        public void write(BlockPos p,Blueprint.Spec s){blocks.put(p,s);writes++;if(p.equals(fail) && failOnce){failOnce=false;throw new IllegalStateException("failure after mutation");}}
    }
    private Blueprint blueprint(Blueprint.Spec... cells){return new Blueprint(cells.length,1,1,cells);}
    private void fails(Runnable run){try{run.run();fail("Expected refusal");}catch(IllegalArgumentException|IllegalStateException expected){}}
    @Test public void noClipboardAndNoSelectionAreExplicit(){EditSession s=new EditSession();fails(s::clipboard);fails(()->s.copy(new Memory()));}
    @Test public void bothSelectionCornersAreRequired(){EditSession s=new EditSession();s.select(true,a);fails(()->s.copy(new Memory()));}
    @Test public void reversedSelectionCopiesInclusiveVolume(){Memory m=new Memory();m.blocks.put(a,STONE);m.blocks.put(b,DIRT);EditSession s=new EditSession();s.select(true,b);s.select(false,a);s.copy(m);assertEquals(2,s.clipboard().size());assertEquals(STONE,s.clipboard().get(0,0,0));}
    @Test public void tooLargeSelectionIsRefusedWithoutWrites(){Memory m=new Memory();EditSession s=new EditSession();s.select(true,a);s.select(false,new BlockPos(32,0,0));fails(()->s.copy(m));assertEquals(0,m.writes);}
    @Test public void coordinateOverflowCannotBecomeSmallSelection(){EditSession s=new EditSession();s.select(true,new BlockPos(Integer.MIN_VALUE,0,0));s.select(false,new BlockPos(Integer.MAX_VALUE,0,0));fails(()->s.copy(new Memory()));}
    @Test public void failedCopyKeepsOldClipboard(){Memory m=new Memory();m.unloaded=b;EditSession s=new EditSession();s.clipboard(blueprint(DIRT));s.select(true,a);s.select(false,b);fails(()->s.copy(m));assertEquals(DIRT,s.clipboard().get(0,0,0));}
    @Test public void clipboardIsAnImmutableSnapshot(){Memory m=new Memory();m.blocks.put(a,STONE);EditSession s=new EditSession();s.select(true,a);s.select(false,a);s.copy(m);m.blocks.put(a,DIRT);assertEquals(STONE,s.clipboard().get(0,0,0));}
    @Test public void constructorDefensivelyCopiesArray(){Blueprint.Spec[] data={STONE};Blueprint bp=new Blueprint(1,1,1,data);data[0]=DIRT;assertEquals(STONE,bp.get(0,0,0));}
    @Test public void pasteSkipsAirAndDoesNotEraseDestination(){Memory m=new Memory();m.blocks.put(target.add(1,0,0),DIRT);EditSession s=new EditSession();s.clipboard(blueprint(STONE,AIR));assertEquals(1,s.paste(m,target));assertEquals(DIRT,m.read(target.add(1,0,0)));}
    @Test public void deniedLastCellPreventsAllWrites(){Memory m=new Memory();m.denied=target.add(1,0,0);EditSession s=new EditSession();s.clipboard(blueprint(STONE,DIRT));fails(()->s.paste(m,target));assertEquals(0,m.writes);}
    @Test public void unloadedLastCellPreventsAllWrites(){Memory m=new Memory();m.unloaded=target.add(1,0,0);EditSession s=new EditSession();s.clipboard(blueprint(STONE,DIRT));fails(()->s.paste(m,target));assertEquals(0,m.writes);}
    @Test public void partialWriteFailureRollsBackEvenFailedCell(){Memory m=new Memory();m.fail=target.add(1,0,0);EditSession s=new EditSession();s.clipboard(blueprint(STONE,DIRT));fails(()->s.paste(m,target));assertEquals(AIR,m.read(target));assertEquals(AIR,m.read(m.fail));fails(()->s.undo(m));}
    @Test public void undoRestoresPreviousBlocksOnce(){Memory m=new Memory();m.blocks.put(target,DIRT);EditSession s=new EditSession();s.clipboard(blueprint(STONE));s.paste(m,target);assertEquals(1,s.undo(m));assertEquals(DIRT,m.read(target));fails(()->s.undo(m));}
    @Test public void undoRefusesToOverwriteAnotherPlayersChange(){Memory m=new Memory();EditSession s=new EditSession();s.clipboard(blueprint(STONE));s.paste(m,target);m.blocks.put(target,DIRT);fails(()->s.undo(m));assertEquals(DIRT,m.read(target));}
    @Test public void undoRechecksPermissionsAndKeepsHistoryOnDenial(){Memory m=new Memory();EditSession s=new EditSession();s.clipboard(blueprint(STONE));s.paste(m,target);m.denied=target;fails(()->s.undo(m));m.denied=null;assertEquals(1,s.undo(m));}
    @Test public void unchangedPasteDoesNotConsumeUndo(){Memory m=new Memory();EditSession s=new EditSession();s.clipboard(blueprint(STONE));s.paste(m,target);assertEquals(0,s.paste(m,target));assertEquals(1,s.undo(m));}
    @Test public void playersHaveIndependentClipboardAndUndo(){EditSession one=new EditSession(),two=new EditSession();Memory m=new Memory();one.clipboard(blueprint(STONE));one.paste(m,target);fails(two::clipboard);fails(()->two.undo(m));assertEquals(1,one.undo(m));}
    @Test public void rotation90SwapsFootprintWithoutLosingCells(){Blueprint bp=new Blueprint(2,1,1,new Blueprint.Spec[]{STONE,DIRT});Blueprint rotated=bp.rotate(1,(s,t)->s);assertEquals(1,rotated.width);assertEquals(2,rotated.depth);assertEquals(STONE,rotated.get(0,0,0));assertEquals(DIRT,rotated.get(0,0,1));}
    @Test public void fourRotationsRestoreAllCoordinates(){Blueprint bp=new Blueprint(2,2,3,new Blueprint.Spec[]{STONE,DIRT,AIR,STONE,AIR,DIRT,DIRT,STONE,AIR,DIRT,STONE,AIR});Blueprint r=bp;for(int i=0;i<4;i++)r=r.rotate(1,(s,t)->s);for(int y=0;y<2;y++)for(int z=0;z<3;z++)for(int x=0;x<2;x++)assertEquals(bp.get(x,y,z),r.get(x,y,z));}
    @Test public void rotationNeverChangesSource(){Blueprint bp=blueprint(STONE,DIRT);bp.rotate(2,(s,t)->s);assertEquals(STONE,bp.get(0,0,0));}
    @Test public void rotationCallsBlockOrientationAdapter(){List<Integer> turns=new ArrayList<>();blueprint(STONE,DIRT).rotate(3,(s,t)->{turns.add(t);return s;});assertEquals(Arrays.asList(3,3),turns);}
    @Test public void invalidDimensionsAndCellCountsAreRefused(){fails(()->new Blueprint(0,1,1,new Blueprint.Spec[0]));fails(()->new Blueprint(32,32,32,new Blueprint.Spec[0]));fails(()->new Blueprint(1,1,1,new Blueprint.Spec[2]));fails(()->new Blueprint(1,1,1,new Blueprint.Spec[1]));}
    @Test public void invalidMetadataAndIdentifiersAreRefused(){fails(()->new Blueprint.Spec("stone",0));fails(()->new Blueprint.Spec("minecraft:stone",16));fails(()->new Blueprint.Spec("minecraft:stone",-1));fails(()->new Blueprint.Spec(null,0));}
    @Test public void entireMaximumVolumeIsAccepted(){assertEquals(4096,Blueprint.volume(16,16,16));}
}
