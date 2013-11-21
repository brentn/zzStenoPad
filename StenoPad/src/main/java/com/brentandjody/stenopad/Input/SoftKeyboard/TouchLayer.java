package com.brentandjody.stenopad.Input.SoftKeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brentandjody.stenopad.R;
import com.brentandjody.stenopad.Translation.Stroke;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brentn on 21/11/13.
 * Intercepts touches, and implements a dual-swipe-like interface for activating keys
 */
public class TouchLayer extends LinearLayout {

    private static final int NUMBER_OF_FINGERS=2;
    private static FrameLayout LOADING_SPINNER;
    private static Paint PAINT;

    private List<TextView> keys = new ArrayList<TextView>();
    private boolean loading;
    private Path[] path;

    public TouchLayer(Context context) {
        super(context);
        initialize();
    }

    public TouchLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TouchLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private OnStrokeCompleteListener onStrokeCompleteListener;
    public interface OnStrokeCompleteListener {
        public void onStrokeComplete();
    }
    public void setOnStrokeCompleteListener(OnStrokeCompleteListener listener) {
        onStrokeCompleteListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        keys.clear();
        enumerate_keys(this);
        LOADING_SPINNER = (FrameLayout) this.findViewById(R.id.ovelay);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (LOADING_SPINNER == null) return;
        if (loading) LOADING_SPINNER.setVisibility(VISIBLE);
        else {
            LOADING_SPINNER.setVisibility(INVISIBLE);
            for (int i=0; i<NUMBER_OF_FINGERS; i++) {
                canvas.drawPath(path[i], PAINT);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x, y;
        int i;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                i = event.getActionIndex();
                x = event.getX(i);
                y = event.getY(i);
                path[i].reset();
                path[i].moveTo(x, y);
                toggleKeyAt(x, y);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                selectKeys(event);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                i = event.getActionIndex();
                if (i == 0) { //TODO: only complete if keys are selected
                    onStrokeCompleteListener.onStrokeComplete();
                }
                path[i].reset();
                break;
            }
        }
        return true;
    }

    public void setLoading() {
        loading=true;
    }

    public void clearLoading() {
        loading=false;
    }

    public Stroke getStroke() {
        // Reads stroke from keyboard, and resets the keys
        List<String> selected_keys = new LinkedList<String>();
        String name;
        for (TextView key : keys) {
            if (key.isSelected()) {
                if (key.getHint()!= null) {
                    name = key.getHint().toString();
                    selected_keys.add(name);
                }
                key.setSelected(false);
            }
        }
        return new Stroke(new LinkedHashSet<String>(selected_keys));
    }

    private void initialize() {
        loading=true;
        for (int x=0; x<NUMBER_OF_FINGERS; x++) {
            path[x] = new Path();
        }
        PAINT = new Paint();
         if (getResources() != null)
            PAINT.setColor(getResources().getColor(android.R.color.holo_orange_light));
        else
            PAINT.setColor(Color.parseColor("#33B5E5"));
        PAINT.setStyle(Paint.Style.STROKE);
        PAINT.setStrokeJoin(Paint.Join.ROUND);
        PAINT.setStrokeCap(Paint.Cap.ROUND);
        PAINT.setStrokeWidth(8);
    }

    private void enumerate_keys(View v) {
        keys.add((TextView) v.findViewById(R.id.num));
        keys.add((TextView) v.findViewById(R.id.S));
        keys.add((TextView) v.findViewById(R.id.S2));
        keys.add((TextView) v.findViewById(R.id.T));
        keys.add((TextView) v.findViewById(R.id.K));
        keys.add((TextView) v.findViewById(R.id.P));
        keys.add((TextView) v.findViewById(R.id.W));
        keys.add((TextView) v.findViewById(R.id.H));
        keys.add((TextView) v.findViewById(R.id.R));
        keys.add((TextView) v.findViewById(R.id.A));
        keys.add((TextView) v.findViewById(R.id.O));
        keys.add((TextView) v.findViewById(R.id.star));
        keys.add((TextView) v.findViewById(R.id.e));
        keys.add((TextView) v.findViewById(R.id.u));
        keys.add((TextView) v.findViewById(R.id.f));
        keys.add((TextView) v.findViewById(R.id.r));
        keys.add((TextView) v.findViewById(R.id.p));
        keys.add((TextView) v.findViewById(R.id.b));
        keys.add((TextView) v.findViewById(R.id.l));
        keys.add((TextView) v.findViewById(R.id.g));
        keys.add((TextView) v.findViewById(R.id.t));
        keys.add((TextView) v.findViewById(R.id.s));
        keys.add((TextView) v.findViewById(R.id.d));
        keys.add((TextView) v.findViewById(R.id.z));
    }

    private void toggleKeyAt(float x, float y) {
        Point pointer = new Point();
        Point offset = getScreenOffset(this);
        pointer.set((int)x+offset.x, (int)y+offset.y);
        for (TextView key : keys) {
            if (pointerOnKey(pointer, key)) {
                key.setSelected(! key.isSelected());
                return;
            }
        }
    }

    private void selectKeys(MotionEvent e) {
        Point pointer = new Point();
        Point offset = getScreenOffset(this);
        int i = e.getActionIndex();
        for (int n=0; n<e.getHistorySize(); n++) {
            pointer.set((int)e.getHistoricalX(i, n)+offset.x, (int)e.getHistoricalY(i, n)+offset.y);
            path[i].lineTo(e.getHistoricalX(i, n), e.getHistoricalY(i, n));
            for (TextView key : keys) {
                if (pointerOnKey(pointer, key)) {
                    key.setSelected(true);
                }
            }
        }
    }

    private Point getScreenOffset(View v) {
        Point result = new Point();
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        result.set(location[0], location[1]);
        return result;
    }

    private Boolean pointerOnKey(Point p, View key) {
        Point bottomRight = new Point();
        Point topLeft = getScreenOffset(key);
        bottomRight.set(topLeft.x+key.getWidth(), topLeft.y+key.getHeight());
        return !((p.x < topLeft.x) || (p.x > bottomRight.x) || (p.y < topLeft.y) || (p.y > bottomRight.y));
    }
}
