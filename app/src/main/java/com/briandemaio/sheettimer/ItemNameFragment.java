package com.briandemaio.sheettimer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class ItemNameFragment extends Fragment {

    private EditText mEditNameView;
    private ImageView mItemImageView;
    private int mImageId;
    private OnItemNameFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Bundle bundle = getArguments();

        View view = inflater.inflate(R.layout.item_name_fragment, container, false);
        mItemImageView = view.findViewById(R.id.itemImageView);
        mEditNameView = view.findViewById(R.id.editText);

        mImageId = getResources().getIdentifier("household_item_1", "drawable", getContext().getPackageName());

        if(bundle != null){
            mImageId = bundle.getInt("imageId");
        }

        setImage(mImageId);

        final Button button = view.findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String item = mEditNameView.getText().toString();
                if (TextUtils.isEmpty(mEditNameView.getText())) {
                    Toast.makeText(getActivity(), "You forgot to add a name!", Toast.LENGTH_LONG ).show();
                }
                else {
                    mListener.onSetSave(item, mImageId);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemNameFragmentInteractionListener) {
            mListener = (OnItemNameFragmentInteractionListener) context;
        }
    }

    public void setImage(int imageId){
        mImageId = imageId;
        Glide.with(getContext()).load(imageId).into(mItemImageView);
    }

    interface OnItemNameFragmentInteractionListener {
        void onSetSave(String item, int imageId);
    }
}
