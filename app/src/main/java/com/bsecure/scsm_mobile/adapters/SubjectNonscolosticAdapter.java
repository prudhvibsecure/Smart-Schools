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
import com.bsecure.scsm_mobile.models.Subjects;

import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class SubjectNonscolosticAdapter extends RecyclerView.Adapter<SubjectNonscolosticAdapter.ContactViewHolder> {


    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;
    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<Subjects> tutorsModelList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;

    public SubjectNonscolosticAdapter(Context context,List<Subjects> list) {
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
            Subjects classMode_lList = tutorsModelList.get(position);
            contactViewHolder.tv_title.setText(classMode_lList.getSubject());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sclostic_sub, parent, false);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
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
        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.non_sc);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface ContactAdapterListener {
        void onMessageRowClicked(List<Subjects> matchesList, int position);

    }

}
