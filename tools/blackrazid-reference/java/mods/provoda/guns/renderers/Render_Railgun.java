/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bdv
 *  mk
 *  ml
 *  mods.provoda.GLLoader.GLModel
 *  mods.provoda.guns.models.clock.Clock
 *  mods.provoda.guns.models.clock.HourArrowModel
 *  mods.provoda.guns.models.clock.MatrixDebugModel
 *  mods.provoda.guns.models.clock.MinArrowModel
 *  mods.provoda.guns.models.clock.O2ArrowModel
 *  mods.provoda.guns.models.clock.SekArrowModel
 *  mods.provoda.guns.renderers.Render_Railgun$NamelessClass1064171697
 *  mods.provoda.models.Laser
 *  mods.provoda.models.Shoot
 *  mods.provoda.until.ClockKeyBind
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
import mods.provoda.guns.Railgun;
import mods.provoda.guns.models.clock.Clock;
import mods.provoda.guns.models.clock.HourArrowModel;
import mods.provoda.guns.models.clock.MatrixDebugModel;
import mods.provoda.guns.models.clock.MinArrowModel;
import mods.provoda.guns.models.clock.O2ArrowModel;
import mods.provoda.guns.models.clock.SekArrowModel;
import mods.provoda.guns.renderers.Render_Railgun;
import mods.provoda.models.Laser;
import mods.provoda.models.Shoot;
import mods.provoda.until.ClockKeyBind;
import mods.provoda.until.Hand;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Render_Railgun
implements IItemRenderer {
    GLModel bullet = new GLModel("/mods/provoda/guns/models/tihar/bullet.obj");
    GLModel helsing = new GLModel("/mods/provoda/guns/models/railgun/railgun.obj");
    GLModel el = new GLModel("/mods/provoda/guns/models/railgun/el.obj");
    GLModel mag = new GLModel("/mods/provoda/guns/models/railgun/mag.obj");
    GLModel strelka = new GLModel("/mods/provoda/guns/models/railgun/strelka.obj");
    GLModel optica = new GLModel("/mods/provoda/guns/models/railgun/optica.obj");
    GLModel zoomer = new GLModel("/mods/provoda/guns/models/railgun/zoomer.obj");
    GLModel test = new GLModel("/mods/provoda/guns/models/railgun/test.obj");
    GLModel laser = new GLModel("/mods/provoda/guns/models/rpk/laser.obj");
    GLModel akk = new GLModel("/mods/provoda/guns/models/railgun/railgun_akk.obj");
    GLModel kon = new GLModel("/mods/provoda/guns/models/railgun/railgun_kon.obj");
    GLModel krep = new GLModel("/mods/provoda/guns/models/railgun/krep.obj");
    GLModel opticasmall = new GLModel("/mods/provoda/guns/models/rpk/optica.obj");
    GLModel kolimator = new GLModel("/mods/provoda/guns/models/ashot/kolimator.obj");
    Hand hand = new Hand();
    Clock clock = new Clock();
    Shoot shoot = new Shoot();
    Laser laserline = new Laser();
    Random rand = new Random();
    O2ArrowModel O2ArrouModel = new O2ArrowModel();
    MinArrowModel MinArrouModel = new MinArrowModel();
    HourArrowModel HourArrowModel = new HourArrowModel();
    SekArrowModel SekArrowModel = new SekArrowModel();
    MatrixDebugModel debugModel = new MatrixDebugModel();
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
    private boolean Cmplete11;
    private boolean isReloading;
    private boolean isUnloading;
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
    private float scopeMaxRot1 = 0.8f;
    private float scopeMaxRot2 = 0.4f;
    private float scopeMaxTransX = 0.1408f;
    private float scopeMaxTransY = 0.044f;
    private float scopeMaxTransZ = 0.145f;
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
    private float maxytrans = 0.0f;
    private float maxztrans = 0.39f;
    private float reloadCurTransX;
    private float reloadCurTransZ;
    private float reloadMaxTransX = 0.05f;
    private float reloadMaxTransZ = 0.2f;
    private float handCurTransX;
    private float handCurTransZ;
    private float handCurTransY;
    private float handMaxTransX = 0.1f;
    private float handMaxTransY = 0.0f;
    private float handMaxTransZ = 0.0f;
    private float handCurRot1;
    private float handCurRot2;
    private float handCurRot3;
    private float handMaxRot1 = 0.0f;
    private float handMaxRot2;
    private float handMaxRot3;
    private float hand1CurTransX;
    private float hand1CurTransY;
    private float hand1CurTransZ;
    private float hand1MaxTransX = 0.5f;
    private float hand1MaxTransY = 0.2f;
    private float hand1MaxTransZ = 0.3f;
    private float hand1CurRot1;
    private float hand1CurRot2;
    private float hand1CurRot3;
    private float hand1MaxRot1;
    private float hand1MaxRot2;
    private float hand1MaxRot3;
    private float hand2CurTransX;
    private float hand2CurTransY;
    private float hand2CurTransZ;
    private float hand2MaxTransX = 0.15f;
    private float hand2MaxTransY = 0.0f;
    private float hand2MaxTransZ = 0.0f;
    private float hand2CurRot1;
    private float hand2CurRot2;
    private float hand2CurRot3;
    private float hand2MaxRot1;
    private float hand2MaxRot2;
    private float hand2MaxRot3;
    private float hand3CurTransX;
    private float hand3CurTransY;
    private float hand3CurTransZ;
    private float hand3MaxTransX = 0.0f;
    private float hand3MaxTransY = 0.0f;
    private float hand3MaxTransZ = 0.01f;
    private float hand3CurRot1;
    private float hand3CurRot2;
    private float hand3CurRot3;
    private float hand3MaxRot1 = 0.0f;
    private float hand3MaxRot2;
    private float hand3MaxRot3;
    private float hand4CurTransX;
    private float hand4CurTransY;
    private float hand4CurTransZ;
    private float hand4MaxTransX = 0.04f;
    private float hand4MaxTransY = 0.03f;
    private float hand4MaxTransZ = 0.0f;
    private float hand4CurRot1;
    private float hand4CurRot2;
    private float hand4CurRot3;
    private float hand4MaxRot1;
    private float hand4MaxRot2;
    private float hand4MaxRot3;
    private float hand5CurTransX;
    private float hand5CurTransY;
    private float hand5CurTransZ;
    private float hand5MaxTransX = 0.0f;
    private float hand5MaxTransY = 0.0f;
    private float hand5MaxTransZ = 0.0f;
    private float hand5CurRot1;
    private float hand5CurRot2;
    private float hand5CurRot3;
    private float hand5MaxRot1 = 10.0f;
    private float hand5MaxRot2 = 20.0f;
    private float hand5MaxRot3;
    private float hand6CurTransX;
    private float hand6CurTransY;
    private float hand6CurTransZ;
    private float hand6MaxTransX;
    private float hand6MaxTransY;
    private float hand6MaxTransZ = 0.060000002f;
    private float hand6CurRot1;
    private float hand6CurRot2;
    private float hand6CurRot3;
    private float hand6MaxRot1 = 60.0f;
    private float hand6MaxRot2;
    private float hand6MaxRot3;
    private float hand7CurTransX;
    private float hand7CurTransY;
    private float hand7CurTransZ;
    private float hand7MaxTransX = 0.0f;
    private float hand7MaxTransY = 0.0f;
    private float hand7MaxTransZ = 0.55f;
    private float hand7CurRot1;
    private float hand7CurRot2;
    private float hand7CurRot3;
    private float hand7MaxRot1 = 50.0f;
    private float hand7MaxRot2;
    private float hand7MaxRot3;
    private float hand8CurTransX;
    private float hand8CurTransY;
    private float hand8CurTransZ;
    private float hand8MaxTransX = 0.0f;
    private float hand8MaxTransY = 0.2f;
    private float hand8MaxTransZ = 0.1f;
    private float hand8CurRot1;
    private float hand8CurRot2;
    private float hand8CurRot3;
    private float hand8MaxRot1;
    private float hand8MaxRot2;
    private float hand8MaxRot3;
    private float hand9CurTransX;
    private float hand9CurTransY;
    private float hand9CurTransZ;
    private float hand9MaxTransX = 0.0f;
    private float hand9MaxTransY = 0.1f;
    private float hand9MaxTransZ = 0.2f;
    private float hand9CurRot1;
    private float hand9CurRot2;
    private float hand9CurRot3;
    private float hand9MaxRot1;
    private float hand9MaxRot2;
    private float hand9MaxRot3;
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
    private boolean rend;
    private boolean airanim;
    private boolean airanim2;
    private boolean hand7;
    private boolean hand7_1 = true;
    private float timershoot;
    private float animspeed = 1.0f;
    private float zoom;

    public boolean handleRenderType(wm item, IItemRenderer.ItemRenderType type) {
        switch (NamelessClass1064171697.$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRenderType[type.ordinal()]) {
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
        this.animspeed = 0.6f;
        int shootTimer = Railgun.otdachaTimer;
        boolean shooting = item.q().n("Shoot");
        switch (NamelessClass1064171697.$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRenderType[type.ordinal()]) {
            case 1: {
                GL11.glPushMatrix();
                GL11.glShadeModel((int)7425);
                GL11.glScaled((double)2.5, (double)2.5, (double)2.0);
                GL11.glTranslatef((float)0.2f, (float)-0.05f, (float)-0.13f);
                GL11.glRotatef((float)15.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)12.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.helsing.render();
                GL11.glPushMatrix();
                GL11.glEnable((int)2884);
                GL11.glScaled((double)1.0, (double)1.0, (double)1.0);
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.7f);
                GL11.glRotatef((float)this.shootrot, (float)0.0f, (float)0.0f, (float)1.0f);
                if (this.shoot1 > 0.0f) {
                    this.shoot1 -= 1.0f;
                }
                if (z > 0 && shooting) {
                    if (this.shoot1 <= 0.0f || this.hideshot > 0.0f) {
                        if (z != 1 && z != 5 && z != 9 && z != 11 && z != 15) {
                            if (z != 3 && z != 8 && z != 4 && z != 6 && z != 13) {
                                if (z == 7 || z == 10 || z == 12 || z == 2 || z == 14) {
                                    mc.p.b("/mods/provoda/textures/light3.png");
                                    this.shoot.render();
                                }
                            } else {
                                mc.p.b("/mods/provoda/textures/light2.png");
                                this.shoot.render();
                            }
                        } else {
                            mc.p.b("/mods/provoda/textures/light1.png");
                            this.shoot.render();
                        }
                        this.hideshot -= 1.0f / (this.animspeed * 2.0f);
                    }
                    this.shootrot = this.rand.nextInt(360);
                } else {
                    this.hideshot = 10.0f;
                }
                GL11.glPopMatrix();
                if (item.q().n("HasAmmo")) {
                    this.mag.render();
                }
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.098f, (float)0.025f, (float)0.198f);
                GL11.glRotatef((float)-45.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.el.render();
                GL11.glPopMatrix();
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.Optica) {
                    GL11.glPushMatrix();
                    this.optica.render();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.25f);
                    this.test.render();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                } else if (revinv.a(0) != null && revinv.a(0).b() == ModBase.OpticaSmall) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.0f);
                    this.krep.render();
                    GL11.glTranslatef((float)0.0f, (float)-0.0f, (float)-0.0f);
                    GL11.glEnable((int)2884);
                    GL11.glTranslatef((float)0.0f, (float)0.17f, (float)0.0f);
                    this.opticasmall.render();
                    GL11.glDisable((int)2884);
                    GL11.glPopMatrix();
                } else if (revinv.a(0) != null && revinv.a(0).b() == ModBase.Kolimator) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.0f);
                    this.krep.render();
                    GL11.glTranslatef((float)-0.003f, (float)0.16f, (float)-0.0f);
                    this.kolimator.render();
                    GL11.glPopMatrix();
                }
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.Laser) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.02f, (float)0.05f, (float)0.13f);
                    this.laser.render();
                    GL11.glPushMatrix();
                    GL11.glScaled((double)0.007f, (double)0.007f, (double)0.02f);
                    GL11.glTranslatef((float)4.5f, (float)0.5f, (float)0.4f);
                    GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glScaled((double)350.0, (double)1.0, (double)1.0);
                    GL11.glTranslatef((float)-0.36f, (float)0.0f, (float)0.0f);
                    mc.p.b("/mods/provoda/textures/laser.png");
                    this.laserline.render();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                }
                if (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunAkk) {
                    this.akk.render();
                } else if (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon) {
                    this.kon.render();
                }
                GL11.glPopMatrix();
                break;
            }
            case 2: {
                GL11.glPushMatrix();
                GL11.glScaled((double)3.5, (double)3.5, (double)3.5);
                GL11.glTranslatef((float)-0.0f, (float)-0.06f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.helsing.render();
                if (item.q().n("HasAmmo")) {
                    this.mag.render();
                }
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.098f, (float)0.025f, (float)0.198f);
                GL11.glRotatef((float)-45.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.el.render();
                GL11.glPopMatrix();
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.Optica) {
                    GL11.glPushMatrix();
                    this.optica.render();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.25f);
                    this.test.render();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                } else if (revinv.a(0) != null && revinv.a(0).b() == ModBase.OpticaSmall) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.0f);
                    this.krep.render();
                    GL11.glTranslatef((float)0.0f, (float)-0.0f, (float)-0.0f);
                    GL11.glEnable((int)2884);
                    GL11.glTranslatef((float)0.0f, (float)0.17f, (float)0.0f);
                    this.opticasmall.render();
                    GL11.glDisable((int)2884);
                    GL11.glPopMatrix();
                } else if (revinv.a(0) != null && revinv.a(0).b() == ModBase.Kolimator) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.0f);
                    this.krep.render();
                    GL11.glTranslatef((float)-0.003f, (float)0.16f, (float)-0.0f);
                    this.kolimator.render();
                    GL11.glPopMatrix();
                }
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.Laser) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.02f, (float)0.05f, (float)0.13f);
                    this.laser.render();
                    GL11.glPushMatrix();
                    GL11.glScaled((double)0.007f, (double)0.007f, (double)0.02f);
                    GL11.glTranslatef((float)4.5f, (float)0.5f, (float)0.4f);
                    GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glScaled((double)350.0, (double)1.0, (double)1.0);
                    GL11.glTranslatef((float)-0.36f, (float)0.0f, (float)0.0f);
                    mc.p.b("/mods/provoda/textures/laser.png");
                    this.laserline.render();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                }
                if (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunAkk) {
                    this.akk.render();
                } else if (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon) {
                    this.kon.render();
                }
                GL11.glPopMatrix();
                break;
            }
            case 3: {
                this.Check();
                this.partCompliteCheck();
                this.isReloading = item.q().e("LoadingTimer") > 0;
                this.isUnloading = item.q().e("UnloadingTimer") > 0;
                this.SkopePush = Mouse.isButtonDown((int)1) && mc.s == null && !item.q().n("Air_Reload") && !item.q().n("Modifi") ? !mc.g.ah() : false;
                if (item.q().n("Modifi")) {
                    this.modifi(true);
                } else {
                    this.modifi(false);
                }
                if (item.q().n("Air_Reload") && !mc.g.ah()) {
                    this.hand5(true);
                    if (item.q().n("Charge")) {
                        this.hand6(true);
                    } else {
                        this.hand6(false);
                    }
                } else {
                    this.hand5(false);
                    this.hand6(false);
                }
                if (!(!ClockKeyBind.keyPressed || this.SkopePush || this.isUnloading || this.isReloading || mc.g.ah())) {
                    this.clock(true);
                } else {
                    this.clock(false);
                }
                if (Minecraft.x().g.ah()) {
                    this.sprinting(true);
                } else {
                    this.sprinting(false);
                }
                if (this.isUnloading) {
                    this.hand(true);
                    if (this.Complete1) {
                        this.hand1(true);
                    }
                    if (this.Complete2) {
                        this.hand2(true);
                    }
                    if (this.Complete3) {
                        this.hand3(true);
                    }
                    if (this.Complete4) {
                        this.hand4(true);
                    }
                    if (this.Complete5) {
                        this.hand7(true);
                    }
                    this.hand7_1 = true;
                    this.rend = false;
                } else if (this.isReloading) {
                    this.hand(true);
                    if (this.Complete1 && !this.rend) {
                        this.hand7(true);
                        if (this.Complete8) {
                            this.hand1(true);
                            this.hand2(true);
                            this.hand3(true);
                            this.hand4(true);
                            this.rend = true;
                        }
                    } else {
                        this.hand7(false);
                    }
                    if (this.rend && this.hand7CurTransX == 0.0f && this.hand7CurTransY == 0.0f && this.hand7CurTransZ == 0.0f) {
                        this.hand4(false);
                    }
                    if (this.rend && this.hand4CurTransX == 0.0f && this.hand4CurTransY == 0.0f && this.hand4CurTransZ == 0.0f) {
                        this.hand3(false);
                    }
                    if (this.rend && this.hand3CurTransX == 0.0f && this.hand3CurTransY == 0.0f && this.hand3CurTransZ == 0.0f) {
                        this.hand2(false);
                    }
                    if (this.rend && this.hand2CurTransX == 0.0f && this.hand2CurTransY == 0.0f && this.hand2CurTransZ == 0.0f) {
                        this.hand1(false);
                    }
                    this.hand7 = true;
                } else {
                    this.hand(false);
                    this.hand1(false);
                    this.hand2(false);
                    this.hand7(false);
                    this.hand3(false);
                    this.hand4(false);
                    this.hand7 = true;
                    this.hand7_1 = true;
                }
                GL11.glPushMatrix();
                GL11.glScaled((double)10.0, (double)10.0, (double)10.0);
                GL11.glTranslatef((float)0.3f, (float)0.05f, (float)0.06f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)26.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)84.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glShadeModel((int)7425);
                if (revinv.a(0) != null) {
                    if (revinv.a(0).b() == ModBase.Kolimator) {
                        if (this.otdachaCurTrans <= 0.0f && (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon && shootTimer == 7 || shootTimer == 19) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.03f;
                        } else if (this.otdachaCurTrans > 0.0f && (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon && shootTimer == 7 || shootTimer == 19) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.03f;
                        }
                    } else if (revinv.a(0).b() == ModBase.OpticaSmall) {
                        if (this.otdachaCurTrans <= 0.0f && (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon && shootTimer == 7 || shootTimer == 19) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.02f;
                        } else if (this.otdachaCurTrans > 0.0f && (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon && shootTimer == 7 || shootTimer == 19) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.02f;
                        }
                    } else if (revinv.a(0).b() == ModBase.Optica) {
                        if (this.otdachaCurTrans <= 0.0f && (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon && shootTimer == 7 || shootTimer == 19) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.02f;
                        } else if (this.otdachaCurTrans > 0.0f && (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon && shootTimer == 7 || shootTimer == 19) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.02f;
                        }
                    }
                } else if (this.otdachaCurTrans <= 0.0f && (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon && shootTimer == 7 || shootTimer == 19) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                    this.otdachaCurTrans = 0.08f;
                } else if (this.otdachaCurTrans > 0.0f && (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon && shootTimer == 7 || shootTimer == 19) && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                    this.otdachaCurTrans = 0.08f;
                }
                if (this.otdachaCurTrans > 0.0f) {
                    this.otdachaCurTrans -= 0.005f / (this.animspeed * 2.0f);
                }
                GL11.glTranslatef((float)0.0f, (float)(-this.modifiCurRot1 / 700.0f), (float)this.modifiCurTransZ);
                GL11.glRotatef((float)(this.modifiCurRot1 * 2.5f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)(-this.modifiCurRot2), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)(-this.otdachaCurTrans));
                if (revinv.a(0) != null) {
                    if (revinv.a(0).b() == ModBase.Optica) {
                        mc.g.n(mk.r.H);
                        GL11.glTranslatef((float)(this.scopeCurTransX / 1.075f), (float)(-this.scopeCurTransY / 3.2f), (float)(-this.scopeCurTransZ * 2.1f));
                        GL11.glRotatef((float)this.scopeCurRot1, (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glRotatef((float)this.scopeCurRot2, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(this.scopeCurRot2 / 0.4f), (float)1.0f, (float)0.0f, (float)0.0f);
                        if (this.SkopePush) {
                            if (mc.z.aj >= -1.2f) {
                                mc.z.aj -= 0.1f;
                                this.zoom -= 0.1f;
                            }
                            mc.z.c = 0.1f;
                        } else {
                            if (this.zoom < 0.0f) {
                                mc.z.aj += 0.1f;
                                this.zoom += 0.1f;
                            }
                            mc.z.c = 0.5f;
                        }
                    } else if (revinv.a(0).b() == ModBase.Kolimator) {
                        mc.g.n(mk.r.H);
                        GL11.glTranslatef((float)(this.scopeCurTransX / 1.0345f), (float)(-this.scopeCurTransY / 3.8f), (float)(-this.scopeCurTransZ * 1.2f));
                        GL11.glRotatef((float)this.scopeCurRot1, (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glRotatef((float)this.scopeCurRot2, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(this.scopeCurRot2 / 0.4f), (float)1.0f, (float)0.0f, (float)0.0f);
                        if (this.SkopePush) {
                            if (mc.z.aj >= -0.3f) {
                                mc.z.aj -= 0.1f;
                                this.zoom -= 0.1f;
                            }
                            mc.z.c = 0.4f;
                        } else {
                            if (this.zoom < 0.0f) {
                                mc.z.aj += 0.1f;
                                this.zoom += 0.1f;
                            }
                            mc.z.c = 0.5f;
                        }
                    } else if (revinv.a(0).b() == ModBase.OpticaSmall) {
                        GL11.glTranslatef((float)(this.scopeCurTransX / 1.0684f), (float)(-this.scopeCurTransY / 3.84f), (float)(-this.scopeCurTransZ * 1.87f));
                        GL11.glRotatef((float)this.scopeCurRot1, (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glRotatef((float)this.scopeCurRot2, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(this.scopeCurRot2 / 0.4f), (float)1.0f, (float)0.0f, (float)0.0f);
                        if (this.SkopePush) {
                            mc.g.d(new ml(16, 753, 0));
                            if (mc.z.aj >= -0.8f) {
                                mc.z.aj -= 0.1f;
                                this.zoom -= 0.1f;
                            }
                            mc.z.c = 0.4f;
                        } else {
                            mc.g.n(mk.r.H);
                            if (this.zoom < 0.0f) {
                                mc.z.aj += 0.1f;
                                this.zoom += 0.1f;
                            }
                            mc.z.c = 0.5f;
                        }
                    } else {
                        mc.g.n(mk.r.H);
                        GL11.glTranslatef((float)(this.scopeCurTransX / 1.105f), (float)(this.scopeCurTransY / 1.87f), (float)(-this.scopeCurTransZ * 3.5f));
                        GL11.glRotatef((float)this.scopeCurRot1, (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glRotatef((float)this.scopeCurRot2, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(this.scopeCurRot2 / 0.4f), (float)1.0f, (float)0.0f, (float)0.0f);
                    }
                } else {
                    mc.g.n(mk.r.H);
                    GL11.glTranslatef((float)(this.scopeCurTransX / 1.105f), (float)(this.scopeCurTransY / 1.87f), (float)(-this.scopeCurTransZ * 3.5f));
                    GL11.glRotatef((float)this.scopeCurRot1, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)this.scopeCurRot2, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)(this.scopeCurRot2 / 0.4f), (float)1.0f, (float)0.0f, (float)0.0f);
                }
                GL11.glTranslatef((float)this.sprintCurTransX, (float)(this.sprintCurTransY / 5.0f), (float)(-this.sprintCurTransZ));
                GL11.glRotatef((float)(this.sprintCurRot1 / 1.5f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)(this.sprintCurRot2 * 1.5f), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)(-this.hand5CurRot1 / 100.0f), (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)this.hand5CurRot1, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)(this.hand5CurRot2 * 1.5f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)this.hand5CurRot2, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)this.handCurTransX, (float)0.0f, (float)0.0f);
                GL11.glDisable((int)2884);
                this.helsing.render();
                if (revinv.a(0) != null && revinv.a(0).b() == ModBase.Optica) {
                    GL11.glPushMatrix();
                    this.optica.render();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.25f);
                    this.test.render();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                } else if (revinv.a(0) != null && revinv.a(0).b() == ModBase.OpticaSmall) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.0f);
                    this.krep.render();
                    GL11.glTranslatef((float)0.0f, (float)-0.0f, (float)-0.0f);
                    GL11.glEnable((int)2884);
                    GL11.glTranslatef((float)0.0f, (float)0.17f, (float)0.0f);
                    this.opticasmall.render();
                    GL11.glDisable((int)2884);
                    GL11.glPopMatrix();
                } else if (revinv.a(0) != null && revinv.a(0).b() == ModBase.Kolimator) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.0f);
                    this.krep.render();
                    GL11.glTranslatef((float)-0.003f, (float)0.16f, (float)-0.0f);
                    this.kolimator.render();
                    GL11.glPopMatrix();
                }
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.Laser) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.02f, (float)0.05f, (float)0.13f);
                    this.laser.render();
                    GL11.glPushMatrix();
                    GL11.glScaled((double)0.007f, (double)0.007f, (double)0.02f);
                    GL11.glTranslatef((float)2.7f, (float)0.2f, (float)0.4f);
                    GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)96.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    if (this.SkopePush) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)2.0f);
                        GL11.glRotatef((float)-6.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    }
                    GL11.glScaled((double)350.0, (double)1.0, (double)1.0);
                    GL11.glTranslatef((float)-0.36f, (float)0.0f, (float)0.0f);
                    mc.p.b("/mods/provoda/textures/laser.png");
                    this.laserline.render();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                }
                if (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunAkk) {
                    this.akk.render();
                } else if (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunKon) {
                    this.kon.render();
                }
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.083f, (float)0.05f, (float)-0.146f);
                GL11.glRotatef((float)-30.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)45.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                if (revinv.a(2) != null && revinv.a(2).b() == ModBase.RailgunAkk) {
                    GL11.glRotatef((float)((float)(-item.q().e("Air")) * 1.5f), (float)0.0f, (float)1.0f, (float)0.0f);
                } else {
                    GL11.glRotatef((float)(-item.q().e("Air") * 2), (float)0.0f, (float)1.0f, (float)0.0f);
                }
                GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                this.strelka.render();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)this.hand3CurTransZ);
                GL11.glTranslatef((float)(-this.hand4CurTransX * 1.5f), (float)(-this.hand4CurTransY), (float)0.0f);
                GL11.glTranslatef((float)(this.hand7CurTransZ / 20.0f), (float)0.0f, (float)(-this.hand7CurTransZ));
                if (item.q().n("HasAmmo") && !this.isUnloading && !this.isReloading || this.isReloading && this.rend || this.isUnloading) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.0f);
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    this.mag.render();
                    GL11.glPopMatrix();
                    GL11.glScalef((float)1.5f, (float)1.5f, (float)1.3f);
                    GL11.glTranslatef((float)-0.04f, (float)0.053f, (float)-0.0f);
                    if (z > 0) {
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 1) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 2) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 3) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 4) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 5) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 6) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 7) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 8) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 9) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 10) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 11) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 12) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 13) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 14) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    if (z > 15) {
                        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.014f);
                        GL11.glPushMatrix();
                        this.bullet.render();
                        GL11.glPopMatrix();
                    }
                    GL11.glPopMatrix();
                }
                GL11.glPushMatrix();
                GL11.glScaled((double)0.35f, (double)0.4f, (double)0.4f);
                GL11.glRotatef((float)85.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)-10.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glTranslatef((float)-0.1f, (float)-0.45f, (float)0.05f);
                GL11.glTranslatef((float)(this.sprintCurRot1 / 250.0f), (float)(-this.sprintCurRot1 / 10000.0f), (float)0.0f);
                GL11.glRotatef((float)(this.sprintCurRot1 + 0.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)(-this.hand1CurTransX), (float)this.hand1CurTransY, (float)(-this.hand1CurTransZ));
                GL11.glTranslatef((float)this.hand2CurTransX, (float)0.0f, (float)0.0f);
                GL11.glBindTexture((int)3553, (int)i1);
                this.hand.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glRotatef((float)82.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)30.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)0.06f, (float)-0.03f, (float)0.05f);
                GL11.glScaled((double)0.4f, (double)0.6f, (double)0.4f);
                GL11.glPushMatrix();
                GL11.glEnable((int)2884);
                GL11.glScaled((double)5.4f, (double)5.4f, (double)5.4f);
                GL11.glTranslatef((float)0.35f, (float)0.5f, (float)0.1f);
                GL11.glRotatef((float)10.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)-30.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)100.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)this.shootrot, (float)0.0f, (float)0.0f, (float)1.0f);
                if (this.shoot1 > 0.0f) {
                    this.shoot1 -= 1.0f;
                }
                if (!((revinv.a(2) == null || revinv.a(2).b() != ModBase.RailgunKon || shootTimer != 7) && shootTimer != 19 || z <= 0 || this.isReloading || this.isUnloading || mc.g.ah() || mc.s != null)) {
                    if (this.shoot1 <= 0.0f || this.hideshot > 0.0f) {
                        if (z != 1 && z != 5 && z != 9 && z != 11 && z != 15) {
                            if (z != 3 && z != 8 && z != 4 && z != 6 && z != 13) {
                                if (z == 7 || z == 10 || z == 12 || z == 2 || z == 14) {
                                    mc.p.b("/mods/provoda/textures/light3.png");
                                    this.shoot.render();
                                }
                            } else {
                                mc.p.b("/mods/provoda/textures/light2.png");
                                this.shoot.render();
                            }
                        } else {
                            mc.p.b("/mods/provoda/textures/light1.png");
                            this.shoot.render();
                        }
                        this.hideshot -= 1.0f / (this.animspeed * 2.0f);
                    }
                    this.shootrot = this.rand.nextInt(360);
                } else {
                    this.hideshot = 10.0f;
                }
                GL11.glPopMatrix();
                GL11.glRotatef((float)(-this.currentxrot), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)this.currentyrot, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)this.currentzrot, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)this.currentxtrans, (float)0.0f, (float)(-this.currentztrans));
                GL11.glTranslatef((float)(this.hand5CurRot1 / 27.0f), (float)(-this.hand5CurRot2 / 250.0f), (float)(this.hand5CurRot2 / 300.0f));
                GL11.glRotatef((float)(-this.hand5CurRot2 * 1.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)(-this.hand5CurRot2 / 1.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)(this.hand5CurRot2 / 1.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)(this.sprintCurTransX * 1.5f), (float)(-this.sprintCurTransY / 1.5f), (float)(this.sprintCurTransZ * 0.4f));
                GL11.glRotatef((float)(this.sprintCurRot1 / 2.5f), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)0.0f, (float)(this.hand6CurRot1 / 850.0f), (float)0.0f);
                GL11.glTranslatef((float)(this.modifiCurRot1 / 50.0f), (float)(-this.modifiCurRot1 / 70.0f), (float)0.0f);
                GL11.glBindTexture((int)3553, (int)i1);
                this.hand.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glScaled((double)0.067f, (double)0.06f, (double)0.05f);
                GL11.glTranslatef((float)1.6f, (float)-0.4f, (float)-0.22f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                mc.p.b("/mods/provoda/textures/clock.png");
                this.clock.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                String min = new SimpleDateFormat("mm").format(Calendar.getInstance().getTime());
                this.minrotateangle = 360 * Integer.parseInt(min) / 60;
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
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.098f, (float)0.025f, (float)0.198f);
                GL11.glRotatef((float)-45.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)(-this.hand6CurRot1 / 2.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                this.el.render();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
        }
    }

    private void Check() {
        if (this.currentxrot > this.maxxrot) {
            this.currentxrot = this.maxxrot;
        }
        if (this.currentxrot < 0.0f) {
            this.currentxrot = 0.0f;
        }
        if (this.currentyrot > this.maxyrot) {
            this.currentyrot = this.maxyrot;
        }
        if (this.currentyrot < 0.0f) {
            this.currentyrot = 0.0f;
        }
        if (this.currentzrot > this.maxzrot) {
            this.currentzrot = this.maxzrot;
        }
        if (this.currentzrot < 0.0f) {
            this.currentzrot = 0.0f;
        }
        if (this.currentxtrans > this.maxxtrans) {
            this.currentxtrans = this.maxxtrans;
        }
        if (this.currentxtrans < 0.0f) {
            this.currentxtrans = 0.0f;
        }
        if (this.currentytrans > this.maxytrans) {
            this.currentytrans = this.maxytrans;
        }
        if (this.currentytrans < 0.0f) {
            this.currentytrans = 0.0f;
        }
        if (this.currentztrans > this.maxztrans) {
            this.currentztrans = this.maxztrans;
        }
        if (this.currentztrans < 0.0f) {
            this.currentztrans = 0.0f;
        }
        if (this.scopeCurRot1 > this.scopeMaxRot1) {
            this.scopeCurRot1 = this.scopeMaxRot1;
        }
        if (this.scopeCurRot1 < 0.0f) {
            this.scopeCurRot1 = 0.0f;
        }
        if (this.scopeCurRot2 > this.scopeMaxRot2) {
            this.scopeCurRot2 = this.scopeMaxRot2;
        }
        if (this.scopeCurRot2 < 0.0f) {
            this.scopeCurRot2 = 0.0f;
        }
        if (this.scopeCurTransX > this.scopeMaxTransX) {
            this.scopeCurTransX = this.scopeMaxTransX;
        }
        if (this.scopeCurTransX < 0.0f) {
            this.scopeCurTransX = 0.0f;
        }
        if (this.scopeCurTransY > this.scopeMaxTransY) {
            this.scopeCurTransY = this.scopeMaxTransY;
        }
        if (this.scopeCurTransY < 0.0f) {
            this.scopeCurTransY = 0.0f;
        }
        if (this.scopeCurTransZ > this.scopeMaxTransZ) {
            this.scopeCurTransZ = this.scopeMaxTransZ;
        }
        if (this.scopeCurTransZ < 0.0f) {
            this.scopeCurTransZ = 0.0f;
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
        if (this.handCurTransZ > this.handMaxTransZ) {
            this.handCurTransZ = this.handMaxTransZ;
        }
        if (this.handCurTransZ < 0.0f) {
            this.handCurTransZ = 0.0f;
        }
        if (this.handCurTransY > this.handMaxTransY) {
            this.handCurTransY = this.handMaxTransY;
        }
        if (this.handCurTransY < 0.0f) {
            this.handCurTransY = 0.0f;
        }
        if (this.handCurTransX > this.handMaxTransX) {
            this.handCurTransX = this.handMaxTransX;
        }
        if (this.handCurTransX < 0.0f) {
            this.handCurTransX = 0.0f;
        }
        if (this.handCurRot1 > this.handMaxRot1) {
            this.handCurRot1 = this.handMaxRot1;
        }
        if (this.handCurRot1 < 0.0f) {
            this.handCurRot1 = 0.0f;
        }
        if (this.handCurRot2 > this.handMaxRot1) {
            this.handCurRot2 = this.handMaxRot1;
        }
        if (this.handCurRot2 < 0.0f) {
            this.handCurRot2 = 0.0f;
        }
        if (this.handCurRot3 > this.handMaxRot1) {
            this.handCurRot3 = this.handMaxRot1;
        }
        if (this.handCurRot3 < 0.0f) {
            this.handCurRot3 = 0.0f;
        }
        if (this.reloadCurTransX > this.reloadMaxTransX) {
            this.reloadCurTransX = this.reloadMaxTransX;
        }
        if (this.reloadCurTransX < 0.0f) {
            this.reloadCurTransX = 0.0f;
        }
        if (this.reloadCurTransZ > this.reloadMaxTransZ) {
            this.reloadCurTransZ = this.reloadMaxTransZ;
        }
        if (this.reloadCurTransZ < 0.0f) {
            this.reloadCurTransZ = 0.0f;
        }
        if (this.hand1CurTransY > this.hand1MaxTransY) {
            this.hand1CurTransY = this.hand1MaxTransY;
        }
        if (this.hand1CurTransY < 0.0f) {
            this.hand1CurTransY = 0.0f;
        }
        if (this.hand1CurTransX > this.hand1MaxTransX) {
            this.hand1CurTransX = this.hand1MaxTransX;
        }
        if (this.hand1CurTransX < 0.0f) {
            this.hand1CurTransX = 0.0f;
        }
        if (this.hand1CurTransZ > this.hand1MaxTransZ) {
            this.hand1CurTransZ = this.hand1MaxTransZ;
        }
        if (this.hand1CurTransZ < 0.0f) {
            this.hand1CurTransZ = 0.0f;
        }
        if (this.hand1CurRot1 > this.hand1MaxRot1) {
            this.hand1CurRot1 = this.hand1MaxRot1;
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
        if (this.hand2CurTransY > this.hand2MaxTransY) {
            this.hand2CurTransY = this.hand2MaxTransY;
        }
        if (this.hand2CurTransY < 0.0f) {
            this.hand2CurTransY = 0.0f;
        }
        if (this.hand2CurTransX > this.hand2MaxTransX) {
            this.hand2CurTransX = this.hand2MaxTransX;
        }
        if (this.hand2CurTransX < 0.0f) {
            this.hand2CurTransX = 0.0f;
        }
        if (this.hand2CurTransZ > this.hand2MaxTransZ) {
            this.hand2CurTransZ = this.hand2MaxTransZ;
        }
        if (this.hand2CurTransZ < 0.0f) {
            this.hand2CurTransZ = 0.0f;
        }
        if (this.hand2CurRot1 > this.hand2MaxRot1) {
            this.hand2CurRot1 = this.hand2MaxRot1;
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
        if (this.hand3CurTransY > this.hand3MaxTransY) {
            this.hand3CurTransY = this.hand3MaxTransY;
        }
        if (this.hand3CurTransY < 0.0f) {
            this.hand3CurTransY = 0.0f;
        }
        if (this.hand3CurTransX > this.hand3MaxTransX) {
            this.hand3CurTransX = this.hand3MaxTransX;
        }
        if (this.hand3CurTransX < 0.0f) {
            this.hand3CurTransX = 0.0f;
        }
        if (this.hand3CurTransZ > this.hand3MaxTransZ) {
            this.hand3CurTransZ = this.hand3MaxTransZ;
        }
        if (this.hand3CurTransZ < 0.0f) {
            this.hand3CurTransZ = 0.0f;
        }
        if (this.hand3CurRot1 > this.hand3MaxRot1) {
            this.hand3CurRot1 = this.hand3MaxRot1;
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
        if (this.hand4CurTransY > this.hand4MaxTransY) {
            this.hand4CurTransY = this.hand4MaxTransY;
        }
        if (this.hand4CurTransY < 0.0f) {
            this.hand4CurTransY = 0.0f;
        }
        if (this.hand4CurTransX > this.hand4MaxTransX) {
            this.hand4CurTransX = this.hand4MaxTransX;
        }
        if (this.hand4CurTransX < 0.0f) {
            this.hand4CurTransX = 0.0f;
        }
        if (this.hand4CurTransZ > this.hand4MaxTransZ) {
            this.hand4CurTransZ = this.hand4MaxTransZ;
        }
        if (this.hand4CurTransZ < 0.0f) {
            this.hand4CurTransZ = 0.0f;
        }
        if (this.hand4CurRot1 > this.hand4MaxRot1) {
            this.hand4CurRot1 = this.hand4MaxRot1;
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
        if (this.hand5CurTransY > this.hand5MaxTransY) {
            this.hand5CurTransY = this.hand5MaxTransY;
        }
        if (this.hand5CurTransY < 0.0f) {
            this.hand5CurTransY = 0.0f;
        }
        if (this.hand5CurTransX > this.hand5MaxTransX) {
            this.hand5CurTransX = this.hand5MaxTransX;
        }
        if (this.hand5CurTransX < 0.0f) {
            this.hand5CurTransX = 0.0f;
        }
        if (this.hand5CurTransZ > this.hand5MaxTransZ) {
            this.hand5CurTransZ = this.hand5MaxTransZ;
        }
        if (this.hand5CurTransZ < 0.0f) {
            this.hand5CurTransZ = 0.0f;
        }
        if (this.hand5CurRot1 > this.hand5MaxRot1) {
            this.hand5CurRot1 = this.hand5MaxRot1;
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
        if (this.hand6CurTransY > this.hand6MaxTransY) {
            this.hand6CurTransY = this.hand6MaxTransY;
        }
        if (this.hand6CurTransY < 0.0f) {
            this.hand6CurTransY = 0.0f;
        }
        if (this.hand6CurTransX > this.hand6MaxTransX) {
            this.hand6CurTransX = this.hand6MaxTransX;
        }
        if (this.hand6CurTransX < 0.0f) {
            this.hand6CurTransX = 0.0f;
        }
        if (this.hand6CurTransZ > this.hand6MaxTransZ) {
            this.hand6CurTransZ = this.hand6MaxTransZ;
        }
        if (this.hand6CurTransZ < 0.0f) {
            this.hand6CurTransZ = 0.0f;
        }
        if (this.hand6CurRot1 > this.hand6MaxRot1) {
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
        if (this.SkopePush) {
            if (this.scopeCurRot1 < this.scopeMaxRot1) {
                this.scopeCurRot1 += 1.0f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurRot2 < this.scopeMaxRot2) {
                this.scopeCurRot2 += 1.0f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransX < this.scopeMaxTransX) {
                this.scopeCurTransX += 0.008f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransY < this.scopeMaxTransY) {
                this.scopeCurTransY += 0.008f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransZ < this.scopeMaxTransZ) {
                this.scopeCurTransZ = (float)((double)this.scopeCurTransZ + 0.008 / (double)(this.animspeed * 2.0f));
            }
        } else {
            if (this.scopeCurRot1 <= this.scopeMaxRot1 && this.scopeCurRot1 > 0.0f) {
                this.scopeCurRot1 -= 1.0f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurRot2 <= this.scopeMaxRot2 && this.scopeCurRot2 > 0.0f) {
                this.scopeCurRot2 -= 1.0f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransX <= this.scopeMaxTransX && this.scopeCurTransX > 0.0f) {
                this.scopeCurTransX -= 0.008f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransY <= this.scopeMaxTransY && this.scopeCurTransY > 0.0f) {
                this.scopeCurTransY -= 0.008f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransZ <= this.scopeMaxTransZ && this.scopeCurTransZ > 0.0f) {
                this.scopeCurTransZ -= 0.008f / (this.animspeed * 2.0f);
            }
        }
    }

    private void clock(boolean push) {
        if (push) {
            if (this.currentzrot < this.maxzrot) {
                this.currentzrot += 3.0f / (this.animspeed * 2.0f);
            }
            if (this.currentxrot < this.maxxrot) {
                this.currentxrot += 7.0f / (this.animspeed * 2.0f);
            }
            if (this.currentyrot < this.maxyrot) {
                this.currentyrot += 2.0f / (this.animspeed * 2.0f);
            }
            if (this.currentxtrans < this.maxxtrans) {
                this.currentxtrans += 0.05f / (this.animspeed * 2.0f);
            }
            if (this.currentytrans < this.maxytrans) {
                this.currentytrans += 0.05f / (this.animspeed * 2.0f);
            }
            if (this.currentztrans < this.maxztrans) {
                this.currentztrans += 0.03f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.currentzrot > 0.0f) {
                this.currentzrot -= 3.0f / (this.animspeed * 2.0f);
            }
            if (this.currentyrot > 0.0f) {
                this.currentyrot -= 7.0f / (this.animspeed * 2.0f);
            }
            if (this.currentxrot > 0.0f) {
                this.currentxrot -= 5.0f / (this.animspeed * 2.0f);
            }
            if (this.currentxtrans > 0.0f) {
                this.currentxtrans -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.currentytrans > 0.0f) {
                this.currentytrans -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.currentztrans > 0.0f) {
                this.currentztrans -= 0.03f / (this.animspeed * 2.0f);
            }
        }
    }

    private void sprinting(boolean push) {
        if (push) {
            if (this.sprintCurTransY < this.sprintMaxTransY) {
                this.sprintCurTransY += 0.001f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurTransZ < this.sprintMaxTransZ) {
                this.sprintCurTransZ += 0.005f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurRot1 < this.sprintMaxRot1) {
                this.sprintCurRot1 += 6.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurRot2 < this.sprintMaxRot2) {
                this.sprintCurRot2 += 1.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurTransX < this.sprintMaxTransX) {
                this.sprintCurTransX += 0.005f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.sprintCurTransY > 0.0f) {
                this.sprintCurTransY -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurTransZ > 0.0f) {
                this.sprintCurTransZ -= 0.005f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurRot1 > 0.0f) {
                this.sprintCurRot1 -= 6.0f / (this.animspeed * 3.0f);
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
                this.handCurTransZ += 0.002f;
            }
            if (this.handCurTransY < this.handMaxTransY) {
                this.handCurTransY += 0.006f;
            }
            if (this.handCurTransX < this.handMaxTransX) {
                this.handCurTransX += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot1 < this.handMaxRot1) {
                this.handCurRot1 += 4.0f / (this.animspeed * 3.0f);
            }
            if (this.handCurRot2 < this.handMaxRot2) {
                this.handCurRot2 += 4.0f;
            }
            if (this.handCurRot3 < this.handMaxRot3) {
                this.handCurRot3 += 0.5f;
            }
        } else {
            if (this.handCurTransZ > 0.0f) {
                this.handCurTransZ -= 0.02f;
            }
            if (this.handCurTransY > 0.0f) {
                this.handCurTransY -= 0.02f;
            }
            if (this.handCurTransX > 0.0f) {
                this.handCurTransX -= 0.004f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot1 > 0.0f) {
                this.handCurRot1 -= 4.0f / (this.animspeed * 5.0f);
            }
            if (this.handCurRot2 > 0.0f) {
                this.handCurRot2 -= 4.0f;
            }
            if (this.handCurRot3 > 0.0f) {
                this.handCurRot3 -= 0.5f;
            }
        }
    }

    private void hand1(boolean push) {
        if (push) {
            if (this.isReloading) {
                this.hand1CurTransZ = this.hand1MaxTransZ;
                this.hand1CurTransY = this.hand1MaxTransY;
                this.hand1CurTransX = this.hand1MaxTransX;
            }
            if (this.hand1CurTransZ < this.hand1MaxTransZ) {
                this.hand1CurTransZ += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransY < this.hand1MaxTransY) {
                this.hand1CurTransY += 0.04f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransX < this.hand1MaxTransX) {
                this.hand1CurTransX += 0.04f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurRot1 < this.hand1MaxRot1) {
                this.hand1CurRot1 += 0.03f;
            }
            if (this.hand1CurRot3 < this.hand1MaxRot3) {
                this.hand1CurRot3 += 0.03f;
            }
            if (this.hand1CurRot2 < this.hand1MaxRot2) {
                this.hand1CurRot2 += 0.03f;
            }
        } else {
            if (this.hand1CurTransZ > 0.0f) {
                this.hand1CurTransZ -= 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransY > 0.0f) {
                this.hand1CurTransY -= 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransX > 0.0f) {
                this.hand1CurTransX -= 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurRot1 > 0.0f) {
                this.hand1CurRot1 -= 1.0f;
            }
            if (this.hand1CurRot2 > 0.0f) {
                this.hand1CurRot2 -= 1.0f;
            }
            if (this.hand1CurRot3 > 0.0f) {
                this.hand1CurRot3 -= 1.0f;
            }
        }
    }

    private void hand2(boolean push) {
        if (push) {
            if (this.isReloading) {
                this.hand2CurTransZ = this.hand2MaxTransZ;
                this.hand2CurTransY = this.hand2MaxTransY;
                this.hand2CurTransX = this.hand2MaxTransX;
            }
            if (this.hand2CurTransZ < this.hand2MaxTransZ) {
                this.hand2CurTransZ += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransY < this.hand2MaxTransY) {
                this.hand2CurTransY += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransX < this.hand2MaxTransX) {
                this.hand2CurTransX += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurRot1 < this.hand2MaxRot1) {
                this.hand2CurRot1 += 10.0f;
            }
            if (this.hand2CurRot3 < this.hand2MaxRot3) {
                this.hand2CurRot3 += 0.03f;
            }
            if (this.hand2CurRot2 < this.hand2MaxRot2) {
                this.hand2CurRot2 += 0.03f;
            }
        } else {
            if (this.hand2CurTransZ > 0.0f) {
                this.hand2CurTransZ -= 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransY > 0.0f) {
                this.hand2CurTransY -= 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransX > 0.0f) {
                this.hand2CurTransX -= 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurRot1 > 0.0f) {
                this.hand2CurRot1 -= 5.0f;
            }
            if (this.hand2CurRot2 > 0.0f) {
                this.hand2CurRot2 -= 1.0f;
            }
            if (this.hand2CurRot3 > 0.0f) {
                this.hand2CurRot3 -= 1.0f;
            }
        }
    }

    private void hand3(boolean push) {
        if (push) {
            if (this.isReloading) {
                this.hand3CurTransZ = this.hand3MaxTransZ;
                this.hand3CurTransY = this.hand3MaxTransY;
                this.hand3CurTransX = this.hand3MaxTransX;
            }
            if (this.hand3CurTransZ < this.hand3MaxTransZ) {
                this.hand3CurTransZ += 0.005f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransY < this.hand3MaxTransY) {
                this.hand3CurTransY += 0.003f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransX < this.hand3MaxTransX) {
                this.hand3CurTransX += 0.006f;
            }
            if (this.hand3CurRot1 < this.hand3MaxRot1) {
                this.hand3CurRot1 += 9.0f;
            }
            if (this.hand3CurRot3 < this.hand3MaxRot3) {
                this.hand3CurRot3 += 2.0f;
            }
            if (this.hand3CurRot2 < this.hand3MaxRot2) {
                this.hand3CurRot2 += 2.0f;
            }
        } else {
            if (this.hand3CurTransZ > 0.0f) {
                this.hand3CurTransZ -= 0.005f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransY > 0.0f) {
                this.hand3CurTransY -= 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransX > 0.0f) {
                this.hand3CurTransX -= 0.12f;
            }
            if (this.hand3CurRot1 > 0.0f) {
                this.hand3CurRot1 -= 9.0f;
            }
            if (this.hand3CurRot2 > 0.0f) {
                this.hand3CurRot2 -= 2.0f;
            }
            if (this.hand3CurRot3 > 0.0f) {
                this.hand3CurRot3 -= 2.0f;
            }
        }
    }

    private void hand4(boolean push) {
        if (push) {
            if (this.isReloading) {
                this.hand4CurTransZ = this.hand4MaxTransZ;
                this.hand4CurTransY = this.hand4MaxTransY;
                this.hand4CurTransX = this.hand4MaxTransX;
            }
            if (this.hand4CurTransZ < this.hand4MaxTransZ) {
                this.hand4CurTransZ += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransY < this.hand4MaxTransY) {
                this.hand4CurTransY += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransX < this.hand4MaxTransX) {
                this.hand4CurTransX += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot1 < this.hand4MaxRot1) {
                this.hand4CurRot1 += 0.03f;
            }
            if (this.hand4CurRot3 < this.hand4MaxRot3) {
                this.hand4CurRot3 += 0.03f;
            }
            if (this.hand4CurRot2 < this.hand4MaxRot2) {
                this.hand4CurRot2 += 0.03f;
            }
        } else {
            if (this.hand4CurTransZ > 0.0f) {
                this.hand4CurTransZ -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransY > 0.0f) {
                this.hand4CurTransY -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransX > 0.0f) {
                this.hand4CurTransX -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot1 > 0.0f) {
                this.hand4CurRot1 -= 0.01f;
            }
            if (this.hand4CurRot2 > 0.0f) {
                this.hand4CurRot2 -= 0.01f;
            }
            if (this.hand4CurRot3 > 0.0f) {
                this.hand4CurRot3 -= 0.01f;
            }
        }
    }

    private void hand5(boolean push) {
        if (push) {
            if (this.hand5CurTransZ < this.hand5MaxTransZ) {
                this.hand5CurTransZ += 0.06f;
            }
            if (this.hand5CurTransY < this.hand5MaxTransY) {
                this.hand5CurTransY += 0.06f;
            }
            if (this.hand5CurTransX < this.hand5MaxTransX) {
                this.hand5CurTransX += 0.06f;
            }
            if (this.hand5CurRot1 < this.hand5MaxRot1) {
                this.hand5CurRot1 += 2.0f / (this.animspeed * 5.0f);
            }
            if (this.hand5CurRot3 < this.hand5MaxRot3) {
                this.hand5CurRot3 += 2.0f / (this.animspeed * 5.0f);
            }
            if (this.hand5CurRot2 < this.hand5MaxRot2) {
                this.hand5CurRot2 += 2.0f / (this.animspeed * 5.0f);
            }
        } else {
            if (this.hand5CurTransZ > 0.0f) {
                this.hand5CurTransZ -= 0.05f;
            }
            if (this.hand5CurTransY > 0.0f) {
                this.hand5CurTransY -= 0.05f;
            }
            if (this.hand5CurTransX > 0.0f) {
                this.hand5CurTransX -= 0.05f;
            }
            if (this.hand5CurRot1 > 0.0f) {
                this.hand5CurRot1 -= 2.0f / (this.animspeed * 5.0f);
            }
            if (this.hand5CurRot2 > 0.0f) {
                this.hand5CurRot2 -= 2.0f / (this.animspeed * 5.0f);
            }
            if (this.hand5CurRot3 > 0.0f) {
                this.hand5CurRot3 -= 2.0f / (this.animspeed * 5.0f);
            }
        }
    }

    private void hand6(boolean push) {
        if (push) {
            if (this.hand6CurTransZ < this.hand6MaxTransZ) {
                this.hand6CurTransZ += 0.13f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransY < this.hand6MaxTransY) {
                this.hand6CurTransY += 0.34f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransX < this.hand6MaxTransX) {
                this.hand6CurTransX += 0.26f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurRot1 < this.hand6MaxRot1) {
                this.hand6CurRot1 += 3.5f / (this.animspeed * 2.2f);
            }
            if (this.hand6CurRot3 < this.hand6MaxRot3) {
                this.hand6CurRot3 += 0.03f;
            }
            if (this.hand6CurRot2 < this.hand6MaxRot2) {
                this.hand6CurRot2 += 0.03f;
            }
        } else {
            if (this.hand6CurTransZ > 0.0f) {
                this.hand6CurTransZ -= 0.23f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransY > 0.0f) {
                this.hand6CurTransY -= 0.1f;
            }
            if (this.hand6CurTransX > 0.0f) {
                this.hand6CurTransX -= 0.1f;
            }
            if (this.hand6CurRot1 > 0.0f) {
                this.hand6CurRot1 -= 4.5f / (this.animspeed * 2.2f);
            }
            if (this.hand6CurRot2 > 0.0f) {
                this.hand6CurRot2 -= 0.01f;
            }
            if (this.hand6CurRot3 > 0.0f) {
                this.hand6CurRot3 -= 0.01f;
            }
        }
    }

    private void hand7(boolean push) {
        if (push) {
            if (this.hand7CurTransZ < this.hand7MaxTransZ) {
                this.hand7CurTransZ += 0.07f / (this.animspeed * 5.0f);
            }
            if (this.hand7CurTransY < this.hand7MaxTransY) {
                this.hand7CurTransY += 0.05f;
            }
            if (this.hand7CurTransX < this.hand7MaxTransX) {
                this.hand7CurTransX += 0.05f;
            }
            if (this.hand7CurRot1 < this.hand7MaxRot1) {
                this.hand7CurRot1 += 8.0f / (this.animspeed * 3.0f);
            }
            if (this.hand7CurRot3 < this.hand7MaxRot3) {
                this.hand7CurRot3 += 0.03f;
            }
            if (this.hand7CurRot2 < this.hand7MaxRot2) {
                this.hand7CurRot2 += 0.03f;
            }
        } else {
            if (this.hand7CurTransZ > 0.0f) {
                this.hand7CurTransZ -= 0.07f / (this.animspeed * 5.0f);
            }
            if (this.hand7CurTransY > 0.0f) {
                this.hand7CurTransY -= 0.05f;
            }
            if (this.hand7CurTransX > 0.0f) {
                this.hand7CurTransX -= 0.05f;
            }
            if (this.hand7CurRot1 > 0.0f && this.isReloading) {
                this.hand7CurRot1 -= 10.0f / (this.animspeed * 3.0f);
            }
            if (this.hand7CurRot2 > 0.0f) {
                this.hand7CurRot2 -= 0.01f;
            }
            if (this.hand7CurRot3 > 0.0f) {
                this.hand7CurRot3 -= 0.01f;
            }
        }
    }

    private void hand8(boolean push) {
        if (push) {
            if (this.hand8CurTransZ < this.hand8MaxTransZ) {
                this.hand8CurTransZ += 0.05f;
            }
            if (this.hand8CurTransY < this.hand8MaxTransY) {
                this.hand8CurTransY += 0.05f;
            }
            if (this.hand8CurTransX < this.hand8MaxTransX) {
                this.hand8CurTransX += 0.05f;
            }
            if (this.hand8CurRot1 < this.hand8MaxRot1) {
                this.hand8CurRot1 += 0.03f;
            }
            if (this.hand8CurRot3 < this.hand8MaxRot3) {
                this.hand8CurRot3 += 0.03f;
            }
            if (this.hand8CurRot2 < this.hand8MaxRot2) {
                this.hand8CurRot2 += 0.03f;
            }
        } else {
            if (this.hand8CurTransZ > 0.0f) {
                this.hand8CurTransZ -= 0.06f;
            }
            if (this.hand8CurTransY > 0.0f) {
                this.hand8CurTransY -= 0.06f;
            }
            if (this.hand8CurTransX > 0.0f) {
                this.hand8CurTransX -= 0.06f;
            }
            if (this.hand8CurRot1 > 0.0f) {
                this.hand8CurRot1 -= 0.01f;
            }
            if (this.hand8CurRot2 > 0.0f) {
                this.hand8CurRot2 -= 0.01f;
            }
            if (this.hand8CurRot3 > 0.0f) {
                this.hand8CurRot3 -= 0.01f;
            }
        }
    }

    private void hand9(boolean push) {
        if (push) {
            if (this.hand9CurTransZ < this.hand9MaxTransZ) {
                this.hand9CurTransZ += 0.05f;
            }
            if (this.hand9CurTransY < this.hand9MaxTransY) {
                this.hand9CurTransY += 0.05f;
            }
            if (this.hand9CurTransX < this.hand9MaxTransX) {
                this.hand9CurTransX += 0.05f;
            }
            if (this.hand9CurRot1 < this.hand9MaxRot1) {
                this.hand9CurRot1 += 0.03f;
            }
            if (this.hand9CurRot3 < this.hand9MaxRot3) {
                this.hand9CurRot3 += 0.03f;
            }
            if (this.hand9CurRot2 < this.hand9MaxRot2) {
                this.hand9CurRot2 += 0.03f;
            }
        } else {
            if (this.hand9CurTransZ > 0.0f) {
                this.hand9CurTransZ -= 0.06f;
            }
            if (this.hand9CurTransY > 0.0f) {
                this.hand9CurTransY -= 0.06f;
            }
            if (this.hand9CurTransX > 0.0f) {
                this.hand9CurTransX -= 0.06f;
            }
            if (this.hand9CurRot1 > 0.0f) {
                this.hand9CurRot1 -= 0.01f;
            }
            if (this.hand9CurRot2 > 0.0f) {
                this.hand9CurRot2 -= 0.01f;
            }
            if (this.hand9CurRot3 > 0.0f) {
                this.hand9CurRot3 -= 0.01f;
            }
        }
    }

    private void partCompliteCheck() {
        this.Complete1 = this.handCurTransZ == this.handMaxTransZ && this.handCurTransY == this.handMaxTransY && this.handCurTransX == this.handMaxTransX && this.handCurRot1 == this.handMaxRot1 && this.handCurRot2 == this.handMaxRot2;
        this.Complete2 = this.hand1CurTransZ == this.hand1MaxTransZ && this.hand1CurTransX == this.hand1MaxTransX && this.hand1CurTransY == this.hand1MaxTransY && this.hand1CurRot1 == this.hand1MaxRot1;
        this.Complete3 = this.hand2CurTransZ == this.hand2MaxTransZ && this.hand2CurTransX == this.hand2MaxTransX && this.hand2CurTransY == this.hand2MaxTransY && this.hand2CurRot1 == this.hand2MaxRot1;
        this.Complete4 = this.hand3CurTransZ == this.hand3MaxTransZ && this.hand3CurTransX == this.hand3MaxTransX && this.hand3CurTransY == this.hand3MaxTransY && this.hand3CurRot1 == this.hand3MaxRot1;
        this.Complete5 = this.hand4CurTransZ == this.hand4MaxTransZ && this.hand4CurTransX == this.hand4MaxTransX && this.hand4CurTransY == this.hand4MaxTransY;
        this.Complete6 = this.hand5CurTransZ == this.hand5MaxTransZ && this.hand5CurTransX == this.hand5MaxTransX && this.hand5CurTransY == this.hand5MaxTransY && this.hand5CurRot1 == this.hand5MaxRot1 && this.hand5CurRot2 == this.hand5MaxRot2;
        this.Complete7 = this.hand6CurTransZ == this.hand6MaxTransZ && this.hand6CurTransX == this.hand6MaxTransX && this.hand6CurTransY == this.hand6MaxTransY && this.hand6CurRot1 == this.hand6MaxRot1;
        this.Complete8 = this.hand7CurTransZ == this.hand7MaxTransZ && this.hand7CurTransX == this.hand7MaxTransX && this.hand7CurTransY == this.hand7MaxTransY && this.hand7CurRot1 == this.hand7MaxRot1;
        this.Complete9 = this.hand8CurTransZ == this.hand8MaxTransZ && this.hand8CurTransX == this.hand8MaxTransX && this.hand8CurTransY == this.hand8MaxTransY;
        this.Complete10 = this.hand9CurTransZ == this.hand9MaxTransZ && this.hand9CurTransX == this.hand9MaxTransX && this.hand9CurTransY == this.hand9MaxTransY;
    }
}
