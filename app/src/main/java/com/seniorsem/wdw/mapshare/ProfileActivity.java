package com.seniorsem.wdw.mapshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.username)
    TextView tvProfileUsername;
    @BindView(R.id.num_maps)
    TextView tvNumCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);


        //get the created maps of a user
        getProfileInfo();

    }

    private void getProfileInfo() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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