package it.polimi.ingsw.client.cli.graphical;


import java.util.HashMap;
import java.util.Map;

/**
 * Enum to obtain the subscript version of a number
 */
public enum SubscriptNumbers {
    ZERO(0, "\u2080"),
    ONE(1,"\u2081"),
    TWO(2,"\u2082"),
    THREE(3,"\u2083"),
    FOUR(4,"\u2084"),
    FIVE(5,"\u2085"),
    SIX(6,"\u2086"),
    SEVEN(7,"\u2087"),
    EIGHT(8,"\u2088"),
    NINE(9,"\u2089");

    private static Map<Integer, SubscriptNumbers> map= new HashMap<>();

    static {
        for(SubscriptNumbers sn : SubscriptNumbers.values()){
            map.put(sn.value, sn);
        }
    }

    public static SubscriptNumbers valueOf(int number){
        return map.get(number);
    }

    SubscriptNumbers(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    private final int value;
    private final String code;
}
