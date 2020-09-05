package com.example.juniorandroidtechtest.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.juniorandroidtechtest.R;

import java.util.ArrayList;

public class SimpleTeamAdapter extends RecyclerView.Adapter<SimpleTeamAdapter.ViewHolder> {

    // ArrayList that handles animal images
    private ArrayList<String> mTeamImages;
    // ArrayList that handles animal Names
    private ArrayList<String> mTeamNames;

    private ArrayList<String> mTeamIds;


    // Allows the class to access application specific resources
    private Context mContext;

    // For intercepting the events from a users interaction
    private OnTeamListener mOnTeamListener;


    /*
        SpeciesRecyclerViewAdapter constructor
     */
    public SimpleTeamAdapter(ArrayList<String> teamImages, ArrayList<String> teamNames, ArrayList<String> teamIds, Context context, OnTeamListener onTeamListener ) {
        mTeamImages = teamImages;
        mTeamNames = teamNames;
        mTeamIds = teamIds;
        mContext = context;
        this.mOnTeamListener = onTeamListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        ViewHolder holder = new ViewHolder(view, mOnTeamListener);
        return holder;
    }

    /*
        Returns the items position in the array, useful for determining which item the user has pressed
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Important method, changes on what layouts look like
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        // GLIDE BUMPTECH image loading from internet API
        Glide.with(mContext)
                .asBitmap()
                .load(mTeamImages.get(position))
                .centerCrop()
                .into(holder.teamImage);

        holder.teamName.setText(mTeamNames.get(position));

    }

    /*
        Returns the size of the array
     */
    @Override
    public int getItemCount() {
        return mTeamNames.size();
    }

    /*
        A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        ImageView teamImage;
        TextView teamName;

        OnTeamListener onTeamListener;

        public ViewHolder(@NonNull View itemView, OnTeamListener onTeamListener) {
            super(itemView);
            teamImage = itemView.findViewById(R.id.recycler_image);
            teamName = itemView.findViewById(R.id.recycler_image_text);

            this.onTeamListener = onTeamListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTeamListener.OnRecyclerItemClick(getAdapterPosition());
        }
    }

    public interface OnTeamListener {
        void OnRecyclerItemClick(int position);
    }
}
