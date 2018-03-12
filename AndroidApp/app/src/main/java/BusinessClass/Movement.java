package BusinessClass;

/**
 * Created by anthony on 11/03/18.
 */

public enum Movement {

    NOTHING ("Nothing"),
    WALKING ("Walking"),
    JUMPING ("Jumping");

    private String name;

    Movement(String s) {
        name = s;
    }

    static  public Movement stringToMovement(String s){
        Movement[] movs = Movement.class.getEnumConstants();
        for (Movement m: movs) {
            if(m.name.equals(s)){
                return m;
            }
        }

        // TODO add error
        return NOTHING;
    }

    public String toString(){
        return name;
    }
}
