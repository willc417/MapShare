package com.seniorsem.wdw.mapshare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindFriendsActivity extends AppCompatActivity {

    @BindView(R.id.etSearchUsername)
    EditText etSearchUsername;
    @BindView(R.id.searchFriendBtn)
    Button searchFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.searchFriendBtn)
    void searchFriend(){

        final String search_entry = etSearchUsername.getText().toString();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(search_entry).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Intent ProfileIntent = new Intent();
                ProfileIntent.setClass(FindFriendsActivity.this, ProfileActivity.class);
                ProfileIntent.putExtra("username", search_entry);
                startActivity(ProfileIntent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FindFriendsActivity.this, "Invalid Username", Toast.LENGTH_SHORT).show();
            }
        });


    }



}
