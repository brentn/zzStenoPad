package com.brentandjody.stenopad;

import android.test.AndroidTestCase;
import junit.framework.Assert;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class StenoDictionaryTest extends AndroidTestCase {
    private StenoDictionary dictionary;

    public void setUp() throws Exception {
        super.setUp();
        dictionary = new StenoDictionary(getContext());
    }

    public void testIsLoading() throws Exception {
        assertFalse(dictionary.isLoading());
        dictionary.load("dict.json");
        assertTrue(dictionary.isLoading());
        dictionary.setOnDictionaryLoadedListener(new StenoDictionary.OnDictionaryLoadedListener() {
            @Override
            public void onDictionaryLoaded() {
                assertFalse(dictionary.isLoading());
            }
        });
    }

    public void testBadFilename() throws Exception {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        dictionary.load("booga.json");
        assertEquals("Dictionary File: booga.json could not be found", errContent.toString().trim());
    }

    public void testIllegalFileType() throws Exception{
        try {
            dictionary.load("test.rtf");
            Assert.fail("Illegal file type");
        } catch (Exception e) {
        }
    }

    public void testLoadAndClear() throws Exception {
        assertEquals(0, dictionary.size());
        dictionary.load("test.json");
        dictionary.setOnDictionaryLoadedListener(new StenoDictionary.OnDictionaryLoadedListener() {
            @Override
            public void onDictionaryLoaded() {
                int size = dictionary.size();
                assertTrue(size > 0);
                dictionary.clear();
                assertEquals(0, dictionary.size());
            }
        });
    }

    public void testLoad2Dictionaries() throws Exception{
        //TODO:
    }

    public void testOverrideEntries() throws Exception{
        //TODO:
    }

    public void testLookupAndForceLookup() throws Exception {
        dictionary.load("test.json");
        dictionary.setOnDictionaryLoadedListener(new StenoDictionary.OnDictionaryLoadedListener() {
            @Override
            public void onDictionaryLoaded() {
                //TODO:
            }
        });
    }

    public void testLongestValidStroke() {
        //TODO:
    }


}
