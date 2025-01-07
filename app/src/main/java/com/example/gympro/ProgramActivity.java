package com.example.gympro;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ProgramActivity extends AppCompatActivity {

    private RecyclerView programRecyclerView;
    private ProgramAdapter programAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        programRecyclerView = findViewById(R.id.programRecyclerView);
        programRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Generate sample questions with answers
        ArrayList<Question> questions = generateQuestions();

        programAdapter = new ProgramAdapter(questions);
        programRecyclerView.setAdapter(programAdapter);
    }

    // Generate sample gym-related questions with 3 answers each
    private ArrayList<Question> generateQuestions() {
        ArrayList<Question> questions = new ArrayList<>();

        questions.add(new Question("What is your primary goal?", new String[]{"Build muscle", "Lose weight", "Increase endurance"}));
        questions.add(new Question("How many days per week can you commit?", new String[]{"1-2 days", "3-4 days", "5-7 days"}));
        questions.add(new Question("What is your preferred workout time?", new String[]{"Morning", "Afternoon", "Evening"}));
        questions.add(new Question("Do you prefer strength or cardio?", new String[]{"Strength", "Cardio", "Both"}));
        questions.add(new Question("What is your fitness level?", new String[]{"Beginner", "Intermediate", "Advanced"}));
        questions.add(new Question("Do you have any injuries?", new String[]{"Yes", "No", "Unsure"}));
        questions.add(new Question("What equipment do you have access to?", new String[]{"Dumbbells", "Machines", "Bodyweight exercises"}));
        questions.add(new Question("Do you want to include warm-up exercises?", new String[]{"Yes", "No", "Optional"}));
        questions.add(new Question("Do you want a full-body or split routine?", new String[]{"Full-body", "Split routine", "Either"}));

        return questions;
    }
}
