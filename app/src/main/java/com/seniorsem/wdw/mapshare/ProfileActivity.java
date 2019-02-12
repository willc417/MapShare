package com.seniorsem.wdw.mapshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final TextView tvProfileUsername = findViewById(R.id.username);
        final TextView tvNumberCreated = findViewById(R.id.num_maps);

        //get the created maps of a user
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User");
        dbRef.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Get the user id for the profile page
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                tvProfileUsername.setText(userEmail);

                //get the number of maps the user has created
                User currUser = dataSnapshot.getValue(User.class);
                List<Map> maps = currUser.getCreatedMaps();
                tvNumberCreated.setText(String.valueOf(maps.size()));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
              /*viewMapsRecyclerAdapter.removeMapByKey(dataSnapshot.getKey());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();*/
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
}
