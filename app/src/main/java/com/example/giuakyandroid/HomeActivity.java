package com.example.giuakyandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.LinearLayout;

import com.example.giuakyandroid.databinding.ActivityHomeBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private String email;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");
    private Query query;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        getInfo(email);
        binding.layoutAdd.setOnClickListener(v -> {
            if(user.getRole().equalsIgnoreCase("manager")){
                Intent add = new Intent(HomeActivity.this, AddStudentActivity.class);
                startActivity(add);
            }
            else if(user.getRole().equalsIgnoreCase("admin")){
                Intent add = new Intent(HomeActivity.this, AddUserActivity.class);
                startActivity(add);
            }
        });
        binding.layoutProfile.setOnClickListener(v -> {
            Intent edit = new Intent(HomeActivity.this, EditProfileActivity.class);
            edit.putExtra("email", email);
            startActivity(edit);
        });
        binding.btnBack.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the image when returning to the activity
        getInfo(email);
    }
    public void getInfo(String email){
        query = usersRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        user = userSnapshot.getValue(User.class);
                        String role = user.getRole().toString();
                        if(role.equals("employee")){
                            binding.layoutAdd.setVisibility(LinearLayout.GONE);
                        }
                        Picasso.get()
                                .load(user.getAvatar())
                                .into(binding.personalImage);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}