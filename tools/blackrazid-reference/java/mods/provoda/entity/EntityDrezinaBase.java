/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  bs
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  mods.provoda.entity.RotativePoint
 *  mp
 *  sq
 *  wk
 *  wm
 */
package mods.provoda.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import mods.provoda.entity.EntityDrezinaStock;
import mods.provoda.entity.RotativePoint;

public class EntityDrezinaBase
extends EntityDrezinaStock {
    public boolean horn = false;
    public boolean zeroReverse = false;
    public boolean brake;
    public boolean pistonfx = false;
    public boolean isHeadLightOn = false;
    public boolean hasLight = false;
    public byte hornStage = 0;
    public byte brakeStage = 0;
    public byte reverse = 0;
    public byte regulator = 0;
    public float maxForce = 10.0f;
    public float maxClutchSpeed;
    public RotativePoint smokePos = new RotativePoint((mp)this, 0.0f, 0.0f, 0.0f);
    public RotativePoint steamPosLeft = new RotativePoint((mp)this, 0.0f, 0.0f, 0.0f);
    public RotativePoint steamPosRight = new RotativePoint((mp)this, 0.0f, 0.0f, 0.0f);
    public RotativePoint lightsPos = new RotativePoint((mp)this, 0.0f, 0.0f, 0.0f);
    public int lastLightPosX = 0;
    public int lastLightPosY = -1;
    public int lastLightPosZ = 0;
    public int fuel = 0;
    public int maxFuel = 4000;
    public int colour;
    public String hornSound = "";
    public String pistonSound = "";
    int maxCountOfPlayers = 2;
    List riddenByEntitys = new ArrayList();
    public static final int[] colours = new int[]{0x505050, 0xFF0000, 3887386, 5320730, 2437522, 8073150, 2651799, 0xABABAB, 0x434343, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 0xF0F0F0};

    public EntityDrezinaBase(aab par1World) {
        super(par1World);
        this.colour = colours[0];
        this.ah.a(9, (Object)new Integer(0));
        this.ah.a(10, (Object)new Integer(0));
        this.ah.a(12, (Object)new String());
        this.a();
    }

    public boolean a_(sq player) {
        for (int item = 0; item < this.riddenByEntitys.size(); ++item) {
            if (!player.equals(this.riddenByEntitys.get(item))) continue;
            this.riddenByEntitys.remove(item);
            System.out.println("\u041f\u0430\u0441\u0441\u0430\u0436\u0438\u0440 \u0441\u043e\u0448\u0435\u043b");
            return true;
        }
        if (this.n != null && this.riddenByEntitys.size() < this.maxCountOfPlayers && this.n instanceof sq && this.n != player) {
            System.out.println("\u0421\u0430\u0434\u0438\u043c \u0438\u0433\u0440\u043e\u043a\u0430");
            this.riddenByEntitys.add(player);
            return true;
        }
        if (this.n != null && this.n != player) {
            this.n.a((mp)null);
            return false;
        }
        wm var3 = player.bK.h();
        if (!this.q.I && var3 != null) {
            if (var3.b() == wk.r && player.ce.d) {
                this.isHeadLightOn = false;
                this.q.e((mp)this);
                return true;
            }
            if (var3.b() == wk.n && this.fuel < this.maxFuel) {
                this.fuel += 1000;
                if (!player.ce.d && --var3.a == 0) {
                    player.bK.a(player.bK.c, (wm)null);
                }
                return true;
            }
        }
        if (!this.q.I) {
            player.a((mp)this);
            return true;
        }
        return false;
    }

    @Override
    public void l_() {
        this.onLocoUpdate();
        this.x();
        if (!this.riddenByEntitys.isEmpty()) {
            for (int i = 0; i < this.riddenByEntitys.size(); ++i) {
                sq player = (sq)this.riddenByEntitys.get(i);
                player.x = 0.0;
                player.y = 0.0;
                player.z = 0.0;
                this.setPlayerPlace(player, i);
            }
        }
    }

    public void setPlayerPlace(sq player, int place) {
        switch (place) {
            default: 
        }
    }

    public void onLocoUpdate() {
        if ((this.wheelAngle[0] <= -20.0f || this.wheelAngle[0] >= 20.0f) && this.wheelAngle[0] <= 160.0f && this.wheelAngle[0] >= -160.0f) {
            this.pistonfx = false;
        } else {
            if (!this.q.I && !this.pistonfx && this.fuel > 0 && this.reverse != 0) {
                this.q.a((mp)this, "row:" + this.pistonSound, 1.5f, 1.0f + 0.5f * (float)Math.abs(this.reverse) / 128.0f);
            } else if (this.q.I) {
                this.spawnPistonFx();
            }
            this.pistonfx = true;
        }
        if (this.zeroReverse) {
            if (this.reverse > 5) {
                this.reverse = (byte)(this.reverse - 5);
            } else if (this.reverse < -5) {
                this.reverse = (byte)(this.reverse + 5);
            } else {
                this.zeroReverse = false;
                this.reverse = 0;
            }
        }
        if (this.brake) {
            this.x *= 0.8;
            this.y *= 0.8;
            this.z *= 0.8;
            if (this.brakeStage == 0) {
                this.q.a((mp)this, "row:brake", 1.0f, 1.5f);
            }
        }
        if (this.brake && this.brakeStage < 30) {
            this.brakeStage = (byte)(this.brakeStage + 1);
        }
        if (!this.brake && this.brakeStage > 0) {
            this.brakeStage = (byte)(this.brakeStage - 1);
        }
        if (this.hornStage == 50 && this.fuel > 0) {
            this.q.a((mp)this, "row:" + this.hornSound, 2.0f, 1.0f);
        }
        this.hornStage = this.hornStage > 1 ? (byte)(this.hornStage - 1) : (byte)0;
        float trainmass = this.mass;
        if (this.fuel <= 0 && this.maxFuel != 100) {
            this.fuel = 0;
        } else if (!this.q.I) {
            if (!this.brake && this.reverse != 0) {
                float c = (float)this.reverse * this.maxForce / 64.0f / trainmass / 256.0f;
                this.applyForwardMotion(c);
            }
            --this.fuel;
        } else {
            this.spawnFx();
        }
        if (!this.q.I) {
            this.ah.b(9, (Object)this.fuel);
            this.ah.b(10, (Object)this.colour);
            this.ah.b(12, (Object)String.valueOf(this.label));
        } else {
            this.fuel = this.ah.c(9);
            this.colour = this.ah.c(10);
            this.label = this.ah.e(12);
        }
        if (this.brake) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnFx() {
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnPistonFx() {
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    protected void a(bs nbttagcompound) {
        super.a(nbttagcompound);
        this.fuel = nbttagcompound.e("fuel");
        this.reverse = nbttagcompound.c("reverse");
    }

    @Override
    protected void b(bs nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.a("fuel", this.fuel);
        nbttagcompound.a("reverse", this.reverse);
    }
}
