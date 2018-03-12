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

    public Training() {
        this.exercices = new ArrayList<>();
        currentExercice=null;
        exerciceNumber=0;
        trainingDone=false;
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

        numberOfRepetition=0;
        totalNumberOfRepetition=0;

        for(int i=0; i<exercices.size(); i++){
            totalNumberOfRepetition+=exercices.get(i).getRepetition();
        }
    }

    public void doAMovement(Movement m){
        if(!trainingDone) {
            numberOfRepetition++;

            if (currentExercice.getMovement() == m) {
                currentExercice.doARepetition();
                if (currentExercice.getRemainingRepetition() == 0) {
                    exerciceNumber++;
                    if (exerciceNumber == exercices.size()) {
                        trainingDone = true;

                    } else {
                        currentExercice = exercices.get(exerciceNumber);
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

            return "Training done " + accuracy + "% of accuracy." ;
        } else {
            return currentExercice.getMovement().toString() + " " + Integer.toString(currentExercice.getRemainingRepetition());
        }

    }
}
