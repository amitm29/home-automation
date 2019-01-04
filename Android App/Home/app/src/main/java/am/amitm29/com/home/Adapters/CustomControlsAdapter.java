package am.amitm29.com.home.Adapters;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import am.amitm29.com.home.AddControlActivity;
import am.amitm29.com.home.Database.CustomControls;
import am.amitm29.com.home.FirstFragment;
import am.amitm29.com.home.HomeActivity;
import am.amitm29.com.home.R;
import am.amitm29.com.home.Utils.BTConnectionUtils;

public class CustomControlsAdapter extends RecyclerView.Adapter<CustomControlsAdapter.CustomControlsViewHolder>{
    private Context mContext;
    private List<CustomControls> mList;
    final private ControlListItemClickListener mOnClickListener;
    private int lastPosition = -1;
    public static String EXTRA_ID = "control_id";

    public CustomControlsAdapter(Context mContext, List<CustomControls> mList, ControlListItemClickListener listener) {
        this.mContext = mContext;
        this.mList = mList;
        mOnClickListener = listener;
    }

    public interface ControlListItemClickListener{
        void onListItemClicked(int clickedItemIndex);
    }

    public class CustomControlsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titleTV, noteTV;
        Switch state;

        public CustomControlsViewHolder(View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.titleTV);
            noteTV = itemView.findViewById(R.id.noteTV);
            state = itemView.findViewById(R.id.switch1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("ID", "item clicked");

            int clickedPosition = getAdapterPosition();
            long id = mList.get(clickedPosition).get_ID();

            Log.d("ID", id+"" +  mList.get(clickedPosition).getTitl());

            Intent i = new Intent(mContext, AddControlActivity.class);
            i.putExtra(EXTRA_ID, id);
            mContext.startActivity(i);

            mOnClickListener.onListItemClicked(clickedPosition);
        }

    }

    @NonNull
    @Override
    public CustomControlsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_control, parent, false);
        return new CustomControlsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CustomControlsViewHolder holder, final int position) {
        if(mList.get(position)==null)
            return;
        holder.titleTV.setText(mList.get(position).getTitl());
        String note = mList.get(position).getNote();
        if(!note.equals("")) {
            holder.noteTV.setText(note);
            holder.titleTV.setPadding(0, 0, 0, 0);
        }
        else {
            holder.noteTV.setVisibility(View.GONE);
            holder.titleTV.setPadding(0, 16, 0, 16);
        }
        if(mList.get(position).getState()) {
            holder.state.setChecked(true);
            Log.d("STATE", "true");
        }
        else {
            holder.state.setChecked(false);
            Log.d("STATE", "false");
        }
        holder.state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = BTConnectionUtils.writeBT(mList.get(position).getOutput());
                if(!b)
                {
                    Toast.makeText(mContext, "An error occurred, please connect again!", Toast.LENGTH_LONG).show();
                    HomeActivity.getInstance().disconnectBT();
                }
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

    public List<CustomControls> getList(){
        return mList;
    }

    public void swapList(List<CustomControls> newList){
        if(mList!=null){
            mList=null;
        }

        mList = newList;

        if(mList!=null){
            notifyDataSetChanged();
        }
    }

}
