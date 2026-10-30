/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemDye
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 */
package com.flansmod.common.guns;

import com.flansmod.client.model.ModelGun;
import com.flansmod.client.model.ModelMG;
import com.flansmod.common.FlansMod;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.EnumFireMode;
import com.flansmod.common.guns.EnumSecondaryFunction;
import com.flansmod.common.guns.IScope;
import com.flansmod.common.guns.ItemBullet;
import com.flansmod.common.guns.ItemGrenade;
import com.flansmod.common.guns.Paintjob;
import com.flansmod.common.guns.ShootableType;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.types.TypeFile;
import com.flansmod.common.vector.Vector3f;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class GunType
extends InfoType
implements IScope {
    public List<ShootableType> ammo = new ArrayList<ShootableType>();
    public boolean canForceReload = true;
    public int reloadTime;
    public int recoil;
    public float bulletSpread;
    public float damage = 0.0f;
    public int durability = 5000;
    public float meleeDamage = 1.0f;
    public float bulletSpeed = 5.0f;
    public int numBullets = 1;
    public int shootDelay = 0;
    public int numAmmoItemsInGun = 1;
    public EnumFireMode mode = EnumFireMode.FULLAUTO;
    public int numBurstRounds = 3;
    public float minigunStartSpeed = 15.0f;
    public boolean canShootUnderwater = true;
    public float knockback = 0.0f;
    public EnumSecondaryFunction secondaryFunction = EnumSecondaryFunction.ADS_ZOOM;
    public boolean oneHanded = false;
    public boolean consumeGunUponUse = false;
    public String dropItemOnShoot = null;
    public int meleeTime = 1;
    public ArrayList<Vector3f> meleePath = new ArrayList();
    public ArrayList<Vector3f> meleePathAngles = new ArrayList();
    public ArrayList<Vector3f> meleeDamagePoints = new ArrayList();
    public boolean usableByPlayers = true;
    public boolean usableByMechas = true;
    public boolean shield = false;
    public Vector3f shieldOrigin;
    public Vector3f shieldDimensions;
    public float shieldDamageAbsorption = 0.0f;
    public String shootSound;
    public int shootSoundLength;
    public boolean distortSound = true;
    public String reloadSound;
    public boolean useLoopingSounds = false;
    public String warmupSound;
    public int warmupSoundLength = 20;
    public String loopedSound;
    public int loopedSoundLength = 20;
    public String cooldownSound;
    public String meleeSound;
    public String idleSound;
    public int idleSoundLength;
    public boolean deployable = false;
    @SideOnly(value=Side.CLIENT)
    public ModelMG deployableModel;
    public String deployableTexture;
    public float standBackDist = 1.5f;
    public float topViewLimit = -60.0f;
    public float bottomViewLimit = 30.0f;
    public float sideViewLimit = 45.0f;
    public float pivotHeight = 0.375f;
    public String defaultScopeTexture;
    public boolean hasScopeOverlay = false;
    public float zoomLevel = 1.0f;
    public float FOVFactor = 1.5f;
    @SideOnly(value=Side.CLIENT)
    public ModelGun model;
    public float modelScale = 1.0f;
    public boolean allowAllAttachments = false;
    public ArrayList<AttachmentType> allowedAttachments = new ArrayList();
    public boolean allowBarrelAttachments = false;
    public boolean allowScopeAttachments = false;
    public boolean allowStockAttachments = false;
    public boolean allowGripAttachments = false;
    public int numGenericAttachmentSlots = 0;
    public ArrayList<Paintjob> paintjobs = new ArrayList();
    public Paintjob defaultPaintjob;
    public static HashMap<String, GunType> guns = new HashMap();
    public static ArrayList<GunType> gunList = new ArrayList();
    public float moveSpeedModifier = 1.0f;
    public float knockbackModifier = 0.0f;

    public GunType(TypeFile file) {
        super(file);
    }

    @Override
    public void postRead(TypeFile file) {
        gunList.add(this);
        guns.put(this.shortName, this);
        this.defaultPaintjob = new Paintjob(this.iconPath, this.texture, new ItemStack[0]);
        ArrayList<Paintjob> newPaintjobList = new ArrayList<Paintjob>();
        newPaintjobList.add(this.defaultPaintjob);
        newPaintjobList.addAll(this.paintjobs);
        this.paintjobs = newPaintjobList;
    }

    public int getAmmoSlots(ItemStack stack) {
        for (AttachmentType attachment : this.getCurrentAttachments(stack)) {
            this.numAmmoItemsInGun = (int)((float)this.numAmmoItemsInGun * attachment.ammoSlots);
        }
        return this.numAmmoItemsInGun;
    }

    @Override
    protected void read(String[] split, TypeFile file) {
        super.read(split, file);
        try {
            if (split[0].equals("Damage")) {
                this.damage = Float.parseFloat(split[1]);
            } else if (split[0].equals("MeleeDamage")) {
                this.meleeDamage = Float.parseFloat(split[1]);
                if (this.meleeDamage > 0.0f) {
                    this.secondaryFunction = EnumSecondaryFunction.MELEE;
                }
            } else if (split[0].equals("CanForceReload")) {
                this.canForceReload = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("ReloadTime")) {
                this.reloadTime = Integer.parseInt(split[1]);
            } else if (split[0].equals("Recoil")) {
                this.recoil = Integer.parseInt(split[1]);
            } else if (split[0].equals("Knockback")) {
                this.knockback = Float.parseFloat(split[1]);
            } else if (split[0].equals("Accuracy") || split[0].equals("Spread")) {
                this.bulletSpread = Float.parseFloat(split[1]);
            } else if (split[0].equals("NumBullets")) {
                this.numBullets = Integer.parseInt(split[1]);
            } else if (split[0].equals("ConsumeGunOnUse")) {
                this.consumeGunUponUse = Boolean.parseBoolean(split[1]);
            } else if (split[0].equals("DropItemOnShoot")) {
                this.dropItemOnShoot = split[1];
            } else if (split[0].equals("NumBurstRounds")) {
                this.numBurstRounds = Integer.parseInt(split[1]);
            } else if (split[0].equals("MinigunStartSpeed")) {
                this.minigunStartSpeed = Float.parseFloat(split[1]);
            } else if (split[0].equals("Durability")) {
                this.durability = Integer.parseInt(split[1]);
            } else if (split[0].equals("ShootDelay")) {
                this.shootDelay = Integer.parseInt(split[1]);
            } else if (split[0].equals("SoundLength")) {
                this.shootSoundLength = Integer.parseInt(split[1]);
            } else if (split[0].equals("DistortSound")) {
                this.distortSound = split[1].equals("True");
            } else if (split[0].equals("ShootSound")) {
                this.shootSound = split[1];
                FlansMod.proxy.loadSound(this.contentPack, "guns", split[1]);
            } else if (split[0].equals("ReloadSound")) {
                this.reloadSound = split[1];
                FlansMod.proxy.loadSound(this.contentPack, "guns", split[1]);
            } else if (split[0].equals("IdleSound")) {
                this.idleSound = split[1];
                FlansMod.proxy.loadSound(this.contentPack, "guns", split[1]);
            } else if (split[0].equals("IdleSoundLength")) {
                this.idleSoundLength = Integer.parseInt(split[1]);
            } else if (split[0].equals("MeleeSound")) {
                this.meleeSound = split[1];
                FlansMod.proxy.loadSound(this.contentPack, "guns", split[1]);
            } else if (split[0].equals("WarmupSound")) {
                this.warmupSound = split[1];
                FlansMod.proxy.loadSound(this.contentPack, "guns", split[1]);
            } else if (split[0].equals("WarmupSoundLength")) {
                this.warmupSoundLength = Integer.parseInt(split[1]);
            } else if (split[0].equals("LoopedSound") || split[0].equals("SpinSound")) {
                this.loopedSound = split[1];
                this.useLoopingSounds = true;
                FlansMod.proxy.loadSound(this.contentPack, "guns", split[1]);
            } else if (split[0].equals("LoopedSoundLength") || split[0].equals("SpinSoundLength")) {
                this.loopedSoundLength = Integer.parseInt(split[1]);
            } else if (split[0].equals("CooldownSound")) {
                this.cooldownSound = split[1];
                FlansMod.proxy.loadSound(this.contentPack, "guns", split[1]);
            } else if (split[0].equals("Mode")) {
                this.mode = EnumFireMode.getFireMode(split[1]);
            } else if (split[0].equals("Scope")) {
                this.hasScopeOverlay = true;
                if (split[1].equals("None")) {
                    this.hasScopeOverlay = false;
                } else {
                    this.defaultScopeTexture = split[1];
                }
            } else if (split[0].equals("ZoomLevel")) {
                this.zoomLevel = Float.parseFloat(split[1]);
                if (this.zoomLevel > 1.0f) {
                    this.secondaryFunction = EnumSecondaryFunction.ZOOM;
                }
            } else if (split[0].equals("FOVZoomLevel")) {
                this.FOVFactor = Float.parseFloat(split[1]);
                if (this.FOVFactor > 1.0f) {
                    this.secondaryFunction = EnumSecondaryFunction.ADS_ZOOM;
                }
            } else if (split[0].equals("Deployable")) {
                this.deployable = split[1].equals("True");
            } else if (FMLCommonHandler.instance().getSide().isClient() && this.deployable && split[0].equals("DeployedModel")) {
                this.deployableModel = FlansMod.proxy.loadModel(split[1], this.shortName, ModelMG.class);
            } else if (FMLCommonHandler.instance().getSide().isClient() && split[0].equals("Model")) {
                this.model = FlansMod.proxy.loadModel(split[1], this.shortName, ModelGun.class);
            } else if (split[0].equals("ModelScale")) {
                this.modelScale = Float.parseFloat(split[1]);
            } else if (split[0].equals("Texture")) {
                this.texture = split[1];
            } else if (split[0].equals("DeployedTexture")) {
                this.deployableTexture = split[1];
            } else if (split[0].equals("StandBackDistance")) {
                this.standBackDist = Float.parseFloat(split[1]);
            } else if (split[0].equals("TopViewLimit")) {
                this.topViewLimit = -Float.parseFloat(split[1]);
            } else if (split[0].equals("BottomViewLimit")) {
                this.bottomViewLimit = Float.parseFloat(split[1]);
            } else if (split[0].equals("SideViewLimit")) {
                this.sideViewLimit = Float.parseFloat(split[1]);
            } else if (split[0].equals("PivotHeight")) {
                this.pivotHeight = Float.parseFloat(split[1]);
            } else if (split[0].equals("Ammo")) {
                ShootableType type = ShootableType.getShootableType(split[1]);
                if (type != null) {
                    this.ammo.add(type);
                }
            } else if (split[0].equals("NumAmmoSlots") || split[0].equals("NumAmmoItemsInGun") || split[0].equals("LoadIntoGun")) {
                this.numAmmoItemsInGun = Integer.parseInt(split[1]);
            } else if (split[0].equals("BulletSpeed")) {
                this.bulletSpeed = Float.parseFloat(split[1]);
            } else if (split[0].equals("CanShootUnderwater")) {
                this.canShootUnderwater = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("OneHanded")) {
                this.oneHanded = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("SecondaryFunction")) {
                this.secondaryFunction = EnumSecondaryFunction.get(split[1]);
            } else if (split[0].equals("UsableByPlayers")) {
                this.usableByPlayers = Boolean.parseBoolean(split[1]);
            } else if (split[0].equals("UsableByMechas")) {
                this.usableByMechas = Boolean.parseBoolean(split[1]);
            } else if (split[0].equals("UseCustomMelee") && Boolean.parseBoolean(split[1])) {
                this.secondaryFunction = EnumSecondaryFunction.CUSTOM_MELEE;
            } else if (split[0].equals("MeleeTime")) {
                this.meleeTime = Integer.parseInt(split[1]);
            } else if (split[0].equals("AddNode")) {
                this.meleePath.add(new Vector3f(Float.parseFloat(split[1]) / 16.0f, Float.parseFloat(split[2]) / 16.0f, Float.parseFloat(split[3]) / 16.0f));
                this.meleePathAngles.add(new Vector3f(Float.parseFloat(split[4]), Float.parseFloat(split[5]), Float.parseFloat(split[6])));
            } else if (split[0].equals("MeleeDamagePoint") || split[0].equals("MeleeDamageOffset")) {
                this.meleeDamagePoints.add(new Vector3f(Float.parseFloat(split[1]) / 16.0f, Float.parseFloat(split[2]) / 16.0f, Float.parseFloat(split[3]) / 16.0f));
            } else if (split[0].equals("MoveSpeedModifier") || split[0].equals("Slowness")) {
                this.moveSpeedModifier = Float.parseFloat(split[1]);
            } else if (split[0].equals("KnockbackReduction") || split[0].equals("KnockbackModifier")) {
                this.knockbackModifier = Float.parseFloat(split[1]);
            } else if (split[0].equals("AllowAllAttachments")) {
                this.allowAllAttachments = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("AllowAttachments")) {
                for (int i = 1; i < split.length; ++i) {
                    this.allowedAttachments.add(AttachmentType.getAttachment(split[i]));
                }
            } else if (split[0].equals("AllowBarrelAttachments")) {
                this.allowBarrelAttachments = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("AllowScopeAttachments")) {
                this.allowScopeAttachments = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("AllowStockAttachments")) {
                this.allowStockAttachments = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("AllowGripAttachments")) {
                this.allowGripAttachments = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("NumGenericAttachmentSlots")) {
                this.numGenericAttachmentSlots = Integer.parseInt(split[1]);
            } else if (split[0].toLowerCase().equals("paintjob")) {
                ItemStack[] dyeStacks = new ItemStack[(split.length - 3) / 2];
                for (int i = 0; i < (split.length - 3) / 2; ++i) {
                    dyeStacks[i] = new ItemStack(Items.field_151100_aR, Integer.parseInt(split[i * 2 + 4]), this.getDyeDamageValue(split[i * 2 + 3]));
                }
                this.paintjobs.add(new Paintjob(split[1], split[2], dyeStacks));
            } else if (split[0].toLowerCase().equals("shield")) {
                this.shield = true;
                this.shieldDamageAbsorption = Float.parseFloat(split[1]);
                this.shieldOrigin = new Vector3f(Float.parseFloat(split[2]) / 16.0f, Float.parseFloat(split[3]) / 16.0f, Float.parseFloat(split[4]) / 16.0f);
                this.shieldDimensions = new Vector3f(Float.parseFloat(split[5]) / 16.0f, Float.parseFloat(split[6]) / 16.0f, Float.parseFloat(split[7]) / 16.0f);
            }
        }
        catch (Exception e) {
            System.out.println("Reading gun file failed.");
            e.printStackTrace();
        }
    }

    private int getDyeDamageValue(String dyeName) {
        int damage = -1;
        for (int i = 0; i < ItemDye.field_150923_a.length; ++i) {
            if (!ItemDye.field_150923_a[i].equals(dyeName)) continue;
            damage = i;
        }
        if (damage == -1) {
            FlansMod.log("Failed to find dye colour : " + dyeName + " while adding " + this.contentPack);
        }
        return damage;
    }

    public boolean isAmmo(ShootableType type) {
        return this.ammo.contains(type);
    }

    public boolean isAmmo(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.func_77973_b() instanceof ItemBullet) {
            return this.isAmmo(((ItemBullet)stack.func_77973_b()).type);
        }
        if (stack.func_77973_b() instanceof ItemGrenade) {
            return this.isAmmo(((ItemGrenade)stack.func_77973_b()).type);
        }
        return false;
    }

    @Override
    public void reloadModel() {
        this.model = FlansMod.proxy.loadModel(this.modelString, this.shortName, ModelGun.class);
    }

    @Override
    public float getZoomFactor() {
        return this.zoomLevel;
    }

    @Override
    public boolean hasZoomOverlay() {
        return this.hasScopeOverlay;
    }

    @Override
    public String getZoomOverlay() {
        return this.defaultScopeTexture;
    }

    @Override
    public float getFOVFactor() {
        return this.FOVFactor;
    }

    public IScope getCurrentScope(ItemStack gunStack) {
        AttachmentType attachedScope = this.getScope(gunStack);
        return attachedScope == null ? this : attachedScope;
    }

    public ArrayList<AttachmentType> getCurrentAttachments(ItemStack gun) {
        this.checkForTags(gun);
        ArrayList<AttachmentType> attachments = new ArrayList<AttachmentType>();
        NBTTagCompound attachmentTags = gun.field_77990_d.func_74775_l("attachments");
        NBTTagList genericsList = attachmentTags.func_150295_c("generics", 10);
        for (int i = 0; i < this.numGenericAttachmentSlots; ++i) {
            this.appendToList(gun, "generic_" + i, attachments);
        }
        this.appendToList(gun, "barrel", attachments);
        this.appendToList(gun, "scope", attachments);
        this.appendToList(gun, "stock", attachments);
        this.appendToList(gun, "grip", attachments);
        return attachments;
    }

    private void appendToList(ItemStack gun, String name, ArrayList<AttachmentType> attachments) {
        AttachmentType type = this.getAttachment(gun, name);
        if (type != null) {
            attachments.add(type);
        }
    }

    public AttachmentType getBarrel(ItemStack gun) {
        return this.getAttachment(gun, "barrel");
    }

    public AttachmentType getScope(ItemStack gun) {
        return this.getAttachment(gun, "scope");
    }

    public AttachmentType getStock(ItemStack gun) {
        return this.getAttachment(gun, "stock");
    }

    public AttachmentType getGrip(ItemStack gun) {
        return this.getAttachment(gun, "grip");
    }

    public AttachmentType getGeneric(ItemStack gun, int i) {
        return this.getAttachment(gun, "generic_" + i);
    }

    public ItemStack getBarrelItemStack(ItemStack gun) {
        return this.getAttachmentItemStack(gun, "barrel");
    }

    public ItemStack getScopeItemStack(ItemStack gun) {
        return this.getAttachmentItemStack(gun, "scope");
    }

    public ItemStack getStockItemStack(ItemStack gun) {
        return this.getAttachmentItemStack(gun, "stock");
    }

    public ItemStack getGripItemStack(ItemStack gun) {
        return this.getAttachmentItemStack(gun, "grip");
    }

    public ItemStack getGenericItemStack(ItemStack gun, int i) {
        return this.getAttachmentItemStack(gun, "generic_" + i);
    }

    public AttachmentType getAttachment(ItemStack gun, String name) {
        this.checkForTags(gun);
        return AttachmentType.getFromNBT(gun.field_77990_d.func_74775_l("attachments").func_74775_l(name));
    }

    public ItemStack getAttachmentItemStack(ItemStack gun, String name) {
        this.checkForTags(gun);
        return ItemStack.func_77949_a((NBTTagCompound)gun.field_77990_d.func_74775_l("attachments").func_74775_l(name));
    }

    private void checkForTags(ItemStack gun) {
        if (!gun.func_77942_o()) {
            gun.field_77990_d = new NBTTagCompound();
        }
        if (!gun.field_77990_d.func_74764_b("attachments")) {
            NBTTagCompound attachmentTags = new NBTTagCompound();
            for (int i = 0; i < this.numGenericAttachmentSlots; ++i) {
                attachmentTags.func_74782_a("generic_" + i, (NBTBase)new NBTTagCompound());
            }
            attachmentTags.func_74782_a("barrel", (NBTBase)new NBTTagCompound());
            attachmentTags.func_74782_a("scope", (NBTBase)new NBTTagCompound());
            attachmentTags.func_74782_a("stock", (NBTBase)new NBTTagCompound());
            attachmentTags.func_74782_a("grip", (NBTBase)new NBTTagCompound());
            gun.field_77990_d.func_74782_a("attachments", (NBTBase)attachmentTags);
        }
    }

    public float getMeleeDamage(ItemStack stack) {
        float stackMeleeDamage = this.meleeDamage;
        for (AttachmentType attachment : this.getCurrentAttachments(stack)) {
            stackMeleeDamage *= attachment.meleeDamageMultiplier;
        }
        return stackMeleeDamage;
    }

    public float getDamage(ItemStack stack) {
        float stackDamage = this.damage;
        for (AttachmentType attachment : this.getCurrentAttachments(stack)) {
            stackDamage *= attachment.damageMultiplier;
        }
        return stackDamage;
    }

    public float getSpread(ItemStack stack) {
        float stackSpread = this.bulletSpread;
        for (AttachmentType attachment : this.getCurrentAttachments(stack)) {
            stackSpread *= attachment.spreadMultiplier;
        }
        return stackSpread;
    }

    public float getRecoil(ItemStack stack) {
        float stackRecoil = this.recoil;
        for (AttachmentType attachment : this.getCurrentAttachments(stack)) {
            stackRecoil *= attachment.recoilMultiplier;
        }
        return stackRecoil;
    }

    public float getBulletSpeed(ItemStack stack) {
        float stackBulletSpeed = this.bulletSpeed;
        for (AttachmentType attachment : this.getCurrentAttachments(stack)) {
            stackBulletSpeed *= attachment.bulletSpeedMultiplier;
        }
        return stackBulletSpeed;
    }

    public float getReloadTime(ItemStack stack) {
        float stackReloadTime = this.reloadTime;
        for (AttachmentType attachment : this.getCurrentAttachments(stack)) {
            stackReloadTime *= attachment.reloadTimeMultiplier;
        }
        return stackReloadTime;
    }

    public EnumFireMode getFireMode(ItemStack stack) {
        for (AttachmentType attachment : this.getCurrentAttachments(stack)) {
            if (attachment.modeOverride == null) continue;
            return attachment.modeOverride;
        }
        return this.mode;
    }

    public static GunType getGun(String s) {
        return guns.get(s);
    }

    public Paintjob getPaintjob(String s) {
        for (Paintjob paintjob : this.paintjobs) {
            if (!paintjob.iconName.equals(s)) continue;
            return paintjob;
        }
        return this.defaultPaintjob;
    }
}

