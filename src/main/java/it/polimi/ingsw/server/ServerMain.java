package it.polimi.ingsw.server;

import it.polimi.ingsw.model.persistency.GameHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ServerMain {
    private static final int DEFAULT_PORT = 1234;
    private static final String JSON_ARGUMENT= "-json";

    public static void main(String[] args){
        int port = DEFAULT_PORT;
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        GameHistory.saveGames = (arguments.size() > 1 && arguments.get(0).equals(JSON_ARGUMENT));
        Server server = new Server(port);
        server.startServer();
    }
}
