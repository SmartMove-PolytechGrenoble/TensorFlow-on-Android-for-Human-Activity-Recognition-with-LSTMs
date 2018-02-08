package BusinessClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anthony on 08/02/18.
 */

public class Training {

    private List<Exercice> exercices;

    public Training() {
        this.exercices = new ArrayList<>();
    }

    public void addExercice(Exercice e){
        exercices.add(e);
    }

    public List<Exercice> getExercices (){
        return  exercices;
    }

    public void saveTraining(String filename){
        // TODO Save the training on a file or anywhere with the name filename
    }

    public void loadTraining (String filename){
        // TODO load the training from the file with the name filename
    }
}
