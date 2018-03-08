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
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private static final int N_SAMPLES = 100;
    private static List<Float> xa;
    private static List<Float> ya;
    private static List<Float> za;
    //private static List<Float> xr;
    //private static List<Float> yr;
    //private static List<Float> zr;

    private TextView headbangTextView;
    private TextView rienTextView;
    private TextView deboutTextView;
    private TextToSpeech textToSpeech;
    private float[] results;
    private TensorFlowClassifier classifier;

    private String[] labels = {"Marcher", "Rien", "Sauter"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xa = new ArrayList<>();
        ya = new ArrayList<>();
        za = new ArrayList<>();
        //xr = new ArrayList<>();
        //yr = new ArrayList<>();
        //zr = new ArrayList<>();

        rienTextView = (TextView) findViewById(R.id.rien_prob);
        deboutTextView = (TextView) findViewById(R.id.debout_prob);
        headbangTextView = (TextView) findViewById(R.id.headbang_prob);

        classifier = new TensorFlowClassifier(getApplicationContext());

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
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

                textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
            }
        }, 2000, 5000);
    }

    protected void onPause() {
        getSensorManager().unregisterListener(this);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), 10000);
        //getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GRAVITY), 10000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        activityPrediction();

        if(event.sensor.getType() != 9) {
            if(xa.size() < N_SAMPLES) {
                xa.add(event.values[0]);
                ya.add(event.values[1]);
                za.add(event.values[2]);
            }
        }
        else{
            /*if(xr.size() < N_SAMPLES) {
                xr.add(event.values[0]);
                yr.add(event.values[1]);
                zr.add(event.values[2]);
            }*/
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void activityPrediction() {
        if (xa.size() >= N_SAMPLES && ya.size() >= N_SAMPLES && za.size() >= N_SAMPLES) {
            List<Float> data = new ArrayList<>();
            data.addAll(xa);
            data.addAll(ya);
            data.addAll(za);
//            data.addAll(xr);
//            data.addAll(yr);
//            data.addAll(zr);

            results = classifier.predictProbabilities(toFloatArray(data));

            headbangTextView.setText(Float.toString(round(results[0], 2)));
            deboutTextView.setText(Float.toString(round(results[2], 2)));
            rienTextView.setText(Float.toString(round(results[1], 2)));

            xa.clear();
            ya.clear();
            za.clear();
//            xr.clear();
//            yr.clear();
//            zr.clear();
        }
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

}
