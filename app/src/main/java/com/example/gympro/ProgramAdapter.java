package com.example.gympro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gympro.R;
import java.util.ArrayList;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder> {

    private ArrayList<Question> questionList;

    public ProgramAdapter(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new ProgramViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.questionText.setText(question.getQuestionText());

        // Cevaplar için RadioGroup oluştur
        Answer[] answers = question.getAnswers();  // Sorular için tüm cevapları çeker
        holder.radioGroup.removeAllViews();
        for (int i = 0; i < answers.length; i++) {
            Answer answer = answers[i];
            RadioButton radioButton = new RadioButton(holder.itemView.getContext());
            radioButton.setText(answer.getAnswerText());
            radioButton.setOnClickListener(v -> question.setSelectedAnswer(answer));
            holder.radioGroup.addView(radioButton);
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class ProgramViewHolder extends RecyclerView.ViewHolder {

        private TextView questionText;
        private RadioGroup radioGroup;

        public ProgramViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            radioGroup = itemView.findViewById(R.id.radioGroup);
        }
    }
}
