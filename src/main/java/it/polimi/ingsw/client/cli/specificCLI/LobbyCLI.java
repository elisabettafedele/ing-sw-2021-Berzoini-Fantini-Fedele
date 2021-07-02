package it.polimi.ingsw.client.cli.specificCLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.cli.graphical.Colour;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.client.utilities.Utils;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.messages.toServer.lobby.GameModeResponse;
import it.polimi.ingsw.messages.toServer.lobby.NicknameResponse;
import it.polimi.ingsw.messages.toServer.lobby.NumberOfPlayersResponse;

import java.io.IOException;
import java.util.List;

/**
 * Class to manage the interaction with the player before the beginning of the match
 */
public class LobbyCLI {
    private static final String DEFAULT_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 1234;

    /**
     * Method to aks to which IP address and port the client wants to connect to
     * @param cli the view used by the client
     * @param firstTryConnection true if this method it's called for the first time
     * @return a new {@link Client} instance
     */
    public static Client askConnectionParameters(CLI cli, boolean firstTryConnection){
        int port;
        String IPAddress;
        boolean firstTry = true;
        //Insert IP address
        do{
            if (!firstTryConnection && firstTry){
                System.out.println(Colour.ANSI_BRIGHT_CYAN.getCode() + "There is not an active server at the IP address and port provided. Try again." + Colour.ANSI_RESET);
            }
            if(firstTry)
                System.out.println("Enter the server's IP address or d (default configuration): ");
            else if (!firstTry)
                System.out.println("Invalid IP address: enter x.x.x.x where x is called an octet and must be a decimal value between 0 and 255. Enter d for default configuration: ");
            IPAddress = InputParser.getLine();
            firstTry = false;
            if (IPAddress.equalsIgnoreCase("d")){
                IPAddress = DEFAULT_ADDRESS;
                port = DEFAULT_PORT;
                return new Client(IPAddress, port, cli);
            }
        }while(!Utils.IPAddressIsValid(IPAddress));
        firstTry = true;
        //Insert port number
        do{
            if(firstTry)
                System.out.println("Enter the port you want to connect to: ");
            else
                System.out.println("Invalid port number: enter an integer between 1024 and 65535");
            firstTry = false;
            port = InputParser.getInt("Invalid port number: enter an integer between 1024 and 65535", CLI.conditionOnIntegerRange(1024, 65535));
        }while (!Utils.portIsValid(port));
        return new Client(IPAddress, port, cli);
    }

    /**
     * Asks the nickname to the player
     * @param client {@link Client} with the connection to the server
     * @param isRetry true if it is not the first time that the nickname is asked
     * @param alreadyTaken true if the previous nickname was not valid because equals to someone else's nickname
     */
    public static void displayNicknameRequest(Client client, boolean isRetry, boolean alreadyTaken) {
        if (client.isNicknameValid() && client.getNickname().isPresent()){
            client.sendMessageToServer(new NicknameResponse(client.getNickname().get()));
            return;
        }

        if (!isRetry)
            System.out.println("Insert your nickname");
        else if (!alreadyTaken)
            System.out.println("Your nickname was invalid, be sure to insert only valid characters (A-Z, a-z, 0-9)");
        else {
            System.out.println("Your nickname has already been taken, insert another one");
        }
        String selection = InputParser.getLine();
        if (selection == null)
            return;
        client.setNickname(selection);
        client.sendMessageToServer(new NicknameResponse(selection));
    }

    /**
     * Method to ask the connection parameters (IP and port) to the player
     * @param client {@link Client} with the connection to the server
     */
    public static void displayGameModeRequest(Client client) {
        if (client.getGameMode().isPresent()){
            client.sendMessageToServer(new GameModeResponse(client.getGameMode().get()));
            return;
        }
        System.out.println(Colour.ANSI_BRIGHT_GREEN.getCode() + "\nConnection established!" + Colour.ANSI_RESET);
        System.out.println("\nInsert a game mode, multiplayer or solo mode: m | s");
        GameMode gameMode = null;
        try {
            gameMode = getGameMode(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gameMode == null)
            return;
        client.setGameMode(gameMode);
        client.sendMessageToServer(new GameModeResponse(gameMode));
    }

    /**
     * Get the gamemode chosen by the client
     * @param client {@link Client} with the connection to the server
     * @return a {@link GameMode} (single or multi player
     * @throws IOException if the chosen gamemode is null
     */
    private static GameMode getGameMode(Client client) throws IOException {
        while(true) {
            String gameModeString = InputParser.getLine();
            if (gameModeString == null)
                throw new IOException();
            if (gameModeString.equals("m"))
                return GameMode.MULTI_PLAYER;
            else if (gameModeString.equals("s"))
                return GameMode.SINGLE_PLAYER;
            else {
                System.out.println("Invalid game mode: type m for multiplayer mode or s for solo mode");
            }
        }
    }

    /**
     * Method to ask the number of player for the next game
     * @param client {@link Client} with the connection to the server
     */
    public static void displayNumberOfPlayersRequest(Client client){
        System.out.println("Insert desired number of players: 2, 3 or 4");
        Integer choice = InputParser.getInt("Invalid number of players: please insert an integer number between 2 and 4", CLI.conditionOnIntegerRange(2, 4));
        if (choice != null)
            client.sendMessageToServer(new NumberOfPlayersResponse(choice));
    }

    public static void displayWaitingInTheLobbyMessage() {
        System.out.println("Waiting in the lobby...");
    }

    public static void displayPlayersReadyToStartMessage(List<String> nicknames) {
        //SINGLE PLAYER
        if (nicknames.size() == 1)
            return;
        //MULTIPLAYER
        System.out.println("All the players are ready to start, players in game are:");
        for (String nickname : nicknames)
            System.out.println("- " + nickname);
    }
}
