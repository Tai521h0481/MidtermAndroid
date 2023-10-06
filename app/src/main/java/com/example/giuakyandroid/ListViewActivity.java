package com.example.giuakyandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.giuakyandroid.databinding.ActivityListViewBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListViewActivity extends AppCompatActivity implements OnUserDeletedListener{
    private ActivityListViewBinding binding;
    private RecyclerView mRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<User> userArrayListAgeSorted;
    private ArrayList<User> userArrayListNameSorted;
    private ArrayList<User> userArrayListAll;
    private List<User> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        displayView("admin");
//        displayView("manager");

    }
    public void displayView(String role){
        String attribute = "role";
        String type1 = "employee";
        String type2 = "manager";
//        if(role.equalsIgnoreCase("admin")){
//            type1 = "manager";
//            type2 = "employee";
//        }else if(role.equalsIgnoreCase("manager")){
//            type1 = "employee";
//        }
//        else {
//
//        }
        List<User> data = new ArrayList<>();
        filter(attribute, type1, type2, new UsersCallback() {
            @Override
            public void onCallback(List<User> users) {
//                for (User user : users) {
//                    // Xử lý từng User
//                }
                data.addAll(users);
                mRecycleView = findViewById(R.id.recycleView);
                mRecycleView.setHasFixedSize(true);

                mLayoutManager = new LinearLayoutManager(ListViewActivity.this);
                mRecycleView.setLayoutManager(mLayoutManager);
                mAdapter = new MyAdapter(ListViewActivity.this, data, ListViewActivity.this);

                mRecycleView.setAdapter(mAdapter);
                mRecycleView.addItemDecoration(new DividerItemDecoration(ListViewActivity.this, DividerItemDecoration.VERTICAL));
                userArrayListAll = new ArrayList<>(data);

                Collections.sort(data, new Comparator<User>() {
                    @Override
                    public int compare(User user1, User user2) {
                        return user1.getName().compareTo(user2.getName());
                    }
                });
                userArrayListNameSorted = new ArrayList<>(data);

                // Sorting by age
                Collections.sort(data, new Comparator<User>() {
                    @Override
                    public int compare(User user1, User user2) {
                        return Integer.compare(user1.getAge(), user2.getAge());
                    }
                });
                userArrayListAgeSorted = new ArrayList<>(data);
            }
        });
        binding.btnVLName.setOnClickListener(this::onClick);
        binding.btnVLAll.setOnClickListener(this::onClick);
        binding.btnVLAge.setOnClickListener(this::onClick);
    }
    private void updateButtonBackgrounds(Button selectedButton) {
        binding.btnVLName.setBackgroundResource(selectedButton == binding.btnVLName ? R.drawable.round_button_sort_type : android.R.color.transparent);
        binding.btnVLAll.setBackgroundResource(selectedButton == binding.btnVLAll ? R.drawable.round_button_sort_type : android.R.color.transparent);
        binding.btnVLAge.setBackgroundResource(selectedButton == binding.btnVLAge ? R.drawable.round_button_sort_type : android.R.color.transparent);
    }

    public void onClick(View view) {
        if (view == binding.btnVLName) {
            MyAdapter listAdapterName = new MyAdapter(ListViewActivity.this, userArrayListNameSorted, ListViewActivity.this);
            binding.recycleView.setAdapter(listAdapterName);
            updateButtonBackgrounds(binding.btnVLName);
        } else if (view == binding.btnVLAll) {
            MyAdapter listAdapterAll = new MyAdapter(ListViewActivity.this, userArrayListAll, ListViewActivity.this);
            binding.recycleView.setAdapter(listAdapterAll);
            updateButtonBackgrounds(binding.btnVLAll);
        } else if (view == binding.btnVLAge) {
            MyAdapter listAdapterAge = new MyAdapter(ListViewActivity.this, userArrayListAgeSorted, ListViewActivity.this);
            binding.recycleView.setAdapter(listAdapterAge);
            updateButtonBackgrounds(binding.btnVLAge);
        }
    }

    @Override
    public void onUserDeleted(User user) {
        Log.d("TAG", "onUserDeleted: " + user);
        userArrayListAgeSorted.remove(user);
        userArrayListNameSorted.remove(user);
        userArrayListAll.remove(user);
    }

    public interface UsersCallback {
        void onCallback(List<User> users);
    }

    public void filter(String attribute, String type1, String type2, UsersCallback usersCallback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef;
//        if (type2.equals("")) {
//            query = usersRef.orderByChild(attribute).equalTo(type1);
//        } else {
//            query = usersRef.orderByChild(attribute).startAt(type1).endAt(type2 + "\uf8ff");
//        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }
                usersCallback.onCallback(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "onCancelled", databaseError.toException());
            }
        });
    }
}