package org.jeonfeel.withlol2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jeonfeel.withlol2.MainActivity;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.adapter.Adapter_freeBoardPhoto;

import java.util.ArrayList;

public class Activity_ModifyFreeBoardPost extends AppCompatActivity {

    private EditText et_modifyFreeBoardTitle,et_modifyFreeBoardContent;
    private CheckBox cb_modifyAnonymity;
    private Button btn_freePostRetouchWrite;
    private RecyclerView modifyPhotoRecyclerView;

    private String currentSummonerName,currentSummonerTier,currentUserUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_board_post_modify);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        getCurrentUserInfo();
        mFindViewById();
        setPhotoRecyclerView();
        setPost();
    }

    private void mFindViewById(){
        et_modifyFreeBoardTitle = findViewById(R.id.et_modifyFreeBoardTitle);
        et_modifyFreeBoardContent = findViewById(R.id.et_modifyFreeBoardContent);
        cb_modifyAnonymity = findViewById(R.id.cb_modifyAnonymity);
        btn_freePostRetouchWrite = findViewById(R.id.btn_freePostRetouchWrite);
        modifyPhotoRecyclerView =  findViewById(R.id.modifyPhotoRecyclerView);
    }
    private void setPost(){
        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");
//        String writerUid = intent.getStringExtra("writerUid");
        String postTitle = intent.getStringExtra("postTitle");
        String postContent = intent.getStringExtra("postContent");
//        String summonerTier = intent.getStringExtra("summonerTier");
//        String summonerName = intent.getStringExtra("summonerName");
        int imgExistence = intent.getIntExtra("imgExistence",0);

        et_modifyFreeBoardTitle.setText(postTitle);
        et_modifyFreeBoardContent.setText(postContent);

        if(imgExistence == 1){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://lolgether.appspot.com").child(postId);
            modifyPhotoRecyclerView.setVisibility(View.VISIBLE);
            ArrayList<Uri> photoList = new ArrayList<>();

            storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference item : listResult.getItems()) {

                        item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()) {
                                    photoList.add(task.getResult());
                                    Adapter_freeBoardPhoto adapter = new Adapter_freeBoardPhoto(photoList,Activity_ModifyFreeBoardPost.this,"modify");
                                    modifyPhotoRecyclerView.setAdapter(adapter);
                                }else{
                                    Toast.makeText(Activity_ModifyFreeBoardPost.this, "이미지를 불러오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    Log.d("qqqq",photoList.size()+"");

                }
            });
        }
    }

    public void getCurrentUserInfo(){
        MainActivity mainActivity = new MainActivity();
        currentSummonerName = mainActivity.getCurrentSummonerName();
        currentSummonerTier = mainActivity.getCurrentSummonerTier();
        currentUserUid = mainActivity.getCurrentUserUid();
    }

    private void setPhotoRecyclerView(){ // 사진 띄어놓는 리사이클러뷰

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        modifyPhotoRecyclerView.setLayoutManager(mLinearLayoutManager);

        modifyPhotoRecyclerView.setVisibility(View.GONE);
    }
}
