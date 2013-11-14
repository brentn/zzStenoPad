package com.brentandjody.stenopad;

import android.test.AndroidTestCase;

import junit.framework.TestCase;


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

    public void testLoadAndClear() throws Exception {
        assertEquals(0, dictionary.size());
        dictionary.load("test.json");
        dictionary.setOnDictionaryLoadedListener(new StenoDictionary.OnDictionaryLoadedListener() {
            @Override
            public void onDictionaryLoaded() {
                assertTrue(dictionary.size() > 0);
                dictionary.clear();
                assertEquals(0, dictionary.size());
            }
        });
    }

}
