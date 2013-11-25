package com.brentandjody.stenopad.Display.Tests;

import android.content.Context;
import android.test.AndroidTestCase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.brentandjody.stenopad.Display.DisplayItem;
import com.brentandjody.stenopad.Display.Screen;
import com.brentandjody.stenopad.Input.SoftKeyboard.TouchLayer;
import com.brentandjody.stenopad.R;

/**
 * Created by brentn on 24/11/13.
 */
public class ScreenTest extends AndroidTestCase{

    private Context context;
    private FrameLayout layout;
    private Screen screen;
    private DisplayItem item;

    public void setUp() throws Exception {
        super.setUp();
        context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (FrameLayout) inflater.inflate(R.layout.stenopad, null);
        screen = new Screen((TextView) layout.findViewById(R.id.main_text), (TextView) layout.findViewById(R.id.preview));
    }

    public void testUpdate() throws Exception {
        screen.update(new DisplayItem(0, "One "), "two"); // "One "
        assertEquals("One ", screen.getText());
        screen.update(new DisplayItem(0, "Two "), "three"); // "One Two "
        assertEquals("One Two ", screen.getText());
        screen.update(new DisplayItem(4, ""), ""); // "Zero "
        assertEquals("One ", screen.getText());
        screen.update(new DisplayItem(4, "Zero "), ""); // "Zero "
        assertEquals("Zero ", screen.getText());
        screen.update(new DisplayItem(0, "One "), "two"); // "Zero One "
        assertEquals("Zero One ", screen.getText());
        screen.update(new DisplayItem(4, "one "), "two"); // "Zero one "
        assertEquals("Zero one ", screen.getText());

    }
}
