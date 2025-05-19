package com.example.madproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;

public interface BookApiService {

    @GET("api/books")
    Call<BookResponse> getBooks(
            @Query("title") String title,
            @Query("author") String author,
            @Query("category") String category,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @GET("api/books/{title}")
    Call<Book> getBookByTitle(@Path("title") String title);

    @GET("api/categories")
    Call<List<String>> getCategories();
}