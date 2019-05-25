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
import com.bsecure.scsm_mobile.models.Exams;
import com.bsecure.scsm_mobile.models.TutorsModel;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class ExamsListAdapter extends RecyclerView.Adapter<ExamsListAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;
    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<Exams> tutorsModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;

    public ExamsListAdapter(List<Exams> list, Context context, ContactAdapterListener listener) {
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
            Exams classMode_lList = tutorsModelList.get(position);
            contactViewHolder.tv_title.setText(classMode_lList.getExam_name());
            contactViewHolder.section_tv.setVisibility(View.GONE);
            int color = generator.getColor(classMode_lList.getExam_name());
            TextDrawable ic1 = builder.build(classMode_lList.getExam_name().substring(0, 2), color);

            contactViewHolder.imgProfile.setImageDrawable(ic1);
            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

            applyClickEvents(contactViewHolder, tutorsModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<Exams> classModelList, final int position) {
        contactViewHolder.mViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onExamRowClicked(classModelList, position);
                } catch (Exception e) {

                }
            }
        });

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_main, parent, false);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
//        View view = LayoutInflater.from(context).inflate(R.layout.class_row, parent, false);
//        ContactViewHolder myHoder = new ContactViewHolder(view);
//        return myHoder;
//        View view = getLayoutInflater().inflate(R.layout.list_item_main, parent, false);
//        if (viewType == ITEM_TYPE_ACTION_WIDTH) return new ItemSwipeWithActionWidthViewHolder(view);
        //if (viewType == ITEM_TYPE_NO_SWIPE) return new ItemNoSwipeViewHolder(view);
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
        public TextView section_n;
        public ImageView imgProfile;
        public LinearLayout contact_user_ll;
        public RelativeLayout viewBackground, viewForeground;
        public View mViewContent;
        public View mActionContainer;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.cl_name);
            section_tv = (TextView) v.findViewById(R.id.section_tv);
            section_n = (TextView) v.findViewById(R.id.section_n);
            section_n.setVisibility(View.GONE);
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
        void onExamRowClicked(List<Exams> matchesList, int position);

    }

}
