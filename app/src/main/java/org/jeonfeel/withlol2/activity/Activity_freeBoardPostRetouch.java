package org.jeonfeel.withlol2.activity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jeonfeel.withlol2.DTO.SaveFreeBoardPost;
import org.jeonfeel.withlol2.MainActivity;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.adapter.Adapter_freeBoardPhoto;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Activity_freeBoardPostRetouch extends AppCompatActivity {

    Button btn_freeBoardRetouchBackspace,btn_RetouchWrite;
    EditText et_freeBoardRetouchTitle,et_freeBoardRetouchContent;
    CheckBox cb_retouchAnonymity;

    String title,content,postId,summonerName;
    private DatabaseReference mDatabase;

    String currentSummonerName,currentSummonerTier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_board_post_retouch);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFindViewById();
        getPostInfo();
        setPost();

        btn_freeBoardRetouchBackspace.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                finish();
            }
        });
        btn_RetouchWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setBtn_retouchPostWrite();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void mFindViewById(){
        btn_freeBoardRetouchBackspace = findViewById(R.id.btn_freeBoardRetouchBackspace);
        btn_RetouchWrite = findViewById(R.id.btn_RetouchWrite);
        et_freeBoardRetouchTitle = findViewById(R.id.et_freeBoardRetouchTitle);
        et_freeBoardRetouchContent = findViewById(R.id.et_freeBoardRetouchContent);
        cb_retouchAnonymity = findViewById(R.id.cb_retouchAnonymity);
    }

    private void getPostInfo(){

        Intent intent = getIntent();
        title = intent.getStringExtra("postTitle");
        content = intent.getStringExtra("postContent");
        postId = intent.getStringExtra("postId");
        summonerName = intent.getStringExtra("summonerName");

    }

    private void setPost(){
        if(summonerName.equals("롤게더 익명")){
            cb_retouchAnonymity.setChecked(true);
        }
        et_freeBoardRetouchTitle.setText(title);
        et_freeBoardRetouchContent.setText(content);
    }


    private void setBtn_retouchPostWrite() throws IOException {
        String title = et_freeBoardRetouchTitle.getText().toString();
        String content = et_freeBoardRetouchContent.getText().toString();

        if(title.length() == 0){
            Toast.makeText(this, "제목을 입력해 주세요!", Toast.LENGTH_SHORT).show();
        }else if(content.length() == 0){
            Toast.makeText(this, "내용을 입력해 주세요!", Toast.LENGTH_SHORT).show();
        }else {
            if (cb_retouchAnonymity.isChecked()) {
                currentSummonerName = "롤게더 익명";
                currentSummonerTier = "anonymity";
            }else if(!cb_retouchAnonymity.isChecked()){
                MainActivity mainActivity = new MainActivity();
                currentSummonerName = mainActivity.getCurrentSummonerName();
                currentSummonerTier = mainActivity.getCurrentSummonerTier();
            }

            mDatabase.child("freeBoard").child(postId).child("summonerName").setValue(currentSummonerName);
            mDatabase.child("freeBoard").child(postId).child("summonerTier").setValue(currentSummonerTier);
            mDatabase.child("freeBoard").child(postId).child("title").setValue(title);
            mDatabase.child("freeBoard").child(postId).child("content").setValue(content);

            Activity_freeBoard ac = (Activity_freeBoard) Activity_freeBoard.activity;
            ac.finish();

            Intent intent = new Intent(this, Activity_freeBoard.class);

            finish();

            startActivity(intent);
        }
    }
}
