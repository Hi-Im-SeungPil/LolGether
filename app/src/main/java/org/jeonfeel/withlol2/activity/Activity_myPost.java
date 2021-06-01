package org.jeonfeel.withlol2.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jeonfeel.withlol2.adapter.Adapter_duoBoard;
import org.jeonfeel.withlol2.etc.DuoBoardItemClickListener;
import org.jeonfeel.withlol2.etc.Item_duoBoard;
import org.jeonfeel.withlol2.MainActivity;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.DTO.SaveDuoBoardPost;
import org.jeonfeel.withlol2.DTO.SaveMyPost;
import org.jeonfeel.withlol2.DTO.User;

import java.util.ArrayList;

public class Activity_myPost extends AppCompatActivity {

    private Button btn_myPostBackspace;
    private ToggleButton tog_myPostDuoBoard,tog_myPostFreeBoard;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,mmDatabase;

    private ArrayList<Item_duoBoard> mItem,sampleItem;
    private Adapter_duoBoard adapter;

    private String _id, summonerTier, writtenTitle, writtenContent, summonerName, writtenTier, writtenUid, selectedMic;
    private int commentCount;
    private long postDate;
    private String boardTitle,selectedPosition,currentUserUid;

    private String pagingWrittenId = "", lastKey = "";

    private String postId,postBoardChild,postSelectedPosition;

    private int check = 1;
    private int recyclerViewCheck = 0;

    private SaveMyPost saveMyPost;
    private SaveDuoBoardPost saveDuoBoardPost;

    private ArrayList<String> ArrayPostId;
    private ArrayList<String> ArrayPostBoardChild;
    private ArrayList<String> ArrayPostSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        MainActivity mainActivity = new MainActivity();
        currentUserUid = mainActivity.getCurrentUserUid();

        ArrayPostId = new ArrayList<>();
        ArrayPostBoardChild = new ArrayList<>();
        ArrayPostSelectedPosition = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("duoBoard");
        mmDatabase = FirebaseDatabase.getInstance().getReference("users/" + currentUserUid + "/duoBoardPost");

        mFindViewById();

        getLastKey();

