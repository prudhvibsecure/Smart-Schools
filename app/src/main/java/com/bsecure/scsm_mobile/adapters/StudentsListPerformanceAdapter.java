package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.models.StudentModel;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class StudentsListPerformanceAdapter extends RecyclerView.Adapter<StudentsListPerformanceAdapter.ViewHolder> {

    private Context context;

    private ArrayList<StudentModel> students;

    private LayoutInflater inflater;

    private ClickListener listener;

    public StudentsListPerformanceAdapter(Context context, ArrayList<StudentModel> students, ClickListener listener)
    {
        this.context = context;

        this.listener = listener;

        this.inflater = LayoutInflater.from(context);

        this.students = students;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.students_row_perf, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        viewHolder.name.setText(students.get(position).getStudent_name());

        viewHolder.roll_no.setText(students.get(position).getRoll_no());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnRowClicked(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView roll_no;


        public ViewHolder(@NonNull View v) {
            super(v);

            name = v.findViewById(R.id.name);
            roll_no = v.findViewById(R.id.roll_no);

        }
    }
}
