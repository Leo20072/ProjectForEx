package com.example.login_and_signup_new;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Addbook extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private String[] arrCategories = { "מדע בדיוני", "פנטזיה", "מתח / מותחן", "בלשי", "רומן רומנטי", "אימה", "היסטורי" };
    private String choosecategory;
    ImageView uploadImage;
    Button saveButton;
    EditText uploadTopic, uploadDesc, uploadLang;
    String imageURL;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addbook);

            spinner = findViewById(R.id.uploadCategory);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter aa =
                new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrCategories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        choosecategory = arrCategories[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}