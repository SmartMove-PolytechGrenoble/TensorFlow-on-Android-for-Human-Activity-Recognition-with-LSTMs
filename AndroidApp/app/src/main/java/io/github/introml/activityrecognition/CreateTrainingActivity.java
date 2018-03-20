package io.github.introml.activityrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import BusinessClass.Exercice;
import BusinessClass.Movement;
import BusinessClass.Training;

/**
 * Created by anthony on 14/03/18.
 */

public class CreateTrainingActivity extends Activity {

    private Spinner movementSpinner;

    private Training training;

    private ListView trainingListView;

    private Button addExerciceButton;
    private Button saveTrainingButton;
    private TextView repetitionTextView;
    private TextView trainingNameTextView;

    private  TrainingAdapter trainingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_training);


        movementSpinner = (Spinner) findViewById(R.id.movementSpinner);

        ArrayAdapter<Movement> adapter = new ArrayAdapter<Movement>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item,Movement.getMovements());
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        movementSpinner.setAdapter(adapter);



        trainingListView = (ListView) findViewById(R.id.exercicesListView);

        createTraining();

        movementSpinner = (Spinner) findViewById(R.id.movementSpinner);
        repetitionTextView = (TextView) findViewById(R.id.repetitionTextView);
        trainingNameTextView = (TextView) findViewById(R.id.trainingNameTextView);

        addExerciceButton = (Button) findViewById(R.id.addTrainingButton);
        addExerciceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movement m = (Movement) movementSpinner.getSelectedItem();
                String s =repetitionTextView.getText().toString();

                if(s.equals("") || Integer.parseInt(s) < 1){
                    Context context = getApplicationContext();
                    CharSequence text = "Please enter a correct number of repetition.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    int repetition = Integer.parseInt(s);
                    training.addExercice(new Exercice(m,repetition));
                    trainingAdapter.notifyDataSetChanged();
                }

            }
        });

        saveTrainingButton = (Button) findViewById(R.id.saveTrainingButton);
        saveTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();

                String trainingName = trainingNameTextView.getText().toString();
                training.setName(trainingName);

                editor.putString("TRAINING" + trainingName, training.getGSON(trainingName));
                editor.commit();

                Map<String,?> keys = sharedPref.getAll();
                Log.e("Test", "Bite");
                for(Map.Entry<String,?> entry : keys.entrySet()){
                    Log.e("map values",entry.getKey() + ": " + entry.getValue().toString());
                }

                Context context = getApplicationContext();
                CharSequence text = "Traning " + trainingName + " saved";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                repetitionTextView.setText("");
                trainingNameTextView.setText("");
                createTraining();

            }
        });


    }

    private void createTraining() {

        training=new Training();

        trainingAdapter = new TrainingAdapter(CreateTrainingActivity.this, training);
        trainingListView.setAdapter(trainingAdapter);

        trainingAdapter.notifyDataSetChanged();
    }

}