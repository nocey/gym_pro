package com.example.gympro;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProgramActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgramAdapter adapter;
    private ArrayList<Question> questionsList;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        recyclerView = findViewById(R.id.recyclerView);
        saveButton = findViewById(R.id.saveButton);

        questionsList = new ArrayList<>();
        loadQuestions();

        adapter = new ProgramAdapter(this, questionsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(v -> saveAnswersToFirestore());
    }

    private void loadQuestions() {
        // Here you can load the questions either from a local array or from Firestore
        questionsList.add(new Question("What is your fitness goal?", new String[]{"Build Muscle", "Lose Fat", "Maintain Weight"}));
        questionsList.add(new Question("How many days per week do you work out?", new String[]{"1-2", "3-4", "5+"}));
        questionsList.add(new Question("What is your experience level?", new String[]{"Beginner", "Intermediate", "Advanced"}));
        // Add more questions as needed
    }

    private void saveAnswersToFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Create a map for storing questions and answers
            Map<String, Object> questionsData = new HashMap<>();

            for (int i = 0; i < questionsList.size(); i++) {
                String questionKey = "question" + (i + 1);
                Question question = questionsList.get(i);

                // Prepare data for each question
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("answer", question.getSelectedAnswer());
                questionData.put("id", i + 1);

                // Add this question's data to the overall map
                questionsData.put(questionKey, questionData);
            }

            // Save all questions under the user's document
            firestore.collection("users")
                    .document(userId)
                    .collection("answers")
                    .document("questions")
                    .set(questionsData)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(ProgramActivity.this, "Answers saved successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(ProgramActivity.this, "Failed to save answers: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }
}
