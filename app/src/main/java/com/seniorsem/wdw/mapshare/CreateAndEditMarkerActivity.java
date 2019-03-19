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
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;

import com.seniorsem.wdw.mapshare.data.MyMarker;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
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

    @BindView(R.id.saveCbtn)
    Button saveCbtn;

    @BindView(R.id.getCurrLocBtn)
    Button getCurrLocBtn;
    @BindView(R.id.nearbyAddress)
    TextView tvNearbyAddress;

    @BindView(R.id.tvSavedLat)
    TextView tvSavedLat;
    @BindView(R.id.tvSavedLon)
    TextView tvSavedLon;


    int choice = 1;
    double LatEntered, LonEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_and_edit_marker);
        ButterKnife.bind(this);

        currLocLayout.setVisibility(View.VISIBLE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        MyMarker editMarker = (MyMarker) getIntent().getSerializableExtra("isEdit");
        if (editMarker != null) {
            fillMarkerFields(editMarker);
        }

    }

    private void fillMarkerFields(MyMarker editMarker) {
        etMarkerLat.setText(String.valueOf(editMarker.getLat()));
        etMarkerLon.setText(String.valueOf(editMarker.getLon()));

        etMarkerTitle.setText(editMarker.getTitle());
        etMarkerDesc.setText(editMarker.getDescription());


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

        if (ActivityCompat.checkSelfPermission(CreateAndEditMarkerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateAndEditMarkerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatEntered = location.getLatitude();
        LonEntered = location.getLongitude();
        tvSavedLon.setText(getString(R.string.lat_with_param, LatEntered));
        tvSavedLat.setText(getString(R.string.lon_with_param, LonEntered));
        tvSavedLon.setVisibility(View.VISIBLE);
        tvSavedLat.setVisibility(View.VISIBLE);

        tvNearbyAddress.setText(geocodeFromCoordinates(LatEntered, LonEntered));

    }

    @OnClick(R.id.coordinatesBtn)
    void ExpandCoordinates() {
        tvSavedLon.setVisibility(View.INVISIBLE);
        tvSavedLat.setVisibility(View.INVISIBLE);
        currLocLayout.setVisibility(View.INVISIBLE);
        coordinatesLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.INVISIBLE);
        choice = 2;

    }


    @OnClick(R.id.saveCbtn)
    void SaveCoordinates() {

            LatLng searchLatLng = geocodeFromAddress(etSearch.getText().toString());
            LatEntered = searchLatLng.latitude;
            LonEntered = searchLatLng.longitude;

        if (LatEntered != 86.0) {
            tvSavedLon.setText(getString(R.string.lat_with_param, LatEntered));
            tvSavedLat.setText(getString(R.string.lon_with_param, LonEntered));
            tvSavedLon.setVisibility(View.VISIBLE);
            tvSavedLat.setVisibility(View.VISIBLE);
        }
    }


    public String geocodeFromCoordinates(Double lat, Double lon) {
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

    public LatLng geocodeFromAddress(String address) {
        double searchLat = 86.0;
        double searchLon = 181.0;

        try {
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> addrs = null;
            addrs = gc.getFromLocationName(address, 1);

            if (addrs.isEmpty()){
                Toast.makeText(this, "Enter a valid address.", Toast.LENGTH_SHORT).show();
            }
            else {

            searchLat = addrs.get(0).getLatitude();
            searchLon = addrs.get(0).getLongitude(); }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new LatLng(searchLat, searchLon);
    }

    @OnClick(R.id.CreateBtn)
    void CreateMarker() {
        Log.d("TAG_UI", "createBtn");
        String titleEntered = etMarkerTitle.getText().toString();
        String descEntered = etMarkerDesc.getText().toString();

        if (choice == 1) { //currLocation
        LatEntered = Double.parseDouble(tvSavedLat.getText().toString());
        LonEntered = Double.parseDouble(tvSavedLon.getText().toString());
        }
        else if (choice == 2) { //coordinates btn
        LatEntered = Double.parseDouble(etMarkerLat.getText().toString());
        LonEntered = Double.parseDouble(etMarkerLon.getText().toString());
        }

        if ((LatEntered > 85.0 || LatEntered < -85.0) || (LonEntered > 180.0 || LonEntered < -180.0)) {
            Toast.makeText(this, "Enter a valid location", Toast.LENGTH_SHORT).show();
        }
        else {
        MyMarker newMarker = new MyMarker(LatEntered, LonEntered, titleEntered, descEntered, null, null, null, null);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("NewMarker", newMarker);
        setResult(RESULT_OK, resultIntent);
        finish();
        }

    }


}
