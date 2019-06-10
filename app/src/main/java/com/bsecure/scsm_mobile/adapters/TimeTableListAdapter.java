package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.models.Periods;
import com.bsecure.scsm_mobile.models.Subjects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class TimeTableListAdapter extends RecyclerView.Adapter<TimeTableListAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;
    private List<Periods> tutorsModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;

    public TimeTableListAdapter(List<Periods> list, Context context, ContactAdapterListener listener) {
        this.context = context;
        this.tutorsModelList = list;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //    @Override
//    public int getItemCount() {
//        return array.length();
//    }
    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (tutorsModelList.size() == 0) {
                arr = 0;

            } else {
                arr = tutorsModelList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {

        try {
            Periods classMode_lList = tutorsModelList.get(position);
            contactViewHolder.number.setText(classMode_lList.getPeriod_num());
            contactViewHolder.from.setText(classMode_lList.getFrom_time());
            contactViewHolder.to.setText(classMode_lList.getTo_time());
            contactViewHolder.name.setText(classMode_lList.getPeriod_name());

            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));
            //applyClickEvents(contactViewHolder, tutorsModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getDate(long timeStamp) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "date";
        }
    }

   /* private void applyClickEvents(ContactViewHolder contactViewHolder, final List<Periods> matchesList, final int position) {
        contactViewHolder.check_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onRowClicked(matchesList, position);
                } catch (Exception e) {

                }
            }
        });

    }*/

    public interface ContactAdapterListener {

        void onRowClicked(List<Periods> matchesList, int position);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.periods_list, parent, false);

        return new ContactViewHolder(itemView);
    }


    public void clear() {
        // matchesList=null;
        final int size = tutorsModelList.size();
        tutorsModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void removeItem(int position) {
        tutorsModelList.remove(position);
        notifyItemRemoved(position);
    }
//
//    public void release() {
//        array = null;
//    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView number;
        public TextView from;
        public TextView to;
        public TextView name;


        public ContactViewHolder(View v) {
            super(v);

            number = (TextView) v.findViewById(R.id.num);
            from = (TextView) v.findViewById(R.id.from);
            to = (TextView) v.findViewById(R.id.to);
            name = (TextView) v.findViewById(R.id.name);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


}
