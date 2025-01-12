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

        // firebase tanımlamaları
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //  Toolbar tanımla
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Günleri gösteren RecyclerView tanımla
        dayRecyclerView = findViewById(R.id.dayRecyclerView);

        // Programı gösteren TextView tanımla
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        DayAdapter adapter = new DayAdapter(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"), this, this::fetchProgramForDay);
        dayRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    // Menüdeki eşleşen öğeleri işle
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

    // Seçilen güne göre programı çek
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

                                                    // Vebviewi temizle
                                                    WebView youtubeWebView = findViewById(R.id.youtubeWebView);
                                                    youtubeWebView.setVisibility(View.GONE);

                                                    if (exercises != null && !exercises.isEmpty()) {
                                                        // Çalışma programını göster
                                                        for (Map<String, String> exercise : exercises) {
                                                            String name = exercise.get("name");
                                                            String sets = exercise.get("sets");
                                                            String link = exercise.get("link");

                                                            // Textview komponenti oluştur
                                                            TextView exerciseText = new TextView(DashboardActivity.this);
                                                            exerciseText.setText(name + " (" + sets + " sets)");

                                                            // Arka plan ve yazı rengini ayarla
                                                            exerciseText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                            exerciseText.setTextColor(getResources().getColor(R.color.colorSecondary));

                                                            // Yazıyı ortala
                                                            exerciseText.setGravity(Gravity.CENTER);

                                                            // Yazı boyutunu ayarla
                                                            exerciseText.setTextSize(20);

                                                            // ortaya margin ekler
                                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                            params.setMargins(0, 20, 0, 20); // margin ekle (üst, sol, alt, sağ)
                                                            exerciseText.setLayoutParams(params);

                                                            programLayout.addView(exerciseText);

                                                            // Egzersizin video linki var ise tıklama eventi ekle
                                                            if (link != null && !link.isEmpty()) {
                                                                TextView watchVideoButton = new TextView(DashboardActivity.this);
                                                                watchVideoButton.setText("Watch Video");

                                                                // Arka plan rengini colorSecondary yap
                                                                watchVideoButton.setBackgroundColor(getResources().getColor(R.color.colorSecondary)); // Secondary color for background
                                                                watchVideoButton.setTextColor(getResources().getColor(R.color.colorPrimary)); // Primary color for text

                                                                // Yazıyı ortala
                                                                watchVideoButton.setGravity(Gravity.CENTER);

                                                                // Yazı boyutunu ayarla
                                                                watchVideoButton.setTextSize(24); // Adjust size to make it bigger than exercise text

                                                                // Padding ekle
                                                                watchVideoButton.setPadding(0, 10, 0, 10);

                                                                // Tıklama eventi ekler
                                                                watchVideoButton.setOnClickListener(v -> {
                                                                    // Yeni bir aktivite içinde videoyu oynat
                                                                    Intent intent = new Intent(DashboardActivity.this, VideoActivity.class);
                                                                    intent.putExtra("VIDEO_URL", link);  // Video linkini gönder
                                                                    startActivity(intent);
                                                                });

                                                                // Egzersiz yazısının altına "Video İzle" butonunu ekle
                                                                programLayout.addView(watchVideoButton);
                                                            }
                                                        }
                                                    }

                                                } else {
                                                    // Dinlenme günü
                                                    TextView restDayText = new TextView(DashboardActivity.this);
                                                    //  Arka plan rengini colorPrimary yap
                                                    restDayText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                    restDayText.setTextColor(getResources().getColor(R.color.colorSecondary));

                                                    restDayText.setGravity(Gravity.CENTER);

                                                    restDayText.setTextSize(20); // Yazı boyutunu ayarla
                                                    restDayText.setText("Rest day");
                                                    programLayout.addView(restDayText);
                                                }
                                            } else {
                                                // Dinlenme günü
                                                TextView restDayText = new TextView(DashboardActivity.this);
                                                //  Arka plan rengini colorPrimary yap
                                                restDayText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                restDayText.setTextColor(getResources().getColor(R.color.colorSecondary));

                                                restDayText.setGravity(Gravity.CENTER);

                                                restDayText.setTextSize(20); // Yazı boyutunu ayarla
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
                return "seven_day";
        }
    }
}

