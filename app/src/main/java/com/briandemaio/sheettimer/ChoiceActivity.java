package com.briandemaio.sheettimer;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import java.util.List;

public class ChoiceActivity extends AppCompatActivity implements
        ItemChoiceAdapter.OnItemSelectedListener, ItemNameFragment.OnItemNameFragmentInteractionListener {

    public static final String EXTRA_REPLY =
            "com.briandemaio.sheettimer.REPLY";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private int mUpdateId;
    private long mUpdateTime;
    public static TaskViewModel mTaskViewModel;
    private static List<Task> allTasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        FragmentManager manager = getSupportFragmentManager();
        Intent intent = getIntent();

        mUpdateId = intent.getIntExtra("updateId",0);
        mUpdateTime = intent.getLongExtra("updateTime", 0);

        if (getResources().getBoolean(R.bool.twoPaneMode)) {
            // all good, we use the fragments defined in the layout
            return;
        }

        if (savedInstanceState != null) {
            // cleanup any existing fragments in case we are in detailed mode
            manager.executePendingTransactions();
            Fragment fragmentById = manager.
                    findFragmentById(R.id.item_choice_placeholder);
            if (fragmentById != null) {
                manager.beginTransaction().remove(fragmentById).commit();
            }
        }

        mTaskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        mTaskViewModel.getAllTask().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> tasks) {
                allTasks = tasks;
            }
        });

        ItemChoiceFragment choiceFragment = new ItemChoiceFragment();
        manager.beginTransaction()
                .replace(R.id.item_choice_placeholder, choiceFragment).commit();
    }

    public static List<Task> getTasks(){
        return allTasks;
    }

    @Override
    public void onItemSelected(int imageId, int itemId) {
        Bundle args = new Bundle();
        if(itemId==0){
            Intent myIntent = new Intent(this, CreateTaskActivity.class);
            startActivity(myIntent);
        }
        else if (getResources().getBoolean(R.bool.twoPaneMode)) {
        ItemNameFragment fragment = (ItemNameFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nameFragment);
            fragment.setImage(imageId);
        } else {
            // replace the fragment
            // Create fragment and give it an argument for the selected article
            ItemNameFragment newFragment = new ItemNameFragment();
            args.putInt("imageId", imageId);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.item_choice_placeholder, newFragment);

            // Commit the transaction
            transaction.commit();
        }
    }

    @Override
    public void onSetSave(String item, int imageId) {
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, item);
        replyIntent.putExtra("imageID", imageId);
        if(mUpdateId != 0){
            replyIntent.putExtra("updateId", mUpdateId);
            replyIntent.putExtra("updateTime", mUpdateTime);
        }
        setResult(RESULT_OK, replyIntent);
        finish();
    }

}


