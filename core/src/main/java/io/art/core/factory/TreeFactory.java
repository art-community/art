package io.art.core.factory;

import io.art.core.collection.*;
import lombok.experimental.*;
import static io.art.core.checker.EmptinessChecker.*;
import static java.util.Arrays.*;
import java.util.*;

@UtilityClass
public class TreeFactory {
    public static <T> Set<T> tree(Comparator<T> comparator) {
        return new TreeSet<>(comparator);
    }

    @SafeVarargs
    public static <T> Set<T> treeOf(Comparator<T> comparator, T... elements) {
        TreeSet<T> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        treeSet.addAll(asList(elements));
        return treeSet;
    }

    public static <T> Set<T> treeOf(Collection<T> elements, Comparator<T> comparator) {
        TreeSet<T> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        treeSet.addAll(elements);
        return treeSet;
    }

    public static <T> Set<T> treeOf(ImmutableSet<T> elements, Comparator<T> comparator) {
        return isEmpty(elements) ? tree(comparator) : treeOf(elements.toMutable(), comparator);
    }

    public static <T> Set<T> treeOf(ImmutableArray<T> elements, Comparator<T> comparator) {
        return isEmpty(elements) ? tree(comparator) : treeOf(elements.toMutable(), comparator);
    }

    public static Set<Long> treeOf(Comparator<Long> comparator, long[] elements) {
        TreeSet<Long> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        for (long element : elements) treeSet.add(element);
        return treeSet;
    }

    public static Set<Integer> treeOf(Comparator<Integer> comparator, int[] elements) {
        TreeSet<Integer> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        for (int element : elements) treeSet.add(element);
        return treeSet;
    }

    public static Set<Byte> treeOf(Comparator<Byte> comparator, byte[] elements) {
        TreeSet<Byte> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        for (byte element : elements) treeSet.add(element);
        return treeSet;
    }

    public static Set<Double> treeOf(Comparator<Double> comparator, double[] elements) {
        TreeSet<Double> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        for (double element : elements) treeSet.add(element);
        return treeSet;
    }

    public static Set<Float> treeOf(Comparator<Float> comparator, float[] elements) {
        TreeSet<Float> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        for (float element : elements) treeSet.add(element);
        return treeSet;
    }

    public static Set<Boolean> treeOf(Comparator<Boolean> comparator, boolean[] elements) {
        TreeSet<Boolean> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        for (boolean element : elements) treeSet.add(element);
        return treeSet;
    }

    public static Set<Short> treeOf(Comparator<Short> comparator, short[] elements) {
        TreeSet<Short> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        for (short element : elements) treeSet.add(element);
        return treeSet;
    }

    public static Set<Character> treeOf(Comparator<Character> comparator, char[] elements) {
        TreeSet<Character> treeSet = new TreeSet<>(comparator);
        if (isEmpty(elements)) return treeSet;
        for (char element : elements) treeSet.add(element);
        return treeSet;
    }
}
