package com.codestudioapps.chattingapp.shareChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.codestudioapps.chattingapp.adapter.AdapterChatShareUsers;
import com.codestudioapps.chattingapp.MainActivity;
import com.codestudioapps.chattingapp.model.ModelUser;
import com.codestudioapps.chattingapp.R;
import com.codestudioapps.chattingapp.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShareChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterChatShareUsers adapterUsers;
    List<ModelUser> userList;
    EditText editText;
    ImageView imageView3;
    ProgressBar pg;
    private static String postId;
    SharedPref sharedPref;
    public static String getPostId() {
        return postId;
    }


    public ShareChatActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_chat);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        recyclerView = findViewById(R.id.users);
        imageView3 = findViewById(R.id.imageView3);
        pg = findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);

        imageView3.setOnClickListener(v -> {
            Intent intent1 = new Intent(ShareChatActivity.this, MainActivity.class);
           startActivity(intent1);
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShareChatActivity.this));
        editText = findViewById(R.id.password);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    pg.setVisibility(View.VISIBLE);
                    filter(s.toString());
                }else {
                    getAllUsers();
                }

            }
        });
        userList = new ArrayList<>();
        getAllUsers();

    }

    private void filter(final String query) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelUser).getId())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                            pg.setVisibility(View.GONE);
                        }
                    }
                    adapterUsers = new AdapterChatShareUsers(ShareChatActivity.this, userList);
                    adapterUsers.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterUsers);
                    pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getAllUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelUser).getId())){
                        userList.add(modelUser);
                        pg.setVisibility(View.GONE);
                    }
                    adapterUsers = new AdapterChatShareUsers(ShareChatActivity.this, userList);
                    recyclerView.setAdapter(adapterUsers);
                    pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
