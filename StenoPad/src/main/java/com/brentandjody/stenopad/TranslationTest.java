package com.brentandjody.stenopad;

import android.test.AndroidTestCase;


public class TranslationTest extends AndroidTestCase {

    private StenoDictionary dictionary;

    public void setUp() throws Exception {
        super.setUp();
        if (dictionary == null) {
            dictionary = new StenoDictionary(getContext());
            dictionary.load("test.json");
        }
    }


}