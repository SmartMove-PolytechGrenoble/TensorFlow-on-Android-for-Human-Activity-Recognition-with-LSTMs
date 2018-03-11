package BusinessClass;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anthony on 11/03/18.
 */

public class Training {

    private List<Exercice> exercices;

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
    }

    public void doAMovement(Movement m){
        if(!trainingDone && currentExercice.getMovement()==m ){
            currentExercice.doARepetition();
            if(currentExercice.getRemainingRepetition()==0){
                exerciceNumber++;
                Log.e("Bite", Integer.toString(exerciceNumber));
                Log.e("Bite", Integer.toString(exercices.size()));
                if(exerciceNumber==exercices.size()){
                    trainingDone=true;
                    Log.e("BITE", "done");
                }else{
                    currentExercice=exercices.get(exerciceNumber);
                }
            }
        }
    }

    public boolean trainingDone(){
        return trainingDone;
    }


    public String getText(){
        return currentExercice.getMovement().toString() + " " + Integer.toString(currentExercice.getRemainingRepetition());
    }
}
