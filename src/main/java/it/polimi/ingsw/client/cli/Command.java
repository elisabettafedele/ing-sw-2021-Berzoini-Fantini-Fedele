package it.polimi.ingsw.client.cli;

import java.util.ArrayList;
import java.util.List;

public enum Command {
    DISCARD("d"), REORGANIZE("r"), SWAP("swap"), MOVE("move"), END_REORGANIZE_DEPOTS("end");

    public final String command;

    private Command(String command){
        this.command = command;
    }

    public static List<String> getReorganizeDepotsCommands(){
        List<String> commands = new ArrayList<>();
        commands.add(SWAP.command);
        commands.add(MOVE.command);
        commands.add(END_REORGANIZE_DEPOTS.command);
        return commands;
    }
}
