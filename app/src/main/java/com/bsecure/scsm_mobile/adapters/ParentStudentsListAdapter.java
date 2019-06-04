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

public class ParentStudentsListAdapter extends RecyclerView.Adapter<ParentStudentsListAdapter.ContactViewHolder> {

    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<StudentModel> classModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public ParentStudentsListAdapter(List<StudentModel> list, Context context, ContactAdapterListener listener) {
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
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {

        try {
            StudentModel classMode_lList = classModelList.get(position);
            contactViewHolder.tv_title.setText(classMode_lList.getStudent_name());
            contactViewHolder.section_tv.setText(classMode_lList.getClass_name()+ "\t-\t" + classMode_lList.getSection());
            contactViewHolder.section_n.setVisibility(View.GONE);
           // contactViewHolder.section_n.setText(classMode_lList.getSection());

            int color = generator.getColor(classMode_lList.getStudent_name());
            TextDrawable ic1 = builder.build(classMode_lList.getStudent_name().substring(0, 1), color);

            contactViewHolder.imgProfile.setImageDrawable(ic1);

            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

            applyClickEvents(contactViewHolder, classModelList, position);
            applyEvents(contactViewHolder, classModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void applyEvents(ContactViewHolder contactViewHolder, final List<StudentModel> classModelList, final int position) {

        if (contactViewHolder instanceof ItemSwipeWithActionWidthViewHolder) {
            ItemSwipeWithActionWidthViewHolder viewHolder = (ItemSwipeWithActionWidthViewHolder) contactViewHolder;
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
            viewHolder.view_list_repo_action_per.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToprofomance(position, classModelList);

                        }
                    }

            );
        }
    }

    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<StudentModel> classModelList, final int position) {
        contactViewHolder.mViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageRowClicked(classModelList, position);
                } catch (Exception e) {

                }
            }
        });
        contactViewHolder.periods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageTimeTable(position, classModelList);
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

        public TextView tv_title;
        public TextView periods;
        public TextView section_tv;
        public TextView section_n;
        public ImageView imgProfile;
        public LinearLayout contact_user_ll;
        public RelativeLayout viewBackground, viewForeground;
        public View mViewContent;
        public View mActionContainer;
        public View view_list_repo_action_per;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.cl_name);
            section_tv = (TextView) v.findViewById(R.id.section_tv);
            section_n = (TextView) v.findViewById(R.id.section_n);
            periods = (TextView) v.findViewById(R.id.periods);
            periods.setVisibility(View.GONE);
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
        void onMessageRowClicked(List<StudentModel> matchesList, int position);

        void swipeToSyllabus(int position, List<StudentModel> classModelList);

        void swipeToMarks(int position, List<StudentModel> classModelList);

        void swipeToAttendence(int position, List<StudentModel> classModelList);

        void swipeToprofomance(int position, List<StudentModel> classModelList);

        void onMessageTimeTable(int position, List<StudentModel> classModelList);
    }

    class ItemSwipeWithActionWidthViewHolder extends ContactViewHolder implements Extension {

        public View mActionViewDelete;
        public View mActionViewEdit;
        public View mActionViewstatus;
        public View view_list_repo_action_per;


        public ItemSwipeWithActionWidthViewHolder(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
            mActionViewstatus = itemView.findViewById(R.id.view_list_repo_action_status);
            //mActionViewstatus.setVisibility(View.GONE);
            mActionViewEdit = itemView.findViewById(R.id.view_list_repo_action_update);
            view_list_repo_action_per = itemView.findViewById(R.id.view_list_repo_action_per);
        }

        @Override
        public float getActionWidth() {
            return mActionContainer.getWidth();
        }
    }

}
