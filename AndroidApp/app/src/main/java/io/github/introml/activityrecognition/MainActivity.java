package io.github.introml.activityrecognition;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import BusinessClass.Exercice;
import BusinessClass.Movement;
import BusinessClass.Training;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private int currentMoveEnd = 1;
    private static final boolean WITH_GYROSCOPE = false;
    private static final int N_SAMPLES = 100;
    private static List<Float> xa;
    private static List<Float> ya;
    private static List<Float> za;
    private static List<Float> xr;
    private static List<Float> yr;
    private static List<Float> zr;

    private HashMap<Integer, TextView> moveToCountIdx = new HashMap<Integer, TextView>();

    private TextView marcherTextView;
    private TextView rienTextView;
    private TextView sauterTextView;
    private TextView mostLikelyTextView;
    private TextView sauterCountTextView;
    private TextToSpeech textToSpeech;
    private float[] results;
    private TensorFlowClassifier classifier;

    private String[] labels = {"Marcher", "Rien", "Sauter"};
    /* Move indexes to count */
    private List moveToCount = new ArrayList<>();

    private int[] motionCounter = new int[labels.length];
    /* This array is init with zeros */
    /* This is the threshhold to accept a move */
    private static final double validation = 0.98;


    private static int currentMove = -1;
    private static long currentMoveTime = 0;
    private static long currentMoveStartTime = 0;

    private Training trainingTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xa = new ArrayList<>();
        ya = new ArrayList<>();
        za = new ArrayList<>();
        xr = new ArrayList<>();
        yr = new ArrayList<>();
        zr = new ArrayList<>();



        rienTextView = (TextView) findViewById(R.id.rien_prob);
        sauterTextView = (TextView) findViewById(R.id.sauter_prob);
        marcherTextView = (TextView) findViewById(R.id.marcher_prob);
        sauterCountTextView = (TextView) findViewById(R.id.sauter_count);

        moveToCount.add(2);
        moveToCountIdx.put(2, sauterCountTextView);

        classifier = new TensorFlowClassifier(getApplicationContext());

        mostLikelyTextView = (TextView) findViewById(R.id.mostLikely_activity);

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);

        trainingTest=new Training();

        //trainingTest.addExercice(new Exercice(Movement.JUMPING, 10));
        trainingTest.addExercice(new Exercice(Movement.NOTHING, 5));
        //trainingTest.addExercice(new Exercice(Movement.WALKING, 10));


        trainingTest.lauchTraining();

        TextView nextMoveTextView = (TextView) findViewById(R.id.nextMoveTextView);
        nextMoveTextView.setText(trainingTest.getText());


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
                if(results[idx] > validation && moveToCountIdx.containsKey(idx) && currentMoveEnd == 1) {
                    textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
                    currentMoveEnd = 0;
                }
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

            marcherTextView.setText(Float.toString(round(results[0], 2)));
            sauterTextView.setText(Float.toString(round(results[2], 2)));
            rienTextView.setText(Float.toString(round(results[1], 2)));

            int mostLikely = mostLikely(results);

            if(results[mostLikely] < validation){
                mostLikely = -1;
                mostLikelyTextView.setText("Not recognized");
            }

            if (mostLikely != currentMove) {
                if(0 <= mostLikely){
                    motionCounter[mostLikely]++;
                }
                currentMove = mostLikely;
                currentMoveStartTime = System.nanoTime();
            }

            currentMoveTime = (currentMoveStartTime - System.nanoTime()) / 1000000;

            if(mostLikely != -1){
                if(moveToCountIdx.containsKey(mostLikely)){
                    moveToCountIdx.get(mostLikely).setText(Integer.toString(motionCounter[mostLikely]));
                }else{
                    currentMoveEnd = 1;
                }

                mostLikelyTextView.setText(labels[mostLikely] + " " + currentMoveTime + "ms : " + motionCounter[mostLikely]);
            }
            else{
                mostLikelyTextView.setText("Not recognized for " + currentMoveTime + "ms");
            }

            zr.clear();
            yr.clear();
            xr.clear();
            za.clear();
            ya.clear();
            xa.clear();


            // Training updating

            Movement currentMovement = intToMove(currentMove);
            trainingTest.doAMovement(currentMovement);

            TextView nextMoveTextView = (TextView) findViewById(R.id.nextMoveTextView);

             nextMoveTextView.setText(trainingTest.getText());

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

    private Movement intToMove(int i){
        switch (i){
            case 0 :
                return Movement.WALKING;
            case 1:
                return Movement.NOTHING;
            default :
                return Movement.JUMPING;
        }
    }

}
