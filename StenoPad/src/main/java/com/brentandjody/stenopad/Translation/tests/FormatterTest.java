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

    public void testFormat() throws Exception {
        Formatter formatter = new Formatter();
        assertNotNull(formatter);
        State state = new State();
        assertNotNull(state);
        DisplayItem result = formatter.format(null, makeTranslation("All"), state);
        checkResult(result, 0, "All ", "");
        result = formatter.format(null, makeTranslation("over"), state);
        checkResult(result, 0, "over ", "");
        List<Translation> translation = makeTranslation("|-");
        result = formatter.format(makeTranslation("over"), translation, state);
        checkResult(result, 5, "", "");
        assertTrue(translation.get(0).getFormatting().capitalized());
        state.setCapitalize();
        result = formatter.format(null, makeTranslation("western"), state );
        checkResult(result, 0, "Western ", "");
    }

    private List<Translation> makeTranslation(String english) {
        List<Translation> result = new LinkedList<Translation>();
        Stroke[] stroke = new Stroke("A").asArray();
        result.add(new Translation(stroke, english));
        return result;
    }

    private void checkResult(DisplayItem result, int bs, String txt, String pre) throws Exception {
        assertEquals(bs, result.getBackspaces());
        assertEquals(txt, result.getText());
        assertEquals(pre, result.getPreview());
    }
}
