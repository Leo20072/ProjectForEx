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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Addbook extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private String[] arrCategories = { "מדע בדיוני", "פנטזיה", "מתח / מותחן", "בלשי", "רומן רומנטי", "אימה", "היסטורי" };
    private String choosecategory;
    EditText uploadImageUrl;
    Button saveButton;
    EditText nameofbook, authorsname, uploadPagesCount, uploadStartDate;
    String imageURL;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

        uploadImageUrl = findViewById(R.id.uploadImageUrl);
        nameofbook = findViewById(R.id.nameofbook);
        authorsname = findViewById(R.id.authorsname);
        uploadPagesCount = findViewById(R.id.uploadPagesCount);
        uploadStartDate = findViewById(R.id.uploadStartDate);
        saveButton = findViewById(R.id.saveButton);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namebook = nameofbook.getText().toString();
                String image = uploadImageUrl.getText().toString();
                String author = authorsname.getText().toString();
                String pagesCount = uploadPagesCount.getText().toString();
                String startDate = uploadStartDate.getText().toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    System.out.println("Error: User is not authenticated.");
                    return;
                }

                DatabaseReference booksRootRef = FirebaseDatabase.getInstance().getReference("books");
                DatabaseReference userBooksRef = booksRootRef.child(userId);

                String newBookId = userBooksRef.push().getKey();

                if (newBookId != null) {
                    Book newbook = new Book(namebook, author, pagesCount, image, choosecategory,startDate);


                    // 4. שמירת הנתונים בנתיב החדש באמצעות setValue()
                    userBooksRef.child(newBookId).setValue(newbook)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(Addbook.this, "Book saved successfully for user: " + userId, Toast.LENGTH_SHORT).show();
                                // עדכון ממשק המשתמש (UI) בהצלחה
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Addbook.this, "Failed to save book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                // הצגת הודעת שגיאה למשתמש
                            });
                }
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        choosecategory = arrCategories[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}