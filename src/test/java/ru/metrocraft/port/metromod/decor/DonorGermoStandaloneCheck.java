package ru.metrocraft.port.metromod.decor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import ru.metrocraft.port.metromod.decor.DonorGermoBlockRules.*;

/** Runs WITHOUT Forge/Gradle/network. Fixtures evaluated from donor BYTECODE,
 * not generated from DonorGermoBlockRules. This is NOT a gameplay test. */
public final class DonorGermoStandaloneCheck {
    private static int checks;
    private static void check(boolean condition,String message) {
        checks++;if(!condition)throw new AssertionError(message);
    }
    private static final class Trace implements Sink {
        final StringBuilder text=new StringBuilder();
        public void place(int x,int y,int z) {text.append('+').append(x).append(',').append(y).append(',').append(z).append(';');}
        public void remove(int x,int y,int z) {text.append('-').append(x).append(',').append(y).append(',').append(z).append(';');}
    }
    private static String hex(byte[] bytes) {
        StringBuilder out=new StringBuilder();for(byte b:bytes)out.append(String.format(Locale.ROOT,"%02x",b&255));return out.toString();
    }
    private static String digest(Trace trace,State s) throws Exception {
        StringBuilder value=new StringBuilder(trace.text).append('|');
        float[] fields={s.animation.x,s.animation.y,s.animation.z,s.previous.x,s.previous.y,s.previous.z};
        for(int i=0;i<fields.length;i++) {
            if(i>0)value.append(',');
            value.append(String.format(Locale.ROOT,"%08x",Float.floatToIntBits(fields[i])));
        }
        return hex(MessageDigest.getInstance("SHA-256").digest(value.toString().getBytes(StandardCharsets.UTF_8)));
    }
    public static void main(String[] args) throws Exception {
        int cases=0;Set<String> covered=new HashSet<>();
        for(String line:Files.readAllLines(Paths.get(args[0]),StandardCharsets.UTF_8)) {
            if(line.startsWith("#") || line.isEmpty())continue;
            String[] v=line.split("\t");DonorGermoBlockRules rule=DonorGermoBlockRules.get(v[0]);
            State s=new State();s.rotation.y=Float.parseFloat(v[2]);
            s.animation=new V(Float.parseFloat(v[3]),Float.parseFloat(v[4]),0);
            s.previous=new V(Float.parseFloat(v[5]),Float.parseFloat(v[6]),0);
            Trace trace=new Trace();
            if(v[1].equals("closed"))rule.towardClosed(s,trace,0,0,0);else rule.towardOpen(s,trace,0,0,0);
            check(digest(trace,s).equals(v[7]),"Bytecode mismatch: "+line+" actual "+digest(trace,s));
            covered.add(v[0]);cases++;
            // Non-block mode must not alter animation/previous or write blocks.
            s=new State();s.blockMode=false;trace=new Trace();
            rule.towardClosed(s,trace,0,0,0);rule.towardOpen(s,trace,0,0,0);
            check(trace.text.length()==0,"Non-block mode writes: "+v[0]);
        }
        check(covered.size()==17,"Profile coverage");
        thresholdsAndHistory();
        System.out.println("PASS: "+cases+" bytecode differential fixtures; "+checks+" assertions; 17 profiles; NOT Forge/gameplay validation");
    }
    private static void thresholdsAndHistory() {
        DonorGermoBlockRules rule=DonorGermoBlockRules.get("metromod_germo_1");
        State s=new State();s.animation.x=-.75F;Trace trace=new Trace();rule.towardClosed(s,trace,0,0,0);
        check(trace.text.toString().equals("+2,1,0;+3,1,0;+4,1,0;+5,1,0;+6,1,0;"),"Strict > threshold");
        s.animation.x=Math.nextUp(-.75F);trace=new Trace();rule.towardClosed(s,trace,0,0,0);
        check(trace.text.toString().startsWith("+2,0,0;"),"Float successor threshold");
        s=new State();s.animation.x=-.76F;s.segments=new boolean[]{false,false,false};trace=new Trace();
        // Activation moves toward OPEN: original helper emits REMOVE callbacks.
        s.activated=true;rule.tick(s,trace,2F);
        check(s.segments[0] && trace.text.toString().startsWith("-2,0,0;"),"Activation/history callback");
        trace=new Trace();rule.tick(s,trace,2F);
        check(trace.text.length()==0,"Same threshold must not emit twice");
        s.activated=false;trace=new Trace();rule.tick(s,trace,500F);
        check(trace.text.toString().startsWith("+2,0,0;"),"Reversal must retain segment flags");
        check(s.animation.x==0,"Clamp to original closed endpoint");
        boolean thrown=false;try {DonorGermoBlockRules.get("guessed_profile");}catch(IllegalArgumentException e){thrown=true;}
        check(thrown,"Unknown profile refused");
    }
}
