package it.polimi.ingsw.server;

import it.polimi.ingsw.model.persistency.GameHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ServerMain {
    private static final int DEFAULT_PORT = 1234;

    public static void main(String[] args){
        int port = DEFAULT_PORT;
        List<String> arguments = new ArrayList<String>(Arrays.asList(args));
        Scanner in = new Scanner(System.in);
        Server server = new Server(port);
        String answer;
        do {
            System.out.println("Do you want to save games in json files? y | n");
            answer = in.nextLine();
        } while (!(answer.equals("y") || answer.equals("n")));
        GameHistory.saveGames = answer.equals("y");
        server.startServer();
    }
}
