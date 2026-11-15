package ru.metrocraft.port.editor;
import org.junit.Test;
import static org.junit.Assert.*;
public class PreviewRulesTest {
    @Test public void expiresAfterTenSecondsIncludingExactDeadline(){long d=PreviewRules.deadline(100);assertTrue(PreviewRules.alive(100,d));assertTrue(PreviewRules.alive(d-1,d));assertFalse(PreviewRules.alive(d,d));assertFalse(PreviewRules.alive(d+1,d));}
    @Test public void nanoTimeWrapDoesNotExtendLifetime(){long start=Long.MAX_VALUE-10,d=PreviewRules.deadline(start);assertTrue(PreviewRules.alive(start,d));assertFalse(PreviewRules.alive(d,d));}
    @Test public void previewRequiresSameWorldOwnerCreativeAndFresh(){assertTrue(PreviewRules.show(true,true,true,false,false,false,true));assertFalse(PreviewRules.show(false,true,true,false,false,false,true));assertFalse(PreviewRules.show(true,false,true,false,false,false,true));assertFalse(PreviewRules.show(true,true,true,false,false,false,false));}
    @Test public void deadSpectatorF1AndMenusHidePreview(){assertFalse(PreviewRules.show(true,true,false,false,false,false,true));assertFalse(PreviewRules.show(true,true,true,true,false,false,true));assertFalse(PreviewRules.show(true,true,true,false,true,false,true));assertFalse(PreviewRules.show(true,true,true,false,false,true,true));}
    @Test public void boundsRejectOverflowAndWorldHeightViolations(){assertFalse(PreviewRules.bounds(Integer.MAX_VALUE,64,0,1,1,1));assertFalse(PreviewRules.bounds(29999999,64,0,2,1,1));assertFalse(PreviewRules.bounds(0,-1,0,1,1,1));assertFalse(PreviewRules.bounds(0,255,0,1,2,1));assertFalse(PreviewRules.bounds(0,0,0,32,32,32));}
    @Test public void legalBoundaryCellsRemainAvailable(){assertTrue(PreviewRules.bounds(-29999999,0,-29999999,1,1,1));assertTrue(PreviewRules.bounds(29999999,255,29999999,1,1,1));assertTrue(PreviewRules.bounds(0,0,0,16,16,16));}
}
