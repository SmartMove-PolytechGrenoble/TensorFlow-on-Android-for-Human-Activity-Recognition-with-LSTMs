package BusinessClass;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.github.introml.activityrecognition.CreateTrainingActivity;
import io.github.introml.activityrecognition.HomeActivity;
import io.github.introml.activityrecognition.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by anthony on 08/02/18.
 */

public class Training {

    private List<Exercice> exercices;

    private String trainingName;

    public Training() {
        this.exercices = new ArrayList<>();
    }

    public void addExercice(Exercice e){
        exercices.add(e);
    }

    public List<Exercice> getExercices (){
        return  exercices;
    }

    public String getGSON (String trainingName){
        this.trainingName=trainingName;

        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Training (String trainingGSon){

        Gson gson = new Gson();
        Training t  = gson.fromJson(trainingGSon, Training.class);
        exercices=t.exercices;
        trainingName=t.trainingName;
    }

    public void setName(String name) {
        this.trainingName = name;
    }

    public String getName() {
        return trainingName;
    }
}
