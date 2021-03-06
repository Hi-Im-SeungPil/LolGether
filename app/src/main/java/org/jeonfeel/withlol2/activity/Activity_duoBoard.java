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
            Toast.makeText(getApplication(), "????????? ????????? ????????? ?????????!!", Toast.LENGTH_SHORT).show();
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
    private void setDuoBoard() { //????????? ????????? ??????

        ProgressDialog progressDialog = new ProgressDialog(Activity_duoBoard.this);
        progressDialog.setMessage("????????? ????????? ???????????? ????????????.");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();

        mDatabase.child(selectedPosition) // ????????? ?????? & ????????????
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
                    tv_noItem.setVisibility(View.VISIBLE);
                    tv_noItem.setText("???????????? ????????????. \n\n??? ???????????? ????????? ?????????!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        progressDialog.dismiss();
}
    private void loadNextData(){

        ProgressDialog progressDialog = new ProgressDialog(Activity_duoBoard.this);
        progressDialog.setMessage("????????? ????????? ???????????? ????????????.");
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
        final RecyclerView duoBoardRecyclerView = (RecyclerView) findViewById(R.id.duoBoardRecyclerView); // ????????? ????????? ??????
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
                }else{ //????????? ????????????
                    Toast.makeText(Activity_duoBoard.this, "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        duoBoardRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // ????????????????????? ?????? ????????????
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                                                                    .findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    loadNextData(); // ?????? ????????? ????????????
                }
            }
        });
    }

    private void getBoardChild(){
        switch (boardTitle){
            case "???????????? ??????" :
                boardChild = "solounranked";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ?????????" :
                boardChild = "soloiron";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ?????????" :
                boardChild = "solobronze";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ??????" :
                boardChild = "solosilver";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ??????" :
                boardChild = "sologold";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ????????????" :
                boardChild = "soloplatinum";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ?????????" :
                boardChild = "solodiamond";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ??????" :
                boardChild = "freeunranked";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ?????????" :
                boardChild = "freeiron";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ?????????" :
                boardChild = "freebronze";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ??????" :
                boardChild = "freesilver";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ??????" :
                boardChild = "freegold";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ????????????" :
                boardChild = "freeplatinum";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ?????????" :
                boardChild = "freediamond";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ?????????" :
                boardChild = "freemaster";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ??????" :
                boardChild = "freegrandmaster";
                selectedPosition = "?????? ?????????";
                break;
            case "???????????? ?????????" :
                boardChild = "freechallenger";
                selectedPosition = "?????? ?????????";
                break;
            case "?????? / ?????? ??????" :
                boardChild = "normalgame";
                selectedPosition = "???????????????????????????";
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

        //????????? ?????? ?????????
        if(boardTitle.equals("?????? / ?????? ??????")){
            positionItems = getResources().getStringArray(R.array.normalgame_array);
            Adapter_spinner positionAdapter = new Adapter_spinner(this,positionItems);
            spi_duoBoardPositionFilter.setAdapter(positionAdapter);
            spi_duoBoardPositionFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (startCheck != 0) {
                        selectedPosition = parent.getSelectedItem().toString()+"???????????????";
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

