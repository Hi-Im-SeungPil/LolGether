package org.jeonfeel.withlol2.adapter;

import android.content.Context;
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

import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.activity.Activity_ModifyFreeBoardPost;
import org.jeonfeel.withlol2.activity.Activity_writingFreeBoardPost;

import java.util.ArrayList;

import static org.jeonfeel.withlol2.activity.Activity_watchingFreeBoardPost.isValidContextForGlide;

public class Adapter_freeBoardPhoto extends RecyclerView.Adapter<Adapter_freeBoardPhoto.mViewHolder> {

    ArrayList<Uri> albumImgList;
    Context mContext;
    String kind;

        //생성자 정의
    public Adapter_freeBoardPhoto(ArrayList<Uri> albumImgList, Context mContext,String kind ){
        this.albumImgList = albumImgList;
        this.mContext = mContext;
        this.kind = kind;
    }

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
//            holder.iv_photo.setImageBitmap(albumImgList.get(position));

        if(isValidContextForGlide(mContext)) {
            Glide.with(mContext).load(albumImgList.get(position))
                    .into(holder.iv_photo);
        }

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

                    if(kind.equals("writing")) {
                        Activity_writingFreeBoardPost fp = new Activity_writingFreeBoardPost();
                        fp.delPhoto(getLayoutPosition());
                        notifyDataSetChanged();

                    }else if(kind.equals("modify")){
                        Activity_ModifyFreeBoardPost fp = new Activity_ModifyFreeBoardPost();
                        fp.delPhoto(getLayoutPosition());
                        notifyDataSetChanged();

                    }
                }
            });
        }
    }
}
