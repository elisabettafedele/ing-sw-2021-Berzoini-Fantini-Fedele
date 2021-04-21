package it.polimi.ingsw.enumerations;

import java.util.HashMap;
import java.util.Map;

public enum ResourceStorageType {
    WAREHOUSE_FIRST_DEPOT(0), WAREHOUSE_SECOND_DEPOT(1), WAREHOUSE_THIRD_DEPOT(2), WAREHOUSE(3), STRONGBOX(4), LEADER_DEPOT(5);
    private int value;
    private static Map<Integer, ResourceStorageType> map= new HashMap<>();
    ResourceStorageType(int value){
        this.value=value;
    }
    static {
        for(ResourceStorageType resourceStorageType : ResourceStorageType.values()){
            map.put(resourceStorageType.value, resourceStorageType);
        }
    }
    public static ResourceStorageType valueOf(int resourceStorageType){
        return map.get(resourceStorageType);
    }

    public int getValue(){
        return value;
    }
}
