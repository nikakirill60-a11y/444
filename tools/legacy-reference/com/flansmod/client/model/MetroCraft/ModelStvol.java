/*
 * Decompiled with CFR 0.152.
 */
package com.flansmod.client.model.MetroCraft;

import com.flansmod.client.model.ModelAttachment;
import com.flansmod.client.tmt.ModelRendererTurbo;

public class ModelStvol
extends ModelAttachment {
    int textureX = 512;
    int textureY = 512;

    public ModelStvol() {
        this.attachmentModel = new ModelRendererTurbo[6];
        this.attachmentModel[0] = new ModelRendererTurbo(this, 1, 1, this.textureX, this.textureY);
        this.attachmentModel[1] = new ModelRendererTurbo(this, 25, 1, this.textureX, this.textureY);
        this.attachmentModel[2] = new ModelRendererTurbo(this, 49, 1, this.textureX, this.textureY);
        this.attachmentModel[3] = new ModelRendererTurbo(this, 73, 1, this.textureX, this.textureY);
        this.attachmentModel[4] = new ModelRendererTurbo(this, 145, 1, this.textureX, this.textureY);
        this.attachmentModel[5] = new ModelRendererTurbo(this, 217, 1, this.textureX, this.textureY);
        this.attachmentModel[0].addShapeBox(145.5f, -31.0f, -1.0f, 6, 3, 5, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f);
        this.attachmentModel[0].func_78793_a(-5.0f, -23.5f, -2.0f);
        this.attachmentModel[1].addShapeBox(145.5f, -28.0f, -1.0f, 6, 1, 5, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, -0.5f);
        this.attachmentModel[1].func_78793_a(-5.0f, -23.5f, -2.0f);
        this.attachmentModel[2].addShapeBox(145.5f, -32.0f, -1.0f, 6, 1, 5, 0.0f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f);
        this.attachmentModel[2].func_78793_a(-5.0f, -23.5f, -2.0f);
        this.attachmentModel[3].addShapeBox(148.5f, -28.0f, -1.0f, 30, 1, 5, 0.0f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.25f, -0.75f, 0.0f, 0.25f, -0.75f, 0.0f, 0.25f, -0.75f, 0.0f, 0.25f, -0.75f);
        this.attachmentModel[3].func_78793_a(-5.0f, -23.5f, -2.0f);
        this.attachmentModel[4].addShapeBox(148.5f, -31.0f, -1.0f, 30, 3, 5, 0.0f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f);
        this.attachmentModel[4].func_78793_a(-5.0f, -23.5f, -2.0f);
        this.attachmentModel[5].addShapeBox(148.5f, -32.0f, -1.0f, 30, 1, 5, 0.0f, 0.0f, 0.25f, -0.75f, 0.0f, 0.25f, -0.75f, 0.0f, 0.25f, -0.75f, 0.0f, 0.25f, -0.75f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, 0.25f);
        this.attachmentModel[5].func_78793_a(-5.0f, -23.5f, -2.0f);
        this.scaleAttachment = 1.1f;
        this.flipAll();
    }
}

