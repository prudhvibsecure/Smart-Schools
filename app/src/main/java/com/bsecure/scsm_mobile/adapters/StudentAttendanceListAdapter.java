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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.models.TutorsModel;
import com.bsecure.scsm_mobile.modules.ViewAttandence;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class StudentAttendanceListAdapter extends RecyclerView.Adapter<StudentAttendanceListAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;

    private List<ViewAttandence> tutorsModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private ClickListener listener;

    public StudentAttendanceListAdapter(List<ViewAttandence> list, Context context, ClickListener listener) {
        this.context = context;
        this.tutorsModelList = list;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        this.listener = listener;
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
    public void onBindViewHolder(ContactViewHolder contactViewHolder, final int position) {

        try {
            ViewAttandence classMode_lList = tutorsModelList.get(position);
            contactViewHolder.tv_title.setText(classMode_lList.getMonth() + "-" + classMode_lList.getYear());
            contactViewHolder.section_tv.setText(classMode_lList.getNo_of_absents() + "/" + classMode_lList.getNo_working_days());
            contactViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnRowClicked(position, v);
                }
            });
            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_att_row, parent, false);

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

        public TextView tv_title;
        public TextView section_tv;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.years);
            section_tv = (TextView) v.findViewById(R.id.years_att);

        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


}
