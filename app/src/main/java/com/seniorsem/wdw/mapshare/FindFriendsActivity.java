package com.seniorsem.wdw.mapshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.adapter.ViewFriendsRecyclerAdapter;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindFriendsActivity extends AppCompatActivity {

    @BindView(R.id.etSearchUsername)
    EditText etSearchUsername;
    @BindView(R.id.friendSearch_TIL)
    TextInputLayout searchFriend_TIL;
    @BindView(R.id.searchFriendBtn)
    Button searchFriend;

    ViewFriendsRecyclerAdapter viewFollowingRecyclerAdapter;
    ViewFriendsRecyclerAdapter viewFollowersRecyclerAdapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();

        viewFollowingRecyclerAdapter = new ViewFriendsRecyclerAdapter(this, 0);
        viewFollowersRecyclerAdapter = new ViewFriendsRecyclerAdapter(this, 1);

        RecyclerView recyclerViewFollowing = findViewById(R.id.recyclerViewFollowing);
        RecyclerView recyclerViewFollowers = findViewById(R.id.recyclerViewFollowers);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewFollowing.setLayoutManager(layoutManager);
        recyclerViewFollowing.setAdapter(viewFollowingRecyclerAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setReverseLayout(true);
        layoutManager2.setStackFromEnd(true);
        recyclerViewFollowers.setLayoutManager(layoutManager2);
        recyclerViewFollowers.setAdapter(viewFollowersRecyclerAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        viewFollowingRecyclerAdapter.removeAll();
        viewFollowersRecyclerAdapter.removeAll();
        initFriends();
    }


    private void initFriends() {
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currUser = documentSnapshot.toObject(User.class);
                List<String> following = currUser.getFollowing();


                for (int i = 0; i < following.size(); i++) {
                    db.collection("users").document(following.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User friend = documentSnapshot.toObject(User.class);
                            viewFollowingRecyclerAdapter.addFriend(friend, documentSnapshot.getId());
                        }
                    });
                }

                List<String> followers = currUser.getFollowers();
                for (int i = 0; i < followers.size(); i++) {
                    db.collection("users").document(followers.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User friend = documentSnapshot.toObject(User.class);
                            viewFollowersRecyclerAdapter.addFriend(friend, documentSnapshot.getId());
                        }
                    });
                }

            }
        });


    }

    @OnClick(R.id.searchFriendBtn)
    void searchFriend() {

        final String search_entry = etSearchUsername.getText().toString();

        if (search_entry.isEmpty()) {
            Toast.makeText(FindFriendsActivity.this, "Enter a username", Toast.LENGTH_SHORT).show();
        } else {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(search_entry).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Intent ProfileIntent = new Intent();
                        ProfileIntent.setClass(FindFriendsActivity.this, ProfileActivity.class);
                        ProfileIntent.putExtra("username", search_entry);
                        startActivity(ProfileIntent);
                    } else {
                        Toast.makeText(FindFriendsActivity.this, "Invalid Username", Toast.LENGTH_SHORT).show();
                        etSearchUsername.setText("");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FindFriendsActivity.this, "Invalid Username", Toast.LENGTH_SHORT).show();
                    etSearchUsername.setText("");
                }
            });
        }
    }

}
