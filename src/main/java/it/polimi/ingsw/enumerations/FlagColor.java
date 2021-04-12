package it.polimi.ingsw.enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration representing all the possible flag colours available in the game
 */
public enum FlagColor {
    GREEN(0), BLUE(1), YELLOW(2), PURPLE(3);

    private int value;
    private static Map<Integer, FlagColor> map = new HashMap<>();

    FlagColor(int value){
        this.value = value;
    }
    
    static{
        for(FlagColor color : FlagColor.values()){
            map.put(color.value, color);
        }
    }

    /**
     * Get the FlagColor corresponding to int
     * @param value the integer to be converted to a FlagColor
     * @return the FlagColor corresponding to int
     */
    public static FlagColor valueOf(int value){
        return map.get(value);
    }

    /**
     * Get the int value of a FlagColor
     * @return the int value of a FlagColor
     */
    public int getValue(){
        return value;
    }
}
