package com.brentandjody.stenopad;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by brent on 14/11/13.
 * takes a stream of steno strokes, and produces a stream of English text
 */
public class Translator {

    private static final int HISTORY_SIZE = 50;
    private static final Set<String> SUFFIX_KEYS = new HashSet<String>() {{
        add("-S"); add("-G"); add("-Z"); add("-D"); }};


    private final StenoDictionary dictionary;
    private Deque<Stroke> strokeQ;
    private LimitedSizeDeque<Translation> history;
    List<Translation> play = new LinkedList<Translation>();
    List<Translation> undo = new LinkedList<Translation>();

    public Translator(StenoDictionary d) {
        dictionary = d;
        strokeQ = new ArrayDeque<Stroke>();
        history = new LimitedSizeDeque<Translation>(HISTORY_SIZE);
    }

    public void translate(Stroke stroke, Translation.TranslationPlayer player) {
        Translation translation;
        Translation state=null;
        List<Stroke> strokes;
        String lookup;
        // process a single stroke
        if (stroke.isCorrection()) {
            if (! strokeQ.isEmpty()) {
                strokeQ.removeLast();
                if (strokeQ.isEmpty()) {
                    replayHistoryItem();
                }
            } else {
                if (! history.isEmpty()) {
                    undo.add(history.removeLast());
                    replayHistoryItem();
                }
            }
        } else {
            if (strokeQ.isEmpty()) {
                lookup = dictionary.lookup(stroke.rtfcre());
                if (lookup==null) { //not found
                    translation = new Translation(stroke.asArray(), stroke.rtfcre());
                } else {
                    if (lookup.isEmpty()) { //ambiguous
                        translation = null;
                        strokeQ.add(stroke);
                    } else { //deterministic
                        translation = new Translation(stroke.asArray(), lookup);
                    }
                }
            } else {
                lookup = dictionary.lookup(Stroke.combine(strokesInQueue()) + "/" + stroke);
                if (lookup==null) {
                    Stroke[] subStroke = dictionary.longestValidStroke(Stroke.combine(strokesInQueue()));
                    lookup = dictionary.forceLookup(Stroke.combine(subStroke));
                    translation = new Translation(subStroke, lookup);
                    for (Stroke s : subStroke) {
                        Stroke r = strokeQ.remove();
                        if (! r.equals(s)) {
                            System.err.print("Stroke in queue did not match stroke that was translated");
                        }
                    }
                    strokeQ.add(stroke);
                } else {
                    if (lookup.isEmpty()) { //ambiguous
                        translation = null;
                        strokeQ.add(stroke);
                    } else { //deterministic
                        strokeQ.add(stroke);
                        translation = new Translation(strokesInQueue(), lookup);
                        strokeQ.clear();
                    }
                }
            }
            if (translation!= null) {
                play.add(translation);
                state=history.peekLast();
                history.add(translation);
            }
        }
        if (player != null) {
            player.playTranslation(undo, play, state, wordsInQueue());
            undo.clear();
            play.clear();
        }
    }

    private void replayHistoryItem() {
        if (history.isEmpty()) return;
        Translation t = history.removeLast();
        undo.add(t);
        for (Stroke s : t.strokes()) {
            translate(s, null);
        }
    }

    private Stroke[] strokesInQueue() {
        Stroke[] result = new Stroke[strokeQ.size()];
        return strokeQ.toArray(result);
    }

    private String wordsInQueue() {
        String stroke = Stroke.combine(strokesInQueue());
        String result = dictionary.forceLookup(stroke);
        if (result == null)
            if (stroke!=null) {
                return stroke;
            } else {
                return "";
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
            add(value);
        }

        @Override
        public boolean add(Value value) {
            boolean result = super.add(value); //addLast
            while (size() > size_limit) {
                removeFirst();
            }
            return result;
        }
    }
}
