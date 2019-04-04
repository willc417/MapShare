package com.seniorsem.wdw.mapshare;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.seniorsem.wdw.mapshare.data.MyMarker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

    @BindView(R.id.imgView)
    ImageView imageView;

    @BindView(R.id.uploadPhoto)
    Button btnPhoto;

    public String filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    private static final int RESULT_LOAD_IMAGE = 1;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    int choice = 1;
    double LatEntered, LonEntered;
    MyMarker editMarker;
    int editMarkerIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_and_edit_marker);
        ButterKnife.bind(this);

        currLocLayout.setVisibility(View.VISIBLE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Bundle b = getIntent().getBundleExtra("EditBundle");
        if (b != null) {
            editMarker = (MyMarker) b.getSerializable("isEdit");
            editMarkerIndex = b.getInt("index");
            if (editMarker != null) {
                fillMarkerFields(editMarker);
            }
        }

    }

    private void fillMarkerFields(MyMarker editMarker) {

        LatEntered = editMarker.getLat();
        LonEntered = editMarker.getLon();
        etMarkerLat.setText(String.valueOf(editMarker.getLat()));
        etMarkerLon.setText(String.valueOf(editMarker.getLon()));

        etMarkerTitle.setText(editMarker.getTitle());
        etMarkerDesc.setText(editMarker.getDescription());

        filePath = editMarker.getImageURL();

        Glide.with(CreateAndEditMarkerActivity.this).load(filePath).into(imageView);

    }


    @OnClick(R.id.uploadPhoto)
    void chooseImage() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                72);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intentCamera, 71);

                    }
                });
        myAlertDialog.show();
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

            if (addrs.isEmpty()) {
                Toast.makeText(this, "Enter a valid address.", Toast.LENGTH_SHORT).show();
            } else {

                searchLat = addrs.get(0).getLatitude();
                searchLon = addrs.get(0).getLongitude();
            }

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

        if ((LatEntered > 85.0 || LatEntered < -85.0) || (LonEntered > 180.0 || LonEntered < -180.0)) {
            Toast.makeText(this, "Enter a valid location", Toast.LENGTH_SHORT).show();
        } else {
            MyMarker newMarker = new MyMarker(LatEntered, LonEntered, titleEntered, descEntered, filePath, null, null, null);
            Intent resultIntent = new Intent();
            Bundle b = new Bundle();
            b.putSerializable("NewMarker", newMarker);
            b.putInt("index", editMarkerIndex);
            resultIntent.putExtra("markerBundle", b);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap = null;
        if (requestCode == 71 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        }
        if (requestCode == 72 && resultCode == RESULT_OK) {
            final Uri uri = data.getData();
            imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(imageBitmap);
        imageView.setVisibility(View.VISIBLE);
        try {
            uploadPostToStorage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadPostToStorage() throws Exception {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInBytes = baos.toByteArray();

        String newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg";
        final StorageReference newImageRef = storageRef.child(newImage);
        final StorageReference newImageImagesRef = storageRef.child("images/" + newImage);
        newImageRef.getName().equals(newImageImagesRef.getName());    // true
        newImageRef.getPath().equals(newImageImagesRef.getPath());    // false

        UploadTask uploadTask = newImageImagesRef.putBytes(imageInBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreateAndEditMarkerActivity.this, "Profile Photo Not uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Toast.makeText(CreateAndEditMarkerActivity.this, "Marker Photo Uploaded", Toast.LENGTH_SHORT).show();
                filePath = taskSnapshot.getDownloadUrl().toString();
                Glide.with(CreateAndEditMarkerActivity.this).load(filePath).into(imageView);
            }
        });
    }

}
