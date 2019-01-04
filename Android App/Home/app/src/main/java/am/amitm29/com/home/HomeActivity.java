package am.amitm29.com.home;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import am.amitm29.com.home.Database.AppDatabase;
import am.amitm29.com.home.Database.DbHelper;
import am.amitm29.com.home.Utils.BTConnectionUtils;

/*
    HomeActivity is the main Activity of the app, which contains a
    frame which is replaced by different fragments, other activities
    are opened over this activity
 */

public class HomeActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    public NavigationView mNavigationView;
    public static HomeActivity mInstance;
    Toast toast;
    String email = "developer.amitm29@gmail.com";
    String subject = "Feedback (Home Automation)";
    private static int backPressCount = 0;

    private static final String LOG_TAG = "HomeActivity";


    public static HashMap<Character, Character> dashboardState, state1, state2;

    //Tags for the child fragments of the HomeFragment
    public static String TAG_FIRST, TAG_HOME, TAG_STATS;

    // Keys for the shared preferences
    public static String STATS_ID = "stats_id", LAST_UPDATED = "last_updated";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TAG_FIRST = "first";
        TAG_HOME = "home";
        TAG_STATS = "stats";

        //When the app is launched, the frame in HomeActivity is replaced by FirstFragment
        FirstFragment fragment = new FirstFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, TAG_FIRST).commit();

        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // state1 and state2 are used to determine if a timer is interrupted or not
        state1 = new HashMap<>(10);
        state2 = new HashMap<>(10);

        // Contains the state of the switches in the DashboardFragment
        dashboardState = new HashMap<>(10);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_dashboard:
                        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                        if(BTConnectionUtils.isBtConnected) {
                            HomeFragment.num = 0;
                            if (homeFragment == null || !homeFragment.isVisible()) {
                                HomeFragment fragment = new HomeFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, TAG_HOME).
                                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                            } else
                                HomeFragment.setPage();
                            item.setChecked(true);
                        }
                        else
                            Toast.makeText(HomeActivity.this, "Please Connect the device first!", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_timers:
                        HomeFragment homeFragment1 = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                        if(BTConnectionUtils.isBtConnected) {
                            HomeFragment.num = 1;
                            if (homeFragment1 == null || !homeFragment1.isVisible()) {
                                HomeFragment fragment = new HomeFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, TAG_HOME).
                                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                            } else
                                HomeFragment.setPage();
                            item.setChecked(true);
                        }
                        else
                            Toast.makeText(HomeActivity.this, "Please Connect the device first!", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_keypad:
                        HomeFragment homeFragment2 = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                        if (BTConnectionUtils.isBtConnected) {
                            HomeFragment.num = 2;
                            if (homeFragment2 == null || !homeFragment2.isVisible()) {
                                HomeFragment fragment = new HomeFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, TAG_HOME).
                                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                            } else
                                HomeFragment.setPage();
                            item.setChecked(true);
                        }
                        else
                            Toast.makeText(HomeActivity.this, "Please Connect the device first!", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_stats:
                        StatsFragment statsFragment = (StatsFragment) getSupportFragmentManager().findFragmentByTag(TAG_STATS);
                        if (statsFragment == null || !statsFragment.isVisible()) {
                            StatsFragment fragment = new StatsFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, TAG_STATS).
                                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        }
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                         break;
                    case R.id.nav_settings:     mDrawerLayout.closeDrawers();
                    break;
                    case R.id.app_guide:

//                        Intent intent3 = new Intent(HomeActivity.this, HelpActivity.class);
//                        startActivity(intent3);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.contact_us:
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", email, null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        final PackageManager pm = getPackageManager();
                        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                        ResolveInfo best = null;
                        for(final ResolveInfo info : matches)
                            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                                best = info;
                        if (best != null)
                            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

                        startActivity(emailIntent);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.about_dev:

                        Intent devIntent = new Intent(HomeActivity.this, DevActivity.class);
                        startActivity(devIntent);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.rate_app:
