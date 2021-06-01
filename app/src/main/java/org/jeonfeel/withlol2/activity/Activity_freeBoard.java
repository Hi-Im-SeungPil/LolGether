package org.jeonfeel.withlol2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jeonfeel.withlol2.DTO.SaveDuoBoardPost;
import org.jeonfeel.withlol2.DTO.SaveFreeBoardPost;
import org.jeonfeel.withlol2.DTO.User;
import org.jeonfeel.withlol2.adapter.Adapter_duoBoard;
import org.jeonfeel.withlol2.adapter.Adapter_freeBoard;
import org.jeonfeel.withlol2.etc.CheckNetwork;
import org.jeonfeel.withlol2.etc.DuoBoardItemClickListener;
import org.jeonfeel.withlol2.etc.FreeBoardItemClickListener;
import org.jeonfeel.withlol2.etc.Item_duoBoard;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.etc.Item_freeBoard;

import java.util.ArrayList;

public class Activity_freeBoard extends AppCompatActivity {
    private final String TAG = "Activity_freeBoard : ";

    public static Activity activity;

    private Button btn_freeBoardRefresh,btn_freeBoardBackspace;
    private FloatingActionButton fab_freeBoardWrite;
    private TextView tv_noItem;
    private RecyclerView freeBoardRecyclerView;
    private Adapter_freeBoard adapter;
    private ArrayList<Item_freeBoard> mItem;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String pagingPostId = "", lastKey = "",id;
    private int check = 1;
    private int startCheck = 0;

    private ArrayList<Item_freeBoard> sampleItem;

    private SaveFreeBoardPost saveFreeBoardPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_board);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        CheckNetwork checkNetwork = new CheckNetwork();
        int netWorkStatus = checkNetwork.CheckNetwork(getApplication());

        if(netWorkStatus == 0){
            Toast.makeText(getApplication(), "인터넷 연결을 확인해 주세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        activity = Activity_freeBoard.this;

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("freeBoard");
        sampleItem = new ArrayList<>();

        mFindViewById();
        setRecyclerView();
        getLastKey();
        setFreeBoard();

        fab_freeBoardWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFab_freeBoardWrite();
            }
        });
        btn_freeBoardBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void mFindViewById(){
        btn_freeBoardBackspace = findViewById(R.id.btn_freeBoardBackspace);
        btn_freeBoardRefresh = findViewById(R.id.btn_freeBoardRefresh);
        fab_freeBoardWrite = findViewById(R.id.fab_freeBoardWrite);
        tv_noItem = findViewById(R.id.tv_noItem);
    }
    private void setRecyclerView(){
        freeBoardRecyclerView = (RecyclerView) findViewById(R.id.freeBoardRecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        freeBoardRecyclerView.setLayoutManager(mLinearLayoutManager);

        mItem = new ArrayList<>();

        adapter = new Adapter_freeBoard(mItem,this);
        freeBoardRecyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(freeBoardRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        freeBoardRecyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setOnItemClickListener(new FreeBoardItemClickListener() {
            @Override
            public void onItemClick(Adapter_freeBoard.CustomViewHolder holder, View view, int position) {
                Item_freeBoard item = adapter.getItem(position);

                if(item != null) {
                    Intent intent = new Intent(getApplication(), Activity_watchingFreeBoardPost.class);
                    intent.putExtra("postId", item.getId());
                    intent.putExtra("writerUid", item.getWriterUid());
                    intent.putExtra("postTitle", item.getTitle());
                    intent.putExtra("postContent", item.getContent());
                    intent.putExtra("summonerTier", item.getTier());
                    intent.putExtra("summonerName", item.getSummonerName());
                    intent.putExtra("postDate",item.getPostWrittenDate());
                    intent.putExtra("commentCount", item.getCommentCount());
                    intent.putExtra("imgExistence",item.getImageExistence());
                    startActivity(intent);

                }else if(item == null){
                    Toast.makeText(Activity_freeBoard.this, "데이터 로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        freeBoardRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    loadNextData();
                }
            }
        });
    }
    private void setFab_freeBoardWrite(){
        Intent intent = new Intent(getApplication(), Activity_writingFreeBoardPost.class);
        startActivity(intent);
    }
    private void setFreeBoard(){

        mDatabase.orderByChild("id")
                .limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue(User.class) != null) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                if(pagingPostId.isEmpty()){
                                    pagingPostId = userSnapshot.child("id").getValue(String.class);
                                    Log.d(TAG,pagingPostId);
                                }
                                 saveFreeBoardPost = userSnapshot.getValue(SaveFreeBoardPost.class);

                                String id = saveFreeBoardPost.getId();
                                String uid = saveFreeBoardPost.getUid();
                                String summonerName = saveFreeBoardPost.getSummonerName();
                                String summonerTier = saveFreeBoardPost.getSummonerTier();
                                String title = saveFreeBoardPost.getTitle();
                                String content = saveFreeBoardPost.getContent();
                                int commentCount = saveFreeBoardPost.getCommentCount();
                                long postDate = saveFreeBoardPost.getPostDate();
                                int imgExist = saveFreeBoardPost.getImgExist();

                                Item_freeBoard Item = new Item_freeBoard(id,uid,summonerTier,title,content,summonerName,imgExist,commentCount,postDate);
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
    }

    private void loadNextData(){

        mDatabase.orderByChild("id")
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
                                saveFreeBoardPost = dataSnapshot.getValue(SaveFreeBoardPost.class);
                                id = saveFreeBoardPost.getId();
                                if(check == 1){
                                    pagingPostId = id;
                                }
                                String uid = saveFreeBoardPost.getUid();
                                String summonerName = saveFreeBoardPost.getSummonerName();
                                String summonerTier = saveFreeBoardPost.getSummonerTier();
                                String title = saveFreeBoardPost.getTitle();
                                String content = saveFreeBoardPost.getContent();
                                int commentCount = saveFreeBoardPost.getCommentCount();
                                long postDate = saveFreeBoardPost.getPostDate();
                                int imgExist = saveFreeBoardPost.getImgExist();

                                Item_freeBoard Item = new Item_freeBoard(id,uid,summonerTier,title,content,summonerName,imgExist,commentCount,postDate);
                                sampleItem.add(Item);
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
                        adapter.notifyDataSetChanged();
                        sampleItem.clear();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void getLastKey(){
        mDatabase.orderByChild("id")
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot s : snapshot.getChildren()) {
                                lastKey = s.child("id").getValue(String.class);
                                Log.d(TAG,lastKey);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}
