package com.example.abumuhsin.udusmini_library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.example.abumuhsin.udusmini_library.firebaseStuff.model.LocalHandout;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import java.util.ArrayList;

/**
 * Created by Abu Muhsin on 12/11/2018.
 */

public class View_Utils {
    private Handler handler;
    private MotionEvent e;
    private boolean is_long_clicked = false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!is_click_not_toFlip(e) && e.getAction() != MotionEvent.ACTION_UP) {
                is_long_clicked = true;
            }
        }
    };

    public View_Utils(MotionEvent event) {
        this.e = event;
    }

    public static int px_to_dp(Context context, float px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, displayMetrics));
    }

    public static int dp_to_px(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics));
    }
    public static float px_to_density(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float density_to_px(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static display getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (wm != null) {
            display = wm.getDefaultDisplay();
        }
        DisplayMetrics metrics = new DisplayMetrics();
        if (display != null) {
            display.getMetrics(metrics);
        }
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return new display(width, height);
    }

    public static class display {
        public int width;
        public int height;

        display(int w, int h) {
            width = w;
            height = h;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static boolean isSoftNavAvailable(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey && !hasBackKey;
    }

    public void HideWithoutFlicker(final View view,ViewGroup parent){
        Animation animation=new TranslateAnimation(0, 0, 0, -view.getHeight());
        animation.setFillEnabled(true);

        animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animatiofillAftern) {
                // TODO Auto-generated method stub
                //                      mainBoardLinear.removeView(rLayout);
                view.setVisibility(View.GONE);

            }
        });

        parent.startAnimation(animation);
    }

    public static boolean isLandScape(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.orientation== Configuration.ORIENTATION_LANDSCAPE;
    }

    public static float getNavHight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    public static void showSystemUI(Activity c) {
        View decorView = c.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public static void test_showSystemUI(Activity c) {
        View decorView = c.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public static void hideSystemUI(Activity c) {
        if (Build.VERSION.SDK_INT < 16) {
            c.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        View decorView = c.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            decorView.setSystemUiVisibility(
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);

        }
    }

    public static void hideView_with_ZoomIn(ViewGroup parent) {
        RevealView_withScale(parent, false);
    }

    public static void ShowView_with_ZoomOut(ViewGroup parent) {
        RevealView_withScale(parent, true);
    }

    private static void RevealView_withScale(ViewGroup parent, boolean visibility) {
        TransitionSet set = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            set = new TransitionSet()
                    .addTransition(new Scale(0.84f))
//                    .addTransition(new com.transitionseverywhere.Fade())
                    .setInterpolator(visibility ? new LinearOutSlowInInterpolator() :
                            new FastOutLinearInInterpolator());
            TransitionManager.beginDelayedTransition(parent, set);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//        }
    }

    public Handler getHandler() {
        return handler;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public static void Select_View(int position, boolean check_status, ArrayList<LocalHandout> item_list) {
        item_list.get(position).setChecked(check_status);
    }

    /**
     * This will be called in ACTION_DOWN of the onTouch() listener
     * then you should call handler.removeCallback on onScroll or OnMove event(
     */
    public boolean is_long_clicked(final MotionEvent e) {
        handler = new Handler();
        handler.postDelayed(runnable, ViewConfiguration.getLongPressTimeout());
        return is_long_clicked;
    }

    private boolean is_click_not_toFlip(MotionEvent e) {
        float max_X = e.getDevice().getMotionRange(MotionEvent.AXIS_X).getMax();
        return ((max_X / e.getX()) >= 1.087613F && (max_X / e.getX()) <= 12.416F);
    }


}
