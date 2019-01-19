package com.seniorsem.wdw.mapshare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.MyMarker;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button navigationButton = findViewById(R.id.btn_navigation);
        final Button profileButton = findViewById(R.id.btn_go_to_profile);
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileButton.setVisibility(View.VISIBLE);

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

   /*     if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        // Add a marker in Sydney and move the camera
        LatLng player = new LatLng(longitude, latitude); //Origninally -35 and 151
        mMap.addMarker(new MarkerOptions().position(player).title("Player Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(player)); */

       // AddMarkers(mMap);
    }

    private void AddMarkers(final GoogleMap mMap) {
        //Log.d("TAG_UI", "User/" + FirebaseAuth.getInstance().getCurrentUser() + "/createdMaps");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User/" + FirebaseAuth.getInstance().getUid() + "/createdMaps");


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map createdMap = dataSnapshot.getValue(Map.class);
                Log.d("TAG_UI", "onChildAdded");
                List<MyMarker> newMarkers = createdMap.getMyMarkers();

                for (int i = 0; i < newMarkers.size(); i++) {
                    LatLng loc = new LatLng(newMarkers.get(i).getLat(), newMarkers.get(i).getLon());
                    Marker newMarker = mMap.addMarker(new MarkerOptions().position(loc).title("Test"));
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // mapReset();
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