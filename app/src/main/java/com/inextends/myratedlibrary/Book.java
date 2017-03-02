package com.inextends.myratedlibrary;

public class Book {
    private String mTitle;
    private String mAuthorName;
    private int mRating;

    public Book(String title, String authorName, int rating) {
        this.mTitle = title;
        this.mAuthorName = authorName;
        this.mRating = rating;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String mAuthorName) {
        this.mAuthorName = mAuthorName;
    }

    public int getRating() {
        return mRating;
    }

    public void setRating(int mRating) {
        this.mRating = mRating;
    }
}
