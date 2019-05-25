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
import com.bsecure.scsm_mobile.models.SyllabusModel;
import com.bsecure.scsm_mobile.models.TutorsModel;
import com.bsecure.scsm_mobile.recyclertouch.Extension;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class SyllabusListAdapter extends RecyclerView.Adapter<SyllabusListAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<SyllabusModel> syllabusModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;

    public SyllabusListAdapter(List<SyllabusModel> list, Context context, ContactAdapterListener listener) {
        this.context = context;
        this.syllabusModelList = list;
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
            contactViewHolder.tv_title.setText(classMode_lList.getLesson());
            contactViewHolder.section_tv.setText(classMode_lList.getDescription());

            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

            applyClickEvents(contactViewHolder, syllabusModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<SyllabusModel> classModelList, final int position) {
        if (contactViewHolder instanceof ItemSwipeWithActionWidthViewHolder) {
            ItemSwipeWithActionWidthViewHolder viewHolder = (ItemSwipeWithActionWidthViewHolder) contactViewHolder;
            viewHolder.mActionViewDelete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToEdit(position, classModelList);
                        }
                    }

            );
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swipe_syllabus, parent, false);

//        View view = LayoutInflater.from(context).inflate(R.layout.class_row, parent, false);
//        ContactViewHolder myHoder = new ContactViewHolder(view);
//        return myHoder;
//        View view = getLayoutInflater().inflate(R.layout.list_item_main, parent, false);
//        if (viewType == ITEM_TYPE_ACTION_WIDTH) return new ItemSwipeWithActionWidthViewHolder(view);
        //if (viewType == ITEM_TYPE_NO_SWIPE) return new ItemNoSwipeViewHolder(view);
        if (viewType == ITEM_TYPE_ACTION_WIDTH)
            return new ItemSwipeWithActionWidthViewHolder(itemView);

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
        public LinearLayout contact_user_ll;
        public RelativeLayout viewBackground, viewForeground;
        public View mViewContent;
        public View mActionContainer;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.les_name);
            section_tv = (TextView) v.findViewById(R.id.les_desc);
            //contact_user_ll = (LinearLayout) v.findViewById(R.id.contact_user_ll);
            mViewContent = itemView.findViewById(R.id.view_list_main_content);
            mActionContainer = itemView.findViewById(R.id.view_list_repo_action_container);
            v.setOnLongClickListener(this);
            // v.setOnLongClickListener(this);
//            viewBackground = v.findViewById(R.id.view_background);
//            viewForeground = v.findViewById(R.id.view_foreground);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface ContactAdapterListener {
        void swipeToEdit(int position, List<SyllabusModel> classModelList);

        void swipeToDelete(int position, List<SyllabusModel> classModelList);
    }

    class ItemSwipeWithActionWidthViewHolder extends ContactViewHolder implements Extension {

        public View mActionViewDelete;


        public ItemSwipeWithActionWidthViewHolder(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
        }

        @Override
        public float getActionWidth() {
            return mActionContainer.getWidth();
        }
    }
}