//                        startActivity(new Intent(android.content.Intent.ACTION_VIEW)
//                                .setData(Uri.parse("https://play.google.com/store/apps/details?id=com.amitm29.am.dailyexpensesmanager")));
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.share:
//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                        shareIntent.setType("text/plain");
//                        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.amitm29.am.dailyexpensesmanager");
//                        if (shareIntent.resolveActivity(getPackageManager()) != null) {
//                            startActivity(shareIntent);
//                        }
                        mDrawerLayout.closeDrawers();
                        break;

                    default:
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(HomeActivity.this, "Invalid Action!", Toast.LENGTH_SHORT);
                        toast.show();


                }

                return false;
            }
        });

        mInstance = this;

    }

    @Override
    protected void onResume() {
        super.onResume();
        backPressCount = 0;
        DbHelper.setmDb(AppDatabase.getsInstance(this));
        mNavigationView.getMenu().getItem(0).setChecked(false);
        if(BTConnectionUtils.isBtConnected)
        {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
            if(homeFragment ==null || !homeFragment.isVisible()) {
                HomeFragment fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, TAG_HOME).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                mNavigationView.getMenu().getItem(0).setChecked(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        StatsFragment statsFragment = (StatsFragment) getSupportFragmentManager().findFragmentByTag(TAG_STATS);

        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);

        FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentByTag(TAG_FIRST);

        //if drawer is open, close it
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }

        else if(homeFragment != null && homeFragment.isVisible()){
            /*
                if HomeFragment is visible and in it DashboardFragment is not visible,
                then move to the DashboardFragment and else if the DashboardFragment is
                visible then check for backPressCount and take the desired action
             */
            if (!HomeFragment.isDashboardFragmentVisible) {
                HomeFragment.num = 0;
                HomeFragment.setPage();
                mNavigationView.getMenu().getItem(0).setChecked(true);
            }else{
                if(backPressCount == 0) {
                    Toast.makeText(this, "Press again to exit!", Toast.LENGTH_SHORT).show();
                    backPressCount = 1;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                                backPressCount = 0;
                                Log.d(LOG_TAG, "Value to backPressCount updated to " + backPressCount);
                            }catch (Exception e){
                                Log.d(LOG_TAG, "exception occurred while sleeping and updating value");
                            }
                        }
                    }).start();
                }else{
                    super.onBackPressed();
                }
            }
        }
        /*
            else if FirstFragment is visible then check for backPressCount
            to take the further action
         */
        else if(firstFragment!=null && firstFragment.isVisible()){
            if(backPressCount == 0) {
                Toast.makeText(this, "Press again to exit!", Toast.LENGTH_SHORT).show();
                backPressCount = 1;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            backPressCount = 0;
                            Log.d(LOG_TAG, "Value to backPressCount updated to " + backPressCount);
                        }catch (Exception e){
                            Log.d(LOG_TAG, "exception occurred while sleeping and updating value");
                        }
                    }
                }).start();
            }else{
                super.onBackPressed();
            }

        }

        /*
            else if statsFragment is visible then check if bluetooth device is
            connected or not
            * if it is connected then replace the frame with HomeFragment, and
              open DashboardFragment in it
            * else replace it with FirstFragment
         */
        else if(statsFragment!=null && statsFragment.isVisible()){
            if(BTConnectionUtils.isBtConnected) {
                HomeFragment fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, TAG_HOME).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                mNavigationView.getMenu().getItem(0).setChecked(true);
            }else{
                FirstFragment fragment = new FirstFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, TAG_FIRST).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                mNavigationView.getMenu().getItem(3).setChecked(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

                    case android.R.id.home:
                        mDrawerLayout.openDrawer(GravityCompat.START);
                        return true;
                    case R.id.action_disconnect:
                        disconnectBT();
                        mNavigationView.getMenu().getItem(0).setChecked(false);
                        Toast.makeText(this, "Disconnected!", Toast.LENGTH_SHORT).show();
                }

        return super.onOptionsItemSelected(item);
    }

    /*
        * takes all the incoming data from the bluetooth input stream and
        * distributes it to rhe respective functions to process the data
     */
    public static void handleIncomingData(String message){
        try {
            /*
                * if temperature is 27.8 then the arduino sends the message Z27.8
                * so, we check that if the first character of the message is Z then
                * it is the temperature and we call updateTemp of DashboardFragment
             */
            if (message.charAt(0) == 'Z') {
                DashboardFragment fragment = (DashboardFragment) HomeFragment.adapter.getItem(0);
                fragment.updateTemp(message.substring(1));
            }
            /*
                * the arduino send the stats corresponding to a particular switch number as
                * Message : S0/2000
                * so if first character of the message is S then call updateStats of StatsFragment
             */
            else if (message.charAt(0) == 'S') {
                StatsFragment fragment3 = (StatsFragment) mInstance.getSupportFragmentManager().findFragmentByTag(TAG_STATS);
                fragment3.updateStats(message.substring(1));
            } else{
                /*
                    * Arduino sends a message like 21 if switch 2 is switched on
                    * and 20 if switch 2 is turned off
                    * updateSwitch method is called and this message from arduino is passed
                    * and it updates the switch state
                 */
                char c = message.charAt(0);
                if(c=='2' || c=='3' || c=='5' || c=='6' || c=='8' || c=='9' || c=='A' || c=='B' || c=='C') {
                    DashboardFragment fragment1 = (DashboardFragment) HomeFragment.adapter.getItem(0);
                    fragment1.updateSwitch(message);

                    /*
                        * the following block of code checks if the timer
                        * corresponding to the switches is interrupted
                        * if it is interrupted then the timer corresponding to that switch is deleted
                     */
                    state1.putAll(state2);
                    Log.d(LOG_TAG, message.charAt(0) + "\t" + state1.get(message.charAt(0)));
                    state2.put(message.charAt(0), message.charAt(1));

                    if (state1.containsKey(message.charAt(0)) && state1.get(message.charAt(0)) != state2.get(message.charAt(0))) {
                        TimersFragment fragment2 = (TimersFragment) HomeFragment.adapter.getItem(1);
                        fragment2.deleteTimerIfInterrupted(message);
                    }
                }

            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Error while handling message");}
    }

    /*
        * this method disconnects the device and replaces the frame
        * in HomeActivity with the FirstFragment
     */
    public void disconnectBT(){
        BTConnectionUtils.disconnect();
        FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentByTag(TAG_FIRST);
        if(firstFragment ==null || !firstFragment.isVisible()) {
            FirstFragment fragment = new FirstFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, TAG_FIRST).
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
    }

    public static HomeActivity getInstance()
    {
        if(mInstance == null)
            return new HomeActivity();
        else
            return mInstance;
    }




}


