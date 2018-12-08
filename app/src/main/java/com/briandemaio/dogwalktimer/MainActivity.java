package com.briandemaio.dogwalktimer;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import static com.briandemaio.dogwalktimer.ChoiceActivity.*;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    private DoggyViewModel mDoggyViewModel;
    private SwipeController mSwipeController;
    public static final int NEW_DOGGY_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_DOGGY_ACTIVITY_REQUEST_CODE = 2;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int gridColumnCount =
                getResources().getInteger(R.integer.grid_column_count);

        RecyclerView recyclerView = findViewById(R.id.main_view);
        final AddedDoggyAdapter adapter = new AddedDoggyAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridColumnCount));

        mDoggyViewModel = ViewModelProviders.of(this).get(DoggyViewModel.class);
        mDoggyViewModel.getmAllDoggies().observe(this, new Observer<List<Doggy>>() {
            @Override
            public void onChanged(@Nullable final List<Doggy> doggies) {
                // Update the cached copy of the words in the adapter.
                adapter.setDoggies(doggies);
            }
        });

        mSwipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Doggy myDoggy = adapter.getDoggyAtPosition(position);
                Toast.makeText(MainActivity.this, "Deleting " +
                        myDoggy.getName(), Toast.LENGTH_LONG).show();

                //Delete Alarm
                cancelDoggyTimeAlarm(myDoggy);

                // Delete the doggy
                mDoggyViewModel.delete(myDoggy);

                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
            }

            @Override
            public void onLeftClicked(int position){
                Doggy myDoggy = adapter.getDoggyAtPosition(position);
                Intent intent = new Intent(MainActivity.this, ChoiceActivity.class);
                intent.putExtra("updateId", myDoggy.getId());
                intent.putExtra("updateTime", myDoggy.getExpiryTime());
                startActivityForResult(intent, UPDATE_DOGGY_ACTIVITY_REQUEST_CODE);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(mSwipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                mSwipeController.onDraw(c);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChoiceActivity.class);
                startActivityForResult(intent, NEW_DOGGY_ACTIVITY_REQUEST_CODE);
            }
        });

        adapter.setOnItemClickListener(new AddedDoggyAdapter.ClickListener()  {
            @Override
            public void onWalkItemClick(View v, int position) {
                Doggy doggy = adapter.getDoggyAtPosition(position);
                long expiryTime = System.currentTimeMillis() + 43200000;
                doggy.setExpiryTime(expiryTime);
                setDoggyTimeAlarm(doggy);
                mDoggyViewModel.update(doggy);
            }
        });

        createNotificationChannel();
        MobileAds.initialize(this, "ca-app-pub-2580444339985264~7777983759");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.reverse_order) {
            return true;
        }
        else if(id == R.id.original_order){

        }

        return super.onOptionsItemSelected(item);
    }

    public void setDoggyTimeAlarm(Doggy doggy) {
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        int timeId = (int) doggy.getExpiryTime();
        notifyIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, timeId);
        notifyIntent.putExtra(AlarmReceiver.NOTIFICATION, doggy.getName());
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, timeId, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                doggy.getExpiryTime(), notifyPendingIntent);
    }

    public void cancelDoggyTimeAlarm(Doggy doggy) {
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        int timeId = (int) doggy.getExpiryTime();
        notifyIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, timeId);
        PendingIntent cancelPendingIntent= PendingIntent.getBroadcast
                (this, timeId, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        cancelPendingIntent.cancel();
        alarmManager.cancel(cancelPendingIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_DOGGY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            long triggerTime = System.currentTimeMillis() + 43200000;
            Doggy doggy = new Doggy(data.getStringExtra(EXTRA_REPLY), data.getIntExtra("imageID", 0), triggerTime);
            mDoggyViewModel.insert(doggy);
            setDoggyTimeAlarm(doggy);
        }
        else if(requestCode == UPDATE_DOGGY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            Doggy doggy = new Doggy(data.getStringExtra(EXTRA_REPLY), data.getIntExtra("imageID", 0));
            doggy.setId(data.getIntExtra("updateId",0));
            doggy.setExpiryTime(data.getLongExtra("updateTime", 0));
            mDoggyViewModel.update(doggy);
        }
        else {
            Toast.makeText(
                    getApplicationContext(),
                    "Not Saved",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Doggy Water Notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies user to water plants");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
