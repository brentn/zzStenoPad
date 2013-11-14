package com.brentandjody.stenopad;

import android.test.AndroidTestCase;


public class StrokeTest extends AndroidTestCase {

    private Stroke stroke;

    public void setUp() throws Exception {
        super.setUp();
        stroke = new Stroke();
    }

    public void testNormalize() throws Exception {
        Stroke stroke = new Stroke();
        assertEquals("S", stroke.normalize("S")[0]);
        assertEquals("S", stroke.normalize("S-")[0]);
        assertEquals("ES", stroke.normalize("ES")[0]);
        assertEquals("ES", stroke.normalize("-ES")[0]);
        assertEquals("TWEPBL", stroke.normalize("TW-EPBL")[0]);
        assertEquals("TWEPBL", stroke.normalize("TWEPBL")[0]);
    }

//
//    def test_steno(self):
//            self.assertEqual(Stroke(['S-']).rtfcre, 'S')
//            self.assertEqual(Stroke(['S-', 'T-']).rtfcre, 'ST')
//            self.assertEqual(Stroke(['T-', 'S-']).rtfcre, 'ST')
//            self.assertEqual(Stroke(['-P', '-P']).rtfcre, '-P')
//            self.assertEqual(Stroke(['-P', 'X-']).rtfcre, 'X-P')

}
