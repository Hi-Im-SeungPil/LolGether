package org.jeonfeel.withlol2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jeonfeel.withlol2.DTO.SaveComment;
import org.jeonfeel.withlol2.DTO.SendNotification;
import org.jeonfeel.withlol2.DTO.User;
import org.jeonfeel.withlol2.MainActivity;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.adapter.Adapter_freeBoardComment;
import org.jeonfeel.withlol2.etc.CheckNetwork;
import org.jeonfeel.withlol2.etc.Item_comment;

import java.util.ArrayList;

public class Activity_watchingFreeBoardPost extends AppCompatActivity {

    public static Activity activity;

    private Button btn_freeBoardWrittenBackspace,btn_freeBoardPostRefresh,btn_freeBoardWriterRecord,btn_freeBoardWriteComment,btn_freeBoardPostPopUp;
    private TextView tv_freeBoardTitle,tv_freeBoardContent,tv_freeBoardWrittenDate,tv_freeBoardSummonerName;
    private EditText et_freeBoardWriteComment;
    private String postId,writerUid,postTitle,postContent,summonerTier,summonerName;
    private int commentCount,imgExistence = 0;
    private long postDate;
    private ImageView iv_freeBoardSummonerTier;
    private CheckBox cb_commentAnonymity;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String currentSummonerName,currentSummonerTier,currentUserUid;
    private Adapter_freeBoardComment adapter;
    private String pagingPostId,lastKey;
    private ArrayList<Item_comment> mItem,sampleItem;
    private NestedScrollView mNestedScrollView;
    private ArrayList<Uri> photoList;
    private ArrayList<String> photoListName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching_freeboard_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sampleItem = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        CheckNetwork checkNetwork = new CheckNetwork();
        int netWorkStatus = checkNetwork.CheckNetwork(Activity_watchingFreeBoardPost.this);

