package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.controls.ColorGenerator;
import com.bsecure.scsm_mobile.controls.TextDrawable;
import com.bsecure.scsm_mobile.models.ClassModel;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.recyclertouch.Extension;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ContactViewHolder> {

    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<ClassModel> classModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public ClassListAdapter(List<ClassModel> list, Context context, ContactAdapterListener listener) {
        this.context = context;
        this.classModelList = list;
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
            if (classModelList.size() == 0) {
                arr = 0;

            } else {
                arr = classModelList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, final int position) {

        try {
            ClassModel classMode_lList = classModelList.get(position);
            String subj=classMode_lList.getSubjects();
            String newNames = subj.replace(",", ", ");
            contactViewHolder.tv_title.setText(classMode_lList.getClsName());
            contactViewHolder.section_tv.setText(newNames);
            contactViewHolder.section_n.setText(classMode_lList.getSectionName());
//            contactViewHolder.periods.setVisibility(View.VISIBLE);
//            contactViewHolder.periods.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onMessageTimeTable(position, classModelList);
//                }
//            });
            int color = generator.getColor(classMode_lList.getClsName());
            TextDrawable ic1 = builder.build(classMode_lList.getClsName().substring(0, 1), color);

            contactViewHolder.imgProfile.setImageDrawable(ic1);

            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

            applyClickEvents(contactViewHolder, classModelList, position);
            applyEvents(contactViewHolder, classModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void applyEvents(ContactViewHolder contactViewHolder, final List<ClassModel> classModelList, final int position) {

        if (contactViewHolder instanceof ItemSwipeWithActionWidthViewHolder) {
            final ItemSwipeWithActionWidthViewHolder viewHolder = (ItemSwipeWithActionWidthViewHolder) contactViewHolder;
            viewHolder.mActionViewDelete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToAttendence(position, classModelList);
                        }
                    }

            );
            viewHolder.mActionViewstatus.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToMarks(position, classModelList);

                        }
                    }

            );
            viewHolder.mActionViewEdit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToSyllabus(position, classModelList);

                        }
                    }

            );
            viewHolder.view_list_repo_action_more.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToMore(position, classModelList,viewHolder.view_list_repo_action_more);

                        }
                    }

            );
        }
    }

    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<ClassModel> classModelList, final int position) {
        contactViewHolder.mViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageRowClicked(classModelList, position);
                } catch (Exception e) {

                }
            }
        });

    }

    @Override
    public int getItemViewType(int position) {

        return ITEM_TYPE_ACTION_WIDTH;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_main_ncs, parent, false);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
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
        final int size = classModelList.size();
        classModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void removeItem(int position) {
        classModelList.remove(position);
        notifyItemRemoved(position);
    }
//
//    public void release() {
//        array = null;
//    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView periods;
        public TextView tv_title;
        public TextView section_tv;
        public TextView section_n;
        public ImageView imgProfile;
        public LinearLayout contact_user_ll;
        public RelativeLayout viewBackground, viewForeground;
        public View mViewContent;
        public View mActionContainer;

        public ContactViewHolder(View v) {
            super(v);

            periods = v.findViewById(R.id.periods);
            periods.setVisibility(View.GONE);
            tv_title = (TextView) v.findViewById(R.id.cl_name);
            section_tv = (TextView) v.findViewById(R.id.section_tv);
            section_n = (TextView) v.findViewById(R.id.section_n);
            imgProfile = (ImageView) v.findViewById(R.id.img_cls);
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
        void onMessageRowClicked(List<ClassModel> matchesList, int position);

        void swipeToSyllabus(int position, List<ClassModel> classModelList);

        void swipeToMarks(int position, List<ClassModel> classModelList);

        void swipeToAttendence(int position, List<ClassModel> classModelList);

        void onMessageTimeTable(int position, List<ClassModel> classModelList);

        void swipeToMore(int position, List<ClassModel> classModelList, View view_list_repo_action_more);
    }

    class ItemSwipeWithActionWidthViewHolder extends ContactViewHolder implements Extension {

        public View mActionViewDelete;
        public View mActionViewEdit;
        public View mActionViewstatus;
        public View mActionViewsper;
        public View view_list_repo_action_more;

        public ItemSwipeWithActionWidthViewHolder(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
            mActionViewstatus = itemView.findViewById(R.id.view_list_repo_action_status);
            mActionViewEdit = itemView.findViewById(R.id.view_list_repo_action_update);
            mActionViewsper = itemView.findViewById(R.id.view_list_repo_action_per);
            view_list_repo_action_more = itemView.findViewById(R.id.view_list_repo_action_more);
            mActionViewsper.setVisibility(View.GONE);
        }

        @Override
        public float getActionWidth() {
            return mActionContainer.getWidth();
        }
    }
    public void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }
}
