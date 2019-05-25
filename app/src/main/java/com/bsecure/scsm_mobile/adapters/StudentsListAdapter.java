package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.bsecure.scsm_mobile.ClickListener;
import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.models.StudentModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.ViewHolder> {
    Context mcontext;
    List<StudentModel>students;
    List<StudentModel>arraylist;
    LayoutInflater inflator;
    ClickListener listener;

    public StudentsListAdapter(Context context, List<StudentModel> students, ClickListener listener)
    {
        this.mcontext = context;
        this.students = students;
        this.arraylist = new ArrayList<>();
        arraylist.addAll(students);
        inflator = LayoutInflater.from(mcontext);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = inflator.inflate(R.layout.student, null, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.name.setText(students.get(i).getStudent_name());
        viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.OnClick(i, b);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CheckBox check;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            check = itemView.findViewById(R.id.check);
        }
    }
}
