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

        ///////FAB CODE///////
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fabHide = (FloatingActionButton) findViewById(R.id.fabHide);
        final FloatingActionButton fabHome = (FloatingActionButton) findViewById(R.id.fab_Home);
        //Need better icon. fabHome
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        //Need more appropriate icon. fab2
        final FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab_3);
        //Need more appropriate icon. fab3
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Shows menu FABs
                FABSHOW(fabHome,fab2,fab3);
                fab.hide();
                fab.setClickable(false);
                fabHide.show();
                fabHide.setClickable(true);
            }
        });
        fabHide.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //Hides menu FABs
                FABHIDE(fabHome,fab2,fab3);
                fab.show();
                fab.setClickable(true);
                fabHide.hide();
                fabHide.setClickable(false);
            }
        });
        fabHome.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intentMain = new Intent();
                intentMain.setClass(CreateMapActivity.this, MapsActivity.class);
                Log.d("TAG_UI", "HERE");
                startActivity(intentMain);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(CreateMapActivity.this, "FAB2 Test", Toast.LENGTH_LONG).show();
            }
        });
        fab3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(CreateMapActivity.this,"FAB3 Test", Toast.LENGTH_LONG).show();
            }
        });
        //END OF FAB
    }
    //FAB FUNCTIONS
    public void FABSHOW(final FloatingActionButton fabA, final FloatingActionButton fabB, final FloatingActionButton fabC){
        //Buttons Originally Hidden behind main FAB. Moves them to positions, and sets clickable and show
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabA.getLayoutParams();
        layoutParams.bottomMargin += (int) (fabA.getHeight() * 1.5);
        fabA.setLayoutParams(layoutParams);
        fabA.setClickable(true);
        fabA.show();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabB.getLayoutParams();
        layoutParams.bottomMargin += (int) (fabB.getHeight() * 2.75);
        fabB.setLayoutParams(layoutParams);
        fabB.setClickable(true);
        fabB.show();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabC.getLayoutParams();
        layoutParams.bottomMargin += (int) (fabC.getHeight() * 4);
        fabC.setLayoutParams(layoutParams);
        fabC.setClickable(true);
        fabC.show();
    }
    public void FABHIDE(final FloatingActionButton fabA, final FloatingActionButton fabB, final FloatingActionButton fabC){
        //Moves new FABs behind main FAB. Not clickable or shown
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabA.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fabA.getHeight() * 1.5);
        fabA.setLayoutParams(layoutParams);
        fabA.setClickable(false);
        fabA.hide();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabB.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fabB.getHeight() * 2.75);
        fabB.setLayoutParams(layoutParams);
        fabB.setClickable(false);
        fabB.hide();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabC.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fabC.getHeight() * 4);
        fabC.setLayoutParams(layoutParams);
        fabC.setClickable(false);
        fabC.hide();
    }
    ///////END FAB CODE///////

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
