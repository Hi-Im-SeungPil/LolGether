package org.jeonfeel.withlol2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jeonfeel.withlol2.DTO.GetLoLId;
import org.jeonfeel.withlol2.DTO.GetSummonerInfo;
import org.jeonfeel.withlol2.MainActivity;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.DTO.User;
import org.jeonfeel.withlol2.etc.CheckNetwork;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Activity_summonerChange extends AppCompatActivity {

    private ImageView iv_summonerChangeBackspace,iv_summonerChangeTier;
    private TextView tv_summonerChangeName,tv_summonerChangeTier,tv_summonerChangeWinsLosses,tv_csummonerLevel;
    private EditText et_summonerChange;
    private Button btn_summonerChangeSearch,btn_summonerSelect;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private GetLoLId getLoLId;
    private GetSummonerInfo getSummonerInfo;
    private JSONObject json_userId;
    private JSONArray json_userInfo;

    private String Uid,uEmail;
    private String userId,resultId;
    private String tier,rank,summonerName;
    private int notificationStatus;
    private int summonerLevel,leaguePoints;
    private int check = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summonerchange);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mFindViewById();

        CheckNetwork checkNetwork = new CheckNetwork();
        int netWorkStatus = checkNetwork.CheckNetwork(getApplication());

        if(netWorkStatus == 0){
            Toast.makeText(getApplication(), "인터넷 연결을 확인해 주세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        Intent intent = getIntent();
        notificationStatus = intent.getIntExtra("notificationStatus",0);

        btn_summonerChangeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(Activity_summonerChange.this);
                progressDialog.setMessage("소환사 정보를 받아오고 있습니다.");
                progressDialog.setCancelable(true);
                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
                progressDialog.show();

                setBtn_summonerChangeSearch();

                progressDialog.dismiss();
            }});
        btn_summonerSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_summonerSelect();
            }
        });
        iv_summonerChangeBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void mFindViewById(){

        iv_summonerChangeBackspace = findViewById(R.id.iv_summonerChangeBackspace);
        iv_summonerChangeTier = findViewById(R.id.iv_summonerChangeTier);
        tv_summonerChangeName = findViewById(R.id.tv_summonerChangeName);
        tv_summonerChangeTier = findViewById(R.id.tv_summonerChangeTier);
        tv_summonerChangeWinsLosses = findViewById(R.id.tv_summonerChangeWinsLosses);
        et_summonerChange = findViewById(R.id.et_summonerChange);
        btn_summonerChangeSearch = findViewById(R.id.btn_summonerChangeSearch);
        btn_summonerSelect = findViewById(R.id.btn_summonerSelect);
        tv_csummonerLevel = findViewById(R.id.tv_csummonerLevel);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Uid = mAuth.getUid();
        uEmail = mAuth.getCurrentUser().getEmail();
    }
    protected void mGetUserId(EditText et){
        userId = et.getText().toString();

        String url = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + userId + "?api_key=RGAPI-11e273ee-915a-4ab6-80e6-7a8ce0ed3905";
        getLoLId = new GetLoLId();

        try{
            json_userId = new JSONObject();
            json_userId = getLoLId.execute(url).get();
            if(json_userId != null ) {
                resultId = json_userId.getString("id");
                summonerLevel = json_userId.getInt("summonerLevel");
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    protected void mGetUserInfo(){

        String url = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/"+ resultId+ "?api_key=RGAPI-11e273ee-915a-4ab6-80e6-7a8ce0ed3905";
        getSummonerInfo = new GetSummonerInfo();

        try{
            json_userInfo = new JSONArray();
            json_userInfo = getSummonerInfo.execute(url).get();

            JSONObject jsonObject = new JSONObject();
            jsonObject = json_userInfo.getJSONObject(0);
            String queueType = jsonObject.getString("queueType");

            if (queueType.equals("RANKED_FLEX_SR")) {
                jsonObject = json_userInfo.getJSONObject(1);
            }

                tier = jsonObject.getString("tier");
                rank = jsonObject.getString("rank");
                summonerName = et_summonerChange.getText().toString();
                leaguePoints = jsonObject.getInt("leaguePoints");

        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setBtn_summonerChangeSearch(){
        mGetUserId(et_summonerChange);

        InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(btn_summonerChangeSearch.getWindowToken(), 0);

        if(resultId != null) {
            tier = null;
            mGetUserInfo();
            setSummonerInfo();
            resultId = null;
            check = 1;
        }else{
            Toast.makeText(getApplication(), "소환사명을 정확히 입력해 주세요", Toast.LENGTH_SHORT).show();
            iv_summonerChangeTier.setImageResource(R.drawable.img_sadbee);
            tv_summonerChangeName.setText("");
            tv_summonerChangeTier.setText("");
            tv_summonerChangeWinsLosses.setText("");
            tv_csummonerLevel.setText("LV.");
            check = 0;
        }
    }
    public void setSummonerInfo(){
        if (tier != null) {
            switch (tier) {
                case "IRON":
                    iv_summonerChangeTier.setImageResource(R.drawable.iron);
                    setSummonerTextView();
                    break;
                case "BRONZE":
                    iv_summonerChangeTier.setImageResource(R.drawable.bronze);
                    setSummonerTextView();
                    break;
                case "SILVER":
                    iv_summonerChangeTier.setImageResource(R.drawable.silver);
                    setSummonerTextView();
                    break;
                case "GOLD":
                    iv_summonerChangeTier.setImageResource(R.drawable.gold);
                    setSummonerTextView();
                    break;
                case "PLATINUM":
                    iv_summonerChangeTier.setImageResource(R.drawable.ple);
                    setSummonerTextView();
                    break;
                case "DIAMOND":
                    iv_summonerChangeTier.setImageResource(R.drawable.dia);
                    setSummonerTextView();
                    break;
                case "MASTER":
                    iv_summonerChangeTier.setImageResource(R.drawable.master);
                    setSummonerTextView();
                    break;
                case "GRANDMASTER":
                    iv_summonerChangeTier.setImageResource(R.drawable.gm);
                    setSummonerTextView();
                    break;
                case "CHALLENGER":
                    iv_summonerChangeTier.setImageResource(R.drawable.ch);
                    setSummonerTextView();
                    break;
            }
        }else{
            summonerName = et_summonerChange.getText().toString();
            tier = "UnRanked";
            rank = "UnRanked";
            leaguePoints = 0;

            iv_summonerChangeTier.setImageResource(R.drawable.unranked);
            tv_summonerChangeName.setText(summonerName);
            tv_summonerChangeTier.setText("UnRanked");
            tv_summonerChangeWinsLosses.setText("UnRanked");
            tv_csummonerLevel.setText("LV."+summonerLevel);
        }
    }
    public void setBtn_summonerSelect(){
        if(check == 1) {
            changeUser(Uid, summonerName, tier, rank,leaguePoints,summonerLevel);

            MainActivity ac = (MainActivity) MainActivity.activity;
            ac.finish();

            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }else if(check == 0){
            Toast.makeText(getApplication(), "소환사 정보를 등록해 주세요", Toast.LENGTH_SHORT).show();
        }
    }
    public void changeUser(String UID, String mSummonerName,
                           String mTier, String mRank, int leaguePoints, int summonerLevel){

                mDatabase.child("users").child(UID).child("summonerName").setValue(mSummonerName);
                mDatabase.child("users").child(UID).child("tier").setValue(mTier);
                mDatabase.child("users").child(UID).child("rank").setValue(mRank);
                mDatabase.child("users").child(UID).child("leaguePoint").setValue(leaguePoints);
                mDatabase.child("users").child(UID).child("summonerLevel").setValue(summonerLevel);

    }
    public void setSummonerTextView(){
        tv_summonerChangeName.setText(summonerName);
        tv_summonerChangeTier.setText(tier + " " + rank);
        tv_summonerChangeWinsLosses.setText(leaguePoints + " 점");
        tv_csummonerLevel.setText("LV."+summonerLevel);
    }
}
