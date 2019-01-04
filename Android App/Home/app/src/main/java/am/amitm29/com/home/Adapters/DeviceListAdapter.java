package am.amitm29.com.home.Adapters;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import am.amitm29.com.home.Database.Device;
import am.amitm29.com.home.DeviceListActivity;
import am.amitm29.com.home.R;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {
    private final static String LOG_TAG = "DeviceListAdapter";


    private Context mContext;
    private ArrayList<Device> mList;
    final private ListItemClickListener mOnClickListener;
    private int lastPosition = -1;

    public DeviceListAdapter(Context context, ArrayList<Device> list, ListItemClickListener listener){
        mContext = context;
        mList = list;
        mOnClickListener = listener;
    }

    public interface ListItemClickListener{
        void onListItemClicked(int clickedItemIndex);
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView deviceNameTV;
        ImageView deviceIV;
        TextView connectTV;

        private DeviceViewHolder(View itemView){
            super(itemView);
            deviceNameTV = itemView.findViewById(R.id.deviceName);
            deviceIV = itemView.findViewById(R.id.device_iv);
            connectTV = itemView.findViewById(R.id.connect_TV);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            String address = mList.get(clickedPosition).getDeviceAddress();

            Log.d("DeviceAddress", address);

            mOnClickListener.onListItemClicked(clickedPosition);
        }
    }


    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_device, parent, false);
        return new DeviceViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, final int position) {
        if(mList.get(position)==null)
            return;
        holder.deviceNameTV.setText(mList.get(position).getDeviceName());
        int deviceClass = mList.get(position).getDeviceMajorClass();

        switch (deviceClass){
            case BluetoothClass.Device.Major.PHONE: holder.deviceIV.setImageDrawable(
                    ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_smartphone_24dp, null));
                    break;

            case BluetoothClass.Device.Major.AUDIO_VIDEO: holder.deviceIV.setImageDrawable(
                    ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_headset_24dp, null));
                    break;
        }

        holder.connectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String address = mList.get(position).getDeviceAddress();
                Log.d(LOG_TAG, address);
                DeviceListActivity.address = address;
                DeviceListActivity.getmInstance().connectBTAndInitialize();
            }
        });

        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        if(mList!=null)
            return mList.size();
        else
            return 0;
    }

    public void swapList(ArrayList<Device> newList){
        if(mList!=null){
            mList=null;
        }

        mList = newList;

        if(mList!=null){
            notifyDataSetChanged();
        }
    }


}
