package com.brentandjody.stenopad;

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.HashSet;


public class TranslationTest extends AndroidTestCase {

    private static final Stroke S = new Stroke(new HashSet<String>() {{add("S-");}});
    private static final Stroke T = new Stroke(new HashSet<String>() {{add("T-");}});

    private StenoDictionary dictionary;
    private Stroke[] strokeResult;
    private String[] rtfcreResult;

    public void setUp() throws Exception {
        super.setUp();
        if (dictionary == null) {
            dictionary = new StenoDictionary(getContext());
            dictionary.load("test.json");
        }
        strokeResult = new Stroke[2]; strokeResult[0]=S; strokeResult[1]=T;
    }

    public void testNoTranslation() throws Exception {
        Translation t = new Translation(strokeResult, null);
        assertTrue(Arrays.equals(strokeResult, t.strokes()));
        assertEquals("S/T", t.rtfcre());
        assertNull(t.english());
    }

    public void testTranslation() throws Exception {
        Translation t = new Translation(strokeResult, "translation");
        assertTrue(Arrays.equals(strokeResult, t.strokes()));
        assertEquals("S/T", t.rtfcre());
        assertEquals("translation", t.english());
    }

}