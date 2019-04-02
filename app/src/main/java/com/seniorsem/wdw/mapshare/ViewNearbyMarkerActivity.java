package com.seniorsem.wdw.mapshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.MyMarker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewNearbyMarkerActivity extends AppCompatActivity {


    @BindView(R.id.nearbyMarkerTitle)
    TextView markerTitle;

    @BindView(R.id.nearbyMarkerDesc)
    TextView nearbyMarkerDesc;

    @BindView(R.id.nearbyMarkerImage)
    ImageView markerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nearby_marker);

        ButterKnife.bind(this);

        Intent thisIntent = getIntent();
        Bundle b = thisIntent.getExtras();
        MyMarker thisMarker = (MyMarker) b.getSerializable("thisMarker");

        markerTitle.setText(thisMarker.getTitle());
        nearbyMarkerDesc.setText(thisMarker.getDescription());
        Glide.with(ViewNearbyMarkerActivity.this).load(thisMarker.getImageURL()).into(markerImage);
    }
}

