package ru.metrocraft.port;

import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.init.Bootstrap;
import org.junit.*;
import ru.metrocraft.port.exodus.ExodusContent;
import static org.junit.Assert.*;

public class ContentPolicyTest {
    @BeforeClass public static void boot(){Bootstrap.register();}
    @Test public void actualLegacyCatalogueRejectsEveryFirearmAmmoAttachmentAndGunRepairKit()throws Exception{
        try(Reader r=new InputStreamReader(getClass().getResourceAsStream("/assets/metroport/content.json"),StandardCharsets.UTF_8)){
            Definition[] rows=new Gson().fromJson(r,Definition[].class);int kept=0,removed=0;
            for(Definition d:rows){
                boolean weapon="gun".equals(d.kind)||"ammo".equals(d.kind)||"attachment".equals(d.kind)||d.repairGun;
                assertEquals(d.id,!weapon,ContentPolicy.definition(d));if(ContentPolicy.definition(d))kept++;else removed++;
            }
            assertEquals(71,rows.length);assertEquals(28,removed);assertEquals(43,kept);
        }
    }
    @Test public void mcmodRegistrationIsDisabledBeforeItEvenTouchesRegistryOrCreatesAGun(){
        int before=MetroPort.ITEMS.size();ru.metrocraft.port.mcmod.McmodWeapons.items(null);assertEquals(before,MetroPort.ITEMS.size());assertFalse(ContentPolicy.FIREARMS);
    }
    @Test public void onlyTheTwoRequestedExodusMasksAndEveryComplexDecorEntryAreRemoved(){
        ExodusContent.load();Set<String> ids=new HashSet<>();for(ExodusContent.Entry e:ExodusContent.entries){ids.add(e.id);assertNotEquals("decor",e.kind);}
        assertEquals(new HashSet<>(Arrays.asList("exodus_guard","exodus_shrimp","exodus_gasmask_pumpkin")),ids);
        assertFalse(ContentPolicy.exodus("exodus_gasmask_gold","mask"));assertFalse(ContentPolicy.exodus("exodus_gasmask_diamond","mask"));
    }
    @Test public void noSeparateOwnFirearmCreativeTabIsCreated(){assertSame(MetroPort.THINGS,MetroPort.WEAPONS);assertNotSame(MetroPort.THINGS,MetroPort.DECOR);}
}
