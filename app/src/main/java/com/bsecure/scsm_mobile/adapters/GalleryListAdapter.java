package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.models.CalenderModel;
import com.bsecure.scsm_mobile.models.GalleryModel;
import com.bsecure.scsm_mobile.models.TransportModel;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;

import java.util.ArrayList;
import java.util.List;

public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.ViewHolder> {
    Context context;
    ArrayList<GalleryModel> galleryList;
    ArrayList<GalleryModel>arraylist;
    LayoutInflater inflator;
    ClickListener listener;

    public GalleryListAdapter(Context context, ArrayList<GalleryModel> galleryList, ClickListener listener)
    {
        this.context = context;
        this.galleryList = galleryList;
        this.arraylist = new ArrayList<GalleryModel>();
        this.listener = listener;
        inflator = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public GalleryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = inflator.inflate(R.layout.gallery_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryListAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.ename.setText(galleryList.get(position).getEname());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnRowClicked(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView ename;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ename = itemView.findViewById(R.id.ename);
        }
    }
}
