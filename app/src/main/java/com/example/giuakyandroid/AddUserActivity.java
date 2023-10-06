package com.example.giuakyandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AddUserActivity extends AppCompatActivity {
    EditText txtName, txtPassword, txtPhoneNumber, txtEmail, txtAge;
    RadioButton rNormal, rLocked, rManager, rEmployee;
    Button btnAdd, btnClose;
    RadioGroup rStatus, rRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        init();
        btnAdd.setOnClickListener(v -> {
            String name = txtName.getText().toString();
            String password = txtPassword.getText().toString();
            String phoneNumber = txtPhoneNumber.getText().toString();
            String email = txtEmail.getText().toString().toLowerCase();
            String age = txtAge.getText().toString();
            String role = "employee";
            String status = "normal";
            String avatar = "employee.png";
            if (rLocked.isChecked()) {
                status = "locked";
            }
            if (rManager.isChecked()) {
                role = "manager";
                avatar = "manager.png";
            }
            if (name.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || age.isEmpty() || role.isEmpty() || status.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            } else if (!email.contains("@")) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                txtEmail.setText("");
                txtEmail.requestFocus();
                return;
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                txtPassword.setText("");
                txtPassword.requestFocus();
                return;
            } else if (Integer.parseInt(age) < 18 || Integer.parseInt(age) > 80) {
                Toast.makeText(this, "Age must be between 18 and 80", Toast.LENGTH_SHORT).show();
                txtAge.setText("");
                txtAge.requestFocus();
                return;
            } else {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String finalRole = role;
                String finalStatus = status;
                String finalAvatar = avatar;
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();
                            if (signInMethods != null && !signInMethods.isEmpty()) {
                                Toast.makeText(AddUserActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                                txtEmail.setText("");
                                txtEmail.requestFocus();
                            } else {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference avatarRef = storageRef.child("avatars/" + finalAvatar);
                                avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String avatarUrl = uri.toString();
                                        User user = new User(name, email, password, finalRole, finalStatus, Integer.parseInt(age), phoneNumber, avatarUrl, Long.parseLong("0"));
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference usersRef = database.getReference("users");
                                        String userId = usersRef.push().getKey();
                                        user.setUserId(userId);
                                        usersRef.child(userId).setValue(user);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(AddUserActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
                Toast.makeText(this, "Add user successfully", Toast.LENGTH_SHORT).show();
                txtAge.setText("");
                txtEmail.setText("");
                txtName.setText("");
                txtPassword.setText("");
                txtPhoneNumber.setText("");
                rStatus.clearCheck();
                rRole.clearCheck();
            }
        });
        btnClose.setOnClickListener(v -> {
            finish();
        });
    }

    public void init() {
        txtName = findViewById(R.id.txtName);
        txtPassword = findViewById(R.id.txtPassword);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtEmail = findViewById(R.id.txtEmail);
        txtAge = findViewById(R.id.txtAge);
        rNormal = findViewById(R.id.rNormal);
        rLocked = findViewById(R.id.rLocked);
        rManager = findViewById(R.id.rManager);
        rEmployee = findViewById(R.id.rEmployee);
        btnAdd = findViewById(R.id.btnAdd);
        btnClose = findViewById(R.id.btnClose);
        rStatus = findViewById(R.id.rStatus);
        rRole = findViewById(R.id.rRole);
    }
}