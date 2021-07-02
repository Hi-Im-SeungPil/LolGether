package org.jeonfeel.withlol2.activity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import org.jeonfeel.withlol2.etc.CheckNetwork;
import org.jeonfeel.withlol2.etc.URLtoBitmap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Activity_ModifyFreeBoardPost extends AppCompatActivity {

    private EditText et_modifyFreeBoardTitle,et_modifyFreeBoardContent;
    private static final int PICK_FROM_ALBUM = 111;
    private CheckBox cb_modifyAnonymity;
    private Button btn_freePostModifyWrite,btn_postModifyAddPhoto;
    private RecyclerView modifyPhotoRecyclerView;

    private String currentSummonerName,currentSummonerTier,currentUserUid;
    private String postId;
    private long postDate;

    private ArrayList<Bitmap> uploadPhotoList;
    public static ArrayList<Uri> photoList;
    private ArrayList<URL> URLList;
    private int imgExistence = 0;
    private boolean modifyPhoto = false;
    private int firstPhotoCount,finallyPhotoCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_board_post_modify);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        checkNetwork();
        getCurrentUserInfo();
        mFindViewById();
        setPhotoRecyclerView();
        setPost();
        setBtn_postModifyAddPhoto();
        setBtn_freePostModifyWrite();
    }

    private void mFindViewById(){
        et_modifyFreeBoardTitle = findViewById(R.id.et_modifyFreeBoardTitle);
        et_modifyFreeBoardContent = findViewById(R.id.et_modifyFreeBoardContent);
        cb_modifyAnonymity = findViewById(R.id.cb_modifyAnonymity);
        btn_freePostModifyWrite = findViewById(R.id.btn_freePostModifyWrite);
        modifyPhotoRecyclerView =  findViewById(R.id.modifyPhotoRecyclerView);
        btn_postModifyAddPhoto = findViewById(R.id.btn_postModifyAddPhoto);
    }

    private void setPost(){
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
//        String writerUid = intent.getStringExtra("writerUid");
        String postTitle = intent.getStringExtra("postTitle");
        String postContent = intent.getStringExtra("postContent");
//        String summonerTier = intent.getStringExtra("summonerTier");
        String summonerName = intent.getStringExtra("summonerName");
        imgExistence = intent.getIntExtra("imgExistence",0);
        postDate = intent.getLongExtra("postDate",0);

        et_modifyFreeBoardTitle.setText(postTitle);
        et_modifyFreeBoardContent.setText(postContent);

        photoList = new ArrayList<>();

        if(summonerName.equals("롤게더 익명")){
            cb_modifyAnonymity.setChecked(true);
        }

        if(imgExistence == 1){
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://lolgether.appspot.com").child(postId);
                modifyPhotoRecyclerView.setVisibility(View.VISIBLE);

                storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {

                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        photoList.add(task.getResult());
                                        firstPhotoCount = photoList.size();
                                        Adapter_freeBoardPhoto adapter = new Adapter_freeBoardPhoto(photoList,Activity_ModifyFreeBoardPost.this,"modify");
                                        modifyPhotoRecyclerView.setAdapter(adapter);
                                    } else {
                                        Toast.makeText(Activity_ModifyFreeBoardPost.this, "이미지를 불러오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void getCurrentUserInfo(){
        MainActivity mainActivity = new MainActivity();
        currentSummonerName = mainActivity.getCurrentSummonerName();
        currentSummonerTier = mainActivity.getCurrentSummonerTier();
        currentUserUid = mainActivity.getCurrentUserUid();
    }

    private void setPhotoRecyclerView(){ // 사진 띄어놓는 리사이클러뷰

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        modifyPhotoRecyclerView.setLayoutManager(mLinearLayoutManager);

        modifyPhotoRecyclerView.setVisibility(View.GONE);
    }
//네트워크 상태 check
    private void checkNetwork(){
        CheckNetwork checkNetwork = new CheckNetwork();

        int netWorkStatus = checkNetwork.CheckNetwork(getApplication());

        if(netWorkStatus == 0){
            Toast.makeText(getApplication(), "인터넷 연결을 확인해 주세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 사진 추가 버튼 설정.
    private void setBtn_postModifyAddPhoto(){
        btn_postModifyAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //사진을 여러개 선택할수 있도록 한다
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),  PICK_FROM_ALBUM);
            }
        });

    }
// 사진 추가 result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();

                if(photoList.size() != 0) {
                    photoList.clear();
                }

                modifyPhotoRecyclerView.setVisibility(View.GONE);

                if(clipData.getItemCount() > 10){
                    Toast.makeText(this, "사진은 10장까지만 가능합니다.", Toast.LENGTH_SHORT).show();

                    if(photoList.size() != 0) {
                        photoList.clear();
                    }

                    modifyPhotoRecyclerView.setVisibility(View.GONE);

                    return;
                }else if(clipData.getItemCount() > 0 && clipData.getItemCount() <= 10){

                    modifyPhoto = true;

                    if(photoList != null) {
                        photoList.clear();
                    }

                    for(int i = 0; i < clipData.getItemCount(); i++){
                        photoList.add(clipData.getItemAt(i).getUri());
                    }
                    modifyPhotoRecyclerView.setVisibility(View.VISIBLE);
                }
            }
            Adapter_freeBoardPhoto adapter = new Adapter_freeBoardPhoto(photoList,this,"modify");
            modifyPhotoRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void setBtn_freePostModifyWrite(){
        btn_freePostModifyWrite.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                uploadPhotoList = new ArrayList<>();
                finallyPhotoCount = photoList.size();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                String title = et_modifyFreeBoardTitle.getText().toString();
                String content = et_modifyFreeBoardContent.getText().toString();

                if(photoList.size() != 0){
                    imgExistence = 1;
                }else{
                    imgExistence = 0;
                }

                if(title.length() == 0){
                    Toast.makeText(Activity_ModifyFreeBoardPost.this, "제목을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                }else if(content.length() == 0){
                    Toast.makeText(Activity_ModifyFreeBoardPost.this, "내용을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                }else if(imgExistence == 1 && photoList.size() != 0 && modifyPhoto == true){

                    removeAllPhoto();

                        for(int i = 0; i < photoList.size(); i++){

                            Bitmap bit = resize(Activity_ModifyFreeBoardPost.this,photoList.get(i),500);

                                String path = getRealPathFromURI(photoList.get(i));

                                ExifInterface exif = null;
                                try {
                                    exif = new ExifInterface(path);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                        ExifInterface.ORIENTATION_UNDEFINED);

                                bit = rotateBitmap(bit, orientation);

                            uploadPhotoList.add(bit);
                        }

                    uploadAllPhoto();

                    }else if(photoList.size() == 0){

                        removeAllPhoto();

                }else if(imgExistence == 1 && modifyPhoto == false && photoList.size() != 0 && finallyPhotoCount != firstPhotoCount){

                    UriToURL();

                    if(uploadPhotoList.size() != 0) {
                        removeAllPhoto();
                        uploadAllPhoto();
                    }else{
                        Toast.makeText(Activity_ModifyFreeBoardPost.this, "문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }

                }
                    if (cb_modifyAnonymity.isChecked()) {
                        currentSummonerName = "롤게더 익명";
                        currentSummonerTier = "anonymity";

                    }else if(!cb_modifyAnonymity.isChecked()){
                        MainActivity mainActivity = new MainActivity();
                        currentSummonerName = mainActivity.getCurrentSummonerName();
                        currentSummonerTier = mainActivity.getCurrentSummonerTier();
                    }

                    Intent getInt = getIntent();
                    int commentC = getInt.getIntExtra("commentCount",0);

                    SaveFreeBoardPost saveFreeBoardPost = new SaveFreeBoardPost(postId, currentUserUid, currentSummonerName, currentSummonerTier, title, content,
                            commentC, postDate, imgExistence);

                    mDatabase.child("freeBoard").child(postId).setValue(saveFreeBoardPost);

                    Activity_freeBoard ac2 = (Activity_freeBoard) Activity_freeBoard.activity;
                    ac2.finish();

                    Intent intent = new Intent(Activity_ModifyFreeBoardPost.this, Activity_freeBoard.class);

                    finish();

                    startActivity(intent);

            }
        });
    }

    private Bitmap resize(Context context, Uri uri, int resize){

        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            // 비트팩토리 decodeStream매서드 사용
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 2;

            while (true) {//사이즈가 크다면 2로 나눈다
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
        return resizeBitmap; // 크기가 조정된 비트맵을 return한다.
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

    public static void delPhoto(int position){
        photoList.remove(position);
    }

    private void removeAllPhoto() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://lolgether.appspot.com");

        storageRef.child(postId).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.delete();
                }
            }
        });
    }

    private void uploadAllPhoto(){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://lolgether.appspot.com");

        Log.d("photo",uploadPhotoList.get(0)+"");

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

    private void UriToURL() {

        for(int i = 0; i< photoList.size(); i++){
            try{
            URLtoBitmap urLtoBitmap = new URLtoBitmap(new URL(photoList.get(i).toString()));
            Bitmap bit = urLtoBitmap.execute().get();
            uploadPhotoList.add(bit);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
