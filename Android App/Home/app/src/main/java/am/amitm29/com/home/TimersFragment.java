package am.amitm29.com.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import am.amitm29.com.home.Adapters.TimerAdapter;
import am.amitm29.com.home.Database.DbHelper;
import am.amitm29.com.home.Database.Timer;
import am.amitm29.com.home.Utils.BTConnectionUtils;

public class TimersFragment extends Fragment implements TimerAdapter.TimerListItemClickListener{
    public TimerAdapter mAdapter;
    RecyclerView timerRV;
    LinearLayout noTimersLL;
    ConstraintLayout CL;
    private static TimersFragment mInstance;
    protected boolean mIsVisibleToUser;
    public String LOG_TAG = "TimerFragment";

    @Override
    public void onStart() {
        super.onStart();

        if (mIsVisibleToUser) {
            onVisible();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIsVisibleToUser) {
            onInVisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        if (isResumed()) {
            if (mIsVisibleToUser) {
                onVisible();
            } else {
                onInVisible();
            }
        }
    }

    public void onVisible() {
        HomeFragment.isTimerFragmentVisible = true;
    }

    public void onInVisible() {
        HomeFragment.isTimerFragmentVisible = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timers, container, false);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInstance = this;

        timerRV = view.findViewById(R.id.timer_rv);
        noTimersLL = view.findViewById(R.id.no_timers_to_show_view);
        CL = view.findViewById(R.id.CL_timers);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        timerRV.setLayoutManager(layoutManager);
        timerRV.setItemAnimator(new DefaultItemAnimator());

        timerRV.setHasFixedSize(true);

        mAdapter = new TimerAdapter(getContext(), null, this);
        timerRV.setAdapter(mAdapter);

        CL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noTimersLL.getVisibility() == View.VISIBLE)
                {
                    Intent i = new Intent(getActivity(), AddTimerActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    @Override
    public void onListItemClicked(int clickedItemIndex) {

    }


    @Override
    public void onResume() {
        super.onResume();
        retrieveAllTimers();
    }

    private void retrieveAllTimers(){
        Log.d(LOG_TAG, "Retrieving all timers");
        final LiveData<List<Timer>> timers = DbHelper.getDbInstance().timerDao().loadAllTimers();
        timers.observe(getParentFragment(), new Observer<List<Timer>>() {
            @Override
            public void onChanged(@Nullable List<Timer> timers) {
                Log.d(LOG_TAG, "Recieving database update for timers from LiveData");

                mAdapter.swapList(timers);
                if(timers==null || timers.size()==0){
                    noTimersLL.setVisibility(View.VISIBLE);
                    timerRV.setVisibility(View.GONE);
                }
                else{
                    noTimersLL.setVisibility(View.GONE);
                    timerRV.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /*
        *deletes the timer if it is interrupted by the user
        * i.e. if the user switches on/off the switch on which
        * the timer was running then that timer is deleted
     */
    public void deleteTimerIfInterrupted(String message){
        final char r_id = message.charAt(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final LiveData<Timer> timers = DbHelper.getDbInstance().timerDao().loadTimerById(r_id);
                    timers.observe(getParentFragment(), new Observer<Timer>() {
                        @Override
                        public void onChanged(@Nullable final Timer timer) {
                            timers.removeObserver(this);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                        try {

                                            String delStr = "TR" + timer.getR_ID();
                                            BTConnectionUtils.writeBT(delStr);
                                            Log.d(LOG_TAG, delStr);

                                            DbHelper.getDbInstance().timerDao().deleteTimer(timer);
                                            Log.d(LOG_TAG, "Timer deleted corresponding to " + r_id);
                                        } catch (Exception e) {
                                            Log.d(LOG_TAG, "No timer found corresponding to " + r_id);
                                        }
                                    }

                            }).start();
                        }
                    });
                }catch (Exception e){}
            }
        }).start();
    }


    public static TimersFragment getInstance(){
        if (mInstance==null)
            return new TimersFragment();
        else
            return mInstance;
    }



}
