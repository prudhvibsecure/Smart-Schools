package com.bsecure.scsm_mobile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
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
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.controls.ColorGenerator;
import com.bsecure.scsm_mobile.controls.TextDrawable;
import com.bsecure.scsm_mobile.models.ApprovalModel;
import com.bsecure.scsm_mobile.modules.ApprovalMessages;
import com.bsecure.scsm_mobile.recyclertouch.Extension;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;

import java.util.List;

public class ApprovalMessagesAdapter extends RecyclerView.Adapter<ApprovalMessagesAdapter.ContactViewHolder> {

    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ClickListener listener;

    private List<ApprovalModel> messages;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public ApprovalMessagesAdapter(List<ApprovalModel> list, Context context, ApprovalMessages listener) {
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
    public void onBindViewHolder(ContactViewHolder contactViewHolder, final int position) {

        try {
            ApprovalModel message = messages.get(position);
            contactViewHolder.message.setText(message.getMessage());
            if(message.getStatus() == 0)
            {
                contactViewHolder.status.setText("PENDING");
                contactViewHolder.status.setTextColor(Color.BLUE);
            }
            else if(message.getStatus() == 1)
            {
                contactViewHolder.status.setText("ACCEPTED");
                contactViewHolder.status.setTextColor(Color.GREEN);
            }
            else if(message.getStatus() == 2)
            {
                contactViewHolder.status.setText("DECLINED");
                contactViewHolder.status.setTextColor(Color.RED);
            }

            contactViewHolder.dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        listener.OnRowClicked(position, v);
                    } catch (Exception e) {

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approval_item, parent, false);
        return new ContactViewHolder(itemView);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView message, status ;
        public ImageView imgProfile, dots;
        public LinearLayout contact_user_ll;
        public RelativeLayout viewBackground, viewForeground;
        public View mViewContent;
        public View mActionContainer;

        public ContactViewHolder(View v) {
            super(v);

            message = (TextView) v.findViewById(R.id.message);
            status = v.findViewById(R.id.status);
            dots = v.findViewById(R.id.dots);

        }

    }


}

