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

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<TransportModel> classModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public RouteListAdapter(List<TransportModel> list, Context context, ContactAdapterListener listener) {
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
            contactViewHolder.name.setText(teacher_lList.getTransport_name());
            contactViewHolder.phone.setText(teacher_lList.getPhone_number());

            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));

            applyClickEvents(contactViewHolder, classModelList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void applyClickEvents(final ContactViewHolder contactViewHolder, final List<TransportModel> classModelList, final int position) {
        contactViewHolder.tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onClickStart(classModelList, position);
                    contactViewHolder.tv_stop.setVisibility(View.VISIBLE);
                    contactViewHolder.tv_start.setVisibility(View.GONE);
                } catch (Exception e) {

                }
            }
        });
        contactViewHolder.tv_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickStart(classModelList, position);
                contactViewHolder.tv_stop.setVisibility(View.GONE);
                contactViewHolder.tv_start.setVisibility(View.VISIBLE);
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
                .inflate(R.layout.route_row, parent, false);

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

        public TextView tv_stop;
        public TextView tv_start;
        public TextView phone;
        public TextView name;
        public LinearLayout contact_user_ll;

        public ContactViewHolder(View v) {
            super(v);

            name = (TextView) v.findViewById(R.id.trnasport_name);
            phone = (TextView) v.findViewById(R.id.phone);
            tv_start = (TextView) v.findViewById(R.id.start);
            tv_stop = (TextView) v.findViewById(R.id.stop);
            v.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface ContactAdapterListener {
        void onMessageRow(List<TransportModel> matchesList, int position);

        void onClickStart(List<TransportModel> classModelList,int position);

        void onClickStop(List<TransportModel> classModelList,int position);
    }


}
