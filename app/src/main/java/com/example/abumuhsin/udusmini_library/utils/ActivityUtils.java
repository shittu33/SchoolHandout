package com.example.abumuhsin.udusmini_library.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

/**
 * Created by Abu Muhsin on 09/10/2018.
 */

public class ActivityUtils {

    public static void start_A_New_Activity_With_Extra(Activity activity,Class aClass, String key, String extra) {
        Intent intent = get_activity_Intent(activity,aClass);
        intent.putExtra(key, extra);
        activity.startActivity(intent);
    }
    public static Intent get_activity_Intent(Activity activity,Class aClass) {
        return new Intent(activity, aClass);
    }

    public static void start_A_New_Activity(Activity activity,Class aClass) {
        activity.startActivity(get_activity_Intent(activity,aClass));
    }
    /**To change Input type of an EditText*/
    public static void changeInputType(View v){
        if (v instanceof EditText){
            ((EditText) v).setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

}
