package com.example.abumuhsin.udusmini_library.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class Handout_detail_adapter extends FragmentStatePagerAdapter {
    private  final List<Fragment>fragmentList = new ArrayList<>();
    private  final List<String> fragmentTitles = new ArrayList<>();
    public Handout_detail_adapter(FragmentManager fm) {
        super(fm);
    }

    public void clearAll() {
        fragmentList.clear();
        fragmentTitles.clear();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }

    public void addFragments(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentTitles.add(title);
    }
}
