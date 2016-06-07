package com.example.login.logindemo;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.majiajie.pagerbottomtabstrip.Controller;
import me.majiajie.pagerbottomtabstrip.PagerBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.TabItemBuilder;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectListener;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tab)
    PagerBottomTabLayout tab;
    @BindView(R.id.fragmentLayout)
    FrameLayout fragmentLayout;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.appbarLayout)
    AppBarLayout appbarLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawable_layout)
    DrawerLayout drawableLayout;

    private ArrayList<Fragment> fragmentList;
    //private int[] tabColors = {0xFF7BA3A8,0xFFF4F3DE,0xFFBEAD92,0xFFF35A4A,0xFF5B4947};
    //private int[] tabColors = {0xFF00796B,0xFF8D6E63,0xFF2196F3,0xFF607D8B,0xFFF57C00};
    private int[] tabColors = {0xFF00796B, 0xFF5B4947, 0xFF607D8B, 0xFFF57C00, 0xFFF57C00};
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
        ButterKnife.bind(this);

//        initToolBar();
//        initDrawerLayout();
//
//        initFragment();
//
//        initBottomTab();


    }

    private void initFragment() {
        fragmentList = new ArrayList<>();

        fragmentList.add(new Fragment0());
        fragmentList.add(new Fragment1());
        fragmentList.add(new Fragment2());
        fragmentList.add(new Fragment3());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
        transaction.add(R.id.fragmentLayout, fragmentList.get(0));
        transaction.commit();
    }

    private void initBottomTab() {
        //用TabItemBuilder构建一个导航按钮
        TabItemBuilder tabItemBuild = new TabItemBuilder(this).create()
                .setDefaultIcon(android.R.drawable.ic_menu_send)
                .setText("信息")
                .setSelectedColor(tabColors[0])
                .setTag("A")
                .build();
        //构建导航栏,得到Controller进行后续控制
        controller = tab.builder()
                .addTabItem(tabItemBuild)
                .addTabItem(android.R.drawable.ic_menu_compass, "位置", tabColors[1])
                .addTabItem(android.R.drawable.ic_menu_search, "搜索", tabColors[2])
                .addTabItem(android.R.drawable.ic_menu_help, "帮助", tabColors[3])
                //.setMode(TabLayoutMode.CHANGE_BACKGROUND_COLOR)
                //.setMode(TabLayoutMode.HIDE_TEXT)
                //.setMode(TabLayoutMode.CHANGE_BACKGROUND_COLOR&TabLayoutMode.HIDE_TEXT)
                .build();
        controller.addTabItemClickListener(listener);
    }

    OnTabItemSelectListener listener = new OnTabItemSelectListener() {
        @Override
        public void onSelected(int index, Object tag) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
            transaction.replace(R.id.fragmentLayout, fragmentList.get(index));
            transaction.commit();
            Log.e("asd", "onSelected:" + index + "   TAG: " + tag.toString());
        }

        @Override
        public void onRepeatClick(int index, Object tag) {
            Log.e("asd", "onRepeatClick:" + index + "   TAG: " + tag.toString());
        }
    };

    private void initDrawerLayout() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Snackbar.make(drawableLayout, item.getTitle(), Snackbar.LENGTH_LONG).show();
                item.setChecked(true);
                drawableLayout.closeDrawers();
                return true;
            }
        });
    }

    private void initToolBar() {

        setSupportActionBar(toolBar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_name);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(drawableLayout.isDrawerOpen(navigationView))
        {
            drawableLayout.closeDrawers();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawableLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
