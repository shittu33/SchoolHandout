package com.example.abumuhsin.udusmini_library.Views;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.abumuhsin.udusmini_library.R;
import com.warkiz.widget.IndicatorSeekBar;

public class PageBottomControlPanel extends FrameLayout implements View.OnClickListener, View.OnTouchListener {
    private TextView progress_txt;
    private ImageButton fwd_btn;
    private ImageButton bkwd_btn;
    private IndicatorSeekBar p_seeSeekBar;
    private ImageButton b_lock, b_speed, b_pdf, b_play, b_screen_on;

    public PageBottomControlPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageBottomControlPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IndicatorSeekBar getP_seeSeekBar() {
        return p_seeSeekBar;
    }

    public void DisableControls(boolean is_pdf){
        if (is_pdf){
            b_pdf.setEnabled(false);
            b_speed.setEnabled(false);
        }
    }
    public void setProgress(String progress){
        progress_txt.setText(progress);
    }
    private void init() {
        Context context = getContext();
        inflate(context, R.layout.bottom_sheet, this);
        progress_txt = findViewById(R.id.prog_tv);
        fwd_btn = findViewById(R.id.fwd_btn);
        bkwd_btn = findViewById(R.id.bkwd_btn);
        p_seeSeekBar = findViewById(R.id.indicator_seekbar);
        b_lock = findViewById(R.id.b_lock);
        b_speed = findViewById(R.id.b_speed);
        b_pdf = findViewById(R.id.b_pdf);
        b_play = findViewById(R.id.b_auto_play);
        b_screen_on = findViewById(R.id.b_screen_on);
        fwd_btn.setOnClickListener(this);
        bkwd_btn.setOnClickListener(this);
        p_seeSeekBar.setOnClickListener(this);
        b_lock.setOnClickListener(this);
        b_speed.setOnClickListener(this);
        b_pdf.setOnClickListener(this);
        b_play.setOnClickListener(this);
        b_screen_on.setOnClickListener(this);
        fwd_btn.setOnTouchListener(this);
        bkwd_btn.setOnTouchListener(this);
    }

    boolean is_play = false;
    boolean is_screen_Always = true;

    @Override
    public void onClick(View view) {
        onControlButtonListener.onAnyControlClick(view);
        switch (view.getId()) {
            case R.id.fwd_btn:
                onControlButtonListener.onfwdClick(view);
                break;
            case R.id.bkwd_btn:
                onControlButtonListener.onbkwdClick(view);
                break;
            case R.id.b_lock:
                onControlButtonListener.onLockClick(view);
                break;
            case R.id.b_speed:
                onControlButtonListener.onSpeedClick(view);
                break;
            case R.id.b_pdf:
                onControlButtonListener.onPdfClick(view);
                break;
            case R.id.b_auto_play:
                toggleAutolay();
                break;
            case R.id.b_screen_on:
                is_screen_Always = !is_screen_Always;
                setB_screenColor(is_screen_Always);
                onControlButtonListener.onScreenOnToggle(view,is_screen_Always);
        }
    }

    public void setB_screenColor(boolean is_screen_always) {
        if (is_screen_always){
            setMovingColor(b_screen_on);
        }else {
            setIdleColor(b_screen_on);
        }
    }

    public void toggleAutolay() {
        is_play = !is_play;
        if (is_play) {
            setMovingColor(b_play);
            b_play.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        } else {
            setIdleColor(b_play);
            b_play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        }
        onControlButtonListener.onAutoPlayClick(b_play, is_play);
    }

    public boolean is_play() {
        return is_play;
    }

    OnControlButtonListener onControlButtonListener;

    public void setOnControlButtonListener(OnControlButtonListener onControlButtonListener) {
        this.onControlButtonListener = onControlButtonListener;
    }

    boolean is_long_ready = false;
    Handler long_press_handler_fwd = new Handler();
    Runnable long_press_run_fwd = new Runnable() {
        @Override
        public void run() {
            setMovingColor(fwd_btn);
            onControlButtonListener.onAnyControlClick(fwd_btn);
            onControlButtonListener.onFwdHold(fwd_btn);
            is_long_ready = true;
        }
    };

    Handler long_press_handler_bkwd = new Handler();
    Runnable long_press_run_bkwd = new Runnable() {
        @Override
        public void run() {
            setMovingColor(bkwd_btn);
            onControlButtonListener.onAnyControlClick(bkwd_btn);
            onControlButtonListener.onBkwdHold(bkwd_btn);
            is_long_ready = true;
        }
    };


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (view == fwd_btn) {
                    is_long_ready = false;
                    long_press_handler_fwd.postDelayed(long_press_run_fwd, ViewConfiguration.getLongPressTimeout());
                } else if (view == bkwd_btn) {
                    is_long_ready = false;
                    long_press_handler_bkwd.postDelayed(long_press_run_bkwd, ViewConfiguration.getLongPressTimeout());
                }
                return true;
            case MotionEvent.ACTION_UP:
                long_press_handler_fwd.removeCallbacks(long_press_run_fwd);
                long_press_handler_bkwd.removeCallbacks(long_press_run_bkwd);
                if (is_long_ready) {
                    setIdleColor((ImageButton) view);
                    if (view == fwd_btn) {
                        onControlButtonListener.onFwdReleased(fwd_btn);
                    } else if (view == bkwd_btn) {
                        onControlButtonListener.onBkwdReleased(bkwd_btn);
                    }
                }else {
                    view.performClick();
                }
        }
        return false;
    }
public void hide(){
        setVisibility(GONE);
}
public void show(){
        setVisibility(VISIBLE);
}
    private void setMovingColor(ImageButton nav_btn) {
        nav_btn.setColorFilter(Color.BLUE);
    }

    private void setIdleColor(ImageButton view) {
        view.setColorFilter(view.getContext().getResources().getColor(R.color.colorPrimary));
    }

    public interface OnControlButtonListener {
        void onfwdClick(View v);

        void onbkwdClick(View v);

        void onLockClick(View v);

        void onSpeedClick(View v);

        void onPdfClick(View v);

        void onAutoPlayClick(View v, boolean is_play);

        void onScreenOnToggle(View v, boolean is_screen_Always);

        void onAnyControlClick(View v);

        void onFwdHold(View v);

        void onFwdReleased(View v);

        void onBkwdHold(View v);

        void onBkwdReleased(View v);
    }

}
