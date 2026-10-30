/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.event.ConfigChangedEvent$OnConfigChangedEvent
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.event.FMLServerStartedEvent
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.network.IGuiHandler
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.common.registry.EntityRegistry
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.command.CommandHandler
 *  net.minecraft.command.ICommand
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.monster.EntitySkeleton
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.common.ForgeChunkManager
 *  net.minecraftforge.common.ForgeChunkManager$LoadingCallback
 *  net.minecraftforge.common.config.Configuration
 *  net.minecraftforge.event.entity.item.ItemTossEvent
 *  net.minecraftforge.event.entity.living.LivingSpawnEvent$CheckSpawn
 *  net.minecraftforge.event.entity.player.PlayerDropsEvent
 */
package com.flansmod.common;

import blocks.Radiation;
import com.flansmod.common.BlockFlansWork;
import com.flansmod.common.CommonGuiHandler;
import com.flansmod.common.CommonProxy;
import com.flansmod.common.CommonTickHandler;
import com.flansmod.common.CreativeTabFlan;
import com.flansmod.common.FlansHooks;
import com.flansmod.common.ItemBlockManyNames;
import com.flansmod.common.PlayerHandler;
import com.flansmod.common.driveables.EntityPlane;
import com.flansmod.common.driveables.EntitySeat;
import com.flansmod.common.driveables.EntityVehicle;
import com.flansmod.common.driveables.EntityWheel;
import com.flansmod.common.driveables.ItemPlane;
import com.flansmod.common.driveables.ItemVehicle;
import com.flansmod.common.driveables.PlaneType;
import com.flansmod.common.driveables.VehicleType;
import com.flansmod.common.driveables.mechas.EntityMecha;
import com.flansmod.common.driveables.mechas.ItemMecha;
import com.flansmod.common.driveables.mechas.ItemMechaAddon;
import com.flansmod.common.driveables.mechas.MechaItemType;
import com.flansmod.common.driveables.mechas.MechaType;
import com.flansmod.common.eventhandlers.PlayerDeathEventListener;
import com.flansmod.common.guns.AAGunType;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.EntityAAGun;
import com.flansmod.common.guns.EntityBullet;
import com.flansmod.common.guns.EntityGrenade;
import com.flansmod.common.guns.EntityMG;
import com.flansmod.common.guns.GrenadeType;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.ItemAAGun;
import com.flansmod.common.guns.ItemAttachment;
import com.flansmod.common.guns.ItemBullet;
import com.flansmod.common.guns.ItemGrenade;
import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.guns.boxes.BlockGunBox;
import com.flansmod.common.guns.boxes.GunBoxType;
import com.flansmod.common.network.PacketHandler;
import com.flansmod.common.parts.ItemPart;
import com.flansmod.common.parts.PartType;
import com.flansmod.common.teams.ArmourBoxType;
import com.flansmod.common.teams.ArmourType;
import com.flansmod.common.teams.BlockArmourBox;
import com.flansmod.common.teams.BlockSpawner;
import com.flansmod.common.teams.ChunkLoadingHandler;
import com.flansmod.common.teams.CommandTeams;
import com.flansmod.common.teams.EntityFlag;
import com.flansmod.common.teams.EntityFlagpole;
import com.flansmod.common.teams.EntityGunItem;
import com.flansmod.common.teams.EntityTeamItem;
import com.flansmod.common.teams.Filter;
import com.flansmod.common.teams.ItemFlagpole;
import com.flansmod.common.teams.ItemOpStick;
import com.flansmod.common.teams.ItemTeamArmour;
import com.flansmod.common.teams.Team;
import com.flansmod.common.teams.TeamsManager;
import com.flansmod.common.teams.TileEntitySpawner;
import com.flansmod.common.tools.EntityParachute;
import com.flansmod.common.tools.ItemTool;
import com.flansmod.common.tools.ToolType;
import com.flansmod.common.types.EnumType;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.types.TypeFile;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

