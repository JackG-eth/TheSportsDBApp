package com.example.juniorandroidtechtest.Fragments;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.juniorandroidtechtest.Adapters.FixturesAdapter;
import com.example.juniorandroidtechtest.Database.DataBaseHelper;
import com.example.juniorandroidtechtest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements FixturesAdapter.OnMatchListener {

    private String mAnimalName;
    private String mImage;
    private String mTeamID;
    private String mTeamInfo;
    private String mFixtureList;

    private TextView mName;
    private ImageView mAnimalImage;
    private View mRoot;

    boolean mFixtureNext;
    boolean mFixtureLast;

    private TextView mAltName;
    private TextView mLeague;
    private TextView mStadium;
    private TextView mWebsite;
    private TextView mDescription;

    private ArrayList<String> mEvents = new ArrayList<>();
    private ArrayList<String> mDates = new ArrayList<>();
    private ArrayList<String> mHomeScore = new ArrayList<>();
    private ArrayList<String> mAwayScore = new ArrayList<>();

    private Button mButtonLast;
    private Button mButtonNext;
    DataBaseHelper mDataBaseHelper;

    private ArrayList<String> mStoredData = new ArrayList<>();

    private FixturesAdapter mFixturesAdapter;
    private RecyclerView mRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFixtureNext = false;
        mFixtureLast = false;

        mRoot = inflater.inflate(R.layout.fragment_home, container, false);

        mName = mRoot.findViewById(R.id.recycler_image_text_child);
        mAnimalImage = mRoot.findViewById(R.id.recycler_image_child);

        mButtonNext = mRoot.findViewById(R.id.next_five);
        mButtonLast = mRoot.findViewById(R.id.last_five);

        mRecyclerView = mRoot.findViewById(R.id.fixture_recycler);
        mFixturesAdapter = new FixturesAdapter(mEvents,mDates,mHomeScore,mAwayScore,getContext(),this);

        // Make sure adapter is aware if there is a dataset change.
        mRecyclerView.setAdapter(mFixturesAdapter);
        mFixturesAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Here", "onClick: Here");
                mEvents.clear();
                mDates.clear();
                mHomeScore.clear();
                mAwayScore.clear();
                mFixturesAdapter.notifyDataSetChanged();
                mFixtureNext = true;
                mFixtureList = "https://www.thesportsdb.com/api/v1/json/1/eventsnext.php?id=" + mTeamID;
                new JsonTask().execute(mFixtureList);
            }
        });
        mButtonLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Here", "onClick: Here");
                mEvents.clear();
                mDates.clear();
                mHomeScore.clear();
                mAwayScore.clear();
                mFixturesAdapter.notifyDataSetChanged();
                mFixtureLast = true;
                mFixtureList = "https://www.thesportsdb.com/api/v1/json/1/eventslast.php?id=" + mTeamID;
                new JsonTask().execute(mFixtureList);
            }
        });

        mAltName = mRoot.findViewById(R.id.alternate);
        mLeague = mRoot.findViewById(R.id.League);
        mStadium = mRoot.findViewById(R.id.Stadium);
        mWebsite = mRoot.findViewById(R.id.website);
        mDescription = mRoot.findViewById(R.id.Description);

        mDataBaseHelper = new DataBaseHelper(getActivity());
        mDataBaseHelper.getReadableDatabase();
        mStoredData = mDataBaseHelper.getDataArray();
        if(mStoredData.size() == 0){
            mTeamID = "";
            mAnimalName ="";
            mImage = "";
        }
        else {
            mTeamID = mStoredData.get(0);
            mAnimalName = mStoredData.get(1);
            mImage = mStoredData.get(2);
        }

        if(mTeamID.equals("")){
            String ftag = "first Launch";
            TeamSelectionFragment testFrag = new TeamSelectionFragment();

            FragmentManager fragmentManager = getFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContent, testFrag, ftag);
            fragmentTransaction.addToBackStack(ftag);
            fragmentTransaction.commit();
        }

        else {
            mName.setText(mAnimalName);
            Glide.with(getActivity())
                    .asBitmap()
                    .load(mImage)
                    .centerCrop()
                    .error(Glide.with(getActivity()).asBitmap().load(R.drawable.ic_launcher_background))
                    .centerCrop()
                    .into(mAnimalImage);


            mTeamInfo = "https://www.thesportsdb.com/api/v1/json/1/lookupteam.php?id=" + mTeamID;

            Log.d("StringT", "onPostExecute: " + mTeamInfo);
            new JsonTask().execute(mTeamInfo);
        }
        return mRoot;
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(mFixtureNext){
                Log.d("HereInsideTest", "onClick: Here");
                try {
                    JSONObject jsonbject = new JSONObject(result);
                    JSONArray jsonArray = jsonbject.getJSONArray("events");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        mDates.add(explrObject.getString("dateEvent"));
                        mEvents.add(explrObject.getString("strEvent"));
                        if (explrObject.getString("intHomeScore").equals("null") || explrObject.getString("intHomeScore") == "null" ){
                            mHomeScore.add("Time of Match");
                            mAwayScore.add(explrObject.getString("strTime"));
                        }else{
                            mHomeScore.add(explrObject.getString("intHomeScore"));
                            mAwayScore.add(explrObject.getString("intAwayScore"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initRecyclerView();
            }
            else if(mFixtureLast){
                Log.d("HereInsideTest", "onClick: Here");
                try {
                    JSONObject jsonbject = new JSONObject(result);
                    JSONArray jsonArray = jsonbject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        mDates.add(explrObject.getString("dateEvent"));
                        mEvents.add(explrObject.getString("strEvent"));
                        mHomeScore.add(explrObject.getString("intHomeScore"));
                        mAwayScore.add(explrObject.getString("intAwayScore"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initRecyclerView();
            }
            else {
                try {
                    JSONObject jsonbject = new JSONObject(result);
                    JSONArray jsonArray = jsonbject.getJSONArray("teams");
                    JSONObject explrObject = jsonArray.getJSONObject(0);
                    mAltName.setText(explrObject.getString("strAlternate"));
                    mLeague.setText(explrObject.getString("strLeague"));
                    mStadium.setText(explrObject.getString("strStadium"));
                    mWebsite.setText(explrObject.getString("strWebsite"));
                    mDescription.setText(explrObject.getString("strDescriptionEN"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private  void initRecyclerView(){
        mRecyclerView = mRoot.findViewById(R.id.fixture_recycler);
        mFixturesAdapter = new FixturesAdapter(mEvents,mDates,mHomeScore,mAwayScore,getContext(),this);
        // Make sure adapter is aware if there is a dataset change.
        mRecyclerView.setAdapter(mFixturesAdapter);
        mFixturesAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFixtureLast = false;
        mFixtureNext = false;
    }

    @Override
    public void OnRecyclerItemClick(int position) {

        if(mHomeScore.get(position).equals("Time of Match")){
            final Dialog dialog = new Dialog(getActivity());
            View dView = getLayoutInflater().inflate(R.layout.futuredialog, null);

            final TextView nMatchName = dView.findViewById(R.id.match_played_future);
            final TextView nHomeScore = dView.findViewById(R.id.support_text);
            final TextView nAwayScore = dView.findViewById(R.id.time_played);

            nMatchName.setText(mEvents.get(position));
            nHomeScore.setText(mHomeScore.get(position));
            nAwayScore.setText(mAwayScore.get(position));

            dialog.setContentView(dView);
            dialog.create();
            dialog.show();
        }
        else {
            final Dialog dialog = new Dialog(getActivity());
            View dView = getLayoutInflater().inflate(R.layout.previousdialog, null);

            final TextView nMatchName = dView.findViewById(R.id.match_played);
            final TextView nHomeScore = dView.findViewById(R.id.home_goals);
            final TextView nAwayScore = dView.findViewById(R.id.away_goals);

            nMatchName.setText(mEvents.get(position));
            nHomeScore.setText(mHomeScore.get(position));
            nAwayScore.setText(mAwayScore.get(position));

            dialog.setContentView(dView);
            dialog.create();
            dialog.show();
        }
    }

}
