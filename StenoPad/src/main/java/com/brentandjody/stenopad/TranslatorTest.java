package com.brentandjody.stenopad;

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


public class TranslatorTest extends AndroidTestCase implements Translation.TranslationPlayer{

    private StenoDictionary dictionary;
    private Translator translator;
    private boolean loaded = false;
    private List<Translation> undo, play;
    private Translation state;
    private String staging = "";

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
        this.undo = undo;
        this.play = play;
        this.state = state;
        this.staging = staging;
    }

    public void testIsLoaded() throws Exception {
        assertTrue(loaded);
    }

    public void testTranslate() throws Exception {
        assertNull(undo);
        assertNull(play);
        assertNull(state);
        assertEquals("", staging);
        translator.translate(new Stroke("*"), this);
        assertEquals(0, undo.size());
        assertEquals(0, play.size());
        assertNull(state);
        assertEquals("", staging);
        translator.translate(new Stroke("AD"), this);
        assertEquals(0, undo.size());
        assertEquals(0, play.size());
        assertNull(state);
        assertEquals("AD", staging);
        translator.translate(new Stroke("SKWRAEUS"), this);
        assertEquals(0, undo.size());
        assertEquals(0, play.size());
        assertNull(state);
        assertEquals("AD/SKWRAEUS", staging);
        translator.translate(new Stroke("EPBT"), this);
        assertEquals(0, undo.size());
        assertEquals("adjacent", play.get(0).english());
        assertNull(state);
        assertEquals("", staging);
        translator.translate(new Stroke("AD"), this);
        assertEquals(0, undo.size());
        assertEquals(0, play.size());
        assertEquals("adjacent", state.english());
        assertEquals("AD", staging);
        translator.translate(new Stroke("SKWRAOUR"), this);
        assertEquals(0, undo.size());
        assertEquals("adjure", play.get(0).english());
        assertEquals("adjacent", state.english());
        assertEquals("",staging);
    }
}
