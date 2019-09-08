package com.example.abumuhsin.udusmini_library.adapters;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class pagerAdapter extends FragmentStatePagerAdapter {
    private  final List<Fragment>fragmentList = new ArrayList<>();
    private  final List<String> fragmentTitles = new ArrayList<>();
    public pagerAdapter(FragmentManager fm) {
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
    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
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