        if(netWorkStatus == 0){
            Toast.makeText(Activity_watchingFreeBoardPost.this, "인터넷 연결을 확인해 주세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        getCurrentUserInfo();

        mFindViewById();

        getPostInfo();
        getLastKey();
        setCommentRecyclerView();

        setPhoto();
        setPost();
        setComment();
        setBtn_writeComment();
        setBtn_writtenRefresh();
        setBtn_postPopUp();
        setBtn_freeBoardWriterRecord();
        btn_freeBoardWrittenBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void mFindViewById(){
        btn_freeBoardWrittenBackspace = findViewById(R.id.btn_freeBoardWrittenBackspace);
        btn_freeBoardPostRefresh = findViewById(R.id.btn_freeBoardPostRefresh);
        btn_freeBoardWriterRecord = findViewById(R.id.btn_freeBoardWriterRecord);
        btn_freeBoardWriteComment = findViewById(R.id.btn_freeBoardWriteComment);
        tv_freeBoardTitle = findViewById(R.id.tv_freeBoardTitle);
        et_freeBoardWriteComment = findViewById(R.id.et_freeBoardWriteComment);
        iv_freeBoardSummonerTier = findViewById(R.id.iv_freeBoardSummonerTier);
        tv_freeBoardWrittenDate = findViewById(R.id.tv_freeBoardWrittenDate);
        tv_freeBoardSummonerName = findViewById(R.id.tv_freeBoardSummonerName);
        tv_freeBoardContent = findViewById(R.id.tv_freeBoardContent);
        mNestedScrollView = findViewById(R.id.scroll);
        btn_freeBoardPostPopUp = findViewById(R.id.btn_freeBoardPostPopUp);
        cb_commentAnonymity = findViewById(R.id.cb_commentAnonymity);
    }

    private void getPostInfo(){

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        writerUid = intent.getStringExtra("writerUid");
        postTitle = intent.getStringExtra("postTitle");
        postContent = intent.getStringExtra("postContent");
        summonerTier = intent.getStringExtra("summonerTier");
        summonerName = intent.getStringExtra("summonerName");
        postDate = intent.getLongExtra("postDate",0);
        commentCount = intent.getIntExtra("commentCount",0);
        imgExistence = intent.getIntExtra("imgExistence",0);

    }

    private void setPhoto(){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://lolgether.appspot.com").child(postId);
        photoList = new ArrayList<>();
        photoListName = new ArrayList<>();

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {

                    LinearLayout Linear_insertImageView = findViewById(R.id.Linear_insertImageView);

                    ImageView iv = new ImageView(Activity_watchingFreeBoardPost.this);
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT,1000);

                    params.setMargins(50,20,50,20);

                    Linear_insertImageView.addView(iv,params);

                    photoListName.add(item.getName());

                    item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                Glide.with(Activity_watchingFreeBoardPost.this).load(task.getResult()).into(iv);
                                photoList.add(task.getResult());
                            }else{
                                Toast.makeText(Activity_watchingFreeBoardPost.this, "이미지를 불러오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void setPost(){
        tv_freeBoardSummonerName.setText(summonerName);
        tv_freeBoardTitle.setText(postTitle);
        tv_freeBoardContent.setText(postContent);

        final int SEC = 60;
        final int MIN = 60;
        final int HOUR = 24;
        final int DAY = 30;
        final int MONTH = 12;

        long diffTime = (System.currentTimeMillis() -  postDate) / 1000;
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

        tv_freeBoardWrittenDate.setText(dateText);

        switch (summonerTier){
            case "anonymity":
                iv_freeBoardSummonerTier.setImageResource(R.drawable.img_bee);
                break;
            case "UnRanked":
                iv_freeBoardSummonerTier.setImageResource(R.drawable.unranked);
                break;
            case "IRON":
                iv_freeBoardSummonerTier.setImageResource(R.drawable.iron);
                break;
            case "BRONZE":
                iv_freeBoardSummonerTier.setImageResource(R.drawable.bronze);
                break;
            case "SILVER":
                iv_freeBoardSummonerTier.setImageResource(R.drawable.silver);
                break;
            case "GOLD":
                iv_freeBoardSummonerTier.setImageResource(R.drawable.gold);
                break;
            case "PLATINUM":
                iv_freeBoardSummonerTier.setImageResource(R.drawable.ple);
                break;
            case "DIAMOND":
                iv_freeBoardSummonerTier.setImageResource(R.drawable.dia);
                break;
            case "MASTER" :
                iv_freeBoardSummonerTier.setImageResource(R.drawable.master);
                break;
            case "GRANDMASTER" :
                iv_freeBoardSummonerTier.setImageResource(R.drawable.gm);
                break;
            case "CHALLENGER" :
                iv_freeBoardSummonerTier.setImageResource(R.drawable.ch);
                break;
        }
    }

    private void setCommentRecyclerView() {
        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.commentRecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        commentRecyclerView.setLayoutManager(mLinearLayoutManager);

        mItem = new ArrayList<>();

        adapter = new Adapter_freeBoardComment(mItem,currentUserUid,mDatabase,postId,commentCount,Activity_watchingFreeBoardPost.this);

        commentRecyclerView.setAdapter(adapter);

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY > oldScrollY) {
                        if (scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) {
                            loadNextData();
                        }
                    }
                }
            }
        });
    }

    private void setBtn_writeComment(){

        btn_freeBoardWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentId = mDatabase.child("freeBoardComment").push().getKey();
                String commentContent = et_freeBoardWriteComment.getText().toString();
                if (commentContent.length() == 0) {
                    Toast.makeText(Activity_watchingFreeBoardPost.this, "내용을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    long commentDate = System.currentTimeMillis();

                    if (cb_commentAnonymity.isChecked()) {
                        currentSummonerName = currentUserUid.substring(0, 6);
                        currentSummonerTier = "anonymity";
                    }else if(!cb_commentAnonymity.isChecked()){
                        MainActivity mainActivity = new MainActivity();
                        currentSummonerName = mainActivity.getCurrentSummonerName();
                        currentSummonerTier = mainActivity.getCurrentSummonerTier();
                    }

                    SaveComment saveComment = new SaveComment(commentId, currentSummonerTier, currentSummonerName,
                            commentContent, currentUserUid, commentDate);

                    mDatabase.child("freeBoardComment").child(postId).child(commentId).setValue(saveComment);

                    mDatabase.child("freeBoard")
                            .child(postId)
                            .child("commentCount").setValue(mItem.size());

                    mDatabase.child("users").child(writerUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String token = snapshot.child("token").getValue(String.class);
                            SendNotification.sendNotification(token, postTitle, "새로운 댓글이 달렸습니다.");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    Item_comment Item = new Item_comment(postId, currentSummonerTier,
                            currentSummonerName, commentContent, currentUserUid, commentDate);
                    mItem.add(Item);
                    adapter.notifyDataSetChanged();

                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(btn_freeBoardWriteComment.getWindowToken(), 0);
                    et_freeBoardWriteComment.setText("");
                }
            }
        });
    }

    private void setComment(){
        mDatabase.child("freeBoardComment")
                .child(postId)
                .orderByChild("id")
                .limitToFirst(7).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(User.class) != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        SaveComment saveComment = userSnapshot.getValue(SaveComment.class);
                        String _id = saveComment.get_id();
                        String commentSummonerTier = saveComment.getSummonerTier();
                        String commentSummonerName = saveComment.getSummonerName();
                        String commentContent = saveComment.getCommentContent();
                        String Uid = saveComment.getUid();
                        long commentDate = saveComment.getCommentDate();
                        pagingPostId = _id;

                        Item_comment Item = new Item_comment(_id,commentSummonerTier,commentSummonerName,
                                commentContent,Uid,commentDate);
                        mItem.add(Item);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_watchingFreeBoardPost.this, "데이터 로드에 실패했습니다. 새로고침 해주세요!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void getCurrentUserInfo(){
        MainActivity mainActivity = new MainActivity();
        currentSummonerName = mainActivity.getCurrentSummonerName();
        currentSummonerTier = mainActivity.getCurrentSummonerTier();
        currentUserUid = mainActivity.getCurrentUserUid();
    }

    public void loadNextData(){
        mDatabase.child("freeBoardComment")
                .child(postId)
                .orderByChild("_id")
                .startAt(pagingPostId)
                .limitToFirst(7)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()) {
                            if(pagingPostId.equals(lastKey)){
                                return;
                            }
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                SaveComment saveComment = dataSnapshot.getValue(SaveComment.class);
                                String _id = saveComment.get_id();
                                String commentSummonerTier = saveComment.getSummonerTier();
                                String commentSummonerName = saveComment.getSummonerName();
                                String commentContent = saveComment.getCommentContent();
                                String Uid = saveComment.getUid();
                                long commentDate = saveComment.getCommentDate();
                                pagingPostId = _id;

                                Item_comment Item = new Item_comment(_id,commentSummonerTier,commentSummonerName,
                                        commentContent,Uid,commentDate);

                                sampleItem.add(Item);
                            }
                        }
                        for(int i = 1; i < sampleItem.size(); i++){
                            mItem.add(sampleItem.get(i));
                        }
                        sampleItem.clear();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public void getLastKey(){
        mDatabase.child("freeBoardComment")
                .child(postId)
                .orderByChild("_id")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot s : snapshot.getChildren()) {
                                lastKey = s.child("_id").getValue(String.class);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void setBtn_postPopUp(){

        btn_freeBoardPostPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(writerUid.equals(currentUserUid)) {

                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                    getMenuInflater().inflate(R.menu.free_board_post_writer_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.btn_postRetouch) {
                        Intent intent = new Intent(getApplication(), Activity_freeBoardPostRetouch.class);

                        intent.putExtra("postId", postId);
                        intent.putExtra("postTitle", postTitle);
                        intent.putExtra("postContent", postContent);
                        intent.putExtra("summonerName",summonerName);
                        intent.putExtra("imgExistence",imgExistence);

                        startActivity(intent);

                            } else if (item.getItemId() == R.id.btn_postDel) {
                                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(Activity_watchingFreeBoardPost.this)
                                        .setMessage(
                                                "삭제 하시겠습니까?")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                mDatabase.child("freeBoard")
                                                        .child(postId).removeValue();
                                                mDatabase.child("freeBoardComment")
                                                        .child(postId).removeValue();
                                                mDatabase.child("users").child(currentUserUid).child("duoBoardPost").child(postId).removeValue();

                                                if(imgExistence == 1){

                                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                                    StorageReference storageRef = storage.getReferenceFromUrl("gs://lolgether.appspot.com").child(postId);

                                                    storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                                        @Override
                                                        public void onSuccess(ListResult listResult) {
                                                            for(StorageReference item : listResult.getItems()){
                                                                item.delete();
                                                            }
                                                        }
                                                    });
                                                }

                                                Activity_freeBoard ac = (Activity_freeBoard) Activity_freeBoard.activity;
                                                ac.finish();

                                                Intent intent = new Intent(Activity_watchingFreeBoardPost.this, Activity_freeBoard.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        });
                                AlertDialog msgDlg = msgBuilder.create();
                                msgDlg.show();
                            }else if(item.getItemId() == R.id.btn_imageRetouch){
                                Intent intent = new Intent(Activity_watchingFreeBoardPost.this,Activity_freeBoardImageRetouch.class);
                                intent.putExtra("postId",postId);
                                startActivity(intent);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }else{
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                    getMenuInflater().inflate(R.menu.post_normal_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.btn_postReport) {

                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            }
        });
    }
    private void setBtn_writtenRefresh(){
        btn_freeBoardPostRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Activity_watchingFreeBoardPost.class);

                intent.putExtra("postId",postId);
                intent.putExtra("writerUid",writerUid);
                intent.putExtra("postTitle",postTitle);
                intent.putExtra("postContent",postContent);
                intent.putExtra("summonerTier",summonerTier);
                intent.putExtra("summonerName",summonerName);
                intent.putExtra("postDate",postDate);
                intent.getIntExtra("commentCount",0);

                finish();
                startActivity(intent);
            }
        });

    }
    private void setBtn_freeBoardWriterRecord(){

        if (summonerName.equals("롤게더 익명")){
            btn_freeBoardWriterRecord.setVisibility(View.INVISIBLE);
        }else {
            btn_freeBoardWriterRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                    getMenuInflater().inflate(R.menu.writer_record, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.btn_watchingWriterRecord){
                                String summonerName = tv_freeBoardSummonerName.getText().toString();
                                Intent intent = new Intent(getApplication(), Activity_searchingSummoner.class);
                                intent.putExtra("userId", summonerName);
                                startActivity(intent);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

}
