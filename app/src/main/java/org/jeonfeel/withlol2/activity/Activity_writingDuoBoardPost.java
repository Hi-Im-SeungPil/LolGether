package org.jeonfeel.withlol2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jeonfeel.withlol2.adapter.Adapter_spinner;
import org.jeonfeel.withlol2.MainActivity;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.DTO.SaveDuoBoardPost;
import org.jeonfeel.withlol2.DTO.SaveMyPost;
import org.jeonfeel.withlol2.etc.CheckNetwork;

public class Activity_writingDuoBoardPost extends AppCompatActivity {
    final String TAG = "Activity_duoBoardWrite";

    String selectedTier;
    EditText et_duoBoardWriteContent;
    String _id, currentUserUid, currentSummonerName, currentSummonerTier, title, content;
    int commentCount;
    long postDate;

    TextView btn_writtenWrite;
    ImageView iv_duoBoardWriteBackspace;

    String boardTitle,boardChild;

    Spinner duoBoardWriteRankSpinner,duoBoardWritePositionSpinner;
    ToggleButton tog_duoBoardWriteMic;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String[] rankItems,positionItems;
    String selectedRank,selectedPosition,selectedMic = "off";
    TextView tv_duoBoardTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_duoboard_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mFindViewById();

        CheckNetwork checkNetwork = new CheckNetwork();
        int netWorkStatus = checkNetwork.CheckNetwork(getApplication());

