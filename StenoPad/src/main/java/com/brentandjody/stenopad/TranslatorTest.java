package com.brentandjody.stenopad;

import android.test.AndroidTestCase;



public class TranslatorTest extends AndroidTestCase {

    private StenoDictionary dictionary;
    private boolean loaded;

    public void setUp() throws Exception {
        super.setUp();
        loaded = false;
        dictionary = new StenoDictionary(getContext());
        dictionary.load("test.json");
        dictionary.setOnDictionaryLoadedListener(new StenoDictionary.OnDictionaryLoadedListener() {
            @Override
            public void onDictionaryLoaded() {
                loaded=true;
                notifyAll();
            }
        });
        while (! loaded) {
            wait();
        }
    }

    public void isLoaded() throws Exception {
        assertTrue(loaded);
    }
}
