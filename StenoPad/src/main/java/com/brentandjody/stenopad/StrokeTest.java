package com.brentandjody.stenopad;

import android.test.AndroidTestCase;


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

//
//    def test_steno(self):
//            self.assertEqual(Stroke(['S-']).rtfcre, 'S')
//            self.assertEqual(Stroke(['S-', 'T-']).rtfcre, 'ST')
//            self.assertEqual(Stroke(['T-', 'S-']).rtfcre, 'ST')
//            self.assertEqual(Stroke(['-P', '-P']).rtfcre, '-P')
//            self.assertEqual(Stroke(['-P', 'X-']).rtfcre, 'X-P')

}
