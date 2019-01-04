package am.amitm29.com.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import am.amitm29.com.home.Utils.BTConnectionUtils;

public class KeypadFragment extends Fragment {
    boolean success;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_keypad, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("KeypadFragment", "onViewCreated");


        view.findViewById(R.id.key_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               success =  BTConnectionUtils.writeBT("0");
               if(!success)
               {
                   Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                   HomeActivity.getInstance().disconnectBT();
               }
            }
        });

        view.findViewById(R.id.key_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("1");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("2");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("3");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("4");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("5");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("6");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("7");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("8");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("9");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_A).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("A");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_B).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("B");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_C).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("C");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_D).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("D");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("*");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

        view.findViewById(R.id.key_hash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success =  BTConnectionUtils.writeBT("#");
                if(!success)
                {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    HomeActivity.getInstance().disconnectBT();
                }
            }
        });

    }

}
