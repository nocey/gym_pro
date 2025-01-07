package com.example.gympro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.QuestionViewHolder> {

    private List<Question> questionList;

    public ProgramAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for individual question items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {
        // Get the current question from the list
        Question question = questionList.get(position);

        // Set the question text to the TextView
        holder.questionTextView.setText(question.getQuestionText());

        // Clear previous selections (if any)
        holder.answerRadioGroup.clearCheck();

        // Get the list of answers for this question
        List<String> answers = question.getAnswers();

        // Set each RadioButton's text with the corresponding answer
        holder.radioButton1.setText(answers.get(0));
        holder.radioButton2.setText(answers.get(1));
        holder.radioButton3.setText(answers.get(2));

        // Set a listener to handle answer selection
        holder.answerRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Use if-else instead of switch
            if (checkedId == R.id.radioButton1) {
                question.setSelectedAnswer(answers.get(0));
            } else if (checkedId == R.id.radioButton2) {
                question.setSelectedAnswer(answers.get(1));
            } else if (checkedId == R.id.radioButton3) {
                question.setSelectedAnswer(answers.get(2));
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of questions
        return questionList.size();
    }

    // ViewHolder class to hold the views for each question item
    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        RadioGroup answerRadioGroup;
        RadioButton radioButton1, radioButton2, radioButton3;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            questionTextView = itemView.findViewById(R.id.questionTextView);
            answerRadioGroup = itemView.findViewById(R.id.answerRadioGroup);
            radioButton1 = itemView.findViewById(R.id.radioButton1);
            radioButton2 = itemView.findViewById(R.id.radioButton2);
            radioButton3 = itemView.findViewById(R.id.radioButton3);
        }
    }
}
