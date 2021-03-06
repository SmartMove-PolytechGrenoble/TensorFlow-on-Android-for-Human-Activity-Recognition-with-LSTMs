package io.github.introml.activityrecognition;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import BusinessClass.Exercice;
import BusinessClass.Movement;
import BusinessClass.Training;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private int currentMoveEnd = 1;
    private static final boolean WITH_GYROSCOPE = false;
    public static final int N_SAMPLES = 165;
    private int multiplier = 3;

    private static List<Float> xa;
    private static List<Float> ya;
    private static List<Float> za;
    private static List<Float> xr;
    private static List<Float> yr;
    private static List<Float> zr;

    private TextView marcherTextView;
    private TextView rienTextView;
    private TextView sauterTextView;
    private TextView mostLikelyTextView;
    private TextToSpeech textToSpeech;
    private float[] results;
    private TensorFlowClassifier classifier;

    private Spinner spinner;
    private List<Training> trainings = new LinkedList<>();
    private Button launchTraining;

    /* The corresponding Label Proba TextView */
    private HashMap<String,TextView> labelTextViews = new HashMap<String,TextView>();
    private static List<String> labels = Arrays.asList("Jogging","Marcher","PasC","Rien","Sauter", "Squat", "360");
    /* Move indexes to count */
    private HashMap<Integer, TextView> moveToCountIdx = new HashMap<Integer, TextView>();
    private List<String> labelsToCount = Arrays.asList("Sauter", "Squat", "360");

    /* Corresponding validation rate x in a row */
    private final Integer moveToCountValidation[] = {0, 0, 0, 0, 1, 1, 2};

    private Integer currentMoveValidation[] = {0, 0, 0, 0, 1, 1, 2};

    /* This array is init with zeros */
    private int[] motionCounter = new int[labels.size()];

    /* This is the threshhold to accept a move */
    private static final double validation = 0.98 ;

    private static final double[] validationMove = {0.87, 0.97, 0.97, 0.97, 0.96, 0.93, 0.93};

    private static int currentMove = -1;
    private static long currentMoveTime = 0;
    private static long currentMoveStartTime = 0;

    private Training trainingTest;

    /* Sound */
    private ToneGenerator toneGen1;

    public static Integer getOutputNumber(){
        return labels.size();
    }

    public static Integer getSampleNumber(){
        return N_SAMPLES;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TableLayout scrollLayout = (TableLayout) findViewById(R.id.label_list);

        /* Setting each row with probability */
        for (String label : labels) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            tr.setPadding(0,0,0,50);

            TextView tvLabel = new TextView(this);
            tvLabel.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, (float) 1.0));
            tvLabel.setText(label);
            tvLabel.setTypeface(null, Typeface.BOLD);
            tvLabel.setGravity(Gravity.CENTER);
            tvLabel.setTextSize(18);

            TextView tvProba = new TextView(this);
            tvProba.setGravity(Gravity.CENTER);
            tvProba.setTextSize(18);
            tvProba.setText("0.0");

            tr.addView(tvLabel);
            tr.addView(tvProba);
            labelTextViews.put(label, tvProba);
            scrollLayout.addView(tr);
        }

        /* Adding move to count rows */
        for (String label : labelsToCount) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            tr.setPadding(0,0,0,40);

            TextView tvLabel = new TextView(this);
            tvLabel.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, (float) 1.0));
            tvLabel.setText("Compteur : "+label);
            tvLabel.setTypeface(null, Typeface.BOLD);
            tvLabel.setGravity(Gravity.CENTER);
            tvLabel.setTextSize(18);

            TextView tvProba = new TextView(this);
            tvProba.setGravity(Gravity.CENTER);
            tvProba.setTextSize(18);
            tvProba.setText("0");

            tr.addView(tvLabel);
            tr.addView(tvProba);
            moveToCountIdx.put(labels.indexOf(label),tvProba);

            scrollLayout.addView(tr);
        }

        xa = new ArrayList<>();
        ya = new ArrayList<>();
        za = new ArrayList<>();

        xr = new ArrayList<>();
        yr = new ArrayList<>();
        zr = new ArrayList<>();

        for (String label : labels) {
           // R.id.
        }

        classifier = new TensorFlowClassifier(getApplicationContext());

        mostLikelyTextView = (TextView) findViewById(R.id.mostLikely_activity);

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);

        generateTraining();

        spinner = (Spinner) findViewById(R.id.trainingChoiceSpinner);

        ArrayAdapter<Training> adapter = new ArrayAdapter<Training>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item,trainings);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);



        launchTraining = (Button) findViewById(R.id.launchTrainingButton);
        launchTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainingTest= (Training) spinner.getSelectedItem();

                trainingTest.lauchTraining();

                // Mise à jour de l'affichage et du speaking
                TextView nextMoveTextView = (TextView) findViewById(R.id.nextMoveTextView);
                nextMoveTextView.setText(trainingTest.getText());
                textToSpeech.speak(trainingTest.getTextToSpeech(), TextToSpeech.QUEUE_ADD, null);
            }
        });

        trainingTest= (Training) spinner.getSelectedItem();
        TextView nextMoveTextView = (TextView) findViewById(R.id.nextMoveTextView);
        nextMoveTextView.setText("");

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    }



    @Override
    public void onInit(int status) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (results == null || results.length == 0) {
                    return;
                }
                float max = -1;
                int idx = -1;
                for (int i = 0; i < results.length; i++) {
                    if (results[i] > max) {
                        idx = i;
                        max = results[i];
                    }
                }

