package com.brentandjody.stenopad.Input.Tests;

import android.content.Context;
import android.test.AndroidTestCase;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.brentandjody.stenopad.Input.SoftKeyboard.TouchLayer;
import com.brentandjody.stenopad.R;
import com.brentandjody.stenopad.Translation.Stroke;


/**
 * Created by brentn on 24/11/13.
 */
public class SoftKeyboardTest extends AndroidTestCase implements TouchLayer.OnStrokeCompleteListener{

    private Context context;
    private TouchLayer keyboard;
    private Stroke last_stroke;

    public void setUp() throws Exception {
        super.setUp();
        context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.keyboard, null);
        keyboard = (TouchLayer) ll.findViewById(R.id.keyboard);
    }

    public void testSetStrokesByString() throws Exception {
        keyboard.setOnStrokeCompleteListener(this);
        //right side only
        keyboard.test_keys("S-");
        assertEquals("S", last_stroke.rtfcre());
        //left side only
        keyboard.test_keys("-S");
        assertEquals("-S", last_stroke.rtfcre());
        //multiple keys
        keyboard.test_keys("S-/T-/P-/H-");
        assertEquals("STPH", last_stroke.rtfcre());
        //right and left sides together
        keyboard.test_keys("S-/-S");
        assertEquals("S-S", last_stroke.rtfcre());
        //right&left with vowels
        keyboard.test_keys("P-/A-/-U/-R");
        assertEquals("PAUR", last_stroke.rtfcre());
        //out of order
        keyboard.test_keys("-T/-R");
        assertEquals("-RT", last_stroke.rtfcre());
        keyboard.test_keys("-T/-S/T-/P-/H-/-R");
        assertEquals("TPH-RTS", last_stroke.rtfcre());
        //illegal keys
        keyboard.test_keys("F-/-R");
        assertEquals("-R", last_stroke.rtfcre());
    }

//    public void testStrokesByTouch() throws Exception {
//        List<TextView> keys = new ArrayList<TextView>();
//        TextView S = (TextView) keyboard.findViewById(R.id.S);
//        keys.add(S);
//        keyboard.test_keys(inst, keys);
//        assertEquals("S", last_stroke.rtfcre());
//    }

    @Override
    public void onStrokeComplete(Stroke stroke) {
        last_stroke = stroke;
    }
}
