package com.example.madproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;

public class PreferenceTestActivity extends AppCompatActivity {

    private TextView questionText;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private TextView progressText;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int generalFictionScore = 0;
    private int literaryFictionScore = 0;
    private int mysteryDetectiveScore = 0;
    private int thrillerFictionScore = 0;
    private int romanceContemporaryScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_test);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);
        progressText = findViewById(R.id.progressText);

        // Setup questions
        setupQuestions();

        // Display first question
        displayQuestion(currentQuestionIndex);

        // Setup next button
        nextButton.setOnClickListener(v -> {
            // Get selected answer and update scores
            int selectedId = optionsGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedButton = findViewById(selectedId);
                int selectedIndex = optionsGroup.indexOfChild(selectedButton);
                updateScores(currentQuestionIndex, selectedIndex);

                // Move to next question or finish
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    displayQuestion(currentQuestionIndex);
                } else {
                    finishTest();
                }
            } else {
                Toast.makeText(PreferenceTestActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupQuestions() {
        questions = new ArrayList<>();

        // More specific questions with single category options
        questions.add(new Question(
                "Do you enjoy reading general fiction?",
                new String[]{"Yes, very much", "Somewhat", "Not really", "Not at all"}
        ));

        questions.add(new Question(
                "How interested are you in literary fiction?",
                new String[]{"Very interested", "Moderately interested", "Slightly interested", "Not interested"}
        ));

        questions.add(new Question(
                "Do you enjoy mystery and detective stories?",
                new String[]{"Yes, very much", "Somewhat", "Not really", "Not at all"}
        ));

        questions.add(new Question(
                "How much do you like thriller novels?",
                new String[]{"Love them", "Like them", "Neutral", "Not my preference"}
        ));

        questions.add(new Question(
                "Are you interested in contemporary romance?",
                new String[]{"Very interested", "Moderately interested", "Slightly interested", "Not interested"}
        ));

        questions.add(new Question(
                "How often do you read general fiction books?",
                new String[]{"Very often", "Sometimes", "Rarely", "Never"}
        ));

        questions.add(new Question(
                "Would you choose a literary fiction book for leisure reading?",
                new String[]{"Definitely", "Probably", "Maybe", "Unlikely"}
        ));

        questions.add(new Question(
                "Do you find mystery stories engaging?",
                new String[]{"Very engaging", "Somewhat engaging", "Not very engaging", "Not engaging at all"}
        ));

        questions.add(new Question(
                "How do thriller novels make you feel?",
                new String[]{"Excited and engaged", "Interested", "Neutral", "Uncomfortable"}
        ));

        questions.add(new Question(
                "Do romance stories appeal to you?",
                new String[]{"Strongly appeal", "Somewhat appeal", "Neutral", "Don't appeal"}
        ));
    }

    private void displayQuestion(int index) {
        Question question = questions.get(index);
        questionText.setText(question.getQuestion());

        // Clear previous options
        optionsGroup.removeAllViews();

        // Add options
        for (String option : question.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setTextSize(16);
            radioButton.setPadding(0, 16, 0, 16);
            optionsGroup.addView(radioButton);
        }

        // Call styling method after adding radio buttons
        setupRadioButtonStyling();

        // Update progress text
        progressText.setText(String.format("Question %d/%d", index + 1, questions.size()));

        // Reset selection
        optionsGroup.clearCheck();
    }


    private void updateScores(int questionIndex, int selectedOptionIndex) {
        // Calculate points based on answer strength (4,3,2,1 or reverse depending on question)
        int points = 0;

        // Positive answers get higher points
        if (selectedOptionIndex == 0) points = 4;
        else if (selectedOptionIndex == 1) points = 3;
        else if (selectedOptionIndex == 2) points = 2;
        else if (selectedOptionIndex == 3) points = 1;

        // Distribute points to appropriate category based on question
        if (questionIndex == 0 || questionIndex == 5) {
            // General fiction questions
            generalFictionScore += points;
        } else if (questionIndex == 1 || questionIndex == 6) {
            // Literary fiction questions
            literaryFictionScore += points;
        } else if (questionIndex == 2 || questionIndex == 7) {
            // Mystery & detective questions
            mysteryDetectiveScore += points;
        } else if (questionIndex == 3 || questionIndex == 8) {
            // Thriller questions
            thrillerFictionScore += points;
        } else if (questionIndex == 4 || questionIndex == 9) {
            // Romance questions
            romanceContemporaryScore += points;
        }
    }

    private void finishTest() {
        String preferredCategory = getPreferredCategory();

        // Store category in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("preferredCategory", preferredCategory);
        editor.apply();

        // Show toast to confirm selection
        Toast.makeText(this, "Preference saved: " + preferredCategory, Toast.LENGTH_SHORT).show();

        // Navigate to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String getPreferredCategory() {
        // Determine the highest-scoring category
        int maxScore = Math.max(
                Math.max(
                        Math.max(
                                Math.max(generalFictionScore, literaryFictionScore),
                                mysteryDetectiveScore
                        ),
                        thrillerFictionScore
                ),
                romanceContemporaryScore
        );

        // Return single category names without commas
        if (maxScore == generalFictionScore) return "General";
        if (maxScore == literaryFictionScore) return "Literary";
        if (maxScore == mysteryDetectiveScore) return "Mystery";
        if (maxScore == thrillerFictionScore) return "Thrillers";
        return "Romance";
    }

    private static class Question {
        private String question;
        private String[] options;

        Question(String question, String[] options) {
            this.question = question;
            this.options = options;
        }

        String getQuestion() {
            return question;
        }

        String[] getOptions() {
            return options;
        }
    }
    private void setupRadioButtonStyling() {
        int childCount = optionsGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioButton radioButton = (RadioButton) optionsGroup.getChildAt(i);

            // Set custom styling for radio buttons
            radioButton.setTextSize(16);
            radioButton.setPadding(24, 24, 24, 24);

            // Apply custom background and elevation when checked
            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    buttonView.setBackgroundResource(R.drawable.selected_option_background);
                    ViewCompat.setElevation(buttonView, 4f);
                } else {
                    buttonView.setBackgroundResource(R.drawable.option_background);
                    ViewCompat.setElevation(buttonView, 0f);
                }
            });

            // Set initial state
            radioButton.setBackgroundResource(R.drawable.option_background);
        }
    }
}