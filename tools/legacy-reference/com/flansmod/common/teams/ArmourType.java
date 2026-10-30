/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 */
package com.flansmod.common.teams;

import com.flansmod.client.model.ModelCustomArmour;
import com.flansmod.common.FlansMod;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.types.TypeFile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;

public class ArmourType
extends InfoType {
    public static ArrayList<ArmourType> armours = new ArrayList();
    public int type;
    public double defence;
    public boolean isGasMask = false;
    public int durability = 5000;
    public String armourTextureName;
    public float moveSpeedModifier = 1.0f;
    public float knockbackModifier = 0.2f;
    public float jumpModifier = 1.0f;
    public boolean nightVision = false;
    public String overlay = null;
    public boolean smokeProtection = false;
    public boolean negateFallDamage = false;
    @SideOnly(value=Side.CLIENT)
    public ModelCustomArmour model;

    public ArmourType(TypeFile file) {
        super(file);
        armours.add(this);
    }

    @Override
    protected void read(String[] split, TypeFile file) {
        super.read(split, file);
        try {
            if (FMLCommonHandler.instance().getSide().isClient() && split[0].equals("Model")) {
                this.model = FlansMod.proxy.loadModel(split[1], this.shortName, ModelCustomArmour.class);
                this.model.type = this;
            }
            if (split[0].equals("Type")) {
                if (split[1].equals("Hat") || split[1].equals("Helmet")) {
                    this.type = 0;
                }
                if (split[1].equals("Chest") || split[1].equals("Body")) {
                    this.type = 1;
                }
                if (split[1].equals("Legs") || split[1].equals("Pants")) {
                    this.type = 2;
                }
                if (split[1].equals("Shoes") || split[1].equals("Boots")) {
                    this.type = 3;
                }
            }
            if (split[0].equals("Durability")) {
                this.durability = Integer.parseInt(split[1]);
            }
            if (split[0].equals("DamageReduction") || split[0].equals("Defence")) {
                this.defence = Double.parseDouble(split[1]);
            }
            if (split[0].equals("MoveSpeedModifier") || split[0].equals("Slowness")) {
                this.moveSpeedModifier = Float.parseFloat(split[1]);
            }
            if (split[0].equals("JumpModifier")) {
                this.jumpModifier = Float.parseFloat(split[1]);
            }
            if (split[0].equals("KnockbackReduction") || split[0].equals("KnockbackModifier")) {
                this.knockbackModifier = Float.parseFloat(split[1]);
            }
            if (split[0].equals("NightVision")) {
                this.nightVision = Boolean.parseBoolean(split[1]);
            }
            if (split[0].equals("NegateFallDamage")) {
                this.negateFallDamage = Boolean.parseBoolean(split[1]);
            }
            if (split[0].equals("Overlay")) {
                this.overlay = split[1];
            }
            if (split[0].equals("SmokeProtection")) {
                this.smokeProtection = Boolean.parseBoolean(split[1]);
            }
            if (split[0].equals("GasMask")) {
                this.isGasMask = Boolean.parseBoolean(split[1]);
            }
            if (split[0].equals("ArmourTexture") || split[0].equals("ArmorTexture")) {
                this.armourTextureName = split[1];
            }
        }
        catch (Exception e) {
            System.out.println("Reading armour file failed.");
            e.printStackTrace();
        }
    }

    public static ArmourType getArmourType(String string) {
        for (ArmourType armour : armours) {
            if (!armour.shortName.equals(string)) continue;
            return armour;
        }
        return null;
    }

    @Override
    public void reloadModel() {
        this.model = FlansMod.proxy.loadModel(this.modelString, this.shortName, ModelCustomArmour.class);
        if (this.model != null) {
            this.model.type = this;
        }
    }
}

