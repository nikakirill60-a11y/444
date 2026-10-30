/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 */
package com.flansmod.client.model;

import com.flansmod.client.tmt.ModelRendererTurbo;
import net.minecraft.client.model.ModelBase;

public class ModelAttachment
extends ModelBase {
    public ModelRendererTurbo[] attachmentModel = new ModelRendererTurbo[0];
    public float renderOffset = 0.0f;
    public float scaleAttachment = 1.0f;

    public void renderAttachment(float f) {
        for (ModelRendererTurbo model : this.attachmentModel) {
            if (model == null) continue;
            model.func_78785_a(f);
        }
    }

    public void flipAll() {
        for (ModelRendererTurbo anAttachmentModel : this.attachmentModel) {
            anAttachmentModel.doMirror(false, true, true);
            anAttachmentModel.func_78793_a(anAttachmentModel.field_78800_c, -anAttachmentModel.field_78797_d, -anAttachmentModel.field_78798_e);
        }
    }
}

