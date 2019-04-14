package com.seniorsem.wdw.mapshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.io.IOException;
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
    @BindView(R.id.addFriendBtn)
    Button addFriendBtn;
    @BindView(R.id.addFriendText)
    TextView addFriendText;

    String documentKey;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        documentKey = getIntent().getStringExtra("username");
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        getProfileInfo();

    }

    private void getProfileInfo() {
        if (documentKey == null) {
            documentKey = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            findFriendsBtn.setVisibility(View.VISIBLE);
        }
        else {
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currUser = documentSnapshot.toObject(User.class);
                    List<String> friends = currUser.getFriends();
                    if (friends.contains(documentKey)) {
                        addFriendBtn.setVisibility(View.GONE);
                        addFriendText.setVisibility(View.VISIBLE);
                    }
                    else{
                        addFriendBtn.setVisibility(View.VISIBLE);
                        addFriendText.setVisibility(View.GONE);
                        }
                }
            });
        }


        db.collection("users").document(documentKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currUser = documentSnapshot.toObject(User.class);

                tvProfileUsername.setText(usernameFromEmail(currUser.getUID()));

                List<String> maps = currUser.getCreatedMaps();
                tvNumCreated.setText(String.valueOf(maps.size()));


                List<String> subMaps = currUser.getSubMaps();
                tvNumberFollowed.setText(String.valueOf(subMaps.size()));

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

    @OnClick(R.id.addFriendBtn)
    void addFriendToDB() {
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currUser = documentSnapshot.toObject(User.class);
                List<String> friends = currUser.getFriends();
                friends.add(documentKey);
                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).update("friends", friends);
            }
        });

        addFriendBtn.setVisibility(View.GONE);
        addFriendText.setVisibility(View.VISIBLE);

    }


    @OnClick(R.id.profile_picture)
    void attachClick() {
        if (documentKey.equals(currentUser)){
            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                    this);
            myAlertDialog.setTitle("Upload Pictures Option");
            myAlertDialog.setMessage("How do you want to set your picture?");

            myAlertDialog.setPositiveButton("Gallery",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            Intent pictureActionIntent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(
                                    pictureActionIntent,
                                    102);

                        }
                    });

            myAlertDialog.setNegativeButton("Camera",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intentCamera, 101);

                        }
                    });
            myAlertDialog.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap = null;
        if (requestCode == 101 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        }
        if (requestCode == 102 && resultCode == RESULT_OK) {
            final Uri uri = data.getData();
            imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivProfilePic.setImageBitmap(imageBitmap);
        ivProfilePic.setVisibility(View.VISIBLE);
        try {
            uploadPostToStorage();
        } catch (Exception e) {
            e.printStackTrace();
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

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;

        }
    }
}