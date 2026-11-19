package ru.metrocraft.port.inventory;

import io.netty.buffer.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GridProtocolTest {
    @BeforeClass public static void bootstrap(){Bootstrap.register();}
    private EntityPlayerMP p;private MetroContainer c;
    @Before public void setup()throws Exception{
        p=mock(EntityPlayerMP.class);p.world=mock(World.class);p.connection=mock(NetHandlerPlayServer.class);p.inventory=new InventoryPlayer(p);
        java.lang.reflect.Field caps=EntityPlayer.class.getField("capabilities");caps.setAccessible(true);caps.set(p,new PlayerCapabilities());
        when(p.getEntityData()).thenReturn(new NBTTagCompound());when(p.isEntityAlive()).thenReturn(true);
        // New equipment migration reads the actual vanilla armor inventory.
        when(p.getItemStackFromSlot(any(net.minecraft.inventory.EntityEquipmentSlot.class))).thenCallRealMethod();
        doCallRealMethod().when(p).setItemStackToSlot(any(net.minecraft.inventory.EntityEquipmentSlot.class),any(ItemStack.class));
        c=new MetroContainer(p);c.windowId=17;p.openContainer=c;
        p.inventory.setItemStack(new ItemStack(Items.STICK,5));c.detectAndSendChanges();
    }
    @Test public void staleAndReplayedClicksCannotPickUpOrDuplicateThePlacedItem(){
        int revision=c.gridRevision;c.gridClick(p,17,revision-1,0,0,false,0);
        assertEquals(5,p.inventory.getItemStack().getCount());assertTrue(p.inventory.getStackInSlot(9).isEmpty());
        c.gridClick(p,17,revision,0,0,false,0);assertTrue(p.inventory.getItemStack().isEmpty());assertEquals(5,p.inventory.getStackInSlot(9).getCount());
        c.gridClick(p,17,revision,0,0,false,0);assertTrue(p.inventory.getItemStack().isEmpty());assertEquals(5,p.inventory.getStackInSlot(9).getCount());
    }
    @Test public void wrongWindowSpectatorClosedAndForeignPlayerCannotMoveItems(){
        c.gridClick(p,18,c.gridRevision,0,0,false,0);assertEquals(5,p.inventory.getItemStack().getCount());
        when(p.isSpectator()).thenReturn(true);c.gridClick(p,17,c.gridRevision,0,0,false,0);assertEquals(5,p.inventory.getItemStack().getCount());when(p.isSpectator()).thenReturn(false);
        c.gridClick(mock(EntityPlayerMP.class),17,c.gridRevision,0,0,false,0);assertEquals(5,p.inventory.getItemStack().getCount());
        p.openContainer=null;c.gridClick(p,17,c.gridRevision,0,0,false,0);assertEquals(5,p.inventory.getItemStack().getCount());
    }
    @Test public void packetContainsOnlyBoundedActionMetadataNotClientOwnedStacks(){
        GridClickPacket before=new GridClickPacket(17,123,63,1,true,0);ByteBuf first=Unpooled.buffer(),second=Unpooled.buffer();
        try{
            before.toBytes(first);assertEquals(15,first.readableBytes());GridClickPacket after=new GridClickPacket();after.fromBytes(first.copy());after.toBytes(second);
            assertEquals(first,second);
        }finally{first.release();second.release();}
    }
    @Test public void propertiesTransmitLayoutThenRevisionToNewListeners(){
        net.minecraft.inventory.IContainerListener listener=mock(net.minecraft.inventory.IContainerListener.class);c.addListener(listener);
        verify(listener,atLeastOnce()).sendWindowProperty(eq(c),eq(127),eq(c.gridRevision));
        c.gridClick(p,17,c.gridRevision,63,0,false,0);
        assertEquals(63,c.gridPositions[0]);verify(listener,atLeastOnce()).sendWindowProperty(c,100,63);
    }
}
