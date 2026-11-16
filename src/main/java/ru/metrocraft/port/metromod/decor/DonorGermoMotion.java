package ru.metrocraft.port.metromod.decor;

import java.util.*;
import ru.metrocraft.port.metromod.decor.DonorGermoBlockRules.*;

/** Copy-on-tick donor state. World operations commit only AFTER a candidate fits.
 * Canonical new-item seed is obtained by replaying donor callbacks into air;
 * it is NOT a claim to recover the original server map's initial blocks. */
public final class DonorGermoMotion implements Sink {
    public final DonorGermoBlockRules rule;
    public final State state;
    private final LinkedHashSet<Cell> cells=new LinkedHashSet<>();
    public static final class Cell {
        public final int x,y,z;
        public Cell(int x,int y,int z) {
            if(x < -48 || x > 48 || y < -48 || y > 48 || z < -48 || z > 48)throw new IllegalArgumentException("Germo cell outside limit");
            this.x=x;this.y=y;this.z=z;
        }
        @Override public boolean equals(Object o) {if(!(o instanceof Cell))return false;Cell c=(Cell)o;return x==c.x && y==c.y && z==c.z;}
        @Override public int hashCode() {return (x*101+y)*101+z;}
    }
    public DonorGermoMotion(String id,float yaw) {
        rule=DonorGermoBlockRules.get(id);state=new State();state.rotation.y=yaw;
        state.segments=new boolean[rule.segmentCount];
    }
    public DonorGermoMotion copy() {
        DonorGermoMotion out=new DonorGermoMotion(rule,state);out.cells.addAll(cells);return out;
    }
    private DonorGermoMotion(DonorGermoBlockRules rule,State source) {
        this.rule=rule;state=new State();state.animation=new V(source.animation);state.previous=new V(source.previous);
        state.rotation=new V(source.rotation);state.scale=new V(source.scale);state.segments=source.segments.clone();
        state.activated=source.activated;state.powered=source.powered;state.blockMode=source.blockMode;
    }
    public Collection<Cell> cells() {return Collections.unmodifiableSet(cells);}
    @Override public void place(int x,int y,int z) {cells.add(new Cell(x,y,z));if(cells.size()>2048)throw new IllegalStateException("Germo cell budget");}
    @Override public void remove(int x,int y,int z) {cells.remove(new Cell(x,y,z));}
    /** Exactly one 20Hz server tick: elapsedSeconds * 40 = 2. */
    public DonorGermoMotion next(boolean activated) {
        DonorGermoMotion out=copy();out.state.activated=activated;out.state.powered=false;
        out.rule.tick(out.state,out,2F);return out;
    }
    public float progress() {
        // Multi-axis shutters close y first then x. Use total travelled distance,
        // not a guessed diagonal interpolation, for the cosmetic progress only.
        float total=0,left=0;
        float[] a={state.animation.x,state.animation.y,state.animation.z};
        float[] o={rule.open.x,rule.open.y,rule.open.z},c={rule.closed.x,rule.closed.y,rule.closed.z};
        for(int i=0;i<3;i++) {float span=Math.abs(o[i]-c[i]);total+=span;left+=Math.min(span,Math.abs(a[i]-c[i]));}
        return total==0?0:Math.max(0,Math.min(1,left/total));
    }
    private boolean stable(DonorGermoMotion other) {
        return state.animation.x==other.state.animation.x && state.animation.y==other.state.animation.y
                && state.animation.z==other.state.animation.z && cells.equals(other.cells)
                && Arrays.equals(state.segments,other.state.segments);
    }
    public static DonorGermoMotion seed(String id,float yaw,float progress) {
        DonorGermoMotion value=new DonorGermoMotion(id,yaw);
        // Literal donor lifecycle: initializes x only; previous=open; server
        // segment flags initially true when starting deactivated/unpowered.
        value.state.animation.x=value.rule.open.x;value.state.previous=new V(value.rule.open);
        Arrays.fill(value.state.segments,true);
        for(int i=0;i<20000;i++) {
            DonorGermoMotion next=value.next(false);boolean stop=value.stable(next);value=next;
            if(stop)break;if(i==19999)throw new IllegalStateException("Germo initialization did not settle");
        }
        if(progress>0)for(int i=0;i<20000;i++) {
            DonorGermoMotion next=value.next(true);boolean stop=value.stable(next);value=next;
            if(value.progress()>=progress || stop)break;if(i==19999)throw new IllegalStateException("Germo migration did not settle");
        }
        return value;
    }
    public int[] packedCells() {
        int[] out=new int[cells.size()*3];int i=0;for(Cell c:cells){out[i++]=c.x;out[i++]=c.y;out[i++]=c.z;}return out;
    }
    /** Strict bounded snapshot; null means corrupt/foreign data, never partial. */
    public static DonorGermoMotion restore(String id,float yaw,float[] data,byte[] flags,int[] packed) {
        DonorGermoMotion out=new DonorGermoMotion(id,yaw);
        if(data.length!=6 || flags.length!=out.rule.segmentCount || packed.length%3!=0 || packed.length>6144)return null;
        for(float v:data)if(!Float.isFinite(v) || Math.abs(v)>100)return null;
        for(byte b:flags)if(b!=0 && b!=1)return null;
        out.state.animation=new V(data[0],data[1],data[2]);out.state.previous=new V(data[3],data[4],data[5]);
        for(int i=0;i<flags.length;i++)out.state.segments[i]=flags[i]!=0;
        try {for(int i=0;i<packed.length;i+=3)out.place(packed[i],packed[i+1],packed[i+2]);}
        catch(IllegalArgumentException e){return null;}
        return out;
    }
}
