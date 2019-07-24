package com.bsecure.scsm_mobile.adapters;

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
import com.bsecure.scsm_mobile.models.ClassModel;

import java.util.ArrayList;

public class AdminClassListAdapter extends RecyclerView.Adapter<AdminClassListAdapter.ViewHolder> {

    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    ClickListener listener;

    public ArrayList<ClassModel>classList;
    Context context;
    LayoutInflater inflater;

    public AdminClassListAdapter(Context context, ArrayList<ClassModel>classList, ClickListener listener)
    {
        this.context = context;
        this.classList = classList;
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.admin_class_row, viewGroup, false);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        ClassModel model = classList.get(position);
        int color = generator.getColor(model.getClsName());
        TextDrawable ic1 = builder.build(model.getClsName().substring(0, 1), color);
        viewHolder.image.setImageDrawable(ic1);

        viewHolder.classname.setText(model.getClsName());
        viewHolder.section.setText(model.getSectionName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnRowClicked(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView classname, section;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

           image = itemView.findViewById(R.id.img_cls);
           classname = itemView.findViewById(R.id.cl_name);
           section = itemView.findViewById(R.id.section_n);

        }
    }
}
