/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.soundicly.jnanoidenhanced.jnanoid;

import java.math.RoundingMode;

/**
 * Methods to perform fast log
 */
public final class MathUtils {

    /**
     * The biggest half power of two that can fit in an unsigned int.
     */
    private static final int MAX_POWER_OF_SQRT2_UNSIGNED = 0xB504F333;

    private MathUtils() {
        throw new IllegalStateException();
    }

    /**
     * Returns the base-2 logarithm of {@code x}, rounded according to the specified rounding mode.
     *
     * @throws IllegalArgumentException if {@code x <= 0}
     * @throws ArithmeticException      if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code x}
     *                                  is not a power of two
     */
    @SuppressWarnings("fallthrough")
    public static int log2(int x, RoundingMode mode) {
        checkPositive("x", x);
        switch (mode) {
            case UNNECESSARY:
                checkRoundingUnnecessary(isPowerOfTwo(x));
                // fall through
            case DOWN:
            case FLOOR:
                return (Integer.SIZE - 1) - Integer.numberOfLeadingZeros(x);

            case UP:
            case CEILING:
                return Integer.SIZE - Integer.numberOfLeadingZeros(x - 1);

            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                // Since sqrt(2) is irrational, log2(x) - logFloor cannot be exactly 0.5
                int leadingZeros = Integer.numberOfLeadingZeros(x);
                int cmp = MAX_POWER_OF_SQRT2_UNSIGNED >>> leadingZeros;
                // floor(2^(logFloor + 0.5))
                int logFloor = (Integer.SIZE - 1) - leadingZeros;
                return logFloor + lessThanBranchFree(cmp, x);

            default:
                throw new AssertionError();
        }
    }

    private static void checkRoundingUnnecessary(boolean condition) {
        if (!condition) {
            throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
        }
    }

    /**
     * Returns {@code true} if {@code x} represents a power of two.
     *
     * <p>This differs from {@code Integer.bitCount(x) == 1}, because {@code
     * Integer.bitCount(Integer.MIN_VALUE) == 1}, but {@link Integer#MIN_VALUE} is not a power of two.
     */
    public static boolean isPowerOfTwo(int x) {
        return x > 0 & (x & (x - 1)) == 0;
    }

    /**
     * Returns 1 if {@code x < y} as unsigned integers, and 0 otherwise. Assumes that x - y fits into
     * a signed int. The implementation is branch-free, and benchmarks suggest it is measurably (if
     * narrowly) faster than the straightforward ternary expression.
     */
    private static int lessThanBranchFree(int x, int y) {
        // The double negation is optimized away by normal Java, but is necessary for GWT
        // to make sure bit twiddling works as expected.
        return ~~(x - y) >>> (Integer.SIZE - 1);
    }

    @SuppressWarnings("SameParameterValue")
    private static void checkPositive(String role, int x) {
        if (x <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
    }

}
