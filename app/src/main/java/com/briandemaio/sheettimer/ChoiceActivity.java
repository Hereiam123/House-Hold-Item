package com.briandemaio.sheettimer;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class ChoiceActivity extends AppCompatActivity implements
        ItemChoiceAdapter.OnItemSelectedListener, ItemNameFragment.OnItemNameFragmentInteractionListener {

    public static final String EXTRA_REPLY =
            "com.briandemaio.sheettimer.REPLY";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private int mUpdateId;
    private long mUpdateTime;
    private static List<Task> allTasks;
    static TaskRoomDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        FragmentManager manager = getSupportFragmentManager();
        Intent intent = getIntent();

        db = Room.databaseBuilder(this, TaskRoomDatabase.class, "MyDB").allowMainThreadQueries().build();
        allTasks = db.taskDao().getAllItems();
        db.close();

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

        Bundle args = new Bundle();
        if(mUpdateId != 0) {
            args.putString("update?", "Update");
        }
        else{
            args.putString("update?", "No update");
        }
        ItemChoiceFragment choiceFragment = new ItemChoiceFragment();
        choiceFragment.setArguments(args);
        manager.beginTransaction()
                .replace(R.id.item_choice_placeholder, choiceFragment).commit();
    }

    public static List<Task> getTasks(){
        return allTasks;
    }

    @Override
    public void onItemSelected(int imageId, String imageString, String itemName) {
        Bundle args = new Bundle();
        if(itemName=="Create Task"){
            Intent myIntent = new Intent(this, CreateTaskActivity.class);
            startActivity(myIntent);
        }
        else if (getResources().getBoolean(R.bool.twoPaneMode)) {
        ItemNameFragment fragment = (ItemNameFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nameFragment);
            if(imageId !=0) {
                fragment.setImage(imageId);
            }
            else{
                fragment.setImageString(imageString);
            }
        } else {
            // replace the fragment
            // Create fragment and give it an argument for the selected article
            ItemNameFragment newFragment = new ItemNameFragment();
            if(imageId !=0){
                args.putInt("imageId", imageId);
            }
            else{
                args.putString("imageString", imageString);
            }
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
    public void onItemLongSelected(String imageString, String itemName) {
        if(imageString != null) {
            db = Room.databaseBuilder(this, TaskRoomDatabase.class, "MyDB").allowMainThreadQueries().build();
            db.taskDao().deleteItem(imageString);
            db.close();
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(
                    getApplicationContext(),
                    "Task "+ itemName+ " deleted",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSetSave(String item, int imageId, String imageString) {
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, item);
        replyIntent.putExtra("imageID", imageId);
        replyIntent.putExtra("imageString", imageString);
        if(mUpdateId != 0){
            replyIntent.putExtra("updateId", mUpdateId);
            replyIntent.putExtra("updateTime", mUpdateTime);
        }
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}


