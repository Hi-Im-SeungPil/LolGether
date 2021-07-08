package org.jeonfeel.withlol2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jeonfeel.withlol2.DTO.PostReport;
import org.jeonfeel.withlol2.adapter.Adapter_duoBoardComment;
import org.jeonfeel.withlol2.etc.CheckNetwork;
import org.jeonfeel.withlol2.etc.Item_comment;
import org.jeonfeel.withlol2.MainActivity;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.DTO.SaveComment;
import org.jeonfeel.withlol2.DTO.SendNotification;
import org.jeonfeel.withlol2.DTO.User;

import java.util.ArrayList;

public class Activity_watchingDuoBoardPost extends AppCompatActivity {

    public static Activity activity;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,mmDatabase;

    private String writtenId,writtenUid,summonerTier,summonerName,writtenTitle,writtenContent,token;
    private int commentCount;
    private long postDate;

    private ImageView img_wdSummonerTier,img_writeComment,iv_duoBoardWrittenBackspace;
    private TextView tv_wdSummonerName,tv_wdWrittenDate,tv_wdTitle,tv_wdContent,tv_boardTitle;
    private EditText et_writeComment;
    private Button btn_writtenRefresh,btn_postPopUp,btn_writerRecord,btn_freeBoardPostPopUp;

    private String currentSummonerName,currentSummonerTier,currentUserUid;
    private int currentUserNotificationStatus;

    private String boardTitle,boardChild,selectedPosition;
    private String pagingPostId="",lastKey="";

    private NestedScrollView mNestedScrollView;

    private ArrayList<Item_comment> mItem,sampleItem;
    private Adapter_duoBoardComment adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching_duoboard_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        CheckNetwork checkNetwork = new CheckNetwork();
        int netWorkStatus = checkNetwork.CheckNetwork(Activity_watchingDuoBoardPost.this);

