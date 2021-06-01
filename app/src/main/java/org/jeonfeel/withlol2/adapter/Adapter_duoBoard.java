package org.jeonfeel.withlol2.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jeonfeel.withlol2.etc.DuoBoardItemClickListener;
import org.jeonfeel.withlol2.etc.Item_duoBoard;
import org.jeonfeel.withlol2.R;

import java.util.ArrayList;

public class Adapter_duoBoard extends RecyclerView.Adapter<Adapter_duoBoard.CustomViewHolder>
        implements DuoBoardItemClickListener {

    private ArrayList<Item_duoBoard> items;
    DuoBoardItemClickListener listener;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView tv_duoBoardItemTitle;
        protected TextView tv_freeBoardItemDate;
        protected TextView tv_duoBoardItemContent;
        protected TextView tv_duoBoardItemSummonerName;
        protected TextView tv_duoBoardItemCommentCount;
        protected ImageView iv_duoBoardItemTier;
        protected ImageView iv_duoBoardItemMic;

        public CustomViewHolder(View view) {
            super(view);

            this.tv_duoBoardItemTitle = view.findViewById(R.id.tv_duoBoardItemTitle);
            this.tv_freeBoardItemDate = view.findViewById(R.id.tv_duoBoardItemDate);
            this.tv_duoBoardItemContent = view.findViewById(R.id.tv_duoBoardItemContent);
            this.tv_duoBoardItemSummonerName = view.findViewById(R.id.tv_duoBoardItemSummonerName);
            this.tv_duoBoardItemCommentCount = view.findViewById(R.id.tv_duoBoardItemCommentCount);
            this.iv_duoBoardItemTier = view.findViewById(R.id.iv_duoBoardItemTier);
            this.iv_duoBoardItemMic = view.findViewById(R.id.iv_duoBoardItemMic);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(CustomViewHolder.this, v, position);
                    }
                }
            });
        }
    }
    public Adapter_duoBoard(ArrayList<Item_duoBoard> data, Context context) {
        this.items = data;
        this.context = context;
    }

    public Adapter_duoBoard() {}

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.duoboard_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        setWrittenDate(position,viewholder,System.currentTimeMillis());
        viewholder.tv_duoBoardItemTitle.setText(items.get(position).getTitle());
        viewholder.tv_duoBoardItemContent.setText(items.get(position).getContent());
        viewholder.tv_duoBoardItemSummonerName.setText(items.get(position).getSummonerName());
        viewholder.tv_duoBoardItemCommentCount.setText(items.get(position).getCommentCount() + "");
        setIv_duoBoardItemTier(position, viewholder);
        setIv_duoBoardItemMic(position, viewholder);
    }

    @Override
    public int getItemCount() {
            return (null != items ? items.size() : 0);
    }

    public Item_duoBoard getItem(int position) {
            return items.get(position);
    }

    public void setOnItemClickListener(DuoBoardItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(CustomViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }
    public void setWrittenDate(int position, Adapter_duoBoard.CustomViewHolder viewholder, long currentTime){
        final int SEC = 60;
        final int MIN = 60;
        final int HOUR = 24;
        final int DAY = 30;
        final int MONTH = 12;

        long regTime = items.get(position).getPostWrittenDate();

        long diffTime = (currentTime - regTime) / 1000;
        String dateText = null;
        if (diffTime < SEC) {
            dateText = "방금 전";
        } else if ((diffTime /= SEC) < MIN) {
            dateText = diffTime + "분 전";
        } else if ((diffTime /= MIN) < HOUR) {
            dateText = (diffTime) + "시간 전";
        } else if ((diffTime /= HOUR) < DAY) {
            dateText = (diffTime) + "일 전";
        } else if ((diffTime /= DAY) < MONTH) {
            dateText = (diffTime) + "달 전";
        } else {
            dateText = (diffTime) + "년 전";
        }

        viewholder.tv_freeBoardItemDate.setText(dateText);
    }
    public void setIv_duoBoardItemTier(int position, Adapter_duoBoard.CustomViewHolder viewholder){
        switch(items.get(position).getTier()){
            case "UnRanked":
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.unranked);
                break;
            case "IRON":
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.iron);
                break;
            case "BRONZE":
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.bronze);
                break;
            case "SILVER":
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.silver);
                break;
            case "GOLD":
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.gold);
                break;
            case "PLATINUM":
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.ple);
                break;
            case "DIAMOND":
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.dia);
                break;
            case "MASTER" :
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.master);
                break;
            case "GRANDMASTER" :
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.gm);
                break;
            case "CHALLENGER" :
                viewholder.iv_duoBoardItemTier.setImageResource(R.drawable.ch);
                break;
        }
    }
    public void setIv_duoBoardItemMic(int position, Adapter_duoBoard.CustomViewHolder viewholder){
        if(items.get(position).getSelectedMic().equals("on")){
            viewholder.iv_duoBoardItemMic.setImageResource(R.drawable.img_mic);
        }else{
            viewholder.iv_duoBoardItemMic.setVisibility(View.GONE);
        }
    }
}



