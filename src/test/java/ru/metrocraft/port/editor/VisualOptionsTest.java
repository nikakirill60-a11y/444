package ru.metrocraft.port.editor;
import ru.metrocraft.port.metromod.VisualOptions;
import org.junit.Test;
import static org.junit.Assert.*;
public class VisualOptionsTest {
    @Test public void defaultsPreserveDev21Appearance(){VisualOptions v=new VisualOptions();assertTrue(v.mask&&v.goggles&&v.noise&&v.tint);assertEquals(1F,v.maskOpacity,0);assertEquals(.035F,v.noiseStrength,0);}
    @Test public void draftIsIndependentUntilApplied(){VisualOptions original=new VisualOptions(),draft=original.copy();draft.mask=false;draft.noiseStrength=0;assertTrue(original.mask);assertEquals(.035F,original.noiseStrength,0);}
    @Test public void nonFiniteValuesFallBackSafely(){VisualOptions v=new VisualOptions();v.maskOpacity=Float.NaN;v.noiseStrength=Float.POSITIVE_INFINITY;v.normalize();assertEquals(1F,v.maskOpacity,0);assertEquals(.035F,v.noiseStrength,0);}
    @Test public void opacityAndNoiseAreBounded(){VisualOptions v=new VisualOptions();v.maskOpacity=-100;v.noiseStrength=100;v.normalize();assertEquals(0,v.maskOpacity,0);assertEquals(.2F,v.noiseStrength,0);}
    @Test public void fullVisualDisableDoesNotRequireRemovingEquipment(){VisualOptions v=new VisualOptions();v.mask=v.goggles=v.noise=v.tint=false;v.normalize();assertFalse(v.mask||v.goggles||v.noise||v.tint);}
}
