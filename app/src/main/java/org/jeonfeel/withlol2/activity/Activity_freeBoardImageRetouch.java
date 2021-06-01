package org.jeonfeel.withlol2.activity;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import java.util.ArrayList;

public class Activity_freeBoardImageRetouch extends AppCompatActivity {

    Button btn_freeBoardRetouchBackspace,btn_imageRetouchAddPhoto,btn_RetouchWrite,btn_imageReset;
    RecyclerView imageRetouchPhotoRecyclerView;
    final int PICK_FROM_ALBUM = 111;
    String currentSummonerName,currentSummonerTier,currentUserUid;
    String postId;
    int imgExistence;
    private DatabaseReference mDatabase;
    public static ArrayList<Bitmap> uploadPhotoList;
    Adapter_freeBoardPhoto adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_board_image_retouch);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFindViewById();
        setPhotoRecyclerView();
        getInfo();

        setBtn_addPhoto();
        try {
            setBtn_postWrite();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBtn_imageReset();
    }

    private void mFindViewById(){
        btn_freeBoardRetouchBackspace = findViewById(R.id.btn_freeBoardRetouchBackspace);
        btn_imageRetouchAddPhoto = findViewById(R.id.btn_imageRetouchAddPhoto);
        btn_RetouchWrite = findViewById(R.id.btn_RetouchWrite);
        btn_imageReset = findViewById(R.id.btn_imageReset);
    }

    private void getInfo(){
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
    }

    private void setPhotoRecyclerView(){

        imageRetouchPhotoRecyclerView = (RecyclerView) findViewById(R.id.imageRetouchPhotoRecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        imageRetouchPhotoRecyclerView.setLayoutManager(mLinearLayoutManager);

        imageRetouchPhotoRecyclerView.setVisibility(View.GONE);

    }

    private void setBtn_addPhoto(){

        btn_imageRetouchAddPhoto.setOnClickListener(new View.OnClickListener() {
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

    private void setBtn_imageReset(){

        btn_imageReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_freeBoardImageRetouch.this)
                        .setMessage("현재 게시글에 업로드된 이미지를 전부 삭제합니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://lolgether.appspot.com").child(postId);

                                storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        for(StorageReference item : listResult.getItems()){
                                            item.delete();
                                        }
                                    }
                                });

                                imgExistence = 0;
                                mDatabase.child("freeBoard").child(postId).child("imgExist").setValue(0);

                                Activity_freeBoard ac = (Activity_freeBoard) Activity_freeBoard.activity;
                                ac.finish();

                                Intent intent = new Intent(Activity_freeBoardImageRetouch.this, Activity_freeBoard.class);

                                finish();

                                startActivity(intent);
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                imgExistence = 0;
                imageRetouchPhotoRecyclerView.setVisibility(View.GONE);
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

                            Bitmap bitImg = resize(Activity_freeBoardImageRetouch.this, clipData.getItemAt(i).getUri(), 700);
                            Log.d("path",String.valueOf(clipData.getItemAt(i).getUri()));

                            Bitmap bmRotated = rotateBitmap(bitImg, orientation);

                            uploadPhotoList.add(bmRotated);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    imageRetouchPhotoRecyclerView.setVisibility(View.VISIBLE);
                    imgExistence = 1;
                }
            }
            adapter = new Adapter_freeBoardPhoto(uploadPhotoList,this,imageRetouchPhotoRecyclerView,postId);
            imageRetouchPhotoRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public static void delPhoto(int position){
        uploadPhotoList.remove(position);
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

    private void setBtn_postWrite() throws IOException {

        btn_RetouchWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://lolgether.appspot.com").child(postId);

                storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for(StorageReference item : listResult.getItems()){
                            item.delete();
                        }
                    }
                });

                if(imgExistence == 1) {

                    for (int i = 0; i < uploadPhotoList.size(); i++) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        uploadPhotoList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        storageRef.child(i+"")
                                .putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        });
                    }
                    imgExistence = 0;
                    mDatabase.child("freeBoard").child(postId).child("imgExist").setValue(1);
                }

                Activity_freeBoard ac = (Activity_freeBoard) Activity_freeBoard.activity;
                ac.finish();

                Intent intent = new Intent(Activity_freeBoardImageRetouch.this, Activity_freeBoard.class);

                finish();

                startActivity(intent);
            }
        });
    }

}
