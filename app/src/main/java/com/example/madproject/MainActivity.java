package com.example.madproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private ProgressBar loadingIndicator;
    private TextView errorText, pageIndicator;
    private Button nextPageButton, prevPageButton;

    private BookApiService apiService;
    private List<Book> booksList = new ArrayList<>();
    private int currentPage = 1, totalPages = 1;
    private String userPreferredCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        booksRecyclerView = findViewById(R.id.booksRecyclerView);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        errorText = findViewById(R.id.errorText);
        nextPageButton = findViewById(R.id.nextPageButton);
        prevPageButton = findViewById(R.id.prevPageButton);
        pageIndicator = findViewById(R.id.pageIndicator);

        // Setup RecyclerView
        bookAdapter = new BookAdapter(this, booksList);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setAdapter(bookAdapter);

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://bookapi-6ocj.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(BookApiService.class);

        // Get the user’s preferred category from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        userPreferredCategory = preferences.getString("preferredCategory", "");
        if (userPreferredCategory.isEmpty()) {
            Toast.makeText(this, "No category Found", Toast.LENGTH_SHORT).show();
        } else {
            loadBooks(userPreferredCategory, 1);
        }

        // Pagination button actions
        nextPageButton.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadBooks(userPreferredCategory, currentPage);
            }
        });

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadBooks(userPreferredCategory, currentPage);
            }
        });
    }

    private void loadBooks(String category, int page) {
        showLoading(true);
        apiService.getBooks("", "", category, page, 10).enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "Category: " + category, Toast.LENGTH_SHORT).show();
                    BookResponse bookResponse = response.body();

                    // Debug API response
                    if (bookResponse.getBooks() != null) {
                        Toast.makeText(MainActivity.this, "Books fetched from API: " + bookResponse.getBooks().size(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Book list is null", Toast.LENGTH_SHORT).show();
                    }

                    booksList.clear();
                    booksList.addAll(bookResponse.getBooks());
                    bookAdapter.notifyDataSetChanged();

                    totalPages = bookResponse.getTotalPages();
                    updatePaginationControls();
                } else {
                    showError("No books available for this category.");
                }
            }


            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void updatePaginationControls() {
        pageIndicator.setText(String.format("Page %d of %d", currentPage, totalPages));
        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
    }

    private void showLoading(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        booksRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        errorText.setVisibility(View.GONE);
    }

    private void showError(String message) {
        loadingIndicator.setVisibility(View.GONE);
        booksRecyclerView.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(message);
    }
}
