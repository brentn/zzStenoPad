package com.brentandjody.stenopad;

import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Set;


public class StrokeTest extends AndroidTestCase {


    public void setUp() throws Exception {
        super.setUp();
    }

    public void testNormalize() throws Exception {
        assertEquals("S", Stroke.normalize("S"));
        assertEquals("S", Stroke.normalize("S-"));
        assertEquals("ES", Stroke.normalize("ES"));
        assertEquals("ES", Stroke.normalize("-ES"));
        assertEquals("TWEPBL", Stroke.normalize("TW-EPBL"));
        assertEquals("TWEPBL", Stroke.normalize("TWEPBL"));
    }

    public void testSteno() throws Exception {
        assertEquals("S", new Stroke(new HashSet<String>() {{add("S-");}}).rtfcre());
        assertEquals("ST", new Stroke(new HashSet<String>() {{add("S-"); add("T-");}}).rtfcre());
        assertEquals("ST", new Stroke(new HashSet<String>() {{add("T-"); add("S-");}}).rtfcre());
        assertEquals("-P", new Stroke(new HashSet<String>() {{add("-P"); add("-P");}}).rtfcre());
        assertEquals("-P", new Stroke(new HashSet<String>() {{add("-P"); add("X-");}}).rtfcre());
    }

}
