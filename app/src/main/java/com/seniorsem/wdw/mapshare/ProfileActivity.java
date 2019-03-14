package com.seniorsem.wdw.mapshare;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    @BindView(R.id.profile_picture)
    ImageView ivProfilePic;
    @BindView(R.id.change_profile_picture)
    TextView tvChangeProfilePicture;

    private static final int RESULT_LOAD_IMAGE = 1;

    String documentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        documentKey = getIntent().getStringExtra("username");
        getProfileInfo();

        tvChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);


            }
        });
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

                Uri profilePicUri = Uri.parse(currUser.getProfilePicture());
                ivProfilePic.setImageURI(profilePicUri);

            }
        });


        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            final Uri selectedImage = data.getData();
            ivProfilePic.setImageURI(selectedImage);
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currUser = documentSnapshot.toObject(User.class);
                    String uriString = selectedImage.toString();
                    currUser.setProfilePicture(uriString);
                    db.collection("users").document(currUser.getUID()).update("profilePicture", uriString);
                }
            });
        }
    }
}