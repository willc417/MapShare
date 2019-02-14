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

        ///////FAB CODE///////
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
                //Shows menu FABs
                FABSHOW(fab1,fab2,fab3);
                fab.hide();
                fab.setClickable(false);
                fabHide.show();
                fabHide.setClickable(true);
            }
        });
        fabHide.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //Hides Menu FABs
                FABHIDE(fab1, fab2, fab3);
                fab.show();
                fab.setClickable(true);
                fabHide.hide();
                fabHide.setClickable(false);
            }
        });
        fab1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intentMain = new Intent();
                intentMain.setClass(MapsActivity.this, CreateMapActivity.class);
                Log.d("TAG_UI", "HERE");
                startActivity(intentMain);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(MapsActivity.this, "FAB2 Test", Toast.LENGTH_LONG).show();
                //REPLACE WITH INTENT CHANGE
            }
        });
        fab3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(MapsActivity.this,"FAB3 Test", Toast.LENGTH_LONG).show();
                //REPLACE WITH INTENT CHANGE
            }
        });
    }
    //FAB FUNCTIONS
    public void FABSHOW(final FloatingActionButton faba, final FloatingActionButton fabb, final FloatingActionButton fabc){
        //Buttons Originally Hidden behind main FAB. Moves them to positions, and sets clickable and show
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) faba.getLayoutParams();
        layoutParams.bottomMargin += (int) (faba.getHeight() * 1.5);
        faba.setLayoutParams(layoutParams);
        faba.setClickable(true);
        faba.show();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabb.getLayoutParams();
        layoutParams.bottomMargin += (int) (fabb.getHeight() * 2.75);
        fabb.setLayoutParams(layoutParams);
        fabb.setClickable(true);
        fabb.show();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabc.getLayoutParams();
        layoutParams.bottomMargin += (int) (fabc.getHeight() * 4);
        fabc.setLayoutParams(layoutParams);
        fabc.setClickable(true);
        fabc.show();
    }


        /*
        Button btn = (Button)findViewById(R.id.temp_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, ProfileActivity.class));
            }
        });
        */

    public void FABHIDE(final FloatingActionButton faba, final FloatingActionButton fabb, final FloatingActionButton fabc){
        //Moves new FABs behind main FAB. Not clickable or shown
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) faba.getLayoutParams();
        layoutParams.bottomMargin -= (int) (faba.getHeight() * 1.5);
        faba.setLayoutParams(layoutParams);
        faba.setClickable(false);
        faba.hide();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabb.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fabb.getHeight() * 2.75);
        fabb.setLayoutParams(layoutParams);
        fabb.setClickable(false);
        fabb.hide();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabc.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fabc.getHeight() * 4);
        fabc.setLayoutParams(layoutParams);
        fabc.setClickable(false);
        fabc.hide();

    }
    ///////FAB CODE///////


    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final double longitude = location.getLongitude();
        final double latitude = location.getLatitude();

        LatLng player = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(player).title("Player Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(player));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(player, zoomLevel));
        AddMarkers(googleMap);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (Math.abs(marker.getPosition().latitude - latitude) <= .01 && Math.abs(marker.getPosition().longitude - longitude) <= .01) {
                    Toast.makeText(MapsActivity.this, "Marker Click Test", Toast.LENGTH_LONG).show();

                    Intent ViewNearbyMarker = new Intent();
                    ViewNearbyMarker.setClass(MapsActivity.this, ViewNearbyMarkerActivity.class);
                    startActivity(ViewNearbyMarker);
                }
                else {
                    Toast.makeText(MapsActivity.this, "Move closer to unlock content", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });


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