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

import com.google.firebase.auth.FirebaseAuth;
import com.seniorsem.wdw.mapshare.CreateMapActivity;
import com.seniorsem.wdw.mapshare.R;
import com.seniorsem.wdw.mapshare.ViewMapsActivity;
import com.seniorsem.wdw.mapshare.data.Map;

import java.util.ArrayList;
import java.util.List;

public class ViewMapsRecyclerAdapter extends RecyclerView.Adapter<ViewMapsRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Map> mapList;
    private List<String> mapKeys;
    private String creatorUID;
    private int lastPosition = -1;

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
        holder.date.setText(context.getString(R.string.date,
                mapList.get(holder.getAdapterPosition()).getDate()));

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


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void removeMap(int index) {
        String docKey = FirebaseAuth.getInstance().getCurrentUser().getEmail() + "_" + mapList.get(index).getTitle().replace(" ", "_");
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ViewMapsActivity.deleteMap(docKey, userKey);
        mapList.remove(index);
        mapKeys.remove(index);
        notifyItemRemoved(index);
    }

    public void removeMapByKey(String key) {
        int index = mapKeys.indexOf(key);
        if (index != -1) {
            mapList.remove(index);
            mapKeys.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void removeAll() {
        for (int i = 0; i < mapKeys.size(); i++) {
            removeMapByKey(mapKeys.get(i));
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


