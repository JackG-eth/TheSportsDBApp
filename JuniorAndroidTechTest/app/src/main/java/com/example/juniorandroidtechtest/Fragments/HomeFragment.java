package com.example.juniorandroidtechtest.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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

public class HomeFragment extends Fragment {

    private String mAnimalName;
    private String mImage;
    private String mTeamID;
    private String teamInfo;

    private TextView mName;
    private ImageView mAnimalImage;

    private View mRoot;

    private String altName;
    private String league;
    private String stadium;
    private String website;
    private String description;

    private TextView mAltName;
    private TextView mLeague;
    private TextView mStadium;
    private TextView mWebsite;
    private TextView mDescription;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mRoot = inflater.inflate(R.layout.fragment_home, container, false);

        mName = mRoot.findViewById(R.id.recycler_image_text_child);
        mAnimalImage = mRoot.findViewById(R.id.recycler_image_child);

        mAltName = mRoot.findViewById(R.id.alternate);
        mLeague = mRoot.findViewById(R.id.League);
        mStadium = mRoot.findViewById(R.id.Stadium);
        mWebsite = mRoot.findViewById(R.id.website);
        mDescription = mRoot.findViewById(R.id.Description);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            mAnimalName = bundle.getString("TeamName");
            mImage = bundle.getString("TeamPhoto");
            mTeamID = bundle.getString("TeamId");

            Log.d("ID", "onPostExecute: " + mTeamID);
        }

        mName.setText(mAnimalName);
        Glide.with(getActivity())
                .asBitmap()
                .load(mImage)
                .centerCrop()
                .error(Glide.with(getActivity()).asBitmap().load(R.drawable.ic_launcher_background))
                .centerCrop()
                .into(mAnimalImage);


        teamInfo = "https://www.thesportsdb.com/api/v1/json/1/lookupteam.php?id=" + mTeamID;

        Log.d("StringT", "onPostExecute: " + teamInfo);
        new JsonTask().execute(teamInfo);
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
            try {
                JSONObject jsonbject = new JSONObject(result);
                JSONArray jsonArray = jsonbject.getJSONArray("teams");
                JSONObject explrObject = jsonArray.getJSONObject(0);
                mAltName.setText(altName = explrObject.getString("strAlternate"));
                mLeague.setText(league = explrObject.getString("strLeague"));
                mStadium.setText(stadium = explrObject.getString("strStadium"));
                mWebsite.setText(website = explrObject.getString("strWebsite"));
                mDescription.setText(description = explrObject.getString("strDescriptionEN"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
