package com.seniorsem.wdw.mapshare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
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

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final Object FFFFFF = 2.35098856;
    private LocationManager locationManager;
    private float zoomLevel = 15.5f;
    private Context context;
    SupportMapFragment mapFragment;

    double longitude;
    double latitude;

    boolean viewingSubs = true;

    @BindView(R.id.switchBtn2)
    Button switchbtn2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        context = getApplicationContext();
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestNeededPermission();

        ButterKnife.bind(this);

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
                FABSHOW(fab1, fab2, fab3);
                fab.hide();
                fab.setClickable(false);
                fabHide.show();
                fabHide.setClickable(true);
            }
        });
        fabHide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Hides Menu FABs
                FABHIDE(fab1, fab2, fab3);
                fab.show();
                fab.setClickable(true);
                fabHide.hide();
                fabHide.setClickable(false);
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentMain = new Intent();
                intentMain.setClass(MapsActivity.this, CreateMapActivity.class);
                Log.d("TAG_UI", "HERE");
                startActivity(intentMain);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentMain = new Intent();
                intentMain.setClass(MapsActivity.this, ViewMapsActivity.class);
                Log.d("TAG_UI", "HERE");
                startActivity(intentMain);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentMain = new Intent();
                intentMain.setClass(MapsActivity.this, ProfileActivity.class);
                Log.d("TAG_UI", "HERE");
                startActivity(intentMain);
            }
        });

        getLocation();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);

    }


    @OnClick(R.id.switchBtn2)
    void switchMaps2() {
        viewingSubs = !viewingSubs;
        mapFragment.getMapAsync(this);
        if (viewingSubs) {
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
            switchbtn2.setText("Saved");
        } else {
            Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
            switchbtn2.setText("Created");
        }
    }

    //FAB FUNCTIONS
    public void FABSHOW(final FloatingActionButton faba, final FloatingActionButton fabb, final FloatingActionButton fabc) {
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


    public void FABHIDE(final FloatingActionButton faba, final FloatingActionButton fabb, final FloatingActionButton fabc) {
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

        googleMap.clear();


        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapStyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapStyle", "Can't find style. Error: ", e);
        }

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
        googleMap.addMarker(new MarkerOptions().position(player).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(240)));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(player));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(player, zoomLevel));
        AddMarkers(googleMap);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (!marker.getTitle().equals("Your Location")) {
                    if (Math.abs(marker.getPosition().latitude - latitude) <= .01 && Math.abs(marker.getPosition().longitude - longitude) <= .01) {

                        Bundle extras = new Bundle();
                        MyMarker thisMarker = (MyMarker) marker.getTag();
                        extras.putSerializable("thisMarker", thisMarker);

                        Intent ViewNearbyMarker = new Intent();
                        ViewNearbyMarker.setClass(MapsActivity.this, ViewNearbyMarkerActivity.class);
                        ViewNearbyMarker.putExtras(extras);
                        startActivity(ViewNearbyMarker);
                    } else {
                        Toast.makeText(MapsActivity.this, "Move closer to unlock content", Toast.LENGTH_LONG).show();
                    }
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

                List<String> currMaps;
                if (!viewingSubs) {
                    currMaps = newUser.getCreatedMaps();
                } else {
                    currMaps = newUser.getSubMaps();
                }

                for (int i = 0; i < currMaps.size(); i++) {

                    db.collection("createdMaps").document(currMaps.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map displayMap = documentSnapshot.toObject(Map.class);
                            List<MyMarker> myMarkers = displayMap.getMyMarkers();
                            for (int j = 0; j < myMarkers.size(); j++) {
                                MyMarker curr = myMarkers.get(j);
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(curr.getLat(), curr.getLon())).title(curr.getTitle())
                                        .snippet("Map Name: " + displayMap.getTitle() + " \nCreator: " + displayMap.getCreaterUID())
                                        .icon(BitmapDescriptorFactory.defaultMarker(displayMap.getMapColor()))).setTag(curr);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


}