package ru.metrocraft.port.blackrazid.weapons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;

/** Pure evaluation of source-owned equipped geometry. Never executes legacy Java or touches OpenGL/NBT. */
public final class NativeWeaponScene {
    private NativeWeaponScene(){}
    private static final class Condition {String op;Condition[] terms;}
    private static final class Node {String op,mesh,role;float[] values,rounds;Condition condition;Node[] yes,no;}
    private static final class Scene {Node[] nodes;}
    private static final Map<String,Scene> SCENES=load();
    private static Map<String,Scene> load(){
        try(Reader r=new InputStreamReader(Objects.requireNonNull(NativeWeaponScene.class.getResourceAsStream("/assets/metroport/blackrazid_weapon_scenes.json")),StandardCharsets.UTF_8)){
            return new Gson().fromJson(r,new TypeToken<Map<String,Scene>>(){}.getType());
        }catch(IOException e){throw new IllegalStateException("Native equipped scenes unavailable",e);}
    }
    public static Set<String> guns(){return Collections.unmodifiableSet(SCENES.keySet());}
    public static final class Draw {
        public final String mesh,role;
        /** Independent column-major matrix suitable for glMultMatrix; no shared mutable transforms. */
        public final float[] matrix;
        private Draw(String mesh,String role,Matrix4f m){
            this.mesh=mesh;this.role=role;
            matrix=new float[]{m.m00,m.m10,m.m20,m.m30,m.m01,m.m11,m.m21,m.m31,m.m02,m.m12,m.m22,m.m32,m.m03,m.m13,m.m23,m.m33};
        }
    }
    private static boolean condition(Condition c,NativeWeaponVisuals.Equipment e){
        switch(c.op){
            case "and":for(Condition term:c.terms)if(!condition(term,e))return false;return true;
            case "or":for(Condition term:c.terms)if(condition(term,e))return true;return false;
            case "not":return !condition(c.terms[0],e);
            case "false":return false;
            case "MAG":return e.magazine;
            case "BIG":return e.big;
            case "AIM":return e.aim;
            case "RELOAD":return e.action==1;
            case "UNLOAD":return e.action==2;
            case "INSERTED":return e.action==1 && Float.isFinite(e.phase) && e.phase>=.5F;
            default:
                if(c.op.startsWith("R"))return Math.max(0,Math.min(400,e.rounds))>=Integer.parseInt(c.op.substring(1));
                if(c.op.startsWith("S")){
                    int slot=c.op.charAt(1)-'0';String value=e.slots[slot],test=c.op.substring(2);
                    if(test.equals("EMPTY"))return value.isEmpty();if(test.equals("PRESENT"))return !value.isEmpty();
                    if(test.startsWith("EQ"))return test.substring(2).equals(value);if(test.startsWith("NE"))return !test.substring(2).equals(value);
                }
                throw new IllegalArgumentException("Unknown source scene condition "+c.op);
        }
    }
    private static final class Evaluator {
        Matrix4f matrix=new Matrix4f();final Deque<Matrix4f> stack=new ArrayDeque<>();
        final List<Draw> draws=new ArrayList<>();final NativeWeaponVisuals.Equipment equipment;
        Evaluator(NativeWeaponVisuals.Equipment e){equipment=e;matrix.setIdentity();}
        void nodes(Node[] nodes){
            for(Node n:nodes){
                switch(n.op){
                    case "if":nodes(condition(n.condition,equipment)?n.yes:n.no);continue;
                    case "push":stack.push(new Matrix4f(matrix));continue;
                    case "pop":if(stack.isEmpty())throw new IllegalStateException("Unbalanced native scene");matrix=stack.pop();continue;
                    case "draw":draws.add(new Draw(n.mesh,n.role,matrix));continue;
                    default:
                        float[] v=n.values.clone();int rounds=Math.max(0,Math.min(400,equipment.rounds));
                        for(int i=0;i<v.length;i++){v[i]+=n.rounds[i]*rounds;if(!Float.isFinite(v[i]))throw new IllegalArgumentException("Non-finite source transform");}
                        Matrix4f transform=new Matrix4f();transform.setIdentity();
                        switch(n.op){
                            case "translate":transform.m03=v[0];transform.m13=v[1];transform.m23=v[2];break;
                            case "scale":transform.m00=v[0];transform.m11=v[1];transform.m22=v[2];break;
                            case "rotate":
                                if(v[0]==0 || v[1]*v[1]+v[2]*v[2]+v[3]*v[3]<1e-12F)continue;
                                transform.setRotation(new AxisAngle4f(v[1],v[2],v[3],(float)Math.toRadians(v[0])));break;
                            default:throw new IllegalArgumentException("Unknown source transform "+n.op);
                        }
                        matrix.mul(transform);
                }
            }
        }
    }
    public static List<Draw> draw(String source,NativeWeaponVisuals.Equipment equipment){
        Scene scene=SCENES.get(source);if(scene==null)throw new IllegalArgumentException("No equipped scene for "+source);
        Evaluator evaluator=new Evaluator(equipment);evaluator.nodes(scene.nodes);
        if(!evaluator.stack.isEmpty())throw new IllegalStateException("Leaked native scene matrix "+source);
        return Collections.unmodifiableList(evaluator.draws);
    }
}
