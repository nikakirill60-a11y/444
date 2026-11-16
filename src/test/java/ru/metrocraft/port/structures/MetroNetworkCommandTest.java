package ru.metrocraft.port.structures;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.*;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MetroNetworkCommandTest {
    @BeforeClass public static void boot() {Bootstrap.register();}
    @After public void cleanup() {MetroNetworkCommand.reset();}
    @Test public void permissionAndUsageDoNotEnableAccidentalWorldgen() {
        MetroNetworkCommand command=new MetroNetworkCommand();assertEquals(2,command.getRequiredPermissionLevel());assertEquals("metronetwork",command.getName());assertTrue(command.getUsage(null).contains("plan"));
        assertEquals(Arrays.asList("plan"),command.getTabCompletions(null,null,new String[]{"pl"},BlockPos.ORIGIN));
    }
    @Test public void unprivilegedPlayerIsRejectedBeforeWorldAccess() throws Exception {
        MinecraftServer server=mock(MinecraftServer.class);EntityPlayerMP player=mock(EntityPlayerMP.class);MetroNetworkCommand command=new MetroNetworkCommand();
        try {command.execute(server,player,new String[]{"plan"});fail();}catch(CommandException expected) {}
        verify(player,never()).getServerWorld();
    }
    @Test public void previewDoesNotReadOrWriteTerrainAndFreezesOriginUntilConfirmation() throws Exception {
        MinecraftServer server=mock(MinecraftServer.class);EntityPlayerMP player=mock(EntityPlayerMP.class);WorldServer world=mock(WorldServer.class);java.lang.reflect.Field provider=World.class.getField("provider");provider.setAccessible(true);provider.set(world,mock(WorldProvider.class));
        when(player.canUseCommand(2,"metronetwork")).thenReturn(true);when(player.getServerWorld()).thenReturn(world);when(player.getPosition()).thenReturn(new BlockPos(0,64,0));when(player.getUniqueID()).thenReturn(UUID.randomUUID());
        MetroNetworkCommand command=new MetroNetworkCommand();command.execute(server,player,new String[]{"plan"});
        verify(world,never()).getBlockState(any(BlockPos.class));verify(world,never()).setBlockState(any(),any(),anyInt());
        ArgumentCaptor<ITextComponent> text=ArgumentCaptor.forClass(ITextComponent.class);verify(player,atLeastOnce()).sendMessage(text.capture());
        assertTrue(text.getAllValues().stream().anyMatch(t->t.getUnformattedText().contains("build ")));
        try {command.execute(server,player,new String[]{"build","wrong-token"});fail();}catch(CommandException expected) {}
        verify(world,never()).setBlockState(any(),any(),anyInt());
    }
    @Test public void missingArgumentsAndUnknownOperationCannotStartBuild() throws Exception {
        MinecraftServer server=mock(MinecraftServer.class);EntityPlayerMP player=mock(EntityPlayerMP.class);when(player.canUseCommand(2,"metronetwork")).thenReturn(true);
        for(String[] args:new String[][]{{},{"build"},{"undo"},{"anything"}}) {
            try {new MetroNetworkCommand().execute(server,player,args);fail();}catch(CommandException expected) {}
        }
        verify(player,never()).getServerWorld();
    }
}
