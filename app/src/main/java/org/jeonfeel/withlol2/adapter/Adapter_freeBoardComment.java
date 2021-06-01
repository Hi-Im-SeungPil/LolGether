package org.jeonfeel.withlol2.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import org.jeonfeel.withlol2.etc.Item_comment;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.DTO.SaveCommentReport;

import java.util.ArrayList;

public class Adapter_freeBoardComment extends RecyclerView.Adapter<Adapter_freeBoardComment.CustomViewHolder> {

    private ArrayList<Item_comment> items;
    protected String currentUserUid;
    private DatabaseReference mDatabase;
    private String postId;
    private int commentCount;
    private Context context;


    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected ImageView img_commentSummonerTier;
        protected TextView tv_commentSummonerName;
        protected TextView tv_commentContent;
        protected TextView tv_commentWriteDate;
        protected Button btn_commentDel;
        protected Button btn_commentReport;

        public CustomViewHolder(@NonNull View view) {
            super(view);

            this.img_commentSummonerTier = view.findViewById(R.id.img_commentSummonerTier);
            this.tv_commentSummonerName = view.findViewById(R.id.tv_commentSummonerName);
            this.tv_commentContent = view.findViewById(R.id.tv_commentContent);
            this.tv_commentWriteDate = view.findViewById(R.id.tv_commentWriteDate);
            this.btn_commentDel = view.findViewById(R.id.btn_commentDel);
            this.btn_commentReport = view.findViewById(R.id.btn_commentReport);
        }
    }

    public Adapter_freeBoardComment(ArrayList<Item_comment> data, String currentUserUid, DatabaseReference mDatabase,
                                   String postId, int commentCount,Context context){
        this.items = data;
        this.currentUserUid = currentUserUid;
        this.mDatabase = mDatabase;
        this.postId = postId;
        this.commentCount = commentCount;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commentitem,parent,false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {

        holder.tv_commentContent.setText(items.get(position).getCommentContent());
        holder.tv_commentSummonerName.setText(items.get(position).getCommentSummonerName());
        setCommentWrittenDate(position,holder,System.currentTimeMillis());
        setCommentImg(position,holder);
        checkCommentWriter(position, holder);
        holder.btn_commentDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_commentDel(position);
            }
        });
        holder.btn_commentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_commentReport(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public void setCommentImg(int position,CustomViewHolder holder){
        switch (items.get(position).getCommentSummonerTier()){
            case "anonymity":
                holder.img_commentSummonerTier.setImageResource(R.drawable.img_bee);
                break;
            case "UnRanked" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.unranked);
                break;
            case "IRON" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.iron);
                break;
            case "BRONZE" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.bronze);
                break;
            case "SILVER" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.silver);
                break;
            case "GOLD" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.gold);
                break;
            case "PLATINUM" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.ple);
                break;
            case "DIAMOND" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.dia);
                break;
            case "MASTER" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.master);
                break;
            case "GRANDMASTER" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.gm);
                break;
            case "CHALLENGER" :
                holder.img_commentSummonerTier.setImageResource(R.drawable.ch);
                break;
        }
    }
    public void setCommentWrittenDate(int position,CustomViewHolder holder,long currentTime){
        final int SEC = 60;
        final int MIN = 60;
        final int HOUR = 24;
        final int DAY = 30;
        final int MONTH = 12;

        long regTime = items.get(position).getCommentDate();

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

        holder.tv_commentWriteDate.setText(dateText);
    }
    public void setBtn_commentDel(int position){
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context)
                .setMessage("삭제 하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child("freeBoardComment").child(postId).child(items.get(position).getCommentId()).removeValue();
                        items.remove(position);
                        mDatabase.child("freeBoard").child(postId).child("commentCount").setValue(items.size());
                        notifyDataSetChanged();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }
    public void checkCommentWriter(int position,CustomViewHolder holder){
        if(currentUserUid.equals(items.get(position).getUid())){
            holder.btn_commentReport.setVisibility(View.GONE);
        }else{
            holder.btn_commentDel.setVisibility(View.GONE);
        }
    }
    public void setBtn_commentReport(int position){
        String uid = items.get(position).getUid();
        String commentId = items.get(position).getCommentId();
        String commentContent = items.get(position).getCommentContent();
        SaveCommentReport itemSaveCommentReport = new SaveCommentReport(commentId,uid,commentContent);
        mDatabase.child("freeBoardCommentReport").child(commentId).setValue(itemSaveCommentReport);
    }

}

