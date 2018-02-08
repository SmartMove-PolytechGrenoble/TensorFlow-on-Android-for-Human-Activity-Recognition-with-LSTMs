package BusinessClass;

/**
 * Created by anthony on 08/02/18.
 */

public class Exercice {

    private Movement movement;

    private int repetition;

    public Movement getMovement() {
        return movement;
    }

    public int getRepetition() {
        return repetition;
    }


    public Exercice ( Movement movement, int repetition){
        this.repetition=repetition;
        this.movement=movement;
    }
}