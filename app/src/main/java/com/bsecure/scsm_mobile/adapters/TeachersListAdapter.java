package com.bsecure.scsm_mobile.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.controls.ColorGenerator;
import com.bsecure.scsm_mobile.controls.TextDrawable;
import com.bsecure.scsm_mobile.models.TeacherModel;
import com.bsecure.scsm_mobile.modules.ViewGallery;

import java.util.ArrayList;

public class TeachersListAdapter extends RecyclerView.Adapter<TeachersListAdapter.ViewHolder> {

    Context context;
    ArrayList<TeacherModel>teachers;
    LayoutInflater inflater;
    ClickListener listener;

    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public TeachersListAdapter(Context context, ArrayList<TeacherModel>teachers, ClickListener listener)
    {
        this.context = context;
        this.teachers = teachers;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public TeachersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.teacher_row, viewGroup, false);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeachersListAdapter.ViewHolder viewHolder, final int position) {

        TeacherModel model = teachers.get(position);
        viewHolder.teacher_name.setText(model.getTeacher_name());
        String sub = model.getSubjects();
        viewHolder.subject.setText(sub);

        int color = generator.getColor(model.getTeacher_name());
        TextDrawable ic1 = builder.build(model.getTeacher_name().substring(0, 1), color);
        viewHolder.image.setImageDrawable(ic1);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnRowClicked(position,v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView teacher_name, subject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.img_cls);
            teacher_name = itemView.findViewById(R.id.cl_name);
            subject = itemView.findViewById(R.id.subject);
        }
    }
}
