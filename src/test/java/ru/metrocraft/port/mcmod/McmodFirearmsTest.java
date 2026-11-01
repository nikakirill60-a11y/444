package ru.metrocraft.port.mcmod;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.junit.*;
import ru.metrocraft.port.*;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
public class McmodFirearmsTest {
    @BeforeClass public static void bootstrap(){Bootstrap.register();}
    private EntityPlayer player;
    private Item metal,chem;
    private McmodAkItem item;
    private ItemStack gun;
    @Before public void setup(){
        player=mock(EntityPlayer.class);player.world=mock(World.class);player.inventory=new InventoryPlayer(player);
        when(player.getHeldItemMainhand()).thenAnswer(inv->player.inventory.getCurrentItem());when(player.isEntityAlive()).thenReturn(true);
        metal=new Item();chem=new Item();MetroPort.ITEMS.put("mcmod_res_metall",metal);MetroPort.ITEMS.put("mcmod_res_xim",chem);
        equip("mcmod_ak74");
    }
    private void equip(String id){
        item=new McmodAkItem(Arrays.stream(McmodWeapons.definitions()).filter(d->d.id.equals(id)).findFirst().get());
        gun=new ItemStack(item);gun.setTagCompound(new NBTTagCompound());gun.setStackDisplayName("Preserved");gun.getTagCompound().setBoolean("NoDrop",true);
        player.inventory.mainInventory.set(0,gun);
    }
    private void resources(int m,int c){
        for(int i=1;i<36;i++)player.inventory.mainInventory.set(i,ItemStack.EMPTY);
        int slot=1;while(m>0){int n=Math.min(64,m);player.inventory.mainInventory.set(slot++,new ItemStack(metal,n));m-=n;}
        while(c>0){int n=Math.min(64,c);player.inventory.mainInventory.set(slot++,new ItemStack(chem,n));c-=n;}
    }
    @Test public void allFourteenGzipMeshesParseWithTheProductionReader()throws Exception{
        int count=0;Set<String> ids=new HashSet<>();
        for(Definition d:McmodWeapons.definitions()){
            assertTrue(ids.add(d.id));if(!d.kind.equals("gun")||d.id.equals("mcmod_ak74"))continue;
            try(InputStream raw=getClass().getResourceAsStream("/assets/metroport/models/mcmod/guns/"+d.id.substring(6)+".obj.gz");InputStream in=new GZIPInputStream(raw)){
                McmodObjMesh mesh=McmodObjMesh.read(in);assertTrue(d.id,mesh.groups.containsKey("body")||mesh.groups.containsKey("body2"));
                for(List<float[]> part:mesh.groups.values())for(float[] vertex:part)for(float f:vertex)assertTrue(d.id,Float.isFinite(f));
                count++;
            }
        }assertEquals(14,count);
    }
    @Test public void installAndRemovalAreExactAtomicAndCannotBeReplayed(){
        resources(25,5);assertTrue(GunUpgrades.apply(player,gun,1,1));
        assertEquals(0,GunUpgrades.count(player,"mcmod_res_metall"));assertEquals(0,GunUpgrades.count(player,"mcmod_res_xim"));
        assertFalse(GunUpgrades.apply(player,gun,1,1));assertTrue(GunUpgrades.apply(player,gun,1,0));
        assertEquals(8,GunUpgrades.count(player,"mcmod_res_metall"));assertEquals(4,GunUpgrades.count(player,"mcmod_res_xim"));
        assertFalse(GunUpgrades.apply(player,gun,1,0));assertEquals(8,GunUpgrades.count(player,"mcmod_res_metall"));
        assertEquals("Preserved",gun.getDisplayName());assertTrue(gun.getTagCompound().getBoolean("NoDrop"));assertSame(gun,player.getHeldItemMainhand());
    }
    @Test public void insufficientResourcesNeverPartiallyCharge(){
        resources(50,29);assertFalse(GunUpgrades.apply(player,gun,2,1));assertEquals(50,GunUpgrades.count(player,"mcmod_res_metall"));assertEquals(29,GunUpgrades.count(player,"mcmod_res_xim"));assertEquals(0,McmodAkItem.module(gun,"modScope"));
    }
    @Test public void repairUsesTheDisplayedPriceNotTheOriginalOffByOneBug(){
        resources(120,50);gun.setItemDamage(1600);assertTrue(GunUpgrades.apply(player,gun,5,1));assertEquals(0,gun.getItemDamage());
        assertEquals(1600,gun.getTagCompound().getInteger("condition"));assertEquals(0,GunUpgrades.count(player,"mcmod_res_metall"));assertFalse(GunUpgrades.apply(player,gun,5,1));
    }
    @Test public void magazineRemovalReturnsTheFifteenSurplusRounds(){
        Item bullet=new Item();MetroPort.ITEMS.put(item.getDefinition().ammo,bullet);resources(15,10);
        assertTrue(GunUpgrades.apply(player,gun,3,1));assertEquals(45,item.capacity(gun));gun.getTagCompound().setInteger("MetroAmmo",45);
        assertTrue(GunUpgrades.apply(player,gun,3,0));assertEquals(30,item.ammo(gun));assertEquals(30,item.capacity(gun));
        assertEquals(15,GunUpgrades.count(player,item.getDefinition().ammo));
    }
    @Test public void fullInventoryCancelsRemovalInsteadOfDeletingAmmoOrRefunds(){
        gun.getTagCompound().setInteger("modMag",1);gun.getTagCompound().setInteger("MetroAmmo",45);
        for(int i=1;i<36;i++)player.inventory.mainInventory.set(i,new ItemStack(Items.STICK,64));
        assertFalse(GunUpgrades.apply(player,gun,3,0));assertEquals(45,item.ammo(gun));assertEquals(1,McmodAkItem.module(gun,"modMag"));
    }
    @Test public void invalidAndIncompatibleActionsAreRejected(){
        resources(100,100);for(int page:new int[]{-1,0,6,255})assertFalse(GunUpgrades.apply(player,gun,page,1));
        assertFalse(GunUpgrades.apply(player,gun,2,3));assertFalse(GunUpgrades.apply(player,gun,1,2));
        equip("mcmod_preved");assertFalse(GunUpgrades.apply(player,gun,1,1));assertTrue(GunUpgrades.apply(player,gun,3,1));assertEquals(5,item.capacity(gun));
    }
    @Test public void sourceConditionAndPressureDamageTiersArePreserved(){
        assertEquals(13,item.shotDamage(gun),0);gun.setItemDamage(801);assertEquals(9.75F,item.shotDamage(gun),0);gun.setItemDamage(1201);assertEquals(6.5F,item.shotDamage(gun),0);
        gun.getTagCompound().setInteger("modStvol",1);assertEquals(4.5F,item.shotDamage(gun),0);
        equip("mcmod_tihar");assertFalse(item.canFire(gun));
        for(int p:new int[]{1,8,15,20,30}){gun.getTagCompound().setInteger("davl",p);assertTrue(item.canFire(gun));assertEquals(16*(p>=20?1:p>=15?2F/3:p>=8?.5F:1F/3),item.shotDamage(gun),1e-5F);}
        item.afterShot(gun,player);assertEquals(29,McmodAkItem.pressure(gun));
    }
    @Test public void pumpIsServerRateLimitedAndClamped(){
        equip("mcmod_tihar");when(player.world.getTotalWorldTime()).thenReturn(100L);item.pump(gun,player);item.pump(gun,player);assertEquals(1,McmodAkItem.pressure(gun));
        when(player.world.getTotalWorldTime()).thenReturn(108L);item.pump(gun,player);assertEquals(2,McmodAkItem.pressure(gun));
        gun.getTagCompound().setInteger("davl",30);when(player.world.getTotalWorldTime()).thenReturn(116L);item.pump(gun,player);assertEquals(30,McmodAkItem.pressure(gun));
    }
    @Test public void workbenchBindsExactGunObjectAndSelectedSlot(){
        GunWorkbenchContainer container=new GunWorkbenchContainer(player);assertTrue(container.canInteractWith(player));assertTrue(container.inventorySlots.isEmpty());
        player.inventory.mainInventory.set(0,gun.copy());assertFalse(container.canInteractWith(player));player.inventory.mainInventory.set(0,gun);
        player.inventory.currentItem=1;assertFalse(container.canInteractWith(player));
    }
}
