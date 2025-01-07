package com.example.gympro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ViewHolder> {

    private final ArrayList<Question> questions;
    private final ArrayList<Integer> selectedAnswers; // Track selected answers for each question

    public ProgramAdapter(ArrayList<Question> questions) {
        this.questions = questions;
        this.selectedAnswers = new ArrayList<>(questions.size()); // Initialize the list for tracking answers
        // Initialize selected answers with -1 (indicating no answer selected initially)
        for (int i = 0; i < questions.size(); i++) {
            selectedAnswers.add(-1);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a custom layout for each question and its answers
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current question
        Question question = questions.get(position);
        holder.questionTextView.setText(question.getQuestionText());

        // Get the 3 answers for the current question
        String[] answers = question.getAnswers();
        holder.answer1TextView.setText(answers[0]);
        holder.answer2TextView.setText(answers[1]);
        holder.answer3TextView.setText(answers[2]);

        // Set the background color for the selected answer
        int selectedAnswer = selectedAnswers.get(position);
        setAnswerSelected(holder, selectedAnswer);

        // Set onClickListener to select an answer
        holder.answer1TextView.setOnClickListener(v -> {
            selectedAnswers.set(position, 0);  // Mark answer 1 as selected
            notifyItemChanged(position); // Refresh the question view
        });
        holder.answer2TextView.setOnClickListener(v -> {
            selectedAnswers.set(position, 1);  // Mark answer 2 as selected
            notifyItemChanged(position); // Refresh the question view
        });
        holder.answer3TextView.setOnClickListener(v -> {
            selectedAnswers.set(position, 2);  // Mark answer 3 as selected
            notifyItemChanged(position); // Refresh the question view
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    // Method to set the background color for the selected answer
    private void setAnswerSelected(ViewHolder holder, int selectedAnswer) {
        if (selectedAnswer == 0) {
            holder.answer1TextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
            holder.answer2TextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
            holder.answer3TextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
        } else if (selectedAnswer == 1) {
            holder.answer1TextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
            holder.answer2TextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
            holder.answer3TextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
        } else if (selectedAnswer == 2) {
            holder.answer1TextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
            holder.answer2TextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
            holder.answer3TextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView answer1TextView;
        TextView answer2TextView;
        TextView answer3TextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            answer1TextView = itemView.findViewById(R.id.answer1TextView);
            answer2TextView = itemView.findViewById(R.id.answer2TextView);
            answer3TextView = itemView.findViewById(R.id.answer3TextView);
        }
    }
}