        setRecyclerView();
        getMyPost();
        tog_myPostDuoBoard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTog_myPostDuoBoard(isChecked);
            }
        });
        tog_myPostFreeBoard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTog_myPostFreeBoard(isChecked);
            }
        });
    }
    public void mFindViewById(){
        btn_myPostBackspace = findViewById(R.id.btn_myPostBackspace);
        tog_myPostFreeBoard = findViewById(R.id.tog_myPostFreeBoard);
        tog_myPostDuoBoard = findViewById(R.id.tog_myPostDuoBoard);

        tog_myPostDuoBoard.setChecked(true);
        tog_myPostFreeBoard.setChecked(false);
    }

    public void getMyPost(){

        mmDatabase.orderByChild("postId")
                .limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(User.class) != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        if(pagingWrittenId.isEmpty()){
                            pagingWrittenId = userSnapshot.child("postId").getValue(String.class);
                        }
                        saveMyPost = userSnapshot.getValue(SaveMyPost.class);

                        postId = saveMyPost.getPostId();
                        postBoardChild = saveMyPost.getPostBoardChild();
                        postSelectedPosition = saveMyPost.getPostSelectedPosition();

                        ArrayPostId.add(0,postId);
                        ArrayPostBoardChild.add(0,postBoardChild);
                        ArrayPostSelectedPosition.add(0,postSelectedPosition);
                    }
                }
                setMyPost();
                ArrayPostId.clear();
                ArrayPostBoardChild.clear();
                ArrayPostSelectedPosition.clear();
                recyclerViewCheck++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void setMyPost(){

        for(int i = 0; i < ArrayPostId.size(); i++){
            selectedPosition = ArrayPostSelectedPosition.get(i);

            mDatabase.child(ArrayPostBoardChild.get(i))
                    .child(selectedPosition)
                    .orderByKey()
                    .equalTo(ArrayPostId.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(User.class) != null) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
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
                            selectedMic = "off";
                            boardTitle = saveDuoBoardPost.getBoardTitle();

                            Item_duoBoard Item = new Item_duoBoard(_id, writtenUid, summonerTier, writtenTier, writtenTitle, writtenContent
                                    , summonerName, selectedMic,selectedPosition,boardTitle,
                                    commentCount,postDate);

                            mItem.add(Item);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public void setRecyclerView(){
        final RecyclerView myPostRecyclerView = (RecyclerView) findViewById(R.id.myPostRecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        myPostRecyclerView.setLayoutManager(mLinearLayoutManager);

        mItem = new ArrayList<>();
        sampleItem = new ArrayList<>();

        adapter = new Adapter_duoBoard(mItem,this);
        myPostRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new DuoBoardItemClickListener() {
            @Override
            public void onItemClick(Adapter_duoBoard.CustomViewHolder holder, View view, int position) {
                Item_duoBoard item = adapter.getItem(position);

                if(item != null) {
                    Intent intent = new Intent(getApplication(), Activity_watchingDuoBoardPost.class);
                    intent.putExtra("writtenId", item.get_id());
                    intent.putExtra("writtenUid", item.getUid());
                    intent.putExtra("writtenTitle", item.getTitle());
                    intent.putExtra("writtenContent", item.getContent());
                    intent.putExtra("summonerTier", item.getTier());
                    intent.putExtra("summonerName", item.getSummonerName());
                    intent.putExtra("postDate",postDate);
                    intent.putExtra("boardTitle", item.getBoardTitle());
                    intent.putExtra("itemPosition", position);
                    intent.putExtra("commentCount", item.getCommentCount());
                    intent.putExtra("selectedPosition",item.getSelectedPosition());
                    startActivity(intent);
                }
            }
        });

        myPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount && recyclerViewCheck != 0) {
                    loadNextData();
                }
            }
        });
    }
    public void loadNextData(){

        mmDatabase.orderByChild("postId")
                .endAt(pagingWrittenId)
                .limitToLast(11)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()) {
                            if(pagingWrittenId.equals(lastKey)){
                                return;
                            }
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                saveMyPost = dataSnapshot.getValue(SaveMyPost.class);
                                _id = saveMyPost.getPostId();

                                if(check == 1){
                                    pagingWrittenId = _id;
                                }

                                postId = saveMyPost.getPostId();
                                postBoardChild = saveMyPost.getPostBoardChild();
                                postSelectedPosition = saveMyPost.getPostSelectedPosition();

                                ArrayPostId.add(0,postId);
                                ArrayPostBoardChild.add(0,postBoardChild);
                                ArrayPostSelectedPosition.add(0,postSelectedPosition);
                                check++;

                                if(check == 12){
                                    check = 1;
                                }
                            }
                        }
                        loadNextPost();
                        ArrayPostId.clear();
                        ArrayPostBoardChild.clear();
                        ArrayPostSelectedPosition.clear();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public void getLastKey(){
        mmDatabase.limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot s : snapshot.getChildren()) {
                                lastKey = s.child("postId").getValue(String.class);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public void loadNextPost(){
        for(int i = 1; i < ArrayPostId.size(); i++){

            selectedPosition = ArrayPostSelectedPosition.get(i);

            mDatabase.child(ArrayPostBoardChild.get(i))
                    .child(selectedPosition)
                    .orderByKey()
                    .equalTo(ArrayPostId.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(User.class) != null) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
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
                            selectedMic = "off";
                            boardTitle = saveDuoBoardPost.getBoardTitle();

                            Item_duoBoard Item = new Item_duoBoard(_id, writtenUid, summonerTier, writtenTier, writtenTitle, writtenContent
                                    , summonerName, selectedMic,selectedPosition,boardTitle,
                                    commentCount,postDate);

                            mItem.add(Item);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void setTog_myPostDuoBoard(boolean isChecked){

        if(isChecked){
            tog_myPostFreeBoard.setChecked(false);
            tog_myPostDuoBoard.setBackground(ContextCompat.getDrawable(this,R.drawable.mypost_tog));
            tog_myPostDuoBoard.setTextColor(Color.parseColor("#FF000000"));
            tog_myPostDuoBoard.setClickable(false);
        }else{
            tog_myPostDuoBoard.setBackgroundColor(Color.WHITE);
            tog_myPostDuoBoard.setTextColor(Color.parseColor("#D8D8D8"));
            tog_myPostDuoBoard.setClickable(true);
        }
    }

    private void setTog_myPostFreeBoard(boolean isChecked){
        if(isChecked){
            tog_myPostDuoBoard.setChecked(false);
            tog_myPostFreeBoard.setBackground(ContextCompat.getDrawable(this,R.drawable.mypost_tog));
            tog_myPostFreeBoard.setTextColor(Color.parseColor("#FF000000"));
            tog_myPostFreeBoard.setClickable(false);
        }else{
            tog_myPostFreeBoard.setBackgroundColor(Color.WHITE);
            tog_myPostFreeBoard.setTextColor(Color.parseColor("#D8D8D8"));
            tog_myPostFreeBoard.setClickable(true);
        }
    }
}