@Mod(modid="flansmod", name="Flan's Mod", version="@VERSION@", acceptableRemoteVersions="@ALLOWEDVERSIONS@", guiFactory="com.flansmod.client.gui.config.ModGuiFactory")
public class FlansMod {
    public static boolean DEBUG = false;
    public static Configuration configFile;
    public static final String MODID = "flansmod";
    public static final String VERSION = "@VERSION@";
    @Mod.Instance(value="flansmod")
    public static FlansMod INSTANCE;
    private static int modGuiIndex;
    public static int generalConfigInteger;
    public static String generalConfigString;
    public static boolean addGunpowderRecipe;
    public static int teamsConfigInteger;
    public static String teamsConfigString;
    public static boolean teamsConfigBoolean;
    @SidedProxy(clientSide="com.flansmod.client.ClientProxy", serverSide="com.flansmod.common.CommonProxy")
    public static CommonProxy proxy;
    public static int ticker;
    public static long lastTime;
    public static File flanDir;
    public static final float soundRange = 50.0f;
    public static final float driveableUpdateRange = 200.0f;
    public static final int numPlayerSnapshots = 20;
    public static float armourSpawnRate;
    public static Team spectators;
    public static final PacketHandler packetHandler;
    public static final PlayerHandler playerHandler;
    public static final TeamsManager teamsManager;
    public static final CommonTickHandler tickHandler;
    public static FlansHooks hooks;
    public static BlockFlansWork workbench;
    public static Radiation radiation;
    public static BlockSpawner spawner;
    public static ItemOpStick opStick;
    public static Filter filter;
    public static ItemFlagpole flag;
    public static ArrayList<BlockGunBox> gunBoxBlocks;
    public static ArrayList<ItemBullet> bulletItems;
    public static ArrayList<ItemGun> gunItems;
    public static ArrayList<ItemAttachment> attachmentItems;
    public static ArrayList<ItemPart> partItems;
    public static ArrayList<ItemPlane> planeItems;
    public static ArrayList<ItemVehicle> vehicleItems;
    public static ArrayList<ItemMechaAddon> mechaToolItems;
    public static ArrayList<ItemMecha> mechaItems;
    public static ArrayList<ItemAAGun> aaGunItems;
    public static ArrayList<ItemGrenade> grenadeItems;
    public static ArrayList<ItemTool> toolItems;
    public static ArrayList<ItemTeamArmour> armourItems;
    public static ArrayList<BlockArmourBox> armourBoxBlocks;
    public static CreativeTabFlan tabFlanGuns;
    public static CreativeTabFlan tabFlanDriveables;
    public static CreativeTabFlan tabFlanParts;
    public static CreativeTabFlan tabFlanTeams;
    public static CreativeTabFlan tabFlanMechas;
    public static final int GUI_CUSTOM_INV;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FlansMod.log("Preinitialising Flan's mod.");
        configFile = new Configuration(event.getSuggestedConfigurationFile());
        FlansMod.syncConfig();
        flanDir = new File(event.getModConfigurationDirectory().getParentFile(), "/Flan/");
        if (!flanDir.exists()) {
            FlansMod.log("Flan folder not found. Creating empty folder.");
            FlansMod.log("You should get some content packs and put them in the Flan folder.");
            flanDir.mkdirs();
            flanDir.mkdir();
        }
        radiation = (Radiation)new Radiation(1).func_149663_c("Radiation");
        GameRegistry.registerBlock((Block)radiation, (String)"Radiation");
        workbench = (BlockFlansWork)new BlockFlansWork(1, 0).func_149663_c("flansNewWorkbench").func_149658_d("flansWorkbench");
        GameRegistry.registerBlock((Block)workbench, ItemBlockManyNames.class, (String)"flansNewWorkbench");
        GameRegistry.addRecipe((ItemStack)new ItemStack((Block)workbench, 1, 0), (Object[])new Object[]{"BBB", "III", "III", Character.valueOf('B'), Items.field_151054_z, Character.valueOf('I'), Items.field_151042_j});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Block)workbench, 1, 1), (Object[])new Object[]{"ICI", "III", Character.valueOf('C'), Items.field_151066_bu, Character.valueOf('I'), Items.field_151042_j});
        opStick = new ItemOpStick();
        filter = new Filter();
        GameRegistry.registerItem((Item)filter, (String)"Filter", (String)MODID);
        GameRegistry.registerItem((Item)opStick, (String)"opStick", (String)MODID);
        flag = (ItemFlagpole)new ItemFlagpole().func_77655_b("flagpole");
        GameRegistry.registerItem((Item)flag, (String)"flagpole", (String)MODID);
        spawner = (BlockSpawner)new BlockSpawner(Material.field_151573_f).func_149663_c("teamsSpawner").func_149722_s().func_149752_b(1000000.0f);
        GameRegistry.registerBlock((Block)spawner, ItemBlockManyNames.class, (String)"teamsSpawner");
        GameRegistry.registerTileEntity(TileEntitySpawner.class, (String)"teamsSpawner");
        proxy.registerRenderers();
        this.readContentPacks(event);
        proxy.load();
        proxy.forceReload();
        FlansMod.log("Preinitializing complete.");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FlansMod.log("Initialising Flan's Mod.");
        FlansMod.log("loading Hood");
        FlansMod.log("Hood is loaded");
        packetHandler.initialise();
        NetworkRegistry.INSTANCE.registerGuiHandler((Object)this, (IGuiHandler)new CommonGuiHandler());
        for (InfoType type : InfoType.infoTypes) {
            type.addRecipe();
        }
        if (addGunpowderRecipe) {
            ItemStack charcoal = new ItemStack(Items.field_151044_h, 1, 1);
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(Items.field_151016_H), (Object[])new Object[]{charcoal, charcoal, charcoal, new ItemStack(Items.field_151114_aO)});
        }
        FlansMod.log("Loaded recipes.");
        EntityRegistry.registerGlobalEntityID(EntityFlagpole.class, (String)"Flagpole", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityFlagpole.class, (String)"Flagpole", (int)93, (Object)this, (int)40, (int)5, (boolean)true);
        EntityRegistry.registerGlobalEntityID(EntityFlag.class, (String)"Flag", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityFlag.class, (String)"Flag", (int)94, (Object)this, (int)40, (int)5, (boolean)true);
        EntityRegistry.registerGlobalEntityID(EntityTeamItem.class, (String)"TeamsItem", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityTeamItem.class, (String)"TeamsItem", (int)97, (Object)this, (int)100, (int)10000, (boolean)true);
        EntityRegistry.registerGlobalEntityID(EntityGunItem.class, (String)"GunItem", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityGunItem.class, (String)"GunItem", (int)98, (Object)this, (int)100, (int)20, (boolean)true);
        EntityRegistry.registerGlobalEntityID(EntityPlane.class, (String)"Plane", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityPlane.class, (String)"Plane", (int)90, (Object)this, (int)250, (int)3, (boolean)false);
        EntityRegistry.registerGlobalEntityID(EntityVehicle.class, (String)"Vehicle", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityVehicle.class, (String)"Vehicle", (int)95, (Object)this, (int)250, (int)10, (boolean)false);
        EntityRegistry.registerGlobalEntityID(EntitySeat.class, (String)"Seat", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntitySeat.class, (String)"Seat", (int)99, (Object)this, (int)250, (int)20, (boolean)false);
        EntityRegistry.registerGlobalEntityID(EntityWheel.class, (String)"Wheel", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityWheel.class, (String)"Wheel", (int)103, (Object)this, (int)250, (int)20, (boolean)false);
        EntityRegistry.registerGlobalEntityID(EntityParachute.class, (String)"Parachute", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityParachute.class, (String)"Parachute", (int)101, (Object)this, (int)40, (int)20, (boolean)false);
        EntityRegistry.registerGlobalEntityID(EntityMecha.class, (String)"Mecha", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityMecha.class, (String)"Mecha", (int)102, (Object)this, (int)250, (int)20, (boolean)false);
        EntityRegistry.registerModEntity(EntityBullet.class, (String)"Bullet", (int)96, (Object)this, (int)40, (int)100, (boolean)false);
        EntityRegistry.registerGlobalEntityID(EntityGrenade.class, (String)"Grenade", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityGrenade.class, (String)"Grenade", (int)100, (Object)this, (int)40, (int)100, (boolean)true);
        EntityRegistry.registerGlobalEntityID(EntityMG.class, (String)"MG", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityMG.class, (String)"MG", (int)91, (Object)this, (int)40, (int)5, (boolean)true);
        EntityRegistry.registerGlobalEntityID(EntityAAGun.class, (String)"AAGun", (int)EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityAAGun.class, (String)"AAGun", (int)92, (Object)this, (int)40, (int)500, (boolean)false);
        ForgeChunkManager.setForcedChunkLoadingCallback((Object)this, (ForgeChunkManager.LoadingCallback)new ChunkLoadingHandler());
        FMLCommonHandler.instance().bus().register((Object)INSTANCE);
        new PlayerDeathEventListener();
        FlansMod.log("Loading complete.");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        packetHandler.postInitialise();
        hooks.hook();
    }

    @SubscribeEvent
    public void playerDrops(PlayerDropsEvent event) {
        for (int i = event.drops.size() - 1; i >= 0; --i) {
            EntityItem ent = (EntityItem)event.drops.get(i);
            InfoType type = InfoType.getType(ent.func_92059_d());
            if (type == null || type.canDrop) continue;
            event.drops.remove(i);
        }
    }

    @SubscribeEvent
    public void playerDrops(ItemTossEvent event) {
        InfoType type = InfoType.getType(event.entityItem.func_92059_d());
        if (type != null && !type.canDrop) {
            event.setCanceled(true);
        }
    }

    @Mod.EventHandler
    public void registerCommand(FMLServerStartedEvent e) {
        CommandHandler handler = (CommandHandler)FMLCommonHandler.instance().getSidedDelegate().getServer().func_71187_D();
        handler.func_71560_a((ICommand)new CommandTeams());
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(MODID)) {
            FlansMod.syncConfig();
        }
    }

    @SubscribeEvent
    public void onLivingSpecialSpawn(LivingSpawnEvent.CheckSpawn event) {
        double chance = event.world.field_73012_v.nextDouble();
        if (chance < (double)armourSpawnRate && event.entityLiving instanceof EntityZombie || event.entityLiving instanceof EntitySkeleton) {
            if (event.world.field_73012_v.nextBoolean() && ArmourType.armours.size() > 0) {
                ArmourType armour = ArmourType.armours.get(event.world.field_73012_v.nextInt(ArmourType.armours.size()));
                if (armour != null && armour.type != 2) {
                    event.entityLiving.func_70062_b(armour.type + 1, new ItemStack(armour.item));
                }
            } else if (Team.teams.size() > 0) {
                Team team = Team.teams.get(event.world.field_73012_v.nextInt(Team.teams.size()));
                if (team.hat != null) {
                    event.entityLiving.func_70062_b(1, team.hat.func_77946_l());
                }
                if (team.chest != null) {
                    event.entityLiving.func_70062_b(2, team.chest.func_77946_l());
                }
                if (team.shoes != null) {
                    event.entityLiving.func_70062_b(4, team.shoes.func_77946_l());
                }
            }
        }
    }

    private void getTypeFiles(List<File> contentPacks) {
        for (File contentPack : contentPacks) {
            int i$;
            if (contentPack.isDirectory()) {
                for (EnumType typeToCheckFor : EnumType.values()) {
                    File typesDir = new File(contentPack, "/" + typeToCheckFor.folderName + "/");
                    if (!typesDir.exists()) continue;
                    File[] fileArray = typesDir.listFiles();
                    int len$ = fileArray.length;
                    for (i$ = 0; i$ < len$; ++i$) {
                        File file = fileArray[i$];
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(file));
                            String[] splitName = file.getName().split("/");
                            TypeFile typeFile = new TypeFile(typeToCheckFor, splitName[splitName.length - 1].split("\\.")[0]);
                            while (true) {
                                String line = null;
                                try {
                                    line = reader.readLine();
                                }
                                catch (Exception e) {
                                    break;
                                }
                                if (line == null) break;
                                typeFile.lines.add(line);
                            }
                            reader.close();
                            continue;
                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                            continue;
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                continue;
            }
            try {
                ZipFile zip = new ZipFile(contentPack);
                ZipInputStream zipStream = new ZipInputStream(new FileInputStream(contentPack));
                BufferedReader reader = new BufferedReader(new InputStreamReader(zipStream));
                ZipEntry zipEntry = zipStream.getNextEntry();
                block13: do {
                    if ((zipEntry = zipStream.getNextEntry()) == null) continue;
                    TypeFile typeFile = null;
                    EnumType[] enumTypeArray = EnumType.values();
                    int len$ = enumTypeArray.length;
                    for (i$ = 0; i$ < len$; ++i$) {
                        EnumType enumType = enumTypeArray[i$];
                        if (!zipEntry.getName().startsWith(enumType.folderName + "/") || zipEntry.getName().split(enumType.folderName + "/").length <= 1 || zipEntry.getName().split(enumType.folderName + "/")[1].length() <= 0) continue;
                        String[] splitName = zipEntry.getName().split("/");
                        typeFile = new TypeFile(enumType, splitName[splitName.length - 1].split("\\.")[0]);
                    }
                    if (typeFile == null) continue;
                    while (true) {
                        String string;
                        Object var9_16 = null;
                        try {
                            string = reader.readLine();
                        }
                        catch (Exception e) {
                            continue block13;
                        }
                        if (string == null) continue block13;
                        typeFile.lines.add(string);
                    }
                } while (zipEntry != null);
                reader.close();
                zip.close();
                zipStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readContentPacks(FMLPreInitializationEvent event) {
        ClassLoader classloader = MinecraftServer.class.getClassLoader();
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
        }
        catch (Exception e) {
            FlansMod.log("Failed to get class loader. All content loading will now fail.");
            e.printStackTrace();
        }
        List<File> contentPacks = proxy.getContentList(method, classloader);
        if (!event.getSide().equals((Object)Side.CLIENT)) {
            // empty if block
        }
        this.getTypeFiles(contentPacks);
        for (EnumType type : EnumType.values()) {
            Class<? extends InfoType> typeClass = type.getTypeClass();
            for (TypeFile typeFile : TypeFile.files.get((Object)type)) {
                try {
                    InfoType infoType = typeClass.getConstructor(TypeFile.class).newInstance(typeFile);
                    infoType.read(typeFile);
                    switch (type) {
                        case bullet: {
                            bulletItems.add((ItemBullet)new ItemBullet((BulletType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case attachment: {
                            attachmentItems.add((ItemAttachment)new ItemAttachment((AttachmentType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case gun: {
                            gunItems.add((ItemGun)new ItemGun((GunType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case grenade: {
                            grenadeItems.add((ItemGrenade)new ItemGrenade((GrenadeType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case part: {
                            partItems.add((ItemPart)new ItemPart((PartType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case plane: {
                            planeItems.add((ItemPlane)new ItemPlane((PlaneType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case vehicle: {
                            vehicleItems.add((ItemVehicle)new ItemVehicle((VehicleType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case aa: {
                            aaGunItems.add((ItemAAGun)new ItemAAGun((AAGunType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case mechaItem: {
                            mechaToolItems.add((ItemMechaAddon)new ItemMechaAddon((MechaItemType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case mecha: {
                            mechaItems.add((ItemMecha)new ItemMecha((MechaType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case tool: {
                            toolItems.add((ItemTool)new ItemTool((ToolType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case box: {
                            gunBoxBlocks.add((BlockGunBox)new BlockGunBox((GunBoxType)infoType).func_149663_c(infoType.shortName));
                            break;
                        }
                        case armour: {
                            armourItems.add((ItemTeamArmour)new ItemTeamArmour((ArmourType)infoType).func_77655_b(infoType.shortName));
                            break;
                        }
                        case armourBox: {
                            armourBoxBlocks.add((BlockArmourBox)new BlockArmourBox((ArmourBoxType)infoType).func_149663_c(infoType.shortName));
                            break;
                        }
                        case playerClass: {
                            break;
                        }
                        case team: {
                            break;
                        }
                        default: {
                            FlansMod.log("Unrecognised type for " + infoType.shortName);
                            break;
                        }
                    }
                }
                catch (Exception e) {
                    FlansMod.log("Failed to add " + type.name() + " : " + typeFile.name);
                    e.printStackTrace();
                }
            }
            FlansMod.log("Loaded " + type.name() + ".");
        }
        Team.spectators = spectators;
    }

    public static PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public static void syncConfig() {
        addGunpowderRecipe = configFile.getBoolean("Gunpowder Recipe", "general", addGunpowderRecipe, "Whether or not to add the extra gunpowder recipe (3 charcoal + 1 lightstone)");
        if (configFile.hasChanged()) {
            configFile.save();
        }
    }

    public static void log(String string) {
        System.out.println("[Flan's Mod] " + string);
    }

    static {
        modGuiIndex = 0;
        generalConfigInteger = 32;
        generalConfigString = "Hello!";
        addGunpowderRecipe = true;
        teamsConfigInteger = 32;
        teamsConfigString = "Hello!";
        teamsConfigBoolean = false;
        ticker = 0;
        armourSpawnRate = 0.25f;
        spectators = new Team("spectators", "Spectators", 0x404040, '7');
        packetHandler = new PacketHandler();
        playerHandler = new PlayerHandler();
        teamsManager = new TeamsManager();
        tickHandler = new CommonTickHandler();
        hooks = new FlansHooks();
        gunBoxBlocks = new ArrayList();
        bulletItems = new ArrayList();
        gunItems = new ArrayList();
        attachmentItems = new ArrayList();
        partItems = new ArrayList();
        planeItems = new ArrayList();
        vehicleItems = new ArrayList();
        mechaToolItems = new ArrayList();
        mechaItems = new ArrayList();
        aaGunItems = new ArrayList();
        grenadeItems = new ArrayList();
        toolItems = new ArrayList();
        armourItems = new ArrayList();
        armourBoxBlocks = new ArrayList();
        tabFlanGuns = new CreativeTabFlan(0);
        tabFlanDriveables = new CreativeTabFlan(1);
        tabFlanParts = new CreativeTabFlan(2);
        tabFlanTeams = new CreativeTabFlan(3);
        tabFlanMechas = new CreativeTabFlan(4);
        GUI_CUSTOM_INV = modGuiIndex++;
    }
}

