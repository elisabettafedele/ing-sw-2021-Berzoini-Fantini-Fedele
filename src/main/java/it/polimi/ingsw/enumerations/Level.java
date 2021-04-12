package it.polimi.ingsw.enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration representing all the possible levels of the Leader Cards and an extra value, ANY, used when the level is not specified
 */
public enum Level {
    ONE(0), TWO(1), THREE(2), ANY(3);

    private int value;
    private static Map<Integer, Level> map= new HashMap<>();

    Level(int value){
        this.value=value;
    }

    static {
        for(Level level : Level.values()){
            map.put(level.value, level);
        }
    }

    /**
     * Get the Level corresponding to an int
     * @param level the integer to be converted to a Level
     * @return the Level corresponding to an int
     */
    public static Level valueOf(int level){
        return map.get(level);
    }

    /**
     * Get the int value of a Level
     * @return the int value of a Level
     */
    public int getValue(){
        return value;
    }
}
