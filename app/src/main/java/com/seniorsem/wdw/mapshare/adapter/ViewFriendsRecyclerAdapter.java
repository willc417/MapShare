package com.seniorsem.wdw.mapshare.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seniorsem.wdw.mapshare.ProfileActivity;
import com.seniorsem.wdw.mapshare.R;
import com.seniorsem.wdw.mapshare.data.User;

import java.util.ArrayList;
import java.util.List;

public class ViewFriendsRecyclerAdapter extends RecyclerView.Adapter<ViewFriendsRecyclerAdapter.ViewHolder> {

    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ProgressDialog progressDialog;
    private List<User> friendList;
    private List<String> friendKeys;
    private int lastPosition = -1;
    private Activity activity;

    public ViewFriendsRecyclerAdapter(Context context) {
        this.context = context;
        this.friendList = new ArrayList<User>();
        this.friendKeys = new ArrayList<String>();
    }

    @NonNull
    @Override
    public ViewFriendsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_friend, viewGroup, false);

            return new ViewFriendsRecyclerAdapter.ViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewFriendsRecyclerAdapter.ViewHolder holder, int position) {


        Glide.with(context).load(friendList.get(holder.getAdapterPosition()).getProfilePicture()).into(holder.friendProfPic);

        holder.friendName.setText(friendList.get(holder.getAdapterPosition()).getUID());


        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ProfileIntent = new Intent();
                ProfileIntent.setClass(context, ProfileActivity.class);
                ProfileIntent.putExtra("username", friendList.get(holder.getAdapterPosition()).getUID());
                context.startActivity(ProfileIntent);
            }
        });

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                removeFriendFromDB(friendList.get(position).getUID());
                removeFriend(position);
            }
        });


        setAnimation(holder.itemView, position);
    }

    private void removeFriendFromDB(final String uid) {
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);
                List<String> friends = u.getFriends();

                friends.remove(uid);

                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).update("friends", friends);

            }
        });
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void removeFriend(int index) {
        friendList.remove(index);
        friendKeys.remove(index);
        notifyItemRemoved(index);
    }

    private void removeMapByKey(String key) {
        int index = friendKeys.indexOf(key);
        if (index != -1) {
            friendList.remove(index);
            friendKeys.remove(index);
            notifyItemChanged(index);
        }
    }

    public void removeAll() {
        int size = friendKeys.size();
        for (int i = 0; i < size; i++) {
            friendList.remove(0);
            friendKeys.remove(0);
        }
        notifyItemRangeRemoved(0, size);
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

    public void addFriend(User friend, String key) {
        friendList.add(friend);
        friendKeys.add(key);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView friendName;
        de.hdodenhof.circleimageview.CircleImageView friendProfPic;
        at.markushi.ui.CircleButton btnRemove;
        at.markushi.ui.CircleButton btnView;

        ViewHolder(View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);
            friendProfPic = itemView.findViewById(R.id.friendProfPic);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }

}