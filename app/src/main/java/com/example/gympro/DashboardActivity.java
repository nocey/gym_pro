package com.example.gympro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView dayRecyclerView;
    private TextView programPreviewTextView;
    private Button logoutButton;
    private Button createProgramButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize FirebaseAuth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();

        // Find views
        programPreviewTextView = findViewById(R.id.programPreviewTextView);
        logoutButton = findViewById(R.id.logoutButton);
        createProgramButton = findViewById(R.id.createProgramButton);
        dayRecyclerView = findViewById(R.id.dayRecyclerView);

        // Set up RecyclerView for the days of the week using DayAdapter
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        DayAdapter adapter = new DayAdapter(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"), this, this::fetchProgramForDay);
        dayRecyclerView.setAdapter(adapter);

        // Logout button logic
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        });

        // Navigate to the new RecyclerView activity (for program creation)
        createProgramButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProgramActivity.class);
            startActivity(intent);
        });
    }

    // This method fetches the program for the selected day
    public void fetchProgramForDay(String day) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users")
                    .document(user.getUid())
                    .collection("answers")
                    .document("summary")
                    .get()
                    .addOnSuccessListener(summaryDocument -> {
                        if (summaryDocument.exists()) {
                            List<String> chosenDays = (List<String>) summaryDocument.get("chosenDays");
                            int totalDays = ((Long) summaryDocument.get("totalDays")).intValue();
                            String programField = getProgramField(totalDays);

                            db.collection("users")
                                    .document(user.getUid())
                                    .collection("Programs")
                                    .document("str")
                                    .get()
                                    .addOnSuccessListener(programDocument -> {
                                        if (programDocument.exists()) {
                                            List<Map<String, Object>> selectedProgram = (List<Map<String, Object>>) programDocument.get(programField);

                                            if (chosenDays != null && chosenDays.contains(day)) {
                                                int dayIndex = chosenDays.indexOf(day);
                                                if (dayIndex != -1 && selectedProgram != null && dayIndex < selectedProgram.size()) {
                                                    Map<String, Object> dayProgram = selectedProgram.get(dayIndex);
                                                    List<Map<String, String>> exercises = (List<Map<String, String>>) dayProgram.get("workouts");

                                                    if (exercises != null && !exercises.isEmpty()) {
                                                        StringBuilder programDetails = new StringBuilder("Workout for " + day + ":\n");
                                                        for (Map<String, String> exercise : exercises) {
                                                            String name = exercise.get("name");
                                                            String sets = exercise.get("sets");
                                                            programDetails.append("- ").append(name).append(" (").append(sets).append(" sets)\n");
                                                        }
                                                        programPreviewTextView.setText(programDetails.toString());
                                                    } else {
                                                        programPreviewTextView.setText("Rest day");
                                                    }
                                                } else {
                                                    programPreviewTextView.setText("Rest day");
                                                }
                                            } else {
                                                programPreviewTextView.setText("Rest day");
                                            }
                                        } else {
                                            Log.d("ProgramFetch", "No program data found!");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        programPreviewTextView.setText("Error fetching program.");
                                        Log.e("ProgramFetch", "Error fetching program document: ", e);
                                    });
                        } else {
                            Log.d("ProgramFetch", "No summary document found!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        programPreviewTextView.setText("Error fetching summary.");
                        Log.e("ProgramFetch", "Error fetching summary document: ", e);
                    });
        }
    }

    // Helper method to determine which field to fetch from the 'str' document based on totalDays
    private String getProgramField(int totalDays) {
        switch (totalDays) {
            case 3:
                return "three_day";
            case 4:
                return "four_day";
            case 5:
                return "five_day";
            default:
                return "seven_day"; // Default
        }
    }
}
