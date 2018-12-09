package com.briandemaio.sheettimer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class ItemChoiceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_choice_fragment, container, false);
        GridView gridView = view.findViewById(R.id.item_grid_view);
        String[] itemTypes = getResources().getStringArray(R.array.item_types_array);

        Item[] items ={
                new Item(itemTypes[0], R.drawable.),
                new Item(itemTypes[1], R.drawable.doggy_2_teeth),
                new Item(itemTypes[2], R.drawable.doggy_3_boxer),
                new Item(itemTypes[3], R.drawable.doggy_4_lab),
                new Item(itemTypes[4], R.drawable.doggy_5_grey),
                new Item(itemTypes[5], R.drawable.doggy_6_mutt),
                new Item(itemTypes[6], R.drawable.doggy_7_lab),
                new Item(itemTypes[7], R.drawable.doggy_8_tan)
        };

        ItemChoiceAdapter itemChoiceAdapter = new ItemChoiceAdapter(getActivity(), items);
        gridView.setAdapter(itemChoiceAdapter);
        return view;
    }
}
