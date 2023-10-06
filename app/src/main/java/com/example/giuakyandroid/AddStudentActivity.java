package com.example.giuakyandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class AddStudentActivity extends AppCompatActivity {
    EditText txtName, txtClassName, txtPhoneNumber, txtEmail, txtAge;
    Button btnAdd, btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        init();
        btnAdd.setOnClickListener(v -> {
            String name = txtName.getText().toString();
            String phoneNumber = txtPhoneNumber.getText().toString();
            String email = txtEmail.getText().toString().toLowerCase();
            String age = txtAge.getText().toString();
            String className = txtClassName.getText().toString();
            String role = "student";
            String avatar = "employee.png";
            if (name.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || age.isEmpty() || role.isEmpty()) {
                Toast.makeText(AddStudentActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            } else if (!email.contains("@")) {
                Toast.makeText(AddStudentActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                txtEmail.setText("");
                txtEmail.requestFocus();
                return;
            } else if (Integer.parseInt(age) < 6 || Integer.parseInt(age) > 80) {
                Toast.makeText(AddStudentActivity.this, "Age must be between 18 and 80", Toast.LENGTH_SHORT).show();
                txtAge.setText("");
                txtAge.requestFocus();
                return;
            } else {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String finalRole = role;
                String finalAvatar = avatar;
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();
                            if (signInMethods != null && !signInMethods.isEmpty()) {
                                Toast.makeText(AddStudentActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                                txtEmail.setText("");
                                txtEmail.requestFocus();
                            } else {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference avatarRef = storageRef.child("avatars/" + finalAvatar);
                                avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String avatarUrl = uri.toString();
                                        Student student = new Student(name, className, phoneNumber, email, Integer.parseInt(age), avatarUrl, new Certificate());
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference usersRef = database.getReference("students");
                                        String userId = usersRef.push().getKey();
                                        student.setStudentId(userId);
                                        usersRef.child(userId).setValue(student);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(AddStudentActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
                Toast.makeText(AddStudentActivity.this, "Add student successfully", Toast.LENGTH_SHORT).show();
                txtAge.setText("");
                txtEmail.setText("");
                txtName.setText("");
                txtClassName.setText("");
                txtPhoneNumber.setText("");
            }
        });
        btnClose.setOnClickListener(v -> {
            finish();
        });
    }

    public void init() {
        txtName = findViewById(R.id.txtName);
        txtClassName = findViewById(R.id.txtClassName);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtEmail = findViewById(R.id.txtEmail);
        txtAge = findViewById(R.id.txtAge);
        btnAdd = findViewById(R.id.btnAdd);
        btnClose = findViewById(R.id.btnClose);
    }
}