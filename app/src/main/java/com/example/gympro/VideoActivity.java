package com.example.gympro;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private WebView youtubeWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // Initialize the WebView
        youtubeWebView = findViewById(R.id.youtubeWebView);

        // Get the video URL passed from DashboardActivity
        String videoUrl = getIntent().getStringExtra("VIDEO_URL");

        // Set up the WebView to show the YouTube video
        if (videoUrl != null) {
            String videoId = videoUrl.substring(videoUrl.indexOf("v=") + 2, videoUrl.indexOf("v=") + 13);
            String embedUrl = "https://www.youtube.com/embed/" + videoId;
            youtubeWebView.getSettings().setJavaScriptEnabled(true);
            youtubeWebView.loadUrl(embedUrl);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();  // Close this activity and return to DashboardActivity
    }
}
