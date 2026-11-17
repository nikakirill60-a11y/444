package ru.metrocraft.port.client.menu;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.metrocraft.port.mcmod.McmodObjMesh;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

/** Render-thread mesh adapter. Uses every usemtl diffuse binding separately.
 * Normal/specular/emission are preserved in the manifest but not applied here:
 * shader/material parity is still pending. Never renders or modifies game hands.
 * Owner must close on GUI exit/reload, on the render thread.
 */
public final class OriginalMenuMeshes implements OriginalMenuGeometry.Sink, AutoCloseable {
    private final Map<String, Definition> definitions = new LinkedHashMap<>();
    private final Map<String, List<Group>> meshes = new LinkedHashMap<>();
    private static final class Definition {
        final ResourceLocation mesh;
        final Map<String, ResourceLocation> textures = new LinkedHashMap<>();
        Definition(JsonObject row) {
            mesh = new ResourceLocation(row.get("mesh").getAsString());
            for (Map.Entry<String, JsonElement> entry : row.getAsJsonObject("materials").entrySet()) {
                textures.put(entry.getKey(), new ResourceLocation(entry.getValue().getAsJsonObject().get("diffuse").getAsString()));
            }
        }
    }
    private static final class Group {
        final int list;
        final ResourceLocation texture;
        Group(int list, ResourceLocation texture) { this.list = list; this.texture = texture; }
    }
    public OriginalMenuMeshes() throws IOException {
        ResourceLocation manifest = new ResourceLocation("metroport", "metromod_menu.json");
        try (Reader in = new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(manifest).getInputStream(), StandardCharsets.UTF_8)) {
            JsonObject root = new JsonParser().parse(in).getAsJsonObject();
            for (JsonElement element : root.getAsJsonArray("models")) {
                JsonObject row = element.getAsJsonObject();
                String key = row.get("source_reference").getAsString();
                if (definitions.put(key, new Definition(row)) != null) throw new IOException("Duplicate menu mesh " + key);
            }
            if (definitions.size() != 35) throw new IOException("Incomplete original menu mesh manifest");
        }
    }
    private List<Group> load(String source) {
        Definition definition = definitions.get(source);
        if (definition == null) throw new IllegalArgumentException("Unknown original menu mesh " + source);
        final McmodObjMesh obj;
        try (InputStream in = new GZIPInputStream(Minecraft.getMinecraft().getResourceManager().getResource(definition.mesh).getInputStream())) {
            obj = McmodObjMesh.readMaterials(in);
        } catch (IOException e) { throw new IllegalStateException("Cannot read original menu mesh " + source, e); }
        if (!obj.groups.keySet().equals(definition.textures.keySet())) throw new IllegalStateException("Menu material closure changed: " + source);
        List<Group> result = new ArrayList<>();
        try {
            for (Map.Entry<String, List<float[]>> entry : obj.groups.entrySet()) {
                int list = GLAllocation.generateDisplayLists(1);
                if (list == 0) throw new IllegalStateException("Cannot allocate original menu display list");
                result.add(new Group(list, definition.textures.get(entry.getKey())));
                GlStateManager.glNewList(list, GL11.GL_COMPILE);
                try {
                    BufferBuilder buffer = Tessellator.getInstance().getBuffer();
                    buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
                    for (float[] v : entry.getValue()) buffer.pos(v[0], v[1], v[2]).tex(v[3], v[4]).normal(v[5], v[6], v[7]).endVertex();
                    Tessellator.getInstance().draw();
                } finally { GlStateManager.glEndList(); }
            }
            return result;
        } catch (RuntimeException | Error e) {
            for (Group group : result) GLAllocation.deleteDisplayLists(group.list);
            throw e;
        }
    }
    @Override public void mesh(String sourceReference) {
        List<Group> groups = meshes.get(sourceReference);
        if (groups == null) { groups = load(sourceReference); meshes.put(sourceReference, groups); }
        for (Group group : groups) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(group.texture);
            GlStateManager.callList(group.list);
        }
    }
    @Override public void close() {
        for (List<Group> mesh : meshes.values()) for (Group group : mesh) GLAllocation.deleteDisplayLists(group.list);
        meshes.clear();
    }
    @Override public void push() { GlStateManager.pushMatrix(); }
    @Override public void pop() { GlStateManager.popMatrix(); }
    @Override public void translate(float x, float y, float z) { GlStateManager.translate(x, y, z); }
    @Override public void rotate(float angle, float x, float y, float z) { GlStateManager.rotate(angle, x, y, z); }
    @Override public void scale(float x, float y, float z) { GlStateManager.scale(x, y, z); }
    @Override public void color(float r, float g, float b) { GlStateManager.color(r, g, b, 1); }
}
