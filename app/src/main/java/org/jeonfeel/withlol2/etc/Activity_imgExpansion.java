package org.jeonfeel.withlol2.etc;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import org.jeonfeel.withlol2.R;

public class Activity_imgExpansion extends AppCompatActivity {

    private PhotoView imgView;
    private Button btn_imgBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_expansion);

        ActionBar ac = getSupportActionBar();
        ac.hide();

        imgView = findViewById(R.id.imgView);
        btn_imgBack = findViewById(R.id.btn_imgBack);

        String url = getIntent().getStringExtra("url");
        Uri uri = Uri.parse(url);
        Glide.with(Activity_imgExpansion.this).load(uri).into(imgView);

        btn_imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
