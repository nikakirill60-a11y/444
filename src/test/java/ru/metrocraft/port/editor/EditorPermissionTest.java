package ru.metrocraft.port.editor;
import net.minecraft.command.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.MinecraftServer;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EditorPermissionTest {
    @BeforeClass public static void boot(){net.minecraft.init.Bootstrap.register();}
    private EntityPlayerMP player(boolean op,boolean creative,boolean spectator,boolean alive)throws Exception{
        EntityPlayerMP p=mock(EntityPlayerMP.class);java.lang.reflect.Field caps=EntityPlayer.class.getField("capabilities");caps.setAccessible(true);caps.set(p,new PlayerCapabilities());
        p.capabilities.isCreativeMode=creative;when(p.canUseCommand(2,"metroedit")).thenReturn(op);when(p.isSpectator()).thenReturn(spectator);when(p.isEntityAlive()).thenReturn(alive);return p;
    }
    private void denied(EntityPlayerMP p)throws Exception{
        try{new MetroEditCommand().execute(mock(MinecraftServer.class),p,new String[]{"confirm"});fail("Must refuse before accessing world/session");}
        catch(CommandException expected){assertTrue(expected.getMessage().contains("Creative"));}
    }
    @Test public void creativeWithoutOpIsDenied()throws Exception{denied(player(false,true,false,true));}
    @Test public void opInSurvivalIsDenied()throws Exception{denied(player(true,false,false,true));}
    @Test public void spectatorOperatorIsDenied()throws Exception{denied(player(true,true,true,true));}
    @Test public void deadOperatorIsDenied()throws Exception{denied(player(true,true,false,false));}
    @Test(expected=CommandException.class) public void consoleCannotEditWithoutPlayerContext()throws Exception{new MetroEditCommand().execute(mock(MinecraftServer.class),mock(ICommandSender.class),new String[]{"confirm"});}
}
