package org.jeonfeel.withlol2.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jeonfeel.withlol2.adapter.Adapter_duoBoard;
import org.jeonfeel.withlol2.adapter.Adapter_spinner;
import org.jeonfeel.withlol2.etc.CheckNetwork;
import org.jeonfeel.withlol2.etc.DuoBoardItemClickListener;
import org.jeonfeel.withlol2.etc.Item_duoBoard;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.DTO.SaveDuoBoardPost;
import org.jeonfeel.withlol2.DTO.User;

import java.util.ArrayList;

public class Activity_duoBoard extends AppCompatActivity {

    public static Activity activity;

    private ArrayList<Item_duoBoard> mItem,sampleItem;
    private Adapter_duoBoard adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private SaveDuoBoardPost saveDuoBoardPost;

    private String _id, summonerTier, writtenTitle, writtenContent, summonerName, writtenTier, writtenUid;
    private int commentCount;

    private String pagingPostId = "", lastKey = "";
    private int check = 1;
    private int startCheck = 0;

    private FloatingActionButton fab_duoBoardWrite;

    private String boardTitle="",boardChild = "";

    private TextView tv_boardTitle,tv_noItem;
    private ImageView iv_duoBoardBackspace;
    private Button btn_duoBoardRefresh;

    private Spinner spi_duoBoardPositionFilter;
    private String[] positionItems;
    private String selectedPosition ,selectedMic;

