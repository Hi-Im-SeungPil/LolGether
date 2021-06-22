package org.jeonfeel.withlol2.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jeonfeel.withlol2.DTO.SaveFreeBoardPost;
import org.jeonfeel.withlol2.adapter.Adapter_duoBoard;
import org.jeonfeel.withlol2.adapter.Adapter_freeBoard;
import org.jeonfeel.withlol2.etc.DuoBoardItemClickListener;
import org.jeonfeel.withlol2.etc.FreeBoardItemClickListener;
import org.jeonfeel.withlol2.etc.Item_duoBoard;
import org.jeonfeel.withlol2.MainActivity;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.DTO.SaveDuoBoardPost;
import org.jeonfeel.withlol2.DTO.SaveMyPost;
import org.jeonfeel.withlol2.DTO.User;
import org.jeonfeel.withlol2.etc.Item_freeBoard;

import java.util.ArrayList;

public class Activity_myPost extends AppCompatActivity {

    private Button btn_myPostBackspace;
    private ToggleButton tog_myPostDuoBoard,tog_myPostFreeBoard;
    private DatabaseReference mDatabase,mDatabase2, duoPostRef,freePostRef;

    private ArrayList<Item_duoBoard> mItem,sampleItem;
    private ArrayList<Item_freeBoard> freeBoardItem,freeSampleItem;
    private Adapter_duoBoard adapter;
    private Adapter_freeBoard freeAdapter;

    private String _id, summonerTier, writtenTitle, writtenContent, summonerName, writtenTier, writtenUid, selectedMic;
    private int commentCount;
    private long postDate;
    private String boardTitle,selectedPosition,currentUserUid;

    private String pagingDuoPostId = "", pagingFreePostId = "",duolastKey = "", freelastKey = "";

    private String postId,postBoardChild,postSelectedPosition;

    private int duoCheck = 1,freeCheck=1;

    private SaveMyPost saveMyPost;
    private SaveDuoBoardPost saveDuoBoardPost;
    private SaveFreeBoardPost saveFreeBoardPost;

    private ArrayList<String> ArrayPostId;
    private ArrayList<String> ArrayPostBoardChild;
    private ArrayList<String> ArrayPostSelectedPosition;
    private ArrayList<String> ArrayFreePostId;

    RecyclerView myDuoPostRecyclerView,myFreePostRecyclerView;

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
        ArrayFreePostId = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("duoBoard");
        mDatabase2 = FirebaseDatabase.getInstance().getReference("freeBoard");
        duoPostRef = FirebaseDatabase.getInstance().getReference("users/" + currentUserUid + "/duoBoardPost");
        freePostRef = FirebaseDatabase.getInstance().getReference("users/" + currentUserUid+"/freeBoardPost");

        mFindViewById();

        getLastKeys();

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

