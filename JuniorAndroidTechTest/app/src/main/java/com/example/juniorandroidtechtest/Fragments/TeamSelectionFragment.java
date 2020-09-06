package com.example.juniorandroidtechtest.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.juniorandroidtechtest.Adapters.SimpleTeamAdapter;
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

public class TeamSelectionFragment extends Fragment implements SimpleTeamAdapter.OnTeamListener{

    private ArrayList<String> mTeamPhotos = new ArrayList<>();
    private ArrayList<String> mTeamNames = new ArrayList<>();
    private ArrayList<String> mTeamId = new ArrayList<>();

    private SimpleTeamAdapter mTeamsAdapter;
    private RecyclerView mRecyclerView;

    private View mRoot;

    DataBaseHelper mDataBaseHelper;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mRoot = inflater.inflate(R.layout.fragment_team_selection, container, false);

        // Clear the arrayLists to prevent duplicates, may change this logic.
        mTeamPhotos.clear();
        mTeamNames.clear();
        mTeamId.clear();

        mRecyclerView = mRoot.findViewById(R.id.team_recycler);
        mTeamsAdapter = new SimpleTeamAdapter(mTeamPhotos,mTeamNames,mTeamId,getContext(),this);

        // Make sure adapter is aware if there is a dataset change.
        mRecyclerView.setAdapter(mTeamsAdapter);
        mTeamsAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new JsonTask().execute("https://www.thesportsdb.com/api/v1/json/1/search_all_teams.php?l=English%20Premier%20League");

        mDataBaseHelper = new DataBaseHelper(getContext());

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
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject explrObject = jsonArray.getJSONObject(i);
                    mTeamPhotos.add(explrObject.getString("strTeamBadge"));
                    mTeamNames.add(explrObject.getString("strTeam"));
                    mTeamId.add(explrObject.getString("idTeam"));

                }
                initRecyclerView();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    private  void initRecyclerView(){
        mRecyclerView = mRoot.findViewById(R.id.team_recycler);
        mTeamsAdapter = new SimpleTeamAdapter(mTeamPhotos,mTeamNames,mTeamId,getContext(),this);
        mRecyclerView.setAdapter(mTeamsAdapter);
        mTeamsAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(mTeamNames.isEmpty()){
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Internet Connection Alert")
                    .setMessage("You have no internet connection, this process will not work until you connect again")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
    }


    @Override
    public void OnRecyclerItemClick(int position) {

        final int value = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Please Confirm");
        builder.setMessage("Are you sure this is the team you wish to follow?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                boolean insertdata = mDataBaseHelper.addData(mTeamId.get(value),mTeamNames.get(value),mTeamPhotos.get(value));
                Log.d("1", "Data Added succesfully");
                if (insertdata){
                    Log.d("Toast", "Data Added succesfully");
                }
                else{
                    Log.d("Toast", "Data added unsucc");
                }

                HomeFragment homeFragment = new HomeFragment();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), homeFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
