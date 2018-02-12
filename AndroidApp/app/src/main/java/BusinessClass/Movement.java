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

   static  public Movement stringToMovement(String s){
      Movement[] movs = Movement.class.getEnumConstants();
      for (Movement m: movs) {
         if(m.name.equals(s)){
            return m;
         }
      }

      // TODO add error
      return SITTING;
   }

   public String toString(){
      return name;
   }
}
