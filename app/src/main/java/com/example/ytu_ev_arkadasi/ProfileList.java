package com.example.ytu_ev_arkadasi;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileList extends AppCompatActivity {

    public static ArrayList<ProfileModel> announcementList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private DatabaseReference mReference2;
    private HashMap<String, Object> mData;
    private FirebaseUser mUser;

    String name, status;
    ImageView photo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profilelist);

        ProfileListAdapter adapter = new ProfileListAdapter(announcementList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.profilelist_recycler_view);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        viewProfiles();
    }

    public void viewProfiles(){
        //mUser = mAuth.getCurrentUser();

        //assert mUser != null;
        mReference = FirebaseDatabase.getInstance().getReference("Users");
        mReference.child("baslik").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcementList.clear();
                for(DataSnapshot data: snapshot.getChildren()){
                    assert data.getKey() != null;
                    assert data.getValue() != null;

                    if(data.getKey().equals("baslik")){
                        name = data.child("baslik").getValue(ProfileModel.class).name.toString();
                    }
                    else if(data.getKey().equals("tarih")){
                        status = data.child("tarih").getValue(ProfileModel.class).status.toString();
                    }
                    announcementList.add(new ProfileModel(R.drawable.ytu_logo, name, status));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
