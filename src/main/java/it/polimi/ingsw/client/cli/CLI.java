package client.cli;


import client.Client;
import client.View;
import client.utilities.InputParser;
import enumerations.GameMode;

import java.util.regex.Pattern;

public class CLI implements View {

    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final String IP_REGEXP = "^(" + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + ")$";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

    private static final String DEFAULT_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 1234;

    private String IPAddress;
    private int port;

    public static void main(String[] args) {
        CLI cli= new CLI();
        cli.init();
    }

    private void init(){
        askConnectionParameters();
        Client client = new Client(IPAddress, port, this);
        client.start();
    }

    private void askConnectionParameters(){
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
                this.IPAddress = DEFAULT_ADDRESS;
                this.port = DEFAULT_PORT;
                return;
            }
        }while(!IPAddressIsValid(IPAddress));
        firstTry = true;
        this.IPAddress = IPAddress;
        //Insert port number
        do{
            if(firstTry)
                System.out.println("Enter the port you want to connect to: ");
            else
                System.out.println("Invalid port number: enter an integer between 1024 and 65535");
            firstTry = false;
            port = InputParser.getInt("Invalid port number: enter an integer between 1024 and 65535");
        }while (!portIsValid(port));
        this.port = port;
    }

    private boolean IPAddressIsValid(String IP){
        return IP != null && IP_PATTERN.matcher(IP).matches();
    }

    private boolean portIsValid(int port){
        return port >= 1024 && port <= 65535;
    }

    @Override
    public void displayNicknameRequest(boolean isRetry, boolean alreadyTaken) {
        if (!isRetry)
            System.out.println("Insert your nickname");
        else if (!alreadyTaken)
            System.out.println("Your nickname was invalid, be sure to insert only valid characters (A-Z, a-z, 0-9)");
        else {
            System.out.println("Your nickname has already been taken, insert another one");
        }
    }

    @Override
    public void displayGameModeRequest() {
        System.out.println("Insert a game mode, multiplayer or solo mode: m | s");
    }

    @Override
    public void displayNumberOfPlayersRequest(boolean isRetry) {
        if (isRetry)
            System.out.println("Invalid number of players: games can be of 2, 3 or 4 players");
        else {
            System.out.println("Insert desired number of players: 2, 3 or 4");
        }
    }

    @Override
    public void displayWaitingInTheLobbyMessage() {
        System.out.println("Waiting in the lobby...");
    }

    @Override
    public void displayPlayersReadyToStartMessage() {
        System.out.println("All the players are ready to start, the game will start in a while...");
    }


    @Override
    public GameMode getGameMode(){
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

    @Override
    public String getNickname() {
        return InputParser.getLine();
    }


}
