package am.amitm29.com.home;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

public class FirstFragment extends Fragment {
    TextView connectDevice, viewStats;
    private String LOG_TAG = "FirstFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("Home Automation");

        ActionBar actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        connectDevice = view.findViewById(R.id.connect_device);
        viewStats = view.findViewById(R.id.view_stats);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectDevice.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ripple, null));
            viewStats.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ripple, null));
            Log.d(LOG_TAG, "true");
        }

        /*
            Clicking on connect to device button opens up
            DeviceListActivity which shows the list of paired
            bluetooth devices
         */
        connectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), DeviceListActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                            getActivity(), connectDevice, connectDevice.getTransitionName())
                            .toBundle();
                    startActivity(i, bundle);
                }
                else
                    startActivity(i);

                }
        });

        /*
            Clicking on show stats buttons opens up StatsActivity
            which shows last updated stats
         */
        viewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int finalRadius = (int)Math.hypot(view.getWidth()/2, view.getHeight()/2);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) view.getWidth() / 2, (int) view.getHeight() / 2, 0, finalRadius);
                    anim.start();
                }

                StatsFragment fragment = new StatsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment, HomeActivity.TAG_STATS).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                HomeActivity.getInstance().mNavigationView.getMenu().getItem(3).setChecked(true);
            }
        });
        }
    }

