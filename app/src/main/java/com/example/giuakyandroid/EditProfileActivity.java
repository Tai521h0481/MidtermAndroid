package com.example.giuakyandroid;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.giuakyandroid.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.SimpleFormatter;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private Uri selectedImage;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private String url;
    private boolean checkUpload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        progressBar.setVisibility(View.GONE);

        getInfor(email);
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        clickOnSave(email);
        binding.personalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 299);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 299 && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            binding.personalImage.setImageURI(selectedImage);
            checkUpload = true;
        }
    }

    private Task<Uri> uploadImg(String email) {
        String fileName = email.split("@")[0];
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference avatarRef = storageRef.child("avatars/" + fileName);
        progressBar.setVisibility(View.VISIBLE);

        TaskCompletionSource<Uri> taskCompletionSource = new TaskCompletionSource<>();

        avatarRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url = uri.toString();
                        progressBar.setVisibility(View.GONE);
                        taskCompletionSource.setResult(uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Đặt ngoại lệ cho TaskCompletionSource nếu có lỗi xảy ra
                taskCompletionSource.setException(exception);
            }
        });
        return taskCompletionSource.getTask();
    }

    public void clickOnSave(String email) {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.edtName.getText().toString();
            String phone = binding.edtPhoneNumber.getText().toString();
            String age = binding.edtAge.getText().toString();
            String password = binding.edtPassword.getText().toString();
            if (name.isEmpty() || phone.isEmpty() || age.isEmpty() || password.isEmpty()) {
                return;
            } else if (password.length() < 6) {
                Toast.makeText(EditProfileActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            } else if (Integer.parseInt(age) > 80 || Integer.parseInt(age) < 18) {
                Toast.makeText(EditProfileActivity.this, "Invalid age", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users");
            Query query = usersRef.orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            String key = userSnapshot.getKey();
                            DatabaseReference userRef = usersRef.child(key);
                            userRef.child("name").setValue(name);
                            userRef.child("phone").setValue(phone);
                            userRef.child("age").setValue(Integer.parseInt(age));
                            userRef.child("password").setValue(password);
                            if (checkUpload) {
                                uploadImg(email).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = uri.toString();
                                        userRef.child("avatar").setValue(url);
                                        Toast.makeText(EditProfileActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();
                                        checkUpload = false;
                                    }
                                });
                            }
                            else{
                                Toast.makeText(EditProfileActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        });
    }

    private void getInfor(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        Query query = usersRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getAvatar())
                                .into(binding.personalImage);
                        binding.tvName.setText(user.getName().toString());
                        binding.tvEmail.setText(user.getEmail().toString());
                        binding.tvRole.setText(user.getRole().toString());
                        binding.edtName.setText(user.getName().toString());
                        binding.edtPhoneNumber.setText(user.getPhone().toString());
                        binding.edtAge.setText(user.getAge().toString());
                        binding.edtPassword.setText(user.getPassword().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}