package com.seniorsem.wdw.mapshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.username_edit)
    EditText etEmail;
    @BindView(R.id.password_edit)
    EditText etPassword;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);


    }

    @OnClick(R.id.login_btn)
    void loginClick() {
        if (!isFormValid()) {
            return;
        }

        showProgressDialog();


        firebaseAuth.signInWithEmailAndPassword(
                etEmail.getText().toString(),
                etPassword.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressDialog();

                if (task.isSuccessful()) {
                    Intent intentMain = new Intent();
                    intentMain.setClass(LoginActivity.this, MapsActivity.class);
                    Log.d("TAG_UI", "HERE");
                    startActivity(intentMain);
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Error: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();

            }
        });
    }

    @OnClick(R.id.register_btn)
    void registerClick() {
        if (!isFormValid()) {
            return;
        }

        showProgressDialog();

        firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();


                        if (task.isSuccessful()) {
                            final FirebaseUser fbUser = task.getResult().getUser();

                            fbUser.updateProfile(new UserProfileChangeRequest.Builder().
                                    setDisplayName(usernameFromEmail(fbUser.getEmail())).build());

                            Toast.makeText(LoginActivity.this, "User created", Toast.LENGTH_SHORT).show();

                            List<String> createdMaps = new ArrayList<>();
                            List<String> subMaps = new ArrayList<>();
                            List<String> followers = new ArrayList<>();
                            List<String> following = new ArrayList<>();

                            //set profile picture url
                            String uri = "https://upload.wikimedia.org/wikipedia/commons/d/d7/Android_robot.svg";

                            User newUser = new User(
                                    fbUser.getEmail(), createdMaps, subMaps, followers, following, uri);

                            final FirebaseFirestore db = FirebaseFirestore.getInstance();

                            db.collection("users").document(fbUser.getEmail()).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this, "User Successfully Registered",
                                            Toast.LENGTH_SHORT).show();

                                    db.collection("users").document(fbUser.getEmail()).collection("createdMaps").document();
                                    db.collection("users").document(fbUser.getEmail()).collection("subMaps").document();
                                    db.collection("users").document(fbUser.getEmail()).collection("followers").document();
                                    db.collection("users").document(fbUser.getEmail()).collection("following").document();
                                    db.collection("users").document(fbUser.getEmail()).collection("profilePic").document();
                                }
                            });

                            newUser.setProfilePicture(uri);


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                hideProgressDialog();
                e.printStackTrace();
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


    private boolean isFormValid() {
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Required");
            return false;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Required");
            return false;
        }

        return true;
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            progressDialog.setMessage("Loading...");
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
