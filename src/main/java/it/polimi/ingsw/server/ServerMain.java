package it.polimi.ingsw.server;

import java.util.*;

/**
 * Class used to start the server on a given port
 */
public class ServerMain {
    private static final int DEFAULT_PORT = 1234;
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    private static final String PORT_ARGUMENT = "-port";
    private static final String LOG_ARGUMENT = "-log";
    private static final String HELP_ARGUMENT = "-help";
    private static boolean saveLog = false;


    public static void main(String[] args){
        int port = DEFAULT_PORT;
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        if (arguments.size() > 0) {
            if (arguments.contains(HELP_ARGUMENT)){
                String s = "This is the Server for Master Of Renaissance table game, with no input the server will start on port " + DEFAULT_PORT + "\n\n" +
                        "Here is a list of all the available commands:\n\n" +
                        "-port: followed by the desired port number that must be between " + MIN_PORT + " and " + MAX_PORT + "\n" +
                        "-log: to save the log in a file" +
                        "-help: to get help\n";
                System.out.println(s);
                return;
            }

            if (arguments.contains(LOG_ARGUMENT))
                saveLog = true;

            if (arguments.contains(PORT_ARGUMENT)){
                String portString = "";
            try {
                portString = arguments.get(arguments.indexOf(PORT_ARGUMENT) + 1);
            } catch (Exception ignored) {
            }

            boolean error = false;

            try {
                int proposedPort = Integer.parseInt(portString);
                if (proposedPort >= MIN_PORT && proposedPort <= MAX_PORT)
                    port = proposedPort;
                else
                    error = true;
            } catch (NumberFormatException ignored) {
                error = true;
            }

            if (error) {
                System.out.println("Invalid port number, insert " + HELP_ARGUMENT + " to see the available port numbers.");
                return;
            }
        }
        }
        Server server = new Server(port, saveLog);
        server.startServer();
    }
}
