package com.seniorsem.wdw.mapshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.adapter.ViewMapsRecyclerAdapter;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewMapsActivity extends AppCompatActivity {

    private ViewMapsRecyclerAdapter viewMapsRecyclerAdapter;
    static FirebaseFirestore db;

    Context context;

    @BindView(R.id.switchBtn)
    Button btnSwitch;
    public boolean viewingCreated;

    @BindView(R.id.currUsername)
    TextView currUsername;

    String documentKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maps);

        viewingCreated = true;
        context = getApplicationContext();
        db = FirebaseFirestore.getInstance();
        ButterKnife.bind(this);

        documentKey = getIntent().getStringExtra("username");

        if (documentKey == null) {
            documentKey = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            currUsername.setVisibility(View.INVISIBLE);

        }
        else {
            currUsername.setText(usernameFromEmail(getString(R.string.currUsername, documentKey)));
        }

        viewMapsRecyclerAdapter = new ViewMapsRecyclerAdapter(this,
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        RecyclerView recyclerViewPlaces = (RecyclerView) findViewById(
                R.id.recyclerViewMaps);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewPlaces.setLayoutManager(layoutManager);
        recyclerViewPlaces.setAdapter(viewMapsRecyclerAdapter);
        ButterKnife.bind(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fabHide = (FloatingActionButton) findViewById(R.id.fabHide);
        final FloatingActionButton fabHome = (FloatingActionButton) findViewById(R.id.fab_1);
        //Need better icon. fabHome
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab2.setImageDrawable(getDrawable(R.drawable.social_clip));
        //Need more appropriate icon. fab2
        final FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab_3);
        //Need more appropriate icon. fab3
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Shows menu FABs
                FABSHOW(fabHome, fab2, fab3);
                fab.hide();
                fab.setClickable(false);
                fabHide.show();
                fabHide.setClickable(true);
            }
        });
        fabHide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Hides menu FABs
                FABHIDE(fabHome, fab2, fab3);
                fab.show();
                fab.setClickable(true);
                fabHide.hide();
                fabHide.setClickable(false);
            }
        });
        fabHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentMain = new Intent();
                intentMain.setClass(ViewMapsActivity.this, CreateMapActivity.class);
                startActivity(intentMain);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentMain = new Intent();
                intentMain.setClass(ViewMapsActivity.this, FindFriendsActivity.class);
                startActivity(intentMain);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentMain = new Intent();
                intentMain.setClass(ViewMapsActivity.this, ProfileActivity.class);
                startActivity(intentMain);
            }
        });
        //END OF FAB
    }

    //FAB FUNCTIONS
    public void FABSHOW(final FloatingActionButton fabA, final FloatingActionButton fabB, final FloatingActionButton fabC) {
        //Buttons Originally Hidden behind main FAB. Moves them to positions, and sets clickable and show
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabA.getLayoutParams();
        layoutParams.bottomMargin += (int) (fabA.getHeight() * 1.5);
        fabA.setLayoutParams(layoutParams);
        fabA.setClickable(true);
        fabA.show();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabB.getLayoutParams();
        layoutParams.bottomMargin += (int) (fabB.getHeight() * 2.75);
        fabB.setLayoutParams(layoutParams);
        fabB.setClickable(true);
        fabB.show();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabC.getLayoutParams();
        layoutParams.bottomMargin += (int) (fabC.getHeight() * 4);
        fabC.setLayoutParams(layoutParams);
        fabC.setClickable(true);
        fabC.show();
    }

    public void FABHIDE(final FloatingActionButton fabA, final FloatingActionButton fabB, final FloatingActionButton fabC) {
        //Moves new FABs behind main FAB. Not clickable or shown
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabA.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fabA.getHeight() * 1.5);
        fabA.setLayoutParams(layoutParams);
        fabA.setClickable(false);
        fabA.hide();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabB.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fabB.getHeight() * 2.75);
        fabB.setLayoutParams(layoutParams);
        fabB.setClickable(false);
        fabB.hide();////////////////////////////
        layoutParams = (FrameLayout.LayoutParams) fabC.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fabC.getHeight() * 4);
        fabC.setLayoutParams(layoutParams);
        fabC.setClickable(false);
        fabC.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewMapsRecyclerAdapter.removeAll();
        initPosts();
    }

    @OnClick(R.id.switchBtn)
    void switchMaps() {
        if (viewingCreated) {
            viewingCreated = false;
            btnSwitch.setText("Show Created Maps");
        } else {
            viewingCreated = true;
            btnSwitch.setText("Show Saved Maps");
        }
        viewMapsRecyclerAdapter.removeAll();
        initPosts();
    }

    private void initPosts() {
        db.collection("users").document(documentKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currUser = documentSnapshot.toObject(User.class);
                List<String> MapKeys;

                if (viewingCreated) {
                    MapKeys = currUser.getCreatedMaps();
                } else {
                    MapKeys = currUser.getSubMaps();
                }
                for (int i = 0; i < MapKeys.size(); i++) {
                    db.collection("createdMaps").document(MapKeys.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map displayMap = documentSnapshot.toObject(Map.class);
                            viewMapsRecyclerAdapter.addMap(displayMap, documentSnapshot.getId());
                        }
                    });
                }
            }
        });

    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;

        }
    }

}
