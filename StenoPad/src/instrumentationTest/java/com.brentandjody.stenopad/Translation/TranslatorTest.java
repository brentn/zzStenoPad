package com.brentandjody.stenopad;

import android.test.AndroidTestCase;

import com.brentandjody.stenopad.Translation.StenoDictionary;
import com.brentandjody.stenopad.Translation.Stroke;
import com.brentandjody.stenopad.Translation.Translation;
import com.brentandjody.stenopad.Translation.Translator;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class TranslatorTest extends AndroidTestCase implements Translation.TranslationPlayer{

    private StenoDictionary dictionary;
    private Translator translator;
    private boolean loaded = false;
    private List<Translation> test_undo, test_play;
    private Translation test_state;
    private String test_staging = "";

    public void setUp() throws Exception {
        super.setUp();
        if (! loaded ) {
            final CountDownLatch latch = new CountDownLatch(1);
            dictionary = new StenoDictionary(getContext());
            dictionary.load("test.json");
            dictionary.setOnDictionaryLoadedListener(new StenoDictionary.OnDictionaryLoadedListener() {
                @Override
                public void onDictionaryLoaded() {
                    latch.countDown();
                }
            });
            latch.await();
            loaded = true;
        }
        translator = new Translator(dictionary);
    }

    public void playTranslation(List<Translation> undo, List<Translation> play, Translation state, String staging) {
        if (undo != null) {
            test_undo = new LinkedList<Translation>();
            for (Translation t : undo) {
                test_undo.add(t);
            }
        } else {
            test_undo = null;
        }
        if (play != null) {
            test_play = new LinkedList<Translation>();
            for (Translation t : play) {
                test_play.add(t);
            }
        } else {
            test_play = null;
        }
        if (state!=null) {
            test_state = state.copy();
        } else {
            test_state = null;
        }
        test_staging = staging;
    }

    public void testIsLoaded() throws Exception {
        assertTrue(loaded);
    }

    public void testTranslate() throws Exception {
        assertNull(test_undo);
        assertNull(test_play);
        assertNull(test_state);
        assertEquals("", test_staging);
        translator.translate(new Stroke("*"), this);
        assertEquals(0, test_undo.size());
        assertEquals(0, test_play.size());
        assertNull(test_state);
        assertEquals("", test_staging);
        translator.translate(new Stroke("AD"), this);
        assertEquals(0, test_undo.size());
        assertEquals(0, test_play.size());
        assertNull(test_state);
        assertEquals("AD", test_staging);
        translator.translate(new Stroke("SKWRAEUS"), this);
        assertEquals(0, test_undo.size());
        assertEquals(0, test_play.size());
        assertNull(test_state);
        assertEquals("AD/SKWRAEUS", test_staging);
        translator.translate(new Stroke("EPBT"), this);
        assertEquals(0, test_undo.size());
        assertEquals(1, test_play.size());
        assertEquals("adjacent", test_play.get(0).english());
        assertNull(test_state);
        assertEquals("", test_staging);
        translator.translate(new Stroke("AD"), this);
        assertEquals(0, test_undo.size());
        assertEquals(0, test_play.size());
        assertEquals("adjacent", test_state.english());
        assertEquals("AD", test_staging);
        translator.translate(new Stroke("SKWRAOUR"), this);
        assertEquals(0, test_undo.size());
        assertEquals("adjure", test_play.get(0).english());
        assertEquals("adjacent", test_state.english());
        assertEquals("", test_staging);
        //TODO: more testing
    }
}
