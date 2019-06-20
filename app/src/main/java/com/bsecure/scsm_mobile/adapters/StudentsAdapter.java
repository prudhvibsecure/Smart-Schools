package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.models.StudentModel;


import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ContactViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;
    private final LayoutInflater layoutInflater;
    private List<StudentModel> matchesList = null;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();

    public StudentsAdapter(List<StudentModel> list, Context context, ContactAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.matchesList = list;
        this.layoutInflater = LayoutInflater.from(context);
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //    @Override
//    public int getItemCount() {
//        return array.length();
//    }
    public void clear() {
        // matchesList=null;
        final int size = matchesList.size();
        matchesList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (matchesList.size() == 0) {
                arr = 0;

            } else {
                arr = matchesList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @Override
    public void onBindViewHolder(final StudentsAdapter.ContactViewHolder contactViewHolder, final int position) {

        try {
            final StudentModel mycontactlist = matchesList.get(position);
            //JSONObject jsonObject = array.getJSONObject(i);

            contactViewHolder.tv_title.setText(mycontactlist.getStudent_name());
            contactViewHolder.contact_ph.setText(mycontactlist.getRoll_no());

//            if (mycontactlist.getStatus().equalsIgnoreCase("0")) {
//                contactViewHolder.chk_name.setChecked(true);
//                contactViewHolder.chk_name.setBackgroundColor(Color.TRANSPARENT);
//                contactViewHolder.chk_name.setBackground(context.getDrawable(R.mipmap.ic_check));
//
//            } else {
//                contactViewHolder.chk_name.setBackgroundColor(Color.TRANSPARENT);
//                contactViewHolder.chk_name.setChecked(false);
//                contactViewHolder.chk_name.setBackground(context.getDrawable(R.mipmap.ic_uncheck));
//            }
//            if (matchesList.get(position).isSelected()) {
//                contactViewHolder.chk_name.setBackground(context.getDrawable(R.mipmap.ic_check));
//            }
//            else
                if (isChecked.containsKey(position)) {
                    contactViewHolder.chk_name.setChecked(isChecked.get(position));
                    contactViewHolder.chk_name.setBackground(context.getDrawable(R.mipmap.ic_uncheck));

                 }else {
                contactViewHolder.chk_name.setChecked(false);
                contactViewHolder.chk_name.setBackground(context.getDrawable(R.mipmap.ic_check));
            }
            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));
            applyClickEvents(contactViewHolder, matchesList, position, value, contactViewHolder.chk_name);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<StudentModel> matchesList, final int position, final boolean value, final CheckBox chk_name) {
//        contactViewHolder.chk_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    listener.onRowClicked(matchesList, value, chk_name, position);
//                } catch (Exception e) {
//
//                }
//            }
//        });
        contactViewHolder.chk_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.onRowClicked(matchesList, value, chk_name, position);
                //save checked data in hash map on check change
                if (compoundButton.isChecked()) {
                    isChecked.put(position, b);
                } else {
                    isChecked.remove(position);
                }

            }
        });
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.students_row, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;

    }

//    public long getItemId(int position) {
//        return position + 1;
//    }
//
//    public void setItems(JSONArray aArray) {
//        this.array = aArray;
//    }
//
//    public JSONArray getItems() {
//        return this.array;
//    }
//
//    public void clear() {
//        array = null;
//        array = new JSONArray();
//    }
//
//    public void release() {
//        array = null;
//    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_title;
        protected TextView contact_ph;
        protected ImageView contact_image;
        protected CheckBox chk_name;
        RelativeLayout row_user_ll;
        LinearLayout contact_user_ll;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.cl_name);
            contact_ph = (TextView) v.findViewById(R.id.section_tv);
            chk_name = (CheckBox) v.findViewById(R.id.chk_name);
            chk_name.setBackground(context.getDrawable(R.mipmap.ic_check));

        }
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);

        }
        notifyItemChanged(pos);
    }

    public interface ContactAdapterListener {

        void onRowClicked(List<StudentModel> matchesList, boolean value, CheckBox chk_name, int position);
    }

}
