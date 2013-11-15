package com.brentandjody.stenopad;

import android.test.AndroidTestCase;

import java.util.HashSet;


public class TranslationTest extends AndroidTestCase {

    private static final Stroke S = new Stroke(new HashSet<String>() {{add("S");}});
    private static final Stroke T = new Stroke(new HashSet<String>() {{add("T");}});
    private static final Stroke[] ST =  {S, T};
    private static final String[] ST_RTFCRE = {"S","T"};

    private StenoDictionary dictionary;

    public void setUp() throws Exception {
        super.setUp();
        if (dictionary == null) {
            dictionary = new StenoDictionary(getContext());
            dictionary.load("test.json");
        }
    }

    public void testNoTranslation() throws Exception {
        Translation t = new Translation(ST, null);
        assertEquals(ST, t.strokes());
        assertEquals(ST_RTFCRE, t.rtfcre());
        assertNull(t.english());
    }

    public void testTranslation() throws Exception {
        Translation t = new Translation(ST, "translation");
        assertEquals(ST, t.strokes());
        assertEquals(ST_RTFCRE, t.rtfcre());
        assertEquals("translation", t.english());
    }

}