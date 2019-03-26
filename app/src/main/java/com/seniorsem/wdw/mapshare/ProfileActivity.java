package com.seniorsem.wdw.mapshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.seniorsem.wdw.mapshare.data.User;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.username)
    TextView tvProfileUsername;
    @BindView(R.id.num_maps)
    TextView tvNumCreated;
    @BindView(R.id.profile_picture)
    ImageView ivProfilePic;
    @BindView(R.id.viewMapsBtn)
    Button viewMapsBtn;
    @BindView(R.id.findFriendsBtn)
    Button findFriendsBtn;
    @BindView(R.id.num_followed)
    TextView tvNumberFollowed;


    private static final int RESULT_LOAD_IMAGE = 1;

    String documentKey;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();


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

        if (documentKey == null) {
            documentKey = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            findFriendsBtn.setVisibility(View.VISIBLE);
        }



        db.collection("users").document(documentKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currUser = documentSnapshot.toObject(User.class);

                tvProfileUsername.setText(currUser.getUID());

                List<String> maps = currUser.getCreatedMaps();
                tvNumCreated.setText(String.valueOf(maps.size()));


                List<String> subMaps = currUser.getSubMaps();
                tvNumberFollowed.setText(String.valueOf(subMaps.size()));

                Uri profilePicUri = Uri.parse(currUser.getProfilePicture());
                ivProfilePic.setImageURI(profilePicUri);

                String profilePicString = currUser.getProfilePicture();


                Glide.with(ProfileActivity.this).load(profilePicString).into(ivProfilePic);
            }
            });

    }

    @OnClick(R.id.viewMapsBtn)
    void viewMaps() {
        Intent ProfileIntent = new Intent();
        ProfileIntent.setClass(ProfileActivity.this, ViewMapsActivity.class);
        ProfileIntent.putExtra("username", documentKey);
        startActivity(ProfileIntent);
    }

    @OnClick(R.id.findFriendsBtn)
    void findFriends() {
        Intent ProfileIntent = new Intent();
        ProfileIntent.setClass(ProfileActivity.this, FindFriendsActivity.class);
        ProfileIntent.putExtra("username", documentKey);
        startActivity(ProfileIntent);
    }




   /* @Override
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
    } */

    @OnClick(R.id.profile_picture)
    void attachClick() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentCamera, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivProfilePic.setImageBitmap(imageBitmap);
            ivProfilePic.setVisibility(View.VISIBLE);
            try {
                uploadPostToStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadPostToStorage() throws Exception {
        ivProfilePic.setDrawingCacheEnabled(true);
        ivProfilePic.buildDrawingCache();
        Bitmap bitmap = ivProfilePic.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInBytes = baos.toByteArray();

        String newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg";
        final StorageReference newImageRef = storageRef.child(newImage);
        final StorageReference newImageImagesRef = storageRef.child("images/" + newImage);
        newImageRef.getName().equals(newImageImagesRef.getName());    // true
        newImageRef.getPath().equals(newImageImagesRef.getPath());    // false

        UploadTask uploadTask = newImageImagesRef.putBytes(imageInBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(ProfileActivity.this, "Profile Photo Not uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Toast.makeText(ProfileActivity.this, "Profile Photo Uploaded", Toast.LENGTH_SHORT).show();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users").document(documentKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User currUser = documentSnapshot.toObject(User.class);
                        String i_url = taskSnapshot.getDownloadUrl().toString();
                        db.collection("users").document(currUser.getUID()).update("profilePicture", taskSnapshot.getDownloadUrl().toString());
                        Glide.with(ProfileActivity.this).load(i_url).into(ivProfilePic);
                    }
                });

            }

        });

    }
}