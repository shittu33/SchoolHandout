package com.example.abumuhsin.udusmini_library.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.adapters.pagerAdapter;
import com.example.abumuhsin.udusmini_library.fragments.GalleryBook_fragment;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.fragments.OnlineBook_fragment;
import com.example.abumuhsin.udusmini_library.fragments.PDFBook_fragment;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
        , OnlineLocalHandoutListener, TabLayout.OnTabSelectedListener, AdapterView.OnItemSelectedListener {
    public static final int LOGIN_REQUEST_CODE = 43345;
    public static final int MY_BOOK_INDEX = 0;
    public static final int ONLINE_BOOK_INDEX = 1;
    public static final String STUDENT_INFO_EXTRA = "student_info_EXTRA";
    public static final int PDF_FRAGMENT_INDEX = 2;
    public static final int GALLERY_FRAGMENT_INDEX = 3;
    public static final int DEPARTMENT_INDEX = 0;
    public static final int RECENT_INDEX = 1;
    //    public static final int FACULTY_INDEX = 1;
    public static final int GST_INDEX = 2;
    public static final int AUTHOR_INDEX = 1;
    int selected_fragment = 0;
    private final String TAG = "active";
    private DrawerLayout draw;
    private ActionBarDrawerToggle toggle;
    private androidx.appcompat.app.ActionBar actionBar;
    private FragmentManager fragmentManager;
    private NavigationView nav;
    private TabLayout tab;
    private Toolbar toolbar;
    //    private View tab_ViewItem;
    private ViewPager pager;
    private pagerAdapter adapter;
    private MyBook_fragment myBook_fragment;
    private OnlineBook_fragment onlineBook_fragment;
    private GalleryBook_fragment galleryBook_fragment;
    private PDFBook_fragment pdf_Book_fragment;
    private ImageButton add_Btn;
    private Spinner filter_spinner;
    private ArrayAdapter<String> filter_adapter;

    public ViewPager getPager() {
        return pager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        settingUpTabs();
        settingUpNavigation();
        FirebaseLoginOperation.get(this);
//        HandleNavHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        adapter.clearAll();
    }

    @Override
    public void onHandoutDownloadFromOnline(Handout handout, File dest_file) {
        myBook_fragment.AddZipBook(Uri.fromFile(dest_file));
    }

    private void init() {
        initViews();
        fragmentManager = getSupportFragmentManager();
        initAdapters();
        nav.setNavigationItemSelectedListener(this);
        add_Btn.setOnClickListener(this);
    }

    private void initAdapters() {
        adapter = new pagerAdapter(fragmentManager);
        final String[] filters = new String[]{"Department"};
        final String[] filters_online = new String[]{"Faculty","author"};
        filter_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        filter_spinner.setAdapter(filter_adapter);
    }

    boolean isMyBooksSelected() {
        return pager.getCurrentItem() == MY_BOOK_INDEX;
    }

    boolean isOnlineBooksSelected() {
        return pager.getCurrentItem() == ONLINE_BOOK_INDEX;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case DEPARTMENT_INDEX:
                if (isMyBooksSelected()) {
                    myBook_fragment.LoadBooksWithDepartment();
                } else if (isOnlineBooksSelected()) {

                }
                break;
            case AUTHOR_INDEX:
                if (isMyBooksSelected()) {

                } else if (isOnlineBooksSelected()) {

                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void initViews() {
//        tab_ViewItem = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        add_Btn = findViewById(R.id.add_Btn);
        filter_spinner = findViewById(R.id.filter_spinner);
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.search_view);
        toolbar = findViewById(R.id.toolbar);
        tab = findViewById(R.id.tab);
        pager = findViewById(R.id.pager);
        nav = findViewById(R.id.nav);
        draw = findViewById(R.id.layout);
        add_Btn.setOnClickListener(this);
        filter_spinner.setOnItemSelectedListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_spinner.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                filter_spinner.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (isMyBooksSelected()) {
                    myBook_fragment.onSerchQuery(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isMyBooksSelected()) {
                    myBook_fragment.onSerchQuery(newText);
                }
                return false;
            }
        });
        HandleNavHeader();
    }

    private void HandleNavHeader(){
        FirebaseUser current_user = FirebaseLoginOperation.getCurrentUser();
        View headerView = nav.getHeaderView(0);
        ImageView user_img_view = headerView.findViewById(R.id.user_img);
        TextView display_tv = headerView.findViewById(R.id.user_full_name);
        if (current_user != null) {
            Uri photo_url = current_user.getPhotoUrl();
            String user_name = current_user.getDisplayName();
            display_tv.setText(user_name);
            GlideApp.with(this).load(photo_url).placeholder(R.drawable.trimed_logo).into(user_img_view);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_Btn) {
            pager.setCurrentItem(MY_BOOK_INDEX);
            myBook_fragment.add_A_Book();
//            if (pager.getCurrentItem() == 0)
//            else if (pager.getCurrentItem() == 1)
//                onlineBook_fragment.Test();
        }
    }

    private void settingUpTabs() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        Intent intent = getIntent();
        if (intent != null) {
            setUpReceievedBook(intent);
        } else {
            Log.i(TAG, "intent is null");
        }
        myBook_fragment = new MyBook_fragment();
        sendZipBundleToMyBook();
        onlineBook_fragment = new OnlineBook_fragment();
        galleryBook_fragment = new GalleryBook_fragment();
        pdf_Book_fragment = new PDFBook_fragment();
        adapter.addFragments(myBook_fragment, "My Books");
        adapter.addFragments(onlineBook_fragment, "Online Books");
        adapter.addFragments(pdf_Book_fragment, "PDF Books");
        adapter.addFragments(galleryBook_fragment, "Gallery");
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
        tab.addOnTabSelectedListener(this);
        pager.setCurrentItem(selected_fragment);
    }

    private void sendZipBundleToMyBook() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("zip_uri", zip_uri);
        Log.i(TAG, "uri is inserted in to the bundle");
        bundle.putString("zip_path", zip_file_path == null ? "" : zip_file_path);
        myBook_fragment.setArguments(bundle);
    }

    boolean is_book_received;
    String zip_file_path;
    Uri zip_uri;

    private void setUpReceievedBook(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        Log.i(TAG, " b4 testing");
        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            Log.i(TAG, " from another app");
            if (type.endsWith("zip")) {
                is_book_received = true;
                zip_uri = intent.getData();
                if (zip_uri != null) {
                    Log.i(TAG, " b4 zip path");
                    zip_file_path = zip_uri.getPath();

//                    zip_file_path = FileUtils.getUriRealPath(this,zip_uri);
                    Log.i(TAG, "zip file path is " + zip_file_path);
                } else {
                    Log.i(TAG, "zip_uri is empty ");
                }
            }
        } else if (Intent.ACTION_MAIN.equals(action)) {
            Log.i(TAG, "This is just a normal startup");
        }
    }


    private void settingUpNavigation() {
        toggle = new ActionBarDrawerToggle(this, draw, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                changeBarTitle("Options");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                changeBarTitle("Books");
            }
        };
        toggle.syncState();
        draw.addDrawerListener(toggle);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(this, profile_Activity.class));
                break;
            case R.id.guide:

                break;
            case R.id.settings:

                break;
            case R.id.about:

                break;
            case R.id.login:
                startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
                break;
        }
        item.setChecked(true);
        draw.closeDrawers();
        return false;
    }

    @Override
    public void onBackPressed() {
        if (draw.isDrawerOpen(GravityCompat.START)) {
            draw.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item);
    }

    private void changeBarTitle(String title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == LoginActivity.STUDENT_LOGGED_IN_CODE) {
//                selected_fragment =ONLINE_BOOK_INDEX;
                pager.setCurrentItem(ONLINE_BOOK_INDEX);
            } else if (resultCode == LoginActivity.SIGN_OUT_CODE) {
//                selected_fragment =MY_BOOK_INDEX;
                pager.setCurrentItem(MY_BOOK_INDEX);
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == PDF_FRAGMENT_INDEX || tab.getPosition() == GALLERY_FRAGMENT_INDEX) {
            filter_spinner.setVisibility(View.GONE);
            add_Btn.setVisibility(View.GONE);
        } else {
            filter_spinner.setVisibility(View.VISIBLE);
            add_Btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
