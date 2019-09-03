package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.models.CalenderModel;
import com.bsecure.scsm_mobile.utils.ContactUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.ViewHolder> {

    Context context;
    List<CalenderModel> calender;
    List<CalenderModel>arraylist;
    LayoutInflater inflator;

    public CalenderAdapter(Context context, List<CalenderModel>calender)
    {
        this.context = context;
        this.calender = calender;
        this.arraylist = new ArrayList<>();
        //arraylist.addAll(calender);
        inflator = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = inflator.inflate(R.layout.calender_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalenderAdapter.ViewHolder viewHolder, int position) {

        viewHolder.name.setText(calender.get(position).getOccassion());
        viewHolder.fromdate.setText(getTimeAgolatest(Long.parseLong(calender.get(position).getFromdate())));
        if(!calender.get(position).getTodate().equals("")) {
            viewHolder.todate.setText(getTimeAgolatest(Long.parseLong(calender.get(position).getTodate())));
        }
        else
        {
            viewHolder.todate.setText("");
        }

    }

    public static String getTimeAgolatest(long time) {

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(time*1000);

        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Today";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format("dd-MMM-yyyy", smsTime).toString();
        } else {
            return DateFormat.format("dd-MMM-yyyy", smsTime).toString();
        }

    }
    @Override
    public int getItemCount() {
        return calender.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, fromdate, todate;
        LinearLayout vv_mm;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            fromdate = itemView.findViewById(R.id.from);
            todate = itemView.findViewById(R.id.to);
            vv_mm = itemView.findViewById(R.id.vv_mm);
        }
    }
}
