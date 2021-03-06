package com.seniorsem.wdw.mapshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.adapter.ViewMarkersRecyclerAdapter;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.MyMarker;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class CreateMapActivity extends AppCompatActivity implements ViewMarkersRecyclerAdapter.CallbackInterface {


    Map editMap;
    String isEdit;

    HashMap<String, Float> colorToFloat = new HashMap<>();


    @BindView(R.id.create_map_title)
    TextView pageHeading;
    @BindView(R.id.et_map_title)
    EditText etMapTitle;
    @BindView(R.id.et_map_desc)
    EditText etMapDesc;
    @BindView(R.id.btn_create_map)
    Button btnCreateMap;
    /*@BindView(R.id.privacy_sp)
    Spinner spPrivacy;*/
    @BindView(R.id.mapColor_sp)
    Spinner spMapColor;


    FirebaseFirestore db;
    int spinner_position;
    int MCspinner_position;
    ArrayAdapter<CharSequence> MCspinnerAdapter;
    private ViewMarkersRecyclerAdapter viewMarkersRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);
        ButterKnife.bind(this);


        //TEMP CODE FOR COLOR SPINNER
        colorToFloat.put("Red", (float) 0.0);
        colorToFloat.put("Azure", (float) 210.0);
        colorToFloat.put("Blue", (float) 240.0);
        colorToFloat.put("Cyan", (float) 180.0);
        colorToFloat.put("Green", (float) 120.0);
        colorToFloat.put("Magenta", (float) 300.0);
        colorToFloat.put("Orange", (float) 30.0);
        colorToFloat.put("Violet", (float) 270.0);
        colorToFloat.put("Rose", (float) 330.0);
        colorToFloat.put("Yellow", (float) 60.0);
        //TEMP CODE FOR COLOR SPINNER


        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.privacy_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spPrivacy.setAdapter(spinnerAdapter);

        MCspinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.mapColor_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMapColor.setAdapter(MCspinnerAdapter);

        db = FirebaseFirestore.getInstance();

        viewMarkersRecyclerAdapter = new ViewMarkersRecyclerAdapter(this);

        RecyclerView recyclerViewPlaces = (RecyclerView) findViewById(
                R.id.recyclerViewMarkers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewPlaces.setLayoutManager(layoutManager);
        recyclerViewPlaces.setAdapter(viewMarkersRecyclerAdapter);

        isEdit = getIntent().getStringExtra("isEdit");
        if (isEdit != null) {
            fillFields(isEdit);
        }
    }

    /*@OnItemSelected(R.id.privacy_sp)
    public void spinnerItemSelected(Spinner spinner, int position) {
        spinner_position = position;
    }*/

    @OnItemSelected(R.id.mapColor_sp)
    public void mapColorspinnerItemSelected(Spinner spinner, int position) {
        MCspinner_position = position;
    }


    @OnClick(R.id.btn_add_marker)
    void addMarker() {
        Intent CreateNewMarker = new Intent();
        CreateNewMarker.setClass(CreateMapActivity.this, CreateAndEditMarkerActivity.class);
        startActivityForResult(CreateNewMarker, 1);

    }
    @Override
    public void onEditMarker(MyMarker editMarker, int index) {
        Log.d("TAG_UI", "HERE");
        Intent EditIntent = new Intent();
        EditIntent.setClass(CreateMapActivity.this, CreateAndEditMarkerActivity.class);
        Bundle b = new Bundle();
        b.putInt("index", index);
        b.putSerializable("isEdit", editMarker);
        EditIntent.putExtra("EditBundle", b);
        startActivityForResult(EditIntent, 2);
    }

    private void fillFields(final String documentKey) {

        pageHeading.setText("Edit Your Map");

        db.collection("createdMaps").document(documentKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                editMap = documentSnapshot.toObject(Map.class);
                etMapTitle.setText(editMap.getTitle());
                etMapDesc.setText(editMap.getDescription());
                //spPrivacy.setSelection(editMap.getPrivacy());

                List<MyMarker> myMarkers = editMap.getMyMarkers();
                for (int i = 0; i < myMarkers.size(); i++) {
                    viewMarkersRecyclerAdapter.addMarker(myMarkers.get(i), String.valueOf(viewMarkersRecyclerAdapter.getItemCount()));
                }
                spMapColor.setSelection(editMap.getMapColorSpinnerIndex());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateMapActivity.this, "Map Edit Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick(R.id.btn_create_map)
    void createMapClick() {

        if (etMapTitle.getText().toString().equals("")) {
            Toast.makeText(this, "You must enter a title.", Toast.LENGTH_SHORT).show();
        } else {
            List<MyMarker> myMarkers = viewMarkersRecyclerAdapter.getMyMarkerList();

            final String titleEntered = etMapTitle.getText().toString();
            String descEntered = etMapDesc.getText().toString();

            Date currentTime = Calendar.getInstance().getTime();

            List<String> subscribers = new ArrayList<>();
            if (editMap != null) {
                subscribers = editMap.getSubscribers();
            }

            Float mapColor = colorToFloat.get(String.valueOf(MCspinnerAdapter.getItem(MCspinner_position)));

            final Map newMap = new Map(FirebaseAuth.getInstance().getCurrentUser().getEmail(), myMarkers, currentTime.toString(), 0, spinner_position, titleEntered, descEntered, subscribers, mapColor, MCspinner_position);

            final String mapKey = FirebaseAuth.getInstance().getCurrentUser().getEmail() + "_" + titleEntered.replace(" ", "_");

            if (isEdit == null) {
                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User newUser = documentSnapshot.toObject(User.class);

                        List<String> currMaps = newUser.getCreatedMaps();

                        currMaps.add(mapKey);

                        newUser.setCreatedMaps(currMaps);

                        db.collection("users").document(newUser.getUID()).update("createdMaps", currMaps);

                    }
                });

                db.collection("createdMaps").document(mapKey).set(newMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });


            } else { //When doing an edited Map

                final String oldMapKey = FirebaseAuth.getInstance().getCurrentUser().getEmail() + "_" + editMap.getTitle().replace(" ", "_");

                newMap.setSubscribers(editMap.getSubscribers());

                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User newUser = documentSnapshot.toObject(User.class);

                        List<String> currMaps = newUser.getCreatedMaps();

                        currMaps.remove(oldMapKey);
                        currMaps.add(mapKey);

                        newUser.setCreatedMaps(currMaps);

                        db.collection("users").document(newUser.getUID()).update("createdMaps", currMaps);

                        editSubs(oldMapKey, newMap, mapKey);
                        db.collection("createdMaps").document(oldMapKey).delete();
                        db.collection("createdMaps").document(mapKey).set(newMap);
                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        finish();

                    }
                });
            }
        }
    }

    private void editSubs(final String originalKey, final Map newMap, final String newMapKey) {
        final List<String> subscribers = newMap.getSubscribers();

        for (int i = 0; i < subscribers.size(); i++) {
            final int finalI = i;
            db.collection("users").document(subscribers.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User u = documentSnapshot.toObject(User.class);
                    u.getSubMaps().remove(originalKey);
                    u.getSubMaps().add(newMapKey);
                    db.collection("users").document(subscribers.get(finalI)).update("subMaps", u.getSubMaps());
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Log.d("TAG_UI", "onActivityResult");
                Bundle b = data.getBundleExtra("markerBundle");
                MyMarker myMarker = (MyMarker) b.getSerializable("NewMarker");
                viewMarkersRecyclerAdapter.addMarker(myMarker, String.valueOf(viewMarkersRecyclerAdapter.getItemCount()));
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Log.d("TAG_UI", "onActivityResult");

                Bundle b = data.getBundleExtra("markerBundle");
                MyMarker myMarker = (MyMarker) b.getSerializable("NewMarker");
                int index = b.getInt("index");
                viewMarkersRecyclerAdapter.removeMarker(index);
                viewMarkersRecyclerAdapter.addMarker(myMarker, String.valueOf(viewMarkersRecyclerAdapter.getItemCount()));
            }
        }

    }
}
