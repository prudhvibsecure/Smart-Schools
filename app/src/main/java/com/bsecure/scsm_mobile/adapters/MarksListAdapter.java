package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.models.MarksModel;

import java.util.List;

public class MarksListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<MarksModel> folderlist = null;

	public MarksListAdapter(Context context, List<MarksModel> list) {
		this.context = context;
		folderlist = list;
	}

	@Override
	public int getCount() {
		return folderlist.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position + 1;
	}

	public void Clear() {
		folderlist.clear();
	}

	public static class ViewHolder {

		TextView flername;
		ImageView flderimg;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.marks, parent, false);

		holder = new ViewHolder();
		holder.flername = (TextView) itemView.findViewById(R.id.fl_name);
		holder.flername.setText(folderlist.get(position).getMarks_obtained());

		return itemView;
	}

}
