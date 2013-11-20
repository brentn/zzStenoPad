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

    int backspaces;

    public Formatter() {
    }

    public DisplayItem format(Iterable<Translation> undo, Iterable<Translation> play, State state) {
        //Process play - and produce a DisplayItem
        backspaces = 0;
        for (Translation t : undo) {
            backspaces+=t.getFormatting().getBackspaces();
        }
        StringBuilder sb = new StringBuilder();
        for (Translation t : play) {
            sb.append(format(t, state));
            state=t.getFormatting();
        }
        return new DisplayItem(backspaces, sb.toString());
    }

    private String format(Translation translation, State priorState) {
        //decodes and updates formatting for translation
        State formatting = translation.getFormatting();
        if (translation.english() == null) {
            formatting.addBackspaces(translation.rtfcre().length()+1);
            translation.setFormatting(formatting);
            return translation.rtfcre()+" ";
        }
        StringBuilder sb = new StringBuilder();
        int bs = 0;
        for (String atom : breakApart(translation.english())) {
            if (atom.charAt(0) == '{') {
                if (atom.equals("{|-}")) { formatting.setCapitalize().attachEnd(); atom=""; }
                if (atom.equals("{>}")) { formatting.setLowercase().attachEnd(); atom=""; }
                if (atom.equals("{^}")) { formatting.attachEnd().attachStart(); atom=""; }
                if (atom.equals("{#Return}")) { sb.append("\n"); formatting.attachEnd(); atom=""; bs+=1; }
                if (atom.equals("{#BackSpace}")) {backspaces++; bs-=1; formatting.attachEnd(); atom=""; }
                if (atom.length()>1 && atom.charAt(1) == '&') { formatting.setGlue(); atom.replace("&", ""); }
                if (atom.length()>1 && atom.charAt(1) == '^') { formatting.attachStart(); atom.replace("{^", "{"); }
                if (atom.length()>2 && atom.charAt(atom.length()-2) == '^') { formatting.attachEnd(); atom.replace("^}", "}"); }
            } else {
                atom = atom.replaceAll("[\\{\\}]", "");
                bs+=atom.length();
                sb.append(atom);
            }
        }
        formatting.addBackspaces(bs);
        translation.setFormatting(formatting);
        String result = applyFormatting(priorState, formatting, sb.toString());
        return result;
    }

    private String applyFormatting(State priorFormat, State format, String s) {
        if ( s==null || s.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        //deal with backspaces first
        if (format.isAttachedStart() || priorFormat.isAttachedEnd()
            || (priorFormat.hasGlue() && format.hasGlue())) {
            backspaces++;
        }
        //deal with capitiaization
        if (priorFormat.isCapitalized()) {
            sb.append(s.substring(0,1).toUpperCase());
            if (s.length() > 1) sb.append(s.substring(1));
        }
        if (priorFormat.isLowercased()) {
            sb.append(s.substring(0,1).toLowerCase());
            if (s.length() > 1) sb.append(s.substring(1));
        }
        if (sb.length()==0) sb.append(s);
        //deal with space at end
        if (!format.isAttachedEnd()) {
            sb.append(" ");
            format.addBackspaces(1);
        }
        return sb.toString();
    }

    public List<String> breakApart(String s) {
        //break a translation string into atoms. (recursive)
        List<String> result = new LinkedList<String>();
        if (s == null || s.isEmpty()) return result;
        if ((s.contains("{") && s.contains("}"))) {
            int start = s.indexOf("{");
            if (start==0) { //first atom is {}
                int end = s.indexOf("}")+1; //substring is (] (exclusive at end)
                result.add(s.substring(start,end));
                if (end < s.length()) {
                    result.addAll(breakApart(s.substring(end)));
                }
            } else { // add text prior to {
                result.add(s.substring(0, start));
                result.addAll(breakApart(s.substring(start)));
            }
        } else {
            result.add(s);
        }
        return result;
    }
}
