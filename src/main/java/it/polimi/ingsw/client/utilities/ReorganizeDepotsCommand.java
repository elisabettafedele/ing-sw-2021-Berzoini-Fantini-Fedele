package it.polimi.ingsw.client.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration to store the command used in the depot reorganization
 */
public enum ReorganizeDepotsCommand {
    DISCARD("d"), REORGANIZE("r"), SWAP("swap"), MOVE("move"), END_REORGANIZE_DEPOTS("end");

    public final String command;

    private ReorganizeDepotsCommand(String command){
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
