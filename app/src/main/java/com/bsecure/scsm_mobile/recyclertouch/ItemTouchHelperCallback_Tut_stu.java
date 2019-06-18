
package com.bsecure.scsm_mobile.recyclertouch;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.bsecure.scsm_mobile.adapters.TransportListAdapter;
import com.bsecure.scsm_mobile.adapters.TransportListAdapter_New;


public class ItemTouchHelperCallback_Tut_stu extends ItemTouchHelperExtension.Callback {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        TransportListAdapter_New.ContactViewHolder holder = (TransportListAdapter_New.ContactViewHolder) viewHolder;

        if (viewHolder instanceof TransportListAdapter_New.ContactViewHolder)
            holder.mViewContent.setTranslationX(dX);
    }
}
