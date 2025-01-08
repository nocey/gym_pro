package com.example.gympro;

public class Answer {
    private String answerText;
    private int strength;
    private int cardio;
    private int dayNumber;  // Added field for day number

    public Answer(String answerText, int strength, int cardio, int dayNumber) {
        this.answerText = answerText;
        this.strength = strength;
        this.cardio = cardio;
        this.dayNumber = dayNumber;  // Initialize day number
    }

    public String getAnswerText() {
        return answerText;
    }

    public int getStrength() {
        return strength;
    }

    public int getCardio() {
        return cardio;
    }

    public int getDayNumber() {
        return dayNumber;  // Getter for day number
    }
}
