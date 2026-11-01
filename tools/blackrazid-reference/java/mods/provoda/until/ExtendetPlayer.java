/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aab
 *  bs
 *  cf
 *  mp
 *  net.minecraftforge.common.IExtendedEntityProperties
 *  sq
 */
package mods.provoda.until;

import mods.provoda.ModBase;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendetPlayer
implements IExtendedEntityProperties {
    public static final String EXT_PROP_NAME = "ExtendedPlayer";
    private final sq player;
    private int air;
    private int MaxAir;
    private int Timer;
    private int MaxTimer;
    private int ThirstTimer;
    private float energy;
    private float thirst;
    private boolean ReloadKeyPressed;
    private boolean ShootKeyPressed;
    private boolean LampKeyP;
    private int AptTimer;
    private int soundTimer;
    private int shootTimer;
    private int factionWatcher = 31;
    private int medTimer;
    private int factionId;
    private boolean firstlogin;

    public ExtendetPlayer(sq player) {
        this.player = player;
        this.MaxAir = 100;
        this.air = 100;
        this.MaxTimer = 21;
        this.Timer = 21;
        this.thirst = 100.0f;
        this.ThirstTimer = 64;
        this.shootTimer = 0;
        this.medTimer = 0;
        this.ReloadKeyPressed = false;
        this.ShootKeyPressed = false;
        this.LampKeyP = false;
        this.player.u().a(this.factionWatcher, (Object)15);
        this.firstlogin = true;
    }

    public static final void register(sq player) {
        player.registerExtendedProperties(EXT_PROP_NAME, (IExtendedEntityProperties)new ExtendetPlayer(player));
    }

    public static final ExtendetPlayer get(sq player) {
        return (ExtendetPlayer)player.getExtendedProperties(EXT_PROP_NAME);
    }

    public void saveNBTData(bs compound) {
        bs properties = new bs();
        properties.a("FirstLogin", this.firstlogin);
        properties.a("AIR", this.air);
        properties.a("MedTimer", this.medTimer);
        properties.a("MAXAIR", this.MaxAir);
        properties.a("TIMER", this.Timer);
        properties.a("ThistTimer", this.ThirstTimer);
        properties.a("MAXTimer", this.MaxTimer);
        properties.a("Thirst", this.thirst);
        properties.a("ENERGY", this.energy);
        properties.a("ST", this.shootTimer);
        properties.a("ReloadKeyPressed", this.ReloadKeyPressed);
        properties.a("ShootKeyPressed", this.ShootKeyPressed);
        properties.a("LampKeyP", this.LampKeyP);
        properties.a("Faction", this.player.u().c(this.factionWatcher));
        compound.a(EXT_PROP_NAME, (cf)properties);
        compound.a(EXT_PROP_NAME, (cf)properties);
    }

    public void loadNBTData(bs compound) {
        bs properties = (bs)compound.a(EXT_PROP_NAME);
        this.firstlogin = properties.n("FirstLogin");
        this.air = properties.e("AIR");
        this.medTimer = properties.e("MedTimer");
        this.MaxAir = properties.e("MAXAIR");
        this.Timer = properties.e("TIMER");
        this.MaxTimer = properties.e("MAXTimer");
        this.thirst = properties.g("Thirst");
        this.energy = properties.g("ENERGY");
        this.shootTimer = properties.e("ST");
        this.ThirstTimer = properties.e("ThistTimer");
        this.ReloadKeyPressed = properties.n("ReloadKeyPressed");
        this.ShootKeyPressed = properties.n("ShootKeyPressed");
        this.LampKeyP = properties.n("LampKeyP");
        this.player.u().b(this.factionWatcher, (Object)properties.e("Faction"));
    }

    public void init(mp entity, aab world) {
    }

    public void firstlogin() {
        this.firstlogin = false;
    }

    public boolean getfirstlogin() {
        return this.firstlogin;
    }

    public void setMedTimer() {
        this.medTimer = 30;
    }

    public int medTimer() {
        return this.medTimer;
    }

    public void consumeMedTimer() {
        if (this.medTimer > 0) {
            --this.medTimer;
        }
    }

    public void setFaction(int i) {
        this.player.u().b(this.factionWatcher, (Object)i);
    }

    public int getFaction() {
        return this.player.u().c(this.factionWatcher);
    }

    public boolean thirsted() {
        if (this.thirst > 0.0f) {
            if (!this.player.ce.d) {
                this.thirst -= 0.004f;
            }
            return true;
        }
        return false;
    }

    public boolean energyLeft() {
        if (this.energy > 0.0f) {
            this.energy -= 0.015f;
            return true;
        }
        return false;
    }

    public int getST() {
        return this.shootTimer;
    }

    public void soundTimer() {
        this.soundTimer = 38;
    }

    public boolean soundTimerTick() {
        if (this.soundTimer > 0) {
            --this.soundTimer;
            return false;
        }
        this.soundTimer();
        return true;
    }

    public void aptTimer() {
        this.AptTimer = 300;
    }

    public void aptTimerTick(sq p) {
        if (this.AptTimer > 0) {
            --this.AptTimer;
        } else if (p.bK.a[13] != null && p.bK.a[13].b() != ModBase.Med) {
            p.bK.a[13] = null;
        }
    }

    public int ST() {
        return this.shootTimer;
    }

    public void consumeST() {
        if (this.shootTimer > 0) {
            --this.shootTimer;
        }
    }

    public void upST(int i) {
        this.shootTimer = i;
    }

    public void thirstUp() {
        if (this.thirst < 100.0f) {
            this.thirst += 30.0f;
        }
    }

    public void energyUp() {
        if (this.energy < 100.0f) {
            this.energy += 20.0f;
        }
    }

    public float energyGet() {
        return this.energy;
    }

    public int thirst() {
        return (int)this.thirst;
    }

    public boolean consumeAir() {
        if (this.air > 1) {
            --this.air;
            return true;
        }
        this.replenishAir();
        return false;
    }

    public boolean Timerset() {
        if (this.Timer == 0) {
            this.replenishTimer();
            return true;
        }
        --this.Timer;
        return false;
    }

    public boolean ThirstTimerSet() {
        if (this.ThirstTimer == 0) {
            this.replenishThirstTimer();
            return true;
        }
        --this.ThirstTimer;
        return false;
    }

    public void replenishAir() {
        this.air = this.MaxAir;
    }

    public void maxWater(int i) {
        this.thirst = i;
    }

    public void replenishThirstTimer() {
        this.ThirstTimer = 64;
    }

    public void replenishTimer() {
        this.Timer = 21;
    }

    public int getAir() {
        return this.air;
    }

    public void setReloadKeyPressed() {
        this.ReloadKeyPressed = true;
    }

    public void setReloadKeyUnpressed() {
        this.ReloadKeyPressed = false;
    }

    public boolean getReloadKeyPressed() {
        return this.ReloadKeyPressed;
    }

    public void setShootKeyPressed() {
        this.ShootKeyPressed = true;
    }

    public void setShootKeyUnpressed() {
        this.ShootKeyPressed = false;
    }

    public boolean getShootKeyPressed() {
        return this.ShootKeyPressed;
    }

    public void setLampKeyP() {
        this.LampKeyP = true;
    }

    public void setLampKeyUp() {
        this.LampKeyP = false;
    }

    public boolean getLampKeyP() {
        return this.LampKeyP;
    }
}
