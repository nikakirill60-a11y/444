/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  cpw.mods.fml.client.FMLClientHandler
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.client.settings.GameSettings
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.util.StatCollector
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.BlockEvent$BreakEvent
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package com.flansmod.common.guns;

import com.flansmod.client.FlansModClient;
import com.flansmod.client.debug.EntityDebugDot;
import com.flansmod.client.debug.EntityDebugVector;
import com.flansmod.client.model.GunAnimations;
import com.flansmod.client.model.RenderGun;
import com.flansmod.common.FlansMod;
import com.flansmod.common.PlayerData;
import com.flansmod.common.PlayerHandler;
import com.flansmod.common.RotatedAxes;
import com.flansmod.common.driveables.EntitySeat;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.EntityAAGun;
import com.flansmod.common.guns.EntityDamageSourceGun;
import com.flansmod.common.guns.EntityGrenade;
import com.flansmod.common.guns.EntityMG;
import com.flansmod.common.guns.EnumFireMode;
import com.flansmod.common.guns.EnumSecondaryFunction;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.IScope;
import com.flansmod.common.guns.InventoryHelper;
import com.flansmod.common.guns.ItemBullet;
import com.flansmod.common.guns.ItemShootable;
import com.flansmod.common.guns.Paintjob;
import com.flansmod.common.guns.ShootableType;
import com.flansmod.common.guns.raytracing.BulletHit;
import com.flansmod.common.guns.raytracing.EntityHit;
import com.flansmod.common.guns.raytracing.EnumHitboxType;
import com.flansmod.common.guns.raytracing.PlayerBulletHit;
import com.flansmod.common.guns.raytracing.PlayerHitbox;
import com.flansmod.common.guns.raytracing.PlayerSnapshot;
import com.flansmod.common.network.PacketGunFire;
import com.flansmod.common.network.PacketPlaySound;
import com.flansmod.common.network.PacketReload;
import com.flansmod.common.network.PacketSelectOffHandGun;
import com.flansmod.common.teams.EntityFlag;
import com.flansmod.common.teams.EntityFlagpole;
import com.flansmod.common.teams.EntityGunItem;
import com.flansmod.common.teams.Team;
import com.flansmod.common.types.IFlanItem;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.vector.Vector3f;
import com.google.common.collect.Multimap;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ItemGun
extends Item
implements IFlanItem {
    public static byte Ammo = 0;
    public boolean hasAmmoAnim = false;
    public GunType type;
    public AttachmentType atype;
    private static boolean rightMouseHeld;
    private static boolean lastRightMouseHeld;
    private static boolean leftMouseHeld;
    private static boolean lastLeftMouseHeld;
    public static boolean Scope;
    public static boolean Run;
    public int soundDelay;
    public boolean isRepaired = false;
    public HashMap<String, IIcon> icons = new HashMap();

    public ItemGun(GunType gun) {
        this.field_77777_bU = 1;
        this.type = gun;
        this.type.item = this;
        this.func_77637_a(FlansMod.tabFlanGuns);
        GameRegistry.registerItem((Item)this, (String)this.type.shortName, (String)"flansmod");
    }

    public boolean func_77651_p() {
        return true;
    }

    public ItemStack getBulletItemStack(ItemStack gun, int id) {
        if (!gun.func_77942_o()) {
            gun.field_77990_d = new NBTTagCompound();
            return null;
        }
        if (!gun.field_77990_d.func_74764_b("ammo")) {
            NBTTagList ammoTagsList = new NBTTagList();
            for (int i = 0; i < this.type.numAmmoItemsInGun; ++i) {
                ammoTagsList.func_74742_a((NBTBase)new NBTTagCompound());
            }
            gun.field_77990_d.func_74782_a("ammo", (NBTBase)ammoTagsList);
            return null;
        }
        if (!gun.field_77990_d.func_74764_b("NoDrop")) {
            gun.field_77990_d.func_74768_a("NoDrop", 1);
            return gun;
        }
        NBTTagList ammoTagsList = gun.field_77990_d.func_150295_c("ammo", 10);
        NBTTagCompound ammoTags = ammoTagsList.func_150305_b(id);
        return ItemStack.func_77949_a((NBTTagCompound)ammoTags);
    }

    public void setBulletItemStack(ItemStack gun, ItemStack bullet, int id) {
        NBTTagList ammoTagsList;
        if (!gun.func_77942_o()) {
            gun.field_77990_d = new NBTTagCompound();
        }
        if (!gun.field_77990_d.func_74764_b("ammo")) {
            ammoTagsList = new NBTTagList();
            for (int i = 0; i < this.type.numAmmoItemsInGun; ++i) {
                ammoTagsList.func_74742_a((NBTBase)new NBTTagCompound());
            }
            gun.field_77990_d.func_74782_a("ammo", (NBTBase)ammoTagsList);
        }
        ammoTagsList = gun.field_77990_d.func_150295_c("ammo", 10);
        NBTTagCompound ammoTags = ammoTagsList.func_150305_b(id);
        if (bullet == null) {
            ammoTags = new NBTTagCompound();
        }
        bullet.func_77955_b(ammoTags);
    }

    public void func_77624_a(ItemStack stack, EntityPlayer player, List lines, boolean advancedTooltips) {
        lines.add(StatCollector.func_74838_a((String)(EnumChatFormatting.WHITE + "\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c: " + ItemGun.get_ammo(stack))));
        lines.add(StatCollector.func_74838_a((String)(EnumChatFormatting.YELLOW + "\u0423\u0440\u043e\u043d: " + this.type.damage)));
        lines.add(StatCollector.func_74838_a((String)(EnumChatFormatting.YELLOW + "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u0440\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c: " + 1200 / this.type.shootDelay + " \u0432\u044b\u0441/\u043c\u0438\u043d.")));
        lines.add(StatCollector.func_74838_a((String)(EnumChatFormatting.YELLOW + "\u0412\u0440\u0435\u043c\u044f \u043f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0438: " + (double)this.type.reloadTime / 20.0 + " \u0441.")));
        lines.add(StatCollector.func_74838_a((String)(EnumChatFormatting.YELLOW + "\u0420\u0430\u0437\u0431\u0440\u043e\u0441: " + this.type.bulletSpread)));
        lines.add(StatCollector.func_74838_a((String)(EnumChatFormatting.YELLOW + "\u041e\u0442\u0434\u0430\u0447\u0430: " + this.type.recoil)));
        for (AttachmentType attachment : this.type.getCurrentAttachments(stack)) {
            String line = attachment.name;
            lines.add(EnumChatFormatting.BLUE + line);
        }
        for (int i = 0; i < this.type.numAmmoItemsInGun; ++i) {
            ItemStack bulletStack = this.getBulletItemStack(stack, i);
            if (bulletStack == null || !(bulletStack.func_77973_b() instanceof ItemBullet)) continue;
            BulletType bulletType = ((ItemBullet)bulletStack.func_77973_b()).type;
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void onUpdateClient(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        if (entity instanceof EntityPlayer && ((EntityPlayer)entity).field_71071_by.func_70448_g() == itemstack) {
            Minecraft mc = Minecraft.func_71410_x();
            EntityPlayer player = (EntityPlayer)entity;
            PlayerData data = PlayerHandler.getPlayerData(player, Side.CLIENT);
            if (this.soundDelay <= 0 && this.type.idleSound != null) {
                PacketPlaySound.sendSoundPacket(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, 50.0, entity.field_71093_bK, this.type.idleSound, false);
                this.soundDelay = this.type.idleSoundLength;
            }
            if (this.type.deployable) {
                return;
            }
            GameSettings gameSettings = FMLClientHandler.instance().getClient().field_71474_y;
            if (FMLClientHandler.instance().getClient().field_71462_r != null) {
                if (FlansModClient.currentScope != null) {
                    FlansModClient.currentScope = null;
                    gameSettings.field_74341_c = FlansModClient.originalMouseSensitivity;
                    gameSettings.field_74320_O = FlansModClient.originalThirdPerson;
                    gameSettings.field_74334_X = FlansModClient.originalFOV;
                }
            } else if (!(mc.field_71476_x != null && (mc.field_71476_x.field_72308_g instanceof EntityFlagpole || mc.field_71476_x.field_72308_g instanceof EntityFlag || mc.field_71476_x.field_72308_g instanceof EntityGunItem || mc.field_71476_x.field_72308_g instanceof EntityGrenade && ((EntityGrenade)mc.field_71476_x.field_72308_g).type.isDeployableBag))) {
                lastRightMouseHeld = rightMouseHeld;
                lastLeftMouseHeld = leftMouseHeld;
                rightMouseHeld = Mouse.isButtonDown((int)0);
                leftMouseHeld = Mouse.isButtonDown((int)1);
                boolean offHandFull = false;
                if (this.type.oneHanded) {
                    if (data.offHandGunSlot == player.field_71071_by.field_70461_c + 1) {
                        data.offHandGunSlot = 0;
                    }
                    int dWheel = Mouse.getDWheel();
                    if (Keyboard.isKeyDown((int)mc.field_71474_y.field_74311_E.func_151463_i()) && dWheel != 0) {
                        data.cycleOffHandItem(player, dWheel);
                    }
                    if (data.offHandGunSlot != 0) {
                        offHandFull = true;
                        ItemStack offHandGunStack = player.field_71071_by.func_70301_a(data.offHandGunSlot - 1);
                        if (offHandGunStack != null && offHandGunStack.func_77973_b() instanceof ItemGun) {
                            GunType offHandGunType = ((ItemGun)offHandGunStack.func_77973_b()).type;
                            if (offHandGunType.usableByPlayers) {
                                if (offHandGunType.getFireMode(offHandGunStack) == EnumFireMode.BURST && data.burstRoundsRemainingLeft > 0) {
                                    if (this.clientSideShoot(player, offHandGunStack, offHandGunType, true)) {
                                        player.field_71071_by.func_70299_a(data.offHandGunSlot - 1, null);
                                    }
                                } else {
                                    if (leftMouseHeld && !lastLeftMouseHeld) {
                                        FlansMod.getPacketHandler().sendToServer(new PacketGunFire(true, true));
                                        if (this.clientSideShoot(player, offHandGunStack, offHandGunType, true)) {
                                            player.field_71071_by.func_70299_a(data.offHandGunSlot - 1, null);
                                        }
                                    }
                                    if ((offHandGunType.getFireMode(offHandGunStack) == EnumFireMode.FULLAUTO || offHandGunType.getFireMode(offHandGunStack) == EnumFireMode.MINIGUN) && !leftMouseHeld && lastLeftMouseHeld) {
                                        FlansMod.getPacketHandler().sendToServer(new PacketGunFire(true, false));
                                    }
                                    if ((offHandGunType.getFireMode(offHandGunStack) == EnumFireMode.FULLAUTO || offHandGunType.getFireMode(offHandGunStack) == EnumFireMode.MINIGUN) && leftMouseHeld && this.clientSideShoot(player, offHandGunStack, offHandGunType, true)) {
                                        player.field_71071_by.func_70299_a(data.offHandGunSlot - 1, null);
                                    }
                                }
                            }
                        } else {
                            data.offHandGunSlot = 0;
                        }
                    }
                }
                if (this.type.usableByPlayers) {
                    if (this.type.getFireMode(itemstack) == EnumFireMode.BURST && data.burstRoundsRemainingRight > 0) {
                        if (this.clientSideShoot(player, itemstack, this.type, false)) {
                            player.field_71071_by.func_70299_a(player.field_71071_by.field_70461_c, null);
                        }
                    } else {
                        if (rightMouseHeld && !lastRightMouseHeld) {
                            FlansMod.getPacketHandler().sendToServer(new PacketGunFire(false, true));
                            if (this.clientSideShoot(player, itemstack, this.type, false)) {
                                player.field_71071_by.func_70299_a(player.field_71071_by.field_70461_c, null);
                            }
                        }
                        if ((this.type.getFireMode(itemstack) == EnumFireMode.FULLAUTO || this.type.getFireMode(itemstack) == EnumFireMode.MINIGUN) && !rightMouseHeld && lastRightMouseHeld) {
                            FlansMod.getPacketHandler().sendToServer(new PacketGunFire(false, false));
                        }
                        if ((this.type.getFireMode(itemstack) == EnumFireMode.FULLAUTO || this.type.getFireMode(itemstack) == EnumFireMode.MINIGUN) && rightMouseHeld && this.clientSideShoot(player, itemstack, this.type, false)) {
                            player.field_71071_by.func_70299_a(player.field_71071_by.field_70461_c, null);
                        }
                    }
                }
                IScope currentScope = this.type.getCurrentScope(itemstack);
                if (!offHandFull && (this.type.secondaryFunction == EnumSecondaryFunction.ADS_ZOOM || this.type.secondaryFunction == EnumSecondaryFunction.ZOOM) && Mouse.isButtonDown((int)1) && FlansModClient.scopeTime <= 0 && FMLClientHandler.instance().getClient().field_71462_r == null) {
                    if (FlansModClient.currentScope == null) {
                        FlansModClient.currentScope = currentScope;
                        FlansModClient.lastZoomLevel = currentScope.getZoomFactor();
                        FlansModClient.lastFOVZoomLevel = currentScope.getFOVFactor();
                        float f = FlansModClient.originalMouseSensitivity = gameSettings.field_74341_c;
                        gameSettings.field_74341_c = f / (float)Math.sqrt(currentScope.getZoomFactor());
                        FlansModClient.originalThirdPerson = gameSettings.field_74320_O;
                        gameSettings.field_74320_O = 0;
                        FlansModClient.originalFOV = gameSettings.field_74334_X;
                        Scope = true;
                    } else {
                        FlansModClient.currentScope = null;
                        gameSettings.field_74341_c = FlansModClient.originalMouseSensitivity;
                        gameSettings.field_74320_O = FlansModClient.originalThirdPerson;
                        gameSettings.field_74334_X = FlansModClient.originalFOV;
                        Scope = false;
                    }
                    FlansModClient.scopeTime = 10;
                }
                if (player.func_70051_ag() && (this.type.secondaryFunction == EnumSecondaryFunction.ADS_ZOOM || this.type.secondaryFunction == EnumSecondaryFunction.ZOOM)) {
                    FlansModClient.currentScope = null;
                    gameSettings.field_74341_c = FlansModClient.originalMouseSensitivity;
                }
            }
        }
        if (this.soundDelay > 0) {
            --this.soundDelay;
        }
    }

    public boolean clientSideShoot(EntityPlayer player, ItemStack stack, GunType gunType, boolean left) {
        PlayerData data = PlayerHandler.getPlayerData(player);
        if (FlansModClient.shootTime(left) <= 0) {
            boolean hasAmmo = false;
            for (int i = 0; i < gunType.numAmmoItemsInGun; ++i) {
                ItemStack bulletStack = this.getBulletItemStack(stack, i);
                if (bulletStack == null || bulletStack.func_77973_b() == null || bulletStack.func_77960_j() >= bulletStack.func_77958_k()) continue;
                hasAmmo = true;
                break;
            }
            if (hasAmmo && stack.func_77978_p().func_74762_e("\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c") > 0) {
                RenderGun.ShootSprite = !RenderGun.ShootSprite;
                GunAnimations animations = null;
                if (left) {
                    if (FlansModClient.gunAnimationsLeft.containsKey(player)) {
                        animations = FlansModClient.gunAnimationsLeft.get(player);
                    } else {
                        animations = new GunAnimations();
                        FlansModClient.gunAnimationsLeft.put((EntityLivingBase)player, animations);
                    }
                } else if (FlansModClient.gunAnimationsRight.containsKey(player)) {
                    animations = FlansModClient.gunAnimationsRight.get(player);
                } else {
                    animations = new GunAnimations();
                    FlansModClient.gunAnimationsRight.put((EntityLivingBase)player, animations);
                }
                int pumpDelay = gunType.model == null ? 0 : gunType.model.pumpDelay;
                int pumpTime = gunType.model == null ? 1 : gunType.model.pumpTime;
                animations.doShoot(pumpDelay, pumpTime);
                FlansModClient.playerRecoil += gunType.getRecoil(stack);
                if (left) {
                    FlansModClient.shootTimeLeft = gunType.shootDelay;
                } else {
                    FlansModClient.shootTimeRight = gunType.shootDelay;
                }
                if (gunType.consumeGunUponUse) {
                    return true;
                }
            }
            if (gunType.getFireMode(stack) == EnumFireMode.BURST) {
                if (left) {
                    data.burstRoundsRemainingLeft = data.burstRoundsRemainingLeft > 0 ? --data.burstRoundsRemainingLeft : gunType.numBurstRounds;
                } else {
                    data.burstRoundsRemainingRight = data.burstRoundsRemainingRight > 0 ? --data.burstRoundsRemainingRight : gunType.numBurstRounds;
                }
            }
        }
        return false;
    }

    public void onUpdateServer(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        if (entity instanceof EntityPlayerMP) {
            ItemStack offHandGunStack;
            EntityPlayerMP player = (EntityPlayerMP)entity;
            PlayerData data = PlayerHandler.getPlayerData((EntityPlayer)player);
            if (data == null) {
                return;
            }
            if (player.field_71071_by.func_70448_g() != itemstack) {
                if (player.field_71071_by.func_70448_g() == null || player.field_71071_by.func_70448_g().func_77973_b() == null || !(player.field_71071_by.func_70448_g().func_77973_b() instanceof ItemGun)) {
                    data.isShootingLeft = false;
                    data.isShootingRight = false;
                    data.offHandGunSlot = 0;
                    new PacketSelectOffHandGun(0).handleServerSide(player);
                }
                return;
            }
            if (this.type.getFireMode(itemstack) == EnumFireMode.BURST && data.burstRoundsRemainingRight > 0) {
                player.field_71071_by.func_70299_a(player.field_71071_by.field_70461_c, this.tryToShoot(itemstack, this.type, world, player, false));
            }
            if (data.isShootingRight) {
                if (this.type.getFireMode(itemstack) == EnumFireMode.FULLAUTO) {
                    player.field_71071_by.func_70299_a(player.field_71071_by.field_70461_c, this.tryToShoot(itemstack, this.type, world, player, false));
                }
                if (this.type.useLoopingSounds && data.loopedSoundDelay <= 0 && data.minigunSpeed > 0.1f && !data.reloadingRight) {
                    data.loopedSoundDelay = data.shouldPlayWarmupSound ? this.type.warmupSoundLength : this.type.loopedSoundLength;
                    PacketPlaySound.sendSoundPacket(player.field_70165_t, player.field_70163_u, player.field_70161_v, 50.0, player.field_71093_bK, data.shouldPlayWarmupSound ? this.type.warmupSound : this.type.loopedSound, false);
                    data.shouldPlayWarmupSound = false;
                }
                if (this.type.getFireMode(itemstack) == EnumFireMode.MINIGUN && data.minigunSpeed > this.type.minigunStartSpeed) {
                    player.field_71071_by.func_70299_a(player.field_71071_by.field_70461_c, this.tryToShoot(itemstack, this.type, world, player, false));
                }
            } else if (this.type.useLoopingSounds && data.shouldPlayCooldownSound) {
                PacketPlaySound.sendSoundPacket(player.field_70165_t, player.field_70163_u, player.field_70161_v, 50.0, player.field_71093_bK, this.type.cooldownSound, false);
                data.shouldPlayCooldownSound = false;
            }
            if (this.type.oneHanded && data.offHandGunSlot != 0 && (offHandGunStack = player.field_71071_by.func_70301_a(data.offHandGunSlot - 1)) != null && offHandGunStack.func_77973_b() instanceof ItemGun) {
                GunType offHandGunType = ((ItemGun)offHandGunStack.func_77973_b()).type;
                if (offHandGunType.getFireMode(offHandGunStack) == EnumFireMode.BURST && data.burstRoundsRemainingLeft > 0) {
                    player.field_71071_by.func_70299_a(data.offHandGunSlot - 1, this.tryToShoot(offHandGunStack, offHandGunType, world, player, true));
                }
                if (data.isShootingLeft) {
                    if (offHandGunType.getFireMode(offHandGunStack) == EnumFireMode.FULLAUTO) {
                        player.field_71071_by.func_70299_a(data.offHandGunSlot - 1, this.tryToShoot(offHandGunStack, offHandGunType, world, player, true));
                    }
                    if (offHandGunType.useLoopingSounds && data.loopedSoundDelay <= 0 && data.minigunSpeed > 0.1f && !data.reloadingLeft) {
                        data.loopedSoundDelay = data.shouldPlayWarmupSound ? offHandGunType.warmupSoundLength : offHandGunType.loopedSoundLength;
                        PacketPlaySound.sendSoundPacket(player.field_70165_t, player.field_70163_u, player.field_70161_v, 50.0, player.field_71093_bK, data.shouldPlayWarmupSound ? offHandGunType.warmupSound : offHandGunType.loopedSound, false);
                        data.shouldPlayWarmupSound = false;
                    }
                    if (offHandGunType.getFireMode(offHandGunStack) == EnumFireMode.MINIGUN && data.minigunSpeed > offHandGunType.minigunStartSpeed) {
                        player.field_71071_by.func_70299_a(data.offHandGunSlot - 1, this.tryToShoot(offHandGunStack, offHandGunType, world, player, true));
                    }
                } else if (offHandGunType.useLoopingSounds && data.shouldPlayCooldownSound) {
                    PacketPlaySound.sendSoundPacket(player.field_70165_t, player.field_70163_u, player.field_70161_v, 50.0, player.field_71093_bK, offHandGunType.cooldownSound, false);
                    data.shouldPlayCooldownSound = false;
                }
            }
        }
    }

    public void func_77663_a(ItemStack itemstack, World world, Entity pEnt, int i, boolean flag) {
        if (world.field_72995_K) {
            this.onUpdateClient(itemstack, world, pEnt, i, flag);
        } else {
            this.onUpdateServer(itemstack, world, pEnt, i, flag);
        }
        if (pEnt instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)pEnt;
            PlayerData data = PlayerHandler.getPlayerData(player);
            if (data == null) {
                return;
            }
            if (data.meleeLength > 0 && this.type.meleePath.size() > 0 && player.field_71071_by.func_70448_g() == itemstack) {
                for (int k = 0; k < this.type.meleeDamagePoints.size(); ++k) {
                    Vector3f dPos;
                    Vector3f meleeDamagePoint = this.type.meleeDamagePoints.get(k);
                    Vector3f nextPos = this.type.meleePath.get((data.meleeProgress + 1) % this.type.meleePath.size());
                    Vector3f nextAngles = this.type.meleePathAngles.get((data.meleeProgress + 1) % this.type.meleePathAngles.size());
                    RotatedAxes nextAxes = new RotatedAxes().rotateGlobalRoll(-nextAngles.x).rotateGlobalPitch(-nextAngles.z).rotateGlobalYaw(-nextAngles.y);
                    Vector3f nextPosInGunCoords = nextAxes.findLocalVectorGlobally(meleeDamagePoint);
                    Vector3f.add(nextPos, nextPosInGunCoords, nextPosInGunCoords);
                    Vector3f.add(new Vector3f(0.0f, 0.0f, 0.0f), nextPosInGunCoords, nextPosInGunCoords);
                    Vector3f nextPosInPlayerCoords = new RotatedAxes(player.field_70177_z + 90.0f, player.field_70125_A, 0.0f).findLocalVectorGlobally(nextPosInGunCoords);
                    if (!FlansMod.proxy.isThePlayer(player)) {
                        nextPosInPlayerCoords.y += 1.6f;
                    }
                    Vector3f nextPosInWorldCoords = new Vector3f(player.field_70165_t + (double)nextPosInPlayerCoords.x, player.field_70163_u + (double)nextPosInPlayerCoords.y, player.field_70161_v + (double)nextPosInPlayerCoords.z);
                    Vector3f vector3f = dPos = data.lastMeleePositions[k] == null ? new Vector3f() : Vector3f.sub(nextPosInWorldCoords, data.lastMeleePositions[k], null);
                    if (player.field_70170_p.field_72995_K && FlansMod.DEBUG) {
                        player.field_70170_p.func_72838_d((Entity)new EntityDebugVector(player.field_70170_p, data.lastMeleePositions[k], dPos, 200, 1.0f, 0.0f, 0.0f));
                    }
                    ArrayList<BulletHit> hits = new ArrayList<BulletHit>();
                    for (int j = 0; j < world.field_72996_f.size(); ++j) {
                        MovingObjectPosition mop;
                        Object obj = world.field_72996_f.get(j);
                        if (obj instanceof EntityPlayer) {
                            MovingObjectPosition mop2;
                            EntityPlayer otherPlayer = (EntityPlayer)obj;
                            PlayerData otherData = PlayerHandler.getPlayerData(otherPlayer);
                            boolean shouldDoNormalHitDetect = false;
                            if (otherPlayer == player) continue;
                            if (otherData != null) {
                                PlayerSnapshot snapshot;
                                int snapshotToTry;
                                if (otherPlayer.field_70128_L || otherData.team == Team.spectators) continue;
                                int n = snapshotToTry = player instanceof EntityPlayerMP ? ((EntityPlayerMP)player).field_71138_i / 50 : 0;
                                if (snapshotToTry >= otherData.snapshots.length) {
                                    snapshotToTry = otherData.snapshots.length - 1;
                                }
                                if ((snapshot = otherData.snapshots[snapshotToTry]) == null) {
                                    snapshot = otherData.snapshots[0];
                                }
                                if (snapshot == null) {
                                    shouldDoNormalHitDetect = true;
                                } else {
                                    ArrayList<BulletHit> playerHits = snapshot.raytrace(data.lastMeleePositions[k] == null ? nextPosInWorldCoords : data.lastMeleePositions[k], dPos);
                                    hits.addAll(playerHits);
                                }
                            }
                            if (otherData != null && !shouldDoNormalHitDetect) continue;
                            MovingObjectPosition movingObjectPosition = mop2 = data.lastMeleePositions[k] == null ? player.field_70121_D.func_72327_a(nextPosInWorldCoords.toVec3(), Vec3.func_72443_a((double)0.0, (double)0.0, (double)0.0)) : player.field_70121_D.func_72327_a(data.lastMeleePositions[k].toVec3(), nextPosInWorldCoords.toVec3());
                            if (mop2 == null) continue;
                            Vector3f hitPoint = new Vector3f(mop2.field_72307_f.field_72450_a - (double)data.lastMeleePositions[k].x, mop2.field_72307_f.field_72448_b - (double)data.lastMeleePositions[k].y, mop2.field_72307_f.field_72449_c - (double)data.lastMeleePositions[k].z);
                            float hitLambda = 1.0f;
                            if (dPos.x != 0.0f) {
                                hitLambda = hitPoint.x / dPos.x;
                            } else if (dPos.y != 0.0f) {
                                hitLambda = hitPoint.y / dPos.y;
                            } else if (dPos.z != 0.0f) {
                                hitLambda = hitPoint.z / dPos.z;
                            }
                            if (hitLambda < 0.0f) {
                                hitLambda = -hitLambda;
                            }
                            hits.add(new PlayerBulletHit(new PlayerHitbox(otherPlayer, new RotatedAxes(), new Vector3f(), new Vector3f(), new Vector3f(), EnumHitboxType.BODY), hitLambda));
                            continue;
                        }
                        Entity entity = (Entity)obj;
                        if (entity == player || entity.field_70128_L || !(entity instanceof EntityLivingBase) && !(entity instanceof EntityAAGun) || (mop = entity.field_70121_D.func_72327_a(data.lastMeleePositions[k].toVec3(), nextPosInWorldCoords.toVec3())) == null) continue;
                        Vector3f hitPoint = new Vector3f(mop.field_72307_f.field_72450_a - (double)data.lastMeleePositions[k].x, mop.field_72307_f.field_72448_b - (double)data.lastMeleePositions[k].y, mop.field_72307_f.field_72449_c - (double)data.lastMeleePositions[k].z);
                        float hitLambda = 1.0f;
                        if (dPos.x != 0.0f) {
                            hitLambda = hitPoint.x / dPos.x;
                        } else if (dPos.y != 0.0f) {
                            hitLambda = hitPoint.y / dPos.y;
                        } else if (dPos.z != 0.0f) {
                            hitLambda = hitPoint.z / dPos.z;
                        }
                        if (hitLambda < 0.0f) {
                            hitLambda = -hitLambda;
                        }
                        hits.add(new EntityHit(entity, hitLambda));
                    }
                    if (!hits.isEmpty()) {
                        Collections.sort(hits);
                        float swingDistance = dPos.length();
                        for (BulletHit bulletHit : hits) {
                            if (bulletHit instanceof PlayerBulletHit) {
                                PlayerBulletHit playerHit = (PlayerBulletHit)bulletHit;
                                float damageMultiplier = 1.0f;
                                switch (playerHit.hitbox.type) {
                                    case LEFTITEM: 
                                    case RIGHTITEM: {
                                        data.meleeLength = 0;
                                        data.meleeProgress = 0;
                                        return;
                                    }
                                    case HEAD: {
                                        damageMultiplier = 2.0f;
                                        break;
                                    }
                                    case RIGHTARM: 
                                    case LEFTARM: {
                                        damageMultiplier = 0.6f;
                                        break;
                                    }
                                }
                                if (playerHit.hitbox.player.func_70097_a(this.getMeleeDamage(player), swingDistance * this.type.meleeDamage)) {
                                    ++playerHit.hitbox.player.field_70720_be;
                                    playerHit.hitbox.player.field_70172_ad = playerHit.hitbox.player.field_70771_an / 2;
                                }
                                if (!FlansMod.DEBUG) continue;
                                world.func_72838_d((Entity)new EntityDebugDot(world, new Vector3f(data.lastMeleePositions[k].x + dPos.x * playerHit.intersectTime, data.lastMeleePositions[k].y + dPos.y * playerHit.intersectTime, data.lastMeleePositions[k].z + dPos.z * playerHit.intersectTime), 1000, 1.0f, 0.0f, 0.0f));
                                continue;
                            }
                            if (!(bulletHit instanceof EntityHit)) continue;
                            EntityHit entityHit = (EntityHit)bulletHit;
                            if (entityHit.entity.func_70097_a(DamageSource.func_76365_a((EntityPlayer)player), swingDistance * this.type.meleeDamage) && entityHit.entity instanceof EntityLivingBase) {
                                EntityLivingBase living = (EntityLivingBase)entityHit.entity;
                                ++living.field_70720_be;
                                living.field_70172_ad = living.field_70771_an / 2;
                            }
                            if (!FlansMod.DEBUG) continue;
                            world.func_72838_d((Entity)new EntityDebugDot(world, new Vector3f(data.lastMeleePositions[k].x + dPos.x * entityHit.intersectTime, data.lastMeleePositions[k].y + dPos.y * entityHit.intersectTime, data.lastMeleePositions[k].z + dPos.z * entityHit.intersectTime), 1000, 1.0f, 0.0f, 0.0f));
                        }
                    }
                    data.lastMeleePositions[k] = nextPosInWorldCoords;
                }
                ++data.meleeProgress;
                if (data.meleeProgress == data.meleeLength) {
                    data.meleeLength = 0;
                    data.meleeProgress = 0;
                }
            }
        }
    }

    public DamageSource getMeleeDamage(EntityPlayer attacker) {
        return new EntityDamageSourceGun(this.type.shortName, (Entity)attacker, attacker, this.type, false);
    }

    public void onMouseHeld(ItemStack stack, World world, EntityPlayerMP player, boolean left, boolean isShooting) {
        PlayerData data = PlayerHandler.getPlayerData((EntityPlayer)player);
        if (data != null && data.shootClickDelay == 0) {
            if (player.field_70154_o instanceof EntitySeat && ((EntitySeat)player.field_70154_o).seatInfo.id == 0) {
                return;
            }
            if (left && data.offHandGunSlot != 0) {
                ItemStack offHandGunStack = player.field_71071_by.func_70301_a(data.offHandGunSlot - 1);
                GunType gunType = ((ItemGun)offHandGunStack.func_77973_b()).type;
                data.isShootingLeft = isShooting;
                if (gunType.getFireMode(offHandGunStack) == EnumFireMode.SEMIAUTO && isShooting) {
                    data.isShootingLeft = false;
                    player.field_71071_by.func_70299_a(data.offHandGunSlot - 1, this.tryToShoot(offHandGunStack, gunType, world, player, true));
                }
                if (gunType.getFireMode(offHandGunStack) == EnumFireMode.BURST && isShooting && data.burstRoundsRemainingLeft == 0) {
                    data.isShootingLeft = false;
                    data.burstRoundsRemainingLeft = gunType.numBurstRounds;
                    player.field_71071_by.func_70299_a(data.offHandGunSlot - 1, this.tryToShoot(offHandGunStack, gunType, world, player, true));
                }
            } else {
                data.isShootingRight = isShooting;
                if (this.type.getFireMode(stack) == EnumFireMode.SEMIAUTO && isShooting) {
                    data.isShootingRight = false;
                    player.field_71071_by.func_70299_a(player.field_71071_by.field_70461_c, this.tryToShoot(stack, this.type, world, player, false));
                }
                if (this.type.getFireMode(stack) == EnumFireMode.BURST && isShooting && data.burstRoundsRemainingRight == 0) {
                    data.isShootingRight = false;
                    data.burstRoundsRemainingRight = this.type.numBurstRounds;
                    player.field_71071_by.func_70299_a(player.field_71071_by.field_70461_c, this.tryToShoot(stack, this.type, world, player, false));
                }
            }
            if (this.type.useLoopingSounds && isShooting) {
                data.shouldPlayWarmupSound = true;
            }
        }
    }

    public void repair_gun(ItemStack itemstack) {
        itemstack.func_77978_p().func_74768_a("\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c", this.type.durability);
    }

    public static int get_ammo(ItemStack itemstack) {
        return itemstack.func_77942_o() ? itemstack.func_77978_p().func_74762_e("\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c") : 1000;
    }

    public ItemStack tryToShoot(ItemStack gunStack, GunType gunType, World world, EntityPlayerMP entityplayer, boolean left) {
        if (this.type.deployable || entityplayer.func_70051_ag() || gunStack.func_77978_p().func_74762_e("\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c") <= 0) {
            return gunStack;
        }
        gunStack.field_77990_d.func_74757_a("isRepaired", false);
        PlayerData data = PlayerHandler.getPlayerData((EntityPlayer)entityplayer);
        if (left && data.shootTimeLeft <= 0 || !left && data.shootTimeRight <= 0) {
            int bulletID;
            ItemStack bulletStack = null;
            for (bulletID = 0; bulletID < gunType.numAmmoItemsInGun; ++bulletID) {
                ItemStack checkingStack = this.getBulletItemStack(gunStack, bulletID);
                if (checkingStack == null || checkingStack.func_77973_b() == null || checkingStack.func_77960_j() >= checkingStack.func_77958_k()) continue;
                bulletStack = checkingStack;
                break;
            }
            if (bulletStack == null) {
                if (this.reload(gunStack, gunType, world, (EntityPlayer)entityplayer, false, left)) {
                    data.shootTimeRight = data.shootTimeLeft = (int)gunType.getReloadTime(gunStack);
                    if (left) {
                        data.reloadingLeft = true;
                        data.burstRoundsRemainingLeft = 0;
                    } else {
                        data.reloadingRight = true;
                        data.burstRoundsRemainingRight = 0;
                    }
                    FlansMod.getPacketHandler().sendTo(new PacketReload(left), entityplayer);
                    if (gunType.reloadSound != null) {
                        PacketPlaySound.sendSoundPacket(entityplayer.field_70165_t, entityplayer.field_70163_u, entityplayer.field_70161_v, 50.0, entityplayer.field_71093_bK, gunType.reloadSound, true);
                    }
                }
            } else if (bulletStack.func_77973_b() instanceof ItemShootable) {
                gunStack.func_77978_p().func_74768_a("\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c", ItemGun.get_ammo(gunStack) - 1);
                this.shoot(gunStack, gunType, world, bulletStack, (EntityPlayer)entityplayer, left);
                bulletStack.func_77964_b(bulletStack.func_77960_j() + 1);
                this.setBulletItemStack(gunStack, bulletStack, bulletID);
                if (gunType.getFireMode(gunStack) == EnumFireMode.BURST) {
                    if (left && data.burstRoundsRemainingLeft > 0) {
                        --data.burstRoundsRemainingLeft;
                    }
                    if (!left && data.burstRoundsRemainingRight > 0) {
                        --data.burstRoundsRemainingRight;
                    }
                }
                if (gunType.consumeGunUponUse) {
                    return null;
                }
            }
        }
        return gunStack;
    }

    public boolean reload(ItemStack gunStack, GunType gunType, World world, EntityPlayer player, boolean forceReload, boolean left) {
        return this.reload(gunStack, gunType, world, (Entity)player, (IInventory)player.field_71071_by, player.field_71075_bZ.field_75098_d, forceReload);
    }

    public boolean reload(ItemStack gunStack, GunType gunType, World world, Entity entity, IInventory inventory, boolean creative, boolean forceReload) {
        if (gunType.deployable) {
            return false;
        }
        if (forceReload && !gunType.canForceReload) {
            return false;
        }
        boolean reloadedSomething = false;
        for (int i = 0; i < gunType.numAmmoItemsInGun; ++i) {
            ItemStack bulletStack = this.getBulletItemStack(gunStack, i);
            if (bulletStack != null && bulletStack.func_77960_j() != bulletStack.func_77958_k() && !forceReload) continue;
            int bestSlot = -1;
            int bulletsInBestSlot = 0;
            for (int j = 0; j < inventory.func_70302_i_(); ++j) {
                int bulletsInThisSlot;
                ItemStack item = inventory.func_70301_a(j);
                if (item == null || !(item.func_77973_b() instanceof ItemShootable) || !gunType.isAmmo(((ItemShootable)item.func_77973_b()).type) || (bulletsInThisSlot = item.func_77958_k() - item.func_77960_j()) <= bulletsInBestSlot) continue;
                bestSlot = j;
                bulletsInBestSlot = bulletsInThisSlot;
            }
            if (bestSlot == -1) continue;
            ItemStack newBulletStack = inventory.func_70301_a(bestSlot);
            ShootableType newBulletType = ((ItemShootable)newBulletStack.func_77973_b()).type;
            if (bulletStack != null && bulletStack.func_77973_b() instanceof ItemShootable && ((ItemShootable)bulletStack.func_77973_b()).type.dropItemOnReload != null && !creative) {
                ItemGun.dropItem(world, entity, ((ItemShootable)bulletStack.func_77973_b()).type.dropItemOnReload);
            }
            if (bulletStack != null && bulletStack.func_77960_j() < bulletStack.func_77958_k() && !InventoryHelper.addItemStackToInventory(inventory, bulletStack, creative)) {
                entity.func_70099_a(bulletStack, 0.5f);
            }
            ItemStack stackToLoad = newBulletStack.func_77946_l();
            stackToLoad.field_77994_a = 1;
            this.setBulletItemStack(gunStack, stackToLoad, i);
            if (!creative) {
                --newBulletStack.field_77994_a;
            }
            if (newBulletStack.field_77994_a <= 0) {
                newBulletStack = null;
            }
            inventory.func_70299_a(bestSlot, newBulletStack);
            reloadedSomething = true;
        }
        return reloadedSomething;
    }

    public static void dropItem(World world, Entity entity, String itemName) {
        if (itemName != null) {
            int damage = 0;
            if (itemName.contains(".")) {
                damage = Integer.parseInt(itemName.split("\\.")[1]);
                itemName = itemName.split("\\.")[0];
            }
            ItemStack dropStack = InfoType.getRecipeElement(itemName, damage);
            entity.func_70099_a(dropStack, 0.5f);
        }
    }

    private void shoot(ItemStack stack, GunType gunType, World world, ItemStack bulletStack, EntityPlayer entityplayer, boolean left) {
        ShootableType bullet = ((ItemShootable)bulletStack.func_77973_b()).type;
        if (this.soundDelay <= 0 && gunType.shootSound != null) {
            AttachmentType barrel = gunType.getBarrel(stack);
            boolean silenced = barrel != null && barrel.silencer;
            PacketPlaySound.sendSoundPacket(entityplayer.field_70165_t, entityplayer.field_70163_u, entityplayer.field_70161_v, 50.0, entityplayer.field_71093_bK, gunType.shootSound, gunType.distortSound, silenced);
            this.soundDelay = gunType.shootSoundLength;
        }
        if (!world.field_72995_K) {
            for (int k = 0; k < gunType.numBullets; ++k) {
                world.func_72838_d((Entity)((ItemShootable)bulletStack.func_77973_b()).getEntity(world, (EntityLivingBase)entityplayer, (entityplayer.func_70093_af() ? 0.7f : 1.0f) * gunType.getSpread(stack), gunType.getDamage(stack), gunType.getBulletSpeed(stack) * 100000.0f, gunType.numBullets > 1, bulletStack.func_77960_j(), gunType));
            }
            if (bullet.dropItemOnShoot != null && !entityplayer.field_71075_bZ.field_75098_d) {
                ItemGun.dropItem(world, (Entity)entityplayer, bullet.dropItemOnShoot);
            }
            if (gunType.dropItemOnShoot != null) {
                ItemGun.dropItem(world, (Entity)entityplayer, gunType.dropItemOnShoot);
            }
        }
        if (left) {
            PlayerHandler.getPlayerData((EntityPlayer)entityplayer).shootTimeLeft = gunType.shootDelay;
        } else {
            PlayerHandler.getPlayerData((EntityPlayer)entityplayer).shootTimeRight = gunType.shootDelay;
        }
        if (gunType.knockback > 0.0f) {
            // empty if block
        }
    }

    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (this.type.deployable) {
            Vec3 lookVec;
            float cosYaw = MathHelper.func_76134_b((float)(-entityplayer.field_70177_z * 0.01745329f - 3.141593f));
            float sinYaw = MathHelper.func_76126_a((float)(-entityplayer.field_70177_z * 0.01745329f - 3.141593f));
            float cosPitch = -MathHelper.func_76134_b((float)(-entityplayer.field_70125_A * 0.01745329f));
            float sinPitch = MathHelper.func_76126_a((float)(-entityplayer.field_70125_A * 0.01745329f));
            double length = 5.0;
            Vec3 posVec = Vec3.func_72443_a((double)entityplayer.field_70165_t, (double)(entityplayer.field_70163_u + 1.62 - (double)entityplayer.field_70129_M), (double)entityplayer.field_70161_v);
            MovingObjectPosition look = world.func_72901_a(posVec, lookVec = posVec.func_72441_c((double)(sinYaw * cosPitch) * length, (double)sinPitch * length, (double)(cosYaw * cosPitch) * length), true);
            if (look != null && look.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK && look.field_72310_e == 1) {
                int playerDir = MathHelper.func_76128_c((double)((double)(entityplayer.field_70177_z * 4.0f / 360.0f) + 0.5)) & 3;
                int i = look.field_72311_b;
                int j = look.field_72312_c;
                int k = look.field_72309_d;
                if (!world.field_72995_K) {
                    if (world.func_147439_a(i, j, k) == Blocks.field_150433_aE) {
                        --j;
                    }
                    if (!(!this.isSolid(world, i, j, k) || world.func_147439_a(i, j + 1, k) != Blocks.field_150350_a && world.func_147439_a(i, j + 1, k) != Blocks.field_150433_aE || world.func_147439_a(i + (playerDir == 1 ? 1 : 0) - (playerDir == 3 ? 1 : 0), j + 1, k - (playerDir == 0 ? 1 : 0) + (playerDir == 2 ? 1 : 0)) != Blocks.field_150350_a || world.func_147439_a(i + (playerDir == 1 ? 1 : 0) - (playerDir == 3 ? 1 : 0), j, k - (playerDir == 0 ? 1 : 0) + (playerDir == 2 ? 1 : 0)) != Blocks.field_150350_a && world.func_147439_a(i + (playerDir == 1 ? 1 : 0) - (playerDir == 3 ? 1 : 0), j, k - (playerDir == 0 ? 1 : 0) + (playerDir == 2 ? 1 : 0)) != Blocks.field_150433_aE)) {
                        for (EntityMG mg : EntityMG.mgs) {
                            if (mg.blockX != i || mg.blockY != j + 1 || mg.blockZ != k || mg.field_70128_L) continue;
                            return itemstack;
                        }
                        if (!world.field_72995_K) {
                            world.func_72838_d((Entity)new EntityMG(world, i, j + 1, k, playerDir, this.type));
                        }
                        if (!entityplayer.field_71075_bZ.field_75098_d) {
                            itemstack.field_77994_a = 0;
                        }
                    }
                }
            }
        }
        if (world.field_72995_K) {
            for (int i = 0; i < 3; ++i) {
                Minecraft.func_71410_x().field_71460_t.field_78516_c.func_78441_a();
            }
        }
        return itemstack;
    }

    private boolean isSolid(World world, int i, int j, int k) {
        Block block = world.func_147439_a(i, j, k);
        return block != null && block.func_149688_o().func_76220_a() && block.func_149662_c();
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return this.type.secondaryFunction != EnumSecondaryFunction.MELEE;
    }

    public boolean func_77662_d() {
        return true;
    }

    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if (this.type.meleeSound != null) {
            PacketPlaySound.sendSoundPacket(entityLiving.field_70165_t, entityLiving.field_70163_u, entityLiving.field_70161_v, 50.0, entityLiving.field_71093_bK, this.type.meleeSound, true);
        }
        if (this.type.secondaryFunction == EnumSecondaryFunction.CUSTOM_MELEE) {
            if (entityLiving.field_70170_p.field_72995_K) {
                GunAnimations animations = FlansModClient.getGunAnimations(entityLiving, false);
                animations.doMelee(this.type.meleeTime);
            }
            if (entityLiving instanceof EntityPlayer) {
                PlayerData data = PlayerHandler.getPlayerData((EntityPlayer)entityLiving);
                data.doMelee((EntityPlayer)entityLiving, this.type.meleeTime, this.type);
            }
        }
        return this.type.secondaryFunction != EnumSecondaryFunction.MELEE;
    }

    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
        return false;
    }

    public boolean func_150897_b(Block p_150897_1_) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public int func_82790_a(ItemStack par1ItemStack, int par2) {
        return this.type.colour;
    }

    public boolean isItemStackDamageable() {
        return true;
    }

    public void func_150895_a(Item item, CreativeTabs tabs, List list) {
        ItemStack gunStack = new ItemStack(item, 1, 0);
        GunType type = ((ItemGun)item).type;
        NBTTagCompound tags = new NBTTagCompound();
        tags.func_74778_a("Paint", type.defaultPaintjob.iconName);
        tags.func_74768_a("\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c", type.durability);
        tags.func_74768_a("NoDrop", 1);
        gunStack.field_77990_d = tags;
        list.add(gunStack);
    }

    @SideOnly(value=Side.CLIENT)
    public void func_94581_a(IIconRegister icon) {
        this.field_77791_bV = icon.func_94245_a("FlansMod:" + this.type.iconPath);
        for (Paintjob paintjob : this.type.paintjobs) {
            this.icons.put(paintjob.iconName, icon.func_94245_a("FlansMod:" + paintjob.iconName));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon func_77650_f(ItemStack stack) {
        if (stack.field_77990_d == null) {
            stack.field_77990_d = new NBTTagCompound();
        }
        if (!stack.field_77990_d.func_74764_b("Paint")) {
            stack.field_77990_d.func_74778_a("Paint", this.type.defaultPaintjob.iconName);
        }
        return this.icons.get(stack.field_77990_d.func_74779_i("Paint"));
    }

    public int func_77626_a(ItemStack par1ItemStack) {
        return 100;
    }

    public EnumAction func_77661_b(ItemStack par1ItemStack) {
        return EnumAction.bow;
    }

    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {
        if (event.getPlayer().func_71045_bC() != null && event.getPlayer().func_71045_bC().func_77973_b() instanceof ItemGun) {
            event.setCanceled(true);
        }
    }

    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put((Object)SharedMonsterAttributes.field_111266_c.func_111108_a(), (Object)new AttributeModifier(field_111210_e, "KnockbackResist", (double)this.type.knockbackModifier, 0));
        map.put((Object)SharedMonsterAttributes.field_111263_d.func_111108_a(), (Object)new AttributeModifier(field_111210_e, "MovementSpeed", (double)(this.type.moveSpeedModifier - 1.0f), 2));
        if (this.type.secondaryFunction == EnumSecondaryFunction.MELEE) {
            map.put((Object)SharedMonsterAttributes.field_111264_e.func_111108_a(), (Object)new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.type.meleeDamage, 0));
        }
        return map;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        return (double)stack.func_77952_i() / (double)stack.func_77958_k();
    }

    @Override
    public InfoType getInfoType() {
        return this.type;
    }

    static {
        Scope = false;
        Run = false;
    }
}