        if(netWorkStatus == 0){
            Toast.makeText(Activity_watchingDuoBoardPost.this, "인터넷 연결을 확인해 주세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        sampleItem = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mmDatabase = FirebaseDatabase.getInstance().getReference();

        activity = Activity_watchingDuoBoardPost.this;

        mFindViewById();

        getCurrentUserInfo();
        getPostInfo();
        mDatabase = FirebaseDatabase.getInstance().getReference("duoBoard/"+boardChild);
        setWritten();
        getLastKey();
        setRecyclerView();
        setComment();

        img_writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImg_writeComment();
            }
        });
        iv_duoBoardWrittenBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_writtenRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_postRefresh();
            }
        });
        btn_postPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_postPopUp(v);
            }
        });
        btn_writerRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_writerRecord(v);
            }
        });
    }

    private void mFindViewById(){

        img_wdSummonerTier = findViewById(R.id.img_wdSummonerTier);
        img_writeComment = findViewById(R.id.img_writeComment);
        tv_wdSummonerName = findViewById(R.id.tv_wdSummonerName);
        tv_wdWrittenDate = findViewById(R.id.tv_wdWrittenDate);
        tv_wdTitle = findViewById(R.id.tv_wdTitle);
        tv_wdContent = findViewById(R.id.tv_wdContent);
        et_writeComment = findViewById(R.id.et_writeComment);
        tv_boardTitle = findViewById(R.id.tv_boardTitle);
        btn_writtenRefresh = findViewById(R.id.btn_writtenRefresh);
        iv_duoBoardWrittenBackspace = findViewById(R.id.iv_duoBoardWrittenBackspace);
        mNestedScrollView = findViewById(R.id.scroll);
        btn_postPopUp = findViewById(R.id.btn_postPopUp);
        btn_writerRecord = findViewById(R.id.btn_writerRecord);
        btn_freeBoardPostPopUp = findViewById(R.id.btn_freeBoardPostPopUp);

    }

    private void getPostInfo(){

        Intent intent = getIntent();
        writtenId = intent.getStringExtra("writtenId");
        writtenUid = intent.getStringExtra("writtenUid");
        summonerTier = intent.getStringExtra("summonerTier");
        summonerName = intent.getStringExtra("summonerName");
        writtenTitle = intent.getStringExtra("writtenTitle");
        writtenContent = intent.getStringExtra("writtenContent");
        postDate = intent.getLongExtra("postDate",0);
        boardTitle = intent.getStringExtra("boardTitle");
        commentCount = intent.getIntExtra("commentCount",0);
        selectedPosition = intent.getStringExtra("selectedPosition");

        getBoardChild();
        tv_boardTitle.setText(boardTitle);

    }

    private void setBtn_writerRecord(View v){
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
        getMenuInflater().inflate(R.menu.writer_record, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.btn_watchingWriterRecord){
                    String id = tv_wdSummonerName.getText().toString();
                    Intent intent = new Intent(getApplication(), Activity_searchingSummoner.class);
                    intent.putExtra("userId", id);
                    startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void setWritten(){
        tv_wdSummonerName.setText(summonerName);
        tv_wdTitle.setText(writtenTitle);
        tv_wdContent.setText(writtenContent);

        final int SEC = 60;
        final int MIN = 60;
        final int HOUR = 24;
        final int DAY = 30;
        final int MONTH = 12;

        long diffTime = (System.currentTimeMillis() - postDate) / 1000;
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

        tv_wdWrittenDate.setText(dateText);
        switch (summonerTier){
            case "UnRanked":
                img_wdSummonerTier.setImageResource(R.drawable.unranked);
                break;
            case "IRON":
                img_wdSummonerTier.setImageResource(R.drawable.iron);
                break;
            case "BRONZE":
                img_wdSummonerTier.setImageResource(R.drawable.bronze);
                break;
            case "SILVER":
                img_wdSummonerTier.setImageResource(R.drawable.silver);
                break;
            case "GOLD":
                img_wdSummonerTier.setImageResource(R.drawable.gold);
                break;
            case "PLATINUM":
                img_wdSummonerTier.setImageResource(R.drawable.ple);
                break;
            case "DIAMOND":
                img_wdSummonerTier.setImageResource(R.drawable.dia);
                break;
            case "MASTER" :
                img_wdSummonerTier.setImageResource(R.drawable.master);
                break;
            case "GRANDMASTER" :
                img_wdSummonerTier.setImageResource(R.drawable.gm);
                break;
            case "CHALLENGER" :
                img_wdSummonerTier.setImageResource(R.drawable.ch);
                break;
        }
    }
    private void setRecyclerView() {
        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.commentRecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        commentRecyclerView.setLayoutManager(mLinearLayoutManager);

        mItem = new ArrayList<>();

        adapter = new Adapter_duoBoardComment(mItem,currentUserUid,mDatabase,mmDatabase,writtenId,
                boardChild,commentCount,selectedPosition,Activity_watchingDuoBoardPost.this);
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
    private void setImg_writeComment(){

        String commentId = mmDatabase.child("duoBoardComment").push().getKey();
        String commentContent = et_writeComment.getText().toString();
        if(commentContent.length() == 0){
            Toast.makeText(Activity_watchingDuoBoardPost.this, "내용을 입력해 주세요!", Toast.LENGTH_SHORT).show();
        }else {
            long commentDate = System.currentTimeMillis();

            SaveComment saveComment = new SaveComment(commentId, currentSummonerTier, currentSummonerName,
                    commentContent, currentUserUid, commentDate);

            mmDatabase.child("duoBoardComment").child(writtenId).child(commentId).setValue(saveComment);

            Item_comment Item = new Item_comment(writtenId, currentSummonerTier,
                    currentSummonerName, commentContent, currentUserUid, commentDate);
            mItem.add(Item);
            adapter.notifyDataSetChanged();

            mDatabase.child(selectedPosition)
                    .child(writtenId)
                    .child("commentCount").setValue(mItem.size());

            mmDatabase.child("users").child(writtenUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    token = snapshot.child("token").getValue(String.class);
                    Integer checkNotificationStatus = snapshot.child("notification").getValue(int.class);
                    int writerNotificationStatus = 0;
                    if(checkNotificationStatus != null){
                        writerNotificationStatus = checkNotificationStatus;
                    }
                    if(writerNotificationStatus != 0 && !currentUserUid.equals(writtenUid)
                     ) {
                        SendNotification.sendNotification(token, writtenTitle, "새로운 댓글이 달렸습니다.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(img_writeComment.getWindowToken(), 0);
            et_writeComment.setText("");
        }
    }

    private void setComment(){
        mmDatabase.child("duoBoardComment")
                .child(writtenId)
                .orderByChild("_id")
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
                Toast.makeText(Activity_watchingDuoBoardPost.this, "데이터 로드에 실패했습니다. 새로고침 해주세요!", Toast.LENGTH_SHORT).show();
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

    public void getBoardChild(){
        switch (boardTitle){
            case "솔로랭크 배치":
                boardChild = "solounranked";
                break;
            case "솔로랭크 아이언":
                boardChild = "soloiron";
                break;
            case "솔로랭크 브론즈":
                boardChild = "solobronze";
                break;
            case "솔로랭크 실버":
                boardChild = "solosilver";
                break;
            case "솔로랭크 골드":
                boardChild = "sologold";
                break;
            case "솔로랭크 플래티넘":
                boardChild = "soloplatinum";
                break;
            case "솔로랭크 다이아":
                boardChild = "solodiamond";
                break;
            case "자유랭크 배치" :
                boardChild = "freeunranked";
                break;
            case "자유랭크 아이언" :
                boardChild = "freeiron";
                break;
            case "자유랭크 브론즈" :
                boardChild = "freebronze";
                break;
            case "자유랭크 실버" :
                boardChild = "freesilver";
                break;
            case "자유랭크 골드" :
                boardChild = "freegold";
                break;
            case "자유랭크 플래티넘" :
                boardChild = "freeplatinum";
                break;
            case "자유랭크 다이아" :
                boardChild = "freediamond";
                break;
            case "자유랭크 마스터" :
                boardChild = "freemaster";
                break;
            case "자유랭크 그마" :
                boardChild = "freegrandmaster";
                break;
            case "자유랭크 챌린저" :
                boardChild = "freechallenger";
                break;
            case "일반 / 기타 모드" :
                boardChild = "nomalgame";
                selectedPosition = "같이하실분";
                break;
        }
    }
    private void setBtn_postPopUp(View v){

        if(writtenUid.equals(currentUserUid) || currentUserUid.equals("OS8uQWFjckZI7pFJJGvmynBdQVK2")) {

            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
            getMenuInflater().inflate(R.menu.post_writer_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.btn_postRetouch) {
                        Intent intent = new Intent(getApplication(), Activity_duoBoardPostRetouch.class);

                        intent.putExtra("writtenId", writtenId);
                        intent.putExtra("writtenTitle", writtenTitle);
                        intent.putExtra("writtenContent", writtenContent);
                        intent.putExtra("boardChild", boardChild);
                        intent.putExtra("boardTitle", boardTitle);
                        intent.putExtra("selectedPosition",selectedPosition);

                        startActivity(intent);
                    } else if (item.getItemId() == R.id.btn_postDel) {

                        String Uid = getIntent().getStringExtra("writtenUid");

                        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(Activity_watchingDuoBoardPost.this)
                                .setMessage(
                                        "삭제 하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mDatabase.child(selectedPosition)
                                                .child(writtenId).removeValue();
                                        mDatabase.child("duoBoardComment")
                                                .child(writtenId).removeValue();
                                        mmDatabase.child("users").child(Uid).child("duoBoardPost").child(writtenId).removeValue();
                                        Activity_duoBoard ac = (Activity_duoBoard) Activity_duoBoard.activity;
                                        ac.finish();

                                        Intent intent = new Intent(Activity_watchingDuoBoardPost.this, Activity_duoBoard.class);
                                        intent.putExtra("boardTitle", boardTitle);
                                        finish();
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

                        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(Activity_watchingDuoBoardPost.this)
                                .setMessage("신고 하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String uid = currentUserUid;
                                        String title = writtenTitle;
                                        String content = writtenContent;
                                        String postid = writtenId;
                                        PostReport postReport = new PostReport(uid,title,content,postid);
                                        mmDatabase.child("duoBoardReport").child(postid).setValue(postReport).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Activity_watchingDuoBoardPost.this, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        AlertDialog msgDlg = msgBuilder.create();
                        msgDlg.show();
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }
    private void setBtn_postRefresh(){
        Intent intent = new Intent(this, Activity_watchingDuoBoardPost.class);

        intent.putExtra("writtenId",writtenId);
        intent.putExtra("writtenUid",writtenUid);
        intent.putExtra("writtenTitle",writtenTitle);
        intent.putExtra("writtenContent",writtenContent);
        intent.putExtra("summonerTier",summonerTier);
        intent.putExtra("summonerName",summonerName);
        intent.putExtra("postDate",postDate);
        intent.putExtra("boardTitle",boardTitle);
        intent.putExtra("selectedPosition",selectedPosition);

        finish();
        startActivity(intent);
    }
    private void loadNextData(){
            mmDatabase.child("duoBoardComment")
                    .child(writtenId)
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
    private void getLastKey(){
        mmDatabase.child("duoBoardComment")
                .child(writtenId)
                .orderByChild("_id")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot s : snapshot.getChildren()) {
                                lastKey = s.child("_id").getValue(String.class);
                                Log.d("pagingPostId3",lastKey);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}