//                textToSpeech.speak(String.valueOf(motionCounter[idx]), TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
//                if(results[idx] > validation && moveToCountIdx.containsKey(idx) && currentMoveEnd == 1) {
//                    textToSpeech.speak(labels.get(idx), TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
//                    currentMoveEnd = 0;
//                }
            }
        }, 2000, 3000);
    }

    protected void onPause() {
        getSensorManager().unregisterListener(this);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), 10000);
        if(WITH_GYROSCOPE) {
            getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE), 10000);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        activityPrediction();

        if(event.sensor.getType() != 4) {
            if(xa.size() < N_SAMPLES) {
                xa.add(event.values[0]);
                ya.add(event.values[1]);
                za.add(event.values[2]);
            }
        }
        else{
            if(xr.size() < N_SAMPLES && WITH_GYROSCOPE) {
                xr.add(event.values[0]);
                yr.add(event.values[1]);
                zr.add(event.values[2]);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void deletePart(List l, int n){
        for(int i = 0; i<n; i++){
            l.remove(0);
        }
    }

    private void resetAllValidation(){
        int j = 0;
        for(Integer i : currentMoveValidation){
            currentMoveValidation[j] = moveToCountValidation[j];
            j++;
        }
    }

    private void activityPrediction() {
        if (xa.size() >= N_SAMPLES && ya.size() >= N_SAMPLES && za.size() >= N_SAMPLES) {
            if (WITH_GYROSCOPE && !(xr.size() >= N_SAMPLES && yr.size() >= N_SAMPLES && zr.size() >= N_SAMPLES)){
                return;
            }
            List<Float> data = new ArrayList<>();

            data.addAll(xa);
            data.addAll(ya);
            data.addAll(za);

            data.addAll(xr);
            data.addAll(yr);
            data.addAll(zr);

            results = classifier.predictProbabilities(toFloatArray(data));

            int labelIdx = 0;

            for (String label : labels) {
                TextView tv = labelTextViews.get(label);
                tv.setText(Float.toString(round(results[labelIdx],2)));
                labelIdx++;
            }

            int mostLikely = mostLikely(results);

            if(results[mostLikely] <= validationMove[mostLikely]){
                mostLikely = -1;
                mostLikelyTextView.setText("Not recognized");
            }

            /* A new move is starting */
            if (mostLikely != currentMove) {
                resetAllValidation();
                currentMoveEnd = 1;
                currentMove = mostLikely;
                currentMoveStartTime = System.nanoTime();
            }

            currentMoveTime = (currentMoveStartTime - System.nanoTime()) / 1000000;

            if(mostLikely != -1){
                Movement currentMovement = intToMove(mostLikely);
                /* Does this move needs to be counted ? */
                if(moveToCountIdx.containsKey(mostLikely)){
                    if(currentMoveEnd == 1 && currentMoveValidation[mostLikely] == 0) {
                        motionCounter[mostLikely]++;

                        if(trainingTest.getCurrentExercice() != null) {
                            if (trainingTest.getCurrentExercice().getMovement() == intToMove(mostLikely)) {
                                // Training updating
                                // Avancement d'un "pas"  dans le training
                                trainingTest.doAMovement(currentMovement);
                                // Mise à jour de l'affichage et de l'aide vocale
                                TextView nextMoveTextView = (TextView) findViewById(R.id.nextMoveTextView);
                                nextMoveTextView.setText(trainingTest.getText());
                                textToSpeech.speak(trainingTest.getTextToSpeech(), TextToSpeech.QUEUE_ADD, null);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                            }
                        }
                        moveToCountIdx.get(mostLikely).setText(Integer.toString(motionCounter[mostLikely]));


                        //textToSpeech.speak(labels.get(mostLikely), TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
                        
                        currentMoveEnd = 0;
                    }else{
                        currentMoveValidation[mostLikely]--;
                    }
                }else{

                    motionCounter[mostLikely]++;
                    currentMoveEnd = 1;
                    if(trainingTest.getCurrentExercice() != null) {
                        if (trainingTest.getCurrentExercice().getMovement() == intToMove(mostLikely)) {
                            trainingTest.doAMovement(currentMovement);
                            TextView nextMoveTextView = (TextView) findViewById(R.id.nextMoveTextView);
                            nextMoveTextView.setText(trainingTest.getText());
                            textToSpeech.speak(trainingTest.getTextToSpeech(), TextToSpeech.QUEUE_ADD, null);
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                        }
                    }
                    //ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                }

                mostLikelyTextView.setText(labels.get(mostLikely) + " " + motionCounter[mostLikely] + "ms");
            }
            else{
                mostLikelyTextView.setText("Not recognized for " + currentMoveTime + "ms");
            }

            deletePart(za, N_SAMPLES/multiplier);
            deletePart(ya, N_SAMPLES/multiplier);
            deletePart(xa, N_SAMPLES/multiplier);
           /*
            zr.clear();
            yr.clear();
            xr.clear();
            za.clear();
            ya.clear();
            xa.clear();
            */


        }
    }

    /* Returns the label number of the most probable current move */
    private int mostLikely(float[] results) {
        int mostLikely = 0;

        for (int i = 0; i < results.length; i++){
            if (results[i] > results[mostLikely]){
                mostLikely = i;
            }
        }

        return mostLikely;
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }
//"Jogging","Marcher","PasC","Rien","Sauter", "Squat", "360"
    private Movement intToMove(int i){
        switch (i){
            case 0 :
                return Movement.JOGGING;
            case 1 :
                return Movement.WALKING;
            case 2 :
                return Movement.PASC;
            case 4:
                return Movement.JUMPING;
            case 5:
                return Movement.SQUAT;
            case 6:
                return Movement.THREESIX;
            default :
                return Movement.NOTHING;
        }
    }

    private void generateTraining() {

        String[] trainingNames = getResources().getStringArray(R.array.training_array);

        Training training=new Training();
        training.setName("Niveau 2");
        training.addExercice(new Exercice(Movement.JUMPING, 5));
        training.addExercice(new Exercice(Movement.THREESIX, 2));
        training.addExercice(new Exercice(Movement.WALKING, 3));
        training.addExercice(new Exercice(Movement.JUMPING, 5));
        trainings.add(training);

        training=new Training();
        training.setName("Lafay");
        training.addExercice(new Exercice(Movement.WALKING, 3));
        training.addExercice(new Exercice(Movement.JUMPING, 5));
        training.addExercice(new Exercice(Movement.THREESIX, 1));
        trainings.add(training);

        training=new Training();
        training.setName("ALM");
        training.addExercice(new Exercice(Movement.JUMPING, 5));
        training.addExercice(new Exercice(Movement.NOTHING, 1));
        training.addExercice(new Exercice(Movement.JUMPING, 5));
        training.addExercice(new Exercice(Movement.NOTHING, 1));
        training.addExercice(new Exercice(Movement.JUMPING, 5));
        training.addExercice(new Exercice(Movement.NOTHING, 1));
        trainings.add(training);

        training=new Training();
        training.setName("SD");
        training.addExercice(new Exercice(Movement.THREESIX, 1));
        training.addExercice(new Exercice(Movement.JUMPING, 5));
        training.addExercice(new Exercice(Movement.THREESIX, 1));
        training.addExercice(new Exercice(Movement.JUMPING, 5));
        training.addExercice(new Exercice(Movement.THREESIX, 1));

        trainings.add(training);

        training=new Training();
        training.setName("Iron Man Demo");
        training.addExercice(new Exercice(Movement.SQUAT, 3));
        training.addExercice(new Exercice(Movement.JOGGING, 10));
        training.addExercice(new Exercice(Movement.WALKING, 10));
        training.addExercice(new Exercice(Movement.JOGGING, 10));
        training.addExercice(new Exercice(Movement.WALKING, 10));
        training.addExercice(new Exercice(Movement.PASC, 10));
        training.addExercice(new Exercice(Movement.JUMPING, 5));
        training.addExercice(new Exercice(Movement.THREESIX, 2));
        training.addExercice(new Exercice(Movement.PASC, 10));
        training.addExercice(new Exercice(Movement.WALKING, 10));
        trainings.add(training);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(entry.getKey().contains("TRAINING")){
                Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
                Training t = new Training(entry.getValue().toString());
                trainings.add(t);
            }
        }


    }
}



