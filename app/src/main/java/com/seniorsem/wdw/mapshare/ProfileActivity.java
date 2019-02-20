package com.seniorsem.wdw.mapshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.username)
    TextView tvProfileUsername;
    @BindView(R.id.num_maps)
    TextView tvNumCreated;

    String documentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        documentKey = getIntent().getStringExtra("username");
        getProfileInfo();

    }

    private void getProfileInfo() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (documentKey == null){
            documentKey = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        db.collection("users").document(documentKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currUser = documentSnapshot.toObject(User.class);

                tvProfileUsername.setText(currUser.getUID());

                List<String> maps = currUser.getCreatedMaps();
                tvNumCreated.setText(String.valueOf(maps.size()));


            }
        });


        }
}