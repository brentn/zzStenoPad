package com.brentandjody.stenopad.Translation.tests;

import android.test.AndroidTestCase;

import com.brentandjody.stenopad.Display.DisplayDevice;
import com.brentandjody.stenopad.Display.DisplayItem;
import com.brentandjody.stenopad.Translation.Dictionary;
import com.brentandjody.stenopad.Translation.Stroke;
import com.brentandjody.stenopad.Translation.Translator;

import java.util.concurrent.CountDownLatch;

public class TranslatorTest extends AndroidTestCase implements DisplayDevice.Display {

    private Dictionary dictionary;
    private Translator translator;
    private boolean loaded = false;
    private int backspaces = 0;
    private String text = "";
    private String preview = "";

    public void setUp() throws Exception {
        super.setUp();
        if (! loaded ) {
            final CountDownLatch latch = new CountDownLatch(1);
            dictionary = new Dictionary(getContext());
            dictionary.load("test.json");
            dictionary.setOnDictionaryLoadedListener(new Dictionary.OnDictionaryLoadedListener() {
                @Override
                public void onDictionaryLoaded() {
                    latch.countDown();
                }
            });
            latch.await();
            loaded = true;
        }
        translator = new Translator(dictionary);
        translator.USE_SUFFIX_FOLDING=true;
    }

    public void update(DisplayItem item, String preview) {
        backspaces = item.getBackspaces();
        text = item.getText();
        this.preview = preview;
    }

    public void testIsLoaded() throws Exception {
        assertTrue(loaded);
    }

    public void testTranslate() throws Exception {
        assertEquals(0, backspaces);
        assertEquals("", text);
        assertEquals("", preview);
        translator.translate(new Stroke("*"), this);
        assertEquals(0, backspaces);
        assertEquals("", text);
        assertEquals("", preview);
        translator.translate(new Stroke("AD"), this);
        assertEquals(0, backspaces);
        assertEquals("", text);
        assertEquals("AD", preview);
        translator.translate(new Stroke("SKWRAEUS"), this);
        assertEquals(0, backspaces);
        assertEquals("", text);
        assertEquals("AD/SKWRAEUS", preview);
        translator.translate(new Stroke("EPBT"), this);
        assertEquals(0, backspaces);
        assertEquals("adjacent ", text);
        assertEquals("", preview);
        translator.translate(new Stroke("AD"), this);
        assertEquals(0, backspaces);
        assertEquals("", text);
        assertEquals("AD", preview);
        translator.translate(new Stroke("SKWRAOUR"), this);
        assertEquals(0, backspaces);
        assertEquals("adjure ", text);
        assertEquals("", preview);
        translator.translate(new Stroke("*"), this);
        assertEquals(7, backspaces);
        assertEquals("", text);
        assertEquals("AD", preview);
        translator.translate(new Stroke("R-R"), this);
        assertEquals(0, backspaces);
        assertEquals("AD \n ", text);
        assertEquals("", preview);
        translator.translate(new Stroke("AD"), this);
        assertEquals(0, backspaces);
        assertEquals("", text);
        assertEquals("AD", preview);
        translator.translate(new Stroke("SKWRUPLTS"), this);
        assertEquals("adjustments ", text);
        assertEquals("", preview);
        translator.translate(new Stroke("PW-FP"), this);
        assertEquals(1, backspaces);
        assertEquals("", text);
        assertEquals("", preview);
        //numbers
        translator.translate(new Stroke("#T-PL"), this);
        assertEquals("278 ", text);
        //suffix folding
        translator.translate(new Stroke("AEF"), this);
        assertEquals("avenue ", text);
        translator.translate(new Stroke("AEFD"), this);
        assertEquals("avenueed ", text);
        translator.translate(new Stroke("AEPG"), this);
        assertEquals("appealing ", text);
        // some difficult cases
        translator.translate(new Stroke("S"), this);
        assertEquals("", text);
        assertEquals("is ", preview);
        translator.translate(new Stroke("AUL"), this);
        assertEquals("", text);
        assertEquals("S/AUL", preview);
        translator.translate(new Stroke("S-P"), this);
        assertEquals("is all ", text);
        assertEquals("", preview);
        translator.translate(new Stroke("TPAOEUPBD"), this);
        assertEquals("find ", preview);
        translator.translate(new Stroke("-G"), this);
        assertEquals("finding ", text);

        //TODO: more testing
    }
}
