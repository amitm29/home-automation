package am.amitm29.com.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import am.amitm29.com.home.Adapters.CustomControlsAdapter;
import am.amitm29.com.home.Database.CustomControls;
import am.amitm29.com.home.Database.DbHelper;
import am.amitm29.com.home.Utils.BTConnectionUtils;

public class DashboardFragment extends Fragment implements CustomControlsAdapter.ControlListItemClickListener {
    static ProgressBar tPB;
    static float temp;
    CustomControlsAdapter mAdapter;
    RecyclerView customControlsRV;
    LinearLayout noControlsLL;
    TextView tempTV;
    private String LOG_TAG = "ControlFragment";
    protected boolean mIsVisibleToUser;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customControlsRV = view.findViewById(R.id.custom_controls_rv);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        customControlsRV.setLayoutManager(layoutManager);
        customControlsRV.setItemAnimator(new DefaultItemAnimator());

        customControlsRV.setHasFixedSize(true);
        mAdapter = new CustomControlsAdapter(getContext(), null, this);
        customControlsRV.setAdapter(mAdapter);
        tempTV = view.findViewById(R.id.temp_TV);
        tPB = view.findViewById(R.id.t_PB);
        noControlsLL = view.findViewById(R.id.no_controls_to_show_view);
    }

    @Override
    public void onListItemClicked(int clickedItemIndex) {

    }

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
        HomeFragment.isDashboardFragmentVisible = true;
    }

    public void onInVisible() {
        HomeFragment.isDashboardFragmentVisible = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveAllControls();

        new Thread() {
            public void run() {
                while (true)
                {
                    try {
                        BTConnectionUtils.writeBT("0");
                        break;
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    private void retrieveAllControls(){
        Log.d(LOG_TAG, "Retrieving all controls");
        final LiveData<List<CustomControls>> controls = DbHelper.getDbInstance().controlsDao().loadAllControls();
        controls.observe(getActivity(), new Observer<List<CustomControls>>() {
            @Override
            public void onChanged(@Nullable List<CustomControls> customControls) {
                Log.d(LOG_TAG, "Recieving database update for controls from LiveData");
                mAdapter.swapList(customControls);
                if(customControls==null || customControls.size()==0){
                    customControlsRV.setVisibility(View.GONE);
                    noControlsLL.setVisibility(View.VISIBLE);
                }
                else {
                    noControlsLL.setVisibility(View.GONE);
                    customControlsRV.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void updateSwitch(final String str){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    List<CustomControls> customControls = mAdapter.getList();
                    for (CustomControls x : customControls) {

                        if (str.charAt(0) == x.getInput().charAt(0)) {

                            HomeActivity.dashboardState.put(str.charAt(0), str.charAt(1));

                            Log.d("STATE", "" + str.charAt(0) + str.charAt(1));
                            if (str.charAt(1) == '0') {
                                x.setState(false);
                                DbHelper.getDbInstance().controlsDao().updateControl(x);
                            } else {
                                x.setState(true);
                                DbHelper.getDbInstance().controlsDao().updateControl(x);
                            }
                            break;
                        }
                    }

                }catch (Exception e){Log.d(LOG_TAG, "exception in updateSwitch!");}
            }
        }).start();
    }

    public void updateTemp(String tempString){
        temp = Float.parseFloat(tempString);
        tempTV.setVisibility(View.VISIBLE);
        tempTV.setText(temp + "" + (char) 0x00B0 +"C");
        tPB.setVisibility(View.GONE);

    }
}
