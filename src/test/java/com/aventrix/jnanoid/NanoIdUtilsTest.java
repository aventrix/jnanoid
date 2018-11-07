/**
 * Copyright (c) 2017 The JNanoID Authors
 * Copyright (c) 2017 Aventrix LLC
 * Copyright (c) 2017 Andrey Sitnik
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.aventrix.jnanoid;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for NanoIdUtils.
 *
 * @author David Klebanoff
 * @see NanoIdUtils
 */
public class NanoIdUtilsTest {


    @Test
    public void NanoIdUtils_VerifyClassIsFinal_Verified() {
        if ((NanoIdUtils.class.getModifiers() & Modifier.FINAL) != Modifier.FINAL) {
            fail("The class is not final");
        }
    }

    @Test
    public void NanoIdUtils_VerifyConstructorsArePrivate_Verified() {
        for (final Constructor<?> constructor : NanoIdUtils.class.getConstructors()) {
            if ((constructor.getModifiers() & Modifier.PRIVATE) != Modifier.PRIVATE) {
                fail("The class has a non-private constructor.");
            }
        }
    }

    @Test
    public void NanoIdUtils_Verify100KRandomNanoIdsAreUnique_Verified() {

        //It's not much, but it's a good sanity check I guess.
        final int idCount = 100000;
        final Set<String> ids = new HashSet<>(idCount);

        for (int i = 0; i < idCount; i++) {
            final String id = NanoIdUtils.randomNanoId();
            if (ids.contains(id) == false) {
                ids.add(id);
            } else {
                fail("Non-unique ID generated: " + id);
            }
        }

    }

    @Test
    public void NanoIdUtils_SeededRandom_Success() {

        //With a seed provided, we can know which IDs to expect, and subsequently verify that the
        // provided random number generator is being used as expected.
        final Random random = new Random(12345);

        final char[] alphabet =
                ("_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

        final int size = 21;

        final String[] expectedIds = new String[] {"kutqLNv1wDmIS56EcT3j7", "U497UttnWzKWWRPMHpLD7",
                "7nj2dWW1gjKLtgfzeI8eC", "I6BXYvyjszq6xV7L9k2A9", "uIolcQEyyQIcn3iM6Odoa" };

        for (final String expectedId : expectedIds) {
            final String generatedId = NanoIdUtils.randomNanoId(random, alphabet, size);
            assertEquals(expectedId, generatedId);
        }

    }

    @Test
    public void NanoIdUtils_VariousAlphabets_Success() {

        //Test ID generation with various alphabets consisting of 1 to 255 unique symbols.
        for (int symbols = 1; symbols <= 255; symbols++) {

            final char[] alphabet = new char[symbols];
            for (int i = 0; i < symbols; i++) {
                alphabet[i] = (char) i;
            }

            final String id = NanoIdUtils
                    .randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet,
                            NanoIdUtils.DEFAULT_SIZE);

            //Create a regex pattern that only matches to the characters in the alphabet
            final StringBuilder patternBuilder = new StringBuilder();
            patternBuilder.append("^[");
            for (final char character : alphabet) {
                patternBuilder.append(Pattern.quote(String.valueOf(character)));
            }
            patternBuilder.append("]+$");

            assertTrue(id.matches(patternBuilder.toString()));
        }

    }

    @Test
    public void NanoIdUtils_VariousSizes_Success() {

        //Test ID generation with all sizes between 1 and 1,000.
        for (int size = 1; size <= 1000; size++) {

            final String id = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                    NanoIdUtils.DEFAULT_ALPHABET, size);

            assertEquals(size, id.length());
        }

    }

    @Test
    public void NanoIdUtils_WellDistributed_Success() {

        //Test if symbols in the generated IDs are well distributed.

        final int idCount = 100000;
        final int idSize = 20;
        final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        final Map<String, Long> charCounts = new HashMap<>();

        for (int i = 0; i < idCount; i++) {

            final String id = NanoIdUtils
                    .randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet, idSize);

            for (int j = 0; j < id.length(); j++) {
                final String value = String.valueOf(id.charAt(j));

                final Long charCount = charCounts.get(value);
                if (charCount == null) {
                    charCounts.put(value, 1L);
                } else {
                    charCounts.put(value, charCount + 1);
                }
            }
        }

        //Verify the distribution of characters is pretty even
        for (final Long charCount : charCounts.values()) {
            final double distribution = (charCount * alphabet.length / (double) (idCount * idSize));
            assertThat(distribution, Matchers.closeTo(1.0, 0.05));
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void randomNanoId_NullRandom_ExceptionThrown() {
        NanoIdUtils.randomNanoId(null, new char[] {'a', 'b', 'c'}, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void randomNanoId_NullAlphabet_ExceptionThrown() {
        NanoIdUtils.randomNanoId(new SecureRandom(), null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void randomNanoId_EmptyAlphabet_ExceptionThrown() {
        NanoIdUtils.randomNanoId(new SecureRandom(), new char[] {}, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void randomNanoId_256Alphabet_ExceptionThrown() {

        //The alphabet is composed of 256 unique characters
        final char[] largeAlphabet = new char[256];
        for (int i = 0; i < 256; i++) {
            largeAlphabet[i] = (char) i;
        }

        NanoIdUtils.randomNanoId(new SecureRandom(), largeAlphabet, 20);

    }

    @Test(expected = IllegalArgumentException.class)
    public void randomNanoId_NegativeSize_ExceptionThrown() {
        NanoIdUtils.randomNanoId(new SecureRandom(), new char[] {'a', 'b', 'c'}, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void randomNanoId_ZeroSize_ExceptionThrown() {
        NanoIdUtils.randomNanoId(new SecureRandom(), new char[] {'a', 'b', 'c'}, 0);
    }

}
