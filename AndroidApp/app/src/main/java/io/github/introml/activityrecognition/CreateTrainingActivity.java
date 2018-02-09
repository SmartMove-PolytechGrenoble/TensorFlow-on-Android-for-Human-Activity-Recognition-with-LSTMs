package io.github.introml.activityrecognition;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import BusinessClass.Exercice;
import BusinessClass.Movement;
import BusinessClass.Training;
import BusinessClass.TrainingAdapter;

public class CreateTrainingActivity extends AppCompatActivity {

    private Spinner movementSpinner;

    private Training training;

    private ListView trainingListView;

    private Button addExerciceButton;
    private TextView repetitionTextView;

    private  TrainingAdapter trainingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_training);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movementSpinner = (Spinner) findViewById(R.id.movementSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movements, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        movementSpinner.setAdapter(adapter);


        training=new Training();
        training.addExercice(new Exercice(Movement.SITTING, 1));
        training.addExercice(new Exercice(Movement.SITTING, 1));

        trainingListView = (ListView) findViewById(R.id.exercicesListView);

        trainingAdapter = new TrainingAdapter(CreateTrainingActivity.this, training);
        trainingListView.setAdapter(trainingAdapter);


        movementSpinner = (Spinner) findViewById(R.id.movementSpinner);
        repetitionTextView = (TextView) findViewById(R.id.repetitionTextView);

        addExerciceButton = (Button) findViewById(R.id.addTrainingButton);
        addExerciceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movement m = Movement.stringToMovement((String) movementSpinner.getSelectedItem());
                String s =repetitionTextView.getText().toString();

                if(s.equals("") || Integer.parseInt(s) < 1){
                    Context context = getApplicationContext();
                    CharSequence text = "Please enter a correct number of repetion.";
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


    }

}
