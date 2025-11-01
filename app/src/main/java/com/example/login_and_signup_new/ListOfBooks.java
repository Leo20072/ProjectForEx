package com.example.login_and_signup_new;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
public class ListOfBooks extends AppCompatActivity {


    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private DatabaseReference userBooksRef;
    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_books); // ודא שזה מכיל את ה-RecyclerView

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
}