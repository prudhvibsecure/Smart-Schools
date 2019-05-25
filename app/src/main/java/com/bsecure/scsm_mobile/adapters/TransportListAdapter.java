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
import com.bsecure.scsm_mobile.models.TransportModel;
import com.bsecure.scsm_mobile.recyclertouch.Extension;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class TransportListAdapter extends RecyclerView.Adapter<TransportListAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<TransportModel> classModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public TransportListAdapter(List<TransportModel> list, Context context, ContactAdapterListener listener) {
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
            TransportModel teacher_lList = classModelList.get(position);
            contactViewHolder.tv_title.setText(teacher_lList.getTransport_name());
            contactViewHolder.section_tv.setText(teacher_lList.getPhone_number());

            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

            applyClickEvents(contactViewHolder, classModelList, position);
            applyEvents(contactViewHolder, classModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void applyEvents(ContactViewHolder contactViewHolder, final List<TransportModel> classModelList, final int position) {

        if (contactViewHolder instanceof ItemSwipeWithActionWidthViewHolder) {
            ItemSwipeWithActionWidthViewHolder viewHolder = (ItemSwipeWithActionWidthViewHolder) contactViewHolder;
            if (classModelList.get(position).getStatus().equalsIgnoreCase("0")) {
                viewHolder.mActionViewstatus.setVisibility(View.VISIBLE);
                viewHolder.mActionViewEdit.setVisibility(View.GONE);
            } else {
                viewHolder.mActionViewstatus.setVisibility(View.GONE);
                viewHolder.mActionViewEdit.setVisibility(View.VISIBLE);
            }
            viewHolder.mActionViewDelete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToDelete(position, classModelList);
                        }
                    }

            );
            viewHolder.mActionViewstatus.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToStatus(position, classModelList,"1");
                        }
                    }

            );
            viewHolder.mActionViewEdit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToStatus(position, classModelList,"0");

                        }
                    }

            );
        }
    }

    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<TransportModel> classModelList, final int position) {
        contactViewHolder.mViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageRow(classModelList, position);
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
                .inflate(R.layout.list_item_main_n, parent, false);

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
        public TextView section_tv;
        public TextView section_n;
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
        void onMessageRow(List<TransportModel> matchesList, int position);

        void swipeToDelete(int position, List<TransportModel> classModelList);

        void swipeToStatus(int position, List<TransportModel> classModelList, String s);
    }

    class ItemSwipeWithActionWidthViewHolder extends ContactViewHolder implements Extension {

        public View mActionViewDelete;
        public View mActionViewEdit;
        public View mActionViewstatus;

        public ItemSwipeWithActionWidthViewHolder(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
            mActionViewstatus = itemView.findViewById(R.id.view_list_repo_action_status);
            mActionViewEdit = itemView.findViewById(R.id.view_list_repo_action_update);
        }

        @Override
        public float getActionWidth() {
            return mActionContainer.getWidth();
        }
    }

}
