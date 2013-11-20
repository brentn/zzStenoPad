package com.brentandjody.stenopad.Translation.tests;

import android.test.AndroidTestCase;

import com.brentandjody.stenopad.Display.DisplayItem;
import com.brentandjody.stenopad.Translation.Formatter;
import com.brentandjody.stenopad.Translation.State;
import com.brentandjody.stenopad.Translation.Stroke;
import com.brentandjody.stenopad.Translation.Translation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brentn on 18/11/13.
 */
public class FormatterTest extends AndroidTestCase{

    public void testBreakApart() throws Exception {
        Formatter formatter = new Formatter();
        List<String> result = formatter.breakApart("");
        assertEquals(0, result.size());
        result = formatter.breakApart("west");
        assertEquals(1, result.size());
        assertEquals("west", result.get(0));
        result = formatter.breakApart("{x}");
        assertEquals(1, result.size());
        assertEquals("{x}", result.get(0));
        result = formatter.breakApart("abc{DEF}g");
        assertEquals(3, result.size());
        assertEquals("abc", result.get(0));
        assertEquals("{DEF}", result.get(1));
        assertEquals("g", result.get(2));
    }

    public void testFormat() throws Exception {
        Formatter formatter = new Formatter();
        List<Translation> none = new LinkedList<Translation>();
        assertNotNull(formatter);
        State state = new State();
        assertNotNull(state);
        // plain word
        DisplayItem result = formatter.format(none, makeTranslation("All"), state);
        checkResult(result, 0, "All ");

        // undo
        List<Translation> t1 = makeTranslation("over");
        result = formatter.format(none, t1, state);
        checkResult(result, 0, "over ");
        assertEquals(5, t1.get(0).getFormatting().getBackspaces());
        assertFalse(t1.get(0).getFormatting().isCapitalized());

        //capitalization
        List<Translation> t2 = makeTranslation("{|-}");
        result = formatter.format(t1, t2, state);
        checkResult(result, 5, "");
        assertTrue(t2.get(0).getFormatting().isCapitalized());
        state.setCapitalize();
        result = formatter.format(none, makeTranslation("western"), state);
        checkResult(result, 0, "Western ");
        state.setLowercase();
        result = formatter.format(none, makeTranslation("Brent"), state);
        checkResult(result, 0, "brent ");

        //attachment
        state.attachEnd();
        result = formatter.format(none, makeTranslation("ararat"), state);
        checkResult(result,1 , "ararat ");
        state.attachStart();
        result = formatter.format(none, makeTranslation("Bob Marley"), state);
        checkResult(result, 1, "bob Marley ");
        t1 = makeTranslation("twinkle");
        t1.get(0).getFormatting().attachStart();
        state = new State();
        result = formatter.format(none, t1, state);
        checkResult(result, 1, "twinkle ");
        t1 = makeTranslation("left-handed");
        state.setGlue();
        result = formatter.format(none, t1, state);
        checkResult(result, 0, "left-handed ");
        t1 = makeTranslation("six-fingered");
        t1.get(0).getFormatting().setGlue();
        result = formatter.format(none, t1, state);
        checkResult(result, 1, "six-fingered ");
        state = new State();
        result = formatter.format(none, t1 , state);
        checkResult(result, 0, "six-fingered ");
    }

    private List<Translation> makeTranslation(String english) {
        List<Translation> result = new LinkedList<Translation>();
        Stroke[] stroke = new Stroke("A").asArray();
        result.add(new Translation(stroke, english));
        return result;
    }

    private void checkResult(DisplayItem result, int bs, String txt) throws Exception {
        assertEquals(bs, result.getBackspaces());
        assertEquals(txt, result.getText());
    }
}
