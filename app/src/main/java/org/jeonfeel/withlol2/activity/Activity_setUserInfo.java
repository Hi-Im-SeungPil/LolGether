package org.jeonfeel.withlol2.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Activity_setUserInfo extends AppCompatActivity {

    public static final int REQUEST_CODE = 111;

    private TextView tv_setSummonerName,tv_setSummonerTier,tv_setSummonerWinningRate;
    private ImageView img_setUserTier;
    private Button btn_setSearchUserId,btn_start;
    private EditText et_setSearchUserId;
    private String userId,resultId = null,tier,rank,summonerName;
    private String Uid,uEmail;
    private GetLoLId getLoLId;
    private GetSummonerInfo getSummonerInfo;
    private JSONObject json_userId;
    private JSONArray json_userInfo;
    private int wins,losses;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    int check = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        tv_setSummonerName = findViewById(R.id.tv_setSummonerName);
        tv_setSummonerTier = findViewById(R.id.tv_setSummonerTier);
        tv_setSummonerWinningRate = findViewById(R.id.tv_setSummonerWinningRate);
        img_setUserTier = findViewById(R.id.img_setUserTier);
        et_setSearchUserId = findViewById(R.id.et_setSearchUserId);
        btn_start = findViewById(R.id.btn_start);
        btn_setSearchUserId = findViewById(R.id.btn_setSearchUserId);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Uid = mAuth.getUid();
        uEmail = mAuth.getCurrentUser().getEmail();

        Log.d("gggg",Uid);

        btn_setSearchUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_setSearchUserId();
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_start();
            }
        });
    }

    protected void mGetUserId(EditText et){
        userId = et.getText().toString();

        String url = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/" +
                userId + "?api_key=RGAPI-11e273ee-915a-4ab6-80e6-7a8ce0ed3905";
        getLoLId = new GetLoLId();

        try{
            json_userId = new JSONObject();
            json_userId = getLoLId.execute(url).get();
            if(json_userId != null ) {
                resultId = json_userId.getString("id");
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

        String url = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/"+
                resultId+ "?api_key=RGAPI-11e273ee-915a-4ab6-80e6-7a8ce0ed3905"; //API 받아올 URI
        getSummonerInfo = new GetSummonerInfo(); // java DTO

        try{ // JSONArray 사용하기
            json_userInfo = new JSONArray();
            json_userInfo = getSummonerInfo.execute(url).get(); // API 받아와서

            JSONObject jsonObject = new JSONObject();
            jsonObject = json_userInfo.getJSONObject(0); // JSONObj에 넣기
            String queueType = jsonObject.getString("queueType");

            if (queueType.equals("RANKED_FLEX_SR")) {
                jsonObject = json_userInfo.getJSONObject(1);
            }

                tier = jsonObject.getString("tier");
                rank = jsonObject.getString("rank");
                summonerName = et_setSearchUserId.getText().toString();
                wins = jsonObject.getInt("wins");
                losses = jsonObject.getInt("losses");

        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // 유저 정보 insert 매서드
    private void writeNewUser(final String UID, final String email, final String mSummonerName,
                              final String mTier, final String mRank, final int mWins, final int mLosses) {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("userInfo", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                String token = task.getResult(); // push알림을 위한 token

                User user = new User(email,mSummonerName,mTier,mRank,token,mWins,mLosses,1);

                mDatabase.child("users").child(UID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() { // firebase insert
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(Activity_setUserInfo.this, "소환사 정보 설정이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Activity_setUserInfo.this, "소환사 정보 설정에 실패했습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void setSummonerInfo() {
        if (tier != null) {
            switch (tier) {
                case "IRON":
                    img_setUserTier.setImageResource(R.drawable.iron);
                    setSummonerTextView();
                    break;
                case "BRONZE":
                    img_setUserTier.setImageResource(R.drawable.bronze);
                    setSummonerTextView();
                    break;
                case "SILVER":
                    img_setUserTier.setImageResource(R.drawable.silver);
                    setSummonerTextView();
                    break;
                case "GOLD":
                    img_setUserTier.setImageResource(R.drawable.gold);
                    setSummonerTextView();
                    break;
                case "PLATINUM":
                    img_setUserTier.setImageResource(R.drawable.ple);
                    setSummonerTextView();
                    break;
                case "DIAMOND":
                    img_setUserTier.setImageResource(R.drawable.dia);
                    setSummonerTextView();
                    break;
                case "MASTER":
                    img_setUserTier.setImageResource(R.drawable.master);
                    setSummonerTextView();
                    break;
                case "GRANDMASTER":
                    img_setUserTier.setImageResource(R.drawable.gm);
                    setSummonerTextView();
                    break;
                case "CHALLENGER":
                    img_setUserTier.setImageResource(R.drawable.ch);
                    setSummonerTextView();
                    break;
            }
        }else{
                summonerName = et_setSearchUserId.getText().toString();
                tier = "UnRanked";
                rank = "UnRanked";
                wins = 0;
                losses = 0;

                img_setUserTier.setImageResource(R.drawable.unranked);
                tv_setSummonerName.setText(summonerName);
                tv_setSummonerTier.setText("UnRanked");
                tv_setSummonerWinningRate.setText("UnRanked");
            }
        }
        public void setBtn_setSearchUserId(){

            mGetUserId(et_setSearchUserId);

            InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(btn_setSearchUserId.getWindowToken(), 0);

            if(resultId != null) {
                tier = null;
                mGetUserInfo();
                setSummonerInfo();
                resultId = null;
                check = 1;
            }else{
                Toast.makeText(getApplication(), "소환사명을 정확히 입력해 주세요", Toast.LENGTH_SHORT).show();
                img_setUserTier.setImageResource(R.drawable.img_sadbee);
                tv_setSummonerName.setText("");
                tv_setSummonerTier.setText("");
                tv_setSummonerWinningRate.setText("");
                check = 0;
            }
        }
        public void setBtn_start(){
            if(check == 1) {
                writeNewUser(Uid, uEmail, summonerName, tier, rank, wins, losses);
            }else if(check == 0){
                Toast.makeText(getApplication(), "소환사 정보를 등록해 주세요", Toast.LENGTH_SHORT).show();
            }
        }
        public void setSummonerTextView(){
            tv_setSummonerName.setText(summonerName);
            tv_setSummonerTier.setText(tier + " " + rank);
            tv_setSummonerWinningRate.setText("승 : " + wins +"\n" + "패 : " + losses);
        }

}

