package BusinessClass;

/**
 * Created by anthony on 11/03/18.
 */

public enum Movement {

    NOTHING ("Nothing"),
    WALKING ("Walking"),
    JUMPING ("Jumping"),
    THREESIX ("360");

    private String name;

    Movement(String s) {
        name = s;
    }

    public String toString(){
        return name;
    }

    public String getName(){
        return name;
    }
}
