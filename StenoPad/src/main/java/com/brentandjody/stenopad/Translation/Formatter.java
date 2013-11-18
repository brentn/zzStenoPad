package com.brentandjody.stenopad.Translation;

import com.brentandjody.stenopad.Display.DisplayItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brentn on 18/11/13.
 * Implement Plover's custom dictionary language
 * to apply formatting to translated steno strokes
 */
public class Formatter {

    private State state;  //formatting of prior stroke

    public Formatter() {
    }

    public DisplayItem format(Translation[] undo, Translation[] play, State state) {
        //Process undo, and play - and produce a DisplayItem
        this.state=state;
        StringBuilder sb = new StringBuilder();
        for (Translation t : undo) {
            sb.append(format(t));
        }
        for (Translation t : play) {
            sb.append(format(t));
        }
    }

    private String format(Translation translation, State priorState) {
        if (translation.english() == null) return translation.rtfcre();
        State formatting = new State();
        StringBuilder sb = new StringBuilder();
        for (String atom : breakApart(translation.english())) {
            if (atom.charAt(0) == '{') {
                if (atom.equals("{|-}")) { formatting.setCapitalize(); atom=""; }
                if (atom.equals("{>}")) { formatting.setLowercase(); atom=""; }
                if (atom.charAt(1) == '&') { formatting.setGlue(); atom.replace("&", ""); }
                if (atom.charAt(1) == '^') { formatting.attachStart(); atom.replace("{^", "{"); }
                if (atom.charAt(atom.length()-2) == '^') { formatting.attachEnd(); atom.replace("^}", "}"); }
                if (atom.equals("{#Return}")) { sb.append("\n"); atom=""; }
                if (atom.equals("{#BackSpace}")) {formatting.addBackspace(); atom=""; }
            } else {
                sb.append(atom);
            }
        }
        return sb.toString();
    }

    private List<String> breakApart(String s) {
        //break a translation string into atoms.
        List<String> result = new LinkedList<String>();
        s = s.trim();
        if ((s.contains("{") && s.contains("}"))) {
            int start = s.indexOf('{');
            if (start==0) { //first atom is {}
                int end = s.indexOf('}');
                result.add(s.substring(start,end));
                if (end < (s.length()-1)) {
                    result.addAll(breakApart(s.substring(end+1)));
                }
            } else { // add text prior to {
                result.add(s.substring(0, start-1));
            }
        } else {
            result.add(s);
        }
        return result;
    }
}
