package com.briandemaio.sheettimer;

import android.content.Context;
import android.os.CountDownTimer;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

public class AddedItemAdapter extends RecyclerView.Adapter<AddedItemAdapter.ItemViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<Item> mItems;
    private static ClickListener clickListener;


    AddedItemAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.added_view_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        if (mItems != null) {
            final Item current = mItems.get(position);
            holder.ItemView.setText(current.getName());
            if(holder.itemTimer != null){
                holder.itemTimer.cancel();
            }

            holder.itemTimer = new CountDownTimer(current.getExpiryTime()-System.currentTimeMillis(), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    int days = (int) (millisUntilFinished / (1000*60*60*24));
                    int hours = (int) (millisUntilFinished / (1000*60*60)%24);
                    int minutes = (int) (millisUntilFinished / (1000*60)%60);
                    int seconds = (int) (millisUntilFinished / 1000) % 60;

                    if(days>0) {
                        holder.itemTimerView.setText(" " + days + "d: "+ hours + "h: "+minutes+"m: "+seconds+"s");
                    }
                    else if(hours > 0){
                        holder.itemTimerView.setText(" "+ hours+" h:" + minutes + " m:"+seconds+" s");
                    }
                    else{
                        holder.itemTimerView.setText(" " + minutes + " m:" + seconds + " s");
                    }
                }

                @Override
                public void onFinish() {
                    holder.itemTimerView.setText("Take me for a Walk!");
                }
            }.start();

            Glide.with(mContext).load(current.getImageResource()).into(holder.itemImageView);
        } else {
            // Covers the case of data not being ready yet.
            holder.ItemView.setText("No Item");
        }
    }

    Item getItemAtPosition(int position) {
        return mItems.get(position);
    }

    void setItems(List<Item> items){
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mItems != null)
            return mItems.size();
        else return 0;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        private final TextView ItemView;
        private final ImageView itemImageView;
        private final ImageButton itemResetView;
        private final TextView itemTimerView;
        private final Button itemEditTimerView;
        private CountDownTimer itemTimer;

        private ItemViewHolder(View itemView) {
            super(itemView);
            ItemView = itemView.findViewById(R.id.recycler_textview_item_name);
            itemImageView = itemView.findViewById(R.id.recycler_imageview_item_art);
            itemTimerView = itemView.findViewById(R.id.recycler_textview_item_timeleft);
            itemResetView = itemView.findViewById(R.id.recycler_reset_timer);
            itemEditTimerView = itemView.findViewById(R.id.recycler_edit_timer);

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                itemEditTimerView.findViewById(R.id.recycler_edit_timer).setVisibility(View.GONE);
            }

            itemResetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onWalkItemClick(view, getAdapterPosition());
                }
            });

            itemEditTimerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onEditTimeClick(view, getAdapterPosition());
                }
            });
        }
    }

    void setOnItemClickListener(ClickListener clickListener) {
        AddedItemAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onWalkItemClick(View v, int position);
        void onEditTimeClick(View v, int position);
    }
}
