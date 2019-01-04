package am.amitm29.com.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import am.amitm29.com.home.Database.DbHelper;
import am.amitm29.com.home.Database.Stats;
import am.amitm29.com.home.Utils.BTConnectionUtils;

import static am.amitm29.com.home.HomeActivity.LAST_UPDATED;
import static am.amitm29.com.home.HomeActivity.STATS_ID;

public class StatsFragment extends Fragment {

    TextView zeroTV, oneTV, twoTV, threeTV, fourTV, fiveTV, sixTV, sevenTV, eightTV, luTV, lrTV, updateMessageTV, timeSinceLR;
    ConstraintLayout zeroCL, oneCL, twoCL, threeCL, fourCL, fiveCL, sixCL, sevenCL, eightCL;
    View loadingView;
    ProgressBar loadingBar;
    private long s0, s1, s2, s3, s4, s5, s6, s7, s8, millis, startMillis;

    private String LOG_TAG = "StatsFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stats_layout, container, false);

    }
    Stats previousStats;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("Stats");

        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);


        zeroTV = view.findViewById(R.id.zero_tv);
        oneTV = view.findViewById(R.id.one_tv);
        twoTV = view.findViewById(R.id.two_tv);
        threeTV = view.findViewById(R.id.three_tv);
        fourTV = view.findViewById(R.id.four_tv);
        fiveTV = view.findViewById(R.id.five_tv);
        sixTV = view.findViewById(R.id.six_tv);
        sevenTV = view.findViewById(R.id.seven_tv);
        eightTV = view.findViewById(R.id.eight_tv);
        lrTV = view.findViewById(R.id.reboot_tv);
        luTV = view.findViewById(R.id.last_updated_tv);
        updateMessageTV = view.findViewById(R.id.update_message_tv);
        timeSinceLR = view.findViewById(R.id.time_since_lr);


        zeroCL = view.findViewById(R.id.zero_cl);
        oneCL = view.findViewById(R.id.one_cl);
        twoCL = view.findViewById(R.id.two_cl);
        threeCL = view.findViewById(R.id.three_cl);
        fourCL = view.findViewById(R.id.four_cl);
        fiveCL = view.findViewById(R.id.five_cl);
        sixCL = view.findViewById(R.id.six_cl);
        sevenCL = view.findViewById(R.id.seven_cl);
        eightCL = view.findViewById(R.id.eight_cl);


        loadingView = view.findViewById(R.id.loading_view);
        loadingBar = view.findViewById(R.id.loading_bar);


        if(BTConnectionUtils.isBtConnected)
        {
            loadingView.setVisibility(View.VISIBLE);
            loadingBar.setVisibility(View.VISIBLE);
            updateMessageTV.setVisibility(View.GONE);
            luTV.setVisibility(View.VISIBLE);
        }
        else
        {
            loadingView.setVisibility(View.GONE);
            loadingBar.setVisibility(View.GONE);
            updateMessageTV.setVisibility(View.GONE);
            luTV.setVisibility(View.VISIBLE);
        }



        try {
            final LiveData<List<Stats>> statsLiveData = DbHelper.getDbInstance().statsDao().loadAllStats();

            statsLiveData.observe(this, new Observer<List<Stats>>() {
                @Override
                public void onChanged(@Nullable List<Stats> allStats) {

                    for(Stats stats : allStats) {
                        if(stats.getId() == HomeActivity.getInstance().getSharedPreferences(
                                "home.app", Context.MODE_PRIVATE).getInt(STATS_ID, 0)) {


                            zeroTV.setText(millisToHMS(stats.getS0()));
                            if(millisToHMS(stats.getS0()).equals("00:00:00"))
                                zeroCL.setVisibility(View.GONE);
                            else
                                zeroCL.setVisibility(View.VISIBLE);


                            oneTV.setText(millisToHMS(stats.getS1()));
                            if(millisToHMS(stats.getS1()).equals("00:00:00"))
                                oneCL.setVisibility(View.GONE);
                            else
                                oneCL.setVisibility(View.VISIBLE);


                            twoTV.setText(millisToHMS(stats.getS2()));
                            if(millisToHMS(stats.getS2()).equals("00:00:00"))
                                twoCL.setVisibility(View.GONE);
                            else
                                twoCL.setVisibility(View.VISIBLE);


                            threeTV.setText(millisToHMS(stats.getS3()));
                            if(millisToHMS(stats.getS3()).equals("00:00:00"))
                                threeCL.setVisibility(View.GONE);
                            else
                                threeCL.setVisibility(View.VISIBLE);


                            fourTV.setText(millisToHMS(stats.getS4()));
                            if(millisToHMS(stats.getS4()).equals("00:00:00"))
                                fourCL.setVisibility(View.GONE);
                            else
                                fourCL.setVisibility(View.VISIBLE);


                            fiveTV.setText(millisToHMS(stats.getS5()));
                            if(millisToHMS(stats.getS5()).equals("00:00:00"))
                                fiveCL.setVisibility(View.GONE);
                            else
                                fiveCL.setVisibility(View.VISIBLE);


                            sixTV.setText(millisToHMS(stats.getS6()));
                            if(millisToHMS(stats.getS6()).equals("00:00:00"))
                                sixCL.setVisibility(View.GONE);
                            else
                                sixCL.setVisibility(View.VISIBLE);


                            sevenTV.setText(millisToHMS(stats.getS7()));
                            if(millisToHMS(stats.getS7()).equals("00:00:00"))
                                sevenCL.setVisibility(View.GONE);
                            else
                                sevenCL.setVisibility(View.VISIBLE);


                            eightTV.setText(millisToHMS(stats.getS8()));
                            if(millisToHMS(stats.getS8()).equals("00:00:00"))
                                eightCL.setVisibility(View.GONE);
                            else
                                eightCL.setVisibility(View.VISIBLE);


                            timeSinceLR.setText(millisToHMS(stats.getMillis()));


                            Calendar cal = Calendar.getInstance();
                            TimeZone tz = cal.getTimeZone();

                            /* debug: is it local time? */
                            Log.d("Time zone: ", tz.getDisplayName());

                            /* date formatter in local timezone */
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, h:mm a");
                            sdf.setTimeZone(tz);

                            /* print your timestamp and double check it's the date you expect */
                            long startTimestamp = stats.getStartTimeInMillis();
                            String startLocalTime = sdf.format(new Date(startTimestamp));
                            Log.d("Time: ", startLocalTime);

                            lrTV.setText(startLocalTime);

                            long lastUpdatedTimestamp = HomeActivity.getInstance().getSharedPreferences(
                                    "home.app", Context.MODE_PRIVATE).getLong(LAST_UPDATED, 0);

                            String lastUpdatedLocalTime = sdf.format(new Date(lastUpdatedTimestamp));
                            Log.d("Time: ", lastUpdatedLocalTime);

                            luTV.setText("Last Updated : " + lastUpdatedLocalTime);

                        }
                    }
                }
            });

        }catch (Exception e){Log.d(LOG_TAG, "Exception occurred while loading stats by id");}

    }

    public void updateStats(String messageSub){

     switch (messageSub.charAt(0)){
         case '0':
             try{
                 s0 = Integer.parseInt(messageSub.substring(2));
             }
             catch (Exception e){
                 s0 = 0;
                 Log.d(LOG_TAG, "Exception occurred in 0");
             }
             break;

         case '1':
             try{
                 s1 = Integer.parseInt(messageSub.substring(2));
             }
             catch (Exception e){
                 s1 = 0;
                 Log.d(LOG_TAG, "Exception occurred in 1");
             }
             break;

         case '2':
             try{
                 s2 = Integer.parseInt(messageSub.substring(2));
             }
             catch (Exception e){
                 s2 = 0;
                 Log.d(LOG_TAG, "Exception occurred in 2");
             }
             break;

         case '3':
             try{
                 s3 = Integer.parseInt(messageSub.substring(2));
             }
             catch (Exception e){
                 s3 = 0;
                 Log.d(LOG_TAG, "Exception occurred in 3");
             }
             break;

         case '4':
             try{
                 s4 = Integer.parseInt(messageSub.substring(2));
             }
             catch (Exception e){
                 s4 = 0;
                 Log.d(LOG_TAG, "Exception occurred in 4");
             }
             break;

         case '5':
             try{
                 s5 = Integer.parseInt(messageSub.substring(2));
             }
             catch (Exception e){
                 s5 = 0;
                 Log.d(LOG_TAG, "Exception occurred in 5");
             }
             break;

         case '6':
             try{
                 s6 = Integer.parseInt(messageSub.substring(2));
             }
             catch (Exception e){
                 s6 = 0;
                 Log.d(LOG_TAG, "Exception occurred in 6");
             }
             break;

         case '7':
             try{
                 s7 = Integer.parseInt(messageSub.substring(2));
             }
             catch (Exception e){
                 s7 = 0;
                 Log.d(LOG_TAG, "Exception occurred in 7");
             }
             break;

         case '8':
             try{
                 s8 = Integer.parseInt(messageSub.substring(2));
             }
             catch (Exception e){
                 s8 = 0;
                 Log.d(LOG_TAG, "Exception occurred in 8");
             }
             break;

         case '9':
             try{
                 millis = Integer.parseInt(messageSub.substring(2));
                 startMillis = System.currentTimeMillis() - millis;

                 final int id = HomeActivity.getInstance().getSharedPreferences(
                         "home.app", Context.MODE_PRIVATE).getInt(STATS_ID, 0);
                 Log.d(LOG_TAG, "ID : " + id);

                 if(id==0){
                     final Stats stats = new Stats(id + 1, millis, startMillis, s0, s1, s2, s3, s4, s5, s6, s7, s8);
                     new Thread() {
                         public void run() {
                             DbHelper.getDbInstance().statsDao().insertStats(stats);
                         }
                     }.start();
                     Log.d(LOG_TAG, "New entry saved");
                     HomeActivity.getInstance().getSharedPreferences(
                             "home.app", Context.MODE_PRIVATE).edit().putInt(STATS_ID, id + 1).apply();
                     Log.d(LOG_TAG, "id value updated to " + id);
                     HomeActivity.getInstance().getSharedPreferences(
                             "home.app.", Context.MODE_PRIVATE).edit().putLong(LAST_UPDATED, System.currentTimeMillis()).apply();
                     Log.d(LOG_TAG, "last updated time changed to " + System.currentTimeMillis());
                 }

                 else {
                     final LiveData<Stats> statsLiveData = DbHelper.getDbInstance().statsDao().loadStatById(id);
                     statsLiveData.observe(this, new Observer<Stats>() {
                         @Override
                         public void onChanged(@Nullable Stats previousStored) {
                             statsLiveData.removeObserver(this);
                             previousStats = previousStored;

                             Log.d(LOG_TAG, "Getting previous stored data");


                             Log.d(LOG_TAG, "new start " + startMillis);
                             Log.d(LOG_TAG, "old start " + previousStats.getStartTimeInMillis());
                             if (startMillis - previousStats.getStartTimeInMillis() > 60000) {

                                 final Stats stats = new Stats(id + 1, millis, startMillis, s0, s1, s2, s3, s4, s5, s6, s7, s8);
                                 new Thread() {
                                     public void run() {
                                         DbHelper.getDbInstance().statsDao().insertStats(stats);
                                     }
                                 }.start();
                                 Log.d(LOG_TAG, "New entry saved");
                                 HomeActivity.getInstance().getSharedPreferences(
                                         "home.app", Context.MODE_PRIVATE).edit().putInt(STATS_ID, id+1).apply();
                                 Log.d(LOG_TAG, "id value updated to " + (id+1));

                                 long updatedTime = System.currentTimeMillis();

                                 HomeActivity.getInstance().getSharedPreferences(
                                         "home.app", Context.MODE_PRIVATE).edit().putLong(LAST_UPDATED, updatedTime).apply();
                                 Log.d(LOG_TAG, "last updated time changed to " + updatedTime);

                             } else {
                                 final Stats stats = new Stats(id, millis, startMillis, s0, s1, s2, s3, s4, s5, s6, s7, s8);
                                 new Thread() {
                                     public void run() {
                                         DbHelper.getDbInstance().statsDao().updateStats(stats);
                                     }
                                 }.start();
                                 Log.d(LOG_TAG, "Entry updated");

                                 long updatedTime = System.currentTimeMillis();

                                 HomeActivity.getInstance().getSharedPreferences(
                                         "home.app", Context.MODE_PRIVATE).edit().putLong(LAST_UPDATED, updatedTime).apply();
                                 Log.d(LOG_TAG, "last updated time changed to " + updatedTime);
                             }
                         }
                     });

                 }
                 loadingView.setVisibility(View.GONE);
                 loadingBar.setVisibility(View.GONE);
                 luTV.setVisibility(View.GONE);
                 updateMessageTV.setVisibility(View.VISIBLE);
             }
             catch (Exception e){
                 Log.d(LOG_TAG, "Exception occurred in 9");
                 return;
             }
             break;

             default:
                 Log.d(LOG_TAG, "default case in updateStats running");

     }

    }

    private String millisToHMS(long timeInMillis)
    {
        long hh = TimeUnit.MILLISECONDS.toHours(timeInMillis);
        long mm = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % TimeUnit.HOURS.toMinutes(1);
        long ss = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % TimeUnit.MINUTES.toSeconds(1);

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hh, mm, ss);
    }

}
