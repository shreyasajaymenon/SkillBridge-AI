package com.skillbridge.algorithm;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.ToDoubleFunction;

/**
 * In-place randomized kth-largest selection using a three-way partition.
 * k is one-based: k=1 returns the largest element.
 *
 * Expected time: O(n). Worst-case time: O(n^2).
 * Auxiliary space: O(1) for the iterative selection itself.
 */
public final class RandomizedQuickselect {
    private RandomizedQuickselect() {}

    public static <T> double selectKthLargest(
            List<T> values, int k, ToDoubleFunction<T> scoreFunction) {
        return selectKthLargest(values, k, scoreFunction, new Random());
    }

    public static <T> double selectKthLargest(
            List<T> values, int k, ToDoubleFunction<T> scoreFunction, Random random) {
        Objects.requireNonNull(values, "values must not be null");
        Objects.requireNonNull(scoreFunction, "scoreFunction must not be null");
        Objects.requireNonNull(random, "random must not be null");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Cannot select from an empty list");
        }
        if (k < 1 || k > values.size()) {
            throw new IllegalArgumentException("k must be between 1 and list size");
        }

        int targetIndex = k - 1; // descending order
        int low = 0;
        int high = values.size() - 1;

        while (low <= high) {
            double pivot = scoreFunction.applyAsDouble(values.get(low + random.nextInt(high - low + 1)));
            int lessStart = low;
            int scan = low;
            int greaterEnd = high;

            // Partition into: scores > pivot | scores == pivot | scores < pivot.
            while (scan <= greaterEnd) {
                double current = scoreFunction.applyAsDouble(values.get(scan));
                int comparison = Double.compare(current, pivot);
                if (comparison > 0) {
                    swap(values, lessStart++, scan++);
                } else if (comparison < 0) {
                    swap(values, scan, greaterEnd--);
                } else {
                    scan++;
                }
            }

            if (targetIndex < lessStart) {
                high = lessStart - 1;
            } else if (targetIndex > greaterEnd) {
                low = greaterEnd + 1;
            } else {
                return pivot;
            }
        }
        throw new IllegalStateException("Selection failed unexpectedly");
    }

    private static <T> void swap(List<T> values, int first, int second) {
        if (first == second) return;
        T temporary = values.get(first);
        values.set(first, values.get(second));
        values.set(second, temporary);
    }
}
