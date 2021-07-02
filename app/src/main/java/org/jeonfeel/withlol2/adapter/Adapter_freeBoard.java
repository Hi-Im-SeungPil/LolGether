package org.jeonfeel.withlol2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.etc.FreeBoardItemClickListener;
import org.jeonfeel.withlol2.etc.Item_freeBoard;

import java.util.ArrayList;

public class Adapter_freeBoard extends RecyclerView.Adapter<Adapter_freeBoard.CustomViewHolder>
        implements FreeBoardItemClickListener {

    private ArrayList<Item_freeBoard> items;
    private FreeBoardItemClickListener listener;
    private Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView tv_freeBoardItemTitle;
        protected TextView tv_freeBoardItemDate;
        protected TextView tv_freeBoardItemContent;
        protected TextView tv_freeBoardItemSummonerName;
        protected TextView tv_freeBoardItemCommentCount;
        protected ImageView iv_freeBoardItemTier;
        protected ImageView iv_freeBoardItemImage;

        public CustomViewHolder(View view) {
            super(view);

            this.tv_freeBoardItemTitle = view.findViewById(R.id.tv_freeBoardItemTitle);
            this.tv_freeBoardItemDate = view.findViewById(R.id.tv_freeBoardItemDate);
            this.tv_freeBoardItemContent = view.findViewById(R.id.tv_freeBoardItemContent);
            this.tv_freeBoardItemSummonerName = view.findViewById(R.id.tv_freeBoardItemSummonerName);
            this.tv_freeBoardItemCommentCount = view.findViewById(R.id.tv_freeBoardItemCommentCount);
            this.iv_freeBoardItemTier = view.findViewById(R.id.iv_freeBoardItemTier);
            this.iv_freeBoardItemImage = view.findViewById(R.id.iv_freeBoardItemImage);

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

    public Adapter_freeBoard(ArrayList<Item_freeBoard> data, Context context) {
        this.items = data;
        this.context = context;
    }

    public Adapter_freeBoard() {}

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.free_board_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        setWrittenDate(position,viewholder,System.currentTimeMillis());

        setImg_freeBoardItemTier(position,viewholder);

        viewholder.tv_freeBoardItemTitle.setText(items.get(position).getTitle());
        viewholder.tv_freeBoardItemContent.setText(items.get(position).getContent());
        viewholder.tv_freeBoardItemSummonerName.setText(items.get(position).getSummonerName());
        viewholder.tv_freeBoardItemCommentCount.setText(String.valueOf(items.get(position).getCommentCount()));

        if(items.get(position).getImageExistence() == 0){
            viewholder.iv_freeBoardItemImage.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public Item_freeBoard getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(FreeBoardItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(CustomViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public void setWrittenDate(int position, Adapter_freeBoard.CustomViewHolder viewholder, long currentTime) {
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

    public void setImg_freeBoardItemTier(int position, CustomViewHolder viewholder) {
        switch (items.get(position).getTier()) {
            case "anonymity":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.img_bee);
                break;
            case "UnRanked":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.unranked);
                break;
            case "IRON":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.iron);
                break;
            case "BRONZE":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.bronze);
                break;
            case "SILVER":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.silver);
                break;
            case "GOLD":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.gold);
                break;
            case "PLATINUM":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.ple);
                break;
            case "DIAMOND":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.dia);
                break;
            case "MASTER":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.master);
                break;
            case "GRANDMASTER":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.gm);
                break;
            case "CHALLENGER":
                viewholder.iv_freeBoardItemTier.setImageResource(R.drawable.ch);
                break;
        }
    }
}
