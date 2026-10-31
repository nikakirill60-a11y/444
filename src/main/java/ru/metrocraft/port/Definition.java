package ru.metrocraft.port;

/** Data generated from the original Flan pack, rather than a hard-coded duplicate catalogue. */
public final class Definition {
    public String id, kind, name, description, source, shortName;
    public int stackSize = 64, durability;
    public String ammo, shootSound, reloadSound;
    public int capacity, reloadTicks, delay, pellets = 1;
    public float damage, spread;
    public float bulletSpeed=40, fallSpeed=.25F, hitBoxSize=.5F;
    public boolean penetrates, breaksGlass;
    public float recoil, randomRecoil=2.5F, randomYaw=1, decreaseRecoil=2, decreaseYaw=1.5F;
    public String scope="None", zoomOverlay="None", armorOverlay="None";
    public float recoilMultiplier=1, bulletSpeedMultiplier=1;
    public boolean silencer;
    public float moveSpeedModifier=1, knockbackModifier=.2F, jumpModifier=1;
    public boolean negateFallDamage;
    public boolean automatic;
    public String slot;
    public float defence;
    public boolean nightVision, gasMask;
    public float heal;
    public int uses = 1;
    public boolean repairGun, repairArmor;
    public String[] allowedAttachments = new String[0];
    public String attachmentSlot;
    public float damageMultiplier = 1, spreadMultiplier = 1, reloadMultiplier = 1, zoom = 1;
}
