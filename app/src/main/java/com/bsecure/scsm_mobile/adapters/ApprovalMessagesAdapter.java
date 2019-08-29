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
import com.bsecure.scsm_mobile.models.ApprovalModel;
import com.bsecure.scsm_mobile.recyclertouch.Extension;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;

import java.util.List;

public class ApprovalMessagesAdapter extends RecyclerView.Adapter<ApprovalMessagesAdapter.ContactViewHolder> {

    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<ApprovalModel> messages;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public ApprovalMessagesAdapter(List<ApprovalModel> list, Context context, ContactAdapterListener listener) {
        this.context = context;
        this.messages = list;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (messages.size() == 0) {
                arr = 0;

            } else {
                arr = messages.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {

        try {
            ApprovalModel message = messages.get(position);
            contactViewHolder.message.setText(message.getMessage());
            //contactViewHolder.status.setText(classMode_lList.getStatus());

            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

            applyClickEvents(contactViewHolder, messages, position);
            applyEvents(contactViewHolder, messages, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void applyEvents(ContactViewHolder contactViewHolder, final List<ApprovalModel> messages, final int position) {

        if (contactViewHolder instanceof ItemSwipeWithActionWidthViewHolder) {
            ItemSwipeWithActionWidthViewHolder viewHolder = (ItemSwipeWithActionWidthViewHolder) contactViewHolder;

            viewHolder.decline.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToDelete(position, messages);
                        }
                    }

            );
            /*viewHolder.mActionViewstatus.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToMarks(position, classModelList);

                        }
                    }

            );*/
            viewHolder.mActionViewEdit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToSyllabus(position, messages);

                        }
                    }

            );
        }
    }

    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<ApprovalModel> messages, final int position) {
        contactViewHolder.mViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageRowClicked(messages, position);
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
                .inflate(R.layout.approval_item, parent, false);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();

        if (viewType == ITEM_TYPE_ACTION_WIDTH)
            return new ItemSwipeWithActionWidthViewHolder(itemView);

        return new ContactViewHolder(itemView);
    }


    public void clear() {
        // matchesList=null;
        final int size = messages.size();
        messages.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void removeItem(int position) {
        messages.remove(position);
        notifyItemRemoved(position);
    }
//
//    public void release() {
//        array = null;
//    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView message, status ;
        public ImageView imgProfile;
        public LinearLayout contact_user_ll;
        public RelativeLayout viewBackground, viewForeground;
        public View mViewContent;
        public View mActionContainer;

        public ContactViewHolder(View v) {
            super(v);

            message = (TextView) v.findViewById(R.id.message);
            //status = v.findViewById(R.id.status);

            mViewContent = itemView.findViewById(R.id.view_list_main_content);
            mActionContainer = itemView.findViewById(R.id.view_list_repo_action_container);

            v.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface ContactAdapterListener {
        void onMessageRowClicked(List<ApprovalModel> matchesList, int position);

        void swipeToSyllabus(int position, List<ApprovalModel> classModelList);

        /* void swipeToMarks(int position, List<StudentModel> classModelList);*/

        void swipeToDelete(int position, List<ApprovalModel> classModelList);
    }

    class ItemSwipeWithActionWidthViewHolder extends ContactViewHolder implements Extension {

        public View mActionViewDelete;
        public View mActionViewEdit;
        public View mActionViewstatus;
        public View mActionViewsper;
        public View decline;


        public ItemSwipeWithActionWidthViewHolder(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
            mActionViewstatus = itemView.findViewById(R.id.view_list_repo_action_status);
            mActionViewEdit = itemView.findViewById(R.id.view_list_repo_action_update);
            mActionViewsper = itemView.findViewById(R.id.view_list_repo_action_per);
            decline = itemView.findViewById(R.id.view_list_repo_action_more);
            ((TextView)mActionViewDelete.findViewById(R.id.view_list_repo_action_delete)).setText("Delete");
            ((TextView)mActionViewDelete.findViewById(R.id.view_list_repo_action_delete)).setBackgroundColor(Color.RED);
            mActionViewsper.setVisibility(View.GONE);
            mActionViewstatus.setVisibility(View.GONE);


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

