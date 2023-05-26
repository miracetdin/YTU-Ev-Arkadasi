package com.example.ytu_ev_arkadasi;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Register extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;

    EditText name, surname, phone, department, grade, email, password, password2;
    ImageView profilePhoto;
    Button takePhoto, selectFromGallery, register;
    RadioGroup radioGroupLooking, radioGroupTime, radioGroupDistance;
    RadioButton radioButtonLooking, radioButtonTime, radioButtonDistance;

    String currentPhotoPath, looking, time, distance;

    Uri photoUri;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private HashMap<String, Object> mData;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.register_et_name);
        surname = (EditText) findViewById(R.id.register_et_surname);
        phone = (EditText) findViewById(R.id.register_et_phone);
        department = (EditText) findViewById(R.id.register_et_department);
        grade = (EditText) findViewById(R.id.register_et_grade);
        email = (EditText) findViewById(R.id.register_et_email);
        password = (EditText) findViewById(R.id.register_et_password);
        password2 = (EditText) findViewById(R.id.register_et_password2);

        profilePhoto = (ImageView) findViewById(R.id.register_iv_profilephoto);

        takePhoto = (Button) findViewById(R.id.register_bt_takephoto);
        selectFromGallery = (Button) findViewById(R.id.register_bt_selectphoto);
        register = (Button) findViewById(R.id.register_bt_register);

        radioGroupLooking = (RadioGroup) findViewById(R.id.register_rg_looking);
        radioGroupTime = (RadioGroup) findViewById(R.id.register_rg_time);
        radioGroupDistance = (RadioGroup) findViewById(R.id.register_rg_distance);

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFunc();
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissions();
            }
        });

        selectFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQUEST_CODE);
            }
        });
    }

    // user register function
    public void registerFunc(){
        if((!TextUtils.isEmpty(email.getText().toString())) && (!TextUtils.isEmpty(password.getText().toString()))
                && (!TextUtils.isEmpty(password2.getText().toString()))){
            if((email.getText().toString().contains("std.yildiz.edu.tr"))) {
                if ((password.getText().toString().equals(password2.getText().toString()))) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        getData();
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(this, "Girilen şifreler aynı değil!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Sadece std.yildiz.edu.tr uzantılı mail adresi ile kayıt olabilirsiniz!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Email ve Şifre alanları boş bırakılamaz!", Toast.LENGTH_SHORT).show();
        }
    }

    // getting user's data
    public void getData(){
        mData = new HashMap<>();
        mUser = mAuth.getCurrentUser();

        mData.put("ID", mUser.getUid());
        mData.put("name", name.getText().toString());
        mData.put("surname", surname.getText().toString());
        mData.put("phone", phone.getText().toString());
        mData.put("department", department.getText().toString());
        mData.put("grade", grade.getText().toString());
        mData.put("email", email.getText().toString());
        mData.put("password", password.getText().toString());
        mData.put("password2", password2.getText().toString());
        mData.put("looking", looking);
        mData.put("time", time);
        mData.put("distance", distance);

        mReference.child("Users").child(mUser.getUid()).setValue(mData)
                .addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Kayıt işlemi başarılı!\nGiriş ekranına yönlendiriliyorsunuz...", Toast.LENGTH_SHORT).show();                        }
                        else{
                            System.out.println("Kaydedilemedi");
                            Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        uploadPhoto();
    }

    // reading radio button groups
    public void checklooking(View view){
        int radioId = radioGroupLooking.getCheckedRadioButtonId();
        radioButtonLooking = findViewById(radioId);
        Toast.makeText(this, "Seçilen Durum: " + radioButtonLooking.getText(), Toast.LENGTH_SHORT).show();
        looking = radioButtonLooking.getText().toString();
    }

    public void checktime(View view){
        int radioId = radioGroupTime.getCheckedRadioButtonId();
        radioButtonTime = findViewById(radioId);
        Toast.makeText(this, "Seçilen Zaman Aralığı: " + radioButtonTime.getText(), Toast.LENGTH_SHORT).show();
        time = radioButtonTime.getText().toString();
    }

    public void checkdistance(View view){
        int radioId = radioGroupDistance.getCheckedRadioButtonId();
        radioButtonDistance = findViewById(radioId);
        Toast.makeText(this, "Seçilen Mesafe: " + radioButtonDistance.getText(), Toast.LENGTH_SHORT).show();
        distance = radioButtonDistance.getText().toString();
    }

    // taking a photo via camera
    private void verifyPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERM_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERM_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Kamerayı kullanmak için izin gerekmektedir!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePıctureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePıctureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            }
            catch (IOException exception) {
                Toast.makeText(this, "Fotoğraf dosyası oluşturulamadı!", Toast.LENGTH_SHORT).show();
            }

            if(photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.example.ytu_ev_arkadasi", photoFile);
                takePıctureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePıctureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "YTU_Mezun_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                profilePhoto.setImageURI(Uri.fromFile(f));
            }
        }
        else if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQUEST_CODE){
                profilePhoto.setImageURI(data.getData());
            }
        }
    }

    public void uploadPhoto(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Görsel yükleniyor...");
        pd.show();

        // final String randomKey = UUID.randomUUID().toString();
        StorageReference mSReference = mStorageReference.child("users/" + mUser.getEmail());

        mSReference.putFile(photoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Fotoğraf yüklendi!", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(Register.this, "Fotoğraf yüklenemedi!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("İlerleme: %" + (int) progressPercent);
                    }
                });
    }

    // pick a photo from gallery
    /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQUEST_CODE){
                profilePhoto.setImageURI(data.getData());
            }
        }
    } */
}