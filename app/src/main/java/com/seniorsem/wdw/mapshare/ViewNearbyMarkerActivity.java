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
        String mapName = b.getString("MapName");
        final int markerNum = b.getInt("MarkerIndex");


        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("createdMaps").document(mapName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map newMap = documentSnapshot.toObject(Map.class);
                MyMarker thisMarker = newMap.getMyMarkers().get(markerNum);

                markerTitle.setText(thisMarker.getTitle());
                nearbyMarkerDesc.setText(thisMarker.getDescription());
                Glide.with(ViewNearbyMarkerActivity.this).load(thisMarker.getImageURL()).into(markerImage);
                //Currently should pull up Dyers but its pulling up hopdoddy. maybe error in markerNum7

            }
        });
    }
}
