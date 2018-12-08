package com.briandemaio.dogwalktimer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class DoggyChoiceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.doggy_choice_fragment, container, false);
        GridView gridView = view.findViewById(R.id.doggy_grid_view);
        String[] doggyTypes = getResources().getStringArray(R.array.doggy_types_array);

        Doggy[] doggies ={
                new Doggy(doggyTypes[0], R.drawable.doggy_1_beagle),
                new Doggy(doggyTypes[1], R.drawable.doggy_2_teeth),
                new Doggy(doggyTypes[2], R.drawable.doggy_3_boxer),
                new Doggy(doggyTypes[3], R.drawable.doggy_4_lab),
                new Doggy(doggyTypes[4], R.drawable.doggy_5_grey),
                new Doggy(doggyTypes[5], R.drawable.doggy_6_mutt),
                new Doggy(doggyTypes[6], R.drawable.doggy_7_lab),
                new Doggy(doggyTypes[7], R.drawable.doggy_8_tan)
        };

        DoggyChoiceAdapter doggyChoiceAdapter = new DoggyChoiceAdapter(getActivity(), doggies);
        gridView.setAdapter(doggyChoiceAdapter);
        return view;
    }
}
