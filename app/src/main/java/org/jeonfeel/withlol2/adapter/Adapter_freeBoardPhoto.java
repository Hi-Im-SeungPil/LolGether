package org.jeonfeel.withlol2.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.activity.Activity_freeBoardImageRetouch;
import org.jeonfeel.withlol2.activity.Activity_freeBoardPostRetouch;
import org.jeonfeel.withlol2.activity.Activity_watchingFreeBoardPost;
import org.jeonfeel.withlol2.activity.Activity_writingFreeBoardPost;

import java.util.ArrayList;

public class Adapter_freeBoardPhoto extends RecyclerView.Adapter<Adapter_freeBoardPhoto.mViewHolder> {

    ArrayList<Bitmap> albumImgList;
    Context mContext;
    RecyclerView recyclerView;
    String activityKind = "",postId;


        //생성자 정의
    public Adapter_freeBoardPhoto(ArrayList<Bitmap> albumImgList, Context mContext,RecyclerView recyclerView,String activityKind){
        this.albumImgList = albumImgList;
        this.mContext = mContext;
        this.recyclerView = recyclerView;
        this.activityKind = activityKind;
    }

    public Adapter_freeBoardPhoto(ArrayList<Bitmap> albumImgList, Context mContext,
                                  RecyclerView recyclerView,String postId,String activityKind){
        this.albumImgList = albumImgList;
        this.mContext = mContext;
        this.recyclerView = recyclerView;
        this.postId = postId;
        this.activityKind = activityKind;
    }
    public Adapter_freeBoardPhoto(){}

    @NonNull
    @Override
    public Adapter_freeBoardPhoto.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.photo_recycler_view_item,parent,false);
        Adapter_freeBoardPhoto.mViewHolder viewHolder = new Adapter_freeBoardPhoto.mViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        //앨범에서 가져온 이미지 표시

            holder.iv_photo.setImageBitmap(albumImgList.get(position));

    }

    @Override
    public int getItemCount() {
            return albumImgList.size();


    }

    public class mViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_photo;
        Button btn_photoDel;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_photo = itemView.findViewById(R.id.iv_photo);
            btn_photoDel = itemView.findViewById(R.id.btn_photoDel);
            btn_photoDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(activityKind.equals("writing")) {
                        Activity_writingFreeBoardPost fp = new Activity_writingFreeBoardPost();
                        fp.delPhoto(getLayoutPosition());
                        notifyDataSetChanged();
                    }else if(activityKind.equals("")){
                        Activity_freeBoardImageRetouch fp = new Activity_freeBoardImageRetouch();
                        fp.delPhoto(getLayoutPosition());
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
