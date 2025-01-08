package com.example.gympro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProgramActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgramAdapter adapter;
    private ArrayList<Question> questionsList;
    private Button saveButton;
    private ArrayList<String> selectedAnswers;  // Store selected answers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        recyclerView = findViewById(R.id.recyclerView);
        saveButton = findViewById(R.id.saveButton);

        questionsList = new ArrayList<>();
        selectedAnswers = new ArrayList<>();
        loadQuestions();

        adapter = new ProgramAdapter(questionsList);  // Only pass the list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(v -> {
            // Check if all answers are selected before saving
            if (areAllQuestionsAnswered()) {
                saveAnswersToFirestore();  // Proceed to save if all are answered
            } else {
                Toast.makeText(ProgramActivity.this, "Please answer all the questions.", Toast.LENGTH_SHORT).show(); // Error message
            }
        });
    }

    private void loadQuestions() {
        // Add your 9 questions here
        questionsList.add(new Question("How often do you engage in physical exercise?", new Answer[] {
                new Answer("Rarely", -1, -1, 0),
                new Answer("Occasionally (1-2 times per week)", 0, 0, 0),
                new Answer("Regularly (3 or more times per week)", 1, 1, 0)
        }));
        questionsList.add(new Question("What is your primary fitness goal?", new Answer[] {
                new Answer("Weight loss", -1, 2, 0),
                new Answer("Muscle building", 2, -1, 0),
                new Answer("Overall fitness & health", 1, 1, 0)
        }));
        questionsList.add(new Question("How would you rate your current fitness level?", new Answer[] {
                new Answer("Beginner", -1, -1, 0),
                new Answer("Intermediate", 0, 0, 0),
                new Answer("Advanced", 1, 1, 0)
        }));
        questionsList.add(new Question("What type of exercise do you prefer?", new Answer[] {
                new Answer("Cardio", -1, 2, 0),
                new Answer("Strength training", 2, -1, 0),
                new Answer("A mix of cardio and strength", 1, 1, 0)
        }));
        questionsList.add(new Question("How many days per week can you commit to working out?", new Answer[] {
                new Answer("3 days", 1, 1, 3),
                new Answer("4 days", 2, 2, 4),
                new Answer("5 days", 3, 3, 5)
        }));
        questionsList.add(new Question("What best describes your workout style?", new Answer[] {
                new Answer("Quick and intense", 2, 0, 0),
                new Answer("Moderate and steady", 1, 1, 0),
                new Answer("Relaxed and low intensity", 0, 2, 0)
        }));
        questionsList.add(new Question("How much time do you have for each workout session?", new Answer[] {
                new Answer("20-30 minutes", 0, 1, 0),
                new Answer("30-45 minutes", 1, 1, 0),
                new Answer("45+ minutes", 2, 2, 0)
        }));
        questionsList.add(new Question("How important is variety in your workout routine?", new Answer[] {
                new Answer("Not very important", 1, 0, 0),
                new Answer("Somewhat important", 0, 1, 0),
                new Answer("Very important", 1, 1, 0)
        }));
        questionsList.add(new Question("What helps you stay consistent with workouts?", new Answer[] {
                new Answer("Having a set routine to follow", 2, 0, 0),
                new Answer("Tracking results and progress", 1, 1, 0),
                new Answer("Adding variety and trying new things", 1, 2, 0)
        }));
    }

    private boolean areAllQuestionsAnswered() {
        for (Question question : questionsList) {
            if (question.getSelectedAnswer() == null) {
                return false; // Return false if any question is not answered
            }
        }
        return true; // All questions answered
    }

    private void saveAnswersToFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Create a map for storing questions and answers
            Map<String, Object> questionsData = new HashMap<>();
            int totalStrength = 0;
            int totalCardio = 0;
            int totalDays = 0;

            for (int i = 0; i < questionsList.size(); i++) {
                String questionKey = "question" + (i + 1);
                Question question = questionsList.get(i);

                // Find the selected answer from the list and update the strength, cardio, and day number
                Answer selectedAnswer = question.getSelectedAnswer();
                if (selectedAnswer != null) {
                    totalStrength += selectedAnswer.getStrength();
                    totalCardio += selectedAnswer.getCardio();
                    if (i == 4) {  // Question 5 (index 4) decides the day number
                        totalDays = selectedAnswer.getDayNumber();
                    }
                }

                // Prepare data for each question
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("answer", selectedAnswer != null ? selectedAnswer.getAnswerText() : "");
                questionData.put("id", i + 1);

                // Add this question's data to the overall map
                questionsData.put(questionKey, questionData);
            }

            // Prepare summary data
            Map<String, Object> summaryData = new HashMap<>();
            summaryData.put("totalStrength", totalStrength);
            summaryData.put("totalCardio", totalCardio);
            summaryData.put("totalDays", totalDays);

            // Save the user's answers and summary data in Firestore
            firestore.collection("users")
                    .document(userId)
                    .collection("answers")
                    .document("questions")
                    .set(questionsData)
                    .addOnSuccessListener(aVoid -> {
                        // Save the summary data (total strength, cardio, days)
                        firestore.collection("users")
                                .document(userId)
                                .collection("answers")
                                .document("summary")
                                .set(summaryData)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(ProgramActivity.this, "Answers saved successfully!", Toast.LENGTH_SHORT).show();
                                    goBackToDashboard();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ProgramActivity.this, "Failed to save summary data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProgramActivity.this, "Failed to save answers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goBackToDashboard() {
        // Go back to the Dashboard activity after saving the data
        Intent intent = new Intent(ProgramActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
