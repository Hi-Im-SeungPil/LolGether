package org.jeonfeel.withlol2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jeonfeel.withlol2.DTO.GetLoLId;
import org.jeonfeel.withlol2.DTO.GetSummonerInfo;
import org.jeonfeel.withlol2.activity.Activity_duoBoard;
import org.jeonfeel.withlol2.activity.Activity_freeBoard;
import org.jeonfeel.withlol2.activity.Activity_login;
import org.jeonfeel.withlol2.activity.Activity_myPost;
import org.jeonfeel.withlol2.activity.Activity_searchingSummoner;
import org.jeonfeel.withlol2.activity.Activity_setUserInfo;
import org.jeonfeel.withlol2.activity.Activity_summonerChange;
import org.jeonfeel.withlol2.etc.CheckNetwork;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    private EditText et_idSearch;

    private Button btn_idSearch;

    private ImageView img_userTier, iv_mainPopup;

    private TextView tv_summonerName, tv_summonerRank, tv_summonerLeaguePoints,tv_summonerLevel;

    private LinearLayout linear_soloRankUnRanked, linear_soloRankIron, linear_soloRankBronze, linear_soloRankSilver,
            linear_soloRankGold, linear_soloRankPlatinum, linear_soloRankDiamond,linear_freeBoard;

    private TextView tv_summonerUpdate;

    private LinearLayout linear_freeRankUnRanked, linear_freeRankIron, linear_freeRankBronze, linear_freeRankSilver,
            linear_freeRankGold, linear_freeRankPlatinum, linear_freeRankDiamond, linear_freeRankMaster,
            linear_freeRankGrandMaster, linear_freeRankChallenger,linear_normalGame;

    private ToggleButton tog_freeRank,tog_soloRank;
    private LinearLayout mainSoloBoardGroup,mainFreeRankBoardGroup;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public static String currentUserUid, currentSummonerName, currentSummonerTier;
    private static int currentUserNotificationStatus;
    public static Activity activity;

    private String currentUserRank;
    private int currentUserLeaguePoints,currentUserLevel;

    private GetLoLId getLoLId;
    private GetSummonerInfo getSummonerInfo;
    private JSONObject json_userId;
    private JSONArray json_userInfo;
    private String resultId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mFindViewById();

        activity = MainActivity.this;

        tog_soloRank.setChecked(true);
        mainFreeRankBoardGroup.setVisibility(View.GONE);
        tog_soloRank.setClickable(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        currentUserUid = mAuth.getUid();

        setSummonerInfo();

        btn_idSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseUserId(et_idSearch);
            }
        });

        setOnclick_boardAction();

        linear_freeBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLinear_freeBoard();
            }
        });
        tv_summonerUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTv_summonerUpdate();
            }
        });
        iv_mainPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIv_mainPopup(v);
            }
        });
        et_idSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    et_idSearch.setText("");
                    parseUserId(et_idSearch);
                    return true;
                }
                return false;
            }
        });
        tog_soloRank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTog_soloRank(isChecked);
            }
        });
        tog_freeRank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTog_freeRank(isChecked);
            }
        });

    }

    private void mFindViewById() {

        et_idSearch = findViewById(R.id.et_idSearch);
        btn_idSearch = findViewById(R.id.btn_idSearch);
        img_userTier = findViewById(R.id.img_userTier);
        linear_soloRankUnRanked = findViewById(R.id.linear_soloRankUnRanked);
        linear_soloRankIron = findViewById(R.id.linear_soloRankIron);
        linear_soloRankBronze = findViewById(R.id.linear_soloRankBronze);
        linear_soloRankSilver = findViewById(R.id.linear_soloRankSilver);
        linear_soloRankGold = findViewById(R.id.linear_soloRankGold);
        linear_soloRankPlatinum = findViewById(R.id.linear_soloRankPlatinum);
        linear_soloRankDiamond = findViewById(R.id.linear_soloRankDiamond);
        tv_summonerName = findViewById(R.id.tv_summonerName);
        tv_summonerRank = findViewById(R.id.tv_summonerRank);
        tv_summonerLeaguePoints = findViewById(R.id.tv_summonerWinningRate);
        tv_summonerUpdate = findViewById(R.id.tv_summonerUpdate);
        iv_mainPopup = findViewById(R.id.iv_mainPopup);
        linear_freeRankUnRanked = findViewById(R.id.linear_freeRankUnRanked);
        linear_freeRankIron = findViewById(R.id.linear_freeRankIron);
        linear_freeRankBronze = findViewById(R.id.linear_freeRankBronze);
        linear_freeRankSilver = findViewById(R.id.linear_freeRankSilver);
        linear_freeRankGold = findViewById(R.id.linear_freeRankGold);
        linear_freeRankPlatinum = findViewById(R.id.linear_freeRankPlatinum);
        linear_freeRankDiamond = findViewById(R.id.linear_freeRankDiamond);
        linear_freeRankMaster = findViewById(R.id.linear_freeRankMaster);
        linear_freeRankGrandMaster = findViewById(R.id.linear_freeRankGrandMaster);
        linear_freeRankChallenger = findViewById(R.id.linear_freeRankChallenger);
        linear_normalGame = findViewById(R.id.linear_normalGame);
        tog_soloRank = findViewById(R.id.tog_soloRank);
        tog_freeRank = findViewById(R.id.tog_freeRank);
        mainSoloBoardGroup = findViewById(R.id.mainSoloBoardGroup);
        mainFreeRankBoardGroup = findViewById(R.id.mainFreeRankBoardGroup);
        linear_freeBoard = findViewById(R.id.linear_freeBoard);
        tv_summonerLevel = findViewById(R.id.tv_summonerLevel);
    }


    //???????????? ??? ???????????? ?????? ????????? ??????

    private void parseUserId(EditText et_idSearch) {
        String id = et_idSearch.getText().toString();
        Intent intent = new Intent(this, Activity_searchingSummoner.class);
        intent.putExtra("userId", id);
        startActivity(intent);
    }

    // ?????? ????????? ????????? ???????????? ????????? ??????

    private void setSummonerInfo() {
        mDatabase.child("users").child(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null) {
                    currentSummonerName = snapshot.child("summonerName").getValue(String.class);
                    currentSummonerTier = snapshot.child("tier").getValue(String.class);
                    currentUserRank = snapshot.child("rank").getValue(String.class);
                    Integer checkNoti = snapshot.child("notification").getValue(int.class);
                    Integer checkUserLevel = snapshot.child("summonerLevel").getValue(int.class);
                    Integer checkUserLeaguePoint = snapshot.child("leaguePoint").getValue(int.class);
                    if (checkNoti != null) {
                        currentUserNotificationStatus = checkNoti;
                    }
                    if (checkUserLevel != null) {
                        currentUserLevel = checkUserLevel;
                    }
                    if (checkUserLevel != null) {
                        currentUserLeaguePoints = checkUserLeaguePoint;
                    }

                    tv_summonerLevel.setText("LV." + currentUserLevel);
                    tv_summonerName.setText(currentSummonerName);
                    if (currentSummonerTier.equals("UnRanked")) {
                        tv_summonerRank.setText("??? ???");
                        tv_summonerLeaguePoints.setText("????????? ?????????!");
                    } else {
                        tv_summonerRank.setText(currentSummonerTier + " " + currentUserRank);
                        tv_summonerLeaguePoints.setText(currentUserLeaguePoints + " ???");
                    }
                    switch (currentSummonerTier) {
                        case "UnRanked":
                            img_userTier.setImageResource(R.drawable.unranked);
                            break;
                        case "IRON":
                            img_userTier.setImageResource(R.drawable.iron);
                            break;
                        case "BRONZE":
                            img_userTier.setImageResource(R.drawable.bronze);
                            break;
                        case "SILVER":
                            img_userTier.setImageResource(R.drawable.silver);
                            break;
                        case "GOLD":
                            img_userTier.setImageResource(R.drawable.gold);
                            break;
                        case "PLATINUM":
                            img_userTier.setImageResource(R.drawable.ple);
                            break;
                        case "DIAMOND":
                            img_userTier.setImageResource(R.drawable.dia);
                            break;
                        case "MASTER":
                            img_userTier.setImageResource(R.drawable.master);
                            break;
                        case "GRANDMASTER":
                            img_userTier.setImageResource(R.drawable.gm);
                            break;
                        case "CHALLENGER":
                            img_userTier.setImageResource(R.drawable.ch);
                            break;
                    }
                }else{

                    mAuth.signOut();
                    Toast.makeText(MainActivity.this, "????????? ?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Activity_login.class);
                    startActivity(intent);
                    finish();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //?????? ???????????? ???????????? Uid
    public static String getCurrentUserUid() {
        return currentUserUid;
    }

    //?????? ???????????? ???????????? ????????? ??????
    public static String getCurrentSummonerName() {
        return currentSummonerName;
    }

    //?????? ???????????? ???????????? ??????
    public static String getCurrentSummonerTier() {
        return currentSummonerTier;
    }

    public static int getCurrentUserNotificationStatus(){
        return currentUserNotificationStatus;
    }

    // ???????????? ?????? ?????????
    public void setOnclick_boardAction(){
        LinearLayout[] solos = new LinearLayout[]{linear_soloRankUnRanked, linear_soloRankIron, linear_soloRankBronze, linear_soloRankSilver,
                linear_soloRankGold, linear_soloRankPlatinum, linear_soloRankDiamond};

        LinearLayout[] frees = new LinearLayout[]{linear_freeRankUnRanked, linear_freeRankIron, linear_freeRankBronze, linear_freeRankSilver,
                linear_freeRankGold, linear_freeRankPlatinum, linear_freeRankDiamond, linear_freeRankMaster,
                linear_freeRankGrandMaster, linear_freeRankChallenger,linear_normalGame};

        setLinear_Action setLinear_action = new setLinear_Action();

        for(int i =0; i < 7; i++){
            solos[i].setOnClickListener(setLinear_action);
        }
        for(int i = 0; i <11; i++ ){
            frees[i].setOnClickListener(setLinear_action);
        }
    }
    //??????????????? ?????? ?????????
    public void setLinear_freeBoard(){
        Intent intent = new Intent(getApplication(), Activity_freeBoard.class);
        startActivity(intent);
    }

    // ????????? ?????? ??????
    public void setTv_summonerUpdate() {

        CheckNetwork checkNetwork = new CheckNetwork();
        int netWorkStatus = checkNetwork.CheckNetwork(MainActivity.this);

        if(netWorkStatus == 0){
            Toast.makeText(MainActivity.this, "????????? ????????? ????????? ?????????!!", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + currentSummonerName + "?api_key=RGAPI-11e273ee-915a-4ab6-80e6-7a8ce0ed3905";
        getLoLId = new GetLoLId();
        try {
            json_userId = new JSONObject();
            json_userId = getLoLId.execute(url).get();
            if (json_userId != null) {
                resultId = json_userId.getString("id");
                currentUserLevel = json_userId.getInt("summonerLevel");
            } else {
                Toast.makeText(this, "???????????? ?????? ????????????, ????????? ????????? ????????? ????????????!", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url2 = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + resultId + "?api_key=RGAPI-11e273ee-915a-4ab6-80e6-7a8ce0ed3905";
        getSummonerInfo = new GetSummonerInfo();

        try {
            json_userInfo = new JSONArray();
            json_userInfo = getSummonerInfo.execute(url2).get();

            JSONObject jsonObject = new JSONObject();
            jsonObject = json_userInfo.getJSONObject(0);
            String queueType = jsonObject.getString("queueType");
            if (queueType.equals("RANKED_FLEX_SR")) {
                jsonObject = json_userInfo.getJSONObject(1);
            }
                currentSummonerTier = jsonObject.getString("tier");
                currentUserRank = jsonObject.getString("rank");
                currentUserLeaguePoints = jsonObject.getInt("leaguePoints");

                tv_summonerLevel.setText("LV."+currentUserLevel);

                if (currentSummonerTier.equals("UnRanked")) {
                    tv_summonerRank.setText("??? ???");
                    tv_summonerLeaguePoints.setText("????????? ?????????!");
                } else {
                    tv_summonerRank.setText(currentSummonerTier + " " + currentUserRank);
                    tv_summonerLeaguePoints.setText(currentUserLeaguePoints + " ???");
                }

                mDatabase.child("users").child(currentUserUid).child("tier").setValue(currentSummonerTier);
                mDatabase.child("users").child(currentUserUid).child("rank").setValue(currentUserRank);
                mDatabase.child("users").child(currentUserUid).child("leaguePoint").setValue(currentUserLeaguePoints);
                mDatabase.child("users").child(currentUserUid).child("summonerLevel")
                        .setValue(currentUserLevel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "????????? ?????? ???????????????!", Toast.LENGTH_SHORT).show();
                    }
                });

                switch (currentSummonerTier) {
                    case "UnRanked":
                        img_userTier.setImageResource(R.drawable.unranked);
                        break;
                    case "IRON":
                        img_userTier.setImageResource(R.drawable.iron);
                        break;
                    case "BRONZE":
                        img_userTier.setImageResource(R.drawable.bronze);
                        break;
                    case "SILVER":
                        img_userTier.setImageResource(R.drawable.silver);
                        break;
                    case "GOLD":
                        img_userTier.setImageResource(R.drawable.gold);
                        break;
                    case "PLATINUM":
                        img_userTier.setImageResource(R.drawable.ple);
                        break;
                    case "DIAMOND":
                        img_userTier.setImageResource(R.drawable.dia);
                        break;
                    case "MASTER":
                        img_userTier.setImageResource(R.drawable.master);
                        break;
                    case "GRANDMASTER":
                        img_userTier.setImageResource(R.drawable.gm);
                        break;
                    case "CHALLENGER":
                        img_userTier.setImageResource(R.drawable.ch);
                        break;
                }
            } catch(InterruptedException e){
                e.printStackTrace();
            } catch(ExecutionException e){
                e.printStackTrace();
            } catch(JSONException e){
                e.printStackTrace();
            }
    }

    //???????????? ?????? ??????
    public void setIv_mainPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
        getMenuInflater().inflate(R.menu.main_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btn_watchingMyPost) {
                    Intent intent = new Intent(getApplication(), Activity_myPost.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.btn_summonerChange) {
                    Intent intent = new Intent(getApplication(), Activity_summonerChange.class);
                    intent.putExtra("notificationStatus",currentUserNotificationStatus);
                    startActivity(intent);
                } else if(item.getItemId() == R.id.btn_setNotification){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("?????? ?????? ?????? ??????")
                            .setPositiveButton("?????? ??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabase.child("users").child(currentUserUid).child("notification").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MainActivity.this, "?????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).setNegativeButton("?????? ??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabase.child("users").child(currentUserUid).child("notification").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "?????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else if(item.getItemId() == R.id.btn_logOut){
                    mAuth.signOut();
                    Intent intent = new Intent(getApplication(), Activity_login.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    // ???????????? ??????
    public void setTog_soloRank(boolean isChecked){
        if(isChecked){
            tog_freeRank.setChecked(false);
            tog_soloRank.setBackground(ContextCompat.getDrawable(this,R.drawable.main_board_title_background_left_on));
            tog_soloRank.setTextColor(Color.parseColor("#FFFFFFFF"));
            mainSoloBoardGroup.setVisibility(View.VISIBLE);
            mainFreeRankBoardGroup.setVisibility(View.GONE);
            tog_soloRank.setClickable(false);
        }else{
            tog_soloRank.setBackground(ContextCompat.getDrawable(this,R.drawable.main_board_title_background_left_off));
            tog_soloRank.setTextColor(Color.parseColor("#D8D8D8"));
            tog_soloRank.setClickable(true);
        }
    }
    //???????????? ??????
    public void setTog_freeRank(boolean isChecked){
        if(isChecked){
            tog_soloRank.setChecked(false);
            tog_freeRank.setBackground(ContextCompat.getDrawable(this,R.drawable.main_board_title_background_right_on));
            tog_freeRank.setTextColor(Color.parseColor("#FFFFFFFF"));
            mainFreeRankBoardGroup.setVisibility(View.VISIBLE);
            mainSoloBoardGroup.setVisibility(View.GONE);
            tog_freeRank.setClickable(false);
        }else{
            tog_freeRank.setBackground(ContextCompat.getDrawable(this,R.drawable.main_board_title_background_right_off));
            tog_freeRank.setTextColor(Color.parseColor("#D8D8D8"));
            tog_freeRank.setClickable(true);
        }
    }

    // ???????????? ????????? ?????????
    public class setLinear_Action implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplication(), Activity_duoBoard.class);
            switch (v.getId()){
                case R.id.linear_soloRankUnRanked:
                    intent.putExtra("boardTitle", "???????????? ??????");
                    break;
                case R.id.linear_soloRankIron:
                    intent.putExtra("boardTitle", "???????????? ?????????");
                    break;
                case R.id.linear_soloRankBronze:
                    intent.putExtra("boardTitle", "???????????? ?????????");
                    break;
                case R.id.linear_soloRankSilver:
                    intent.putExtra("boardTitle", "???????????? ??????");
                    break;
                case R.id.linear_soloRankGold:
                    intent.putExtra("boardTitle", "???????????? ??????");
                    break;
                case R.id.linear_soloRankPlatinum:
                    intent.putExtra("boardTitle", "???????????? ????????????");
                    break;
                case R.id.linear_soloRankDiamond:
                    intent.putExtra("boardTitle", "???????????? ?????????");
                    break;
                case R.id.linear_freeRankUnRanked:
                    intent.putExtra("boardTitle", "???????????? ??????");
                    break;
                case R.id.linear_freeRankIron:
                    intent.putExtra("boardTitle", "???????????? ?????????");
                    break;
                case R.id.linear_freeRankBronze:
                    intent.putExtra("boardTitle", "???????????? ?????????");
                    break;
                case R.id.linear_freeRankSilver:
                    intent.putExtra("boardTitle", "???????????? ??????");
                    break;
                case R.id.linear_freeRankGold:
                    intent.putExtra("boardTitle", "???????????? ??????");
                    break;
                case R.id.linear_freeRankPlatinum:
                    intent.putExtra("boardTitle", "???????????? ????????????");
                    break;
                case R.id.linear_freeRankDiamond:
                    intent.putExtra("boardTitle", "???????????? ?????????");
                    break;
                case R.id.linear_freeRankMaster:
                    intent.putExtra("boardTitle", "???????????? ?????????");
                    break;
                case R.id.linear_freeRankGrandMaster:
                    intent.putExtra("boardTitle", "???????????? ??????");
                    break;
                case R.id.linear_freeRankChallenger:
                    intent.putExtra("boardTitle", "???????????? ?????????");
                    break;
                case R.id.linear_normalGame:
                    intent.putExtra("boardTitle", "?????? / ?????? ??????");
                    break;
            }
            startActivity(intent);
        }
    }

}
