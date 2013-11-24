package com.brentandjody.stenopad.Display;

import android.graphics.Point;
import android.widget.TextView;

/**
 * Created by brent on 17/11/13.
 * Display translations to device, with preview (after the cursor) of what is currently in the queue
 */
public class Screen extends DisplayDevice implements DisplayDevice.Display {

    private int text_size = 24;
    private TextView main, preview;
    private int line_height=0;

    public Screen(TextView main_view, TextView preview_view) {
        main = main_view;
        main.setTextSize(text_size);
        preview = preview_view;
        preview.setTextSize(text_size);
    }

    public void update(DisplayItem item, String preview_text) {
        //append main_text to main, replace preview with preview_text
        if (main.length() > 0 && item.getBackspaces() > 0) {
            String current_text = "";
            int new_length=0;
            if (main.getText() != null) {
                current_text = main.getText().toString();
                new_length = current_text.length();
            }
            main.setText(current_text.substring(0, new_length-1));
        }
        main.append(item.getText());
        preview.setText(preview_text);
        Point preview_pos = getPreviewPosition();
        preview.setPadding(preview_pos.x, preview_pos.y, 0, 0);
        main.invalidate();
        preview.invalidate();
    }

    private Point getPreviewPosition() {
        //position preview at the end of main
        if (main == null) return new Point(0,0);
        final int main_width = main.getWidth();
        final int preview_width = preview.getWidth()-preview.getPaddingLeft();
        int y = main.getLineBounds(main.getLineCount()-1, null); // baseline of last line
        if (line_height==0) line_height = y; // set this the first time through
        final CharSequence current_text = main.getText();
        if (current_text==null) return new Point(0, y);
        final int text_length = current_text.length();
        int x = 0;
        // look for the position of the last character
        int charnum = main.getOffsetForPosition(x, y);
        while (charnum < text_length && x < main_width) {
            x += 10;
            charnum = main.getOffsetForPosition(x, y);
        }
        if ((x + preview_width) >= main_width) {
            x = 0;
        } else {
            y -= line_height;
        }
        return new Point(x, y);
    }

}
