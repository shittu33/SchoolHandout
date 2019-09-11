package com.example.testingmodle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amazing_picker.activities.Picker_Activity;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    public static final int START_PICKER_REQUEST_CODE = 8787;
    public static final String MainActivity_TAG = "MainActivityDebug";
    public static final int PDF_SELECTED = 2222;
    public static final int IMAGES_SELECTED = 3333;
    public static final int NO_IMAGES_SELECTED = 4444;
    public static final int ACTION_CANCEL = 5555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_pick;
        btn_pick = findViewById(R.id.btn_pick);
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Picker_Activity.class);
                intent.putExtra("book_name", "yes");
                startActivityForResult(intent, START_PICKER_REQUEST_CODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_PICKER_REQUEST_CODE) {
            if (resultCode == NO_IMAGES_SELECTED) {
                Log.i(MainActivity_TAG, "no image");
                Toast.makeText(this, "no image was selected", Toast.LENGTH_SHORT).show();
                return;
            }
            if (resultCode == IMAGES_SELECTED || resultCode == PDF_SELECTED) {
                Log.i(MainActivity_TAG, " onActivityResult: some images are selected");
                ArrayList<String> images = data.getStringArrayListExtra("pics");
                assert images != null;
                Collections.reverse(images);
                if (resultCode == IMAGES_SELECTED) {
                    Log.i(MainActivity_TAG, "some image are selected ");
                } else {
                    String pdf_name = data.getStringExtra("pdf_name");
                    Log.i(MainActivity_TAG, "some pdf page selected from " + pdf_name);
                }
            }
        }

    }
}
