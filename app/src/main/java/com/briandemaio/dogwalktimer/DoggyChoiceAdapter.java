package com.briandemaio.dogwalktimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DoggyChoiceAdapter extends BaseAdapter {

    private final Context mContext;
    private final Doggy[] doggies;
    private OnItemSelectedListener listener;

    public DoggyChoiceAdapter(Context context, Doggy[] doggies){
        this.mContext = context;
        this.doggies = doggies;
    }

    @Override
    public int getCount() {
        return doggies.length;
    }

    @Override
    public Object getItem(int position) {
        return doggies[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Doggy doggy = doggies[position];

        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(R.layout.choice_view_item, null);

        final ImageView imageView = (ImageView)convertView.findViewById(R.id.doggy_image);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.doggy_name_text);

        Glide.with(mContext).load(doggy.getImageResource()).into(imageView);
        nameTextView.setText(doggy.getName());

        if (mContext instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) mContext;
        } else {
            throw new ClassCastException(mContext.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }

        imageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDoggyItemSelected(doggy.getImageResource());
            }
        });

        return convertView;
    }

    public interface OnItemSelectedListener {
        void onDoggyItemSelected(int imageId);
    }
}
