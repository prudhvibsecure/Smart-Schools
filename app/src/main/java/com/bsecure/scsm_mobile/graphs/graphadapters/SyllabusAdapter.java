package com.bsecure.scsm_mobile.graphs.graphadapters;

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
import com.bsecure.scsm_mobile.models.SyllabusModel;
import com.bsecure.scsm_mobile.recyclertouch.Extension;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class SyllabusAdapter extends RecyclerView.Adapter<SyllabusAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;

    private List<SyllabusModel> syllabusModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;

    public SyllabusAdapter(List<SyllabusModel> list, Context context) {
        this.context = context;
        this.syllabusModelList = list;
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
            if (syllabusModelList.size() == 0) {
                arr = 0;

            } else {
                arr = syllabusModelList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {

        try {
            SyllabusModel classMode_lList = syllabusModelList.get(position);
            contactViewHolder.tv_title.setText(classMode_lList.getSubject());
            contactViewHolder.section_tv.setText(classMode_lList.getLesson());
            contactViewHolder.dec.setText(classMode_lList.getDescription());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.syllabus_show, parent, false);


        return new ContactViewHolder(itemView);
    }


    public void clear() {
        // matchesList=null;
        final int size = syllabusModelList.size();
        syllabusModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemViewType(int position) {

        return ITEM_TYPE_ACTION_WIDTH;
    }

    public void removeItem(int position) {
        syllabusModelList.remove(position);
        notifyItemRemoved(position);
    }
//
//    public void release() {
//        array = null;
//    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView tv_title;
        public TextView section_tv;
        public TextView dec;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.sub_tv);
            tv_title.setVisibility(View.GONE);
            section_tv = (TextView) v.findViewById(R.id.sub_les);
            dec = (TextView) v.findViewById(R.id.sub_desc);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


}
