package it.polimi.ingsw.client.cli.specificCLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.utilities.Utils;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.messages.toServer.lobby.GameModeResponse;
import it.polimi.ingsw.messages.toServer.lobby.NicknameResponse;
import it.polimi.ingsw.messages.toServer.lobby.NumberOfPlayersResponse;

import java.util.List;

public class LobbyCLI {
    private static final String DEFAULT_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 1234;

    public static Client askConnectionParameters(CLI cli){
        int port;
        String IPAddress;
        boolean firstTry = true;
        //Insert IP address
        do{
            if(firstTry)
                System.out.println("Enter the server's IP address or d (default configuration): ");
            else
                System.out.println("Invalid IP address: enter x.x.x.x where x is called an octet and must be a decimal value between 0 and 255. Enter d for default configuration: ");
            IPAddress = InputParser.getLine();
            firstTry = false;
            if (IPAddress.toLowerCase().equals("d")){
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


    public static void displayNicknameRequest(Client client, boolean isRetry, boolean alreadyTaken) {
        if (!isRetry)
            System.out.println("Insert your nickname");
        else if (!alreadyTaken)
            System.out.println("Your nickname was invalid, be sure to insert only valid characters (A-Z, a-z, 0-9)");
        else {
            System.out.println("Your nickname has already been taken, insert another one");
        }
        client.sendMessageToServer(new NicknameResponse(InputParser.getLine()));
    }

    public static void displayGameModeRequest(Client client) {
        System.out.println("Insert a game mode, multiplayer or solo mode: m | s");
        client.sendMessageToServer(new GameModeResponse(getGameMode()));
    }

    private static GameMode getGameMode(){
        while(true) {
            String gameModeString = InputParser.getLine();
            if (gameModeString.equals("m"))
                return GameMode.MULTI_PLAYER;
            else if (gameModeString.equals("s"))
                return GameMode.SINGLE_PLAYER;
            else {
                System.out.println("Invalid game mode: type m for multiplayer mode or s for solo mode");
            }
        }
    }

    public static void displayNumberOfPlayersRequest(Client client){
        System.out.println("Insert desired number of players: 2, 3 or 4");
        client.sendMessageToServer(new NumberOfPlayersResponse(InputParser.getInt("Invalid number of players: please insert an integer number between 2 and 4", CLI.conditionOnIntegerRange(2, 4))));
    }

    public static void displayWaitingInTheLobbyMessage() {
        System.out.println("Waiting in the lobby...");
    }

    public static void displayPlayersReadyToStartMessage(List<String> nicknames) {
        System.out.println("All the players are ready to start, players in game are:");
        for (String nickname : nicknames)
            System.out.println("- " + nickname);
    }
}
