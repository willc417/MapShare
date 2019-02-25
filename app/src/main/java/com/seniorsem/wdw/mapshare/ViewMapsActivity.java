package com.seniorsem.wdw.mapshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.adapter.ViewMapsRecyclerAdapter;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewMapsActivity extends AppCompatActivity {

    private ViewMapsRecyclerAdapter viewMapsRecyclerAdapter;
    private ProgressDialog progressDialog;
    static FirebaseFirestore db;

    Context context;

    @BindView(R.id.switchBtn)
    Button btnSwitch;
    public boolean viewingCreated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maps);

        viewingCreated = true;
        context = getApplicationContext();
        db = FirebaseFirestore.getInstance();

        viewMapsRecyclerAdapter = new ViewMapsRecyclerAdapter(getApplicationContext(),
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        RecyclerView recyclerViewPlaces = (RecyclerView) findViewById(
                R.id.recyclerViewMaps);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewPlaces.setLayoutManager(layoutManager);
        recyclerViewPlaces.setAdapter(viewMapsRecyclerAdapter);
        ButterKnife.bind(this);

        //initPosts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewMapsRecyclerAdapter.removeAll();
        initPosts();
    }

    @OnClick(R.id.switchBtn)
    void switchMaps() {
        showProgressDialog();
        if (viewingCreated) {
            viewingCreated = false;
            btnSwitch.setText("Saved Maps");
        } else {
            viewingCreated = true;
            btnSwitch.setText("Created Maps");
        }
        viewMapsRecyclerAdapter.removeAll();
        initPosts();
        hideProgressDialog();

    }

    private void initPosts() {
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currUser = documentSnapshot.toObject(User.class);
                List<String> MapKeys;

                if (viewingCreated) {
                    MapKeys = currUser.getCreatedMaps();
                } else {
                    MapKeys = currUser.getSubMaps();
                }
                for (int i = 0; i < MapKeys.size(); i++) {
                    db.collection("createdMaps").document(MapKeys.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map displayMap = documentSnapshot.toObject(Map.class);
                            viewMapsRecyclerAdapter.addMap(displayMap, documentSnapshot.getId());
                        }
                    });
                }
            }
        });

    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Switching...");
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
