package BusinessClass;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by anthony on 11/03/18.
 */

public class Training {

    private List<Exercice> exercices;

    private int totalNumberOfRepetition;

    private int numberOfRepetition;

    private String trainingName;

    private Exercice currentExercice;

    private int exerciceNumber;
    private boolean trainingDone;
    private boolean trainingStarted;
    private String textToSpeech;

    public Training() {
        this.exercices = new ArrayList<>();
        currentExercice=null;
        exerciceNumber=0;
        trainingDone=false;
        trainingStarted=false;
    }

    public void addExercice(Exercice e){
        exercices.add(e);
    }

    public List<Exercice> getExercices (){
        return  exercices;
    }

    public void setName(String name) {
        this.trainingName = name;
    }

    public String getName() {
        return trainingName;
    }

    public void lauchTraining(){
        exerciceNumber=0;
        currentExercice=exercices.get(exerciceNumber);
        trainingDone=false;
        trainingStarted=true;

        numberOfRepetition=0;
        totalNumberOfRepetition=0;

        for(int i=0; i<exercices.size(); i++){
            totalNumberOfRepetition+=exercices.get(i).getRepetition();
        }

        textToSpeech="Let's begin with " + currentExercice.getMovement().toString() + " " + currentExercice.getRepetition() + " times";
    }

    public void doAMovement(Movement m){
        if(!trainingDone && trainingStarted) {
            numberOfRepetition++;

            if (currentExercice.getMovement() == m) {
                currentExercice.doARepetition();
                if (currentExercice.getRemainingRepetition() == 0) {
                    exerciceNumber++;
                    if (exerciceNumber == exercices.size()) {
                        trainingDone = true;
                        textToSpeech="Training done, good job;";

                    } else {
                        currentExercice = exercices.get(exerciceNumber);
                        textToSpeech="Well done, now " + currentExercice.getMovement().toString() + " " + currentExercice.getRepetition() + " times";
                    }
                }
            }
        }
    }

    public boolean trainingDone(){
        return trainingDone;
    }


    public String getText(){

        if(trainingDone){

            float accuracy = (float) totalNumberOfRepetition / (float) numberOfRepetition;
            accuracy*=100;

            Log.e("Debug", "nor" + numberOfRepetition);
            Log.e("Debug", "tnor" + totalNumberOfRepetition);
            Log.e("Debug", "a" + accuracy);

            return "Training done, " + accuracy + "% of accuracy." ;
        } else {
            if(trainingStarted){
                return currentExercice.getMovement().toString() + " " + Integer.toString(currentExercice.getRemainingRepetition());
            }else{
                return "";
            }
        }

    }

    public String getTextToSpeech(){

        String tmp = textToSpeech;
        textToSpeech="";
        return tmp;
    }
}
