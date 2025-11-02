package com.example.login_and_signup_new;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class CustomDialogEdit extends Dialog implements View.OnClickListener {
    Button btnAdd1CurrentPagesCount, btnAdd10CurrentPagesCount, btnsave;
    Context context;
    EditText currentPagesCount, changeNameBook, changeAuthorsName, changeImage;

    public CustomDialogEdit(@NonNull Context context) {
        super(context);

        setContentView(R.layout.custom_dialog_edit);
        this.context = context;

        this.changeNameBook = findViewById(R.id.changeNameBook);
        this.changeAuthorsName = findViewById(R.id.changeAuthorsName);
        this.currentPagesCount = findViewById(R.id.currentPagesCount);
        this.changeImage = findViewById(R.id.changeImage);

        this.btnAdd1CurrentPagesCount = findViewById(R.id.btnAdd1CurrentPagesCount);
        this.btnAdd10CurrentPagesCount = findViewById(R.id.btnAdd10CurrentPagesCount);
        // this.btnsave = findViewById(R.id.btnsave);
        btnAdd1CurrentPagesCount.setOnClickListener(this);
        btnAdd10CurrentPagesCount.setOnClickListener(this);
        btnsave.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        if (btnsave == view)
        {

        }
        if(btnAdd1CurrentPagesCount == view)
        {
            dismiss(); // eliminate the dialog
        }

        if(btnAdd10CurrentPagesCount == view)
        {

        }
    }
}