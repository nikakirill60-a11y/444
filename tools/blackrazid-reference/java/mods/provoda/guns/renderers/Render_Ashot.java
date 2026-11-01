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
 *  mods.provoda.guns.renderers.Render_Ashot$NamelessClass385194952
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
import mods.provoda.guns.Ashot;
import mods.provoda.guns.models.clock.Clock;
import mods.provoda.guns.models.clock.HourArrowModel;
import mods.provoda.guns.models.clock.MatrixDebugModel;
import mods.provoda.guns.models.clock.MinArrowModel;
import mods.provoda.guns.models.clock.O2ArrowModel;
import mods.provoda.guns.models.clock.SekArrowModel;
import mods.provoda.guns.renderers.Render_Ashot;
import mods.provoda.models.Laser;
import mods.provoda.models.Shoot;
import mods.provoda.until.ClockKeyBind;
import mods.provoda.until.Hand;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Render_Ashot
implements IItemRenderer {
    Hand hand = new Hand();
    GLModel bullet = new GLModel("/mods/provoda/guns/models/ashot/bullet.obj");
    GLModel model = new GLModel("/mods/provoda/guns/models/ashot/ashot.obj");
    GLModel model1 = new GLModel("/mods/provoda/guns/models/ashot/ashot1.obj");
    GLModel butt = new GLModel("/mods/provoda/guns/models/ashot/butt.obj");
    GLModel butt1 = new GLModel("/mods/provoda/guns/models/ashot/butt1.obj");
    GLModel privot = new GLModel("/mods/provoda/guns/models/ashot/privot.obj");
    GLModel glushak = new GLModel("/mods/provoda/guns/models/saiga/glushak.obj");
    GLModel barrel = new GLModel("/mods/provoda/guns/models/uboinik/glushak.obj");
    GLModel kolimator = new GLModel("/mods/provoda/guns/models/ashot/kolimator.obj");
    GLModel opticasmall = new GLModel("/mods/provoda/guns/models/rpk/optica.obj");
    GLModel laser = new GLModel("/mods/provoda/guns/models/rpk/laser.obj");
    GLModel zatvor = new GLModel("/mods/provoda/guns/models/ashot/zatvor.obj");
    Clock clock = new Clock();
    Shoot shoot = new Shoot();
    Laser laserline = new Laser();
    Random rand = new Random();
    O2ArrowModel O2ArrouModel = new O2ArrowModel();
    MinArrowModel MinArrouModel = new MinArrowModel();
    HourArrowModel HourArrowModel = new HourArrowModel();
    SekArrowModel SekArrowModel = new SekArrowModel();
    MatrixDebugModel debugModel = new MatrixDebugModel();
    private float zatvorCurTrans;
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
    private float shootrot;
    private float hideshot;
    public float otdachaCurTrans;
    public float shoot1;
    private float o2rotateangle;
    private float minrotateangle;
    private float hourrotateangle;
    private float sekrotateangle;
    private float scopeCurRot1;
    private float scopeCurRot2;
    private float scopeCurTransX;
    private float scopeCurTransY;
    private float scopeCurTransZ;
    private float scopeMaxRot1 = 1.8f;
    private float scopeMaxRot2 = 2.0f;
    private float scopeMaxTransX = 0.167f;
    private float scopeMaxTransY = 0.058f;
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
    private float maxxtrans = 0.19f;
    private float maxytrans = 0.1f;
    private float maxztrans = 0.39f;
    private float reloadCurTransX;
    private float reloadCurTransZ;
    private float reloadMaxTransX = 0.05f;
    private float reloadMaxTransZ = 0.2f;
    private float handCurTransX;
    private float handCurTransZ;
    private float handCurTransY;
    private float handMaxTransX = 0.0f;
    private float handMaxTransY = 0.05f;
    private float handMaxTransZ = 0.0f;
    private float handCurRot1;
    private float handCurRot2;
    private float handCurRot3;
    private float handMaxRot1 = 20.0f;
    private float handMaxRot2;
    private float handMaxRot3;
    private float hand1CurTransX;
    private float hand1CurTransY;
    private float hand1CurTransZ;
    private float hand1MaxTransX = 0.1f;
    private float hand1MaxTransY = 0.0f;
    private float hand1MaxTransZ = 0.0f;
    private float hand1CurRot1;
    private float hand1CurRot2;
    private float hand1CurRot3;
    private float hand1MaxRot1;
    private float hand1MaxRot2;
    private float hand1MaxRot3;
    private float hand2CurTransX;
    private float hand2CurTransY;
    private float hand2CurTransZ;
    private float hand2MaxTransX = 0.01f;
    private float hand2MaxTransY = 0.0f;
    private float hand2MaxTransZ = 0.0f;
    private float hand2CurRot1;
    private float hand2CurRot2;
    private float hand2CurRot3;
    private float hand2MaxRot1 = 30.0f;
    private float hand2MaxRot2;
    private float hand2MaxRot3;
    private float hand3CurTransX;
    private float hand3CurTransY;
    private float hand3CurTransZ;
    private float hand3MaxTransX = 0.0f;
    private float hand3MaxTransY = 0.0f;
    private float hand3MaxTransZ = 0.05f;
    private float hand3CurRot1;
    private float hand3CurRot2;
    private float hand3CurRot3;
    private float hand3MaxRot1;
    private float hand3MaxRot2;
    private float hand3MaxRot3;
    private float hand4CurTransX;
    private float hand4CurTransY;
    private float hand4CurTransZ;
    private float hand4MaxTransX = 0.04f;
    private float hand4MaxTransY = 0.06f;
    private float hand4MaxTransZ = 0.06f;
    private float hand4CurRot1;
    private float hand4CurRot2;
    private float hand4CurRot3;
    private float hand4MaxRot1;
    private float hand4MaxRot2;
    private float hand4MaxRot3;
    private float hand5CurTransX;
    private float hand5CurTransY;
    private float hand5CurTransZ;
    private float hand5MaxTransX = 0.2f;
    private float hand5MaxTransY = 0.4f;
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
    private float hand6MaxTransX = 0.3f;
    private float hand6MaxTransY = 0.38f;
    private float hand6MaxTransZ = 0.3f;
    private float hand6CurRot1;
    private float hand6CurRot2;
    private float hand6CurRot3;
    private float hand6MaxRot1 = 20.0f;
    private float hand6MaxRot2;
    private float hand6MaxRot3;
    private float hand7CurTransX;
    private float hand7CurTransY;
    private float hand7CurTransZ;
    private float hand7MaxTransX = 0.22f;
    private float hand7MaxTransY = 0.45f;
    private float hand7MaxTransZ = 0.17f;
    private float hand7CurRot1;
    private float hand7CurRot2;
    private float hand7CurRot3;
    private float hand7MaxRot1;
    private float hand7MaxRot2;
    private float hand7MaxRot3;
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
        switch (NamelessClass385194952.$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRenderType[type.ordinal()]) {
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
        int shootTimer = Ashot.otdachaTimer;
        boolean shooting = item.q().n("Shoot");
        switch (NamelessClass385194952.$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRenderType[type.ordinal()]) {
            case 1: {
                GL11.glPushMatrix();
                GL11.glScaled((double)2.5, (double)2.5, (double)2.5);
                GL11.glTranslatef((float)0.22f, (float)-0.07f, (float)-0.13f);
                GL11.glRotatef((float)15.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)12.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glShadeModel((int)7425);
                this.model.render();
                this.model1.render();
                this.zatvor.render();
                GL11.glPushMatrix();
                GL11.glEnable((int)2884);
                GL11.glScaled((double)0.3f, (double)0.3f, (double)0.3f);
                GL11.glTranslatef((float)0.0f, (float)0.25f, (float)0.95f);
                GL11.glRotatef((float)this.shootrot, (float)0.0f, (float)0.0f, (float)1.0f);
                if (this.shoot1 > 0.0f) {
                    this.shoot1 -= 1.0f;
                }
                if (z >= 0 && shooting) {
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
                if (revinv.a(3) != null) {
                    if (revinv.a(3).b() == ModBase.AshotButt) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.001f, (float)0.001f);
                        this.butt1.render();
                        GL11.glPopMatrix();
                    } else {
                        this.butt.render();
                    }
                } else {
                    this.butt.render();
                }
                if (revinv.a(0) != null) {
                    if (revinv.a(0).b() == ModBase.Kolimator) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0025f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)-0.0027f, (float)0.126f, (float)0.058f);
                        this.kolimator.render();
                        GL11.glEnable((int)2884);
                        GL11.glScalef((float)0.046f, (float)0.05f, (float)0.05f);
                        GL11.glTranslatef((float)0.005f, (float)-0.175f, (float)-1.27f);
                        GL11.glRotatef((float)15.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glRotatef((float)200.3f, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glDisable((int)2884);
                        GL11.glPopMatrix();
                    } else if (revinv.a(0).b() == ModBase.OpticaSmall) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0025f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)0.0f, (float)0.05f, (float)0.135f);
                        GL11.glEnable((int)2884);
                        GL11.glTranslatef((float)0.0f, (float)0.085f, (float)-0.085f);
                        this.opticasmall.render();
                        GL11.glDisable((int)2884);
                        GL11.glPopMatrix();
                    }
                }
                if (revinv.a(3) != null && revinv.a(3).b() == ModBase.AshotButt) {
                    this.privot.render();
                }
                if (revinv.a(2) != null) {
                    if (revinv.a(2).b() == ModBase.Glushak) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.074f, (float)-0.23f);
                        this.glushak.render();
                        GL11.glPopMatrix();
                    } else if (revinv.a(2).b() == ModBase.UboinikBarrel) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.075f, (float)0.26f);
                        this.barrel.render();
                        GL11.glPopMatrix();
                    }
                }
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.Laser) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.07f, (float)-0.07f);
                    this.laser.render();
                    GL11.glPushMatrix();
                    GL11.glScaled((double)0.007f, (double)0.007f, (double)0.02f);
                    GL11.glTranslatef((float)2.0f, (float)0.2f, (float)8.0f);
                    GL11.glRotatef((float)-3.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)104.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glTranslatef((float)0.0f, (float)-0.0f, (float)3.3f);
                    GL11.glRotatef((float)-16.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)-0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glScaled((double)200.0, (double)1.0, (double)1.0);
                    GL11.glTranslatef((float)-0.36f, (float)0.0f, (float)0.0f);
                    mc.p.b("/mods/provoda/textures/laser.png");
                    this.laserline.render();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
                break;
            }
            case 2: {
                GL11.glPushMatrix();
                GL11.glScaled((double)2.5, (double)2.5, (double)2.5);
                GL11.glTranslatef((float)0.0f, (float)-0.03f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.model.render();
                this.model1.render();
                this.zatvor.render();
                if (revinv.a(3) != null) {
                    if (revinv.a(3).b() == ModBase.AshotButt) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.001f, (float)0.001f);
                        this.butt1.render();
                        GL11.glPopMatrix();
                    } else {
                        this.butt.render();
                    }
                } else {
                    this.butt.render();
                }
                if (revinv.a(0) != null) {
                    if (revinv.a(0).b() == ModBase.Kolimator) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0025f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)-0.0027f, (float)0.126f, (float)0.058f);
                        this.kolimator.render();
                        GL11.glEnable((int)2884);
                        GL11.glScalef((float)0.046f, (float)0.05f, (float)0.05f);
                        GL11.glTranslatef((float)0.005f, (float)-0.175f, (float)-1.27f);
                        GL11.glRotatef((float)15.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glRotatef((float)200.3f, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glDisable((int)2884);
                        GL11.glPopMatrix();
                    } else if (revinv.a(0).b() == ModBase.OpticaSmall) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0025f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)0.0f, (float)0.05f, (float)0.135f);
                        GL11.glEnable((int)2884);
                        GL11.glTranslatef((float)0.0f, (float)0.085f, (float)-0.085f);
                        this.opticasmall.render();
                        GL11.glDisable((int)2884);
                        GL11.glPopMatrix();
                    }
                }
                if (revinv.a(3) != null && revinv.a(3).b() == ModBase.AshotButt) {
                    this.privot.render();
                }
                if (revinv.a(2) != null) {
                    if (revinv.a(2).b() == ModBase.Glushak) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.074f, (float)-0.23f);
                        this.glushak.render();
                        GL11.glPopMatrix();
                    } else if (revinv.a(2).b() == ModBase.UboinikBarrel) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.075f, (float)0.26f);
                        this.barrel.render();
                        GL11.glPopMatrix();
                    }
                }
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.Laser) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.07f, (float)-0.07f);
                    this.laser.render();
                    GL11.glPushMatrix();
                    GL11.glScaled((double)0.007f, (double)0.007f, (double)0.02f);
                    GL11.glTranslatef((float)2.0f, (float)0.2f, (float)8.0f);
                    GL11.glRotatef((float)-3.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)104.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glTranslatef((float)0.0f, (float)-0.0f, (float)3.3f);
                    GL11.glRotatef((float)-16.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)-0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glScaled((double)200.0, (double)1.0, (double)1.0);
                    GL11.glTranslatef((float)-0.36f, (float)0.0f, (float)0.0f);
                    mc.p.b("/mods/provoda/textures/laser.png");
                    this.laserline.render();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
                break;
            }
            case 3: {
                this.partCompliteCheck();
                this.isReloading = item.q().e("LoadingTimer") > 0;
                this.isUnloading = item.q().e("UnloadingTimer") > 0;
                this.SkopePush = Mouse.isButtonDown((int)1) && mc.s == null && !item.q().n("Modifi") ? !mc.g.ah() : false;
                if (!(!ClockKeyBind.keyPressed || this.SkopePush || this.isUnloading || this.isReloading || mc.g.ah() || item.q().n("Modifi"))) {
                    this.clock(true);
                } else {
                    this.clock(false);
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
                if (!this.isUnloading) {
                    if (this.isReloading) {
                        this.hand7(false);
                        this.hand(true);
                        if (this.Complete1) {
                            this.hand1(true);
                        }
                        if (this.Complete2) {
                            this.hand2(true);
                        }
                        if (this.Complete3) {
                            if (!this.Complete6) {
                                this.hand3(true);
                            } else {
                                this.hand3(false);
                                this.hand4(false);
                            }
                        }
                        if (this.Complete4) {
                            this.hand4(true);
                        }
                        if (this.Complete5) {
                            this.hand5(true);
                        }
                        if (this.Complete6) {
                            this.hand6(true);
                        }
                    } else {
                        this.hand1(false);
                        this.hand(false);
                        if (this.hand1CurTransX == 0.0f && this.hand1CurTransY == 0.0f && this.hand1CurTransZ == 0.0f && this.hand1CurRot1 == 0.0f && this.hand1CurRot2 == 0.0f) {
                            this.hand2(false);
                        }
                        this.hand3(false);
                        this.hand4(false);
                        this.hand5(false);
                        this.hand6(false);
                        if (revinv.a(3) != null && !this.isReloading) {
                            if (revinv.a(3).b() == ModBase.AshotButt && !this.isReloading) {
                                this.hand7(true);
                            } else {
                                this.hand7(false);
                            }
                        } else {
                            this.hand7(false);
                        }
                    }
                }
                GL11.glPushMatrix();
                GL11.glPushMatrix();
                GL11.glShadeModel((int)7425);
                GL11.glScaled((double)10.0, (double)10.0, (double)10.0);
                GL11.glTranslatef((float)0.3f, (float)0.05f, (float)0.1f);
                GL11.glRotatef((float)17.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)89.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)10.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)-38.0f, (float)1.0f, (float)0.0f, (float)1.0f);
                if (revinv.a(0) != null) {
                    if (revinv.a(0).b() == ModBase.Kolimator) {
                        if (this.otdachaCurTrans <= 0.0f && shootTimer == 2 && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.03f;
                        } else if (this.otdachaCurTrans > 0.0f && shootTimer == 2 && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.03f;
                        }
                    } else if (revinv.a(0).b() == ModBase.OpticaSmall) {
                        if (this.otdachaCurTrans <= 0.0f && shootTimer == 2 && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.02f;
                        } else if (this.otdachaCurTrans > 0.0f && shootTimer == 2 && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                            this.otdachaCurTrans = 0.02f;
                        }
                    }
                } else if (this.otdachaCurTrans <= 0.0f && shootTimer == 2 && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                    this.otdachaCurTrans = 0.08f;
                } else if (this.otdachaCurTrans > 0.0f && shootTimer == 2 && !this.isReloading && !this.isUnloading && z > 0 && !mc.g.ah()) {
                    this.otdachaCurTrans = 0.08f;
                }
                if (this.otdachaCurTrans > 0.0f) {
                    this.otdachaCurTrans -= 0.005f / (this.animspeed * 2.0f);
                }
                if (revinv.a(0) != null) {
                    if (revinv.a(0).b() == ModBase.Optica) {
                        mc.g.n(mk.r.H);
                        GL11.glTranslatef((float)(this.scopeCurTransX / 1.0917f), (float)(this.scopeCurTransY / 1.75f), (float)(-this.scopeCurTransZ * 2.45f));
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
                        GL11.glTranslatef((float)(this.scopeCurTransX * 1.018f), (float)(this.scopeCurTransY / 2.08f), (float)(-this.scopeCurTransZ * 1.5f));
                        GL11.glRotatef((float)(this.scopeCurRot1 / 1.1f), (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glRotatef((float)(this.scopeCurRot2 * 1.4f), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(this.scopeCurRot2 / 3.0f), (float)1.0f, (float)0.0f, (float)0.0f);
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
                        GL11.glTranslatef((float)(this.scopeCurTransX / 1.0365f), (float)(this.scopeCurTransY / 1.889f), (float)(-this.scopeCurTransZ * 2.75f));
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
                        GL11.glTranslatef((float)(this.scopeCurTransX * 1.01f), (float)(this.scopeCurTransY / 1.07f), (float)(-this.scopeCurTransZ));
                        GL11.glRotatef((float)(this.scopeCurRot1 / 1.1f), (float)0.0f, (float)1.0f, (float)0.0f);
                        GL11.glRotatef((float)this.scopeCurRot2, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glRotatef((float)(this.scopeCurRot2 / 3.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                    }
                } else {
                    mc.g.n(mk.r.H);
                    GL11.glTranslatef((float)(this.scopeCurTransX * 1.01f), (float)(this.scopeCurTransY / 1.07f), (float)(-this.scopeCurTransZ));
                    GL11.glRotatef((float)(this.scopeCurRot1 / 1.1f), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)this.scopeCurRot2, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)(this.scopeCurRot2 / 3.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                }
                GL11.glTranslatef((float)(this.modifiCurRot1 / 500.0f), (float)(this.modifiCurRot1 / 1500.0f), (float)this.modifiCurTransZ);
                GL11.glRotatef((float)(this.modifiCurRot1 * 2.5f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)(-this.modifiCurRot2), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)(-this.otdachaCurTrans));
                GL11.glTranslatef((float)this.sprintCurTransX, (float)(-this.sprintCurTransY / 1.5f), (float)(-this.sprintCurTransZ * 0.5f));
                GL11.glRotatef((float)(this.sprintCurRot1 / 6.5f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)(this.sprintCurRot2 * 1.5f), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)0.0f, (float)this.handCurTransY, (float)0.0f);
                GL11.glRotatef((float)(-this.handCurRot1), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)this.hand1CurTransX, (float)0.0f, (float)0.0f);
                this.model.render();
                if (this.zatvorCurTrans <= 0.0f && shootTimer == 2 && !this.isReloading && !this.isUnloading && !mc.g.ah()) {
                    this.zatvorCurTrans = 0.08f;
                } else if (this.zatvorCurTrans > 0.0f && shootTimer == 2 && !this.isReloading && !this.isUnloading && !mc.g.ah()) {
                    this.zatvorCurTrans = 0.08f;
                }
                if (this.zatvorCurTrans > 0.0f) {
                    this.zatvorCurTrans -= 0.005f / (this.animspeed * 2.0f);
                }
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.0f, (float)0.042f, (float)-0.061f);
                GL11.glRotatef((float)(this.zatvorCurTrans * 600.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                this.zatvor.render();
                GL11.glPopMatrix();
                if (revinv.a(3) != null) {
                    if (revinv.a(3).b() == ModBase.AshotButt) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.001f, (float)0.001f);
                        this.butt1.render();
                        GL11.glPopMatrix();
                    } else {
                        this.butt.render();
                    }
                } else {
                    this.butt.render();
                }
                if (revinv.a(0) != null) {
                    if (revinv.a(0).b() == ModBase.Kolimator) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0025f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)-0.0027f, (float)0.126f, (float)0.058f);
                        this.kolimator.render();
                        GL11.glEnable((int)2884);
                        GL11.glScalef((float)0.046f, (float)0.05f, (float)0.05f);
                        GL11.glTranslatef((float)0.005f, (float)-0.175f, (float)-1.27f);
                        GL11.glRotatef((float)15.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                        GL11.glRotatef((float)200.3f, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glDisable((int)2884);
                        GL11.glPopMatrix();
                    } else if (revinv.a(0).b() == ModBase.OpticaSmall) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0025f, (float)0.0f, (float)0.0f);
                        GL11.glTranslatef((float)0.0f, (float)0.05f, (float)0.135f);
                        GL11.glEnable((int)2884);
                        GL11.glTranslatef((float)0.0f, (float)0.085f, (float)-0.085f);
                        this.opticasmall.render();
                        GL11.glDisable((int)2884);
                        GL11.glPopMatrix();
                    }
                }
                GL11.glPushMatrix();
                GL11.glTranslatef((float)-0.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)(-this.hand2CurRot1), (float)0.0f, (float)0.0f, (float)1.0f);
                this.model1.render();
                if (revinv.a(3) != null && revinv.a(3).b() == ModBase.AshotButt) {
                    this.privot.render();
                }
                if (revinv.a(2) != null) {
                    if (revinv.a(2).b() == ModBase.Glushak) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.074f, (float)-0.23f);
                        this.glushak.render();
                        GL11.glPopMatrix();
                    } else if (revinv.a(2).b() == ModBase.UboinikBarrel) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)0.0f, (float)0.075f, (float)0.26f);
                        this.barrel.render();
                        GL11.glPopMatrix();
                    }
                }
                if (revinv.a(1) != null && revinv.a(1).b() == ModBase.Laser) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)0.0f, (float)0.07f, (float)-0.07f);
                    this.laser.render();
                    GL11.glPushMatrix();
                    GL11.glScaled((double)0.007f, (double)0.007f, (double)0.02f);
                    GL11.glTranslatef((float)2.0f, (float)0.2f, (float)8.0f);
                    GL11.glRotatef((float)-3.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glRotatef((float)104.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    if (this.SkopePush) {
                        if (revinv.a(0) != null) {
                            if (revinv.a(0).b() == ModBase.OpticaSmall) {
                                GL11.glTranslatef((float)0.0f, (float)-2.0f, (float)3.0f);
                                GL11.glRotatef((float)-16.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                                GL11.glRotatef((float)-13.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                            } else {
                                GL11.glTranslatef((float)0.0f, (float)-1.0f, (float)4.0f);
                                GL11.glRotatef((float)-16.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                                GL11.glRotatef((float)-1.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                            }
                        } else {
                            GL11.glTranslatef((float)0.0f, (float)-1.0f, (float)4.0f);
                            GL11.glRotatef((float)-16.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                            GL11.glRotatef((float)-1.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                        }
                    }
                    GL11.glScaled((double)200.0, (double)1.0, (double)1.0);
                    GL11.glTranslatef((float)-0.36f, (float)0.0f, (float)0.0f);
                    mc.p.b("/mods/provoda/textures/laser.png");
                    this.laserline.render();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                }
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.0f, (float)0.075f, (float)0.037f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)1.0f, (float)0.0f);
                GL11.glScalef((float)1.5f, (float)1.5f, (float)1.5f);
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)(-this.hand3CurTransZ));
                GL11.glTranslatef((float)this.hand4CurTransX, (float)(-this.hand4CurTransY), (float)(-this.hand4CurTransZ));
                this.bullet.render();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScaled((double)0.25, (double)0.3f, (double)0.3f);
                GL11.glRotatef((float)80.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)-0.12f, (float)-0.5f, (float)-0.05f);
                GL11.glBindTexture((int)3553, (int)i1);
                this.hand.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScaled((double)0.3f, (double)0.3f, (double)0.3f);
                GL11.glRotatef((float)80.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)50.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)-0.2f, (float)-0.4f, (float)0.01f);
                GL11.glPushMatrix();
                GL11.glEnable((int)2884);
                GL11.glScaled((double)1.0, (double)1.0, (double)1.0);
                if (revinv.a(2) != null) {
                    if (revinv.a(2).b() == ModBase.UboinikBarrel) {
                        if (this.SkopePush) {
                            GL11.glTranslatef((float)1.5f, (float)1.55f, (float)0.05f);
                        } else {
                            GL11.glTranslatef((float)1.6f, (float)1.55f, (float)0.05f);
                        }
                    } else if (this.SkopePush) {
                        GL11.glTranslatef((float)1.3f, (float)1.3f, (float)0.0f);
                    } else {
                        GL11.glTranslatef((float)1.2f, (float)1.3f, (float)0.0f);
                    }
                } else if (this.SkopePush) {
                    GL11.glTranslatef((float)1.3f, (float)1.3f, (float)0.0f);
                } else {
                    GL11.glTranslatef((float)1.2f, (float)1.3f, (float)0.0f);
                }
                GL11.glRotatef((float)10.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)-30.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)100.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)this.shootrot, (float)0.0f, (float)0.0f, (float)1.0f);
                if (this.shoot1 > 0.0f) {
                    this.shoot1 -= 1.0f;
                }
                if (!((revinv.a(2) == null || revinv.a(2).b() == ModBase.Glushak) && revinv.a(2) != null || shootTimer != 2 || z < 1 || this.isReloading || this.isUnloading || mc.g.ah() || mc.s != null)) {
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
                GL11.glRotatef((float)(-this.currentxrot), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)this.currentyrot, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)this.currentzrot, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)this.currentxtrans, (float)(-this.currentytrans), (float)(-this.currentztrans));
                GL11.glTranslatef((float)(-this.hand5CurTransX), (float)(-this.hand5CurTransY), (float)0.0f);
                GL11.glTranslatef((float)this.hand6CurTransX, (float)this.hand6CurTransY, (float)(-this.hand6CurTransZ));
                GL11.glRotatef((float)(-this.hand6CurRot1), (float)0.0f, (float)0.0f, (float)1.0f);
                if (revinv.a(3) != null && revinv.a(3).b() == ModBase.AshotButt && !this.isReloading) {
                    GL11.glScalef((float)1.0f, (float)(this.hand7CurTransX * 8.0f), (float)(this.hand7CurTransX * 5.0f));
                }
                GL11.glTranslatef((float)(this.hand7CurTransX * 1.7f), (float)(this.hand7CurTransY / 5.0f), (float)(-this.hand7CurTransZ / 5.0f));
                GL11.glRotatef((float)(-this.hand7CurTransX * 90.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)(this.hand7CurTransX * 30.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glTranslatef((float)(this.modifiCurRot1 / 50.0f), (float)(-this.modifiCurRot1 / 70.0f), (float)0.0f);
                GL11.glBindTexture((int)3553, (int)i1);
                this.hand.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glScaled((double)0.067f, (double)0.07f, (double)0.05f);
                GL11.glTranslatef((float)1.6f, (float)-0.4f, (float)-0.22f);
                GL11.glRotatef((float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                if (revinv.a(3) != null && revinv.a(3).b() == ModBase.AshotButt && !this.isReloading) {
                    GL11.glScalef((float)(this.hand7CurTransX * 3.5f), (float)1.0f, (float)1.0f);
                }
                mc.p.b("/mods/provoda/textures/clock.png");
                this.clock.a((mp)null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                String var15 = new SimpleDateFormat("mm").format(Calendar.getInstance().getTime());
                this.minrotateangle = 360 * Integer.parseInt(var15) / 60;
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
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
        }
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
                this.scopeCurRot1 += 0.1f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurRot2 < this.scopeMaxRot2) {
                this.scopeCurRot2 += 0.1f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransX < this.scopeMaxTransX) {
                this.scopeCurTransX += 0.016f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransY < this.scopeMaxTransY) {
                this.scopeCurTransY += 0.008f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransZ < this.scopeMaxTransZ) {
                this.scopeCurTransZ = (float)((double)this.scopeCurTransZ + 0.008 / (double)(this.animspeed * 2.0f));
            }
        } else {
            if (this.scopeCurRot1 <= this.scopeMaxRot1 && this.scopeCurRot1 > 0.0f) {
                this.scopeCurRot1 -= 0.1f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurRot2 <= this.scopeMaxRot2 && this.scopeCurRot2 > 0.0f) {
                this.scopeCurRot2 -= 0.1f / (this.animspeed * 2.0f);
            }
            if (this.scopeCurTransX <= this.scopeMaxTransX && this.scopeCurTransX > 0.0f) {
                this.scopeCurTransX -= 0.016f / (this.animspeed * 2.0f);
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
                this.currentzrot += 3.0f / (this.animspeed * 3.0f);
            }
            if (this.currentxrot < this.maxxrot) {
                this.currentxrot += 7.0f / (this.animspeed * 3.0f);
            }
            if (this.currentyrot < this.maxyrot) {
                this.currentyrot += 2.0f / (this.animspeed * 3.0f);
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
                this.currentzrot -= 3.0f / (this.animspeed * 3.0f);
            }
            if (this.currentyrot > 0.0f) {
                this.currentyrot -= 7.0f / (this.animspeed * 3.0f);
            }
            if (this.currentxrot > 0.0f) {
                this.currentxrot -= 5.0f / (this.animspeed * 3.0f);
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
                this.sprintCurTransY += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurTransZ < this.sprintMaxTransZ) {
                this.sprintCurTransZ += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurRot1 < this.sprintMaxRot1) {
                this.sprintCurRot1 += 7.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurRot2 < this.sprintMaxRot2) {
                this.sprintCurRot2 += 2.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurTransX < this.sprintMaxTransX) {
                this.sprintCurTransX += 0.01f / (this.animspeed * 2.0f);
            }
        } else {
            if (this.sprintCurTransY > 0.0f) {
                this.sprintCurTransY -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurTransZ > 0.0f) {
                this.sprintCurTransZ -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.sprintCurRot1 > 0.0f) {
                this.sprintCurRot1 -= 9.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurRot2 > 0.0f) {
                this.sprintCurRot2 -= 2.0f / (this.animspeed * 3.0f);
            }
            if (this.sprintCurTransX > 0.0f) {
                this.sprintCurTransX -= 0.01f / (this.animspeed * 2.0f);
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
                this.handCurTransZ += 0.002f / (this.animspeed * 2.0f);
            }
            if (this.handCurTransY < this.handMaxTransY) {
                this.handCurTransY += 0.006f / (this.animspeed * 2.0f);
            }
            if (this.handCurTransX < this.handMaxTransX) {
                this.handCurTransX += 0.004f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot1 < this.handMaxRot1) {
                this.handCurRot1 += 4.0f / (this.animspeed * 3.0f);
            }
            if (this.handCurRot2 < this.handMaxRot1) {
                this.handCurRot2 += 0.5f / (this.animspeed * 3.0f);
            }
            if (this.handCurRot3 < this.handMaxRot1) {
                this.handCurRot3 += 0.5f / (this.animspeed * 3.0f);
            }
        } else {
            if (this.handCurTransZ > 0.0f) {
                this.handCurTransZ -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.handCurTransY > 0.0f) {
                this.handCurTransY -= 0.006f / (this.animspeed * 2.0f);
            }
            if (this.handCurTransX > 0.0f) {
                this.handCurTransX -= 0.001f / (this.animspeed * 2.0f);
            }
            if (this.handCurRot1 > 0.0f) {
                this.handCurRot1 -= 2.0f / (this.animspeed * 3.0f);
            }
            if (this.handCurRot2 > 0.0f) {
                this.handCurRot2 -= 0.5f / (this.animspeed * 3.0f);
            }
            if (this.handCurRot3 > 0.0f) {
                this.handCurRot3 -= 0.5f / (this.animspeed * 3.0f);
            }
        }
    }

    private void hand1(boolean push) {
        if (push) {
            if (this.hand1CurTransZ < this.hand1MaxTransZ) {
                this.hand1CurTransZ += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransY < this.hand1MaxTransY) {
                this.hand1CurTransY += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurTransX < this.hand1MaxTransX) {
                this.hand1CurTransX += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand1CurRot1 < this.hand1MaxRot1) {
                this.hand1CurRot1 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand1CurRot3 < this.hand1MaxRot3) {
                this.hand1CurRot3 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand1CurRot2 < this.hand1MaxRot2) {
                this.hand1CurRot2 += 0.03f / (this.animspeed * 3.0f);
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
                this.hand1CurRot1 -= 1.0f / (this.animspeed * 3.0f);
            }
            if (this.hand1CurRot2 > 0.0f) {
                this.hand1CurRot2 -= 1.0f / (this.animspeed * 3.0f);
            }
            if (this.hand1CurRot3 > 0.0f) {
                this.hand1CurRot3 -= 1.0f / (this.animspeed * 3.0f);
            }
        }
    }

    private void hand2(boolean push) {
        if (push) {
            if (this.hand2CurTransZ < this.hand2MaxTransZ) {
                this.hand2CurTransZ += 0.015f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransY < this.hand2MaxTransY) {
                this.hand2CurTransY += 0.015f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurTransX < this.hand2MaxTransX) {
                this.hand2CurTransX += 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand2CurRot1 < this.hand2MaxRot1) {
                this.hand2CurRot1 += 10.0f / (this.animspeed * 3.0f);
            }
            if (this.hand2CurRot3 < this.hand2MaxRot3) {
                this.hand2CurRot3 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand2CurRot2 < this.hand2MaxRot2) {
                this.hand2CurRot2 += 0.03f / (this.animspeed * 3.0f);
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
                this.hand2CurRot1 -= 10.0f / (this.animspeed * 3.0f);
            }
            if (this.hand2CurRot2 > 0.0f) {
                this.hand2CurRot2 -= 1.0f / (this.animspeed * 3.0f);
            }
            if (this.hand2CurRot3 > 0.0f) {
                this.hand2CurRot3 -= 1.0f / (this.animspeed * 3.0f);
            }
        }
    }

    private void hand3(boolean push) {
        if (push) {
            if (this.hand3CurTransZ < this.hand3MaxTransZ) {
                this.hand3CurTransZ += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransY < this.hand3MaxTransY) {
                this.hand3CurTransY += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransX < this.hand3MaxTransX) {
                this.hand3CurTransX += 0.02f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurRot1 < this.hand3MaxRot1) {
                this.hand3CurRot1 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand3CurRot3 < this.hand3MaxRot3) {
                this.hand3CurRot3 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand3CurRot2 < this.hand3MaxRot2) {
                this.hand3CurRot2 += 0.03f / (this.animspeed * 3.0f);
            }
        } else {
            if (this.hand3CurTransZ > 0.0f) {
                this.hand3CurTransZ -= 0.008f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransY > 0.0f) {
                this.hand3CurTransY -= 0.008f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurTransX > 0.0f) {
                this.hand3CurTransX -= 0.008f / (this.animspeed * 2.0f);
            }
            if (this.hand3CurRot1 > 0.0f) {
                this.hand3CurRot1 -= 0.01f / (this.animspeed * 3.0f);
            }
            if (this.hand3CurRot2 > 0.0f) {
                this.hand3CurRot2 -= 0.01f / (this.animspeed * 3.0f);
            }
            if (this.hand3CurRot3 > 0.0f) {
                this.hand3CurRot3 -= 0.01f / (this.animspeed * 3.0f);
            }
        }
    }

    private void hand4(boolean push) {
        if (push) {
            if (this.hand4CurTransZ < this.hand4MaxTransZ) {
                this.hand4CurTransZ += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransY < this.hand4MaxTransY) {
                this.hand4CurTransY += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransX < this.hand4MaxTransX) {
                this.hand4CurTransX += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot1 < this.hand4MaxRot1) {
                this.hand4CurRot1 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand4CurRot3 < this.hand4MaxRot3) {
                this.hand4CurRot3 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand4CurRot2 < this.hand4MaxRot2) {
                this.hand4CurRot2 += 0.03f / (this.animspeed * 3.0f);
            }
        } else {
            if (this.hand4CurTransZ > 0.0f) {
                this.hand4CurTransZ -= 0.006f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransY > 0.0f) {
                this.hand4CurTransY -= 0.006f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurTransX > 0.0f) {
                this.hand4CurTransX -= 0.004f / (this.animspeed * 2.0f);
            }
            if (this.hand4CurRot1 > 0.0f) {
                this.hand4CurRot1 -= 0.01f / (this.animspeed * 3.0f);
            }
            if (this.hand4CurRot2 > 0.0f) {
                this.hand4CurRot2 -= 0.01f / (this.animspeed * 3.0f);
            }
            if (this.hand4CurRot3 > 0.0f) {
                this.hand4CurRot3 -= 0.01f / (this.animspeed * 3.0f);
            }
        }
    }

    private void hand5(boolean push) {
        if (push) {
            if (this.hand5CurTransZ < this.hand5MaxTransZ) {
                this.hand5CurTransZ += 0.06f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurTransY < this.hand5MaxTransY) {
                this.hand5CurTransY += 0.06f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurTransX < this.hand5MaxTransX) {
                this.hand5CurTransX += 0.06f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurRot1 < this.hand5MaxRot1) {
                this.hand5CurRot1 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand5CurRot3 < this.hand5MaxRot3) {
                this.hand5CurRot3 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand5CurRot2 < this.hand5MaxRot2) {
                this.hand5CurRot2 += 0.03f / (this.animspeed * 3.0f);
            }
        } else {
            if (this.hand5CurTransZ > 0.0f) {
                this.hand5CurTransZ -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurTransY > 0.0f) {
                this.hand5CurTransY -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurTransX > 0.0f) {
                this.hand5CurTransX -= 0.01f / (this.animspeed * 2.0f);
            }
            if (this.hand5CurRot1 > 0.0f) {
                this.hand5CurRot1 -= 1.0f / (this.animspeed * 3.0f);
            }
            if (this.hand5CurRot2 > 0.0f) {
                this.hand5CurRot2 -= 0.01f / (this.animspeed * 3.0f);
            }
            if (this.hand5CurRot3 > 0.0f) {
                this.hand5CurRot3 -= 0.01f / (this.animspeed * 3.0f);
            }
        }
    }

    private void hand6(boolean push) {
        if (push) {
            if (this.hand6CurTransZ < this.hand6MaxTransZ) {
                this.hand6CurTransZ += 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransY < this.hand6MaxTransY) {
                this.hand6CurTransY += 0.034f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransX < this.hand6MaxTransX) {
                this.hand6CurTransX += 0.026f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurRot1 < this.hand6MaxRot1) {
                this.hand6CurRot1 += 2.0f / (this.animspeed * 3.0f);
            }
            if (this.hand6CurRot3 < this.hand6MaxRot3) {
                this.hand6CurRot3 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand6CurRot2 < this.hand6MaxRot2) {
                this.hand6CurRot2 += 0.03f / (this.animspeed * 3.0f);
            }
        } else {
            if (this.hand6CurTransZ > 0.0f) {
                this.hand6CurTransZ -= 0.1f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransY > 0.0f) {
                this.hand6CurTransY -= 0.1f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurTransX > 0.0f) {
                this.hand6CurTransX -= 0.03f / (this.animspeed * 2.0f);
            }
            if (this.hand6CurRot1 > 0.0f) {
                this.hand6CurRot1 -= 1.0f / (this.animspeed * 3.0f);
            }
            if (this.hand6CurRot2 > 0.0f) {
                this.hand6CurRot2 -= 0.01f / (this.animspeed * 3.0f);
            }
            if (this.hand6CurRot3 > 0.0f) {
                this.hand6CurRot3 -= 0.01f / (this.animspeed * 3.0f);
            }
        }
    }

    private void hand7(boolean push) {
        if (push) {
            if (this.hand7CurTransZ < this.hand7MaxTransZ) {
                this.hand7CurTransZ += 0.05f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurTransY < this.hand7MaxTransY) {
                this.hand7CurTransY += 0.05f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurTransX < this.hand7MaxTransX) {
                this.hand7CurTransX += 0.05f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurRot1 < this.hand7MaxRot1) {
                this.hand7CurRot1 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand7CurRot3 < this.hand7MaxRot3) {
                this.hand7CurRot3 += 0.03f / (this.animspeed * 3.0f);
            }
            if (this.hand7CurRot2 < this.hand7MaxRot2) {
                this.hand7CurRot2 += 0.03f / (this.animspeed * 3.0f);
            }
        } else {
            if (this.hand7CurTransZ > 0.0f) {
                this.hand7CurTransZ -= 0.06f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurTransY > 0.0f) {
                this.hand7CurTransY -= 0.06f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurTransX > 0.0f) {
                this.hand7CurTransX -= 0.06f / (this.animspeed * 2.0f);
            }
            if (this.hand7CurRot1 > 0.0f) {
                this.hand7CurRot1 -= 0.01f / (this.animspeed * 3.0f);
            }
            if (this.hand7CurRot2 > 0.0f) {
                this.hand7CurRot2 -= 0.01f / (this.animspeed * 3.0f);
            }
            if (this.hand7CurRot3 > 0.0f) {
                this.hand7CurRot3 -= 0.01f / (this.animspeed * 3.0f);
            }
        }
    }

    private void partCompliteCheck() {
        this.Complete1 = this.handCurTransZ == this.handMaxTransZ && this.handCurTransY == this.handMaxTransY && this.handCurTransX == this.handMaxTransX && this.handCurRot1 == this.handMaxRot1;
        this.Complete2 = this.hand1CurTransZ == this.hand1MaxTransZ && this.hand1CurTransX == this.hand1MaxTransX && this.hand1CurTransY == this.hand1MaxTransY && this.hand1CurRot1 == this.hand1MaxRot1;
        this.Complete3 = this.hand2CurTransZ == this.hand2MaxTransZ && this.hand2CurTransX == this.hand2MaxTransX && this.hand2CurTransY == this.hand2MaxTransY && this.hand2CurRot1 == this.hand2MaxRot1;
        this.Complete4 = this.hand3CurTransZ == this.hand3MaxTransZ && this.hand3CurTransX == this.hand3MaxTransX && this.hand3CurTransY == this.hand3MaxTransY && this.hand3CurRot1 == this.hand3MaxRot1;
        this.Complete5 = this.hand4CurTransZ == this.hand4MaxTransZ && this.hand4CurTransX == this.hand4MaxTransX && this.hand4CurTransY == this.hand4MaxTransY;
        this.Complete6 = this.hand5CurTransZ == this.hand5MaxTransZ && this.hand5CurTransX == this.hand5MaxTransX && this.hand5CurTransY == this.hand5MaxTransY;
        this.Complete7 = this.hand6CurTransZ == this.hand6MaxTransZ && this.hand6CurTransX == this.hand6MaxTransX && this.hand6CurTransY == this.hand6MaxTransY && this.hand6CurRot1 == this.hand6MaxRot1;
        this.Complete8 = this.hand7CurTransZ == this.hand7MaxTransZ && this.hand7CurTransX == this.hand7MaxTransX && this.hand7CurTransY == this.hand7MaxTransY;
    }
}
