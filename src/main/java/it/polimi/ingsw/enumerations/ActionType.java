package it.polimi.ingsw.enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration to list all the possible actions
 */
public enum ActionType {
    TAKE_RESOURCE_FROM_MARKET(0), BUY_DEVELOPMENT_CARD(1), ACTIVATE_PRODUCTION(2), ACTIVATE_LEADER_CARD(3), DISCARD_LEADER_CARD(4);
    private int value;
    private static Map<Integer, ActionType> map= new HashMap<>();
    ActionType(int value){
        this.value=value;
    }
    static {
        for(ActionType actionType : ActionType.values()){
            map.put(actionType.value, actionType);
        }
    }
    public static ActionType valueOf(int value){
        return map.get(value);
    }

    public int getValue(){
        return value;
    }
}

