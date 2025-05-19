package com.example.madproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookDetailActivity extends AppCompatActivity {

    private TextView titleText;
    private TextView authorText;
    private TextView descriptionText;
    private TextView publisherText;
    private TextView publishDateText;
    private TextView categoryText;
    private TextView priceText;
    private ProgressBar loadingIndicator;
    private TextView errorText;

    private BookApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
        descriptionText = findViewById(R.id.descriptionText);
        publisherText = findViewById(R.id.publisherText);
        publishDateText = findViewById(R.id.publishDateText);
        categoryText = findViewById(R.id.categoryText);
        priceText = findViewById(R.id.priceText);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        errorText = findViewById(R.id.errorText);

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://bookapi-6ocj.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(BookApiService.class);

        // Setup FAB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> {
                        Snackbar.make(view, "Removed from favorites", Snackbar.LENGTH_SHORT).show();
                    }).show();
        });

        // Get book title from intent
        String bookTitle = getIntent().getStringExtra("BOOK_TITLE");
        if (bookTitle != null && !bookTitle.isEmpty()) {
            loadBookDetails(bookTitle);
        } else {
            showError("Book title not provided");
        }
    }

    private void loadBookDetails(String title) {
        showLoading(true);

        apiService.getBookByTitle(title).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                showLoading(false);

                // Log response code and message
                Log.d("API_RESPONSE", "Code: " + response.code());
                Log.d("API_RESPONSE", "Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    // Log successful response data
                    Book book = response.body();
                    Log.d("API_RESPONSE", "Book title: " + book.getTitle());
                    Log.d("API_RESPONSE", "Book author: " + book.getAuthor());
                    // Log other fields as needed

                    displayBookDetails(response.body());
                } else {
                    // Log error response body if available
                    try {
                        Log.e("API_RESPONSE", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("API_RESPONSE", "Error reading error body", e);
                    }

                    showError("Failed to load book details");
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                showLoading(false);
                Log.e("API_RESPONSE", "Network failure", t);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void displayBookDetails(Book book) {
        titleText.setText(book.getTitle());
        authorText.setText(book.getAuthor());
        descriptionText.setText(book.getDescription());
        publisherText.setText("Publisher: " + book.getPublisher());
        publishDateText.setText("Published: " + book.getPublishDate());
        categoryText.setText("Category: " + book.getCategory());
        priceText.setText("Price: " + (book.getPrice().isEmpty() ? "Not available" : book.getPrice()));
    }

    private void showLoading(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        errorText.setVisibility(View.GONE);
    }

    private void showError(String message) {
        loadingIndicator.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(message);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}