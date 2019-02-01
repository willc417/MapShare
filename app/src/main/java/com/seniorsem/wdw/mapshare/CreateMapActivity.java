package com.seniorsem.wdw.mapshare;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.seniorsem.wdw.mapshare.adapter.ViewMapsRecyclerAdapter;
import com.seniorsem.wdw.mapshare.adapter.ViewMarkersRecyclerAdapter;
import com.seniorsem.wdw.mapshare.data.Map;

import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.MyMarker;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.ArrayList;
import java.util.Calendar;


import java.util.Date;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class CreateMapActivity extends AppCompatActivity {

    @BindView(R.id.et_map_title)
    EditText etMapTitle;
    @BindView(R.id.et_map_desc)
    EditText etMapDesc;
    @BindView(R.id.btn_create_map)
    Button btnCreateMap;
    @BindView(R.id.privacy_sp)
    Spinner spPrivacy;

    @BindView(R.id.markerTitle)
    EditText etMarkerTitle;
    @BindView(R.id.markerDesc)
    EditText etMarkerDesc;
    @BindView(R.id.markerLon)
    EditText etMarkerLon;
    @BindView(R.id.markerLat)
    EditText etMarkerLat;


    int spinner_position;
    private ViewMarkersRecyclerAdapter viewMarkersRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);
        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.privacy_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrivacy.setAdapter(spinnerAdapter);


        viewMarkersRecyclerAdapter = new ViewMarkersRecyclerAdapter(getApplicationContext(),
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        RecyclerView recyclerViewPlaces = (RecyclerView) findViewById(
                R.id.recyclerViewMarkers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewPlaces.setLayoutManager(layoutManager);
        recyclerViewPlaces.setAdapter(viewMarkersRecyclerAdapter);

    }

    @OnItemSelected(R.id.privacy_sp)
    public void spinnerItemSelected(Spinner spinner, int position) {
        spinner_position = position;
    }


    @OnClick(R.id.btn_add_marker)
    void addMarker() {
        String titleEntered = etMarkerTitle.getText().toString();
        String descEntered = etMarkerDesc.getText().toString();
        Double LatEntered = Double.parseDouble(etMarkerLat.getText().toString());
        Double LonEntered = Double.parseDouble(etMarkerLon.getText().toString());

        MyMarker newMarker = new MyMarker(LatEntered, LonEntered, titleEntered, descEntered, null, null, null, null);
        viewMarkersRecyclerAdapter.addMarker(newMarker, String.valueOf(viewMarkersRecyclerAdapter.getItemCount()));

    }


    @OnClick(R.id.btn_create_map)
    void createMapClick() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User");
        dbRef.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User currUser = dataSnapshot.getValue(User.class);

                Log.d("TAG_UI", "createMapClick");
                String key = dataSnapshot.getKey();
                List<Map> createdMaps;
                if (currUser.getCreatedMaps() != null) {
                    createdMaps = currUser.getCreatedMaps();
                } else {
                    createdMaps = new ArrayList<Map>();
                }
                List<MyMarker> myMarkers = viewMarkersRecyclerAdapter.getMyMarkerList();

                String titleEntered = etMapTitle.getText().toString();
                String descEntered = etMapDesc.getText().toString();

                Date currentTime = Calendar.getInstance().getTime();


                Map newMap = new Map(FirebaseAuth.getInstance().getCurrentUser().getUid(), myMarkers, currentTime.toString(), 0, spinner_position, titleEntered, descEntered);
                createdMaps.add(newMap);
                currUser.setCreatedMaps(createdMaps);
                dbRef.child(key).setValue(currUser);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