        if(netWorkStatus == 0){
            Toast.makeText(getApplication(), "????????? ????????? ????????? ?????????!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        boardTitle = intent.getStringExtra("boardTitle");

        getBoardChild();
        getCurrentUserInfo();
        setSpinner();

        btn_writtenWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_postWrite();
            }
        });
        iv_duoBoardWriteBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tog_duoBoardWriteMic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTog_duoBoardWriteMic(isChecked);
            }
        });

    }
    public void mFindViewById(){
        et_duoBoardWriteContent = findViewById(R.id.et_duoBoardWriteContent);
        btn_writtenWrite = findViewById(R.id.btn_writtenWrite);
        iv_duoBoardWriteBackspace = findViewById(R.id.iv_duoBoardWriteBackspace);
        duoBoardWriteRankSpinner = findViewById(R.id.duoBoardWriteRankSpinner);
        duoBoardWritePositionSpinner = findViewById(R.id.duoBoardWritePositionSpinner);
        tog_duoBoardWriteMic = findViewById(R.id.tog_duoBoardWriteMic);
        tv_duoBoardTitle = findViewById(R.id.tv_duoBoardTitle);
    }
    public void getCurrentUserInfo(){
        MainActivity mainActivity = new MainActivity();
        currentSummonerName = mainActivity.getCurrentSummonerName();
        currentSummonerTier = mainActivity.getCurrentSummonerTier();
        currentUserUid = mainActivity.getCurrentUserUid();
    }

    public void setBtn_postWrite(){

        if(boardTitle.equals("?????? / ?????? ??????") && et_duoBoardWriteContent.length() != 0){
            selectedPosition = selectedRank+selectedPosition;
        }

        _id = mDatabase.child("duoboard").push().getKey();

        postDate = System.currentTimeMillis();

        title = tv_duoBoardTitle.getText().toString();
        content = et_duoBoardWriteContent.getText().toString();
        if (content.length() == 0){
            Toast.makeText(this, "????????? ????????? ?????????!", Toast.LENGTH_SHORT).show();
        }else {
            commentCount = 0;

            SaveDuoBoardPost saveDuoBoardPost = new SaveDuoBoardPost(_id, currentUserUid, selectedTier, currentSummonerName, currentSummonerTier,
                    title, content, selectedPosition, selectedMic, boardTitle,
                    commentCount, postDate);

            SaveMyPost saveMyPost = new SaveMyPost(_id, boardChild, selectedPosition);

            mDatabase.child("duoBoard").child(boardChild).child(selectedPosition).child(_id).setValue(saveDuoBoardPost);
            mDatabase.child("users").child(currentUserUid).child("duoBoardPost").child(_id).setValue(saveMyPost);

            Activity_duoBoard ac = (Activity_duoBoard) Activity_duoBoard.activity;
            ac.finish();

            Intent intent = new Intent(this, Activity_duoBoard.class);

            intent.putExtra("boardTitle", boardTitle);

            finish();

            startActivity(intent);
        }
    }

    public void getBoardChild(){
        switch (boardTitle) {
            case "???????????? ??????" :
                boardChild = "solounranked";
                break;
            case "???????????? ?????????" :
                boardChild = "soloiron";
                break;
            case "???????????? ?????????" :
                boardChild = "solobronze";
                break;
            case "???????????? ??????" :
                boardChild = "solosilver";
                break;
            case "???????????? ??????" :
                boardChild = "sologold";
                break;
            case "???????????? ????????????" :
                boardChild = "soloplatinum";
                break;
            case "???????????? ?????????" :
                boardChild = "solodiamond";
                break;
            case "???????????? ??????" :
                boardChild = "freeunranked";
                break;
            case "???????????? ?????????" :
                boardChild = "freeiron";
                break;
            case "???????????? ?????????" :
                boardChild = "freebronze";
                break;
            case "???????????? ??????" :
                boardChild = "freesilver";
                break;
            case "???????????? ??????" :
                boardChild = "freegold";
                break;
            case "???????????? ????????????" :
                boardChild = "freeplatinum";
                break;
            case "???????????? ?????????" :
                boardChild = "freediamond";
                break;
            case "???????????? ?????????" :
                boardChild = "freemaster";
                break;
            case "???????????? ??????" :
                boardChild = "freegrandmaster";
                break;
            case "???????????? ?????????" :
                boardChild = "freechallenger";
                break;
            case "?????? / ?????? ??????" :
                boardChild = "normalgame";
                selectedPosition = "???????????????";
                break;
        }
    }

    public void setSpinner(){

        //?????? ?????? ?????????

        switch (boardTitle) {
            case "???????????? ??????" :
                rankItems = getResources().getStringArray(R.array.solorank_unrank_rankarray);
                Adapter_spinner rankAdapter1 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter1);
                break;
            case "???????????? ?????????" :
                rankItems = getResources().getStringArray(R.array.solorank_iron_rankarray);
                Adapter_spinner rankAdapter2 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter2);
                break;
            case "???????????? ?????????" :
                rankItems = getResources().getStringArray(R.array.solorank_bronze_rankarray);
                Adapter_spinner rankAdapter3 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter3);
                break;
            case "???????????? ??????" :
                rankItems = getResources().getStringArray(R.array.solorank_silver_rankarray);
                Adapter_spinner rankAdapter4 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter4);
                break;
            case "???????????? ??????" :
                rankItems = getResources().getStringArray(R.array.solorank_gold_rankarray);
                Adapter_spinner rankAdapter5 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter5);
                break;
            case "???????????? ????????????" :
                rankItems = getResources().getStringArray(R.array.solorank_platinum_rankarray);
                Adapter_spinner rankAdapter6 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter6);
                break;
            case "???????????? ?????????" :
                rankItems = getResources().getStringArray(R.array.solorank_diamond_rankarray);
                Adapter_spinner rankAdapter7 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter7);
                break;
            case "???????????? ??????" :
                rankItems = getResources().getStringArray(R.array.freerank_unrank_rankarray);
                Adapter_spinner rankAdapter8 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter8);
                break;
            case "???????????? ?????????" :
                rankItems = getResources().getStringArray(R.array.freerank_iron_rankarray);
                Adapter_spinner rankAdapter9 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter9);
                break;
            case "???????????? ?????????" :
                rankItems = getResources().getStringArray(R.array.freerank_bronze_rankarray);
                Adapter_spinner rankAdapter10 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter10);
                break;
            case "???????????? ??????" :
                rankItems = getResources().getStringArray(R.array.freerank_silver_rankarray);
                Adapter_spinner rankAdapter11 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter11);
                break;
            case "???????????? ??????" :
                rankItems = getResources().getStringArray(R.array.freerank_gold_rankarray);
                Adapter_spinner rankAdapter12 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter12);
                break;
            case "???????????? ????????????" :
                rankItems = getResources().getStringArray(R.array.freerank_platinum_rankarray);
                Adapter_spinner rankAdapter13 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter13);
                break;
            case "???????????? ?????????" :
                rankItems = getResources().getStringArray(R.array.freerank_diamond_rankarray);
                Adapter_spinner rankAdapter14 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter14);
                break;
            case "???????????? ?????????" :
                rankItems = getResources().getStringArray(R.array.freerank_master_rankarray);
                Adapter_spinner rankAdapter15 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter15);
                break;
            case "???????????? ??????" :
                rankItems = getResources().getStringArray(R.array.freerank_grandmaster_rankarray);
                Adapter_spinner rankAdapter16 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter16);
                break;
            case "???????????? ?????????" :
                rankItems = getResources().getStringArray(R.array.freerank_challenger_rankarray);
                Adapter_spinner rankAdapter17 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter17);
                break;
            case "?????? / ?????? ??????" :
                rankItems = getResources().getStringArray(R.array.normalgame_array);
                Adapter_spinner rankAdapter18 = new Adapter_spinner(this,rankItems);
                duoBoardWriteRankSpinner.setAdapter(rankAdapter18);
                break;
        }
            duoBoardWriteRankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedRank = parent.getSelectedItem().toString();
                    tv_duoBoardTitle.setText("[ "+selectedRank+" ]"+" "+selectedPosition+" ????????????.");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    parent.setSelection(0);
                    tv_duoBoardTitle.setText("[ "+selectedRank+" ]"+" "+selectedPosition+" ????????????.");
                }
            });

        //????????? ?????? ?????????
        positionItems = getResources().getStringArray(R.array.solorank_position_array);
        Adapter_spinner positionAdapter = new Adapter_spinner(this,positionItems);
        duoBoardWritePositionSpinner.setAdapter(positionAdapter);
        duoBoardWritePositionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = parent.getSelectedItem().toString();
                tv_duoBoardTitle.setText("[ "+selectedRank+" ]"+" "+selectedPosition+" ????????????.");
            }
            @Override

            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
                tv_duoBoardTitle.setText("[ "+selectedRank+" ]"+" "+selectedPosition+" ????????????.");
            }
        });
        if(boardTitle.equals("?????? / ?????? ??????")){
            duoBoardWritePositionSpinner.setVisibility(View.GONE);
        }
    }
    public void setTog_duoBoardWriteMic(boolean isChecked){
        if(isChecked){
            selectedMic = "on";
            tog_duoBoardWriteMic.setBackground(ContextCompat.getDrawable(this,R.drawable.img_mic_on));
        }else{
            selectedMic = "off";
            tog_duoBoardWriteMic.setBackground(ContextCompat.getDrawable(this,R.drawable.img_mic_off));
        }
    }
}