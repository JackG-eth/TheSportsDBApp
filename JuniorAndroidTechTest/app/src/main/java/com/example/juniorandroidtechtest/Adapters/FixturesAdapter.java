package com.example.juniorandroidtechtest.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juniorandroidtechtest.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FixturesAdapter extends RecyclerView.Adapter<FixturesAdapter.ViewHolder> {

    // ArrayList that handles animal images
    private ArrayList<String> mEvent;
    // ArrayList that handles animal Names
    private ArrayList<String> mDate;

    private ArrayList<String> mHomeScore;
    private ArrayList<String> mAwayScore;


    // Allows the class to access application specific resources
    private Context mContext;

    // For intercepting the events from a users interaction
    private OnMatchListener mOnMatchListener;


    /*
        SpeciesRecyclerViewAdapter constructor
     */
    public FixturesAdapter(ArrayList<String> Event, ArrayList<String> Date, ArrayList<String> homeScore, ArrayList<String> awayScore, Context context, OnMatchListener onMatchListener) {
        mEvent = Event;
        mDate = Date;
        mHomeScore = homeScore;
        mAwayScore = awayScore;
        mContext = context;
        this.mOnMatchListener = onMatchListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent, false);
        ViewHolder holder = new ViewHolder(view, mOnMatchListener);
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
        holder.date.setText(mDate.get(position));
        holder.fixture.setText(mEvent.get(position));
        //holder.homeScore.setText(mHomeScore.get(position));
        //holder.awayScore.setText(mAwayScore.get(position));
    }

    /*
        Returns the size of the array
     */
    @Override
    public int getItemCount() {
        return mEvent.size();
    }

    /*
        A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView date;
        TextView fixture;
        TextView homeScore;
        TextView awayScore;


        OnMatchListener onMatchListener;

        public ViewHolder(@NonNull View itemView, OnMatchListener onMatchListener) {
            super(itemView);
            date = itemView.findViewById(R.id.item_date);
            fixture = itemView.findViewById(R.id.item_fixture);
            //homeScore = itemView.findViewById(R.id.home_score);
            //awayScore = itemView.findViewById(R.id.away_score);
            this.onMatchListener = onMatchListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMatchListener.OnRecyclerItemClick(getAdapterPosition());
        }
    }

    public interface OnMatchListener {
        void OnRecyclerItemClick(int position);
    }
}
