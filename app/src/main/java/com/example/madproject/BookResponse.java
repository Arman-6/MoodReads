package com.example.madproject;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookResponse {

    @SerializedName("total")
    private int total;

    @SerializedName("page")
    private int page;

    @SerializedName("per_page")
    private int perPage;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("books")
    private List<Book> books;

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Book> getBooks() {
        return books;
    }
}