/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bdv
 *  mods.provoda.GLLoader.GLModel
 *  mods.provoda.guns.models.clock.Clock
 *  mods.provoda.guns.models.clock.HourArrowModel
 *  mods.provoda.guns.models.clock.MatrixDebugModel
 *  mods.provoda.guns.models.clock.MinArrowModel
 *  mods.provoda.guns.models.clock.O2ArrowModel
 *  mods.provoda.guns.models.clock.SekArrowModel
 *  mods.provoda.guns.renderers.Render_Gatling$NamelessClass319164892
 *  mods.provoda.models.Shoot
 *  mods.provoda.until.Hand
 *  mp
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 *  wm
 */
package mods.provoda.guns.renderers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import mods.provoda.GLLoader.GLModel;
import mods.provoda.ModBase;
import mods.provoda.gui.modifi.ModifiInventory;
import mods.provoda.guns.Gatling;
import mods.provoda.guns.models.clock.Clock;
import mods.provoda.guns.models.clock.HourArrowModel;
import mods.provoda.guns.models.clock.MatrixDebugModel;
import mods.provoda.guns.models.clock.MinArrowModel;
import mods.provoda.guns.models.clock.O2ArrowModel;
import mods.provoda.guns.models.clock.SekArrowModel;
import mods.provoda.guns.renderers.Render_Gatling;
import mods.provoda.models.Shoot;
import mods.provoda.until.Hand;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Render_Gatling
implements IItemRenderer {
    Hand hand = new Hand();
    GLModel bullet = new GLModel("/mods/provoda/guns/models/gatling/bullet.obj");
    GLModel gilza = new GLModel("/mods/provoda/guns/models/gatling/gilza.obj");
    GLModel gatling = new GLModel("/mods/provoda/guns/models/gatling/gatling.obj");
    GLModel mag = new GLModel("/mods/provoda/guns/models/gatling/mag.obj");
    GLModel stvol = new GLModel("/mods/provoda/guns/models/gatling/stvol.obj");
    GLModel stvol5 = new GLModel("/mods/provoda/guns/models/gatling/stvol5.obj");
    GLModel zvezda = new GLModel("/mods/provoda/guns/models/gatling/zvezda.obj");
    GLModel base3 = new GLModel("/mods/provoda/guns/models/gatling/base3.obj");
    GLModel base5 = new GLModel("/mods/provoda/guns/models/gatling/base5.obj");
    GLModel air = new GLModel("/mods/provoda/guns/models/gatling/air.obj");
    GLModel giro = new GLModel("/mods/provoda/guns/models/gatling/giro.obj");
    GLModel giro_wheel = new GLModel("/mods/provoda/guns/models/gatling/giro_wheel.obj");
    GLModel pruj = new GLModel("/mods/provoda/guns/models/gatling/pruj.obj");
    GLModel prujmeh = new GLModel("/mods/provoda/guns/models/gatling/prujmeh.obj");
    GLModel butt = new GLModel("/mods/provoda/guns/models/gatling/butt.obj");
    Clock clock = new Clock();
    Shoot shoot = new Shoot();
    Random rand = new Random();
    O2ArrowModel O2ArrouModel = new O2ArrowModel();
    MinArrowModel MinArrouModel = new MinArrowModel();
    HourArrowModel HourArrowModel = new HourArrowModel();
    SekArrowModel SekArrowModel = new SekArrowModel();
    MatrixDebugModel debugModel = new MatrixDebugModel();
    private float ot;
    private boolean bul1;
    private boolean bul2;
    private boolean bul3;
    private boolean bul4;
    private float bulCur1X;
    private float bulCur1Y;
    private float bulCur2X;
    private float bulCur2Y;
    private float bulCur3X;
    private float bulCur3Y;
    private float bulCur4X;
    private float bulCur4Y;
    private float bul1Rot;
    private float bul2Rot;
    private float bul3Rot;
    private float bul4Rot;
    private float curAir;
    private float airCurFloat;
    private float airCurFloatPruj;
    private boolean airComplete;
    private float stvolrot;
    private float zvezdarot;
    private float patr;
    private boolean SkopePush;
    private boolean Complete1;
    private boolean Complete2;
    private boolean Complete3;
    private boolean Complete4;
    private boolean Complete5;
    private boolean Complete6;
    private boolean Complete7;
    private boolean Complete8;
    private boolean Complete9;
    private boolean Complete10;
    private boolean Complete11;
    private boolean Complete12;
    private boolean Complete13;
    private boolean Complete14;
    private boolean isReloading;
    private boolean isUnloading;
    private boolean rend1;
    public float otdachaCurTrans;
    public float shoot1;
    private float shootrot;
    private float hideshot;
    private float o2rotateangle;
    private float minrotateangle;
    private float hourrotateangle;
    private float sekrotateangle;
    private float scopeCurRot1;
    private float scopeCurRot2;
    private float scopeCurTransX;
    private float scopeCurTransY;
    private float scopeCurTransZ;
    private float modifiCurTransX;
    private float modifiCurTransY;
    private float modifiCurTransZ;
    private float modifiCurRot1;
    private float modifiCurRot2;
    private float modifiMaxTransX;
    private float modifiMaxTransY;
    private float modifiMaxTransZ;
    private float modifiMaxRot1 = 30.0f;
    private float modifiMaxRot2 = 30.0f;
    private float scopeMaxRot1 = 1.8f;
    private float scopeMaxRot2 = 2.0f;
    private float scopeMaxTransX = 0.182f;
    private float scopeMaxTransY = 0.06f;
    private float scopeMaxTransZ = 0.12f;
    private float sprintCurTransX;
    private float sprintCurTransY;
    private float sprintCurTransZ;
    private float sprintCurRot1;
    private float sprintCurRot2;
    private float sprintMaxTransX = 0.09f;
    private float sprintMaxTransY = 0.02f;
    private float sprintMaxTransZ = 0.07f;
    private float sprintMaxRot1 = 80.0f;
    private float sprintMaxRot2 = 10.0f;
    private float currentxrot;
    private float currentyrot;
    private float currentzrot;
    private float currentxtrans;
    private float currentytrans;
    private float currentztrans;
    private float maxzrot = 30.0f;
    private float maxyrot = 30.0f;
    private float maxxrot = 82.0f;
    private float maxxtrans = 0.09f;
    private float maxytrans = 0.1f;
    private float maxztrans = 0.39f;
    private float reloadCurTransX;
    private float reloadCurTransZ;
    private float reloadMaxTransX = 0.05f;
    private float reloadMaxTransZ = 0.2f;
    private float handCurTransX;
    private float handCurTransZ;
    private float handCurTransY;
    private float handMaxTransX = 0.03f;
    private float handMaxTransY = 0.0f;
    private float handMaxTransZ = 0.0f;
    private float handCurRot1;
    private float handCurRot2;
    private float handCurRot3;
    private float handMaxRot1 = 0.0f;
    private float handMaxRot2 = 0.0f;
    private float handMaxRot3;
    private float hand1CurTransX;
    private float hand1CurTransY;
    private float hand1CurTransZ;
    private float hand1MaxTransX = 0.02f;
    private float hand1MaxTransY = 0.03f;
    private float hand1MaxTransZ = 0.0f;
    private float hand1CurRot1;
    private float hand1CurRot2;
    private float hand1CurRot3;
    private float hand1MaxRot1 = 50.0f;
    private float hand1MaxRot2;
    private float hand1MaxRot3;
    private float hand2CurTransX;
    private float hand2CurTransY;
    private float hand2CurTransZ;
    private float hand2MaxTransX = 0.035f;
    private float hand2MaxTransY = 0.02f;
    private float hand2MaxTransZ = 0.0f;
    private float hand2CurRot1;
    private float hand2CurRot2;
    private float hand2CurRot3;
    private float hand2MaxRot1 = 60.0f;
    private float hand2MaxRot2;
    private float hand2MaxRot3;
    private float hand3CurTransX;
    private float hand3CurTransY;
    private float hand3CurTransZ;
    private float hand3MaxTransX = 0.035f;
    private float hand3MaxTransY = 0.025f;
    private float hand3MaxTransZ = 0.0f;
    private float hand3CurRot1;
    private float hand3CurRot2;
    private float hand3CurRot3;
    private float hand3MaxRot1 = 15.0f;
    private float hand3MaxRot2;
    private float hand3MaxRot3;
    private float hand4CurTransX;
    private float hand4CurTransY;
    private float hand4CurTransZ;
    private float hand4MaxTransX = 0.012f;
    private float hand4MaxTransY = 0.03f;
    private float hand4MaxTransZ = 0.0f;
    private float hand4CurRot1;
    private float hand4CurRot2;
    private float hand4CurRot3;
    private float hand4MaxRot1 = 55.0f;
    private float hand4MaxRot2;
    private float hand4MaxRot3;
    private float hand5CurTransX;
    private float hand5CurTransY;
    private float hand5CurTransZ;
    private float hand5MaxTransX = 0.003f;
    private float hand5MaxTransY = 0.041f;
    private float hand5MaxTransZ = 0.0f;
    private float hand5CurRot1;
    private float hand5CurRot2;
    private float hand5CurRot3;
    private float hand5MaxRot1;
    private float hand5MaxRot2;
    private float hand5MaxRot3;
    private float hand6CurTransX;
    private float hand6CurTransY;
    private float hand6CurTransZ;
    private float hand6MaxTransX = 0.0f;
    private float hand6MaxTransY = 0.15f;
    private float hand6MaxTransZ = 0.0f;
    private float hand6CurRot1;
    private float hand6CurRot2;
    private float hand6CurRot3;
    private float hand6MaxRot1 = 20.0f;
    private float hand6MaxRot2;
    private float hand6MaxRot3;
    private float hand7CurTransX;
    private float hand7CurTransY;
    private float hand7CurTransZ;
    private float hand7MaxTransX = 0.05f;
    private float hand7MaxTransY = 0.1f;
    private float hand7MaxTransZ = 0.3f;
    private float hand7CurRot1;
    private float hand7CurRot2;
    private float hand7CurRot3;
    private float hand7MaxRot1;
    private float hand7MaxRot2;
    private float hand7MaxRot3;
    private float hand8CurTransX;
    private float hand8CurTransY;
    private float hand8CurTransZ;
    private float hand8MaxTransX = 0.3f;
    private float hand8MaxTransY = 0.0f;
    private float hand8MaxTransZ = 0.3f;
    private float hand8CurRot1;
    private float hand8CurRot2;
    private float hand8CurRot3;
    private float hand8MaxRot1;
    private float hand8MaxRot2;
    private float hand8MaxRot3;
    private float hand9CurTransX;
    private float hand9CurTransY;
    private float hand9CurTransZ;
    private float hand9MaxTransX = 0.3f;
    private float hand9MaxTransY = 0.0f;
    private float hand9MaxTransZ = 0.3f;
    private float hand9CurRot1;
    private float hand9CurRot2;
    private float hand9CurRot3;
    private float hand9MaxRot1;
    private float hand9MaxRot2;
    private float hand9MaxRot3;
    private float hand10CurTransX;
    private float hand10CurTransY;
    private float hand10CurTransZ;
    private float hand10MaxTransX = 0.15f;
    private float hand10MaxTransY = 0.15f;
    private float hand10MaxTransZ = 0.3f;
    private float hand10CurRot1;
    private float hand10CurRot2;
    private float hand10CurRot3;
    private float hand10MaxRot1;
    private float hand10MaxRot2;
    private float hand10MaxRot3;
    private float hand11CurTransX;
    private float hand11CurTransY;
    private float hand11CurTransZ;
    private float hand11MaxTransX = 0.2f;
    private float hand11MaxTransY = 0.3f;
    private float hand11MaxTransZ = 0.05f;
    private float hand11CurRot1;
    private float hand11CurRot2;
    private float hand11CurRot3;
    private float hand11MaxRot1;
    private float hand11MaxRot2;
    private float hand11MaxRot3;
    private float hand12CurTransX;
    private float hand12CurTransY;
    private float hand12CurTransZ;
    private float hand12MaxTransX = 0.1f;
    private float hand12MaxTransY = 0.0f;
    private float hand12MaxTransZ = 0.1f;
    private float hand12CurRot1;
    private float hand12CurRot2;
    private float hand12CurRot3;
    private float hand12MaxRot1;
    private float hand12MaxRot2;
    private float hand12MaxRot3;
    private boolean rend;
    private boolean airanim;
    private boolean airanim2;
    private boolean hand7;
    private boolean hand7_1 = true;
    private float timershoot;
    private float animspeed = 1.0f;
    private float zoom;
    private int gilzaTimer;
    private boolean ruk;

    public boolean handleRenderType(wm item, IItemRenderer.ItemRenderType type) {
        switch (NamelessClass319164892.$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRenderType[type.ordinal()]) {
            case 1: {
                if (ModBase.clientType == 0) {
                    return true;
                }
                return ModBase.clientType == 1;
            }
            case 2: {
                if (ModBase.clientType == 0) {
                    return true;
                }
                return ModBase.clientType == 1;
            }
            case 3: {
                return true;
            }
        }
        return false;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, wm item, IItemRenderer.ItemRendererHelper helper) {
        return false;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, wm item, Object ... data) {
        Minecraft mc = Minecraft.x();
        ModifiInventory revinv = new ModifiInventory(item);
        int i1 = mc.p.a(mc.g.cv, (String)null);
        if (mc.p.a(mc.g.cv, (String)null) == -1) {
            i1 = mc.p.f("/mob/char.png");
        }
        int z = item.q().e("Bullet_Left");
        boolean z1 = item.q().n("HasAmmo");
        int shootTimer = Gatling.otdachaTimer;
        int air1 = item.q().e("Air");
        this.animspeed = 0.6f;
        boolean shooting = item.q().n("Shoot");
        switch (NamelessClass319164892.$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRenderType[type.ordinal()]) {
            case 1: {
                GL11.glPushMatrix();
                GL11.glShadeModel((int)7425);
                GL11.glScaled((double)2.5, (double)2.5, (double)2.0);
                GL11.glTranslatef((float)0.35f, (float)-0.3f, (float)0.17f);
                GL11.glRotatef((float)15.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)16.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)3.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.gatling.render();
                GL11.glPushMatrix();
                GL11.glEnable((int)2884);
                GL11.glScaled((double)0.3f, (double)0.3f, (double)0.3f);
                GL11.glTranslatef((float)0.0f, (float)0.2f, (float)2.4f);
                GL11.glRotatef((float)this.shootrot, (float)0.0f, (float)0.0f, (float)1.0f);
                if (this.shoot1 > 0.0f) {
                    this.shoot1 -= 1.0f;
                }
                if ((revinv.a(2) != null && revinv.a(2).b() != ModBase.Glushak || revinv.a(2) == null) && z >= 1 && shooting) {
                    int i = this.rand.nextInt(3);
                    if (i == 0) {
                        mc.p.b("/mods/provoda/textures/shoot1.png");
                        this.shoot.render();
                    } else if (i == 1) {
                        mc.p.b("/mods/provoda/textures/shoot2.png");
                        this.shoot.render();
                    } else if (i == 2) {
                        mc.p.b("/mods/provoda/textures/shoot3.png");
                        this.shoot.render();
                    } else {
                        mc.p.b("/mods/provoda/textures/shoot4.png");
                        this.shoot.render();
                    }
                    this.hideshot -= 1.0f / (this.animspeed * 2.0f);
                } else {
                    this.hideshot = 10.0f;
                }
                GL11.glPopMatrix();
                if (item.q().n("HasAmmo")) {
                    this.mag.render();
                }
                this.zvezda.render();
                GL11.glPushMatrix();
                if (item.q().n("Rot") && z > 0) {
                    this.stvolrot += 25.0f / (this.animspeed * 3.0f);
                }
                GL11.glRotatef((float)this.stvolrot, (float)0.0f, (float)0.0f, (float)1.0f);
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                    this.stvol5.render();
                } else {
                    this.stvol.render();
                }
                GL11.glPopMatrix();
                if (revinv.a(2) != null && revinv.a(2).b() == ModBase.GatlingMotor) {
                    GL11.glPushMatrix();
                    this.base5.render();
                    GL11.glPopMatrix();
                } else {
                    GL11.glPushMatrix();
                    this.base3.render();
                    GL11.glPopMatrix();
                    if (revinv.a(2) != null && revinv.a(2).b() == ModBase.GatlingGiro) {
                        GL11.glPushMatrix();
                        this.giro.render();
                        GL11.glPopMatrix();
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.002f, (float)-0.002f, (float)-0.32f);
                        this.giro_wheel.render();
                        GL11.glPopMatrix();
                    }
                }
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.GatlingButt) {
                    GL11.glPushMatrix();
                    this.butt.render();
                    GL11.glPopMatrix();
                }
                if (revinv.a(2) == null || revinv.a(2).b() != ModBase.GatlingMotor) {
                    GL11.glPushMatrix();
                    GL11.glRotatef((float)this.airCurFloat, (float)0.0f, (float)0.0f, (float)1.0f);
                    this.prujmeh.render();
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)-0.0f, (float)-0.093f, (float)-0.441f);
                    GL11.glRotatef((float)(-this.airCurFloatPruj * 200.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                    this.pruj.render();
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
                break;
            }
            case 2: {
                GL11.glPushMatrix();
                GL11.glScaled((double)3.5, (double)3.5, (double)3.5);
                GL11.glTranslatef((float)0.0f, (float)-0.03f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.gatling.render();
                if (item.q().n("HasAmmo")) {
                    this.mag.render();
                }
                this.stvol.render();
                this.zvezda.render();
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                    this.stvol5.render();
                } else {
                    this.stvol.render();
                }
                if (revinv.a(2) != null && revinv.a(2).b() == ModBase.GatlingMotor) {
                    GL11.glPushMatrix();
                    this.base5.render();
                    GL11.glPopMatrix();
                } else {
                    GL11.glPushMatrix();
                    this.base3.render();
                    GL11.glPopMatrix();
                    if (revinv.a(2) != null && revinv.a(2).b() == ModBase.GatlingGiro) {
                        GL11.glPushMatrix();
                        this.giro.render();
                        GL11.glPopMatrix();
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.002f, (float)-0.002f, (float)-0.32f);
                        this.giro_wheel.render();
                        GL11.glPopMatrix();
                    }
                }
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.GatlingButt) {
                    GL11.glPushMatrix();
                    this.butt.render();
                    GL11.glPopMatrix();
                }
                if (revinv.a(2) == null || revinv.a(2).b() != ModBase.GatlingMotor) {
                    GL11.glPushMatrix();
                    GL11.glRotatef((float)this.airCurFloat, (float)0.0f, (float)0.0f, (float)1.0f);
                    this.prujmeh.render();
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)-0.0f, (float)-0.093f, (float)-0.441f);
                    GL11.glRotatef((float)(-this.airCurFloatPruj * 200.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                    this.pruj.render();
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
                break;
            }
            case 3: {
                this.partCompliteCheck();
                this.isReloading = item.q().e("LoadingTimer") > 0;
                this.isUnloading = item.q().e("UnloadingTimer") > 0;
                if (item.q().n("Charge")) {
                    this.charge(true);
                    this.airCurFloat += 4.0f / (this.animspeed * 2.0f);
                } else {
                    this.charge(false);
                }
                if (Minecraft.x().g.ah()) {
                    this.sprinting(true);
                } else {
                    this.sprinting(false);
                }
                if (item.q().n("Modifi")) {
                    this.modifi(true);
                } else {
                    this.modifi(false);
                }
                if (this.isUnloading) {
                    this.hand7 = true;
                    this.hand6(true);
                    if (this.Complete7) {
                        this.hand7(true, z1);
                        if (this.Complete8) {
                            this.hand8(true, z1);
                        }
                    }
                } else if (this.isReloading) {
                    this.hand7 = true;
                    this.ruk = false;
                    if (this.hand7_1) {
                        this.hand6(true);
                        this.hand7(true, z1);
                        this.hand8(true, z1);
                    } else if (!this.hand7_1) {
                        this.hand8(false, z1);
                        if (this.hand8CurTransX == 0.0f && this.hand8CurTransY == 0.0f && this.hand8CurTransZ == 0.0f && this.hand8CurRot1 == 0.0f) {
                            this.hand7(false, z1);
                        }
                    }
                    if (this.Complete7 && this.Complete8 && this.Complete9) {
                        this.hand7_1 = false;
                        this.rend = true;
                    }
                } else {
                    this.hand7_1 = true;
                    this.rend = false;
                    if (item.q().n("Air_Reload")) {
                        this.hand6(true);
                    } else {
                        this.hand6(false);
                        if (this.hand7 && this.hand6CurTransY == 0.0f && this.hand6CurRot1 == 0.0f) {
                            this.hand7 = false;
                        }
                    }
                    this.hand7(false, z1);
                    this.hand8(false, z1);
                    this.hand9(false);
                    this.hand10(false);
                    this.hand11(false);
                    this.hand12(false);
                }
                GL11.glPushMatrix();
                GL11.glScaled((double)10.0, (double)10.0, (double)10.0);
                GL11.glShadeModel((int)7425);
                GL11.glTranslatef((float)0.82f, (float)0.2f, (float)0.2f);
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                    if (revinv.a(1) != null && revinv.a(1).b() == ModBase.GatlingButt) {
                        if (this.otdachaCurTrans <= 0.0f && (shootTimer == 1 && air1 > 266 || shootTimer == 2 && air1 > 133 && air1 < 267 || shootTimer == 3 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.08f;
                        } else if (this.otdachaCurTrans > 0.0f && (shootTimer == 1 && air1 > 266 || shootTimer == 2 && air1 > 133 && air1 < 267 || shootTimer == 3 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.08f;
                        }
                    } else if (this.otdachaCurTrans <= 0.0f && (shootTimer == 1 && air1 > 266 || shootTimer == 2 && air1 > 133 && air1 < 267 || shootTimer == 3 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                        this.otdachaCurTrans = 0.08f;
                    } else if (this.otdachaCurTrans > 0.0f && (shootTimer == 1 && air1 > 266 || shootTimer == 2 && air1 > 133 && air1 < 267 || shootTimer == 3 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                        this.otdachaCurTrans = 0.08f;
                    }
                } else if (revinv.a(1) != null && revinv.a(1).b() == ModBase.GatlingButt) {
                    if (this.otdachaCurTrans <= 0.0f && (shootTimer == 2 && air1 > 266 || shootTimer == 3 && air1 > 133 && air1 < 267 || shootTimer == 4 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                        this.otdachaCurTrans = 0.08f;
                    } else if (this.otdachaCurTrans > 0.0f && (shootTimer == 2 && air1 > 266 || shootTimer == 3 && air1 > 133 && air1 < 267 || shootTimer == 4 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                        this.otdachaCurTrans = 0.08f;
                    }
                } else if (this.otdachaCurTrans <= 0.0f && (shootTimer == 2 && air1 > 266 || shootTimer == 3 && air1 > 133 && air1 < 267 || shootTimer == 4 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                    this.otdachaCurTrans = 0.08f;
                } else if (this.otdachaCurTrans > 0.0f && (shootTimer == 2 && air1 > 266 || shootTimer == 3 && air1 > 133 && air1 < 267 || shootTimer == 4 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                    this.otdachaCurTrans = 0.08f;
                }
                if (this.otdachaCurTrans > 0.0f) {
                    this.otdachaCurTrans -= 0.005f / (this.animspeed * 2.0f);
                }
                GL11.glTranslatef((float)(this.sprintCurTransX * 0.0f), (float)(-this.sprintCurTransY * 5.0f), (float)(-this.sprintCurTransZ * 2.0f));
                GL11.glRotatef((float)(this.sprintCurRot1 / 5.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)(-this.sprintCurRot2 * 1.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)(this.modifiCurRot1 / 800.0f), (float)(this.modifiCurRot1 / 300.0f), (float)(-this.modifiCurRot1 / 400.0f));
                GL11.glRotatef((float)(this.modifiCurRot1 * 0.2f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)(this.modifiCurRot2 / 2.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)(this.modifiCurRot2 / 3.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)17.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)93.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)10.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)-39.0f, (float)1.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)(-this.otdachaCurTrans));
                GL11.glRotatef((float)(-this.hand6CurRot1), (float)1.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)0.0f, (float)this.hand6CurTransY, (float)0.0f);
                GL11.glPushMatrix();
                this.gatling.render();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                if ((Mouse.isButtonDown((int)0) && z > 0 && item.q().e("Air") > 0 && !item.q().n("Air_Reload") || Mouse.isButtonDown((int)1) && item.q().e("Air") > 0) && !this.isReloading && !this.isUnloading && mc.s == null) {
                    this.stvolrot += 25.0f / (this.animspeed * 3.0f);
                } else if (this.stvolrot != 10.0f) {
                    this.stvolrot = this.stvolrot > 150.0f && this.stvolrot < 210.0f ? (this.stvolrot += 25.0f / (this.animspeed * 3.0f)) : (this.stvolrot < 240.0f ? (this.stvolrot += 15.0f / (this.animspeed * 3.0f)) : (this.stvolrot < 290.0f ? (this.stvolrot += 10.0f / (this.animspeed * 3.0f)) : (this.stvolrot += 5.0f / (this.animspeed * 3.0f))));
                }
                if (this.stvolrot > 360.0f) {
                    this.stvolrot = 10.0f;
                }
                GL11.glRotatef((float)this.stvolrot, (float)0.0f, (float)0.0f, (float)1.0f);
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                    this.stvol5.render();
                } else {
                    this.stvol.render();
                }
                GL11.glPopMatrix();
                if (revinv.a(2) != null && revinv.a(2).b() == ModBase.GatlingMotor) {
                    GL11.glPushMatrix();
                    this.base5.render();
                    GL11.glPopMatrix();
                } else {
                    GL11.glPushMatrix();
                    this.base3.render();
                    GL11.glPopMatrix();
                    if (revinv.a(2) != null && revinv.a(2).b() == ModBase.GatlingGiro) {
                        GL11.glPushMatrix();
                        this.giro.render();
                        GL11.glPopMatrix();
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.002f, (float)-0.002f, (float)-0.32f);
                        GL11.glRotatef((float)this.stvolrot, (float)0.0f, (float)1.0f, (float)0.0f);
                        this.giro_wheel.render();
                        GL11.glPopMatrix();
                    }
                }
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.GatlingButt) {
                    GL11.glPushMatrix();
                    this.butt.render();
                    GL11.glPopMatrix();
                }
                if (revinv.a(2) == null || revinv.a(2).b() != ModBase.GatlingMotor) {
                    GL11.glPushMatrix();
                    GL11.glScalef((float)0.9f, (float)1.0f, (float)1.0f);
                    GL11.glTranslatef((float)-0.003f, (float)0.03f, (float)-0.0f);
                    GL11.glRotatef((float)-340.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)((float)item.q().e("Air") / 1.65f), (float)0.0f, (float)0.0f, (float)1.0f);
                    this.air.render();
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glRotatef((float)this.airCurFloat, (float)0.0f, (float)0.0f, (float)1.0f);
                    this.prujmeh.render();
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)-0.0f, (float)-0.093f, (float)-0.441f);
                    GL11.glRotatef((float)(-this.airCurFloatPruj * 200.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                    this.pruj.render();
                    GL11.glPopMatrix();
                }
                GL11.glPushMatrix();
                GL11.glScaled((double)0.35f, (double)0.35f, (double)0.35f);
                GL11.glRotatef((float)84.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glTranslatef((float)-0.14f, (float)-2.0f, (float)-0.3f);
                if (!(this.isReloading || this.isUnloading || this.hand7)) {
                    GL11.glTranslatef((float)(-this.hand6CurTransY * 2.0f), (float)(this.hand6CurTransY * 2.5f), (float)(-this.hand6CurTransY * 2.0f));
                }
                GL11.glTranslatef((float)0.0f, (float)(-this.airCurFloatPruj * 2.0f), (float)this.airCurFloatPruj);
                GL11.glBindTexture((int)3553, (int)i1);
                this.hand.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                    if ((shootTimer > 0 && air1 > 266 || shootTimer > 1 && air1 > 133 && air1 < 267 || shootTimer > 2 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && mc.s == null) {
                        this.hand(true);
                        this.hand1(true);
                        this.hand2(true);
                        this.hand3(true);
                        this.hand4(true);
                        this.hand5(true);
                        this.hand(true);
                        this.hand1(true);
                        this.hand2(true);
                        this.hand3(true);
                        this.hand4(true);
                        this.hand5(true);
                        this.hand(true);
                        this.hand1(true);
                        this.hand2(true);
                        this.hand3(true);
                        this.hand4(true);
                        this.hand5(true);
                        this.zvezdarot += 33.6f / (this.animspeed * 3.0f);
                    }
                } else if ((shootTimer > 1 && air1 > 266 || shootTimer > 2 && air1 > 133 && air1 < 267 || shootTimer > 3 && air1 > 0 && air1 < 134) && !this.isReloading && !this.isUnloading && mc.s == null) {
                    this.hand(true);
                    this.hand1(true);
                    this.hand2(true);
                    this.hand3(true);
                    this.hand4(true);
                    this.hand5(true);
                    this.hand(true);
                    this.hand1(true);
                    this.hand2(true);
                    this.hand3(true);
                    this.hand4(true);
                    this.hand5(true);
                    this.hand(true);
                    this.hand1(true);
                    this.hand2(true);
                    this.hand3(true);
                    this.hand4(true);
                    this.hand5(true);
                    this.zvezdarot += 33.6f / (this.animspeed * 3.0f);
                }
                if (this.zvezdarot >= 360.0f) {
                    this.zvezdarot = 0.0f;
                }
                GL11.glTranslatef((float)0.142f, (float)0.008f, (float)0.0f);
                GL11.glRotatef((float)this.zvezdarot, (float)0.0f, (float)0.0f, (float)1.0f);
                this.zvezda.render();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScaled((double)1.0, (double)1.0, (double)1.0);
                GL11.glTranslatef((float)0.1f, (float)0.1f, (float)-0.01f);
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                    if ((shootTimer == 1 && air1 > 266 || shootTimer == 2 && air1 > 133 && air1 < 267 || shootTimer == 3 && air1 > 0 && air1 < 134) && this.gilzaTimer <= 0) {
                        this.bul1 = true;
                        if (this.bulCur1X != 0.0f && this.bulCur1Y != 0.0f) {
                            this.bul2 = true;
                            this.gilzaTimer = 10;
                        }
                        if (this.bulCur2X != 0.0f && this.bulCur2Y != 0.0f) {
                            this.bul3 = true;
                            this.gilzaTimer = 10;
                        }
                        if (this.bulCur3X != 0.0f && this.bulCur3Y != 0.0f) {
                            this.bul4 = true;
                            this.gilzaTimer = 10;
                        }
                    }
                } else if ((shootTimer == 2 && air1 > 266 || shootTimer == 3 && air1 > 133 && air1 < 267 || shootTimer == 4 && air1 > 0 && air1 < 134) && this.gilzaTimer <= 0) {
                    this.bul1 = true;
                    if (this.bulCur1X != 0.0f && this.bulCur1Y != 0.0f) {
                        this.bul2 = true;
                        this.gilzaTimer = 10;
                    }
                    if (this.bulCur2X != 0.0f && this.bulCur2Y != 0.0f) {
                        this.bul3 = true;
                        this.gilzaTimer = 10;
                    }
                    if (this.bulCur3X != 0.0f && this.bulCur3Y != 0.0f) {
                        this.bul4 = true;
                        this.gilzaTimer = 10;
                    }
                }
                if (this.gilzaTimer > 0) {
                    --this.gilzaTimer;
                }
                if ((double)this.bulCur1X >= 0.3 && this.bulCur1Y >= 0.03f) {
                    this.bul1 = false;
                }
                if ((double)this.bulCur2X >= 0.4 && this.bulCur2Y >= 0.025f) {
                    this.bul2 = false;
                }
                if ((double)this.bulCur3X >= 0.35 && this.bulCur3Y >= 0.02f) {
                    this.bul3 = false;
                }
                if ((double)this.bulCur4X >= 0.45 && this.bulCur4Y >= 0.035f) {
                    this.bul4 = false;
                }
                if (this.bul1) {
                    if ((double)this.bulCur1X != 0.3) {
                        this.bulCur1X += 0.055f / (this.animspeed * 2.0f);
                    }
                    if ((double)this.bulCur1Y != 0.03) {
                        this.bulCur1Y += 0.055f / (this.animspeed * 2.0f);
                    }
                } else {
                    this.bulCur1Y = 0.0f;
                    this.bulCur1X = 0.0f;
                }
                if (this.bul2) {
                    if ((double)this.bulCur2X != 0.4) {
                        this.bulCur2X += 0.055f / (this.animspeed * 2.0f);
                    }
                    if ((double)this.bulCur2Y != 0.025) {
                        this.bulCur2Y += 0.055f / (this.animspeed * 2.0f);
                    }
                } else {
                    this.bulCur2Y = 0.0f;
                    this.bulCur2X = 0.0f;
                }
                if (this.bul3) {
                    if ((double)this.bulCur3X != 0.35) {
                        this.bulCur3X += 0.055f / (this.animspeed * 2.0f);
                    }
                    if ((double)this.bulCur3Y != 0.02) {
                        this.bulCur3Y += 0.055f / (this.animspeed * 2.0f);
                    }
                } else {
                    this.bulCur3Y = 0.0f;
                    this.bulCur3X = 0.0f;
                }
                if (this.bul4) {
                    if ((double)this.bulCur4X != 0.45) {
                        this.bulCur4X += 0.055f / (this.animspeed * 2.0f);
                    }
                    if ((double)this.bulCur4Y != 0.035) {
                        this.bulCur4Y += 0.055f / (this.animspeed * 2.0f);
                    }
                } else {
                    this.bulCur4Y = 0.0f;
                    this.bulCur4X = 0.0f;
                }
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(-this.bulCur1X * 4.0f), (float)(-this.bulCur1Y * 1.5f), (float)0.0f);
                if (this.bul1 && z > 0) {
                    this.gilza.render();
                }
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(-this.bulCur2X * 4.0f), (float)(-this.bulCur2Y * 1.5f), (float)0.0f);
                if (this.bul2 && z > 0) {
                    this.gilza.render();
                }
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(-this.bulCur3X * 4.0f), (float)(-this.bulCur3Y * 1.2f), (float)0.0f);
                if (this.bul3 && z > 0) {
                    this.gilza.render();
                }
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(-this.bulCur4X * 4.0f), (float)(-this.bulCur4Y * 1.5f), (float)0.0f);
                if (this.bul4 && z > 0) {
                    this.gilza.render();
                }
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)this.hand8CurTransX, (float)-0.0f, (float)(-this.hand8CurTransZ * 1.6f));
                GL11.glTranslatef((float)(-this.hand9CurTransX), (float)-0.0f, (float)this.hand9CurTransZ);
                GL11.glPushMatrix();
                GL11.glScaled((double)0.45f, (double)0.4f, (double)0.6f);
                GL11.glRotatef((float)85.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)20.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)0.35f, (float)-0.9f, (float)-0.15f);
                GL11.glPushMatrix();
                GL11.glEnable((int)2884);
                GL11.glScaled((double)1.0, (double)1.0, (double)1.0);
                GL11.glTranslatef((float)0.03f, (float)2.0f, (float)0.11f);
                GL11.glRotatef((float)10.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)-30.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)100.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)this.shootrot, (float)0.0f, (float)0.0f, (float)1.0f);
                if (this.shoot1 > 0.0f) {
                    this.shoot1 -= 1.0f;
                }
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.GatlingBarrel) {
                    if ((revinv.a(2) != null && revinv.a(2).b() != ModBase.Glushak || revinv.a(2) == null) && (shootTimer == 1 && air1 > 266 || shootTimer == 2 && air1 > 133 && air1 < 267 || shootTimer == 3 && air1 > 0 && air1 < 134) && z >= 1 && !this.isReloading && !this.isUnloading && !mc.g.ah() && mc.s == null) {
                        int i = this.rand.nextInt(3);
                        if (i == 0) {
                            mc.p.b("/mods/provoda/textures/shoot1.png");
                            this.shoot.render();
                        } else if (i == 1) {
                            mc.p.b("/mods/provoda/textures/shoot2.png");
                            this.shoot.render();
                        } else if (i == 2) {
                            mc.p.b("/mods/provoda/textures/shoot3.png");
                            this.shoot.render();
                        } else {
                            mc.p.b("/mods/provoda/textures/shoot4.png");
                            this.shoot.render();
                        }
                        this.hideshot -= 1.0f / (this.animspeed * 2.0f);
                    } else {
                        this.hideshot = 10.0f;
                    }
                } else if ((revinv.a(2) != null && revinv.a(2).b() != ModBase.Glushak || revinv.a(2) == null) && (shootTimer == 2 && air1 > 266 || shootTimer == 3 && air1 > 133 && air1 < 267 || shootTimer == 4 && air1 > 0 && air1 < 134) && z >= 1 && !this.isReloading && !this.isUnloading && !mc.g.ah() && mc.s == null) {
                    int i = this.rand.nextInt(3);
                    if (i == 0) {
                        mc.p.b("/mods/provoda/textures/shoot1.png");
                        this.shoot.render();
                    } else if (i == 1) {
                        mc.p.b("/mods/provoda/textures/shoot2.png");
                        this.shoot.render();
                    } else if (i == 2) {
                        mc.p.b("/mods/provoda/textures/shoot3.png");
                        this.shoot.render();
                    } else {
                        mc.p.b("/mods/provoda/textures/shoot4.png");
                        this.shoot.render();
                    }
                    this.hideshot -= 1.0f / (this.animspeed * 2.0f);
                } else {
                    this.hideshot = 10.0f;
                }
                this.shootrot += 0.1f;
                GL11.glPopMatrix();
                GL11.glTranslatef((float)(-this.hand7CurTransX), (float)this.hand7CurTransY, (float)this.hand7CurTransZ);
                GL11.glTranslatef((float)(-this.hand10CurTransX), (float)this.hand10CurTransY, (float)(-this.hand10CurTransZ));
                GL11.glTranslatef((float)(-this.hand11CurTransX), (float)this.hand11CurTransY, (float)this.hand11CurTransZ);
                GL11.glTranslatef((float)this.hand12CurTransX, (float)0.0f, (float)this.hand12CurTransZ);
                GL11.glBindTexture((int)3553, (int)i1);
                this.hand.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glScaled((double)0.067f, (double)0.07f, (double)0.05f);
                GL11.glTranslatef((float)1.6f, (float)-0.4f, (float)-0.22f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                mc.p.b("/mods/provoda/textures/clock.png");
                this.clock.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                String var17 = new SimpleDateFormat("mm").format(Calendar.getInstance().getTime());
                this.minrotateangle = 360 * Integer.parseInt(var17) / 60;
                String hour = new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());
                this.hourrotateangle = 720 * Integer.parseInt(hour) / 24;
                String sek = new SimpleDateFormat("ss").format(Calendar.getInstance().getTime());
                this.sekrotateangle = 360 * Integer.parseInt(sek) / 60;
                bdv p = mc.g;
                if (p.bK.f(3) != null) {
                    if (p.bK.f(3).q() != null) {
                        int time_in_mask = p.bK.f(3).q().e("Time_Left");
                        this.o2rotateangle = 90 * time_in_mask / 900;
                    } else {
                        this.o2rotateangle = 0.0f;
                    }
                } else {
                    this.o2rotateangle = 0.0f;
                }
                GL11.glTranslatef((float)0.75f, (float)-0.25f, (float)0.0f);
                GL11.glPushMatrix();
                GL11.glRotatef((float)this.o2rotateangle, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)-0.77f, (float)0.26f, (float)0.0f);
                this.O2ArrouModel.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glRotatef((float)this.minrotateangle, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)-0.77f, (float)0.26f, (float)0.0f);
                this.MinArrouModel.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glRotatef((float)this.hourrotateangle, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)-0.77f, (float)0.26f, (float)0.0f);
                this.HourArrowModel.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glRotatef((float)this.sekrotateangle, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)-0.77f, (float)0.26f, (float)0.0f);
                this.SekArrowModel.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                if (item.q().n("HasAmmo") && !this.isUnloading && !this.isReloading || this.isReloading && this.rend || this.isUnloading) {
                    if (item.q().n("HasAmmo")) {
                        this.mag.render();
                    }
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.105f, (float)0.01f, (float)0.0f);
                    GL11.glTranslatef((float)(-this.handCurTransX), (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    if (z > 0) {
                        if (!this.isReloading && !this.isUnloading) {
                            if (!this.isReloading && !this.isUnloading) {
                                this.bullet.render();
                            }
                        } else {
                            this.bullet.render();
                        }
                    }
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.125f, (float)0.04f, (float)0.0f);
                    GL11.glTranslatef((float)(-this.hand1CurTransX), (float)(-this.hand1CurTransY), (float)0.0f);
                    GL11.glRotatef((float)this.hand1CurRot1, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)-50.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    if (z > 1) {
                        if (!this.isReloading && !this.isUnloading) {
                            if (!this.isReloading && !this.isUnloading) {
                                this.bullet.render();
                            }
                        } else {
                            this.bullet.render();
                        }
                    }
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.16f, (float)0.04f, (float)0.0f);
                    GL11.glTranslatef((float)(-this.hand2CurTransX), (float)-0.0f, (float)0.0f);
                    GL11.glRotatef((float)this.hand2CurRot1, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)-110.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    if (z > 2) {
                        if (!this.isReloading && !this.isUnloading) {
                            if (!this.isReloading && !this.isUnloading) {
                                this.bullet.render();
                            }
                        } else {
                            this.bullet.render();
                        }
                    }
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.195f, (float)0.015f, (float)0.0f);
                    GL11.glTranslatef((float)(-this.hand3CurTransX), (float)this.hand3CurTransY, (float)0.0f);
                    GL11.glRotatef((float)this.hand3CurRot1, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)-125.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    if (z > 3) {
                        if (!this.isReloading && !this.isUnloading) {
                            if (!this.isReloading && !this.isUnloading) {
                                this.bullet.render();
                            }
                        } else {
                            this.bullet.render();
                        }
                    }
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.207f, (float)-0.015f, (float)0.0f);
                    GL11.glTranslatef((float)(-this.hand4CurTransX), (float)this.hand4CurTransY, (float)0.0f);
                    GL11.glRotatef((float)this.hand4CurRot1, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)-170.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    if (z > 4) {
                        if (!this.isReloading && !this.isUnloading) {
                            if (!this.isReloading && !this.isUnloading) {
                                this.bullet.render();
                            }
                        } else {
                            this.bullet.render();
                        }
                    }
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.21f, (float)-0.056f, (float)0.0f);
                    GL11.glTranslatef((float)(-this.hand5CurTransX), (float)this.hand5CurTransY, (float)0.0f);
                    GL11.glRotatef((float)-170.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    if (z > 5) {
                        if (!this.isReloading && !this.isUnloading) {
                            if (!this.isReloading && !this.isUnloading) {
                                this.bullet.render();
                            }
                        } else {
                            this.bullet.render();
                        }
                    }
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)this.hand5CurTransY, (float)0.0f);
                    GL11.glTranslatef((float)0.21f, (float)-0.097f, (float)0.0f);
                    GL11.glRotatef((float)-170.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    if (z > 6) {
                        if (this.Complete10 && this.isReloading) {
                            this.bullet.render();
                        } else if (this.isUnloading) {
                            this.bullet.render();
                        } else if (!this.isReloading && !this.isUnloading) {
                            this.bullet.render();
                        }
                    }
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
        }
        if (this.sprintCurTransY > this.sprintMaxTransY) {
            this.sprintCurTransY = this.sprintMaxTransY;
        }
        if (this.sprintCurTransY < 0.0f) {
            this.sprintCurTransY = 0.0f;
        }
        if (this.sprintCurTransZ > this.sprintMaxTransZ) {
            this.sprintCurTransZ = this.sprintMaxTransZ;
        }
        if (this.sprintCurTransZ < 0.0f) {
            this.sprintCurTransZ = 0.0f;
        }
        if (this.sprintCurRot1 > this.sprintMaxRot1) {
            this.sprintCurRot1 = this.sprintMaxRot1;
        }
        if (this.sprintCurRot1 < 0.0f) {
            this.sprintCurRot1 = 0.0f;
        }
        if (this.sprintCurRot2 > this.sprintMaxRot2) {
            this.sprintCurRot2 = this.sprintMaxRot2;
        }
        if (this.sprintCurRot2 < 0.0f) {
            this.sprintCurRot2 = 0.0f;
        }
        if (this.sprintCurTransX > this.sprintMaxTransX) {
            this.sprintCurTransX = this.sprintMaxTransX;
        }
        if (this.sprintCurTransX < 0.0f) {
            this.sprintCurTransX = 0.0f;
        }
        if (this.modifiCurTransY > this.modifiMaxTransY) {
            this.modifiCurTransY = this.modifiMaxTransY;
        }
        if (this.modifiCurTransY < 0.0f) {
            this.modifiCurTransY = 0.0f;
        }
        if (this.modifiCurTransX > this.modifiMaxTransX) {
            this.modifiCurTransX = this.modifiMaxTransX;
        }
        if (this.modifiCurTransX < 0.0f) {
            this.modifiCurTransX = 0.0f;
        }
        if (this.modifiCurTransZ > this.modifiMaxTransZ) {
            this.modifiCurTransZ = this.modifiMaxTransZ;
        }
        if (this.modifiCurTransZ < 0.0f) {
            this.modifiCurTransZ = 0.0f;
        }
        if (this.modifiCurRot1 > this.modifiMaxRot1) {
            this.modifiCurRot1 = this.modifiMaxRot1;
        }
        if (this.modifiCurRot1 < 0.0f) {
            this.modifiCurRot1 = 0.0f;
        }
        if (this.modifiCurRot2 > this.modifiMaxRot2) {
            this.modifiCurRot2 = this.modifiMaxRot2;
        }
        if (this.modifiCurRot2 < 0.0f) {
            this.modifiCurRot2 = 0.0f;
        }
        if (this.hand1CurTransY >= this.hand1MaxTransY && this.hand1CurTransX >= this.hand1MaxTransX && this.hand1CurRot1 >= this.hand1MaxRot1 && this.hand4CurTransY >= this.hand4MaxTransY && this.hand4CurTransX >= this.hand4MaxTransX && this.hand4CurRot1 >= this.hand4MaxRot1 && this.hand5CurTransY >= this.hand5MaxTransY && this.hand5CurTransX >= this.hand5MaxTransX && this.hand5CurRot1 >= this.hand5MaxRot1 && this.hand3CurTransY >= this.hand3MaxTransY && this.hand3CurTransX >= this.hand3MaxTransX && this.hand3CurRot1 >= this.hand3MaxRot1 && this.hand2CurTransY >= this.hand2MaxTransY && this.hand2CurTransX >= this.hand2MaxTransX && this.hand2CurRot1 >= this.hand2MaxRot1 && this.handCurTransY >= this.handMaxTransY && this.handCurTransX >= this.handMaxTransX && this.handCurRot1 >= this.handMaxRot1) {
            this.hand1CurTransY = 0.0f;
            this.hand1CurRot1 = 0.0f;
            this.hand1CurTransX = 0.0f;
            this.handCurTransY = 0.0f;
            this.handCurRot1 = 0.0f;
            this.handCurTransX = 0.0f;
            this.hand2CurTransY = 0.0f;
            this.hand2CurRot1 = 0.0f;
            this.hand2CurTransX = 0.0f;
            this.hand3CurTransY = 0.0f;
            this.hand3CurRot1 = 0.0f;
            this.hand3CurTransX = 0.0f;
            this.hand4CurTransY = 0.0f;
            this.hand4CurRot1 = 0.0f;
            this.hand4CurTransX = 0.0f;
            this.hand5CurTransY = 0.0f;
            this.hand5CurRot1 = 0.0f;
            this.hand5CurTransX = 0.0f;
        }
        if (this.handCurTransZ >= this.handMaxTransZ) {
            this.handCurTransZ = 0.0f;
        }
        if (this.handCurTransZ < 0.0f) {
            this.handCurTransZ = 0.0f;
        }
        if (this.handCurTransY >= this.handMaxTransY) {
            this.handCurTransY = 0.0f;
        }
        if (this.handCurTransY < 0.0f) {
            this.handCurTransY = 0.0f;
        }
        if (this.handCurTransX >= this.handMaxTransX) {
            this.handCurTransX = 0.0f;
        }
        if (this.handCurTransX < 0.0f) {
            this.handCurTransX = 0.0f;
        }
        if (this.handCurRot1 >= this.handMaxRot1) {
            this.handCurRot1 = 0.0f;
        }
        if (this.handCurRot1 < 0.0f) {
            this.handCurRot1 = 0.0f;
        }
        if (this.handCurRot2 >= this.handMaxRot1) {
            this.handCurRot2 = 0.0f;
        }
        if (this.handCurRot2 < 0.0f) {
            this.handCurRot2 = 0.0f;
        }
        if (this.handCurRot3 >= this.handMaxRot1) {
            this.handCurRot3 = 0.0f;
        }
        if (this.handCurRot3 < 0.0f) {
            this.handCurRot3 = 0.0f;
        }
        if (this.hand1CurTransY < 0.0f) {
            this.hand1CurTransY = 0.0f;
        }
        if (this.hand1CurTransX < 0.0f) {
            this.hand1CurTransX = 0.0f;
        }
        if (this.hand1CurTransZ >= this.hand1MaxTransZ) {
            this.hand1CurTransZ = 0.0f;
        }
        if (this.hand1CurTransZ < 0.0f) {
            this.hand1CurTransZ = 0.0f;
        }
        if (this.hand1CurRot1 < 0.0f) {
            this.hand1CurRot1 = 0.0f;
        }
        if (this.hand1CurRot2 > this.hand1MaxRot2) {
            this.hand1CurRot2 = this.hand1MaxRot2;
        }
        if (this.hand1CurRot2 < 0.0f) {
            this.hand1CurRot2 = 0.0f;
        }
        if (this.hand1CurRot3 > this.hand1MaxRot3) {
            this.hand1CurRot3 = this.hand1MaxRot3;
        }
        if (this.hand1CurRot3 < 0.0f) {
            this.hand1CurRot3 = 0.0f;
        }
        if (this.hand2CurTransY < 0.0f) {
            this.hand2CurTransY = 0.0f;
        }
        if (this.hand2CurTransX < 0.0f) {
            this.hand2CurTransX = 0.0f;
        }
        if (this.hand2CurTransZ >= this.hand2MaxTransZ) {
            this.hand2CurTransZ = 0.0f;
        }
        if (this.hand2CurTransZ < 0.0f) {
            this.hand2CurTransZ = 0.0f;
        }
        if (this.hand2CurRot1 < 0.0f) {
            this.hand2CurRot1 = 0.0f;
        }
        if (this.hand2CurRot2 > this.hand2MaxRot2) {
            this.hand2CurRot2 = this.hand2MaxRot2;
        }
        if (this.hand2CurRot2 < 0.0f) {
            this.hand2CurRot2 = 0.0f;
        }
        if (this.hand2CurRot3 > this.hand2MaxRot3) {
            this.hand2CurRot3 = this.hand2MaxRot3;
        }
        if (this.hand2CurRot3 < 0.0f) {
            this.hand2CurRot3 = 0.0f;
        }
        if (this.hand3CurTransY < 0.0f) {
            this.hand3CurTransY = 0.0f;
        }
        if (this.hand3CurTransX < 0.0f) {
            this.hand3CurTransX = 0.0f;
        }
        if (this.hand3CurTransZ >= this.hand3MaxTransZ) {
            this.hand3CurTransZ = 0.0f;
        }
        if (this.hand3CurTransZ < 0.0f) {
            this.hand3CurTransZ = 0.0f;
        }
        if (this.hand3CurRot1 < 0.0f) {
            this.hand3CurRot1 = 0.0f;
        }
        if (this.hand3CurRot2 > this.hand3MaxRot2) {
            this.hand3CurRot2 = this.hand3MaxRot2;
        }
        if (this.hand3CurRot2 < 0.0f) {
            this.hand3CurRot2 = 0.0f;
        }
        if (this.hand3CurRot3 > this.hand3MaxRot3) {
            this.hand3CurRot3 = this.hand3MaxRot3;
        }
        if (this.hand3CurRot3 < 0.0f) {
            this.hand3CurRot3 = 0.0f;
        }
        if (this.hand4CurTransY < 0.0f) {
            this.hand4CurTransY = 0.0f;
        }
        if (this.hand4CurTransX < 0.0f) {
            this.hand4CurTransX = 0.0f;
        }
        if (this.hand4CurTransZ >= this.hand4MaxTransZ) {
            this.hand4CurTransZ = 0.0f;
        }
        if (this.hand4CurTransZ < 0.0f) {
            this.hand4CurTransZ = 0.0f;
        }
        if (this.hand4CurRot1 < 0.0f) {
            this.hand4CurRot1 = 0.0f;
        }
        if (this.hand4CurRot2 > this.hand4MaxRot2) {
            this.hand4CurRot2 = this.hand4MaxRot2;
        }
        if (this.hand4CurRot2 < 0.0f) {
            this.hand4CurRot2 = 0.0f;
        }
        if (this.hand4CurRot3 > this.hand4MaxRot3) {
            this.hand4CurRot3 = this.hand4MaxRot3;
        }
        if (this.hand4CurRot3 < 0.0f) {
            this.hand4CurRot3 = 0.0f;
        }
        if (this.hand5CurTransY < 0.0f) {
            this.hand5CurTransY = 0.0f;
        }
        if (this.hand5CurTransX < 0.0f) {
            this.hand5CurTransX = 0.0f;
        }
        if (this.hand5CurTransZ >= this.hand5MaxTransZ) {
            this.hand5CurTransZ = 0.0f;
        }
        if (this.hand5CurTransZ < 0.0f) {
            this.hand5CurTransZ = 0.0f;
        }
        if (this.hand5CurRot1 < 0.0f) {
            this.hand5CurRot1 = 0.0f;
        }
        if (this.hand5CurRot2 > this.hand5MaxRot2) {
            this.hand5CurRot2 = this.hand5MaxRot2;
        }
        if (this.hand5CurRot2 < 0.0f) {
            this.hand5CurRot2 = 0.0f;
        }
        if (this.hand5CurRot3 > this.hand5MaxRot3) {
            this.hand5CurRot3 = this.hand5MaxRot3;
        }
        if (this.hand5CurRot3 < 0.0f) {
            this.hand5CurRot3 = 0.0f;
        }
        if (this.hand6CurTransY >= this.hand6MaxTransY) {
            this.hand6CurTransY = this.hand6MaxTransY;
        }
        if (this.hand6CurTransY < 0.0f) {
            this.hand6CurTransY = 0.0f;
        }
        if (this.hand6CurTransX >= this.hand6MaxTransX) {
            this.hand6CurTransX = this.hand6MaxTransX;
        }
        if (this.hand6CurTransX < 0.0f) {
            this.hand6CurTransX = 0.0f;
        }
        if (this.hand6CurTransZ >= this.hand6MaxTransZ) {
            this.hand6CurTransZ = this.hand6MaxTransZ;
        }
        if (this.hand6CurTransZ < 0.0f) {
            this.hand6CurTransZ = 0.0f;
        }
        if (this.hand6CurRot1 >= this.hand6MaxRot1) {
            this.hand6CurRot1 = this.hand6MaxRot1;
        }
        if (this.hand6CurRot1 < 0.0f) {
            this.hand6CurRot1 = 0.0f;
        }
        if (this.hand6CurRot2 > this.hand6MaxRot2) {
            this.hand6CurRot2 = this.hand6MaxRot2;
        }
        if (this.hand6CurRot2 < 0.0f) {
            this.hand6CurRot2 = 0.0f;
        }
        if (this.hand6CurRot3 > this.hand6MaxRot3) {
            this.hand6CurRot3 = this.hand6MaxRot3;
        }
        if (this.hand6CurRot3 < 0.0f) {
            this.hand6CurRot3 = 0.0f;
        }
        if (this.hand7CurTransY > this.hand7MaxTransY) {
            this.hand7CurTransY = this.hand7MaxTransY;
        }
        if (this.hand7CurTransY < 0.0f) {
            this.hand7CurTransY = 0.0f;
        }
        if (this.hand7CurTransX > this.hand7MaxTransX) {
            this.hand7CurTransX = this.hand7MaxTransX;
        }
        if (this.hand7CurTransX < 0.0f) {
            this.hand7CurTransX = 0.0f;
        }
        if (this.hand7CurTransZ > this.hand7MaxTransZ) {
            this.hand7CurTransZ = this.hand7MaxTransZ;
        }
        if (this.hand7CurTransZ < 0.0f) {
            this.hand7CurTransZ = 0.0f;
        }
        if (this.hand7CurRot1 > this.hand7MaxRot1) {
            this.hand7CurRot1 = this.hand7MaxRot1;
        }
        if (this.hand7CurRot1 < 0.0f) {
            this.hand7CurRot1 = 0.0f;
        }
        if (this.hand7CurRot2 > this.hand7MaxRot2) {
            this.hand7CurRot2 = this.hand7MaxRot2;
        }
        if (this.hand7CurRot2 < 0.0f) {
            this.hand7CurRot2 = 0.0f;
        }
        if (this.hand7CurRot3 > this.hand7MaxRot3) {
            this.hand7CurRot3 = this.hand7MaxRot3;
        }
        if (this.hand7CurRot3 < 0.0f) {
            this.hand7CurRot3 = 0.0f;
        }
        if (this.hand8CurTransY > this.hand8MaxTransY) {
            this.hand8CurTransY = this.hand8MaxTransY;
        }
        if (this.hand8CurTransY < 0.0f) {
            this.hand8CurTransY = 0.0f;
        }
        if (this.hand8CurTransX > this.hand8MaxTransX) {
            this.hand8CurTransX = this.hand8MaxTransX;
        }
        if (this.hand8CurTransX < 0.0f) {
            this.hand8CurTransX = 0.0f;
        }
        if (this.hand8CurTransZ > this.hand8MaxTransZ) {
            this.hand8CurTransZ = this.hand8MaxTransZ;
        }
        if (this.hand8CurTransZ < 0.0f) {
            this.hand8CurTransZ = 0.0f;
        }
        if (this.hand8CurRot1 > this.hand8MaxRot1) {
            this.hand8CurRot1 = this.hand8MaxRot1;
        }
        if (this.hand8CurRot1 < 0.0f) {
            this.hand8CurRot1 = 0.0f;
        }
        if (this.hand8CurRot2 > this.hand8MaxRot2) {
            this.hand8CurRot2 = this.hand8MaxRot2;
        }
        if (this.hand8CurRot2 < 0.0f) {
            this.hand8CurRot2 = 0.0f;
        }
        if (this.hand8CurRot3 > this.hand8MaxRot3) {
            this.hand8CurRot3 = this.hand8MaxRot3;
        }
        if (this.hand8CurRot3 < 0.0f) {
            this.hand8CurRot3 = 0.0f;
        }
        if (this.hand9CurTransY > this.hand9MaxTransY) {
            this.hand9CurTransY = this.hand9MaxTransY;
        }
        if (this.hand9CurTransY < 0.0f) {
            this.hand9CurTransY = 0.0f;
        }
        if (this.hand9CurTransX > this.hand9MaxTransX) {
            this.hand9CurTransX = this.hand9MaxTransX;
        }
        if (this.hand9CurTransX < 0.0f) {
            this.hand9CurTransX = 0.0f;
        }
        if (this.hand9CurTransZ > this.hand9MaxTransZ) {
            this.hand9CurTransZ = this.hand9MaxTransZ;
        }
        if (this.hand9CurTransZ < 0.0f) {
            this.hand9CurTransZ = 0.0f;
        }
        if (this.hand9CurRot1 > this.hand9MaxRot1) {
            this.hand9CurRot1 = this.hand9MaxRot1;
        }
        if (this.hand9CurRot1 < 0.0f) {
            this.hand9CurRot1 = 0.0f;
        }
        if (this.hand9CurRot2 > this.hand9MaxRot2) {
            this.hand9CurRot2 = this.hand9MaxRot2;
        }
        if (this.hand9CurRot2 < 0.0f) {
            this.hand9CurRot2 = 0.0f;
        }
        if (this.hand9CurRot3 > this.hand9MaxRot3) {
            this.hand9CurRot3 = this.hand9MaxRot3;
        }
        if (this.hand9CurRot3 < 0.0f) {
            this.hand9CurRot3 = 0.0f;
        }
        if (this.hand10CurTransY > this.hand10MaxTransY) {
            this.hand10CurTransY = this.hand10MaxTransY;
        }
        if (this.hand10CurTransY < 0.0f) {
            this.hand10CurTransY = 0.0f;
        }
        if (this.hand10CurTransX > this.hand10MaxTransX) {
            this.hand10CurTransX = this.hand10MaxTransX;
        }
        if (this.hand10CurTransX < 0.0f) {
            this.hand10CurTransX = 0.0f;
        }
        if (this.hand10CurTransZ > this.hand10MaxTransZ) {
            this.hand10CurTransZ = this.hand10MaxTransZ;
        }
        if (this.hand10CurTransZ < 0.0f) {
            this.hand10CurTransZ = 0.0f;
        }
        if (this.hand10CurRot1 > this.hand10MaxRot1) {
            this.hand10CurRot1 = this.hand10MaxRot1;
        }
        if (this.hand10CurRot1 < 0.0f) {
            this.hand10CurRot1 = 0.0f;
        }
        if (this.hand10CurRot2 > this.hand10MaxRot2) {
            this.hand10CurRot2 = this.hand10MaxRot2;
        }
        if (this.hand10CurRot2 < 0.0f) {
            this.hand10CurRot2 = 0.0f;
        }
        if (this.hand10CurRot3 > this.hand10MaxRot3) {
            this.hand10CurRot3 = this.hand10MaxRot3;
        }
        if (this.hand10CurRot3 < 0.0f) {
            this.hand10CurRot3 = 0.0f;
        }
        if (this.hand11CurTransY > this.hand11MaxTransY) {
            this.hand11CurTransY = this.hand11MaxTransY;
        }
        if (this.hand11CurTransY < 0.0f) {
            this.hand11CurTransY = 0.0f;
        }
        if (this.hand11CurTransX > this.hand11MaxTransX) {
            this.hand11CurTransX = this.hand11MaxTransX;
        }
        if (this.hand11CurTransX < 0.0f) {
            this.hand11CurTransX = 0.0f;
        }
        if (this.hand11CurTransZ > this.hand11MaxTransZ) {
            this.hand11CurTransZ = this.hand11MaxTransZ;
        }
        if (this.hand11CurTransZ < 0.0f) {
            this.hand11CurTransZ = 0.0f;
        }
        if (this.hand11CurRot1 > this.hand11MaxRot1) {
            this.hand11CurRot1 = this.hand11MaxRot1;
        }
        if (this.hand11CurRot1 < 0.0f) {
            this.hand11CurRot1 = 0.0f;
        }
        if (this.hand11CurRot2 > this.hand11MaxRot2) {
            this.hand11CurRot2 = this.hand11MaxRot2;
        }
        if (this.hand11CurRot2 < 0.0f) {
            this.hand11CurRot2 = 0.0f;
        }
        if (this.hand11CurRot3 > this.hand11MaxRot3) {
            this.hand11CurRot3 = this.hand11MaxRot3;
        }
        if (this.hand11CurRot3 < 0.0f) {
            this.hand11CurRot3 = 0.0f;
        }
        if (this.hand12CurTransY > this.hand12MaxTransY) {
            this.hand12CurTransY = this.hand12MaxTransY;
        }
        if (this.hand12CurTransY < 0.0f) {
            this.hand12CurTransY = 0.0f;
        }
        if (this.hand12CurTransX > this.hand12MaxTransX) {
            this.hand12CurTransX = this.hand12MaxTransX;
        }
        if (this.hand12CurTransX < 0.0f) {
            this.hand12CurTransX = 0.0f;
        }
        if (this.hand12CurTransZ > this.hand12MaxTransZ) {
            this.hand12CurTransZ = this.hand12MaxTransZ;
        }
        if (this.hand12CurTransZ < 0.0f) {
            this.hand12CurTransZ = 0.0f;
        }
        if (this.hand12CurRot1 > this.hand12MaxRot1) {
            this.hand12CurRot1 = this.hand12MaxRot1;
        }
        if (this.hand12CurRot1 < 0.0f) {
            this.hand12CurRot1 = 0.0f;
        }
        if (this.hand12CurRot2 > this.hand12MaxRot2) {
            this.hand12CurRot2 = this.hand12MaxRot2;
        }
        if (this.hand12CurRot2 < 0.0f) {
            this.hand12CurRot2 = 0.0f;
        }
        if (this.hand12CurRot3 > this.hand12MaxRot3) {
            this.hand12CurRot3 = this.hand12MaxRot3;
        }
        if (this.hand12CurRot3 < 0.0f) {
            this.hand12CurRot3 = 0.0f;
        }
    }

    private void charge(boolean push) {
        if (push) {
            if (this.airCurFloatPruj < 0.1f) {
                this.airCurFloatPruj += 0.01f / (this.animspeed * 2.0f);
            }
        } else if (this.airCurFloatPruj > 0.0f) {
            this.airCurFloatPruj -= 0.01f / (this.animspeed * 2.0f);
        }
    }

    private void sprinting(boolean push) {
        if (push) {
            if (this.sprintCurTransY < this.sprintMaxTransY) {
                this.sprintCurTransY += 0.002f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurTransZ < this.sprintMaxTransZ) {
                this.sprintCurTransZ += 0.005f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurRot1 < this.sprintMaxRot1) {
                this.sprintCurRot1 += 9.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurRot2 < this.sprintMaxRot2) {
                this.sprintCurRot2 += 1.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurTransX < this.sprintMaxTransX) {
                this.sprintCurTransX += 0.005f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.sprintCurTransY > 0.0f) {
                this.sprintCurTransY -= 0.002f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurTransZ > 0.0f) {
                this.sprintCurTransZ -= 0.005f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurRot1 > 0.0f) {
                this.sprintCurRot1 -= 9.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurRot2 > 0.0f) {
                this.sprintCurRot2 -= 1.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurTransX > 0.0f) {
                this.sprintCurTransX -= 0.005f / (this.animspeed * 2.0f);
            }
        }
    }

    private void modifi(boolean push) {
        if (push) {
            if (this.modifiCurTransZ < this.modifiMaxTransZ) {
                this.modifiCurTransZ += 0.002f / (this.animspeed * 2.0f);
            }
            if (this.modifiCurTransY < this.modifiMaxTransY) {
                this.modifiCurTransY += 0.006f / (this.animspeed * 2.0f);
            }
            if (this.modifiCurTransX < this.modifiMaxTransX) {
                this.modifiCurTransX += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.modifiCurRot1 < this.modifiMaxRot1) {
                this.modifiCurRot1 += 4.0f / (this.animspeed * 3.0f);
            }
            if (this.modifiCurRot2 < this.modifiMaxRot2) {
                this.modifiCurRot2 += 4.0f / (this.animspeed * 3.0f);
            }
        } else {
            if (this.modifiCurTransZ > 0.0f) {
                this.modifiCurTransZ -= 0.02f / (this.animspeed * 2.0f);
            }
            if (this.modifiCurTransY > 0.0f) {
                this.modifiCurTransY -= 0.02f / (this.animspeed * 2.0f);
            }
            if (this.modifiCurTransX > 0.0f) {
                this.modifiCurTransX -= 0.004f / (this.animspeed * 2.0f);
            }
            if (this.modifiCurRot1 > 0.0f) {
                this.modifiCurRot1 -= 4.0f / (this.animspeed * 3.0f);
            }
            if (this.modifiCurRot2 > 0.0f) {
                this.modifiCurRot2 -= 4.0f / (this.animspeed * 3.0f);
            }
        }
    }

    private void hand(boolean push) {
        if (push) {
            if (this.handCurTransZ < this.handMaxTransZ) {
                this.handCurTransZ += 0.0f / (this.animspeed * 2.0f);
            }
            if (this.handCurTransY < this.handMaxTransY) {
                this.handCurTransY += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.handCurTransX < this.handMaxTransX) {
                this.handCurTransX += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot1 < this.handMaxRot1) {
                this.handCurRot1 += 4.0f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot2 < this.handMaxRot1) {
                this.handCurRot2 += 4.0f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot3 < this.handMaxRot1) {
                this.handCurRot3 += 0.5f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.handCurTransZ > 0.0f) {
                this.handCurTransZ -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.handCurTransY > 0.0f) {
                this.handCurTransY -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.handCurTransX > 0.0f) {
                this.handCurTransX -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot1 > 0.0f) {
                this.handCurRot1 -= 2.0f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot2 > 0.0f) {
                this.handCurRot2 -= 4.0f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot3 > 0.0f) {
                this.handCurRot3 -= 0.5f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand1(boolean push) {
        if (push) {
            if (this.hand1CurTransZ < this.hand1MaxTransZ) {
                this.hand1CurTransZ += 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransY < this.hand1MaxTransY) {
                this.hand1CurTransY += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransX < this.hand1MaxTransX) {
                this.hand1CurTransX += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurRot1 < this.hand1MaxRot1) {
                this.hand1CurRot1 += 6.8f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurRot3 < this.hand1MaxRot3) {
                this.hand1CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurRot2 < this.hand1MaxRot2) {
                this.hand1CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand1CurTransZ > 0.0f) {
                this.hand1CurTransZ -= 0.025f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransY > 0.0f) {
                this.hand1CurTransY -= 0.025f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransX > 0.0f) {
                this.hand1CurTransX -= 0.025f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurRot1 > 0.0f) {
                this.hand1CurRot1 -= 1.0f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurRot2 > 0.0f) {
                this.hand1CurRot2 -= 1.0f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurRot3 > 0.0f) {
                this.hand1CurRot3 -= 1.0f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand2(boolean push) {
        if (push) {
            if (this.hand2CurTransZ < this.hand2MaxTransZ) {
                this.hand2CurTransZ += 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransY < this.hand2MaxTransY) {
                this.hand2CurTransY += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransX < this.hand2MaxTransX) {
                this.hand2CurTransX += 0.0048f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurRot1 < this.hand2MaxRot1) {
                this.hand2CurRot1 += 10.0f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurRot3 < this.hand2MaxRot3) {
                this.hand2CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurRot2 < this.hand2MaxRot2) {
                this.hand2CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand2CurTransZ > 0.0f) {
                this.hand2CurTransZ -= 0.2f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransY > 0.0f) {
                this.hand2CurTransY -= 0.2f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransX > 0.0f) {
                this.hand2CurTransX -= 0.2f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurRot1 > 0.0f) {
                this.hand2CurRot1 -= 5.0f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurRot2 > 0.0f) {
                this.hand2CurRot2 -= 1.0f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurRot3 > 0.0f) {
                this.hand2CurRot3 -= 1.0f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand3(boolean push) {
        if (push) {
            if (this.hand3CurTransZ < this.hand3MaxTransZ) {
                this.hand3CurTransZ += 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransY < this.hand3MaxTransY) {
                this.hand3CurTransY += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransX < this.hand3MaxTransX) {
                this.hand3CurTransX += 0.0048f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurRot1 < this.hand3MaxRot1) {
                this.hand3CurRot1 += 8.0f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurRot3 < this.hand3MaxRot3) {
                this.hand3CurRot3 += 2.0f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurRot2 < this.hand3MaxRot2) {
                this.hand3CurRot2 += 2.0f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand3CurTransZ > 0.0f) {
                this.hand3CurTransZ -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransY > 0.0f) {
                this.hand3CurTransY -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransX > 0.0f) {
                this.hand3CurTransX -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurRot1 > 0.0f) {
                this.hand3CurRot1 -= 1.0f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurRot2 > 0.0f) {
                this.hand3CurRot2 -= 2.0f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurRot3 > 0.0f) {
                this.hand3CurRot3 -= 2.0f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand4(boolean push) {
        if (push) {
            if (this.hand4CurTransZ < this.hand4MaxTransZ) {
                this.hand4CurTransZ += 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransY < this.hand4MaxTransY) {
                this.hand4CurTransY += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransX < this.hand4MaxTransX) {
                this.hand4CurTransX += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot1 < this.hand4MaxRot1) {
                this.hand4CurRot1 += 8.0f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot3 < this.hand4MaxRot3) {
                this.hand4CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot2 < this.hand4MaxRot2) {
                this.hand4CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand4CurTransZ > 0.0f) {
                this.hand4CurTransZ = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransY > 0.0f) {
                this.hand4CurTransY = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransX > 0.0f) {
                this.hand4CurTransX = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot1 > 0.0f) {
                this.hand4CurRot1 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot2 > 0.0f) {
                this.hand4CurRot2 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot3 > 0.0f) {
                this.hand4CurRot3 -= 0.01f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand5(boolean push) {
        if (push) {
            if (this.hand5CurTransZ < this.hand5MaxTransZ) {
                this.hand5CurTransZ += 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurTransY < this.hand5MaxTransY) {
                this.hand5CurTransY += 0.0056f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurTransX < this.hand5MaxTransX) {
                this.hand5CurTransX += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurRot1 < this.hand5MaxRot1) {
                this.hand5CurRot1 += 8.0f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurRot3 < this.hand5MaxRot3) {
                this.hand5CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurRot2 < this.hand5MaxRot2) {
                this.hand5CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand5CurTransZ > 0.0f) {
                this.hand5CurTransZ -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurTransY > 0.0f) {
                this.hand5CurTransY -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurTransX > 0.0f) {
                this.hand5CurTransX -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurRot1 > 0.0f) {
                this.hand5CurRot1 -= 1.0f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurRot2 > 0.0f) {
                this.hand5CurRot2 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurRot3 > 0.0f) {
                this.hand5CurRot3 -= 0.01f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand6(boolean push) {
        if (push) {
            if (this.hand6CurTransZ < this.hand6MaxTransZ) {
                this.hand6CurTransZ += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransY < this.hand6MaxTransY) {
                this.hand6CurTransY += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransX < this.hand6MaxTransX) {
                this.hand6CurTransX += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurRot1 < this.hand6MaxRot1) {
                this.hand6CurRot1 += 2.0f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurRot3 < this.hand6MaxRot3) {
                this.hand6CurRot3 += 2.0f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurRot2 < this.hand6MaxRot2) {
                this.hand6CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand6CurTransZ > 0.0f) {
                this.hand6CurTransZ -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransY > 0.0f) {
                this.hand6CurTransY -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransX > 0.0f) {
                this.hand6CurTransX -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurRot1 > 0.0f) {
                this.hand6CurRot1 -= 1.0f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurRot2 > 0.0f) {
                this.hand6CurRot2 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurRot3 > 0.0f) {
                this.hand6CurRot3 -= 0.01f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand7(boolean push, boolean z1) {
        if (push) {
            if (this.hand7CurTransZ < this.hand7MaxTransZ) {
                this.hand7CurTransZ += 0.05f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurTransY < this.hand7MaxTransY) {
                this.hand7CurTransY += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurTransX < this.hand7MaxTransX) {
                this.hand7CurTransX += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurRot1 < this.hand7MaxRot1) {
                this.hand7CurRot1 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurRot3 < this.hand7MaxRot3) {
                this.hand7CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurRot2 < this.hand7MaxRot2) {
                this.hand7CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand7CurTransZ > 0.0f) {
                this.hand7CurTransZ -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurTransY > 0.0f) {
                this.hand7CurTransY -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurTransX > 0.0f) {
                this.hand7CurTransX -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurRot1 > 0.0f) {
                this.hand7CurRot1 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurRot2 > 0.0f) {
                this.hand7CurRot2 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurRot3 > 0.0f) {
                this.hand7CurRot3 -= 0.01f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand8(boolean push, boolean z1) {
        if (push) {
            if (this.hand8CurTransZ < this.hand8MaxTransZ) {
                this.hand8CurTransZ += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand8CurTransY < this.hand8MaxTransY) {
                this.hand8CurTransY += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand8CurTransX < this.hand8MaxTransX) {
                this.hand8CurTransX += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand8CurRot1 < this.hand8MaxRot1) {
                this.hand8CurRot1 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand8CurRot3 < this.hand8MaxRot3) {
                this.hand8CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand8CurRot2 < this.hand8MaxRot2) {
                this.hand8CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (z1) {
                if (this.hand8CurTransZ > 0.0f) {
                    this.hand8CurTransZ -= 0.01f / (this.animspeed * 2.0f);
                }
                if (this.hand8CurTransY > 0.0f) {
                    this.hand8CurTransY -= 0.01f / (this.animspeed * 2.0f);
                }
                if (this.hand8CurTransX > 0.0f) {
                    this.hand8CurTransX -= 0.01f / (this.animspeed * 2.0f);
                }
            } else {
                if (this.hand8CurTransZ > 0.0f) {
                    this.hand8CurTransZ -= 0.01f / (this.animspeed * 2.0f);
                }
                if (this.hand8CurTransY > 0.0f) {
                    this.hand8CurTransY -= 0.01f / (this.animspeed * 2.0f);
                }
                if (this.hand8CurTransX > 0.0f) {
                    this.hand8CurTransX -= 0.01f / (this.animspeed * 2.0f);
                }
            }
            if (this.hand8CurRot1 > 0.0f) {
                this.hand8CurRot1 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand8CurRot2 > 0.0f) {
                this.hand8CurRot2 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand8CurRot3 > 0.0f) {
                this.hand8CurRot3 -= 0.01f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand9(boolean push) {
        if (push) {
            if (this.hand9CurTransZ < this.hand9MaxTransZ) {
                this.hand9CurTransZ += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurTransY < this.hand9MaxTransY) {
                this.hand9CurTransY += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurTransX < this.hand9MaxTransX) {
                this.hand9CurTransX += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurRot1 < this.hand9MaxRot1) {
                this.hand9CurRot1 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurRot3 < this.hand9MaxRot3) {
                this.hand9CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurRot2 < this.hand9MaxRot2) {
                this.hand9CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand9CurTransZ > 0.0f) {
                this.hand9CurTransZ = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurTransY > 0.0f) {
                this.hand9CurTransY = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurTransX > 0.0f) {
                this.hand9CurTransX = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurRot1 > 0.0f) {
                this.hand9CurRot1 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurRot2 > 0.0f) {
                this.hand9CurRot2 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand9CurRot3 > 0.0f) {
                this.hand9CurRot3 -= 0.01f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand10(boolean push) {
        if (push) {
            if (this.hand10CurTransZ < this.hand10MaxTransZ) {
                this.hand10CurTransZ += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurTransY < this.hand10MaxTransY) {
                this.hand10CurTransY += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurTransX < this.hand10MaxTransX) {
                this.hand10CurTransX += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurRot1 < this.hand10MaxRot1) {
                this.hand10CurRot1 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurRot3 < this.hand10MaxRot3) {
                this.hand10CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurRot2 < this.hand10MaxRot2) {
                this.hand10CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand10CurTransZ > 0.0f) {
                this.hand10CurTransZ = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurTransY > 0.0f) {
                this.hand10CurTransY = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurTransX > 0.0f) {
                this.hand10CurTransX = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurRot1 > 0.0f) {
                this.hand10CurRot1 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurRot2 > 0.0f) {
                this.hand10CurRot2 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand10CurRot3 > 0.0f) {
                this.hand10CurRot3 -= 0.01f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand11(boolean push) {
        if (push) {
            if (this.hand11CurTransZ < this.hand11MaxTransZ) {
                this.hand11CurTransZ += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurTransY < this.hand11MaxTransY) {
                this.hand11CurTransY += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurTransX < this.hand11MaxTransX) {
                this.hand11CurTransX += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurRot1 < this.hand11MaxRot1) {
                this.hand11CurRot1 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurRot3 < this.hand11MaxRot3) {
                this.hand11CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurRot2 < this.hand11MaxRot2) {
                this.hand11CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand11CurTransZ > 0.0f) {
                this.hand11CurTransZ = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurTransY > 0.0f) {
                this.hand11CurTransY = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurTransX > 0.0f) {
                this.hand11CurTransX = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurRot1 > 0.0f) {
                this.hand11CurRot1 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurRot2 > 0.0f) {
                this.hand11CurRot2 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand11CurRot3 > 0.0f) {
                this.hand11CurRot3 -= 0.01f / (this.animspeed * 2.0f);
            }
        }
    }

    private void hand12(boolean push) {
        if (push) {
            if (this.hand12CurTransZ < this.hand12MaxTransZ) {
                this.hand12CurTransZ += 0.015f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurTransY < this.hand12MaxTransY) {
                this.hand12CurTransY += 0.015f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurTransX < this.hand12MaxTransX) {
                this.hand12CurTransX += 0.015f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurRot1 < this.hand12MaxRot1) {
                this.hand12CurRot1 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurRot3 < this.hand12MaxRot3) {
                this.hand12CurRot3 += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurRot2 < this.hand12MaxRot2) {
                this.hand12CurRot2 += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.hand12CurTransZ > 0.0f) {
                this.hand12CurTransZ = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurTransY > 0.0f) {
                this.hand12CurTransY = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurTransX > 0.0f) {
                this.hand12CurTransX = 0.0f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurRot1 > 0.0f) {
                this.hand12CurRot1 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurRot2 > 0.0f) {
                this.hand12CurRot2 -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand12CurRot3 > 0.0f) {
                this.hand12CurRot3 -= 0.01f / (this.animspeed * 2.0f);
            }
        }
    }

    private void partCompliteCheck() {
        this.Complete1 = this.handCurTransZ == this.handMaxTransZ && this.handCurTransY == this.handMaxTransY && this.handCurTransX == this.handMaxTransX && this.handCurRot1 == this.handMaxRot1 && this.handCurRot2 == this.handMaxRot2;
        this.Complete2 = this.hand1CurTransZ == this.hand1MaxTransZ && this.hand1CurTransX == this.hand1MaxTransX && this.hand1CurTransY == this.hand1MaxTransY && this.hand1CurRot1 == this.hand1MaxRot1;
        this.Complete3 = this.hand2CurTransZ == this.hand2MaxTransZ && this.hand2CurTransX == this.hand2MaxTransX && this.hand2CurTransY == this.hand2MaxTransY && this.hand2CurRot1 == this.hand2MaxRot1;
        this.Complete4 = this.hand3CurTransZ == this.hand3MaxTransZ && this.hand3CurTransX == this.hand3MaxTransX && this.hand3CurTransY == this.hand3MaxTransY && this.hand3CurRot1 == this.hand3MaxRot1;
        this.Complete5 = this.hand4CurTransZ == this.hand4MaxTransZ && this.hand4CurTransX == this.hand4MaxTransX && this.hand4CurTransY == this.hand4MaxTransY;
        this.Complete6 = this.hand5CurTransZ == this.hand5MaxTransZ && this.hand5CurTransX == this.hand5MaxTransX && this.hand5CurTransY == this.hand5MaxTransY;
        this.Complete7 = this.hand6CurTransZ == this.hand6MaxTransZ && this.hand6CurTransX == this.hand6MaxTransX && this.hand6CurTransY == this.hand6MaxTransY && this.hand6CurRot1 == this.hand6MaxRot1;
        this.Complete8 = this.hand7CurTransZ == this.hand7MaxTransZ && this.hand7CurTransX == this.hand7MaxTransX && this.hand7CurTransY == this.hand7MaxTransY && this.hand7CurRot1 == this.hand7MaxRot1;
        this.Complete9 = this.hand8CurTransZ == this.hand8MaxTransZ && this.hand8CurTransX == this.hand8MaxTransX && this.hand8CurTransY == this.hand8MaxTransY && this.hand8CurRot1 == this.hand8MaxRot1;
        this.Complete10 = this.hand9CurTransZ == this.hand9MaxTransZ && this.hand9CurTransX == this.hand9MaxTransX && this.hand9CurTransY == this.hand9MaxTransY;
        this.Complete11 = this.hand10CurTransZ == this.hand10MaxTransZ && this.hand10CurTransX == this.hand10MaxTransX && this.hand10CurTransY == this.hand10MaxTransY && this.hand10CurRot1 == this.hand10MaxRot1;
        this.Complete12 = this.hand11CurTransZ == this.hand11MaxTransZ && this.hand11CurTransX == this.hand11MaxTransX && this.hand11CurTransY == this.hand11MaxTransY && this.hand11CurRot1 == this.hand11MaxRot1;
        this.Complete13 = this.hand12CurTransZ == this.hand12MaxTransZ && this.hand12CurTransX == this.hand12MaxTransX && this.hand12CurTransY == this.hand12MaxTransY && this.hand12CurRot1 == this.hand12MaxRot1;
    }
}
