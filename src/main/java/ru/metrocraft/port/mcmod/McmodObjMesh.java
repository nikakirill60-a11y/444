package ru.metrocraft.port.mcmod;

import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.charset.*;

/** GPU-independent OBJ reader. Both Cinema4D g and Blender o delimit articulated parts. */
public final class McmodObjMesh {
    public final Map<String, List<float[]>> groups;
    private McmodObjMesh(Map<String, List<float[]>> data) {
        Map<String, List<float[]>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<float[]>> entry : data.entrySet())
            result.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        groups = Collections.unmodifiableMap(result);
    }
    /** Preserve raw source OBJ, including CP1251 material labels in the original gas mask. */
    public static McmodObjMesh read(InputStream input) throws IOException {return read(input,false);}
    /** Native transport OBJs switch materials inside a single object/group. */
    public static McmodObjMesh readMaterials(InputStream input) throws IOException {return read(input,true);}
    private static McmodObjMesh read(InputStream input,boolean byMaterial) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192]; int length;
        while ((length=input.read(buffer))!=-1) bytes.write(buffer,0,length);
        byte[] data=bytes.toByteArray(); String text;
        try { text=StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(data)).toString(); }
        catch (CharacterCodingException e) { text=new String(data,Charset.forName("windows-1251")); }
        if (text.startsWith("\uFEFF")) text=text.substring(1);
        return read(new StringReader(text),byMaterial);
    }
    public static McmodObjMesh read(Reader input) throws IOException {return read(input,false);}
    private static McmodObjMesh read(Reader input,boolean byMaterial) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        List<float[]> positions = new ArrayList<>(), uv = new ArrayList<>(), normals = new ArrayList<>();
        Map<String, List<float[]>> groups = new LinkedHashMap<>();
        String group = "default", line;
        while ((line = reader.readLine()) != null) {
            line = line.split("#", 2)[0].trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\s+");
            switch (p[0]) {
                case "v": positions.add(new float[]{f(p[1]), f(p[2]), f(p[3])}); break;
                case "vt": uv.add(new float[]{f(p[1]), 1-f(p[2])}); break;
                case "vn": normals.add(new float[]{f(p[1]), f(p[2]), f(p[3])}); break;
                case "g": case "o": if(!byMaterial)group = p.length > 1 ? p[1] : "default"; break;
                case "usemtl": if(byMaterial)group=p.length>1?p[1]:"default";break;
                case "f":
                    if (p.length < 4) throw new IOException("OBJ face has fewer than three vertices");
                    List<float[]> vertices = groups.computeIfAbsent(group, key -> new ArrayList<>());
                    for (int i=2; i<p.length-1; i++) {
                        String[] refs = {p[1], p[i], p[i+1]};
                        float[][] points = new float[3][];
                        for (int j=0; j<3; j++) points[j] = positions.get(index(refs[j].split("/")[0], positions.size()));
                        float ax=points[1][0]-points[0][0], ay=points[1][1]-points[0][1], az=points[1][2]-points[0][2];
                        float bx=points[2][0]-points[0][0], by=points[2][1]-points[0][1], bz=points[2][2]-points[0][2];
                        float nx=ay*bz-az*by, ny=az*bx-ax*bz, nz=ax*by-ay*bx;
                        float length=(float)Math.sqrt(nx*nx+ny*ny+nz*nz);
                        float[] faceNormal = length == 0 ? new float[]{0,1,0} : new float[]{nx/length,ny/length,nz/length};
                        for (int j=0; j<3; j++) {
                            String[] ref = refs[j].split("/", -1);
                            float[] t = ref.length>1 && !ref[1].isEmpty() ? uv.get(index(ref[1], uv.size())) : new float[2];
                            float[] n = ref.length>2 && !ref[2].isEmpty() ? normals.get(index(ref[2], normals.size())) : faceNormal;
                            float[] v = points[j];
                            vertices.add(new float[]{v[0],v[1],v[2],t[0],t[1],n[0],n[1],n[2]});
                        }
                    }
                    break;
                default: break; // One original atlas bound explicitly: MTL lookup is intentionally unnecessary.
            }
        }
        return new McmodObjMesh(groups);
    }
    private static float f(String text) { return Float.parseFloat(text); }
    private static int index(String text, int size) {
        int i = Integer.parseInt(text);
        int result = i > 0 ? i-1 : size+i;
        if (i == 0 || result < 0 || result >= size) throw new IllegalArgumentException("Invalid OBJ index " + text);
        return result;
    }
}
