package com.example.gympro;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> answers;
    private String selectedAnswer;

    public Question(String questionText, List<String> answers) {
        this.questionText = questionText;
        this.answers = answers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setSelectedAnswer(String answer) {
        this.selectedAnswer = answer;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }
}
