package com.example.madproject;

import com.google.gson.annotations.SerializedName;

public class Book {

    @SerializedName("Title")
    private String title;

    @SerializedName("Authors")
    private String author;

    @SerializedName("Description")
    private String description;

    @SerializedName("Publisher")
    private String publisher;

    @SerializedName("Publish Date")
    private String publishDate;

    @SerializedName("Category")
    private String category;

    @SerializedName("Price")
    private String price;

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }
}