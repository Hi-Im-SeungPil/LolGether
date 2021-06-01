package org.jeonfeel.withlol2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.etc.CheckNetwork;

public class Activity_duoBoardPostRetouch extends AppCompatActivity {

    private Button btn_postRetouchBackspace;
    private TextView btn_postRetouchWrite,et_postRetouchTitle;
    private EditText et_postRetouchContent;
    private TextView tv_postRetouchTitle;
    private String title,contents,boardChild,postId,boardTitle,selectedPosition;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duo_board_post_retouch);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        CheckNetwork checkNetwork = new CheckNetwork();
        int netWorkStatus = checkNetwork.CheckNetwork(getApplication());

        if(netWorkStatus == 0){
            Toast.makeText(getApplication(), "인터넷 연결을 확인해 주세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btn_postRetouchBackspace = findViewById(R.id.btn_postRetouchBackspace);
        btn_postRetouchWrite = findViewById(R.id.btn_postRetouchWrite);
        tv_postRetouchTitle = findViewById(R.id.tv_postRetouchTitle);
        et_postRetouchContent = findViewById(R.id.et_postRetouchContent);

        setPostRetouch();

        btn_postRetouchWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_postRetouchWrite();
            }
        });
        btn_postRetouchBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void setPostRetouch(){
        Intent intent = getIntent();
        title = intent.getStringExtra("writtenTitle");
        contents = intent.getStringExtra("writtenContent");
        postId = intent.getStringExtra("writtenId");
        boardChild = intent.getStringExtra("boardChild");
        boardTitle = intent.getStringExtra("boardTitle");
        selectedPosition = intent.getStringExtra("selectedPosition");

        tv_postRetouchTitle.setText(title);
        et_postRetouchContent.setText(contents);
    }
    public void setBtn_postRetouchWrite(){
        title = tv_postRetouchTitle.getText().toString();
        contents = et_postRetouchContent.getText().toString();

        if(contents.length() < 1){
            Toast.makeText(this, "내용을 입력해 주세요!", Toast.LENGTH_SHORT).show();
        }else {

            mDatabase.child("duoBoard").child(boardChild).child(selectedPosition).child(postId).child("content").setValue(contents);

            Activity_watchingDuoBoardPost ac1 = (Activity_watchingDuoBoardPost) Activity_watchingDuoBoardPost.activity;
            ac1.finish();

            Activity_duoBoard ac2 = (Activity_duoBoard) Activity_duoBoard.activity;
            ac2.finish();

            Intent intent = new Intent(getApplication(), Activity_duoBoard.class);

            intent.putExtra("boardTitle", boardTitle);

            finish();

            startActivity(intent);
        }
    }
}
