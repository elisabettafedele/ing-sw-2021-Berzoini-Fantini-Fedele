package it.polimi.ingsw.enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration representing all the possible resources available in the game
 */
public enum Resource {
    COIN(0),STONE(1),SERVANT(2),SHIELD(3),ANY(4);
    private int value;
    private static Map<Integer, Resource> map= new HashMap<>();
    Resource(int value){
        this.value=value;
    }
    static {
        for(Resource resource : Resource.values()){
            map.put(resource.value, resource);
        }
    }
    public static Resource valueOf(int resource){
        return map.get(resource);
    }

    public int getValue(){
        return value;
    }
}
