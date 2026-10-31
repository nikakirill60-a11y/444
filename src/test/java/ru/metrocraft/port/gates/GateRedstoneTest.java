package ru.metrocraft.port.gates;

import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.UUID;
import static org.junit.Assert.*;

public class GateRedstoneTest {
    @BeforeClass public static void setup() { Bootstrap.register(); }
    private float target(GateTile tile)throws Exception {
        Field field=GateTile.class.getDeclaredField("target");field.setAccessible(true);return field.getFloat(tile);
    }
    @Test public void allVariantsInstallClosedEvenHistoricallyOpenVariants()throws Exception {
        for(GateType type:GateType.values()) {
            GateTile tile=new GateTile();tile.configure(UUID.randomUUID(),type,EnumFacing.SOUTH);
            assertEquals(0,tile.progress,0);assertEquals(0,target(tile),0);
        }
    }
    @Test public void legacyManualTargetCannotHoldAnUnpoweredGateOpen()throws Exception {
        NBTTagCompound tag=new NBTTagCompound();tag.setString("Type","OPEN");tag.setFloat("Progress",1);
        tag.setFloat("Manual",1);tag.setFloat("Target",1);tag.setBoolean("Powered",false);
        GateTile tile=new GateTile();tile.readFromNBT(tag);
        assertEquals(1,tile.progress,0); // Preserve leaf position; closing must still obey collision protection.
        assertEquals(0,target(tile),0);
        tag.setBoolean("Powered",true);tile.readFromNBT(tag);assertEquals(1,target(tile),0);
        assertEquals(0,GateTile.redstoneTarget(false),0);assertEquals(1,GateTile.redstoneTarget(true),0);
    }
    @Test public void clickingHasNoControlPathAndDoesNotRequireAPlayerOrWorld() {
        for(GateType type:GateType.values())assertFalse(new GateBlock(type).onBlockActivated(null,null,null,null,EnumHand.MAIN_HAND,EnumFacing.SOUTH,0,0,0));
    }
}
