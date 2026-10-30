package ru.metrocraft.port;

/** Data generated from the original Flan pack, rather than a hard-coded duplicate catalogue. */
public final class Definition {
    public String id, kind, name, description, source, shortName;
    public int stackSize = 64, durability;
    public String ammo, shootSound, reloadSound;
    public int capacity, reloadTicks, delay, pellets = 1;
    public float damage, spread;
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
