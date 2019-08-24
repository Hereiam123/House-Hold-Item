package com.briandemaio.sheettimer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

import static com.briandemaio.sheettimer.ChoiceActivity.REQUEST_IMAGE_CAPTURE;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText mEditNameView;
    private ImageView mItemImageView;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
        mItemImageView = findViewById(R.id.itemImageView);
        mEditNameView = findViewById(R.id.editText);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String item = mEditNameView.getText().toString();
                if (TextUtils.isEmpty(mEditNameView.getText())) {
                    Toast.makeText(CreateTaskActivity.this, "You forgot to add a task name!", Toast.LENGTH_LONG ).show();
                }
                else {
                    Task newTask = new Task(item, mUri.toString());
                    ChoiceActivity.mTaskViewModel.insert(newTask);
                    Intent myIntent = new Intent(CreateTaskActivity.this, ChoiceActivity.class);
                    startActivity(myIntent);
                }
            }
        });
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mItemImageView.setImageBitmap(imageBitmap);
            mUri = getImageUri(getApplicationContext(), imageBitmap);
            Toast.makeText(CreateTaskActivity.this,"Here "+ mUri, Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, "Failed To Capture Image", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(CreateTaskActivity.this.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }
}
