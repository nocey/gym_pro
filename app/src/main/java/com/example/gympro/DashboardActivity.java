package com.example.gympro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
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
                                                        // Clear previous buttons or data
                                                        LinearLayout programLayout = findViewById(R.id.programLayout);
                                                        programLayout.removeAllViews();

                                                        // Initialize the WebView (hidden by default)
                                                        WebView youtubeWebView = findViewById(R.id.youtubeWebView);
                                                        youtubeWebView.setVisibility(View.GONE);  // Hide it initially

                                                        for (Map<String, String> exercise : exercises) {
                                                            String name = exercise.get("name");
                                                            String sets = exercise.get("sets");
                                                            String link = exercise.get("link");  // Get the link

                                                            // Create a TextView for the name and sets
                                                            TextView exerciseText = new TextView(DashboardActivity.this);
                                                            exerciseText.setText(name + " (" + sets + " sets)");
                                                            programLayout.addView(exerciseText);

                                                            // Create a Button for the link (YouTube)
                                                            Button videoButton = new Button(DashboardActivity.this);
                                                            videoButton.setText("Watch video");
                                                            videoButton.setOnClickListener(v -> {
                                                                // Open the video in a new activity
                                                                Intent intent = new Intent(DashboardActivity.this, VideoActivity.class);
                                                                intent.putExtra("VIDEO_URL", link);  // Pass the video URL
                                                                startActivity(intent);
                                                            });
                                                            programLayout.addView(videoButton);
                                                        }
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

    // This method loads the YouTube video in the WebView
    public void showYouTubeVideo(String link, WebView youtubeWebView) {
        // Extract the video ID from the link and construct the embed URL
        String videoId = link.substring(link.indexOf("v=") + 2, link.indexOf("v=") + 13);
        String embedUrl = "https://www.youtube.com/embed/" + videoId;

        // Show the WebView and load the video
        youtubeWebView.setVisibility(View.VISIBLE);
        youtubeWebView.getSettings().setJavaScriptEnabled(true);
        youtubeWebView.loadUrl(embedUrl);
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
