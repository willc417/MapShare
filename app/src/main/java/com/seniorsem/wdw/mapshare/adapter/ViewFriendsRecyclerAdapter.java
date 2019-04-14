package com.seniorsem.wdw.mapshare.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

    private List<User> friendList;
    private List<String> friendKeys;
    private int lastPosition = -1;
    int isFollowers;

    public ViewFriendsRecyclerAdapter(Context context, int isFollowers) {

        this.context = context;
        this.friendList = new ArrayList<>();
        this.friendKeys = new ArrayList<>();
        this.isFollowers = isFollowers;
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

        holder.friendName.setText(usernameFromEmail(friendList.get(holder.getAdapterPosition()).getUID()));

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ProfileIntent = new Intent();
                ProfileIntent.setClass(context, ProfileActivity.class);
                ProfileIntent.putExtra("username", friendList.get(holder.getAdapterPosition()).getUID());
                context.startActivity(ProfileIntent);
            }
        });

        if (isFollowers == 1) {
            holder.btnRemove.setVisibility(View.GONE);
        }
        else {

            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                    alertDialogBuilder.setMessage("Are you sure you want to unfollow?");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    int position = holder.getAdapterPosition();
                                    removeFriendFromDB(friendList.get(position).getUID());
                                    removeFriend(position);
                                }
                            });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }

        setAnimation(holder.itemView, position);
    }

    private void removeFriendFromDB(final String uid) {
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);
                List<String> friends = u.getFollowing();

                friends.remove(uid);

                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).update("following", friends);

            }
        });

        db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);
                List<String> friends = u.getFollowers();

                friends.remove(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                db.collection("users").document(uid).update("followers", friends);

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

    public void removeFriend(int index) {
        friendList.remove(index);
        friendKeys.remove(index);
        notifyItemRemoved(index);
    }

    public void removeAll() {
        int size = friendKeys.size();
        for (int i = 0; i < size; i++) {
            friendList.remove(0);
            friendKeys.remove(0);
        }
        notifyItemRangeRemoved(0, size);
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

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;

        }
    }

}