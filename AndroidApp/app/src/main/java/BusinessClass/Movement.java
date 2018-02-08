package BusinessClass;

/**
 * Created by anthony on 08/02/18.
 */

public enum Movement {

   DOWNSTAIRS ("Downstairs"),
   JOGGING ("Jogging"),
   SITTING ("Sitting"),
   STANDING ("Standing"),
   UPSTAIRS ("Upstairs"),
   WALKING ("Walking");

   private String name;

   Movement(String s) {
      name = s;
   }

   public String toString(){
      return name;
   }
}
