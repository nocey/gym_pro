package com.example.gympro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private List<String> days;
    private Context context;
    private OnDayClickListener onDayClickListener;

    public DayAdapter(List<String> days, Context context, OnDayClickListener onDayClickListener) {
        this.days = days;
        this.context = context;
        this.onDayClickListener = onDayClickListener;
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        String day = days.get(position);
        holder.dayTextView.setText(day);

        // Set the click listener to pass the selected day to the listener
        holder.itemView.setOnClickListener(v -> onDayClickListener.onDayClick(day));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnDayClickListener {
        void onDayClick(String day);
    }

    public class DayViewHolder extends RecyclerView.ViewHolder {

        TextView dayTextView;

        public DayViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}
