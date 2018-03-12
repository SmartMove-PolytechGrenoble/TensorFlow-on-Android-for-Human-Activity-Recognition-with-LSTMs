package io.github.introml.activityrecognition;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import BusinessClass.Training;

public class TrainingListActivity extends AppCompatActivity {

    private ListView trainingListListView;
    private TrainingListAdapter trainingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_list);

        trainingListListView = (ListView) findViewById(R.id.trainingListListView);



    }

    private void showTraining() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        List<Training> trainingList = new ArrayList<>();

        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(entry.getKey().contains("TRAINING")){
                Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
                Training t = new Training(entry.getValue().toString());
                trainingList.add(t);
            }
        }

        Log.d("Bite", trainingList.size() + "éléments");

        trainingListAdapter = new TrainingListAdapter(TrainingListActivity.this, trainingList);
        trainingListListView.setAdapter(trainingListAdapter);

        trainingListAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();

        showTraining();
    }
}
