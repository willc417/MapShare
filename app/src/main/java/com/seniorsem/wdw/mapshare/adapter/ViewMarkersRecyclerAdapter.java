package com.seniorsem.wdw.mapshare.adapter;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.seniorsem.wdw.mapshare.CreateAndEditMarkerActivity;
import com.seniorsem.wdw.mapshare.R;
import com.seniorsem.wdw.mapshare.data.MyMarker;

import java.util.ArrayList;
import java.util.List;

public class ViewMarkersRecyclerAdapter extends RecyclerView.Adapter<ViewMarkersRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<MyMarker> myMarkerList;
    private List<String> myMarkerKeys;
    private int lastPosition = -1;

    public ViewMarkersRecyclerAdapter(Context context, String creatorUID) {
        this.context = context;
        this.myMarkerList = new ArrayList<MyMarker>();
        this.myMarkerKeys = new ArrayList<String>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.marker_card, viewGroup, false);

            return new ViewHolder(v);
        }

    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvTitle.setText(context.getString(R.string.title,
                myMarkerList.get(holder.getAdapterPosition()).getTitle()));
        holder.tvDescription.setText(
                myMarkerList.get(holder.getAdapterPosition()).getDescription());
        holder.tvLon.setText(String.valueOf(
                myMarkerList.get(holder.getAdapterPosition()).getLat()));
        holder.tvLat.setText(String.valueOf(
                myMarkerList.get(holder.getAdapterPosition()).getLon()));

        setAnimation(holder.itemView, position);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditIntent = new Intent();
                EditIntent.setClass(context, CreateAndEditMarkerActivity.class);
                EditIntent.putExtra("isEdit", getMyMarker(holder.getAdapterPosition()));
                context.startActivity(EditIntent);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMarker(holder.getAdapterPosition());
            }
        });

      /*  if(!TextUtils.isEmpty(postList.get(holder.getAdapterPosition()).

                getImageUrl()))

        {
            Glide.with(context).load(
                    postList.get(holder.getAdapterPosition()).getImageUrl()
            ).into(holder.ivImage);
            holder.ivImage.setVisibility(View.VISIBLE);
        } else

        {
            holder.ivImage.setVisibility(View.GONE);
        }
        */
        setAnimation(holder.itemView, position);
    }

    public void removeMarker(int index){
        Toast.makeText(context, "Remove Marker", Toast.LENGTH_SHORT).show();
        myMarkerList.remove(index);
        myMarkerKeys.remove(index);
        notifyItemRemoved(index);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public MyMarker getMyMarker(int position) {
        return myMarkerList.get(position);
    }

    public List<MyMarker> getMyMarkerList() {
        return myMarkerList;
    }

    @Override
    public int getItemCount() {
        return myMarkerList.size();
    }

    public void addMarker(MyMarker newMarker, String key) {
        myMarkerList.add(newMarker);
        myMarkerKeys.add(key);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvDescription;
        public TextView tvLon;
        public TextView tvLat;
        public at.markushi.ui.CircleButton btnDelete;
        public at.markushi.ui.CircleButton btnEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLon = itemView.findViewById(R.id.tvLon);
            tvLat = itemView.findViewById(R.id.tvLat);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

}


