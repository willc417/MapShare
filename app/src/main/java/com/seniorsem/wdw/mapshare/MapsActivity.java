package com.seniorsem.wdw.mapshare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.MyMarker;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private LocationManager locationManager;
    private float zoomLevel = 15.5f;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        context = getApplicationContext();
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
                Intent intentMain = new Intent();
                intentMain.setClass(MapsActivity.this, ViewMapsActivity.class);
                Log.d("TAG_UI", "HERE");
                startActivity(intentMain);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intentMain = new Intent();
                intentMain.setClass(MapsActivity.this, FindFriendsActivity.class);
                Log.d("TAG_UI", "HERE");
                startActivity(intentMain);
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

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final double longitude = location.getLongitude();
        final double latitude = location.getLatitude();




        LatLng player = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(player).title("Player Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

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

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User newUser = documentSnapshot.toObject(User.class);

                List<String> currMaps = newUser.getCreatedMaps();

                for (int i = 0; i < currMaps.size(); i++) {

                    db.collection("createdMaps").document(currMaps.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map displayMap = documentSnapshot.toObject(Map.class);
                            List<MyMarker> myMarkers = displayMap.getMyMarkers();
                            for (int j = 0; j < myMarkers.size(); j++) {
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(myMarkers.get(j).getLat(), myMarkers.get(j).getLon())).title("TEST"));
                            }
                        }
                    });
                }
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