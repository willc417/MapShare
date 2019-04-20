package com.seniorsem.wdw.mapshare.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.seniorsem.wdw.mapshare.R;
import com.seniorsem.wdw.mapshare.data.MyMarker;

import java.util.ArrayList;
import java.util.List;

public class ViewMarkersRecyclerAdapter extends RecyclerView.Adapter<ViewMarkersRecyclerAdapter.ViewHolder> {

    private CallbackInterface mCallback;
    private Context context;
    private List<MyMarker> myMarkerList;
    private List<String> myMarkerKeys;
    private int lastPosition = -1;

    public interface CallbackInterface{

        /**
         * Callback invoked when clicked
         * @param editMarker the marker to be edited
         */
        void onEditMarker(MyMarker editMarker, int index);
    }


    public ViewMarkersRecyclerAdapter(Context context) {

        try{
            mCallback = (CallbackInterface) context;
        }catch(ClassCastException ex){
            //.. should log the error or throw and exception
            Log.e("MyAdapter","Must implement the CallbackInterface in the Activity", ex);
        }
        this.context = context;
        this.myMarkerList = new ArrayList<>();
        this.myMarkerKeys = new ArrayList<>();
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
        holder.tvTitle.setText(myMarkerList.get(holder.getAdapterPosition()).getTitle());
        holder.tvLon.setText(String.valueOf(
                myMarkerList.get(holder.getAdapterPosition()).getLat()));
        holder.tvLat.setText(String.valueOf(
                myMarkerList.get(holder.getAdapterPosition()).getLon()));

        setAnimation(holder.itemView, position);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onEditMarker(getMyMarker(holder.getAdapterPosition()), holder.getAdapterPosition()); }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                alertDialogBuilder.setMessage("Are you sure you want to remove the Image?");
                alertDialogBuilder.setPositiveButton("Remove",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                removeMarker(holder.getAdapterPosition());
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

        setAnimation(holder.itemView, position);
    }

    public void removeMarker(int index){
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

    private MyMarker getMyMarker(int position) {
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvLon;
        TextView tvLat;
        at.markushi.ui.CircleButton btnDelete;
        at.markushi.ui.CircleButton btnEdit;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLon = itemView.findViewById(R.id.tvLon);
            tvLat = itemView.findViewById(R.id.tvLat);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

}


