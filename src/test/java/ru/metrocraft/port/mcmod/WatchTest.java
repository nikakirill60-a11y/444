package ru.metrocraft.port.mcmod;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import org.junit.*;
import ru.metrocraft.port.*;
import ru.metrocraft.port.inventory.WatchEquipment;
import java.io.InputStream;
import java.util.*;
import static org.junit.Assert.*;
public class WatchTest {
    @BeforeClass public static void setup(){Bootstrap.register();}
    @Test public void worldClockWrapsAtMidnightAndSupportsNegativeAndHugeTimes(){
        assertEquals("06:00",WatchItem.time(0));assertEquals("12:00",WatchItem.time(6000));
        assertEquals("18:00",WatchItem.time(12000));assertEquals("00:00",WatchItem.time(18000));
        assertEquals("05:59",WatchItem.time(23999));assertEquals(WatchItem.time(0),WatchItem.time(24000));
        assertEquals("05:59",WatchItem.time(-1));assertTrue(WatchItem.minutes(Long.MAX_VALUE)>=0);
        for(long time=-48000;time<48000;time+=137)assertEquals(WatchItem.time(time),WatchItem.time(time+24000));
    }
    @Test public void watchIsAnIndependentSingleItemNotAHelmetOrGunUpgrade(){
        ItemStack watch=new ItemStack(new WatchItem());assertEquals(1,watch.getMaxStackSize());
        assertTrue(WatchEquipment.accepts(watch));assertFalse(WatchEquipment.accepts(ItemStack.EMPTY));
        Definition[] guns=McmodWeapons.definitions();assertFalse(WatchEquipment.accepts(new ItemStack(new McmodAkItem(guns[0]))));
    }
    @Test public void fiveOriginalWatchMeshesArePresentAndNoArmOrWeaponWasCopied()throws Exception{
        try(InputStream in=getClass().getResourceAsStream("/assets/metroport/models/mcmod/watch.obj")){
            McmodObjMesh mesh=McmodObjMesh.read(in);
            assertEquals(new HashSet<>(Arrays.asList("clock","num1","num2","num3","num4")),mesh.groups.keySet());
            for(List<float[]> group:mesh.groups.values()){
                assertFalse(group.isEmpty());for(float[] v:group)for(float f:v)assertTrue(Float.isFinite(f));
            }
        }
    }
    @Test public void remainingAmmoAndFourTechnicalBlocksAreDeclaredWithoutAddingOtherGuns(){
        Map<String,Integer> counts=new HashMap<>();Set<String> ids=new HashSet<>();
        for(Definition d:McmodSupport.definitions()){assertTrue(ids.add(d.id));counts.put(d.kind,counts.getOrDefault(d.kind,0)+1);}
        assertEquals(11,ids.size());assertEquals(Integer.valueOf(6),counts.get("ammo"));
        for(String kind:new String[]{"watch","air","radiation","collision","respawn"})assertEquals(Integer.valueOf(1),counts.get(kind));
        assertFalse(counts.containsKey("gun"));
    }
}
