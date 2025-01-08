package com.example.gympro;

public class Question {
    private String questionText;
    private Answer[] answers;  // Change from single Answer to an array of Answer
    private Answer selectedAnswer;

    public Question(String questionText, Answer[] answers) {
        this.questionText = questionText;
        this.answers = answers;  // Initialize answers
    }

    public String getQuestionText() {
        return questionText;
    }

    public Answer[] getAnswers() {  // Add this method to return all answers for the question
        return answers;
    }

    public Answer getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(Answer selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
}
