package com.brentandjody.stenopad.Translation;

import android.content.Context;

import com.brentandjody.stenopad.Display.DisplayDevice;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by brent on 14/11/13.
 * takes a stream of steno strokes, and produces a stream of English text
 */
public class Translator {

    private static final int HISTORY_SIZE = 50;
    public static boolean USE_SUFFIX_FOLDING = true;
    private static final Set<String> SUFFIX_KEYS = new HashSet<String>() {{
        add("-S"); add("-G"); add("-Z"); add("-D"); }};

    private final Dictionary dictionary;
    private Deque<Stroke> strokeQ = new ArrayDeque<Stroke>();
    private Formatter formatter = new Formatter();
    private LimitedSizeDeque<Definition> history = new LimitedSizeDeque<Definition>(HISTORY_SIZE);
    List<Definition> play = new LinkedList<Definition>();
    List<Definition> undo = new LinkedList<Definition>();

    public Translator(Context context) {
        dictionary = new Dictionary(context);
        dictionary.loadDefault();
    }

    public Translator(Dictionary d) {
        dictionary = d;
    }

    public void translate(Stroke stroke, DisplayDevice.Display display) {
        State state = translate(stroke);
        if (display != null) {
            display.update(formatter.format(undo, play, state), wordsInQueue());
            undo.clear();
            play.clear();
        }
    }

    private State translate(Stroke s) {
        State state = null;
        Definition translation;
        if (s.isCorrection()) {
            processUndo();
            state = getStateFromHistory();
        } else {
            // check queue+stroke first
            String full_stroke;
            if (strokeQ.isEmpty())
                full_stroke = s.rtfcre();
            else
                full_stroke = Stroke.combine(strokesInQueue())+"/"+s.rtfcre();
            String definition = dictionary.lookup(full_stroke);
            if (found(definition)) {
                if (ambiguous(definition)) {
                    strokeQ.add(s);
                } else { //not ambiguous
                    translation = new Definition(full_stroke, definition);
                    play.add(translation);
                    state = getStateFromHistory();
                    history.add(translation);
                    strokeQ.clear();
                }
            } else { //full_stroke not found
                if (!worksWithSuffix(full_stroke)) {
                    if (strokeQ.isEmpty()) { //queue is empty, and last stroke not found
                        translation = new Definition(s.asArray(), s.rtfcre()); // add RTF/CRE as text
                        play.add(translation);
                        state = getStateFromHistory();
                        history.add(translation);
                    } else { //still strokes in queue to try
                        // reduce the queue by one stroke, and try again
                        Stack<Stroke> lastStrokes = new Stack<Stroke>();
                        String firstStrokes = Stroke.combine(strokesInQueue());
                        while (strokeQ.size() > 0 && dictionary.forceLookup(firstStrokes) == null) {
                            lastStrokes.push(strokeQ.removeLast());
                            firstStrokes = Stroke.combine(strokesInQueue());
                        }
                        if (strokeQ.size()==0) { //nothing was found
                            firstStrokes = "";
                            while (!lastStrokes.empty()) firstStrokes += lastStrokes.pop().rtfcre()+"/";
                            firstStrokes = firstStrokes.substring(0,firstStrokes.length()-1);
                            translation = new Definition (firstStrokes, firstStrokes);
                        } else {
                            translation = new Definition(firstStrokes, dictionary.forceLookup(firstStrokes)); //translate the part we can
                            strokeQ.clear();
                            while (!lastStrokes.empty()) strokeQ.add(lastStrokes.pop()); //put the rest on the queue
                        }
                        play.add(translation);
                        state = getStateFromHistory();
                        history.add(translation);
//
                        translate(s);
                    }
                }
            }
        }
        if (state == null) state = getStateFromHistory();
        return state;
    }

    private void processUndo() {
        if (strokeQ.isEmpty()) {
            if (!history.isEmpty()) {
                // put the strokes from last definition on the queue, and remove the final stroke
                Definition definition = history.removeLast();
                undo.add(definition);
                strokeQ.addAll(Arrays.asList(definition.strokes()));
                strokeQ.removeLast();
            }
        } else { //there are strokes in the queue
            strokeQ.removeLast();
        }
        // replay the last item in history, to re-set the queue
        if (strokeQ.isEmpty()) replayHistoryItem();
    }

    private boolean worksWithSuffix(String stroke) {
        if (! USE_SUFFIX_FOLDING) return false;
        Stroke lastStroke;
        boolean hasSlash=false;
        if (stroke.contains("/")) {
            hasSlash=true;
            lastStroke = new Stroke(stroke.substring(stroke.lastIndexOf("/")+1));
        }  else {
            lastStroke = new Stroke(stroke);
        }
        if( (lastStroke.toString().length() < 2) || (SUFFIX_KEYS.contains(lastStroke.rtfcre()))) return false;
        if (!Collections.disjoint(lastStroke.keys(), SUFFIX_KEYS)) {
            for (String key : SUFFIX_KEYS) {
                if (lastStroke.keys().contains(key)) {
                    String prefix="";
                    if (hasSlash) prefix=stroke.substring(0,stroke.lastIndexOf("/")+1);
                    lastStroke.removeKey(key);
                    translate(new Stroke(prefix+lastStroke.rtfcre()));
                    translate(new Stroke(key));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean found(String lookup) {
        //returns true if the string resulting from a lookup is not null
        return lookup!=null;
    }

    private boolean ambiguous(String lookup) {
        //returns true if the string resulting from alookup is empty
        return (found(lookup) && lookup.isEmpty());
    }

    private State getStateFromHistory() {
        if (history.isEmpty()) {
            return new State();
        } else {
            return history.peekLast().getFormatting();
        }
    }

    private void replayHistoryItem() {
        if (history.isEmpty()) return;
        Definition t = history.removeLast();
        undo.add(t);
        for (Stroke s : t.strokes()) {
            translate(s, null);
        }
    }

    private Stroke[] strokesInQueue() {
        //return all the strokes in the queue as an array
        Stroke[] result = new Stroke[strokeQ.size()];
        return strokeQ.toArray(result);
    }

    private String wordsInQueue() {
        String stroke = Stroke.combine(strokesInQueue());
        String result = dictionary.forceLookup(stroke);
        if (result == null) {
            if (stroke!=null) {
                result = stroke;
            } else {
                result = "";
            }
        } else {
            result = formatter.simpleFormat(result);
        }
        return result;
    }

    class LimitedSizeDeque<Value> extends LinkedBlockingDeque<Value> {
        private final int size_limit;

        public LimitedSizeDeque(int size) {
            size_limit = size;
        }

        @Override
        public void addFirst(Value value) {
            System.err.print("Cannot add history at this end of the deque");
        }

        @Override
        public void addLast(Value value) {
            super.addLast(value);
            limitSize();
        }

        @Override
        public boolean add(Value value) {
            super.add(value); //addLast
            limitSize();
            return true;
        }

        private void limitSize() {
            while (size() > size_limit) {
                removeFirst();
            }
        }
    }
}
