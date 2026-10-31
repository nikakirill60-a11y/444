/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.model.AdvancedModelLoader
 *  net.minecraftforge.client.model.IModelCustom
 *  org.lwjgl.opengl.GL11
 */
package metro.armor_utill;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import metro.armor_utill.ArmorCustomModel;
import metro.common.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class ArmorCustomModelTest
extends ArmorCustomModel {
    public static IModelCustom model_armor = null;
    public static ResourceLocation texture_armor = null;
    public static final IModelCustom model = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/boi_armor/model.obj"));
    public static final ResourceLocation texture = new ResourceLocation("metro", "models/armor/boi_armor/texture.png");
    public static final ResourceLocation texture_helm = new ResourceLocation("metro", "models/armor/boi_armor/texture_helm.png");
    public static final IModelCustom gasmask = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/gasmask/model.obj"));
    public static final ResourceLocation gasmask_texture = new ResourceLocation("metro", "models/armor/gasmask/texp.png");
    public static final IModelCustom hanza = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/hanza/model.obj"));
    public static final ResourceLocation hanza_texture = new ResourceLocation("metro", "models/armor/hanza/texture.png");
    public static final IModelCustom himza = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/himza/model.obj"));
    public static final ResourceLocation himza_texture = new ResourceLocation("metro", "models/armor/himza/texture.png");
    public static final IModelCustom officer_ozk = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/officer_ozk/model.obj"));
    public static final ResourceLocation officer_ozk_texture = new ResourceLocation("metro", "models/armor/officer_ozk/texture.png");
    public static final IModelCustom ozk = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/ozk/model.obj"));
    public static final ResourceLocation ozk_texture = new ResourceLocation("metro", "models/armor/ozk/texture.png");
    public static final IModelCustom povstanec = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/povstanec/model.obj"));
    public static final ResourceLocation povstanec_texture = new ResourceLocation("metro", "models/armor/povstanec/texture.png");
    public static final IModelCustom psh1b = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/psh1b/model.obj"));
    public static final ResourceLocation psh1b_texture = new ResourceLocation("metro", "models/armor/psh1b/texture.png");
    public static final IModelCustom Scout_armor = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/Scout_armor/model.obj"));
    public static final ResourceLocation Scout_armor_texture = new ResourceLocation("metro", "models/armor/Scout_armor/texture.png");
    public static final IModelCustom sverchok_mp6 = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/sverchok_mp6/model.obj"));
    public static final ResourceLocation sverchok_mp6_texture = new ResourceLocation("metro", "models/armor/sverchok_mp6/texture.png");
    public static final IModelCustom bandit_armor = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/bandit_armor/model.obj"));
    public static final ResourceLocation bandit_armor_texture = new ResourceLocation("metro", "models/armor/bandit_armor/texture.png");
    public static final IModelCustom banditkurtka = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/banditkurtka/model.obj"));
    public static final ResourceLocation banditkurtka_texture = new ResourceLocation("metro", "models/armor/banditkurtka/texture.png");
    public static final IModelCustom bkz = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/bkz/model.obj"));
    public static final ResourceLocation bkz_texture = new ResourceLocation("metro", "models/armor/bkz/texture.png");
    public static final IModelCustom BTBKZ = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/BTBKZ/model.obj"));
    public static final ResourceLocation BTBKZ_texture = new ResourceLocation("metro", "models/armor/BTBKZ/texture.png");
    public static final IModelCustom ghillie = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/ghillie/model.obj"));
    public static final ResourceLocation ghillie_texture = new ResourceLocation("metro", "models/armor/ghillie/texture.png");
    public static final IModelCustom gorka = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/gorka/model.obj"));
    public static final ResourceLocation gorka_texture = new ResourceLocation("metro", "models/armor/gorka/texture.png");
    public static final IModelCustom juggernaut = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/juggernaut/model.obj"));
    public static final ResourceLocation juggernaut_texture = new ResourceLocation("metro", "models/armor/juggernaut/texture.png");
    public static final IModelCustom kitel = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/kitel/model.obj"));
    public static final ResourceLocation kitel_texture = new ResourceLocation("metro", "models/armor/kitel/texture.png");
    public static final IModelCustom kurtka = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/kurtka/model.obj"));
    public static final ResourceLocation kurtka_texture = new ResourceLocation("metro", "models/armor/kurtka/texture.png");
    public static final IModelCustom olkhon = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/olkhon/model.obj"));
    public static final ResourceLocation olkhon_texture = new ResourceLocation("metro", "models/armor/olkhon/texture.png");
    public static final IModelCustom plash = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/plash/model.obj"));
    public static final ResourceLocation plash_texture = new ResourceLocation("metro", "models/armor/plash/texture.png");
    public static final IModelCustom police = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/police/model.obj"));
    public static final ResourceLocation police_texture = new ResourceLocation("metro", "models/armor/police/texture.png");
    public static final IModelCustom sniper = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/sniper/model.obj"));
    public static final ResourceLocation sniper_texture = new ResourceLocation("metro", "models/armor/sniper/texture.png");
    public static final IModelCustom sniper_camo = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/sniper_camo/model.obj"));
    public static final ResourceLocation sniper_camo_texture = new ResourceLocation("metro", "models/armor/sniper_camo/texture.png");
    public static final IModelCustom standart_k = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/standart_k/model.obj"));
    public static final ResourceLocation standart_k_texture = new ResourceLocation("metro", "models/armor/standart_k/texture.png");
    public static final IModelCustom sverchok = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/sverchok/model.obj"));
    public static final ResourceLocation sverchok_texture = new ResourceLocation("metro", "models/armor/sverchok/texture.png");
    public static final IModelCustom taiga = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/taiga/model.obj"));
    public static final ResourceLocation taiga_texture = new ResourceLocation("metro", "models/armor/taiga/texture.png");
    public static final IModelCustom vatnik = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/vatnik/model.obj"));
    public static final ResourceLocation vatnik_texture = new ResourceLocation("metro", "models/armor/vatnik/texture.png");
    public static final IModelCustom voen_tactical = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/voen_tactical/model.obj"));
    public static final ResourceLocation voen_tactical_texture = new ResourceLocation("metro", "models/armor/voen_tactical/texture.png");
    public static final IModelCustom zapal = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/zapal/model.obj"));
    public static final ResourceLocation zapal_texture = new ResourceLocation("metro", "models/armor/zapal/texture.png");
    public static final IModelCustom prorok = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/prorok/model.obj"));
    public static final ResourceLocation prorok_texture = new ResourceLocation("metro", "models/armor/prorok/texture.png");
    public static final IModelCustom cherv = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/cherv/model.obj"));
    public static final ResourceLocation cherv_texture = new ResourceLocation("metro", "models/armor/cherv/tex.png");
    public static final IModelCustom reih = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/reih/model.obj"));
    public static final ResourceLocation reih_texture = new ResourceLocation("metro", "models/armor/reih/tex.png");
    public static final IModelCustom mgu = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/armor/mgu/model.obj"));
    public static final ResourceLocation mgu_texture = new ResourceLocation("metro", "models/armor/mgu/tex.png");
    private final int partType;
    int type;

    @SideOnly(value=Side.CLIENT)
    public ArmorCustomModelTest(int armorType, int type) {
        this.partType = armorType;
        this.type = type;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void pre() {
        if (!Main.isServer) {
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            switch (this.type) {
                case 1: {
                    model_armor = gasmask;
                    texture_armor = gasmask_texture;
                    break;
                }
                case 2: {
                    model_armor = hanza;
                    texture_armor = hanza_texture;
                    break;
                }
                case 3: {
                    model_armor = himza;
                    texture_armor = himza_texture;
                    break;
                }
                case 4: {
                    model_armor = officer_ozk;
                    texture_armor = officer_ozk_texture;
                    break;
                }
                case 5: {
                    model_armor = ozk;
                    texture_armor = ozk_texture;
                    break;
                }
                case 6: {
                    model_armor = povstanec;
                    texture_armor = povstanec_texture;
                    break;
                }
                case 7: {
                    model_armor = psh1b;
                    texture_armor = psh1b_texture;
                    break;
                }
                case 8: {
                    model_armor = Scout_armor;
                    texture_armor = Scout_armor_texture;
                    break;
                }
                case 9: {
                    model_armor = sverchok_mp6;
                    texture_armor = sverchok_mp6_texture;
                    break;
                }
                case 10: {
                    model_armor = bandit_armor;
                    texture_armor = bandit_armor_texture;
                    break;
                }
                case 11: {
                    model_armor = banditkurtka;
                    texture_armor = banditkurtka_texture;
                    break;
                }
                case 12: {
                    model_armor = bkz;
                    texture_armor = bkz_texture;
                    break;
                }
                case 13: {
                    model_armor = BTBKZ;
                    texture_armor = bkz_texture;
                    break;
                }
                case 14: {
                    model_armor = ghillie;
                    texture_armor = ghillie_texture;
                    break;
                }
                case 15: {
                    model_armor = gorka;
                    texture_armor = gorka_texture;
                    break;
                }
                case 16: {
                    model_armor = juggernaut;
                    texture_armor = juggernaut_texture;
                    break;
                }
                case 17: {
                    model_armor = kitel;
                    texture_armor = kitel_texture;
                    break;
                }
                case 18: {
                    model_armor = kurtka;
                    texture_armor = kurtka_texture;
                    break;
                }
                case 19: {
                    model_armor = olkhon;
                    texture_armor = olkhon_texture;
                    break;
                }
                case 20: {
                    model_armor = plash;
                    texture_armor = plash_texture;
                    break;
                }
                case 21: {
                    model_armor = police;
                    texture_armor = police_texture;
                    break;
                }
                case 22: {
                    model_armor = sniper;
                    texture_armor = sniper_texture;
                    break;
                }
                case 23: {
                    model_armor = sniper_camo;
                    texture_armor = sniper_camo_texture;
                    break;
                }
                case 24: {
                    model_armor = standart_k;
                    texture_armor = standart_k_texture;
                    break;
                }
                case 25: {
                    model_armor = sverchok;
                    texture_armor = sverchok_texture;
                    break;
                }
                case 26: {
                    model_armor = taiga;
                    texture_armor = taiga_texture;
                    break;
                }
                case 27: {
                    model_armor = vatnik;
                    texture_armor = vatnik_texture;
                    break;
                }
                case 28: {
                    model_armor = voen_tactical;
                    texture_armor = voen_tactical_texture;
                    break;
                }
                case 29: {
                    model_armor = zapal;
                    texture_armor = zapal_texture;
                    break;
                }
                case 30: {
                    model_armor = prorok;
                    texture_armor = prorok_texture;
                    break;
                }
                case 31: {
                    model_armor = cherv;
                    texture_armor = cherv_texture;
                    break;
                }
                case 32: {
                    model_armor = reih;
                    texture_armor = reih_texture;
                    break;
                }
                case 33: {
                    model_armor = mgu;
                    texture_armor = mgu_texture;
                }
            }
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void post() {
        GL11.glDisable((int)3042);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void partHead() {
        if (!Main.isServer) {
            GL11.glScalef((float)1.05f, (float)1.05f, (float)1.05f);
            GL11.glTranslatef((float)0.0f, (float)-1.5f, (float)0.0f);
            GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            String part = "head";
            Minecraft.func_71410_x().field_71446_o.func_110577_a(texture_armor);
            model_armor.renderPart(part);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void partBody() {
        if (!Main.isServer) {
            String part = "body";
            GL11.glScalef((float)1.05f, (float)1.05f, (float)1.05f);
            GL11.glTranslatef((float)0.0f, (float)-1.45f, (float)0.0f);
            GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            Minecraft.func_71410_x().field_71446_o.func_110577_a(texture_armor);
            model_armor.renderPart(part);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void partRightArm() {
        if (!Main.isServer) {
            String part = "rightarm";
            GL11.glScalef((float)1.05f, (float)1.1f, (float)1.05f);
            GL11.glTranslatef((float)0.32f, (float)-1.34f, (float)0.0f);
            GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            Minecraft.func_71410_x().field_71446_o.func_110577_a(texture_armor);
            model_armor.renderPart(part);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void partLeftArm() {
        if (!Main.isServer) {
            String part = "leftarm";
            GL11.glScalef((float)1.05f, (float)1.1f, (float)1.05f);
            GL11.glTranslatef((float)-0.31f, (float)-1.34f, (float)0.0f);
            GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            Minecraft.func_71410_x().field_71446_o.func_110577_a(texture_armor);
            model_armor.renderPart(part);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void partRightLeg() {
        if (!Main.isServer) {
            String part = "rightleg";
            GL11.glScalef((float)1.05f, (float)1.05f, (float)1.05f);
            GL11.glTranslatef((float)0.125f, (float)-0.7f, (float)0.0f);
            GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            Minecraft.func_71410_x().field_71446_o.func_110577_a(texture_armor);
            model_armor.renderPart(part);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void partLeftLeg() {
        if (!Main.isServer) {
            String part = "leftleg";
            GL11.glScalef((float)1.05f, (float)1.05f, (float)1.05f);
            GL11.glTranslatef((float)-0.12f, (float)-0.7f, (float)0.0f);
            GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            Minecraft.func_71410_x().field_71446_o.func_110577_a(texture_armor);
            model_armor.renderPart(part);
        }
    }
}
