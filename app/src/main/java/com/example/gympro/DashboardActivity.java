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
        dayRecyclerView = findViewById(R.id.weekRecyclerView);

        // Set up RecyclerView for the days of the week using DayAdapter
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DayAdapter adapter = new DayAdapter(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"), this);
        dayRecyclerView.setAdapter(adapter);

        // Display welcome message if user is logged in
        if (user != null) {
            TextView welcomeTextView = findViewById(R.id.welcomeTextView);
            welcomeTextView.setText("Welcome, " + user.getEmail());
        }

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

    public void fetchProgramForDay(String day) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Fetch the user's summary data to determine the workout program
            db.collection("users")
                    .document(user.getUid())
                    .collection("answers")
                    .document("summary")
                    .get()
                    .addOnSuccessListener(summaryDocument -> {
                        if (summaryDocument.exists()) {
                            // Log the summary data for debugging
                            Log.d("ProgramFetch", "Summary document data: " + summaryDocument.getData());

                            // Retrieve the list of chosen days and total days
                            List<String> chosenDays = (List<String>) summaryDocument.get("chosenDays");
                            int totalDays = ((Long) summaryDocument.get("totalDays")).intValue();

                            // Log the chosenDays and totalDays for debugging
                            Log.d("ProgramFetch", "Chosen days: " + chosenDays);
                            Log.d("ProgramFetch", "Total days: " + totalDays);

                            // Get the program based on the totalDays
                            String programField = getProgramField(totalDays);

                            // Now fetch the specific program from the 'str' document
                            db.collection("users")
                                    .document(user.getUid())
                                    .collection("programs")
                                    .document("str")
                                    .get()
                                    .addOnSuccessListener(programDocument -> {
                                        if (programDocument.exists()) {
                                            Log.d("ProgramFetch", "Program document data: " + programDocument.getData());

                                            // Fetch the specific program (e.g., five_day, four_day, etc.)
                                            List<Map<String, Object>> selectedProgram = (List<Map<String, Object>>) programDocument.get(programField);

                                            // Check if the selected day exists in the program and fetch the exercises
                                            if (chosenDays != null && chosenDays.contains(day)) {
                                                int dayIndex = chosenDays.indexOf(day);
                                                if (dayIndex != -1 && selectedProgram != null && dayIndex < selectedProgram.size()) {
                                                    Map<String, Object> dayProgram = selectedProgram.get(dayIndex);
                                                    List<Map<String, String>> exercises = (List<Map<String, String>>) dayProgram.get("workouts");

                                                    if (exercises != null && !exercises.isEmpty()) {
                                                        String firstExercise = exercises.get(0).get("name");
                                                        programPreviewTextView.setText("First exercise: " + firstExercise);
                                                        Log.d("ProgramFetch", "First exercise for " + day + ": " + firstExercise);
                                                    } else {
                                                        programPreviewTextView.setText("Rest day");
                                                        Log.d("ProgramFetch", "Rest day for " + day);
                                                    }
                                                } else {
                                                    programPreviewTextView.setText("Rest day");
                                                    Log.d("ProgramFetch", "No workouts found for " + day);
                                                }
                                            } else {
                                                programPreviewTextView.setText("Rest day");
                                                Log.d("ProgramFetch", "Day not found in chosen days");
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
        if (totalDays == 3) {
            return "three_day";
        } else if (totalDays == 4) {
            return "four_day";
        } else if (totalDays == 5) {
            return "five_day";
        } else {
            return ""; // Handle case where totalDays is not 3, 4, or 5
        }
    }
}
