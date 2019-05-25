package com.bsecure.scsm_mobile.graphs.graphadapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.ClassListAdapter;
import com.bsecure.scsm_mobile.models.ClassModel;
import com.bsecure.scsm_mobile.models.MarksModel;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class SubWisePercentageAdapter extends RecyclerView.Adapter<SubWisePercentageAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;
    private ContactAdapterListener listener;
    private Context context = null;
    private View.OnClickListener onClickListener;
    int selectedPosition = -1;
    private List<MarksModel> tutorsModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private int selectedPos = 0;

    public SubWisePercentageAdapter(List<MarksModel> list, Context context, ContactAdapterListener listener) {
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
            MarksModel classMode_lList = tutorsModelList.get(position);
            contactViewHolder.tv_title.setText(Html.fromHtml("<u>" + classMode_lList.getSubject() + "</u>"));
            if (position == selectedPos) {
                contactViewHolder.tv_title.setBackgroundColor(Color.parseColor("#DF013A"));
                listener.onRowCTouch(tutorsModelList, position);
            } else {
                contactViewHolder.tv_title.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            applyClickEvents(contactViewHolder, tutorsModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void applyClickEvents(final ContactViewHolder contactViewHolder, final List<MarksModel> classModelList, final int position) {
        contactViewHolder.tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    selectedPos = position;
                    listener.onRowCTouch(classModelList, position);
                    notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        });

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sub_click_view, parent, false);

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
    public interface ContactAdapterListener {
        void onRowCTouch(List<MarksModel> matchesList, int position);

    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView tv_title;
        public TextView section_tv;
        public TextView grade;
        public TextView marks;
        public TextView percent;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.sub);

        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


}
