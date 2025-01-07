package com.example.gympro;

public class Question {
    private String questionText;
    private String[] answers; // Array of 3 answers

    // Constructor to initialize the question and answers
    public Question(String questionText, String[] answers) {
        this.questionText = questionText;
        this.answers = answers;
    }

    // Getter for the question text
    public String getQuestionText() {
        return questionText;
    }

    // Getter for the answers
    public String[] getAnswers() {
        return answers;
    }
}
