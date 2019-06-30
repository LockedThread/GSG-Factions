package com.gameservergroup.gsgcore.collections;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {

    private static final Boolean DUMMY_OBJECT = Boolean.TRUE;
    private static final Function<Object, Boolean> MIGRATION_FUNCTION = (k) -> DUMMY_OBJECT;

    private final ConcurrentHashMap<E, Boolean> concurrentHashMap;

    public ConcurrentHashSet(Collection<E> collection) {
        this.concurrentHashMap = new ConcurrentHashMap<>();
        if (collection != null && !collection.isEmpty()) {
            for (E e : collection) {
                concurrentHashMap.put(e, DUMMY_OBJECT);
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public ConcurrentHashSet(Set<E> set) {
        this.concurrentHashMap = new ConcurrentHashMap<>();
        if (set instanceof NavigableSet) {
            concurrentHashMap.putAll(Maps.asMap((NavigableSet<E>) set, MIGRATION_FUNCTION::apply));
        } else if (set instanceof SortedSet) {
            concurrentHashMap.putAll(Maps.asMap((SortedSet<E>) set, MIGRATION_FUNCTION::apply));
        } else {
            concurrentHashMap.putAll(Maps.asMap(set, MIGRATION_FUNCTION::apply));
        }
    }

    public ConcurrentHashSet() {
        this(new ConcurrentHashMap<>());
    }

    public ConcurrentHashSet(Map<E, ?> map) {
        this.concurrentHashMap = new ConcurrentHashMap<>();
        if (map != null && !map.isEmpty()) {
            for (E e : map.keySet()) {
                this.concurrentHashMap.put(e, DUMMY_OBJECT);
            }
        }
    }

    /**
     * Removes e from the "set" via remove it from the ConcurrentHashMap
     *
     * @param o the element to remove to the "set"
     * @return the result of {@link ConcurrentHashMap#remove(Object)}
     */
    @Override
    public boolean remove(Object o) {
        return concurrentHashMap.remove(o);
    }

    /**
     * Adds e to the "set" via putting it into the ConcurrentHashMap with {@link ConcurrentHashSet#DUMMY_OBJECT}
     *
     * @param e the element to add to the "set"
     * @return the result of ConcurrentHashMap#put
     */
    @Override
    public boolean add(E e) {
        return concurrentHashMap.put(e, DUMMY_OBJECT) == null;
    }

    /**
     * This method uses the guava methods so it builds maps from different
     * collections easily using Maps#asMap & Maps#toMap with the value as {@link ConcurrentHashSet#DUMMY_OBJECT}.
     *
     * @param collection the collection to add to the "set"
     * @return constant true, no API to check this
     */
    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        if (collection instanceof Set) {
            if (concurrentHashMap instanceof NavigableSet) {
                concurrentHashMap.putAll(Maps.asMap((NavigableSet<E>) collection, MIGRATION_FUNCTION::apply));
            } else if (concurrentHashMap instanceof SortedSet) {
                concurrentHashMap.putAll(Maps.asMap((SortedSet<E>) collection, MIGRATION_FUNCTION::apply));
            } else {
                concurrentHashMap.putAll(Maps.asMap((Set<E>) collection, MIGRATION_FUNCTION::apply));
            }
        } else {
            concurrentHashMap.putAll(Maps.toMap(collection, MIGRATION_FUNCTION::apply));
        }
        return true;
    }

    /**
     * @return {@link ConcurrentHashMap.KeySetView#iterator()}
     */
    @Override
    public Iterator<E> iterator() {
        return concurrentHashMap.keySet().iterator();
    }

    /**
     * {@link ConcurrentHashMap.KeySetView#forEach(Consumer)}
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        concurrentHashMap.keySet().forEach(action);
    }

    /**
     * @return {@link ConcurrentHashMap.KeySetView#removeIf(Predicate)}
     */
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return concurrentHashMap.keySet().removeIf(filter);
    }

    /**
     * @return {@link ConcurrentHashMap.KeySetView#removeAll(Collection)}
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    /**
     * @return {@link ConcurrentHashMap.KeySetView#spliterator()}
     */
    @Override
    public Spliterator<E> spliterator() {
        return concurrentHashMap.keySet().spliterator();
    }

    /**
     * @return {@link ConcurrentHashMap.KeySetView#stream()}
     */
    @Override
    public Stream<E> stream() {
        return concurrentHashMap.keySet().stream();
    }

    /**
     * @return {@link ConcurrentHashMap.KeySetView#parallelStream()}
     */
    @Override
    public Stream<E> parallelStream() {
        return concurrentHashMap.keySet().parallelStream();
    }

    /**
     * @return {@link ConcurrentHashMap#size()}
     */
    @Override
    public int size() {
        return concurrentHashMap.size();
    }

    /**
     * @return {@link ConcurrentHashMap#containsKey(Object)} ()}
     */
    @Override
    public boolean contains(Object o) {
        return concurrentHashMap.containsKey(o);
    }

    /**
     * @return an iteration over {@link ConcurrentHashMap#containsKey(Object)} ()}
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!concurrentHashMap.containsKey(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return concurrentHashMap.toString();
    }

    @Override
    public int hashCode() {
        return concurrentHashMap.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return concurrentHashMap.equals(o);
    }

}
