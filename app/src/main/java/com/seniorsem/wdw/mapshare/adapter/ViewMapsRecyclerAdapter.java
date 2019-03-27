package com.seniorsem.wdw.mapshare.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.CreateMapActivity;
import com.seniorsem.wdw.mapshare.R;
import com.seniorsem.wdw.mapshare.data.Map;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.ArrayList;
import java.util.List;

public class ViewMapsRecyclerAdapter extends RecyclerView.Adapter<ViewMapsRecyclerAdapter.ViewHolder> {

    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ProgressDialog progressDialog;
    private List<Map> mapList;
    private List<String> mapKeys;
    private String creatorUID;
    private int lastPosition = -1;
    private Activity activity;

    public ViewMapsRecyclerAdapter(Context context, String creatorUID) {
        this.context = context;
        this.creatorUID = creatorUID;
        this.mapList = new ArrayList<Map>();
        this.mapKeys = new ArrayList<String>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_map, viewGroup, false);

            return new ViewHolder(v);
        }

    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvCreator.setText(mapList.get(holder.getAdapterPosition()).getCreaterUID());
        holder.tvTitle.setText(mapList.get(holder.getAdapterPosition()).getTitle());
        holder.tvDescription.setText(
                mapList.get(holder.getAdapterPosition()).getDescription());
        holder.date.setText(mapList.get(holder.getAdapterPosition()).getDate());

        setAnimation(holder.itemView, position);

        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(
                mapList.get(holder.getAdapterPosition()).getCreaterUID())) {

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent EditIntent = new Intent();
                    EditIntent.setClass(context, CreateMapActivity.class);
                    String mapKey = FirebaseAuth.getInstance().getCurrentUser().getEmail() + "_" + (mapList.get(holder.getAdapterPosition()).getTitle()).replace(" ", "_");
                    EditIntent.putExtra("isEdit", mapKey);
                    context.startActivity(EditIntent);
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeMap(holder.getAdapterPosition());
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);
        }

        setAnimation(holder.itemView, position);
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void removeMap(int index) {
        String docKey = FirebaseAuth.getInstance().getCurrentUser().getEmail() + "_" + mapList.get(index).getTitle().replace(" ", "_");
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        deleteMap_1(docKey, userKey);
        mapList.remove(index);
        mapKeys.remove(index);
        notifyItemRemoved(index);
    }

    private void removeMapByKey(String key) {
        int index = mapKeys.indexOf(key);
        if (index != -1) {
            mapList.remove(index);
            mapKeys.remove(index);
            notifyItemChanged(index);
        }
    }

    public void removeAll() {
        int size = mapKeys.size();
        for (int i = 0; i < size; i++) {
            mapList.remove(0);
            mapKeys.remove(0);
        }
        notifyItemRangeRemoved(0, size);
    }

    private void deleteMap_1(final String mapDocKey, final String userKey) {

        db.collection("users").document(userKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);

                u.getCreatedMaps().remove(mapDocKey);

                db.collection("users").document(userKey).update("createdMaps", u.getCreatedMaps());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG_UI", "User Failure");
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                deleteMap_2(mapDocKey, userKey);
            }
        });
    }

    private void deleteMap_2(final String mapDocKey, final String userKey) {
        db.collection("createdMaps").document(mapDocKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map thisMap = documentSnapshot.toObject(Map.class);

                final List<String> subsToDelete = thisMap.getSubscribers();

                for (int i = 0; i < subsToDelete.size(); i++) {
                    final int finalI = i;
                    db.collection("users").document(subsToDelete.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User u = documentSnapshot.toObject(User.class);

                            u.getSubMaps().remove(mapDocKey);

                            db.collection("users").document(subsToDelete.get(finalI)).update("subMaps", u.getSubMaps());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG_UI", "Sub Failure");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG_UI", "createdMaps Failure");
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                deleteMap_3(mapDocKey, userKey);
            }
        });
    }

    private void deleteMap_3(String mapDocKey, String userKey) {
        db.collection("createdMaps").document(mapDocKey).delete();
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Deleting Map...");
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    public void addMap(Map newMap, String key) {
        mapList.add(newMap);
        mapKeys.add(key);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCreator;
        public TextView tvTitle;
        public TextView tvDescription;
        public TextView date;
        public at.markushi.ui.CircleButton btnDelete;
        public at.markushi.ui.CircleButton btnEdit;


        public ViewHolder(View itemView) {
            super(itemView);
            tvCreator = itemView.findViewById(R.id.tvCreator);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            date = itemView.findViewById(R.id.date);
        }
    }

}


