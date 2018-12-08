package com.briandemaio.dogwalktimer;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;

public class AddedDoggyAdapter extends RecyclerView.Adapter<AddedDoggyAdapter.DoggyViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<Doggy> mDoggies;
    private static ClickListener clickListener;


    AddedDoggyAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public DoggyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.added_view_item, parent, false);
        return new DoggyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DoggyViewHolder holder, int position) {
        if (mDoggies != null) {
            final Doggy current = mDoggies.get(position);
            holder.doggyItemView.setText(current.getName());
            if(holder.doggyTimer != null){
                holder.doggyTimer.cancel();
            }

            holder.doggyTimer = new CountDownTimer(current.getExpiryTime()-System.currentTimeMillis(), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int hours = (int) (millisUntilFinished / (1000*60*60)%24);
                    int minutes = (int) (millisUntilFinished / (1000*60)%60);
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    if(hours > 0){
                        holder.doggyTimerView.setText(" "+ hours+" h:" + minutes + " m:"+seconds+" s");
                    }
                    else{
                        holder.doggyTimerView.setText(" " + minutes + " m:" + seconds + " s");
                    }
                }

                @Override
                public void onFinish() {
                    holder.doggyTimerView.setText("All Done");
                }
            }.start();

            Glide.with(mContext).load(current.getImageResource()).into(holder.doggyImageView);
        } else {
            // Covers the case of data not being ready yet.
            holder.doggyItemView.setText("No Doggy");
        }
    }

    Doggy getDoggyAtPosition (int position) {
        return mDoggies.get(position);
    }

    void setDoggies(List<Doggy> doggies){
        mDoggies = doggies;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mDoggies != null)
            return mDoggies.size();
        else return 0;
    }

    class DoggyViewHolder extends RecyclerView.ViewHolder{
        private final TextView doggyItemView;
        private final ImageView doggyImageView;
        private final ImageButton doggyResetView;
        private final TextView doggyTimerView;
        private CountDownTimer doggyTimer;

        private DoggyViewHolder(View itemView) {
            super(itemView);
            doggyItemView = itemView.findViewById(R.id.recycler_textview_doggy_name);
            doggyImageView = itemView.findViewById(R.id.recycler_imageview_doggy_art);
            doggyTimerView = itemView.findViewById(R.id.recycler_textview_doggy_timeleft);
            doggyResetView = itemView.findViewById(R.id.recycler_reset_timer);
            doggyResetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onWalkItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    void setOnItemClickListener(ClickListener clickListener) {
        AddedDoggyAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onWalkItemClick(View v, int position);
    }
}
