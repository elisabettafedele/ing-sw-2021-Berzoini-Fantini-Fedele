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
    public static Level valueOf(int level){
        return map.get(level);
    }

    public int getValue(){
        return value;
    }
}
