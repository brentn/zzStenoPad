package com.brentandjody.stenopad;

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.HashSet;


public class StrokeTest extends AndroidTestCase {


    public void setUp() throws Exception {
        super.setUp();
    }

    public void testConstructor() throws Exception {
        HashSet<String> keys = new HashSet<String>();
        Stroke s = new Stroke(keys);
        assertEquals("", s.rtfcre());
        assertEquals("Stroke( : )", s.toString());
        assertFalse(s.isCorrection());
        s = new Stroke("");
        assertEquals("", s.rtfcre());
        assertEquals("Stroke( : )", s.toString());
        assertFalse(s.isCorrection());
        keys.add("R-");
        s = new Stroke(keys);
        assertEquals("R", s.rtfcre());
        assertEquals("Stroke(R : R-)", s.toString());
        assertFalse(s.isCorrection());
        s = new Stroke("R");
        assertEquals("R", s.rtfcre());
        assertEquals("Stroke(R : R)", s.toString());
        assertFalse(s.isCorrection());
        keys.add("A-"); keys.add("-P");
        s = new Stroke(keys);
        assertEquals("RAP", s.rtfcre());
        assertEquals("Stroke(RAP : -PA-R-)", s.toString());
        s = new Stroke("PAUBP");
        assertEquals("PAUPB", s.rtfcre());
        assertEquals("Stroke(PAUPB : PAUBP)", s.toString());
        s = new Stroke("*");
        assertEquals("*", s.rtfcre());
        assertEquals("*Stroke(* : *)", s.toString());
        assertTrue(s.isCorrection());
    }

    public void testCombine() throws Exception {
        Stroke[] nostroke = new Stroke[0];
        Stroke a = new Stroke(new HashSet<String>() {{add("S-");add("T-"); add("P-"); add("H-");}});
        Stroke b = new Stroke(new HashSet<String>() {{add("-F"); add("-P"); add("-L"); add("-T"); add("-D");}});
        Stroke[] ab = new Stroke[2]; ab[0]=a;ab[1]=b;
        assertEquals("", Stroke.combine(nostroke));
        assertEquals("STPH/-FPLTD", Stroke.combine(ab));
    }

    public void testSeparate() throws Exception {
        Stroke stph = new Stroke(new HashSet<String>() {{add("S-");add("T-"); add("P-"); add("H-");}});
        Stroke rbg = new Stroke(new HashSet<String>() {{add("-R");add("-B");add("-G");}});
        Stroke[] result = new Stroke[1]; result[0] = stph;
        assertNull(Stroke.separate(""));
        assertTrue(Arrays.equals(Stroke.separate("STPH"), result));
        result = new Stroke[2]; result[0]=rbg; result[1]=stph;
        assertTrue(Arrays.equals(Stroke.separate("-RBG/STPH"), result));
    }

    public void testEquals() throws Exception {
        Stroke s1 = new Stroke(new HashSet<String>() {{add("S-");add("T-"); add("P-"); add("H-");}});
        Stroke s2 = new Stroke(new HashSet<String>() {{add("S-");add("P-"); add("H-"); add("T-");}});
        Stroke s3 = new Stroke(new HashSet<String>() {{add("S-");add("T-"); add("P-"); add("H-"); add("-R");}});
        assertTrue(s1.equals(s1));
        assertFalse(s2.equals(null));
        assertTrue(s1.equals(s2));
        assertTrue(s2.equals(s1));
        assertFalse(s2.equals(s3));
        assertFalse(s3.equals(s2));
    }

    public void testAsArray() throws Exception {
        Stroke s1 = new Stroke(new HashSet<String>() {{add("S-");add("T-"); add("P-"); add("H-");}});
        Stroke[] result = new Stroke[1]; result[0]=s1;
        assertEquals("STPH", s1.rtfcre());
        assertTrue(Arrays.equals(result, s1.asArray()));
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
