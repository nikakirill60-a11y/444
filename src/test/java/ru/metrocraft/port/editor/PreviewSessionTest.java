package ru.metrocraft.port.editor;

import net.minecraft.util.math.BlockPos;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class PreviewSessionTest {
    private static final Blueprint.Spec AIR=new Blueprint.Spec("minecraft:air",0),STONE=new Blueprint.Spec("minecraft:stone",0),DIRT=new Blueprint.Spec("minecraft:dirt",0);
    private static final BlockPos TARGET=new BlockPos(10,64,10);
    private static class Memory implements EditSession.Access {
        final Map<BlockPos,Blueprint.Spec> blocks=new HashMap<>();int writes,authorizations;BlockPos denied,unloaded,fail;
        boolean failed;
        public Blueprint.Spec read(BlockPos p){if(p.equals(unloaded))throw new IllegalArgumentException("unloaded");return blocks.getOrDefault(p,AIR);}
        public void authorize(BlockPos p,Blueprint.Spec old,Blueprint.Spec next){authorizations++;if(p.equals(denied))throw new IllegalArgumentException("protected");}
        public void write(BlockPos p,Blueprint.Spec s){blocks.put(p,s);writes++;if(p.equals(fail)&&!failed){failed=true;throw new IllegalStateException("fail after mutation");}}
    }
    private EditSession session(Blueprint.Spec... specs){EditSession s=new EditSession();s.clipboard(new Blueprint(specs.length,1,1,specs));return s;}
    private void refuses(Runnable action){try{action.run();fail("Expected refusal");}catch(IllegalArgumentException|IllegalStateException expected){}}
    @Test public void previewNeverWritesOrFiresMutationAuthorization(){Memory m=new Memory();EditSession.Preview p=session(STONE).preview(m,TARGET);assertEquals(1,p.changed.size());assertEquals(0,m.writes);assertEquals(0,m.authorizations);assertEquals(AIR,m.read(TARGET));}
    @Test public void confirmRequiresStagedPreview(){Memory m=new Memory();refuses(()->session(STONE).confirm(m));assertEquals(0,m.writes);}
    @Test public void confirmUsesPinnedDestination(){Memory m=new Memory();EditSession s=session(STONE);s.preview(m,TARGET);assertEquals(1,s.confirm(m));assertEquals(STONE,m.read(TARGET));assertEquals(AIR,m.read(BlockPos.ORIGIN));}
    @Test public void onlyActuallyChangedNonAirCellsAreOutlined(){Memory m=new Memory();m.blocks.put(TARGET,STONE);EditSession.Preview p=session(STONE,AIR,DIRT).preview(m,TARGET);assertEquals(Collections.singletonList(TARGET.add(2,0,0)),p.changed);assertEquals(3,p.box.width);}
    @Test public void zeroChangeConfirmationDoesNotWrite(){Memory m=new Memory();m.blocks.put(TARGET,STONE);EditSession s=session(STONE);s.preview(m,TARGET);assertEquals(0,s.confirm(m));assertEquals(0,m.writes);}
    @Test public void concurrentWorldEditRefusesWholeConfirmation(){Memory m=new Memory();EditSession s=session(STONE,STONE);s.preview(m,TARGET);m.blocks.put(TARGET.add(1,0,0),DIRT);refuses(()->s.confirm(m));assertEquals(0,m.writes);assertEquals(DIRT,m.read(TARGET.add(1,0,0)));}
    @Test public void permissionsAreRecheckedAtConfirmation(){Memory m=new Memory();EditSession s=session(STONE,STONE);s.preview(m,TARGET);m.denied=TARGET.add(1,0,0);refuses(()->s.confirm(m));assertEquals(0,m.writes);}
    @Test public void chunkUnloadedAfterPreviewRefusesConfirmation(){Memory m=new Memory();EditSession s=session(STONE);s.preview(m,TARGET);m.unloaded=TARGET;refuses(()->s.confirm(m));assertEquals(0,m.writes);}
    @Test public void changingClipboardInvalidatesOldPreview(){Memory m=new Memory();EditSession s=session(STONE);s.preview(m,TARGET);s.clipboard(new Blueprint(1,1,1,new Blueprint.Spec[]{DIRT}));refuses(()->s.confirm(m));}
    @Test public void changingSelectionInvalidatesOldPreview(){Memory m=new Memory();EditSession s=session(STONE);s.preview(m,TARGET);s.select(true,BlockPos.ORIGIN);refuses(()->s.confirm(m));}
    @Test public void failedNewPreviewCannotLeaveOldPlanArmed(){Memory m=new Memory();EditSession s=session(STONE);s.preview(m,TARGET);m.unloaded=BlockPos.ORIGIN;refuses(()->s.preview(m,BlockPos.ORIGIN));refuses(()->s.confirm(m));}
    @Test public void cancellationKeepsClipboardAndExistingUndo(){Memory m=new Memory();EditSession s=session(STONE);s.paste(m,BlockPos.ORIGIN);s.preview(m,TARGET);s.cancelPreview();refuses(()->s.confirm(m));assertEquals(1,s.clipboard().size());assertEquals(1,s.undo(m));}
    @Test public void successfulConfirmationCanBeUndoneOnlyOnce(){Memory m=new Memory();EditSession s=session(STONE);s.preview(m,TARGET);s.confirm(m);assertEquals(1,s.undo(m));assertEquals(AIR,m.read(TARGET));refuses(()->s.undo(m));}
    @Test public void confirmationIsSingleUse(){Memory m=new Memory();EditSession s=session(STONE);s.preview(m,TARGET);s.confirm(m);refuses(()->s.confirm(m));assertEquals(1,m.writes);}
    @Test public void failedWriteRollsBackConfirmedPreview(){Memory m=new Memory();EditSession s=session(STONE,DIRT);s.preview(m,TARGET);m.fail=TARGET.add(1,0,0);refuses(()->s.confirm(m));assertEquals(AIR,m.read(TARGET));assertEquals(AIR,m.read(m.fail));}
    @Test public void directPasteInvalidatesPendingConfirmation(){Memory m=new Memory();EditSession s=session(STONE);s.preview(m,TARGET);s.paste(m,BlockPos.ORIGIN);refuses(()->s.confirm(m));assertEquals(AIR,m.read(TARGET));}
    @Test public void selectionBoxIsInclusiveAndNormalizesCorners(){EditSession s=new EditSession();s.select(true,new BlockPos(3,4,5));s.select(false,new BlockPos(1,2,3));EditSession.Box b=s.selection();assertEquals(new BlockPos(1,2,3),b.origin);assertEquals(3,b.width);assertEquals(3,b.height);assertEquals(3,b.depth);}
    @Test public void selectionBoxRejectsMissingAndOversizedSelection(){EditSession s=new EditSession();refuses(s::selection);s.select(true,BlockPos.ORIGIN);s.select(false,new BlockPos(32,0,0));refuses(s::selection);}
    @Test public void mirrorsBothAxesWithoutMutatingOriginal(){Blueprint p=new Blueprint(2,1,2,new Blueprint.Spec[]{STONE,DIRT,AIR,STONE});Blueprint x=p.mirror(true,(s,a)->s),z=p.mirror(false,(s,a)->s);assertEquals(DIRT,x.get(0,0,0));assertEquals(AIR,z.get(0,0,0));assertEquals(STONE,p.get(0,0,0));for(boolean axis:new boolean[]{true,false}){Blueprint twice=p.mirror(axis,(s,a)->s).mirror(axis,(s,a)->s);for(int i=0;i<2;i++)for(int j=0;j<2;j++)assertEquals(p.get(i,0,j),twice.get(i,0,j));}}
    @Test public void mirroredOrientationAdapterReceivesCorrectAxis(){List<Boolean> axes=new ArrayList<>();Blueprint p=new Blueprint(1,1,1,new Blueprint.Spec[]{STONE});p.mirror(true,(s,a)->{axes.add(a);return s;});p.mirror(false,(s,a)->{axes.add(a);return s;});assertEquals(Arrays.asList(true,false),axes);}
}
