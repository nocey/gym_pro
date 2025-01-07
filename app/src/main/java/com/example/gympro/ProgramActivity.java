package com.example.gympro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgramAdapter adapter;
    private List<Question> questionList;
    private Button saveAnswersButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize question list
        questionList = new ArrayList<>();
        loadQuestions();

        // Initialize and set the adapter
        adapter = new ProgramAdapter(questionList);
        recyclerView.setAdapter(adapter);

        // Set up Save Answers button
        saveAnswersButton = findViewById(R.id.saveAnswersButton);
        saveAnswersButton.setOnClickListener(v -> saveAnswersToFirestore());
    }

    private void loadQuestions() {
        // Load sample questions and answers
        questionList.add(new Question("What is your fitness goal?", List.of(new String[]{"Build muscle", "Lose weight", "Improve endurance"})));
        questionList.add(new Question("How many times do you work out per week?", List.of(new String[]{"1-2 times", "3-4 times", "5+ times"})));
        questionList.add(new Question("What type of workout do you prefer?", List.of(new String[]{"Cardio", "Strength", "Flexibility"})));

        // Add more questions as needed
    }

    private void saveAnswersToFirestore() {
        // Loop through the questions and get selected answers
        for (Question question : questionList) {
            Map<String, Object> answerData = new HashMap<>();
            answerData.put("questionText", question.getQuestionText());
            answerData.put("selectedAnswer", question.getSelectedAnswer());

            // Save each question and selected answer to Firestore
            db.collection("userAnswers")
                    .add(answerData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ProgramActivity.this, "Answers saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProgramActivity.this, "Error saving answers", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
