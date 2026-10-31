/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.item.Item
 *  net.minecraft.potion.PotionEffect
 */
package com.flansmod.common.guns;

import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.EnumWeaponType;
import com.flansmod.common.guns.ShootableType;
import com.flansmod.common.types.TypeFile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.ModelBase;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;

public class BulletType
extends ShootableType {
    public int flak = 0;
    public String flakParticles = "largesmoke";
    public boolean setEntitiesOnFire = false;
    public EnumWeaponType weaponType = EnumWeaponType.NONE;
    public String hitSound;
    public boolean hasLight = false;
    public float penetratingPower = 1.0f;
    public boolean lockOnToPlanes = false;
    public boolean lockOnToVehicles = false;
    public boolean lockOnToMechas = false;
    public boolean lockOnToPlayers = false;
    public boolean lockOnToLivings = false;
    public float maxLockOnAngle = 45.0f;
    public float lockOnForce = 1.0f;
    public ArrayList<PotionEffect> hitEffects = new ArrayList();
    public static List<BulletType> bullets = new ArrayList<BulletType>();

    public BulletType(TypeFile file) {
        super(file);
        this.texture = "defaultBullet";
        bullets.add(this);
    }

    @Override
    protected void read(String[] split, TypeFile file) {
        super.read(split, file);
        try {
            if (split[0].equals("FlakParticles")) {
                this.flak = Integer.parseInt(split[1]);
            } else if (split[0].equals("FlakParticleType")) {
                this.flakParticles = split[1];
            } else if (split[0].equals("SetEntitiesOnFire")) {
                this.setEntitiesOnFire = Boolean.parseBoolean(split[1]);
            } else if (split[0].equals("HitSound")) {
                this.hitSound = split[1];
            } else if (split[0].equals("Penetrates")) {
                this.penetratingPower = Boolean.parseBoolean(split[1].toLowerCase()) ? 1.0f : 0.25f;
            } else if (split[0].equals("Penetration") || split[0].equals("PenetratingPower")) {
                this.penetratingPower = Float.parseFloat(split[1]);
            } else if (split[0].equals("Bomb")) {
                this.weaponType = EnumWeaponType.BOMB;
            } else if (split[0].equals("Shell")) {
                this.weaponType = EnumWeaponType.SHELL;
            } else if (split[0].equals("Missile")) {
                this.weaponType = EnumWeaponType.MISSILE;
            } else if (split[0].equals("WeaponType")) {
                this.weaponType = EnumWeaponType.valueOf(split[1].toUpperCase());
            } else if (split[0].equals("HasLight")) {
                this.hasLight = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("LockOnToDriveables")) {
                this.lockOnToVehicles = this.lockOnToMechas = Boolean.parseBoolean(split[1].toLowerCase());
                this.lockOnToPlanes = this.lockOnToMechas;
            } else if (split[0].equals("LockOnToVehicles")) {
                this.lockOnToVehicles = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("LockOnToPlanes")) {
                this.lockOnToPlanes = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("LockOnToMechas")) {
                this.lockOnToMechas = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("LockOnToPlayers")) {
                this.lockOnToPlayers = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("LockOnToLivings")) {
                this.lockOnToLivings = Boolean.parseBoolean(split[1].toLowerCase());
            } else if (split[0].equals("MaxLockOnAngle")) {
                this.maxLockOnAngle = Float.parseFloat(split[1]);
            } else if (split[0].equals("LockOnForce") || split[0].equals("TurningForce")) {
                this.lockOnForce = Float.parseFloat(split[1]);
            } else if (split[0].equals("PotionEffect")) {
                this.hitEffects.add(BulletType.getPotionEffect(split));
            }
        }
        catch (Exception e) {
            System.out.println("Reading bullet file failed.");
            e.printStackTrace();
        }
    }

    public static BulletType getBullet(String s) {
        for (BulletType bullet : bullets) {
            if (!bullet.shortName.equals(s)) continue;
            return bullet;
        }
        return null;
    }

    public static BulletType getBullet(Item item) {
        for (BulletType bullet : bullets) {
            if (bullet.item != item) continue;
            return bullet;
        }
        return null;
    }

    @Override
    public void reloadModel() {
        this.model = FlansMod.proxy.loadModel(this.modelString, this.shortName, ModelBase.class);
    }
}