    private long postDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duoboard);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        CheckNetwork checkNetwork = new CheckNetwork();
        int netWorkStatus = checkNetwork.CheckNetwork(getApplication());

        if(netWorkStatus == 0){
            Toast.makeText(getApplication(), "인터넷 연결을 확인해 주세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        Intent intent = getIntent();
        boardTitle = intent.getStringExtra("boardTitle");

        getBoardChild();

        fab_duoBoardWrite = findViewById(R.id.fab_duoBoardWrite);
        tv_boardTitle = findViewById(R.id.tv_boardTitle);
        iv_duoBoardBackspace = findViewById(R.id.iv_duoBoardBackspace);
        btn_duoBoardRefresh = findViewById(R.id.btn_duoBoardRefresh);
        spi_duoBoardPositionFilter = findViewById(R.id.spi_duoBoardPositionFilter);
        tv_noItem = findViewById(R.id.tv_noItem);

        tv_boardTitle.setText(boardTitle);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("duoBoard/"+boardChild);

        sampleItem = new ArrayList<>();

        setSpinner();
        setRecyclerView();
        setDuoBoard();

        activity = Activity_duoBoard.this;

        fab_duoBoardWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFab_duoBoardWrite();
            }
        });
        iv_duoBoardBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_duoBoardRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { setBtn_duoBoardRefresh();
            }
        });
    }
    private void setDuoBoard() { //게시판 게시물 세팅

        ProgressDialog progressDialog = new ProgressDialog(Activity_duoBoard.this);
        progressDialog.setMessage("소환사 정보를 받아오고 있습니다.");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();

        mDatabase.child(selectedPosition) // 페이징 처리 & 불러오기
                .orderByChild("id")
                .limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(User.class) != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            if(pagingPostId.isEmpty()){
                                pagingPostId = userSnapshot.child("id").getValue(String.class);
                            }

                            saveDuoBoardPost = userSnapshot.getValue(SaveDuoBoardPost.class);
                            _id = saveDuoBoardPost.getId();
                            summonerTier = saveDuoBoardPost.getSummonerTier();
                            writtenTier = saveDuoBoardPost.getPostTier();
                            writtenTitle = saveDuoBoardPost.getTitle();
                            writtenContent = saveDuoBoardPost.getContent();
                            summonerName = saveDuoBoardPost.getSummonerName();
                            postDate = saveDuoBoardPost.getPostDate();
                            commentCount = saveDuoBoardPost.getCommentCount();
                            writtenUid = saveDuoBoardPost.getUid();
                            selectedMic = saveDuoBoardPost.getSelectedMic();

                            Item_duoBoard Item = new Item_duoBoard(_id, writtenUid, summonerTier, writtenTier, writtenTitle, writtenContent
                                    , summonerName, selectedMic,selectedPosition,boardTitle,
                                    commentCount,postDate);
                            sampleItem.add(0,Item);
                    }
                }
                mItem.addAll(sampleItem);
                adapter.notifyDataSetChanged();
                sampleItem.clear();
                if(mItem.size() > 0){
                    tv_noItem.setVisibility(View.GONE);
                }else{
                    tv_noItem.setText("게시물이 없습니다. \n\n첫 게시물을 작성해 보세요!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        progressDialog.dismiss();
}
    private void loadNextData(){

        ProgressDialog progressDialog = new ProgressDialog(Activity_duoBoard.this);
        progressDialog.setMessage("소환사 정보를 받아오고 있습니다.");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();

        mDatabase.child(selectedPosition)
                .orderByChild("id")
                .endAt(pagingPostId)
                .limitToLast(11)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    if(pagingPostId.equals(lastKey)){
                        return;
                    }
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        saveDuoBoardPost = dataSnapshot.getValue(SaveDuoBoardPost.class);
                        _id = saveDuoBoardPost.getId();

                        if(check == 1){
                            pagingPostId = _id;
                        }

                        summonerTier = saveDuoBoardPost.getSummonerTier();
                        writtenTier = saveDuoBoardPost.getPostTier();
                        writtenTitle = saveDuoBoardPost.getTitle();
                        writtenContent = saveDuoBoardPost.getContent();
                        summonerName = saveDuoBoardPost.getSummonerName();
                        postDate = saveDuoBoardPost.getPostDate();
                        commentCount = saveDuoBoardPost.getCommentCount();
                        writtenUid = saveDuoBoardPost.getUid();

                        Item_duoBoard Item = new Item_duoBoard(_id, writtenUid, summonerTier, writtenTier, writtenTitle, writtenContent
                                , summonerName, selectedMic,selectedPosition,boardTitle,commentCount,postDate);
                        sampleItem.add(Item);
                        adapter.notifyDataSetChanged();
                        check++;

                        if(check == 12){
                            check = 1;
                        }
                    }
                }
                int index = mItem.size();
                for(int i = 0; i < sampleItem.size() - 1 ; i++){
                    mItem.add(index,sampleItem.get(i));
                }
                sampleItem.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        progressDialog.dismiss();
    }
    private void getLastKey(){
        mDatabase.child(selectedPosition)
                .orderByChild("id")
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        lastKey = s.child("id").getValue(String.class);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void setRecyclerView(){
        final RecyclerView duoBoardRecyclerView = (RecyclerView) findViewById(R.id.duoBoardRecyclerView); // 리사이 클러뷰 세팅
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        duoBoardRecyclerView.setLayoutManager(mLinearLayoutManager);

        mItem = new ArrayList<>();

        adapter = new Adapter_duoBoard(mItem,this);
        duoBoardRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new DuoBoardItemClickListener() {
            @Override
            public void onItemClick(Adapter_duoBoard.CustomViewHolder holder, View view, int position) {
                Item_duoBoard item = adapter.getItem(position);

                if (item != null) {
                    Intent intent = new Intent(getApplication(), Activity_watchingDuoBoardPost.class);
                    intent.putExtra("writtenId", item.get_id());
                    intent.putExtra("writtenUid", item.getUid());
                    intent.putExtra("writtenTitle", item.getTitle());
                    intent.putExtra("writtenContent", item.getContent());
                    intent.putExtra("summonerTier", item.getTier());
                    intent.putExtra("summonerName", item.getSummonerName());
                    intent.putExtra("postDate", postDate);
                    intent.putExtra("boardTitle", boardTitle);
                    intent.putExtra("commentCount", item.getCommentCount());
                    intent.putExtra("selectedPosition", selectedPosition);
                    startActivity(intent);
                }else{ //데이터 받아오기
                    Toast.makeText(Activity_duoBoard.this, "데이터 로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        duoBoardRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // 리사이클러뷰가 끝에 다닿으면
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                                                                    .findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    loadNextData(); // 다음 데이터 불러오기
                }
            }
        });
    }
    private void getBoardChild(){
        switch (boardTitle){
            case "솔로랭크 배치" :
                boardChild = "solounranked";
                selectedPosition = "모든 포지션";
                break;
            case "솔로랭크 아이언" :
                boardChild = "soloiron";
                selectedPosition = "모든 포지션";
                break;
            case "솔로랭크 브론즈" :
                boardChild = "solobronze";
                selectedPosition = "모든 포지션";
                break;
            case "솔로랭크 실버" :
                boardChild = "solosilver";
                selectedPosition = "모든 포지션";
                break;
            case "솔로랭크 골드" :
                boardChild = "sologold";
                selectedPosition = "모든 포지션";
                break;
            case "솔로랭크 플래티넘" :
                boardChild = "soloplatinum";
                selectedPosition = "모든 포지션";
                break;
            case "솔로랭크 다이아" :
                boardChild = "solodiamond";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 배치" :
                boardChild = "freeunranked";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 아이언" :
                boardChild = "freeiron";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 브론즈" :
                boardChild = "freebronze";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 실버" :
                boardChild = "freesilver";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 골드" :
                boardChild = "freegold";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 플래티넘" :
                boardChild = "freeplatinum";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 다이아" :
                boardChild = "freediamond";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 마스터" :
                boardChild = "freemaster";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 그마" :
                boardChild = "freegrandmaster";
                selectedPosition = "모든 포지션";
                break;
            case "자유랭크 챌린저" :
                boardChild = "freechallenger";
                selectedPosition = "모든 포지션";
                break;
            case "일반 / 기타 모드" :
                boardChild = "normalgame";
                selectedPosition = "일반게임같이하실분";
                break;
        }
    }

    private void setFab_duoBoardWrite(){
        Intent intent = new Intent(getApplication(), Activity_writingDuoBoardPost.class);
        intent.putExtra("boardTitle",boardTitle);
        startActivity(intent);
    }
    private void setBtn_duoBoardRefresh(){

        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }
    private void setSpinner(){

        //포지션 선택 스피너
        if(boardTitle.equals("일반 / 기타 모드")){
            positionItems = getResources().getStringArray(R.array.normalgame_array);
            Adapter_spinner positionAdapter = new Adapter_spinner(this,positionItems);
            spi_duoBoardPositionFilter.setAdapter(positionAdapter);
            spi_duoBoardPositionFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (startCheck != 0) {
                        selectedPosition = parent.getSelectedItem().toString()+"같이하실분";
                        mItem.clear();
                        adapter.notifyDataSetChanged();
                        pagingPostId = "";
                        check = 1;
                        getLastKey();
                        setDuoBoard();
                    }
                    startCheck++;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }else {
            positionItems = getResources().getStringArray(R.array.solorank_position_array);
            Adapter_spinner positionAdapter = new Adapter_spinner(this, positionItems);
            spi_duoBoardPositionFilter.setAdapter(positionAdapter);
            spi_duoBoardPositionFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (startCheck != 0) {
                        selectedPosition = parent.getSelectedItem().toString();
                        mItem.clear();
                        adapter.notifyDataSetChanged();
                        pagingPostId = "";
                        check = 1;
                        getLastKey();
                        setDuoBoard();
                    }
                    startCheck++;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }
}

