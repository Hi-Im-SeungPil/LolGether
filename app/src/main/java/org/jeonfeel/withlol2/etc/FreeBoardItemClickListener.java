package org.jeonfeel.withlol2.etc;

import android.view.View;

import org.jeonfeel.withlol2.adapter.Adapter_freeBoard;

public interface FreeBoardItemClickListener {
    public void onItemClick(Adapter_freeBoard.CustomViewHolder holder, View view, int position);
}
