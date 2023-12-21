package com.soundicly.jnanoidenhanced;

import com.soundicly.jnanoidenhanced.jnanoid.MathUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author fishzhao
 * @since 2022-05-06
 */
public class MathUtilsTest {

    private static int slowLog2(int x, RoundingMode roundingMode) {
        double value = Math.log(x) / Math.log(2);
        return BigDecimal.valueOf(value).setScale(0, roundingMode).intValue();
    }

    @Test
    public void MathUtils_isPowerOf2_Verified() {
        assertTrue(MathUtils.isPowerOfTwo(1));
        assertTrue(MathUtils.isPowerOfTwo(2));
        assertTrue(MathUtils.isPowerOfTwo(4));
        assertTrue(MathUtils.isPowerOfTwo(256));
        assertTrue(MathUtils.isPowerOfTwo(1 << 15));

        assertFalse(MathUtils.isPowerOfTwo(3));
        assertFalse(MathUtils.isPowerOfTwo(5));
        assertFalse(MathUtils.isPowerOfTwo(456721));
        assertFalse(MathUtils.isPowerOfTwo(-99));
    }

    @Test
    public void MathUtils_log2_Verified() {
        final RoundingMode[] roundingModes = RoundingMode.values();
        for (int i = 2; i < 256; ++i) {
            for (RoundingMode roundingMode : roundingModes) {
                if (roundingMode != RoundingMode.UNNECESSARY)
                    assertEquals(slowLog2(i, roundingMode), MathUtils.log2(i, roundingMode));
            }
        }
        for (int i = 0; i < Integer.SIZE - 1; ++i) {
            assertEquals(i, MathUtils.log2(1 << i, RoundingMode.UNNECESSARY));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void log2_negativeNumber_ExceptionThrown() {
        MathUtils.log2(-99, RoundingMode.FLOOR);
    }

    @Test(expected = ArithmeticException.class)
    public void log2_roundingMode_ExceptionThrown() {
        MathUtils.log2(1821, RoundingMode.UNNECESSARY);
    }
}
