package com.example.login_and_signup_new;

public class Book {
    private String nameOfBook;
    private String authorsname;
    private String uploadPagesCount;
    private String dataImage;
    private String uploadCategory;

    public String getNameOfBook() {
        return nameOfBook;
    }

    public String getAuthorsname() {
        return authorsname;
    }

    public String getUploadPagesCount() {
        return uploadPagesCount;
    }

    public String getUploadCategory() {
        return uploadCategory;
    }

    public String getDataImage() {
        return dataImage;
    }

    public Book(String nameOfBook, String authorsname, String uploadPagesCount, String dataImage, String uploadCategory) {
        this.nameOfBook = nameOfBook;
        this.authorsname = authorsname;
        this.uploadPagesCount = uploadPagesCount;
        this.dataImage = dataImage;
        this.uploadCategory = uploadCategory;
    }

    public Book(){
    }
}
