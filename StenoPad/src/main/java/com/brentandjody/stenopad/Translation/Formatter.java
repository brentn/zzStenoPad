package com.brentandjody.stenopad.Translation;

import com.brentandjody.stenopad.Display.DisplayItem;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by brentn on 18/11/13.
 * Implement Plover's custom dictionary language
 * to apply formatting to translated steno strokes
 */
public class Formatter {

    private static final HashSet<String> SPECIAL_SUFFIXES = new HashSet<String>()
    {{add("{^ly}");add("{^s}");add("{^y}");add("{^en}");add("{^ing}");add("{^ist}");add("{^ed}");}};

    int backspaces_to_send=0;

    public Formatter() {
    }

    public DisplayItem format(Iterable<Definition> undo, Iterable<Definition> play, State state) {
        //Process play - and produce a DisplayItem
        Stack<String> pre_play=null;
        backspaces_to_send = 0;
        String replay;
        // total the backspaces required
        for (Definition t : undo) {
            backspaces_to_send+=t.getFormatting().getBackspaces();
            replay = t.getFormatting().getReplay();
            if (!replay.isEmpty()) {
                if (pre_play==null) pre_play = new Stack<String>();
                pre_play.push(replay);
            }
        }
        StringBuilder sb = new StringBuilder();
        // add new text
        for (Definition t : play) {
            String next_word = format(t, state);
            state=t.getFormatting();
            if (state.isAttachedStart() && sb.length()>0 && sb.charAt(sb.length()-1)==' ') {
                sb.deleteCharAt(sb.length()-1); //erase a space from end
            }
            sb.append(next_word);
        }
        return new DisplayItem(backspaces_to_send, sb.toString());
    }

    public String simpleFormat(String input) {
        // apply formatting rules with no prior state (good for preview text)
        Definition definition = new Definition("", input);
        return format(definition, null);
    }

    private String format(Definition definition, State priorState) {
        //decodes and updates formatting for definition
        State formatting = definition.getFormatting();
        // when there is no english
        if (definition.english() == null) {
            formatting.addBackspaces(definition.rtfcre().length()+1);
            definition.setFormatting(formatting);
            return definition.rtfcre()+" ";
        }
        StringBuilder sb = new StringBuilder();

        for (String atom : breakApart(definition.english())) {
            if (atom.charAt(0) == '{') {
                if (atom.equals("{-|}")) {
                    formatting.setCapitalize().attachEnd().addBackspaces(-4); atom=""; }
                if (atom.equals("{>}")) {
                    formatting.setLowercase().attachEnd().addBackspaces(-3); atom=""; }
                if (atom.equals("{^}")) {
                    formatting.attachEnd().attachStart().addBackspaces(-3); atom=""; }
                if (atom.equals("{#Return}")) {
                    sb.append("\n");
                    formatting.addBackspaces(-8).setReplay(atom);
                    atom=""; }
                if (atom.equals("{#BackSpace}")) {
                    backspaces_to_send++;
                    formatting.attachEnd().addBackspaces(-10).setReplay(atom);
                    atom=""; }
                if ((!atom.isEmpty()) && SPECIAL_SUFFIXES.contains(atom)) {
                    formatting.addBackspaces(-atom.length()).setReplay(atom);
                    appendSuffix(sb, atom);
                    formatting.addBackspaces(sb.length()+1).attachStart();
                    atom=""; }
                if (atom.length()>1 && atom.charAt(1) == '&') {
                    formatting.setGlue().addBackspaces(-3);
                    atom = atom.replace("{&", "").replace("}",""); }
                if (atom.length()>2 && atom.charAt(atom.length()-2) == '^') {
                    formatting.attachEnd().addBackspaces(-1);
                    atom = atom.replace("^}", "}"); }
                if (atom.length()>1 && atom.charAt(1) == '^') {
                    formatting.addBackspaces(-atom.length()).setReplay(atom);
                    appendSuffix(sb, atom);
                    formatting.addBackspaces(sb.length()+1).attachStart();
                    atom = "";}
            }
            if (!atom.isEmpty()) {
                if (atom.contains("{")||atom.contains("}")) {
                    formatting.addBackspaces(atom.length()).setReplay(atom);
                    atom = atom.replaceAll("[\\{\\}]", "");
                }
                sb.append(atom);
            }
        }
//dont think I need this        definition.setFormatting(formatting);
        return applyFormatting(priorState, formatting, sb.toString());
    }

    private String applyFormatting(State priorFormat, State format, String s) {
        if ( s==null || s.isEmpty()) return "";
        if (priorFormat==null) priorFormat = new State();
        StringBuilder sb = new StringBuilder();
        //deal with backspaces first
        if (format.isAttachedStart() || priorFormat.isAttachedEnd()
            || (priorFormat.hasGlue() && format.hasGlue())) {
            format.addBackspaces(-1);
            backspaces_to_send++;
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

    private void appendSuffix(StringBuilder sb, String suffix) {
        String s = sb.toString();
        if (suffix.equals("{^ly}")) {
            s = s.replaceAll("(.*[aeiou]c)", "$1ally");
            sbSet(sb, s);
        }
        if (suffix.equals("{^s}")) {
            s = s.replaceAll("(.*(?:s|sh|x|z|zh))", "$1es");
            s = s.replaceAll("(.*(?:oa|ea|i|ee|oo|au|ou|l|n|(?<![gin]a)r|t)ch)", "$1es");
            s = s.replaceAll("(.+[bcdfghjklmnpqrstvwxz])y", "$1ies");
            sbSet(sb, s);
        }
        if (suffix.equals("{^ing}")) {
            s = s.replaceAll("(.+)ie", "$1ing");
            sbSet(sb, s);
        }
        if (suffix.equals("{^ist}")) {
            s = s.replaceAll("(.+[cdfghlmnpr])y", "$1ist");
            sbSet(sb, s);
        }
        if (suffix.equals("{^en}")) {
            s = s.replaceAll("(.+)([t])e", "$1$2$2en");
            sbSet(sb, s);
        }
        if (suffix.matches("\\^([a-hj-xz].*)")) {
            s = s.replaceAll("(.+[bcdfghjklmnpqrstvwxz])y", "$1i");
            sbSet(sb, s);
            sb.append(suffix.replace("{^","").replace("}",""));
        }
        if (suffix.matches("\\^[aeiouy]*.")) {
            s = s.replaceAll("(.+[bcdfghjklmnpqrstuvwxz])e", "$1");
            s = s.replaceAll("(.*(?:[bcdfghjklmnprstvwxyz]|qu)[aeiou])([bcdfgklmnprtvz])", "$1$2$2");
            sbSet(sb, s);
        }
        sb.append(suffix.replace("{^","").replace("}",""));
    }

    private void sbSet(StringBuilder sb, String s) {
        if (sb.length()==0)
            sb.append(s);
        else
            sb.replace(0,sb.length()-1,s);
    }
}
