package com.briandemaio.sheettimer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.briandemaio.sheettimer.ChoiceActivity.REQUEST_IMAGE_CAPTURE;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText mEditNameView;
    private ImageView mItemImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
        mItemImageView = findViewById(R.id.itemImageView);
        mEditNameView = findViewById(R.id.editText);

        /*final Button button = view.findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String item = mEditNameView.getText().toString();
                if (TextUtils.isEmpty(mEditNameView.getText())) {
                    Toast.makeText(getActivity(), "You forgot to add a task name!", Toast.LENGTH_LONG ).show();
                }
                else {
                    mListener.onSetSave(item, mImageId);
                }
            }
        });*/

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
        }
    }
}
