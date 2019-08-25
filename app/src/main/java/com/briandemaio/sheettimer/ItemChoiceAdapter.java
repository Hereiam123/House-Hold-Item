package com.briandemaio.sheettimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ItemChoiceAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Item> items;
    private OnItemSelectedListener listener;

    public ItemChoiceAdapter(Context context, ArrayList<Item> items){
        this.mContext = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Item item = items.get(position);

        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(R.layout.choice_view_item, null);

        final ImageView imageView = (ImageView)convertView.findViewById(R.id.item_image);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.item_name_text);

        if(item.getImageResource() == 0) {
            Glide.with(mContext).load(item.getStringImageResource()).into(imageView);
        }
        else{
            Glide.with(mContext).load(item.getImageResource()).into(imageView);
        }

        nameTextView.setText(item.getName());

        if (mContext instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) mContext;
        } else {
            throw new ClassCastException(mContext.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }

        imageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemSelected(item.getImageResource(), item.getId());
            }
        });

        return convertView;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int imageId, int itemId);
    }
}
