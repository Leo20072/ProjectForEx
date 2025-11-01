package com.example.login_and_signup_new;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final Context context;
    private List<Book> booksList;
    private List<String> bookKeys; // המפתחות של Firebase לפעולות מחיקה/עדכון

    // 1. קונסטרוקטור
    public BookAdapter(Context context) {
        this.context = context;
        this.booksList = new ArrayList<>();
        this.bookKeys = new ArrayList<>();
    }

    // 2. מחלקה פנימית BookViewHolder
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        // רכיבי ה-UI מתוך list_item_book.xml
        TextView title, author, pages, category, startDate;
        Button btnView, btnEdit, btnDelete;

        public BookViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_book_title);
            author = itemView.findViewById(R.id.tv_book_author);
            pages = itemView.findViewById(R.id.tv_pages_count);
            category = itemView.findViewById(R.id.tv_category);
            startDate = itemView.findViewById(R.id.tv_start_date); // חדש

            btnView = itemView.findViewById(R.id.btn_view);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    // 3. יצירת ה-ViewHolder (מנפח את ה-XML)
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    // 4. קישור נתונים לרכיבי ה-UI
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = booksList.get(position);
        final String currentKey = bookKeys.get(position); // המפתח הייחודי

        // הצגת פרטי הספר
        holder.title.setText(currentBook.getNameOfBook());
        holder.author.setText("מאת: " + currentBook.getAuthorsname());
        holder.pages.setText("עמודים: " + currentBook.getUploadPagesCount());
        holder.category.setText("קטגוריה: " + currentBook.getUploadCategory());
        holder.startDate.setText("התחלה: " + currentBook.getUploadStartDate());

        // טיפול בלחיצות על הכפתורים
        holder.btnDelete.setOnClickListener(v -> {
            // קריאה לפונקציית מחיקה שתטמיע בהמשך
            // handleBookDelete(currentKey);
            System.out.println("לחצת על מחק לספר ID: " + currentKey);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference booksRootRef = FirebaseDatabase.getInstance().getReference("books");
            DatabaseReference userBooksRef = booksRootRef.child(userId);
            userBooksRef.child(currentKey).removeValue();



        });

        holder.btnEdit.setOnClickListener(v -> {
            // קריאה לפונקציית עדכון שתטמיע בהמשך
            // handleBookEdit(currentBook, currentKey);
            System.out.println("לחצת על עדכן לספר: " + currentBook.getNameOfBook());
        });

        holder.btnView.setOnClickListener(v -> {
            // פעולת הצגת פרטים מלאים
            System.out.println("לחצת על צפה לספר: " + currentBook.getNameOfBook());
        });
    }

    // 5. קבלת מספר הפריטים ברשימה
    @Override
    public int getItemCount() {
        return booksList.size();
    }

    // 6. עדכון רשימת הספרים מה-Activity
    public void setBooks(List<Book> books, List<String> keys) {
        this.booksList = books;
        this.bookKeys = keys;
        notifyDataSetChanged(); // רענון ה-RecyclerView
    }
}