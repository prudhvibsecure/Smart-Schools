package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.models.MarksModel;
import com.bsecure.scsm_mobile.modules.ViewAttandence;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class StudentMarkViewListAdapter extends RecyclerView.Adapter<StudentMarkViewListAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;

    private List<MarksModel> tutorsModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;

    public StudentMarkViewListAdapter(List<MarksModel> list, Context context) {
        this.context = context;
        this.tutorsModelList = list;
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
            MarksModel classMode_lList = tutorsModelList.get(position);
            contactViewHolder.tv_title.setText(classMode_lList.getSubject());
            contactViewHolder.section_tv.setText(classMode_lList.getTotal_marks());

//            if (TextUtils.isEmpty(classMode_lList.getMarks())) {
                contactViewHolder.rank.setText(classMode_lList.getRank());
//            } else {
                contactViewHolder.marks.setText(classMode_lList.getMarks());
//            }


            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_marks_row_iew, parent, false);

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
        public TextView rank;
        public TextView marks;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.subject);
            section_tv = (TextView) v.findViewById(R.id.t_marks);
            marks = (TextView) v.findViewById(R.id.marks);
            rank = (TextView) v.findViewById(R.id.rank);
//            rank.setVisibility(View.GONE);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


}
