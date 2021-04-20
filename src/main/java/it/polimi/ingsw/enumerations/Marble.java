package it.polimi.ingsw.enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration representing all the possible colours of the marbles available in the Market
 */
public enum Marble {
    GREY(1), PURPLE(2), RED(4), BLUE(3), YELLOW(0), WHITE(5);

    private int value;
    private static Map<Integer, Marble> map = new HashMap<>();

    Marble(int value){
        this.value = value;
    }

    static{
        for(Marble marble : Marble.values()){
            map.put(marble.value, marble);
        }
    }

    /**
     * Get the resource corresponding to an int
     * @param marble the integer to be converted to a Resource
     * @return the resource corresponding to an int
     */
    public static Marble valueOf(int marble){
        return map.get(marble);
    }

    /**
     * Get the int value of a Resource
     * @return the int value of a Resource
     */
    public int getValue(){
        return value;
    }



}
