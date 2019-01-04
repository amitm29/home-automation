package am.amitm29.com.home.Adapters;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import am.amitm29.com.home.AddTimerActivity;
import am.amitm29.com.home.Database.DbHelper;
import am.amitm29.com.home.Database.Timer;
import am.amitm29.com.home.HomeActivity;
import am.amitm29.com.home.R;
import am.amitm29.com.home.TimersFragment;
import am.amitm29.com.home.Utils.BTConnectionUtils;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder>{
    private Context mContext;
    private List<Timer> mList;
    final private TimerListItemClickListener mOnClickListener;
    private int lastPosition = -1;
    public static String EXTRA_RID = "timer_rid";
    public static String LOG_TAG = "TimerAdapter";

    public TimerAdapter(Context mContext, List<Timer> mList, TimerListItemClickListener listener) {
        this.mContext = mContext;
        this.mList = mList;
        mOnClickListener = listener;
    }

    public interface TimerListItemClickListener{
        void onListItemClicked(int clickedItemIndex);
    }

    public class TimerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CountDownTimer timer;
        TextView titleTV, actionTV, timeRemainingTV;
        ImageView deleteIV, editIV;


        public TimerViewHolder(View itemView) {

            super(itemView);
            titleTV = itemView.findViewById(R.id.titleTV);
            actionTV = itemView.findViewById(R.id.action_TV);
            timeRemainingTV = itemView.findViewById(R.id.time_remaining_TV);
            editIV = itemView.findViewById(R.id.editIV);
            deleteIV = itemView.findViewById(R.id.deleteIV);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClicked(clickedPosition);
        }

    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_timer, parent, false);
        return new TimerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TimerViewHolder holder, final int position) {
        if(mList.get(position)==null)
            return;

        if(mList.get(position).getFinalTimeinMillis() < System.currentTimeMillis())
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DbHelper.getDbInstance().timerDao().deleteTimer(mList.get(position));
                }
            }).start();

        }
        String title = mList.get(position).getTitl();
        final char rId = mList.get(position).getR_ID();
        int ac = mList.get(position).getAction();
        if(ac==1)
            holder.actionTV.setText(": ON");
        else
            holder.actionTV.setText(": OFF");

        if(title.equals("")) {
            holder.titleTV.setText(rId+"");
        }
        else {
            holder.titleTV.setText(title);
        }

        if(holder.timer!= null)
            holder.timer.cancel();

        holder.timer = new CountDownTimer(
                mList.get(position).getFinalTimeinMillis()-System.currentTimeMillis()
                , 1000) {
            @Override
            public void onTick(long l) {

                int minutes, seconds;
                minutes = (int) (l / 60000);
                seconds = (int) ((l / 1000) % 60);
                holder.timeRemainingTV.setText(" " + minutes + " : " + seconds);
            }

            @Override
            public void onFinish() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            final LiveData<Timer> timers = DbHelper.getDbInstance().timerDao().loadTimerById(rId);
                            timers.observe(((HomeActivity) TimersFragment.getInstance().getActivity()), new Observer<Timer>() {
                                @Override
                                public void onChanged(@Nullable final Timer timer) {
                                    timers.removeObserver(this);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            synchronized (this) {
                                                try {
                                                    DbHelper.getDbInstance().timerDao().deleteTimer(timer);
                                                    Log.d(LOG_TAG, "Timer deleted corresponding to " + rId);
                                                } catch (Exception e) {
                                                    Log.d(LOG_TAG, "No timer found corresponding to " + rId);
                                                }
                                            }
                                        }
                                    }).start();
                                }
                            });
                        }catch (Exception e){
                            Log.d(LOG_TAG, "Exception occurred while deleteling timer on completion");
                        }
                    }
                }).start();
            }
        }.start();

        holder.editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                char rID = mList.get(position).getR_ID();

                Log.d("ID", rID+"" +  mList.get(position).getTitl());

                Intent i = new Intent(mContext, AddTimerActivity.class);
                i.putExtra(EXTRA_RID, rID);
                mContext.startActivity(i);
            }
        });

        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String delStr = "TR" + mList.get(position).getR_ID();
                holder.timer = null;
                Log.d("TimeAdapter", delStr);
                BTConnectionUtils.writeBT(delStr);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DbHelper.getDbInstance().timerDao().deleteTimer(mList.get(position));
                    }
                }).start();
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

    public List<Timer> getList(){
        return mList;
    }

    public void swapList(List<Timer> newList){
        if(mList!=null){
            mList=null;
        }

        mList = newList;

        if(mList!=null){
            notifyDataSetChanged();
        }
    }

}
