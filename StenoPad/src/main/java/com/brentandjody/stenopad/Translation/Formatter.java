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

    public Formatter() {
    }

    public DisplayItem format(Iterable<Translation> undo, Iterable<Translation> play, State state) {
        //Process play - and produce a DisplayItem
        int backspaces = 0;
        for (Translation t : undo) {
            backspaces+=t.getFormatting().getBackspaces();
        }
        StringBuilder sb = new StringBuilder();
        for (Translation t : play) {
            sb.append(format(t, state));
            state=t.getFormatting();
        }
        return new DisplayItem(backspaces, sb.toString(), "");
    }

    private String format(Translation translation, State priorState) {
        //decodes and updates formatting for translation
        State formatting = new State();
        if (translation.english() == null) {
            formatting.addBackspaces(translation.rtfcre().length()+1);
            translation.setFormatting(formatting);
            return translation.rtfcre()+" ";
        }
        StringBuilder sb = new StringBuilder();
        int bs = 0;
        for (String atom : breakApart(translation.english())) {
            if (atom.charAt(0) == '{') {
                if (atom.equals("{|-}")) { formatting.setCapitalize(); atom=""; }
                if (atom.equals("{>}")) { formatting.setLowercase(); atom=""; }
                if (atom.charAt(1) == '&') { formatting.setGlue(); atom.replace("&", ""); }
                if (atom.charAt(1) == '^') { formatting.attachStart(); atom.replace("{^", "{"); }
                if (atom.charAt(atom.length()-2) == '^') { formatting.attachEnd(); atom.replace("^}", "}"); }
                if (atom.equals("{#Return}")) { sb.append("\n"); atom=""; bs+=1; }
                if (atom.equals("{#BackSpace}")) {sb.append("\b"); bs-=1; formatting.attachEnd(); atom=""; }
            } else {
                bs+=atom.length();
                sb.append(atom);
            }
        }
        if (!formatting.isAttachedEnd()) {
            bs++;
            sb.append(" ");
        }
        formatting.addBackspaces(bs);
        translation.setFormatting(formatting);
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
