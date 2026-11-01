/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  metro.client.render.guns.ArmorArm
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  net.minecraftforge.client.model.AdvancedModelLoader
 *  net.minecraftforge.client.model.IModelCustom
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package metro.client.render.guns;

import java.time.LocalTime;
import java.util.Random;
import metro.client.events.EventsClient;
import metro.client.render.guns.ArmorArm;
import metro.common.gun.ItemGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ak74_Render
implements IItemRenderer {
    public Minecraft mc = Minecraft.func_71410_x();
    boolean s;
    public boolean idle = true;
    public boolean idle1 = true;
    public boolean aim = false;
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;
    public float xi = 0.0f;
    public float yi = 0.0f;
    public float xr = 0.0f;
    public float yr = 0.0f;
    public float zr = 0.0f;
    public float roty = 0.0f;
    public float rotx = 0.0f;
    public float roty_m = 0.0f;
    public float rotx_m = 0.0f;
    int zi = 1;
    public float devx;
    public float devy;
    public float devz;
    public float devRx;
    public float devRy;
    int ruk = 1;
    int ruk2 = 0;
    float rotyg;
    float zg;
    float rotgun1;
    float rotgun2;
    float gilz;
    float Zatvor;
    float Zatvor2;
    boolean dim;
    float dim_timer;
    int ym;
    float gl_rotY;
    float gl_rotX;
    int optic_y;
    private static IModelCustom objmodel = AdvancedModelLoader.loadModel((ResourceLocation)new ResourceLocation("metro", "models/guns/ak/model.obj"));
    public static final ResourceLocation texture = new ResourceLocation("metro", "models/guns/ak/texture.png");
    public static final ResourceLocation flash = new ResourceLocation("metro", "models/guns/ak/flash.png");
    public static final ResourceLocation texture4 = new ResourceLocation("metro", "models/guns/mods/texture.png");
    public static final ResourceLocation texturec = new ResourceLocation("metro", "models/guns/mods/texturec.png");
    public static final ResourceLocation texturen = new ResourceLocation("metro", "models/guns/mods/texturen.png");
    public static final ResourceLocation textures = new ResourceLocation("metro", "models/guns/mods/textures.png");
    public static final ResourceLocation texture_p = new ResourceLocation("metro", "models/guns/mods/texture_p.png");
    public static final ResourceLocation low = new ResourceLocation("metro", "models/guns/ak/texture_low.png");
    Random rnd = new Random();

    public boolean handleRenderType(ItemStack is, IItemRenderer.ItemRenderType type) {
        return type != IItemRenderer.ItemRenderType.INVENTORY;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack is, IItemRenderer.ItemRendererHelper helper) {
        return type != IItemRenderer.ItemRenderType.INVENTORY;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack is, Object ... data) {
        EntityClientPlayerMP p = this.mc.field_71439_g;
        WorldClient w = this.mc.field_71441_e;
        LocalTime localTime = LocalTime.now();
        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glPushMatrix();
            float scale11 = 0.0015f;
            GL11.glScalef((float)scale11, (float)scale11, (float)scale11);
            GL11.glRotatef((float)-60.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)45.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)25.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)-75.0f, (float)-200.0f, (float)150.0f);
            Minecraft.func_71410_x().field_71446_o.func_110577_a(low);
            objmodel.renderOnly(new String[]{"body", "mag"});
            if (is.field_77990_d != null) {
                if (is.field_77990_d.func_74762_e("modScope") == 1) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texture4);
                    objmodel.renderOnly(new String[]{"scope_a"});
                    GL11.glDisable((int)3042);
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texture_p);
                    objmodel.renderOnly(new String[]{"scope_g1"});
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texture4);
                    objmodel.renderOnly(new String[]{"scope_b"});
                    objmodel.renderOnly(new String[]{"scope_g2"});
                }
                if (is.field_77990_d.func_74762_e("modScope") == 2) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texturec);
                    objmodel.renderOnly(new String[]{"col"});
                }
                if (is.field_77990_d.func_74762_e("modScope") == 3) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texturen);
                    objmodel.renderOnly(new String[]{"scope2"});
                }
                if (is.field_77990_d.func_74762_e("modStvol") == 1) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(textures);
                    objmodel.renderOnly(new String[]{"slicer"});
                }
                if (is.field_77990_d.func_74762_e("modRessiver") == 1) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(textures);
                    objmodel.renderOnly(new String[]{"laser"});
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(textures);
                    objmodel.renderOnly(new String[]{"laser1"});
                }
            }
            GL11.glPopMatrix();
        }
        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glPushMatrix();
            float scale33 = 0.001f;
            GL11.glScalef((float)scale33, (float)(-scale33), (float)(-scale33));
            GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)-50.0f, (float)100.0f, (float)-100.0f);
            Minecraft.func_71410_x().field_71446_o.func_110577_a(low);
            objmodel.renderOnly(new String[]{"body", "mag"});
            if (is.field_77990_d != null) {
                if (is.field_77990_d.func_74762_e("modScope") == 1) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texture4);
                    objmodel.renderOnly(new String[]{"scope_a"});
                    GL11.glDisable((int)3042);
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texture_p);
                    objmodel.renderOnly(new String[]{"scope_g1"});
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texture4);
                    objmodel.renderOnly(new String[]{"scope_b"});
                    objmodel.renderOnly(new String[]{"scope_g2"});
                }
                if (is.field_77990_d.func_74762_e("modScope") == 2) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texturec);
                    objmodel.renderOnly(new String[]{"col"});
                }
                if (is.field_77990_d.func_74762_e("modScope") == 3) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texturen);
                    objmodel.renderOnly(new String[]{"scope2"});
                }
                if (is.field_77990_d.func_74762_e("modStvol") == 1) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(textures);
                    objmodel.renderOnly(new String[]{"slicer"});
                }
                if (is.field_77990_d.func_74762_e("modRessiver") == 1) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(textures);
                    objmodel.renderOnly(new String[]{"laser"});
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(textures);
                    objmodel.renderOnly(new String[]{"laser1"});
                }
            }
            GL11.glPopMatrix();
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            this.devx = 160.0f;
            this.devz = -750.0f;
            this.devy = 170.0f;
            this.devRx = -42.0f;
            this.devRy = 0.0f;
            float Xpric = -290.0f;
            float Ypric = 180.0f;
            float Zpric = 450.0f;
            if (is.field_77990_d.func_74762_e("modScope") == 0) {
                this.devy = 170.0f;
                Ypric = 180.0f;
                Zpric = 200.0f;
            }
            if (is.field_77990_d.func_74762_e("modScope") == 1) {
                this.devy = 180.0f;
                Ypric = 120.0f;
                Zpric = 450.0f;
            }
            if (is.field_77990_d.func_74762_e("modScope") == 2) {
                Ypric = 160.0f;
            }
            if (is.field_77990_d.func_74762_e("modScope") == 3) {
                Ypric = 130.0f;
            }
            float pspeed = 10.0f;
            this.gl_rotY = this.roty + this.roty_m + this.devRy + this.rotgun1 + this.rotgun2;
            this.gl_rotX = this.devRx + this.rotx + this.rotx_m + this.rotyg;
            float scale = 0.005f;
            GL11.glPushMatrix();
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)this.gl_rotX, (float)0.0f, (float)1.0f, (float)0.0f);
            if (is.field_77990_d.func_74762_e("modScope") == 1 && this.aim) {
                GL11.glTranslatef((float)(this.devx + this.x + this.xi), (float)(this.devy + this.y + (float)this.ym), (float)(this.devz + this.zg + this.z + this.Zatvor / 8.0f));
            } else {
                GL11.glTranslatef((float)(this.devx + this.x + this.xi), (float)(this.devy + this.y + (float)this.ym), (float)(this.devz + this.zg + this.z + this.Zatvor));
            }
            Minecraft.func_71410_x().field_71446_o.func_110577_a(texture);
            objmodel.renderOnly(new String[]{"stvol", "pric"});
            if (is.field_77990_d.func_74762_e("modScope") == 1) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(texture4);
                objmodel.renderOnly(new String[]{"scope_a"});
                GL11.glDisable((int)3042);
                Minecraft.func_71410_x().field_71446_o.func_110577_a(texture_p);
                objmodel.renderOnly(new String[]{"scope_g1"});
                if (this.z < 430.0f) {
                    this.optic_y = 0;
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(texture4);
                    objmodel.renderOnly(new String[]{"scope_b"});
                    objmodel.renderOnly(new String[]{"scope_g2"});
                } else {
                    this.optic_y = -150;
                }
            }
            if (is.field_77990_d.func_74762_e("modScope") == 2) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(texturec);
                objmodel.renderOnly(new String[]{"col"});
            }
            if (is.field_77990_d.func_74762_e("modScope") == 3) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(texturen);
                objmodel.renderOnly(new String[]{"scope2"});
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)this.gl_rotX, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)(this.devx + this.x + this.xi), (float)(this.devy + this.y + (float)this.ym + (float)this.optic_y), (float)(this.devz + this.zg + this.z + this.Zatvor));
            Minecraft.func_71410_x().field_71446_o.func_110577_a(texture);
            objmodel.renderOnly(new String[]{"body", "mag"});
            if (is.field_77990_d.func_74762_e("modStvol") == 1) {
                Minecraft.func_71410_x().field_71446_o.func_110577_a(textures);
                objmodel.renderOnly(new String[]{"slicer"});
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)this.gl_rotX, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)(this.devx + this.x + this.xi), (float)(this.devy + this.y + (float)this.ym), (float)(this.devz + this.z + this.zg + this.Zatvor * 3.0f));
            Minecraft.func_71410_x().field_71446_o.func_110577_a(texture);
            objmodel.renderOnly(new String[]{"zatv"});
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)this.gl_rotX, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)(this.devx + this.x + this.xi), (float)(this.devy + this.y + (float)this.ym + 100.0f), (float)(this.devz + this.z + this.zg + this.Zatvor / 2.0f));
            ArmorArm.renderB();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)this.gl_rotX, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)(this.devx + this.x + this.xi), (float)(this.devy + this.y + (float)this.ym + (float)this.optic_y), (float)(this.devz + this.z + this.zg + this.Zatvor / 2.0f));
            ArmorArm.renderA();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)this.gl_rotX, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)(this.devx + this.x + this.xi - 5.0f), (float)(this.devy + this.y + (float)this.ym + (float)this.optic_y), (float)(this.devz + this.zg + this.z + this.Zatvor));
            if (is.field_77990_d.func_74762_e("modRessiver") == 1) {
                OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)120.0f, (float)120.0f);
                GL11.glBlendFunc((int)770, (int)771);
                Minecraft.func_71410_x().field_71446_o.func_110577_a(textures);
                objmodel.renderOnly(new String[]{"laser"});
                GL11.glEnable((int)3042);
                GL11.glDisable((int)2896);
                OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
                GL11.glBlendFunc((int)770, (int)771);
                Minecraft.func_71410_x().field_71446_o.func_110577_a(textures);
                objmodel.renderOnly(new String[]{"laser1"});
                GL11.glEnable((int)2896);
                GL11.glDisable((int)3042);
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glEnable((int)3042);
            GL11.glDisable((int)2896);
            OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)(0.0f + this.gl_rotY), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)this.gl_rotX, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)(this.devx + this.x + this.xi), (float)(this.devy + this.y + (float)this.ym), (float)(this.devz + this.z + this.zg + this.Zatvor));
            Minecraft.func_71410_x().field_71446_o.func_110577_a(flash);
            if (ItemGun.fire && this.zi == 1) {
                this.dim = true;
                if (is.field_77990_d.func_74762_e("modStvol") != 1) {
                    objmodel.renderOnly(new String[]{"fire1"});
                }
            }
            if (this.dim) {
                this.dim_timer = (float)((double)this.dim_timer + 19.0 / (Double.valueOf(this.mc.field_71426_K.split(" ")[0]) / 100.0 * 2.0));
                int texture = (int)(this.dim_timer / 24.0f);
                if (texture <= 24) {
                    Minecraft.func_71410_x().field_71446_o.func_110577_a(new ResourceLocation("metro", "models/guns/smoke/tex_" + texture + ".png"));
                }
                if (this.dim_timer >= 576.0f) {
                    this.dim_timer = 0.0f;
                    this.dim = false;
                }
                objmodel.renderOnly(new String[]{"fire2"});
            }
            GL11.glEnable((int)2896);
            GL11.glDisable((int)3042);
            GL11.glPopMatrix();
            if (ItemGun.fire) {
                if (this.zi == 1) {
                    if (this.Zatvor < 70.0f) {
                        this.Zatvor += 53.0f;
                    } else {
                        this.zi = 2;
                    }
                }
                if (this.zi == 2) {
                    if (this.Zatvor >= 0.0f) {
                        this.Zatvor -= 50.0f;
                    } else {
                        this.zi = 1;
                        ItemGun.fire = false;
                    }
                }
            }
            this.aim = !EventsClient.guiUpgrade && Mouse.isButtonDown((int)1) && !this.mc.field_71439_g.func_70051_ag() && !is.field_77990_d.func_74767_n("isReload");
            if (this.aim && this.roty > -1.0f) {
                this.rotx = 0.0f;
                if (this.x > Xpric) {
                    this.x -= pspeed * 4.0f;
                }
                if (this.y < Ypric) {
                    this.y += pspeed * 2.0f;
                }
                if (this.z < Zpric) {
                    this.z += pspeed * 4.0f;
                }
            }
            if (!this.aim) {
                if (this.x <= 0.0f) {
                    this.x += pspeed * 2.0f;
                }
                if (this.y > 0.0f) {
                    this.y -= pspeed;
                }
                if (this.z > 0.0f) {
                    this.z -= pspeed * 2.0f;
                }
            }
            if (this.mc.field_71439_g.func_70051_ag()) {
                if (this.rotx <= 20.0f) {
                    this.rotx += 3.0f;
                }
                if (this.roty >= -20.0f) {
                    this.roty -= 1.0f;
                }
            } else {
                if (this.rotx >= 0.0f) {
                    this.rotx -= 3.0f;
                }
                if (this.roty < 0.0f) {
                    this.roty += 0.5f;
                }
            }
            if (is.field_77990_d.func_74767_n("isReload")) {
                if (this.ym > -200) {
                    this.ym -= 5;
                }
                if (this.rotgun2 > -34.0f) {
                    this.rotgun2 -= 1.0f;
                } else if (this.rotgun2 < 0.0f) {
                    this.rotgun2 += 1.0f;
                }
            } else if (this.rotgun2 < 0.0f) {
                this.rotgun2 += 1.0f;
                this.ym = 0;
            }
            if (EventsClient.guiUpgrade) {
                this.aim = false;
                if (this.rotx_m <= 20.0f) {
                    this.rotx_m += 2.0f;
                }
                if (this.roty_m <= 10.0f) {
                    this.roty_m += 2.0f;
                }
                if ((float)this.ym < 0.4f) {
                    this.ym = (int)((float)this.ym + 0.05f);
                }
            } else {
                if (this.rotx_m > 0.0f) {
                    this.rotx_m -= 1.0f;
                }
                if (this.roty_m > 0.0f) {
                    this.roty_m -= 1.0f;
                }
                if ((float)this.ym > 0.0f) {
                    this.ym = (int)((float)this.ym - 0.05f);
                }
            }
        }
    }
}
