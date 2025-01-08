package com.example.gympro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private List<String> daysOfWeek;
    private Context context;

    public DayAdapter(List<String> daysOfWeek, Context context) {
        this.daysOfWeek = daysOfWeek;
        this.context = context;
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for the individual day item
        View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        String day = daysOfWeek.get(position);
        holder.dayTextView.setText(day);

        // Set an onClickListener to fetch the program when a day is clicked
        holder.itemView.setOnClickListener(v -> {
            // Make sure the context is an instance of DashboardActivity
            if (context instanceof DashboardActivity) {
                ((DashboardActivity) context).fetchProgramForDay(day);
            }
        });
    }

    @Override
    public int getItemCount() {
        return daysOfWeek.size();
    }

    public class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;

        public DayViewHolder(View itemView) {
            super(itemView);
            // Find the TextView that will display the day
            dayTextView = itemView.findViewById(R.id.dayTextView); // Ensure this ID exists in your item_day layout
        }
    }
}
