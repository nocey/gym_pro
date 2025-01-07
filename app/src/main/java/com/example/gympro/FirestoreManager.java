package com.example.gympro;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreManager {
    private FirebaseFirestore db;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    public void addQuestionAndAnswers() {
        // Add question and answers to Firestore
        Map<String, Object> question = new HashMap<>();
        question.put("questionText", "What is your fitness goal?");

        db.collection("questions")
                .document("question1")
                .set(question)
                .addOnSuccessListener(aVoid -> addAnswersToQuestion("question1"))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error adding question", e));
    }

    private void addAnswersToQuestion(String questionId) {
        Map<String, Object> answer1 = new HashMap<>();
        answer1.put("answerText", "Build muscle");
        answer1.put("isSelected", false);

        Map<String, Object> answer2 = new HashMap<>();
        answer2.put("answerText", "Lose weight");
        answer2.put("isSelected", false);

        Map<String, Object> answer3 = new HashMap<>();
        answer3.put("answerText", "Improve endurance");
        answer3.put("isSelected", false);

        db.collection("questions")
                .document(questionId)
                .collection("answers")
                .add(answer1);

        db.collection("questions")
                .document(questionId)
                .collection("answers")
                .add(answer2);

        db.collection("questions")
                .document(questionId)
                .collection("answers")
                .add(answer3);
    }
}
