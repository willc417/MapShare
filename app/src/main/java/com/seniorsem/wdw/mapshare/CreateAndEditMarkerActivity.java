package com.seniorsem.wdw.mapshare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seniorsem.wdw.mapshare.data.MyMarker;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CreateAndEditMarkerActivity extends AppCompatActivity {

    private LocationManager locationManager;

    @BindView(R.id.markerTitle)
    EditText etMarkerTitle;
    @BindView(R.id.markerDesc)
    EditText etMarkerDesc;
    @BindView(R.id.CreateBtn)
    Button createBtn;
    @BindView(R.id.searchBtn)
    CircleButton searchBtn;
    @BindView(R.id.coordinatesBtn)
    CircleButton coordinatesBtn;
    @BindView(R.id.currLocBtn)
    CircleButton currLocBtn;

    @BindView(R.id.searchLayout)
    RelativeLayout searchLayout;
    @BindView(R.id.coordinatesLayout)
    RelativeLayout coordinatesLayout;
    @BindView(R.id.currLocLayout)
    RelativeLayout currLocLayout;

    @BindView(R.id.markerLon)
    EditText etMarkerLon;
    @BindView(R.id.markerLat)
    EditText etMarkerLat;

    @BindView(R.id.etSearch)
    EditText etSearch;

    @BindView(R.id.getCurrLocBtn)
    Button getCurrLocBtn;
    @BindView(R.id.currLat)
    TextView tvCurrLat;
    @BindView(R.id.currLon)
    TextView tvCurrLon;
    @BindView(R.id.nearbyAddress)
    TextView tvNearbyAddress;



    int choice = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_and_edit_marker);
        ButterKnife.bind(this);
        currLocLayout.setVisibility(View.VISIBLE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @OnClick(R.id.searchBtn)
    void ExpandSearch() {
        currLocLayout.setVisibility(View.INVISIBLE);
        coordinatesLayout.setVisibility(View.INVISIBLE);
        searchLayout.setVisibility(View.VISIBLE);
        choice = 0;

    }

    @OnClick(R.id.currLocBtn)
    void ExpandCurrLoc() {
        currLocLayout.setVisibility(View.VISIBLE);
        coordinatesLayout.setVisibility(View.INVISIBLE);
        searchLayout.setVisibility(View.INVISIBLE);
        choice = 1;

    }

    @OnClick(R.id.getCurrLocBtn)
    void SetCurrLoc() {

        if (ActivityCompat.checkSelfPermission(CreateAndEditMarkerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CreateAndEditMarkerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        tvCurrLon.setText(String.valueOf(location.getLongitude()));
        tvCurrLat.setText(String.valueOf(location.getLatitude()));

        tvNearbyAddress.setText(geocode(Double.parseDouble(tvCurrLat.getText().toString()),
                Double.parseDouble(tvCurrLon.getText().toString())));

    }

    @OnClick(R.id.coordinatesBtn)
    void ExpandCoordinates() {
        currLocLayout.setVisibility(View.INVISIBLE);
        coordinatesLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.INVISIBLE);
        choice = 2;

    }

    public String geocode(Double lat, Double lon) {
        String place = "";

        try {
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> addrs = null;
            addrs = gc.getFromLocation(lat, lon, 1);

            place = addrs.get(0).getAddressLine(0) + "";

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return place;
    }

    @OnClick(R.id.CreateBtn)
    void CreateMarker() {
        Log.d("TAG_UI", "createBtn");
        String titleEntered = etMarkerTitle.getText().toString();
        String descEntered = etMarkerDesc.getText().toString();

        Double LatEntered, LonEntered;

        if (choice == 0) { //search location
            LatEntered = 0.0;
            LonEntered = 0.0;
        }
        else if (choice == 1) { //currLocation
            LatEntered = Double.parseDouble(tvCurrLat.getText().toString());
            LonEntered = Double.parseDouble(tvCurrLon.getText().toString());
        }
        else { //coordinates btn
            LatEntered = Double.parseDouble(etMarkerLat.getText().toString());
            LonEntered = Double.parseDouble(etMarkerLon.getText().toString());
        }



        MyMarker newMarker = new MyMarker(LatEntered, LonEntered, titleEntered, descEntered, null, null, null, null);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("NewMarker", (Serializable) newMarker);
        setResult(RESULT_OK, resultIntent);
        finish();
    }




}
