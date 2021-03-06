package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class NonScholasticStudentsAdapter extends  RecyclerView.Adapter<NonScholasticStudentsAdapter.ViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private ContactAdapterListener listener;

    private List<StudentModel> matchesList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();


    public NonScholasticStudentsAdapter(List<StudentModel> list, Context context, ContactAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.matchesList = list;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.students_marks_row, viewGroup, false);
        ViewHolder myHoder = new ViewHolder(view);
        return myHoder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder ViewHolder, int position) {

        try {
            StudentModel mycontactlist = matchesList.get(position);
            //JSONObject jsonObject = array.getJSONObject(i);
            ViewHolder.chk_name.setVisibility(View.GONE);
            ViewHolder.tv_title.setText(mycontactlist.getStudent_name());
            ViewHolder.roll_no.setText(Html.fromHtml("<b>Roll No<b/>-" + mycontactlist.getRoll_no()));
            if (isChecked.size() == 0) {
                if (mycontactlist.getMarkslist() != null) {
                    ViewHolder.chk_name.setText(mycontactlist.getMarkslist());
                    ViewHolder.chk_box.setChecked(false);
                }
            } else {
                if (isChecked.containsKey(position)) {
                    ViewHolder.chk_box.setChecked(isChecked.get(position));
                    if (mycontactlist.getMarkslist() != null) {
                        ViewHolder.chk_name.setText(mycontactlist.getMarkslist());
                    }
                    ViewHolder.chk_box.setChecked(true);
                } else {
                    ViewHolder.chk_box.setChecked(false);
                    if (mycontactlist.getMarkslist() != null) {
                        ViewHolder.chk_name.setText(mycontactlist.getMarkslist());
                    }
                }
            }
            boolean value = selectedItems.get(position);
            ViewHolder.itemView.setActivated(selectedItems.get(position, false));
            applyClickEvents(ViewHolder, matchesList, position, value, ViewHolder.chk_name, ViewHolder.chk_box);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void applyClickEvents(ViewHolder ViewHolder, final List<StudentModel> matchesList, final int position, final boolean value, final EditText marks, final CheckBox checked) {

        ViewHolder.chk_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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


    public class ViewHolder extends RecyclerView.ViewHolder{

        protected TextView tv_title;
        protected TextView roll_no;
        protected TextView contact_ph;
        protected ImageView contact_image;
        protected EditText chk_name;
        CheckBox chk_box;
        RelativeLayout row_user_ll;
        LinearLayout contact_user_ll;
        public ViewHolder(@NonNull View v) {
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
