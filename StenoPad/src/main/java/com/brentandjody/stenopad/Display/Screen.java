package com.brentandjody.stenopad.Display;

import android.graphics.Rect;
import android.widget.TextView;

/**
 * Created by brent on 17/11/13.
 * Display translations to device, with preview (after the cursor) of what is currently in the queue
 */
public class Screen extends DisplayDevice implements DisplayDevice.Display {

    private int text_size = 24;
    private TextView main, preview;

    public Screen(TextView main_view, TextView preview_view) {
        main = main_view;
        main.setTextSize(text_size);
        preview = preview_view;
        preview.setTextSize(text_size);
    }

    public void update(DisplayItem item, String preview_text) {
        //append main_text to main, replace preview with preview_text
        main.append(item.getText());
        final int main_width = main.getWidth();
        preview.setText(preview_text);
        final int preview_width = preview.getMeasuredWidth();
        //position preview at the end of main
        Rect bounds = new Rect();
        main.getLineBounds(main.getLineCount()-1, bounds);
        preview.setText(""+bounds.top);
        if ((bounds.width()+preview_width) < main_width) {
            // put preview at end of last line
            preview.setPadding(bounds.right, bounds.top, 0, 0);
        } else {
            // put preview at start of next line
            preview.setPadding(0, bounds.bottom, 0, 0);
        }
        main.invalidate();
        preview.invalidate();
    }
}
