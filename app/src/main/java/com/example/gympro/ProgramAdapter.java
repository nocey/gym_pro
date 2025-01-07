package com.example.gympro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder> {
    private Context context;
    private List<Question> questionsList;

    public ProgramAdapter(Context context, List<Question> questionsList) {
        this.context = context;
        this.questionsList = questionsList;
    }

    @Override
    public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.question_item, parent, false);
        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProgramViewHolder holder, int position) {
        Question question = questionsList.get(position);
        holder.questionText.setText(question.getQuestionText());

        // Populate answers in the radio buttons
        holder.radioButton1.setText(question.getAnswers()[0]);
        holder.radioButton2.setText(question.getAnswers()[1]);
        holder.radioButton3.setText(question.getAnswers()[2]);

        // Listen for answer selection
        holder.radioButton1.setOnClickListener(v -> question.setSelectedAnswer(question.getAnswers()[0]));
        holder.radioButton2.setOnClickListener(v -> question.setSelectedAnswer(question.getAnswers()[1]));
        holder.radioButton3.setOnClickListener(v -> question.setSelectedAnswer(question.getAnswers()[2]));
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public static class ProgramViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        RadioButton radioButton1, radioButton2, radioButton3;

        public ProgramViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            radioButton1 = itemView.findViewById(R.id.radioButton1);
            radioButton2 = itemView.findViewById(R.id.radioButton2);
            radioButton3 = itemView.findViewById(R.id.radioButton3);
        }
    }
}
