package org.jeonfeel.withlol2.activity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import org.jeonfeel.withlol2.etc.CheckNetwork;

import java.util.ArrayList;

public class Activity_ModifyFreeBoardPost extends AppCompatActivity {

    private EditText et_modifyFreeBoardTitle,et_modifyFreeBoardContent;
    private static final int PICK_FROM_ALBUM = 111;
    private CheckBox cb_modifyAnonymity;
    private Button btn_freePostModifyWrite,btn_postModifyAddPhoto;
    private RecyclerView modifyPhotoRecyclerView;

    private String currentSummonerName,currentSummonerTier,currentUserUid;

    private static ArrayList<Bitmap> uploadPhotoList;
    private static ArrayList<Uri> uploadPhotoList2;
    private int imgExistence = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_board_post_modify);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        checkNetwork();
        getCurrentUserInfo();
        mFindViewById();
        setPhotoRecyclerView();
        setPost();
        setBtn_postModifyAddPhoto();
    }

    private void mFindViewById(){
        et_modifyFreeBoardTitle = findViewById(R.id.et_modifyFreeBoardTitle);
        et_modifyFreeBoardContent = findViewById(R.id.et_modifyFreeBoardContent);
        cb_modifyAnonymity = findViewById(R.id.cb_modifyAnonymity);
        btn_freePostModifyWrite = findViewById(R.id.btn_freePostModifyWrite);
        modifyPhotoRecyclerView =  findViewById(R.id.modifyPhotoRecyclerView);
        btn_postModifyAddPhoto = findViewById(R.id.btn_postModifyAddPhoto);
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
//네트워크 상태 check
    private void checkNetwork(){
        CheckNetwork checkNetwork = new CheckNetwork();

        int netWorkStatus = checkNetwork.CheckNetwork(getApplication());

        if(netWorkStatus == 0){
            Toast.makeText(getApplication(), "인터넷 연결을 확인해 주세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 사진 추가 버튼 설정.
    private void setBtn_postModifyAddPhoto(){
        btn_postModifyAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //사진을 여러개 선택할수 있도록 한다
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),  PICK_FROM_ALBUM);
            }
        });
    }
// 사진 추가 result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                imgExistence = 0;
                modifyPhotoRecyclerView.setVisibility(View.GONE);

                if(clipData.getItemCount() > 10){
                    Toast.makeText(this, "사진은 10장까지만 가능합니다.", Toast.LENGTH_SHORT).show();
                    imgExistence = 0;
                    return;
                }else if(clipData.getItemCount() > 0 && clipData.getItemCount() <= 10){

                    uploadPhotoList = new ArrayList<>();
                    uploadPhotoList2 = new ArrayList<>();
                    for(int i = 0; i < clipData.getItemCount(); i++){
                        uploadPhotoList2.add(clipData.getItemAt(i).getUri());
                    }
                    modifyPhotoRecyclerView.setVisibility(View.VISIBLE);
                    imgExistence = 1;
                }
            }
            Adapter_freeBoardPhoto adapter = new Adapter_freeBoardPhoto(uploadPhotoList2,this,"modify");
            modifyPhotoRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
