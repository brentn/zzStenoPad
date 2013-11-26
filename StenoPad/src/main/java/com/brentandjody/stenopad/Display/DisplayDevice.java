package com.brentandjody.stenopad.Display;

import android.graphics.Rect;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

/**
 * Created by brent on 17/11/13.
 * Display translations to device, with preview (after the cursor) of what is currently in the queue
 */
public abstract class DisplayDevice {

    public interface Display {
        public void update(DisplayItem item, String preview_text);
    }

    public abstract void update(DisplayItem item, String preview_text);

}
