package com.seniorsem.wdw.mapshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.adapter.ViewMapsRecyclerAdapter;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.MyMarker;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewMapsActivity extends AppCompatActivity {

    private ViewMapsRecyclerAdapter viewMapsRecyclerAdapter;
    static FirebaseFirestore db;

    static Context context;

    @BindView(R.id.switchBtn)
    Button btnSwitch;
    public boolean viewingCreated = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maps);

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

        initPosts();
    }

    @OnClick(R.id.switchBtn)
    void switchMaps() {
        if (viewingCreated) {
            viewingCreated = false;
            btnSwitch.setText("Saved Maps");
        } else {
            viewingCreated = true;
            btnSwitch.setText("Created Maps");
        }
        viewMapsRecyclerAdapter.removeAll();
        initPosts();
    }

    private void initPosts() {
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currUser = documentSnapshot.toObject(User.class);
                List<String> MapKeys;
                String collectionKey = "";

                if (viewingCreated) {
                    MapKeys = currUser.getCreatedMaps();
                    collectionKey = "createdMaps";}
                else
                {
                    MapKeys = currUser.getSubMaps();
                    collectionKey = "subMaps";

                }
                for (int i = 0; i < MapKeys.size(); i++) {
                    db.collection(collectionKey).document(MapKeys.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

    public static void deleteMap(final String mapDocKey, final String userKey) {
        db.collection("users").document(userKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);

                u.getCreatedMaps().remove(mapDocKey);

                db.collection("users").document(userKey).update("createdMaps", u.getCreatedMaps());
            }
        });

        db.collection("createdMaps").document(mapDocKey).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Removed Map", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
