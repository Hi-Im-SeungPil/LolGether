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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jeonfeel.withlol2.DTO.SaveFreeBoardPost;
import org.jeonfeel.withlol2.DTO.SaveMyPost;
import org.jeonfeel.withlol2.MainActivity;
import org.jeonfeel.withlol2.adapter.Adapter_freeBoardPhoto;
import org.jeonfeel.withlol2.R;
import org.jeonfeel.withlol2.etc.CheckNetwork;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Activity_writingFreeBoardPost extends AppCompatActivity {

    private Button btn_freeBoardWriteBackspace,btn_addPhoto,btn_postWrite;
    private EditText et_freeBoardTitle,et_freeBoardContent;
    private static final int PICK_FROM_ALBUM = 111;
    private RecyclerView photoRecyclerView;
    private static ArrayList<Bitmap> uploadPhotoList;
    private Adapter_freeBoardPhoto adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String postId,currentSummonerName,currentUserUid,currentSummonerTier;
    private CheckBox cb_anonymity;
    private int imgExistence;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_freeboard_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        CheckNetwork checkNetwork = new CheckNetwork();

        int netWorkStatus = checkNetwork.CheckNetwork(getApplication());

        if(netWorkStatus == 0){
            Toast.makeText(getApplication(), "인터넷 연결을 확인해 주세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        mFindViewById();
        setPhotoRecyclerView();

        getCurrentUserInfo();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btn_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtn_addPhoto();
            }
        });
        btn_postWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setBtn_postWrite();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_freeBoardWriteBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void mFindViewById(){
        btn_freeBoardWriteBackspace = findViewById(R.id.btn_freeBoardWriteBackspace);
        btn_addPhoto = findViewById(R.id.btn_addPhoto);
        btn_postWrite = findViewById(R.id.btn_postWrite);
        et_freeBoardTitle = findViewById(R.id.et_freeBoardTitle);
        et_freeBoardContent = findViewById(R.id.et_freeBoardContent);
        cb_anonymity = findViewById(R.id.cb_anonymity);
    }

    private void setBtn_addPhoto(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //사진을 여러개 선택할수 있도록 한다
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),  PICK_FROM_ALBUM);

    }
    private void setPhotoRecyclerView(){

        photoRecyclerView = (RecyclerView) findViewById(R.id.photoRecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        photoRecyclerView.setLayoutManager(mLinearLayoutManager);

        photoRecyclerView.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK && data != null) {

           if (data.getClipData() != null) {
               ClipData clipData = data.getClipData();
               imgExistence = 0;
               photoRecyclerView.setVisibility(View.GONE);
               if(clipData.getItemCount() > 10){
                   Toast.makeText(this, "사진은 10장까지만 가능합니다.", Toast.LENGTH_SHORT).show();
                   return;
               }else if(clipData.getItemCount() > 0 && clipData.getItemCount() <= 10){
                   uploadPhotoList = new ArrayList<>();

                   for(int i = 0; i < clipData.getItemCount(); i++){
                       try {
                           String path = getRealPathFromURI(clipData.getItemAt(i).getUri());
                           Log.d("path",path);
                           ExifInterface exif = new ExifInterface(path);
                           int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                   ExifInterface.ORIENTATION_UNDEFINED);

                           Bitmap bitImg = resize(Activity_writingFreeBoardPost.this, clipData.getItemAt(i).getUri(), 700);
                           Log.d("path",String.valueOf(clipData.getItemAt(i).getUri()));

                           Bitmap bmRotated = rotateBitmap(bitImg, orientation);

                           uploadPhotoList.add(bmRotated);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   photoRecyclerView.setVisibility(View.VISIBLE);
                   imgExistence = 1;
               }
           }
            adapter = new Adapter_freeBoardPhoto(uploadPhotoList,this,photoRecyclerView,"writing");
            photoRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
    // 글 작성 완료 버튼
      private void setBtn_postWrite() throws IOException {
        String title = et_freeBoardTitle.getText().toString();
        String content = et_freeBoardContent.getText().toString();
        postId = mDatabase.child("freeBoard").push().getKey();
        if(title.length() == 0){
            Toast.makeText(this, "제목을 입력해 주세요!", Toast.LENGTH_SHORT).show();
        }else if(content.length() == 0){
            Toast.makeText(this, "내용을 입력해 주세요!", Toast.LENGTH_SHORT).show();
        }else {
            if(imgExistence == 1) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://lolgether.appspot.com");

                for (int i = 0; i < uploadPhotoList.size(); i++) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    uploadPhotoList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    storageRef.child(postId + "/" + i)
                            .putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
                }
            }
            if (cb_anonymity.isChecked()) {
                currentSummonerName = "롤게더 익명";
                currentSummonerTier = "anonymity";
            }else if(!cb_anonymity.isChecked()){
                MainActivity mainActivity = new MainActivity();
                currentSummonerName = mainActivity.getCurrentSummonerName();
                currentSummonerTier = mainActivity.getCurrentSummonerTier();
            }
            SaveFreeBoardPost saveFreeBoardPost = new SaveFreeBoardPost(postId, currentUserUid, currentSummonerName, currentSummonerTier, title, content,
                    0, System.currentTimeMillis(), imgExistence);
            imgExistence = 0;

            mDatabase.child("freeBoard").child(postId).setValue(saveFreeBoardPost);
            mDatabase.child("users").child(currentUserUid).child("freeBoardPost").child(postId).setValue(postId);

            Activity_freeBoard ac = (Activity_freeBoard) Activity_freeBoard.activity;
            ac.finish();

            Intent intent = new Intent(this, Activity_freeBoard.class);

            finish();

            startActivity(intent);
        }
    }

    public static void delPhoto(int position){
        uploadPhotoList.remove(position);
    }

    public void getCurrentUserInfo(){
        MainActivity mainActivity = new MainActivity();
        currentSummonerName = mainActivity.getCurrentSummonerName();
        currentSummonerTier = mainActivity.getCurrentSummonerTier();
        currentUserUid = mainActivity.getCurrentUserUid();
    }

    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 2;

            while (true) {
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);

            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

        cursor.close();
        return path;
    }

}
