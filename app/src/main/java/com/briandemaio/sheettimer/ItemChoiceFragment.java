package com.briandemaio.sheettimer;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class ItemChoiceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_choice_fragment, container, false);
        GridView gridView = view.findViewById(R.id.item_grid_view);
        String[] itemTypes = getResources().getStringArray(R.array.item_types_array);

        ArrayList<Item> items = new ArrayList<>();
        Item camera = new Item("Create Task", R.drawable.camera);
        items.add(camera);

        List<Task> tasks = ChoiceActivity.getTasks();

        if(tasks != null) {
            for(Task task : tasks){
                String taskName = task.getTask();
                String taskImage = task.getImageResource();
                Item newTask = new Item(taskName, taskImage);
                items.add(newTask);
            }
        }

        for(int i=0; i<=61; i++) {
            int itemNum = i+1;
            items.add(new Item(itemTypes[i],getResources().getIdentifier("household_item_"+itemNum,"drawable", getContext().getPackageName())));
        }

        ItemChoiceAdapter itemChoiceAdapter = new ItemChoiceAdapter(getActivity(), items);
        gridView.setAdapter(itemChoiceAdapter);
        return view;
    }
}
