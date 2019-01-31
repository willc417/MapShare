package com.seniorsem.wdw.mapshare;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.MyMarker;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private LocationManager locationManager;
    private float zoomLevel = 15.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestNeededPermission();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fabHide = (FloatingActionButton) findViewById(R.id.fabHide);
        final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        //Need more appropriate icon. fab2
        final FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab_3);
        //Need more appropriate icon. fab3
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Click Action
                //Creates a FAB for intent 1
                Toast.makeText(MapsActivity.this, "Open", Toast.LENGTH_LONG).show();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
                layoutParams.bottomMargin += (int) (fab1.getHeight() * 1.5);
                fab1.setLayoutParams(layoutParams);
                fab1.setClickable(true);
                fab1.show();

                //Creates a FAB for intent 2
                layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
                layoutParams.bottomMargin += (int) (fab2.getHeight() * 2.75);
                fab2.setLayoutParams(layoutParams);
                fab2.setClickable(true);
                fab2.show();

                //Creates a FAB for intent 3
                layoutParams = (FrameLayout.LayoutParams) fab3.getLayoutParams();
                layoutParams.bottomMargin += (int) (fab3.getHeight() * 4);
                fab3.setLayoutParams(layoutParams);
                fab3.setClickable(true);
                fab3.show();

                //Hides Menu FAB. Shows cancel FAB
                fab.hide();
                fab.setClickable(false);
                fabHide.show();
                fabHide.setClickable(true);

            }
        });
        //Cancel FAB listener
        fabHide.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //On Click, Sets children FABs to original location and not clickable
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
                layoutParams.bottomMargin -= (int) (fab1.getHeight() * 1.5);
                fab1.setLayoutParams(layoutParams);
                fab1.setClickable(false);
                fab1.hide();

                layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
                layoutParams.bottomMargin -= (int) (fab2.getHeight() * 2.75);
                fab2.setLayoutParams(layoutParams);
                fab2.setClickable(false);
                fab2.hide();

                layoutParams = (FrameLayout.LayoutParams) fab3.getLayoutParams();
                layoutParams.bottomMargin -= (int) (fab3.getHeight() * 4);
                fab3.setLayoutParams(layoutParams);
                fab3.setClickable(false);
                fab3.hide();

                fab.show();
                fab.setClickable(true);
                fabHide.hide();
                fabHide.setClickable(false);
            }
        });
        fab1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(MapsActivity.this, "FAB1 Test", Toast.LENGTH_LONG).show();
                Intent intentMain = new Intent();
                intentMain.setClass(MapsActivity.this, CreateMapActivity.class);
                Log.d("TAG_UI", "HERE");
                startActivity(intentMain);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(MapsActivity.this, "FAB2 Test", Toast.LENGTH_LONG).show();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(MapsActivity.this,"FAB3 Test", Toast.LENGTH_LONG).show();
            }
        });

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {


        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        LatLng player = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(player).title("Player Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(player));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(player, zoomLevel));
        AddMarkers(googleMap);


    }

    private void AddMarkers(final GoogleMap googleMap) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User");
        dbRef.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User currUser = dataSnapshot.getValue(User.class);
                if (currUser.getCreatedMaps() != null) {
                    List<Map> createdMaps = currUser.getCreatedMaps();
                    for (int i = 0; i < createdMaps.size(); i++) {
                        List<MyMarker> myMarkers = createdMaps.get(i).getMyMarkers();
                        if (myMarkers != null) {
                            for (int j = 0; j < myMarkers.size(); j++) {
                                Marker newMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(myMarkers.get(j).getLat(), myMarkers.get(j).getLon())).title("TEST"));
                            }
                        }

                    }
                }
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

    private void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Toast...
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        } else {
            startLocationMonitoring();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                // start our job
                startLocationMonitoring();
            } else {
                Toast.makeText(this, "Permission not granted :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationMonitoring() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopLocationMonitoring() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //LastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}