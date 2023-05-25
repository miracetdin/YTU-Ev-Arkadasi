package com.example.ytu_ev_arkadasi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button login, register, resetPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.login_et_email);
        password = (EditText) findViewById(R.id.login_et_password);

        login = (Button) findViewById(R.id.login_bt_login);
        register = (Button) findViewById(R.id.login_bt_register);
        resetPassword = (Button) findViewById(R.id.login_bt_resetpassword);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFunc();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(intent);
            }
        });
    }

    public void loginFunc(){
        if((!TextUtils.isEmpty(email.getText().toString())) && (!TextUtils.isEmpty(password.getText().toString()))){
            if(email.getText().toString().contains("std.yildiz.edu.tr")) {
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                mUser = mAuth.getCurrentUser();
                                Toast.makeText(Login.this, "Hoşgeldin \n" + mUser.getEmail(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else{
                Toast.makeText(this, "Sadece std.yildiz.edu.tr uzantılı mail adresi ile giriş yapabilirisiniz!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(Login.this, "E-Posta ya da Şifre Boş Bırakılamaz!", Toast.LENGTH_SHORT).show();
        }
    }
}