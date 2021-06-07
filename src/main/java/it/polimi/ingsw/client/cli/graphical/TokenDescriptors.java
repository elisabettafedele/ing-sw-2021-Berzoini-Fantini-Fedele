package it.polimi.ingsw.client.cli.graphical;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerations to describes the tokens used by Lorenzo il Magnifico during single player matches
 */
public enum TokenDescriptors {
    TOKEN_65(65, "Lorenzo has discarded two blue development cards\n"),
    TOKEN_66(66, "Lorenzo has discarded two green development cards\n"),
    TOKEN_67(67, "Lorenzo has discarded two purple development cards\n"),
    TOKEN_68(68, "Lorenzo has discarded two yellow development cards\n"),
    TOKEN_69(69, "Lorenzo has made two steps on the faith track\n"),
    TOKEN_70(70, "Lorenzo has made two steps on the faith track\n"),
    TOKEN_71(71, "Lorenzo has made one step on the faith track\n" +
            "and has shuffled the solo action token stack\n");

    private int value;
    private String description;

    private static Map<Integer, TokenDescriptors> map = new HashMap<>();

    static {
        for(TokenDescriptors tc : TokenDescriptors.values()){
            map.put(tc.value, tc);
        }
    }

    public static TokenDescriptors valueOf(int number){
        return map.get(number);
    }

    public String getDescription(){
        return description;
    }

    TokenDescriptors(int value, String description) {
        this.value = value;
        this.description = description;
    }
}
