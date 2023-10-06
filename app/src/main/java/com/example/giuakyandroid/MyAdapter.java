package com.example.giuakyandroid;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    private Context context;
    ActivityResultLauncher<Intent> launcher;
    AppCompatActivity activity;
    private List<User> data;
    private OnUserDeletedListener listener;

    public MyAdapter(List<User> data) {
        this.data = data;
    }

    public MyAdapter(Context context, List<User> userArrayList, ActivityResultLauncher<Intent> launcher) {
        this.data = userArrayList;
        this.context = context;
        this.activity = (AppCompatActivity) context;
        this.launcher = launcher;
    }
    public MyAdapter(Context context, List<User> userArrayList, OnUserDeletedListener listener) {
        this.data = userArrayList;
        this.context = context;
        this.activity = (AppCompatActivity) context;
        this.listener = listener;
    }

    public MyAdapter(Context context, List<User> userArrayList) {
        this.data = userArrayList;
        this.context = context;
        this.activity = (AppCompatActivity) context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_my_adapter, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        User item = data.get(position);
        myHolder.txtName.setText(item.getName());
        myHolder.txtAge.setText((item.getAge()) + "");
        myHolder.txtRole.setText(item.getRole());
        Picasso.get()
                .load(item.getAvatar())
                .into(myHolder.imageId);
        myHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
                builder.setTitle("Confirmation");
                builder.setMessage("Do you want to remove this user?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = myHolder.getAdapterPosition();
                        User user = data.get(position);
                        if (user.getUserId() != null) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference usersRef = database.getReference("users");
                            usersRef.child(user.getUserId()).removeValue();

                            data.remove(position);

                            notifyItemRemoved(position);
                            if(listener == null){
                                Log.d("TAG", "onClick: listener is null");
                            }
                            if (listener != null) {
                                listener.onUserDeleted(user);
                                Log.d("TAG", "onClick: listener != null");

                            }
                        } else {
                            Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtAge, txtRole;
        public CircleImageView imageId;
        public ImageView btnDelete, btnEdit;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            txtAge = itemView.findViewById(R.id.txtAge);
            txtName = itemView.findViewById(R.id.txtName);
            txtRole = itemView.findViewById(R.id.txtRole);
            imageId = itemView.findViewById(R.id.imageId);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
