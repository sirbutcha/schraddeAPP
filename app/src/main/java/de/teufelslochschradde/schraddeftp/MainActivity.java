package de.teufelslochschradde.schraddeftp;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import de.teufelslochschradde.schraddeftp.ftp_com.Butcha_FTP;


public class MainActivity extends AppCompatActivity{

    private static final int NUM_PAGES = 4;

    private Butcha_FTP mFtpClient;

    public ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    boolean bStarted = false;

    String mSelectedEvent = null;
    String mSelectedYear = null;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==2 && mSelectedYear != null){
                    fab.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_create_new_folder_40dp));
                    fab.setVisibility(View.VISIBLE);
                } else if(position==3 && mSelectedEvent != null){
                        fab.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_image_40dp));
                        fab.setVisibility(View.VISIBLE);

                } else {
                    fab.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mFtpClient = new Butcha_FTP();

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new StartFragment();
                case 1:
                   return new ChooseYearFragment();
                case 2:
                    return new ChooseEventFragment();
                case 3:
                    return new ChooseImgsFragment();
                default:
                    return new StartFragment();
            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public Butcha_FTP getFtpClient(){
        return mFtpClient;
    }

    public void setSelectedEvent(String event){
        mSelectedEvent = event;
    }

    public String getSelectedEvent(){
        return mSelectedEvent;
    }

    public void setSelectedYear(String year){
        mSelectedYear = year;
    }

    public String getSelectedYear(){
        return mSelectedYear;
    }


}
