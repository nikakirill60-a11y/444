package ru.metrocraft.port.client;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import ru.metrocraft.port.client.tmt.ModelRendererTurbo;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Data-only reconstruction of original Turbo boxes, UVs and corner deformation. */
public final class LegacyGeometry extends ModelBase {
    public static final Map<String, LegacyGeometry> CACHE = new HashMap<>();
    public final Map<String, List<ModelRendererTurbo>> groups = new LinkedHashMap<>();
    public final Map<ModelRendererTurbo, float[]> original = new IdentityHashMap<>();
    public static final class Op { String op; float[] values; }
    public static final class Part { String group; int index; float[] uv; Op[] ops; }
    public static final class Rig {
        public float[] barrelAttachPoint,scopeAttachPoint,colAttachPoint,gripAttachPoint,stockAttachPoint,barrelBreakPoint;
        public float downReload,gunSlideDistance,pumpHandleDistance,breakAngle,scaleAttachment=1;
        public String animationType;
    }
    public Rig rig;
    public static final class Data { Part[] parts; Op[] transforms; Rig rig; }

    public static LegacyGeometry get(String id) {
        return CACHE.computeIfAbsent(id, LegacyGeometry::new);
    }

    private LegacyGeometry(String id) {
        ResourceLocation resource = new ResourceLocation("metroport", "geometry/" + id + ".json");
        try (InputStreamReader reader = new InputStreamReader(Minecraft.getMinecraft().getResourceManager()
                .getResource(resource).getInputStream(), StandardCharsets.UTF_8)) {
            Data data = new Gson().fromJson(reader, Data.class);
            rig=data.rig==null?new Rig():data.rig;
            for (Part part : data.parts) {
                float[] uv = part.uv;
                ModelRendererTurbo box = new ModelRendererTurbo(this, (int)uv[0], (int)uv[1], (int)uv[2], (int)uv[3]);
                for (Op op : part.ops) apply(box, op);
                groups.computeIfAbsent(part.group, key -> new ArrayList<>()).add(box);
            }
            for (Op transform : data.transforms) {
                for (Map.Entry<String, List<ModelRendererTurbo>> entry : groups.entrySet()) {
                    if (transform.op.equals("translateShoot") && !entry.getKey().startsWith("shootSprite")) continue;
                    if (transform.op.equals("translateWeapon") && (entry.getKey().equals("lHandModel") || entry.getKey().equals("rHandModel"))) continue;
                    for (ModelRendererTurbo box : entry.getValue()) {
                        if (transform.op.equals("flipAll")) {
                            box.doMirror(false, true, true);
                            box.rotationPointY = -box.rotationPointY;
                            box.rotationPointZ = -box.rotationPointZ;
                        } else {
                            box.rotationPointX += transform.values[0];
                            box.rotationPointY += transform.values[1];
                            box.rotationPointZ += transform.values[2];
                        }
                    }
                }
            }
            for (List<ModelRendererTurbo> group : groups.values()) for (ModelRendererTurbo part : group) {
                original.put(part, new float[]{part.rotationPointX, part.rotationPointY, part.rotationPointZ,
                        part.rotateAngleX, part.rotateAngleY, part.rotateAngleZ});
            }
        } catch (Exception e) { throw new IllegalStateException("Cannot load legacy geometry " + resource, e); }
    }

    private void apply(ModelRendererTurbo p, Op op) {
        float[] v = op.values;
        switch (op.op) {
            case "point": p.setRotationPoint(v[0], v[1], v[2]); break;
            case "rx": p.rotateAngleX = v[0]; break;
            case "ry": p.rotateAngleY = v[0]; break;
            case "rz": p.rotateAngleZ = v[0]; break;
            case "px": p.rotationPointX = v[0]; break;
            case "py": p.rotationPointY = v[0]; break;
            case "pz": p.rotationPointZ = v[0]; break;
            case "box": p.addBox(v[0],v[1],v[2],(int)v[3],(int)v[4],(int)v[5],v.length > 6 ? v[6] : 0); break;
            case "shape": p.addShapeBox(v[0],v[1],v[2],(int)v[3],(int)v[4],(int)v[5],v[6],
                    v[7],v[8],v[9],v[10],v[11],v[12],v[13],v[14],v[15],v[16],v[17],v[18],
                    v[19],v[20],v[21],v[22],v[23],v[24],v[25],v[26],v[27],v[28],v[29],v[30]); break;
            default: throw new IllegalArgumentException(op.op);
        }
    }

    public void renderAll(float scale) {
        for (String group : groups.keySet()) if (!group.startsWith("shootSprite")) renderGroup(group, scale);
    }

    public void renderGroup(String name, float scale) {
        List<ModelRendererTurbo> parts = groups.get(name);
        if (parts != null) for (ModelRendererTurbo part : parts) part.render(scale);
    }
}
