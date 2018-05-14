package com.spotxchange.testapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.spotxchange.v4.SpotX;

public class MainActivity extends AppCompatActivity
        implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    private ViewPager _viewPager;
    private TabLayout _tabLayout;
    private Menu _toolbarMenu;

    private static int _currentTab; // Store current tab statically so it can survive the activity being re-created

    public enum Fragments {
        SDK,
        MOPUB,
        BRIGHTCOVE
    }

    /** Currently-selected screen. */
    public static Fragments getCurrentTab() {
        return Fragments.values()[_currentTab];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        // Enable SpotX logging
        SpotX.debugMode = true;

        _tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        _tabLayout.addOnTabSelectedListener(this);

        _viewPager = (ViewPager)findViewById(R.id.viewPager);
        _viewPager.addOnPageChangeListener(this);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getFragmentManager());
        _viewPager.setAdapter(adapter);

        _tabLayout.getTabAt(_currentTab).select();
        _viewPager.setCurrentItem(_currentTab);
    }

    // MARK: Menu actions

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_actions, menu);
        _toolbarMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Display settings screen
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // MARK: OnTabSelectedListener

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        _viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    // MARK: OnPageChangeListener

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        // Update tab layout if needed
        if (_tabLayout.getSelectedTabPosition() != position) {
            _tabLayout.getTabAt(position).select();
        }
        _currentTab = position;

        if (position == Fragments.SDK.ordinal()) {
            // SDK screen: Set toolbar to green
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.spotxGreen)));
        } else {
            // MoPub and Brightcove: set toolbar to blue
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.spotxBlue)));
        }

        if (position == Fragments.SDK.ordinal()
                || position == Fragments.BRIGHTCOVE.ordinal()) {
            // Show settings icon for SDK and Brightcove
            if (_toolbarMenu != null) {
                _toolbarMenu.setGroupVisible(0, true);
            }
        } else {
            // Hide it for MoPub as settings are controlled externally
            if (_toolbarMenu != null) {
                _toolbarMenu.setGroupVisible(0, false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    // MARK: FragmentPagerAdapter

    public class FragmentStatePagerAdapter extends FragmentPagerAdapter {
        private SparseArray<Fragment> _registeredFragments = new SparseArray<>();

        public FragmentStatePagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public int getCount() {
            return Fragments.values().length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (Fragments.values()[position]) {
                case SDK:
                    return new SDKFragment();
                case MOPUB:
                    return new MoPubFragment();
                case BRIGHTCOVE:
                    return new BrightcoveFragment();
            }
            return new Fragment();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            _registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            _registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }
    }
}
