package com.example.abumuhsin.udusmini_library.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.dueeeke.tablayout.SegmentTabLayout;
import com.dueeeke.tablayout.listener.OnTabSelectListener;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.adapters.pagerAdapter;
import com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.firebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.fragments.Books_fragment;
import com.example.abumuhsin.udusmini_library.fragments.Discussion_fragment;
import com.example.abumuhsin.udusmini_library.fragments.Download_fragment;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.fragments.OnlineBook_fragment;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tenclouds.fluidbottomnavigation.FluidBottomNavigation;
import com.tenclouds.fluidbottomnavigation.FluidBottomNavigationItem;
import com.tenclouds.fluidbottomnavigation.listener.OnTabSelectedListener;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.example.abumuhsin.udusmini_library.firebaseStuff.services.FirebaseMessageService.TOKEN_NODE;
import static com.example.abumuhsin.udusmini_library.firebaseStuff.services.FirebaseMessageService.USERS_TOKEN_NODE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
        , OnlineLocalHandoutListener, AdapterView.OnItemSelectedListener {
    public static final int LOGIN_REQUEST_CODE = 43345;
    public static final int MY_BOOK_INDEX = 0;
    public static final int ONLINE_BOOK_INDEX = 1;
    public static final String STUDENT_INFO_EXTRA = "student_info_EXTRA";
    public static final int DISCUSSION_FRAGMENT_INDEX = 2;
    public static final int PDF_FRAGMENT_INDEX = 3;
    public static final int GALLERY_FRAGMENT_INDEX = 4;
    public static final int DEPARTMENT_INDEX = 0;
    public static final int RECENT_INDEX = 1;
    //    public static final int FACULTY_INDEX = 1;
    public static final int GST_INDEX = 2;
    public static final int AUTHOR_INDEX = 1;
    public static final int FACULTY_INDEX = 0;
    public static final String IS_FOR_POS = "is_for_pos";
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
    private ImageButton add_Btn;
    private Spinner filter_spinner;
    private ArrayAdapter<String> filter_adapter;
    private Discussion_fragment discussion_fragment;
    private Download_fragment download_fragment;
    private SegmentTabLayout tabLayout_1;
    private String[] mTitles = {"   Local    ", "    Online    "/*, "    Pdf ", "    Gallery "*/};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitFileVersionCompatibility();
        EnableFirebasePersistence(savedInstanceState);
        init();
        settingUpTabs();
        setUpBottomNav();
        settingUpNavigation();
        FirebaseLoginOperation.get(this);
        FirebaseUser firebaseCurrentUser = FirebaseLoginOperation.getCurrentUser();
        if (firebaseCurrentUser != null) {
            initFCM();
        }
    }

    private void InitFileVersionCompatibility() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "something went wrong with storage stuffs");
            }
        }
    }

    private void EnableFirebasePersistence(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            try {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                FirebaseMessaging.getInstance().subscribeToTopic("CMP");
//                if (firebaseApp != null) {
//                    firebaseApp.setAutomaticResourceManagementEnabled(true);
//                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "something went wrong with either persistence or subscription");
            }
        }

    }
    FluidBottomNavigation fluidBottomNavigation;
    BottomNavigationView bottomNavigationView;
    private void setUpBottomNav() {
//        bottomNavigationView = findViewById(R.id.BottomNavigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationItemSelectedListener);
//        bottomNavigationView.setSelectedItemId(R.id.handouts);
        fluidBottomNavigation = findViewById(R.id.fluidBottomNavigation);
        List<FluidBottomNavigationItem> list = new ArrayList<>();
        list.add( new FluidBottomNavigationItem(getString(R.string.chat),ContextCompat.getDrawable(this, R.drawable.ic_chat)));
        list.add( new FluidBottomNavigationItem(
                getString(R.string.books),
                ContextCompat.getDrawable(this, R.drawable.ic_news)));
        list.add( new FluidBottomNavigationItem(
                getString(R.string.history),
                ContextCompat.getDrawable(this, R.drawable.ic_history_black_24dp)));
        fluidBottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        fluidBottomNavigation.setBackColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        fluidBottomNavigation.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        fluidBottomNavigation.setIconColor(ContextCompat.getColor(this, R.color.colorPrimary));
        fluidBottomNavigation.setIconSelectedColor(ContextCompat.getColor(this, R.color.iconSelectedColor));

        fluidBottomNavigation.setItems(list);
        fluidBottomNavigation.setSelectedTabPosition(1);
        fluidBottomNavigation.setOnTabSelectedListener(onBottomTabSelectedListener);
    }

    private void initFCM() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                SendRegTokenToDatabase(newToken);
            }
        });
    }

    private void SendRegTokenToDatabase(String newToken) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            databaseReference.child(USERS_TOKEN_NODE)
                    .child(currentUser.getUid())
                    .child(TOKEN_NODE)
                    .setValue(newToken);
        }else {
            Log.e(TAG, "No user Login yet");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        adapter.clearAll();
    }

    @Override
    public void onHandoutDownloadFromOnline(Handout handout, File zip_file, File cover_file) {
        myBook_fragment.AddZipBook(handout.getHandout_title(), Uri.fromFile(zip_file), cover_file);
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
        final String[] filters_online = new String[]{"Faculty", "author"};
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
//                if (isMyBooksSelected()) {
//                    myBook_fragment.LoadBooksWithDepartment();
//                } else if (isOnlineBooksSelected()) {
//                    onlineBook_fragment.LoadHandoutWithFaculties();
//                }
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
        add_Btn = findViewById(R.id.add_Btn);
        tabLayout_1 = findViewById(R.id.tl_1);
        tabLayout_1.setTabData(mTitles);
        tabLayout_1.setIndicatorColor(getResources().getColor(R.color.colorPrimary));
        tabLayout_1.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                pager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

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
                } else if (isOnlineBooksSelected()) {
                    onlineBook_fragment.onSerchQuery(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isMyBooksSelected()) {
                    myBook_fragment.onSerchQuery(newText);
                } else if (isOnlineBooksSelected()) {
                    onlineBook_fragment.onSerchQuery(newText);
                }
                return false;
            }
        });
        HandleNavHeader();
    }

    private void HandleNavHeader() {
        FirebaseHandoutOperation firebaseHandoutOperation = new FirebaseHandoutOperation(this);
        View headerView = nav.getHeaderView(0);
        final ImageView user_img_view = headerView.findViewById(R.id.user_img);
        final TextView display_tv = headerView.findViewById(R.id.user_full_name);
        firebaseHandoutOperation.LoadCurrentStudentImage(false, new FirebaseHandoutOperation.OnStudentImageLoaded() {
            @Override
            public void StudentImageLoaded(String student_image) {
                try {
                    GlideApp.with(MainActivity.this).load(student_image).placeholder(R.drawable.trimed_logo).into(user_img_view);
                    Log.i(FirebaseHandoutOperation.OPERATION_TAG, "profile pic loading succeed");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(FirebaseHandoutOperation.OPERATION_TAG, "profile pic loading failed with glide");
                }
            }

            @Override
            public void StudentImageLoadFailed(Object error) {
                Log.i(FirebaseHandoutOperation.OPERATION_TAG, "profile pic loading failed with database");
            }
        });
        firebaseHandoutOperation.LoadCurrentStudentName(new FirebaseHandoutOperation.OnStudentNameLoaded() {
            @Override
            public void StudentNameLoaded(String student_name) {
                display_tv.setText(student_name);
                display_tv.append("'s Handout");
            }

            @Override
            public void StudentNameLoadFailed(Object error) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_Btn) {
            pager.setCurrentItem(MY_BOOK_INDEX);
            myBook_fragment.add_A_Book();
        }
    }

    private void settingUpTabs() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        books_fragment = new Books_fragment();
        Intent intent = getIntent();
        if (intent != null) {
            setUpReceievedBook(intent);
        } else {
            Log.i(TAG, "intent is null");
        }
//        FragmentManager manager = getSupportFragmentManager();
//        manager.beginTransaction().add(R.id.frag, books_fragment).commit();
//        setTitle("Books");
        myBook_fragment = new MyBook_fragment();
        sendZipBundleToMyBook();
        onlineBook_fragment = new OnlineBook_fragment();
        adapter.addFragments(myBook_fragment, getResources().getString(R.string.local));
        adapter.addFragments(onlineBook_fragment, getResources().getString(R.string.online));
        pager.setAdapter(adapter);
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
//        tab.setupWithViewPager(pager);
//        tab.addOnTabSelectedListener(this);
//        pager.setCurrentItem(selected_fragment);
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
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
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
            case R.id.pdf:
                startActivity(new Intent(this, MyPdfActivity.class));
                break;
            case R.id.gallery:
                startActivity(new Intent(this, MyGalleryActivity.class));
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

    public ViewPager getPager() {
        return pager;
    }
    Books_fragment books_fragment;

    OnTabSelectedListener onBottomTabSelectedListener = new OnTabSelectedListener() {
        @Override
        public void onTabSelected(int position) {
            switch (position) {
                case 0:
                    hideBooks();
                    FragmentManager manager2 = getSupportFragmentManager();
                    manager2.beginTransaction().replace(R.id.frag, new Discussion_fragment()).commit();
                    ChangeTitle(getResources().getString(R.string.discussion));
                    break;
                case 1:
                    pager.setVisibility(View.VISIBLE);
                    tabLayout_1.setVisibility(View.VISIBLE);
                    ChangeTitle(getResources().getString(R.string.books));
                    break;
                case 2:
                    hideBooks();
                    FragmentManager manager3 = getSupportFragmentManager();
                    manager3.beginTransaction().replace(R.id.frag, new Download_fragment()).commit();
                    ChangeTitle(getResources().getString(R.string.history));
            }

        }
    };

    private void ChangeTitle(String title) {
        ((TextView) findViewById(R.id.title_tv)).setText(title);
    }

    private void hideBooks() {
        pager.setVisibility(View.GONE);
        tabLayout_1.setVisibility(View.GONE);
    }

}
