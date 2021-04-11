package it.polimi.ingsw.enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration representing all the possible colours of the marbles available in the Market
 */
public enum Marble {
    GREY(0), PURPLE(1), RED(2), BLUE(3), YELLOW(4), WHITE(5);

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
    public static Marble valueOf(int marble){
        return map.get(marble);
    }

    public int getValue(){
        return value;
    }



}
