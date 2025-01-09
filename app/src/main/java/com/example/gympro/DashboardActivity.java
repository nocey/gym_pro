package com.example.gympro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize FirebaseAuth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find views
        dayRecyclerView = findViewById(R.id.dayRecyclerView);

        // Set up RecyclerView for the days of the week using DayAdapter
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        DayAdapter adapter = new DayAdapter(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"), this, this::fetchProgramForDay);
        dayRecyclerView.setAdapter(adapter);
    }

    // Inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            // Logout logic
            mAuth.signOut();
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
            return true;
        } else if (id == R.id.menu_create_program) {
            // Navigate to ProgramActivity
            Intent intent = new Intent(DashboardActivity.this, ProgramActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Fetch program for the selected day (restored to the original logic)
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

                                            LinearLayout programLayout = findViewById(R.id.programLayout);
                                            programLayout.removeAllViews();  // Clear any previous content

                                            if (chosenDays != null && chosenDays.contains(day)) {
                                                int dayIndex = chosenDays.indexOf(day);
                                                if (dayIndex != -1 && selectedProgram != null && dayIndex < selectedProgram.size()) {
                                                    Map<String, Object> dayProgram = selectedProgram.get(dayIndex);
                                                    List<Map<String, String>> exercises = (List<Map<String, String>>) dayProgram.get("workouts");

                                                    // Clear WebView (not used here)
                                                    WebView youtubeWebView = findViewById(R.id.youtubeWebView);
                                                    youtubeWebView.setVisibility(View.GONE);

                                                    if (exercises != null && !exercises.isEmpty()) {
                                                        // Show workout program
                                                        for (Map<String, String> exercise : exercises) {
                                                            String name = exercise.get("name");
                                                            String sets = exercise.get("sets");
                                                            String link = exercise.get("link");

                                                            // Create a TextView for each exercise name and sets
                                                            TextView exerciseText = new TextView(DashboardActivity.this);
                                                            exerciseText.setText(name + " (" + sets + " sets)");

                                                            // Set background and text color
                                                            exerciseText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                            exerciseText.setTextColor(getResources().getColor(R.color.colorSecondary));

                                                            // Center the text
                                                            exerciseText.setGravity(Gravity.CENTER);

                                                            // Increase the text size and make them a bit bigger
                                                            exerciseText.setTextSize(20); // Adjust size as needed

                                                            // Add margin to center better vertically
                                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                            params.setMargins(0, 20, 0, 20); // Margin top and bottom to separate from other elements
                                                            exerciseText.setLayoutParams(params);

                                                            programLayout.addView(exerciseText);

                                                            // If the exercise has a link, add a button to watch the video
                                                            if (link != null && !link.isEmpty()) {
                                                                TextView watchVideoButton = new TextView(DashboardActivity.this);
                                                                watchVideoButton.setText("Watch Video");

                                                                // Set background and text color for the watch button
                                                                watchVideoButton.setBackgroundColor(getResources().getColor(R.color.colorSecondary)); // Secondary color for background
                                                                watchVideoButton.setTextColor(getResources().getColor(R.color.colorPrimary)); // Primary color for text

                                                                // Center the text for the watch button
                                                                watchVideoButton.setGravity(Gravity.CENTER);

                                                                // Increase the text size for the "Watch Video" button (make it bigger than the other text)
                                                                watchVideoButton.setTextSize(24); // Adjust size to make it bigger than exercise text

                                                                // Add padding for spacing
                                                                watchVideoButton.setPadding(0, 10, 0, 10);

                                                                // Set the click listener to open the video
                                                                watchVideoButton.setOnClickListener(v -> {
                                                                    // Open the video in a new activity
                                                                    Intent intent = new Intent(DashboardActivity.this, VideoActivity.class);
                                                                    intent.putExtra("VIDEO_URL", link);  // Pass the video URL
                                                                    startActivity(intent);
                                                                });

                                                                // Add the "Watch Video" button below the exercise text
                                                                programLayout.addView(watchVideoButton);
                                                            }
                                                        }
                                                    }

                                                } else {
                                                    // Show "Rest day" message
                                                    TextView restDayText = new TextView(DashboardActivity.this);
                                                    // Set background color to colorPrimary and text color to colorSecondary
                                                    restDayText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                    restDayText.setTextColor(getResources().getColor(R.color.colorSecondary));

                                                    restDayText.setGravity(Gravity.CENTER);

                                                    restDayText.setTextSize(20); // You can adjust the value to make it as big as needed
                                                    restDayText.setText("Rest day");
                                                    programLayout.addView(restDayText);
                                                }
                                            } else {
                                                // Day not chosen, show "Rest day" message
                                                TextView restDayText = new TextView(DashboardActivity.this);
                                                // Set background color to colorPrimary and text color to colorSecondary
                                                restDayText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                restDayText.setTextColor(getResources().getColor(R.color.colorSecondary));

                                                restDayText.setGravity(Gravity.CENTER);

                                                restDayText.setTextSize(20); // You can adjust the value to make it as big as needed
                                                restDayText.setText("Rest day");
                                                programLayout.addView(restDayText);
                                            }
                                        } else {
                                            Log.d("ProgramFetch", "No program data found!");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ProgramFetch", "Error fetching program document: ", e);
                                    });
                        } else {
                            Log.d("ProgramFetch", "No summary document found!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProgramFetch", "Error fetching summary document: ", e);
                    });
        }
    }





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

