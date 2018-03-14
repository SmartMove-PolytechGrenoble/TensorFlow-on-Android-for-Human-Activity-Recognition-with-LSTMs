package BusinessClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anthony on 11/03/18.
 */

public enum Movement {
    SQUAT ("Squat"),
    JOGGING ("Jogging"),
    NOTHING ("Nothing"),
    WALKING ("Walking"),
    JUMPING ("Jumping"),
    PASC ("PASC"),
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

    public static List<Movement> getMovements(){
        List<Movement> l = new ArrayList<>();
        l.add(SQUAT);
        l.add(JOGGING);
        l.add(NOTHING);
        l.add(WALKING);
        l.add(JUMPING);
        l.add(THREESIX);
        l.add(PASC);

        return  l;
    }
}
