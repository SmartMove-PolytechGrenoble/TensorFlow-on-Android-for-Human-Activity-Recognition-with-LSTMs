package BusinessClass;

/**
 * Created by anthony on 11/03/18.
 */

public class Exercice {

    private Movement movement;

    private int repetition;

    private int remainingRepetition;

    public Movement getMovement() {
        return movement;
    }

    public int getRepetition() {
        return repetition;
    }

    public int getRemainingRepetition() {
        return remainingRepetition;
    }


    public Exercice ( Movement movement, int repetition){
        this.repetition=repetition;
        this.movement=movement;
        this.remainingRepetition=repetition;
    }

    public void doARepetition(){
        remainingRepetition--;
    }
}