        duoPostRef.orderByChild("postId")
                .limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(User.class) != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        if(pagingDuoPostId.isEmpty()){
                            pagingDuoPostId = userSnapshot.child("postId").getValue(String.class);
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
                setMyDuoPost();
                ArrayPostId.clear();
                ArrayPostBoardChild.clear();
                ArrayPostSelectedPosition.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        freePostRef.orderByChild("postId")
                .limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(User.class) != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        if(pagingFreePostId.isEmpty()){
                            pagingFreePostId = userSnapshot.child("postId").getValue(String.class);
                        }
                        String postId = userSnapshot.child("postId").getValue(String.class);
                        ArrayFreePostId.add(0,postId);
                    }
                }
                setMyFreePost();
                ArrayFreePostId.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void setMyDuoPost(){

        for(int i = 0; i < ArrayPostId.size(); i++) {
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
                                    , summonerName, selectedMic, selectedPosition, boardTitle,
                                    commentCount, postDate);

                            mItem.add(Item);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    myFreePostRecyclerView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void setMyFreePost(){
        for(int i = 0; i < ArrayFreePostId.size(); i++){

            mDatabase2.orderByKey()
                    .equalTo(ArrayFreePostId.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(User.class) != null) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            saveFreeBoardPost = userSnapshot.getValue(SaveFreeBoardPost.class);

                            String _id = saveFreeBoardPost.getId();
                            String summonerTier = saveFreeBoardPost.getSummonerTier();
                            String writtenTitle = saveFreeBoardPost.getTitle();
                            String writtenContent = saveFreeBoardPost.getContent();
                            String summonerName = saveFreeBoardPost.getSummonerName();
                            long postDate = saveFreeBoardPost.getPostDate();
                            int commentCount = saveFreeBoardPost.getCommentCount();
                            String writtenUid = saveFreeBoardPost.getUid();
                            int imgExistence = saveFreeBoardPost.getImgExist();

                            Item_freeBoard Item = new Item_freeBoard(_id,writtenUid,summonerTier,writtenTitle,writtenContent,summonerName,imgExistence
                                    ,commentCount,postDate);

                            freeBoardItem.add(Item);
                            freeAdapter.notifyDataSetChanged();
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
        myDuoPostRecyclerView = (RecyclerView) findViewById(R.id.myDuoPostRecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        myDuoPostRecyclerView.setLayoutManager(mLinearLayoutManager);

        mItem = new ArrayList<>();
        sampleItem = new ArrayList<>();

        adapter = new Adapter_duoBoard(mItem,this);
        myDuoPostRecyclerView.setAdapter(adapter);

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
                    intent.putExtra("commentCount", item.getCommentCount());
                    intent.putExtra("selectedPosition",item.getSelectedPosition());
                    startActivity(intent);
                }
            }
        });

        myDuoPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount && tog_myPostDuoBoard.isChecked()) {
                    loadDuoNextData();
                }
            }
        });

        myFreePostRecyclerView = (RecyclerView) findViewById(R.id.myFreePostRecyclerView);
        LinearLayoutManager mLinearLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        myFreePostRecyclerView.setLayoutManager(mLinearLayoutManager2);

        freeBoardItem = new ArrayList<>();
        freeSampleItem = new ArrayList<>();

        freeAdapter = new Adapter_freeBoard(freeBoardItem,this);
        myFreePostRecyclerView.setAdapter(freeAdapter);

        freeAdapter.setOnItemClickListener(new FreeBoardItemClickListener() {
            @Override
            public void onItemClick(Adapter_freeBoard.CustomViewHolder holder, View view, int position) {
                Item_freeBoard item = freeAdapter.getItem(position);

                if(item != null) {
                    Intent intent = new Intent(getApplication(), Activity_watchingFreeBoardPost.class);
                    intent.putExtra("postId", item.getId());
                    intent.putExtra("writerUid", item.getWriterUid());
                    intent.putExtra("postTitle", item.getTitle());
                    intent.putExtra("postContent", item.getContent());
                    intent.putExtra("summonerTier", item.getTier());
                    intent.putExtra("summonerName", item.getSummonerName());
                    intent.putExtra("postDate",item.getPostWrittenDate());
                    intent.putExtra("imgExistence", item.getImageExistence());
                    intent.putExtra("commentCount", item.getCommentCount());
                    startActivity(intent);
                }
            }
        });

        myFreePostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount && tog_myPostFreeBoard.isChecked()) {
//                    loadFreeNextData();
                }
            }
        });

    }
    private void loadDuoNextData(){

        duoPostRef.orderByChild("postId")
                .endAt(pagingDuoPostId)
                .limitToLast(11)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()) {
                            if(pagingDuoPostId.equals(duolastKey)){
                                return;
                            }
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                saveMyPost = dataSnapshot.getValue(SaveMyPost.class);
                                _id = saveMyPost.getPostId();

                                if(duoCheck == 1){
                                    pagingDuoPostId = _id;
                                }

                                postId = saveMyPost.getPostId();
                                postBoardChild = saveMyPost.getPostBoardChild();
                                postSelectedPosition = saveMyPost.getPostSelectedPosition();

                                ArrayPostId.add(0,postId);
                                ArrayPostBoardChild.add(0,postBoardChild);
                                ArrayPostSelectedPosition.add(0,postSelectedPosition);
                                duoCheck++;

                                if(duoCheck == 12){
                                    duoCheck = 1;
                                }
                            }
                        }
                        loadDuoNextPost();
                        ArrayPostId.clear();
                        ArrayPostBoardChild.clear();
                        ArrayPostSelectedPosition.clear();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private void getLastKeys(){
        duoPostRef.limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot s : snapshot.getChildren()) {
                                duolastKey = s.child("postId").getValue(String.class);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        freePostRef.limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot s : snapshot.getChildren()) {
                                freelastKey = s.child("postId").getValue(String.class);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }
    private void loadDuoNextPost(){
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

                            String _id = saveDuoBoardPost.getId();
                            String summonerTier = saveDuoBoardPost.getSummonerTier();
                            String writtenTier = saveDuoBoardPost.getPostTier();
                            String writtenTitle = saveDuoBoardPost.getTitle();
                            String writtenContent = saveDuoBoardPost.getContent();
                            String summonerName = saveDuoBoardPost.getSummonerName();
                            long postDate = saveDuoBoardPost.getPostDate();
                            int commentCount = saveDuoBoardPost.getCommentCount();
                            String writtenUid = saveDuoBoardPost.getUid();
                            String selectedMic = "off";
                            String boardTitle = saveDuoBoardPost.getBoardTitle();

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

    private void loadFreeNextData(){

        freePostRef.orderByChild("postId")
                .endAt(pagingDuoPostId)
                .limitToLast(11)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()) {
                            if(pagingDuoPostId.equals(duolastKey)){
                                return;
                            }
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                saveMyPost = dataSnapshot.getValue(SaveMyPost.class);
                                _id = saveMyPost.getPostId();

                                if(duoCheck == 1){
                                    pagingDuoPostId = _id;
                                }

                                postId = saveMyPost.getPostId();
                                postBoardChild = saveMyPost.getPostBoardChild();
                                postSelectedPosition = saveMyPost.getPostSelectedPosition();

                                ArrayPostId.add(0,postId);
                                ArrayPostBoardChild.add(0,postBoardChild);
                                ArrayPostSelectedPosition.add(0,postSelectedPosition);
                                duoCheck++;

                                if(duoCheck == 12){
                                    duoCheck = 1;
                                }
                            }
                        }
                        loadDuoNextPost();
                        ArrayPostId.clear();
                        ArrayPostBoardChild.clear();
                        ArrayPostSelectedPosition.clear();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void setTog_myPostDuoBoard(boolean isChecked){

        if(isChecked){
            tog_myPostFreeBoard.setChecked(false);
            tog_myPostDuoBoard.setBackground(ContextCompat.getDrawable(this,R.drawable.mypost_tog));
            tog_myPostDuoBoard.setTextColor(Color.parseColor("#FF000000"));
            tog_myPostDuoBoard.setClickable(false);
            myDuoPostRecyclerView.setVisibility(View.VISIBLE);
        }else{
            tog_myPostDuoBoard.setBackgroundColor(Color.WHITE);
            tog_myPostDuoBoard.setTextColor(Color.parseColor("#D8D8D8"));
            tog_myPostDuoBoard.setClickable(true);
            myDuoPostRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private void setTog_myPostFreeBoard(boolean isChecked){

        if(isChecked){
            tog_myPostDuoBoard.setChecked(false);
            tog_myPostFreeBoard.setBackground(ContextCompat.getDrawable(this,R.drawable.mypost_tog));
            tog_myPostFreeBoard.setTextColor(Color.parseColor("#FF000000"));
            tog_myPostFreeBoard.setClickable(false);
            myFreePostRecyclerView.setVisibility(View.VISIBLE);
        }else{
            tog_myPostFreeBoard.setBackgroundColor(Color.WHITE);
            tog_myPostFreeBoard.setTextColor(Color.parseColor("#D8D8D8"));
            tog_myPostFreeBoard.setClickable(true);
            myFreePostRecyclerView.setVisibility(View.INVISIBLE);
        }
    }
}
