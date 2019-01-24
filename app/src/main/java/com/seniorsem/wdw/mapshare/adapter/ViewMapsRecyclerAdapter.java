package com.seniorsem.wdw.mapshare.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.seniorsem.wdw.mapshare.R;
import com.seniorsem.wdw.mapshare.data.Map;

import java.util.ArrayList;
import java.util.List;

public class ViewMapsRecyclerAdapter extends RecyclerView.Adapter<ViewMapsRecyclerAdapter.ViewHolder>{

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
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvCreator.setText(context.getString(R.string.creator,
                mapList.get(holder.getAdapterPosition()).getCreaterUID()));
        holder.tvTitle.setText(context.getString(R.string.title,
                mapList.get(holder.getAdapterPosition()).getTitle()));
        holder.tvDescription.setText(
                mapList.get(holder.getAdapterPosition()).getDescription());
        holder.date.setText(context.getString(R.string.date,
                mapList.get(holder.getAdapterPosition()).getDate()));

        setAnimation(holder.itemView, position);

        /*if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(postList.get(holder.getAdapterPosition()).getUid())) {
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePost(holder.getAdapterPosition());
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }*/

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
        FirebaseDatabase.getInstance().getReference("posts").child(
                mapKeys.get(index)).removeValue();
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

        public ViewHolder(View itemView) {
            super(itemView);
            tvCreator = itemView.findViewById(R.id.tvCreator);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            date = itemView.findViewById(R.id.date);
        }
    }

}


