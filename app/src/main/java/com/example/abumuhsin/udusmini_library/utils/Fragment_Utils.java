package com.example.abumuhsin.udusmini_library.utils;

import java.util.HashMap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

;

/**
 * Created by Abu Muhsin on 10/11/2018.
 */

public class Fragment_Utils {
    private HashMap<String, Fragment.SavedState> saveState_map = new HashMap<>();

    public  Fragment_Utils() {
    }

    public void saveState(Fragment fragment, String key) {
        if (!fragment.isAdded()) {
            fragment.setInitialSavedState(saveState_map.get(key));
        }
    }

    public void RestoreState(FragmentManager fragmentManager, Fragment fragment, String key) {
        if (fragment.isAdded()) {
            saveState_map.put(key,fragmentManager.saveFragmentInstanceState(fragment));
        }
    }
}
