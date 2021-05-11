package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.enumerations.Marble;

public enum Colours {
    ANSI_BLACK("\u001B[30m"),
    ANSI_RED  ("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE ("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_CYAN ("\u001B[36m"),
    ANSI_WHITE("\u001B[37m"),
    ANSI_BRIGHT_BLACK("\u001B[90m"),
    ANSI_BRIGHT_RED  ("\u001B[91m"),
    ANSI_BRIGHT_GREEN("\u001B[92m"),
    ANSI_BRIGHT_YELLOW("\u001B[93m"),
    ANSI_BRIGHT_BLUE ("\u001B[94m"),
    ANSI_BRIGHT_PURPLE("\u001B[95m"),
    ANSI_BRIGHT_CYAN ("\u001B[96m"),
    ANSI_BRIGHT_WHITE("\u001B[97m")
    ,ANSI_RESET("\u001B[0m");


    private final String code;

    Colours(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public static String getMarbleColour(Marble marble){
        if (marble == Marble.YELLOW)
            return ANSI_YELLOW.code;
        if (marble == Marble.PURPLE)
            return ANSI_PURPLE.code;
        if (marble == Marble.BLUE)
            return ANSI_BLUE.code;
        if (marble == Marble.RED)
            return ANSI_RED.code;
        else
            return ANSI_WHITE.code;
    }
}
