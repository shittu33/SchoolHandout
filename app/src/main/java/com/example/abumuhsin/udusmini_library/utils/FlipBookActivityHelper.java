package com.example.abumuhsin.udusmini_library.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.abumuhsin.udusmini_library.R;

/**
 * Created by Abu Muhsin on 09/10/2018.
 */

public class FlipBookActivityHelper {

    public static void start_A_New_Activity_With_Extra(Activity activity, Class aClass, String key, String extra) {
        Intent intent = get_activity_Intent(activity, aClass);
        intent.putExtra(key, extra);
        activity.startActivity(intent);
    }

    public static Intent get_activity_Intent(Activity activity, Class aClass) {
        return new Intent(activity, aClass);
    }

    public static void start_A_New_Activity(Activity activity, Class aClass) {
        activity.startActivity(get_activity_Intent(activity, aClass));
    }

    /**
     * To change Input type of an EditText
     */
    public static void changeInputType(View v) {
        if (v instanceof EditText) {
            ((EditText) v).setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

    public static void ToggleScreenStatus(Activity activity, boolean is_screen_always) {
        if (is_screen_always) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }
    }

    public static void ShowSpeedDialog(Context context, int selected_speed, final SpeedDialogListener speedDialogListener) {
        final CharSequence[] items = {"Slow", "Normal", "Fast", "Very Fast"};
        new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("Change Flip speed")
                .setSingleChoiceItems(items
                        , selected_speed, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                speedDialogListener.onSpeedDialogShown(i,items[i].toString());
                            }
                        })
                .show();

    }

    public interface SpeedDialogListener {
        void onSpeedDialogShown(int selected_speed, String selected_text);
    }
}
