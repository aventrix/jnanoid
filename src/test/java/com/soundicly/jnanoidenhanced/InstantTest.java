package com.soundicly.jnanoidenhanced;

import com.soundicly.jnanoidenhanced.jnanoid.MathUtils;
import com.soundicly.jnanoidenhanced.jnanoid.NanoIdUtils;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author fishzhao
 * @since 2022-05-06
 */
public class InstantTest {

    private static <T> T instant(Class<? extends T> type) throws Exception {
        final Constructor<? extends T> declaredConstructor = type.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        return declaredConstructor.newInstance();
    }

    @Test(expected = InvocationTargetException.class)
    public void MathUtils_Instant_ExceptionThrown() throws Exception {
        instant(MathUtils.class);
    }

    @Test(expected = InvocationTargetException.class)
    public void NanoIdUtils_Instant_ExceptionThrown() throws Exception {
        instant(NanoIdUtils.class);
    }
}
