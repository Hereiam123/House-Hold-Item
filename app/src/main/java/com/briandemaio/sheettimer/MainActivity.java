package com.briandemaio.sheettimer;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends AppCompatActivity{

    private AdView mAdView;

    private FirebaseAnalytics mFirebaseAnalytics;

    private ItemViewModel mItemViewModel;
    private SwipeController mSwipeController;
    public static final int NEW_ITEM_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_ITEM_ACTIVITY_REQUEST_CODE = 2;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        int gridColumnCount =
                getResources().getInteger(R.integer.grid_column_count);

        RecyclerView recyclerView = findViewById(R.id.main_view);
        final AddedItemAdapter adapter = new AddedItemAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridColumnCount));

        mItemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        mItemViewModel.getmAllItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable final List<Item> items) {
                // Update the cached copy of the words in the adapter.
                adapter.setItems(items);
            }
        });

        mSwipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Item myItem = adapter.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "Deleting " +
                        myItem.getName(), Toast.LENGTH_LONG).show();

                //Delete Alarm
                cancelItemTimeAlarm(myItem);

                // Delete the item
                mItemViewModel.delete(myItem);

                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
            }

            @Override
            public void onLeftClicked(int position){
                Item myItem = adapter.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, ChoiceActivity.class);
                intent.putExtra("updateId", myItem.getId());
                intent.putExtra("updateTime", myItem.getExpiryTime());
                startActivityForResult(intent, UPDATE_ITEM_ACTIVITY_REQUEST_CODE);
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
                startActivityForResult(intent, NEW_ITEM_ACTIVITY_REQUEST_CODE);
            }
        });

            adapter.setOnItemClickListener(new AddedItemAdapter.ClickListener()  {
            @Override
            public void onCleanItemClick(View v, int position) {
                Item item = adapter.getItemAtPosition(position);
                long expiryTime = System.currentTimeMillis() + getResources().getInteger(R.integer.default_timer_value);
                item.setExpiryTime(expiryTime);
                setItemTimeAlarm(item);
                mItemViewModel.update(item);
            }

            @Override
            public void onEditTimeClick(final View v, final int position) {

                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            //Get set expiration year, month and date for future use
                            final int expirYear = year;
                            final int expirMonth = month + 1;
                            final int expirDay= dayOfMonth;

                            //get current hour of day and minute
                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                            int mMinute = c.get(Calendar.MINUTE);

                            // Launch Time Picker Dialog
                            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay,
                                                              int minute) {
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                            Date date = null;
                                            try {
                                                date = sdf.parse(expirYear+"/"+expirMonth+"/"+expirDay+" "+hourOfDay+":"+minute+":00");
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            Item item = adapter.getItemAtPosition(position);
                                            long expiryTime = date.getTime();
                                            item.setExpiryTime(expiryTime);
                                            setItemTimeAlarm(item);
                                            mItemViewModel.update(item);
                                        }
                                    }, mHour, mMinute, false);
                            timePickerDialog.show();
                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
            }
        });

        createNotificationChannel();
        MobileAds.initialize(this, "ca-app-pub-2580444339985264~2889913723");
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

    public void setItemTimeAlarm(Item item) {
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        int timeId = (int) item.getExpiryTime();
        notifyIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, timeId);
        notifyIntent.putExtra(AlarmReceiver.NOTIFICATION, item.getName());
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, timeId, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                item.getExpiryTime(), notifyPendingIntent);
    }

    public void cancelItemTimeAlarm(Item item) {
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        int timeId = (int) item.getExpiryTime();
        notifyIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, timeId);
        PendingIntent cancelPendingIntent= PendingIntent.getBroadcast
                (this, timeId, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        cancelPendingIntent.cancel();
        alarmManager.cancel(cancelPendingIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Item item;
        int imageIdExtra;
        String imageStringExtra;
        if (requestCode == NEW_ITEM_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
             imageIdExtra = data.getIntExtra("imageID", 0);
            imageStringExtra = data.getStringExtra("imageString");
            long triggerTime = System.currentTimeMillis() + getResources().getInteger(R.integer.default_timer_value);
            if(imageIdExtra!=0) {
                item = new Item(data.getStringExtra(ChoiceActivity.EXTRA_REPLY), imageIdExtra, triggerTime);
            }
            else{
                item = new Item(data.getStringExtra(ChoiceActivity.EXTRA_REPLY), imageStringExtra, triggerTime);
            }
            mItemViewModel.insert(item);
            setItemTimeAlarm(item);
        }
        else if(requestCode == UPDATE_ITEM_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            imageIdExtra = data.getIntExtra("imageID", 0);
            imageStringExtra = data.getStringExtra("imageString");
            if(imageIdExtra!=0) {
                item = new Item(data.getStringExtra(ChoiceActivity.EXTRA_REPLY), imageIdExtra);
            }
            else{
                item = new Item(data.getStringExtra(ChoiceActivity.EXTRA_REPLY), imageStringExtra);
            }
            item.setId(data.getIntExtra("updateId",0));
            item.setExpiryTime(data.getLongExtra("updateTime", 0));
            mItemViewModel.update(item);
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
                            "Item Cleaning Notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies user to clean");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
