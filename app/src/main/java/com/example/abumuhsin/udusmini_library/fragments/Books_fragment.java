package com.example.abumuhsin.udusmini_library.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dueeeke.tablayout.SegmentTabLayout;
import com.dueeeke.tablayout.listener.OnTabSelectListener;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.MainActivity;
import com.example.abumuhsin.udusmini_library.adapters.pagerAdapter;
import com.example.abumuhsin.udusmini_library.firebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.utils.Fragment_Utils;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class Books_fragment extends Fragment {
    private String[] mTitles = {"   Local   ", "    Online  "};
    private View view;
    private ViewPager pager;
    private pagerAdapter adapter;
    private Uri zip_uri;
    MyBook_fragment myBook_fragment;
    SegmentTabLayout tabLayout_1;

    public Books_fragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView");
        view = inflater.inflate(R.layout.books_fragment_layout, container, false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
        if (adapter!=null) {
//            adapter = new pagerAdapter(getFragmentManager());
        myBook_fragment = new MyBook_fragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("zip_uri", zip_uri);
        Log.i(TAG, "uri is inserted in to the bundle");
        myBook_fragment.setArguments(bundle);
        OnlineBook_fragment onlineBook_fragment = new OnlineBook_fragment();
        adapter.clearAll();
        adapter.addFragments(myBook_fragment, "My Books");
        adapter.addFragments(onlineBook_fragment, "Online Books");
        adapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }
    private void saveState() {
        new Fragment_Utils().saveState(this, "yes");
    }
    private void initAdapters() {
        Log.e(TAG,"initAdapters");
        tabLayout_1.setTabData(mTitles);
        tabLayout_1.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                pager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        adapter = new pagerAdapter(getFragmentManager());
        myBook_fragment = new MyBook_fragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("zip_uri", zip_uri);
        Log.i(TAG, "uri is inserted in to the bundle");
        myBook_fragment.setArguments(bundle);
        OnlineBook_fragment onlineBook_fragment = new OnlineBook_fragment();
        adapter.addFragments(myBook_fragment, "My Books");
        adapter.addFragments(onlineBook_fragment, "Online Books");
        pager.setAdapter(adapter);
        pager.setCurrentItem(DEFAULT_PAGER_POS);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout_1.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private static final String TAG = "Books_fragment";
    private int DEFAULT_PAGER_POS = 0;
    @Override
    public void onAttach(@NonNull Context context) {
        Log.e(TAG,"onAttach");
        super.onAttach(context);
        Bundle arguments = getArguments();
        FirebaseLoginOperation.get(this.requireActivity());
        if (arguments != null) {
            String pos_arg = arguments.getString("pos");
            if (pos_arg !=null && pos_arg.equals(MainActivity.IS_FOR_POS)){
                DEFAULT_PAGER_POS = 0;
                if (pager!=null){
                    pager.setCurrentItem(DEFAULT_PAGER_POS);
//                    tabLayout_1.setCurrentTab(0);
                    if (myBook_fragment!=null){
                        myBook_fragment.add_A_Book();
                    }
                }
            }
            zip_uri = arguments.getParcelable("zip_uri");
        }
    }

    private void init(View view) {
        intiViews(view);
        initAdapters();
    }

    public MyBook_fragment getMyBook_fragment() {
        return myBook_fragment;
    }

    private void intiViews(View view) {
        pager = view.findViewById(R.id.pager);
        tabLayout_1 = view.findViewById(R.id.tl_1);

    }


}
