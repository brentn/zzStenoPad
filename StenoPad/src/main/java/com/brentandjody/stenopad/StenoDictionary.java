package com.brentandjody.stenopad;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;


public class StenoDictionary {

    private static final String[] DICTIONARY_TYPES = {".json", ".rtf"};

    Dictionary dictionary;
    Context context;
    Boolean loading = false;

    public StenoDictionary(Context c) {
        context = c;
        dictionary = new Dictionary();
    }

    public Boolean isLoading() {
        return loading;
    }

    public void clear() {
        dictionary = new Dictionary();
    }

    public int size() {
        return dictionary.size();
    }

    public void load(String filename) {
        String extension = filename.substring(filename.lastIndexOf("."));
        if (Arrays.asList(DICTIONARY_TYPES).contains(extension)) {
            try {
                InputStream stream = context.getAssets().open(filename);
            } catch (IOException e) {
                System.err.println("Dictionary File: "+filename+" could not be found");
            }
        } else {
            filename=null;
            throw new IllegalArgumentException(extension + " is not an accepted dictionary format.");
        }
        loading = true;
        new DictionaryLoader().execute(filename);
    }

    private OnDictionaryLoadedListener onDictionaryLoadedListener;
    public interface OnDictionaryLoadedListener {
        public void onDictionaryLoaded();
    }
    public void setOnDictionaryLoadedListener(OnDictionaryLoadedListener listener) {
        onDictionaryLoadedListener = listener;
    }

    private class DictionaryLoader extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... filenames) {
            int count = filenames.length;
            String line, stroke, translation;
            String[] fields;
            for (int i = 0; i < count; i++) {
                if (filenames[i] == null || filenames[i].isEmpty())
                    throw new IllegalArgumentException("Dictionary filename not provided");
                try {
                    AssetManager am = context.getAssets();
                    InputStream filestream = am.open(filenames[i]);
                    InputStreamReader reader = new InputStreamReader(filestream);
                    BufferedReader lines = new BufferedReader(reader);
                    while ((line = lines.readLine()) != null) {
                        fields = line.split("\"");
                        if ((fields.length >= 3) && (fields[3].length() > 0)) {
                            stroke = fields[1];
                            translation = fields[3];
                            dictionary.put(stroke, translation);
                        }
                    }
                    lines.close();
                    reader.close();
                    filestream.close();
                } catch (IOException e) {
                    System.err.println("Dictionary File: "+filenames[i]+" could not be found");
                }
            }
            return (long) count;
        }

        protected void onPostExecute(Long result) {
            loading = false;
            onDictionaryLoadedListener.onDictionaryLoaded();
        }
    }

}
