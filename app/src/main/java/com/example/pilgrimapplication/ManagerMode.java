package com.example.pilgrimapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class ManagerMode extends AppCompatActivity {
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String imagePath;
    private String imageName;
    private  final int GALLERY_CODE=0;
    Button btn_choicePicture,btn_galleryUpdate,btn_noticeRegistry,btn_surveyPrepare;
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_mode);
        String[] PERMISSIONS ={
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        if(!hasPermissions(this, PERMISSIONS)){
            //권한요청
            ActivityCompat.requestPermissions(this, PERMISSIONS,1);
        }
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("GalleryUris");
        firebaseStorage=FirebaseStorage.getInstance();
        btn_choicePicture=findViewById(R.id.btn_pictureChoice);
        btn_galleryUpdate=findViewById(R.id.btn_gallleryUpdate);
        btn_noticeRegistry=findViewById(R.id.btn_manger_noti);
        btn_surveyPrepare=findViewById(R.id.btn_surveyPrepare);

        btn_choicePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,GALLERY_CODE);
            }
        });
        btn_galleryUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadFile(imagePath);
                }catch(NullPointerException e){

                }
            }
        });
        btn_noticeRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplication(),SurveyRegister.class);
                startActivity(intent);

            }
        });
        btn_surveyPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePath=getPath(data.getData());

    }
    //갤러리에서 선택한 이미지에 uri를 구한다.
    public String getPath(Uri uri){
        String[] proj={MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader=new CursorLoader(this,uri,proj,null,null,null);
        Cursor cursor=cursorLoader.loadInBackground();
        int index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }
    private void uploadFile(String uri) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("업로드중...");
        if (imagePath != null)
            progressDialog.show();
        else
            Toast.makeText(this, "먼저 이미지를 선택해주세요", Toast.LENGTH_SHORT).show();

        StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://pilgrimapplication.appspot.com/");
        Uri file = Uri.fromFile(new File(uri));
        imageName= file.getLastPathSegment();
        StorageReference riversRef = storageRef.child("images/" + imageName);
        UploadTask uploadTask = riversRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                Toast.makeText(getApplicationContext(), "데이터베이스 업로드 완료!", Toast.LENGTH_SHORT).show();
                imgDeliver(imageName);//Firebase storage에 이미지를 넣는 것이 성공하면 실행 됨.
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");

            }
        });
    }
    //Firebase storage에 들어간 이미지의 uri를 Firebase Database의 집어넣음
    private void imgDeliver(String name){
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://pilgrimapplication.appspot.com/").child("images");
        storageRef.child(name).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri=task.getResult();
                ImageRD imageDTO=new ImageRD();
                imageDTO.imageUrI=downloadUri.toString();
                databaseReference.push().setValue(imageDTO);//클래스의 형태로 Database에 넣음.

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ManagerMode.this, "Gallery 사진 등록 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
