package com.example.ytu_ev_arkadasi;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Profile extends AppCompatActivity {

    TextView name, surname, mail, password, department, grade, status, time, distance, phone;
    ImageView profilePhoto;
    Button whatsapp, update;

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private HashMap<String, Object> mData;
    private FirebaseUser mUser;
    private StorageReference mStorageReference;
    String phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.profile_tv_name);
        surname = (TextView) findViewById(R.id.profile_tv_surname);
        mail = (TextView) findViewById(R.id.profile_tv_email);
        password = (TextView) findViewById(R.id.profile_tv_password);
        department = (TextView) findViewById(R.id.profile_tv_department);
        grade = (TextView) findViewById(R.id.profile_tv_grade);
        status = (TextView) findViewById(R.id.profile_tv_status);
        time = (TextView) findViewById(R.id.profile_tv_time);
        distance = (TextView) findViewById(R.id.profile_tv_distance);
        phone = (TextView) findViewById(R.id.profile_tv_phone);

        profilePhoto = (ImageView) findViewById(R.id.profile_iv_photo);

        whatsapp = (Button) findViewById(R.id.profile_bt_whatsapp);
        update = (Button) findViewById(R.id.profile_bt_update);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        viewUserInfo();

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;

                try {
                    PackageManager pm = getApplicationContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
                catch (PackageManager.NameNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
        });
    }

    public void viewUserInfo(){
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()) {
                    assert data.getKey() != null;
                    assert data.getValue() != null;

                    if (data.getKey().equals("name")) {
                        name.setText(data.getValue().toString());
                    } else if (data.getKey().equals("surname")) {
                        surname.setText(data.getValue().toString());
                    } else if (data.getKey().equals("email")) {
                        mail.setText(data.getValue().toString());
                    } else if (data.getKey().equals("password")) {
                        password.setText(data.getValue().toString());
                    } else if (data.getKey().equals("department")) {
                        department.setText(data.getValue().toString());
                    } else if (data.getKey().equals("grade")) {
                        grade.setText(data.getValue().toString());
                    } else if (data.getKey().equals("status")) {
                        status.setText(data.getValue().toString());
                    } else if (data.getKey().equals("time")) {
                        time.setText(data.getValue().toString());
                    } else if (data.getKey().equals("distance")) {
                        distance.setText(data.getValue().toString());
                    } else if (data.getKey().equals("phone")) {
                        phone.setText(data.getValue().toString());
                        phoneNumber = data.getValue().toString();
                    }
                }

                mStorageReference = FirebaseStorage.getInstance().getReference().child("users/" + mUser.getEmail());

                try {
                    final File localFile = File.createTempFile(mUser.getEmail(), "jpg");
                    mStorageReference.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    profilePhoto.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Profile.this, "Profil fotoğrafı yüklenemedi!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
