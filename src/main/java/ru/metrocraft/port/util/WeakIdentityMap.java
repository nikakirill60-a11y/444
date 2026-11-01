package ru.metrocraft.port.util;
import java.lang.ref.*;
import java.util.*;
import java.util.function.Function;
/** Weak lifetime, reference-identity keys, and atomic access across integrated-server/render threads.
 * Entity.equals/hashCode compare entity IDs: NEVER use them for side-local mutable inventories.
 */
public final class WeakIdentityMap<K,V> {
    private final ReferenceQueue<K> queue=new ReferenceQueue<>();
    private final Map<Key<K>,V> values=new HashMap<>();
    private static final class Key<T> extends WeakReference<T> {
        private final int hash;
        Key(T key,ReferenceQueue<T> queue){super(Objects.requireNonNull(key),queue);hash=System.identityHashCode(key);}
        @Override public int hashCode(){return hash;}
        @Override public boolean equals(Object other){
            if(this==other)return true;
            if(!(other instanceof Key))return false;
            Object key=get();return key!=null&&key==((Key<?>)other).get();
        }
    }
    private void clean(){Reference<? extends K> key;while((key=queue.poll())!=null)values.remove(key);}
    public synchronized V remove(K key){clean();return values.remove(new Key<>(key,null));}
    public synchronized V get(K key){clean();return values.get(new Key<>(key,null));}
    public synchronized V computeIfAbsent(K key,Function<? super K,? extends V> factory){
        clean();Key<K> lookup=new Key<>(key,null);V value=values.get(lookup);
        if(value==null){value=Objects.requireNonNull(factory.apply(key));values.put(new Key<>(key,queue),value);}
        return value;
    }
}
