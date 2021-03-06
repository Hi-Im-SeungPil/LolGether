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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import org.jeonfeel.withlol2.etc.Item_comment;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.DTO.SaveCommentReport;

import java.util.ArrayList;

public class Adapter_duoBoardComment extends RecyclerView.Adapter<Adapter_duoBoardComment.CustomViewHolder> {

    private ArrayList<Item_comment> items;
    protected String currentUserUid;
    private DatabaseReference mDatabase,mmDatabase;
    private String postId,boardChild,selectedPosition;
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

    public Adapter_duoBoardComment(ArrayList<Item_comment> data, String currentUserUid, DatabaseReference mDatabase,
                                   DatabaseReference mmDatabase, String postId, String boardChild, int commentCount, String selectedPosition,
    Context context) {
        this.items = data;
        this.currentUserUid = currentUserUid;
        this.mDatabase = mDatabase;
        this.mmDatabase = mmDatabase;
        this.postId = postId;
        this.boardChild = boardChild;
        this.commentCount = commentCount;
        this.selectedPosition = selectedPosition;
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
    public int getItemViewType(int position) {
        return position;
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

    private void setCommentWrittenDate(int position,CustomViewHolder holder,long currentTime){

        final int SEC = 60;
        final int MIN = 60;
        final int HOUR = 24;
        final int DAY = 30;
        final int MONTH = 12;

        long regTime = items.get(position).getCommentDate();

        long diffTime = (currentTime - regTime) / 1000;
        String dateText = null;
        if (diffTime < SEC) {
            dateText = "?????? ???";
        } else if ((diffTime /= SEC) < MIN) {
            dateText = diffTime + "??? ???";
        } else if ((diffTime /= MIN) < HOUR) {
            dateText = (diffTime) + "?????? ???";
        } else if ((diffTime /= HOUR) < DAY) {
            dateText = (diffTime) + "??? ???";
        } else if ((diffTime /= DAY) < MONTH) {
            dateText = (diffTime) + "??? ???";
        } else {
            dateText = (diffTime) + "??? ???";
        }

        holder.tv_commentWriteDate.setText(dateText);
    }
    private void setBtn_commentDel(int position){

        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context).setMessage("?????? ???????????????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mmDatabase.child("duoBoardComment").child(postId).child(items.get(position).getCommentId()).removeValue();
                        items.remove(position);
                        mDatabase.child(selectedPosition).child(postId).child("commentCount").setValue(items.size());
                        notifyDataSetChanged();
                    }
                }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

    private void checkCommentWriter(int position,CustomViewHolder holder){

        if(currentUserUid.equals(items.get(position).getUid()) || items.get(position).getUid().equals("OS8uQWFjckZI7pFJJGvmynBdQVK2")){
            holder.btn_commentReport.setVisibility(View.GONE);
        }else{
            holder.btn_commentDel.setVisibility(View.GONE);
        }
    }

    private void setBtn_commentReport(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage("?????? ???????????????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String uid = items.get(position).getUid();
                        String commentId = items.get(position).getCommentId();
                        String commentContent = items.get(position).getCommentContent();

                        SaveCommentReport itemSaveCommentReport = new SaveCommentReport(commentId,uid,commentContent);
                        mmDatabase.child("duoBoardCommentReport").child(commentId).setValue(itemSaveCommentReport).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
