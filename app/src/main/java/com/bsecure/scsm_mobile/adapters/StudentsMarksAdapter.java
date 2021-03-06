package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class StudentsMarksAdapter extends RecyclerView.Adapter<StudentsMarksAdapter.ContactViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<StudentModel> matchesList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();

    public StudentsMarksAdapter(List<StudentModel> list, Context context, ContactAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.matchesList = list;
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
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {

        try {
            StudentModel mycontactlist = matchesList.get(position);
            //JSONObject jsonObject = array.getJSONObject(i);
            contactViewHolder.tv_title.setText(mycontactlist.getStudent_name());
            contactViewHolder.roll_no.setText(Html.fromHtml("<b>Roll No<b/>-" + mycontactlist.getRoll_no()));
            if (matchesList.get(position).isSelected()){
                if (isChecked.containsKey(position)) {
                    contactViewHolder.chk_box.setChecked(isChecked.get(position));
                    if (mycontactlist.getMarkslist() != null) {
                        contactViewHolder.chk_name.setText(mycontactlist.getMarkslist());
                    }
                    contactViewHolder.chk_box.setChecked(true);
                } else {
                    contactViewHolder.chk_box.setChecked(false);
                    if (mycontactlist.getMarkslist() != null) {
                        contactViewHolder.chk_name.setText(mycontactlist.getMarkslist());
                    }
                }
            }else {
                if (isChecked.size() == 0) {
                    if (mycontactlist.getMarkslist() != null) {
                        contactViewHolder.chk_name.setText(mycontactlist.getMarkslist());
                        contactViewHolder.chk_box.setChecked(false);
                    }
                } else {
                    if (isChecked.containsKey(position)) {
                        contactViewHolder.chk_box.setChecked(isChecked.get(position));
                        if (mycontactlist.getMarkslist() != null) {
                            contactViewHolder.chk_name.setText(mycontactlist.getMarkslist());
                        }
                        contactViewHolder.chk_box.setChecked(true);
                    } else {
                        contactViewHolder.chk_box.setChecked(false);
                        if (mycontactlist.getMarkslist() != null) {
                            contactViewHolder.chk_name.setText(mycontactlist.getMarkslist());
                        }
                    }
                }
            }

            if(mycontactlist.getStatus().equals("1"))
            {
                contactViewHolder.chk_box.setChecked(false);
                //contactViewHolder.chk_box.setEnabled(false);
                contactViewHolder.chk_box.setBackground(context.getDrawable(R.mipmap.inactive));
                contactViewHolder.chk_name.setText("NA");
                mycontactlist.setMarkslist("NA");
            }

            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));
            applyClickEvents(contactViewHolder, matchesList, position, value, contactViewHolder.chk_name, contactViewHolder.chk_box);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<StudentModel> matchesList, final int position, final boolean value, final EditText marks, final CheckBox checked) {
//        contactViewHolder.chk_box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    listener.onRowClicked(matchesList, value, position, marks, checked);
//                } catch (Exception e) {
//
//                }
//            }
//        });
        contactViewHolder.chk_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //save checked data in hash map on check change
                listener.onRowClicked(matchesList, value, position, marks, checked);
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

        View view = LayoutInflater.from(context).inflate(R.layout.students_marks_row, parent, false);
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
        protected TextView roll_no;
        protected TextView contact_ph;
        protected ImageView contact_image;
        protected EditText chk_name;
        CheckBox chk_box;
        RelativeLayout row_user_ll;
        LinearLayout contact_user_ll;

        public ContactViewHolder(View v) {
            super(v);

            tv_title = (TextView) v.findViewById(R.id.cl_name);
            roll_no = (TextView) v.findViewById(R.id.roll_no);
            chk_name = (EditText) v.findViewById(R.id.marks_name);
            chk_box = (CheckBox) v.findViewById(R.id.chk_name);

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

        void onRowClicked(List<StudentModel> matchesList, boolean value, int position, EditText marks, CheckBox checked);
    }

}
