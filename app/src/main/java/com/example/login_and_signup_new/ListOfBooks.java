package com.example.login_and_signup_new;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListOfBooks extends AppCompatActivity {


    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private DatabaseReference userBooksRef;
    private ValueEventListener valueEventListener;
    Button btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_books); // ודא שזה מכיל את ה-RecyclerView

        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListOfBooks.this, MainActivity.class));
            }
        });

        // 1. אתחול ה-RecyclerView
        recyclerView = findViewById(R.id.booksrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // 2. אתחול וחיבור ה-Adapter
        bookAdapter = new BookAdapter(this);
        recyclerView.setAdapter(bookAdapter);

        // 3. התחל לשלוף נתונים
        retrieveUserBooks();

    }

    // שיטת Retrieve (שליפה)
    public void retrieveUserBooks() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // המשתמש לא מחובר
            return;
        }
        String userId = user.getUid();

        userBooksRef = FirebaseDatabase.getInstance().getReference("books").child(userId);

        Query searchQuery;


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Book> booksList = new ArrayList<>();
                List<String> bookKeys = new ArrayList<>();


                for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                    String bookKey = bookSnapshot.getKey();
                    // שימוש במחלקת Book המתוקנת
                    Book book = bookSnapshot.getValue(Book.class);

                    if (book != null && bookKey != null) {
                        booksList.add(book);
                        bookKeys.add(bookKey);
                    }
                }

                // עדכון ה-Adapter עם רשימת הספרים והמפתחות
                bookAdapter.setBooks(booksList, bookKeys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // טיפול בשגיאה
                Toast.makeText(ListOfBooks.this, "שגיאה בקריאת נתונים: ", Toast.LENGTH_SHORT).show();

            }
        };
        // הוספת המאזין (ירוץ פעם אחת וכן בכל שינוי עתידי)
        userBooksRef.addValueEventListener(valueEventListener);

    }

     @Override
    protected void onDestroy() {
        super.onDestroy();
        // הסרת המאזין כדי למנוע דליפת זיכרון
        if (userBooksRef != null && valueEventListener != null) {
            userBooksRef.removeEventListener(valueEventListener);
        }
    }


    public void createDialogEdit() {
        CustomDialogEdit customDialog = new CustomDialogEdit(this);
        customDialog.show();

    }


    public void onEditBook(String bookKey, Book bookToEdit) {
        showEditBookDialog(bookKey, bookToEdit);
    }

    private void showEditBookDialog(String bookKey, Book bookToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("עריכת פרטי ספר");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_edit, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.changeNameBook);
        EditText etAuthor = dialogView.findViewById(R.id.changeAuthorsName);
        EditText etPages = dialogView.findViewById(R.id.currentPagesCount); // חדש
        EditText etImageUrl = dialogView.findViewById(R.id.changeImage); // חדש

        // 2. מילוי השדות בנתונים הנוכחיים של הספר
        etTitle.setText(bookToEdit.getNameOfBook());
        etAuthor.setText(bookToEdit.getAuthorsname());
        etPages.setText(bookToEdit.getUploadPagesCount()); // מילוי מספר העמודים
        etImageUrl.setText(bookToEdit.getUploadImageUrl()); // מילוי קישור התמונה

        // 3. כפתור 'שמור' (עדכון הנתונים ב-Firebase)
        builder.setPositiveButton("שמור שינויים", (dialog, id) -> {

            // א. קבלת הנתונים החדשים
            String newTitle = etTitle.getText().toString().trim();
            String newAuthor = etAuthor.getText().toString().trim();
            String newPagesCount = etPages.getText().toString().trim();
            String newImageUrl = etImageUrl.getText().toString().trim();

            if (newTitle.isEmpty() || newAuthor.isEmpty()) {
                Toast.makeText(this, "שם הספר ושם המחבר אינם יכולים להיות ריקים.", Toast.LENGTH_LONG).show();
                return;
            }

            // ב. יצירת אובייקט Book מעודכן
            Book updatedBook = new Book();

            // --- 1. שדות מעודכנים (מהדיאלוג) ---
            updatedBook.setNameOfBook(newTitle);
            updatedBook.setAuthorsname(newAuthor);
            updatedBook.setUploadPagesCount(newPagesCount);
            updatedBook.setUploadImageUrl(newImageUrl);

            // --- 2. שדות שאינם ניתנים לעריכה (העתקה מהאובייקט המקורי) ---
            // נתונים אלה חיוניים כדי לא לאבד אותם ב-Firebase!
            updatedBook.setUploadCategory(bookToEdit.getUploadCategory());
            updatedBook.setUploadStartDate(bookToEdit.getUploadStartDate());

            // ג. קריאה לשיטת העדכון ב-Firebase
            updateBook(bookKey, updatedBook);

            Toast.makeText(this, "הספר עודכן בהצלחה!", Toast.LENGTH_SHORT).show();
        });

        // 4. כפתור 'ביטול'
        builder.setNegativeButton("ביטול", (dialog, id) -> dialog.cancel());

        // 5. הצגת הדיאלוג
        builder.create().show();
    }

    public void updateBook(String bookKey, Book updatedBook) {
        // 1. קבלת המשתמש המחובר ואימות
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || bookKey == null) {
            System.err.println("שגיאה: משתמש לא מאומת או מפתח ספר חסר לעדכון.");
            return;
        }
        String userId = user.getUid();

        // 2. יצירת הפניה לנתיב הספר הספציפי: books/$UID/$bookKey
        DatabaseReference bookToUpdateRef = FirebaseDatabase.getInstance()
                .getReference("books")
                .child(userId)
                .child(bookKey);

        // 3. יצירת מפה (Map) המכילה את כל השדות לעדכון
        // אנו ממירים את כל האובייקט Book המעודכן למפה.
        Map<String, Object> bookValues = new HashMap<>();

        // הוספת כל ששת השדות מהאובייקט המעודכן
        bookValues.put("authorsname", updatedBook.getAuthorsname());
        bookValues.put("nameOfBook", updatedBook.getNameOfBook());
        bookValues.put("uploadCategory", updatedBook.getUploadCategory());
        bookValues.put("uploadImageUrl", updatedBook.getUploadImageUrl());
        bookValues.put("uploadPagesCount", updatedBook.getUploadPagesCount());
        bookValues.put("uploadStartDate", updatedBook.getUploadStartDate());

        // 4. ביצוע פעולת העדכון (updateChildren)
        bookToUpdateRef.updateChildren(bookValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("✅ הספר עודכן בהצלחה. Key: " + bookKey);
                        // ניתן להוסיף Toast או הודעה למשתמש
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.err.println("❌ כישלון בעדכון הספר: " + e.getMessage());
                        // ניתן להוסיף Toast שגיאה
                    }
                });
    }




}