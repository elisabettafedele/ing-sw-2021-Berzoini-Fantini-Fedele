package it.polimi.ingsw.client.cli;


import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.messages.toServer.*;
import it.polimi.ingsw.model.cards.LeaderCard;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class CLI implements View {

    private static final String DEFAULT_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 1234;

    private String IPAddress;
    private int port;

    private Client client;

    public static void main(String[] args) {
        CLI cli= new CLI();
        cli.init();
    }

    private void init(){
        askConnectionParameters();
        client = new Client(IPAddress, port, this);
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
        }while(!Utils.IPAddressIsValid(IPAddress));
        firstTry = true;
        this.IPAddress = IPAddress;
        //Insert port number
        do{
            if(firstTry)
                System.out.println("Enter the port you want to connect to: ");
            else
                System.out.println("Invalid port number: enter an integer between 1024 and 65535");
            firstTry = false;
            port = InputParser.getInt("Invalid port number: enter an integer between 1024 and 65535", conditionOnIntegerRange(1024, 65535));
        }while (!Utils.portIsValid(port));
        this.port = port;
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
        client.sendMessageToServer(new NicknameResponse(InputParser.getLine()));
    }

    @Override
    public void displayGameModeRequest() {
        System.out.println("Insert a game mode, multiplayer or solo mode: m | s");
        client.sendMessageToServer(new GameModeResponse(getGameMode()));
    }

    @Override
    public void displayNumberOfPlayersRequest(boolean isRetry) {
        if (isRetry)
            System.out.println("Invalid number of players: games can be of 2, 3 or 4 players");
        else {
            System.out.println("Insert desired number of players: 2, 3 or 4");
        }
        client.sendMessageToServer(new NumberOfPlayersResponse(InputParser.getInt("Invalid number of players: please insert an integer number between 2 and 4", conditionOnIntegerRange(2, 4))));
    }

    @Override
    public void displayWaitingInTheLobbyMessage() {
        System.out.println("Waiting in the lobby...");
    }

    @Override
    public void displayPlayersReadyToStartMessage(List<String> nicknames) {
        //TODO print nicknames
        System.out.println("All the players are ready to start, the game will start in a while...");
    }

    @Override
    public void displayTimeoutExpiredMessage() {
        System.out.println("Timeout expired");
        //boolean reconnect = getBoolean("Timeout expired, do you want to reconnect? y | n");
        client.closeSocket();
       // if (reconnect)
           // client.start();
    }

    @Override
    public void displayMarbleInsertionPositionRequest(Action action) {
        System.out.println("Insert a marble insertion position (from 1 to 8) to insert the marble in the market trace: ");
        client.sendMessageToServer(new MarbleInsertionPositionResponse(action, InputParser.getInt("Invalid position: the position must be an integer from 1 to 8", conditionOnIntegerRange(1, 8))));
    }

    @Override
    public void displayChooseWhiteMarbleConversionRequest(List<Marble> marbles, int numberOfMarbles) {
        System.out.println("You have these two possible white marble conversions: " + marbles.get(0) + " | " + marbles.get(1));
        client.sendMessageToServer(new ChooseWhiteMarbleConversionResponse(getMarbleColor(marbles)));
    }

    @Override
    public void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion) {
        System.out.println("These are the colors of the marbles you took from the market:");
        for (int i = 0; i < marblesTaken.size() - 1; i++)
            System.out.println(marblesTaken.get(i) + ", ");
        System.out.println(marblesTaken.get(marblesTaken.size()-1));
        if (marblesTaken.contains(Marble.WHITE)){
            if (!needToChooseConversion)
                System.out.println("You have one White Marble Effect Active! White marbles will be automatically converted according to this effect");
            else
                System.out.println("You have more than one White Marble Effect Active! You need to choose one white marble at a time how you want to convert it");
        }
    }

    private GameMode getGameMode(){
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

    private boolean getBoolean(String message){
        String reconnectString;
        do {
            System.out.println(message);
            reconnectString = InputParser.getLine();
        } while (!reconnectString.equalsIgnoreCase("y")  && !reconnectString.equalsIgnoreCase("n"));
        return reconnectString.equalsIgnoreCase("y");

    }

    private Marble getMarbleColor(List<Marble> conversions){
        Marble marble = null;
        String marbleString;
        boolean error = false;
        do {
            System.out.println("Select one conversion: ");
            marbleString = InputParser.getLine();
            try {
                marble = Marble.valueOf(marbleString.toUpperCase());
                error = false;
                if (!conversions.contains(marble))
                    error = true;
            }catch (IllegalArgumentException e){
                error = true;
                System.out.println("Error: insert a valid marble color");
            }
        } while (error);
        return marble;
    }

    @Override
    public void displayChooseLeaderCardsRequest(List<Integer> leaderCardsIDs) {
        System.out.println("Choose two Leader Cards to keep");
        for (Integer id : leaderCardsIDs){
            System.out.printf("%d. %s \n", id, MatchData.getInstance().getLeaderCardByID(id));
        }
        System.out.print("Insert the ID of the first leader card chosen: ");
        Integer firstChoice = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", conditionOnInteger(leaderCardsIDs));
        leaderCardsIDs.remove(firstChoice);
        MatchData.getInstance().addChosenLeaderCard(firstChoice);
        System.out.print("Insert the ID of the second leader card chosen: ");
        Integer secondChoice = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", conditionOnInteger(leaderCardsIDs));
        MatchData.getInstance().addChosenLeaderCard(secondChoice);
        leaderCardsIDs.remove(secondChoice);
        client.sendMessageToServer(new ChooseLeaderCardsResponse(leaderCardsIDs));
    }

    @Override
    public void displaySelectLeaderCardRequest(List<Integer> leaderCardsIDs) {
        System.out.println("Select a Leader Card");
        for (Integer id : leaderCardsIDs){
            System.out.printf("%d. %s \n", id, MatchData.getInstance().getLeaderCardByID(id));
        }
        System.out.print("Insert the ID of the Leader Card you want to select: ");
        Integer selection = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", conditionOnInteger(leaderCardsIDs));
        client.sendMessageToServer( new SelectLeaderCardResponse(selection));
    }

    @Override
    public void loadLeaderCards(List<LeaderCard> leaderCards){
        MatchData.getInstance().setAllLeaderCards(leaderCards);
    }

    @Override
    public void displayChooseResourceTypeRequest(List<String> resourceTypes, int quantity) {
    }

    public void loadDevelopmentCards(Map<Integer, List<String>> lightDevelopmentCards) {
        MatchData.getInstance().setAllDevelopmentCards(lightDevelopmentCards);
    }

    public static Predicate<Integer> conditionOnIntegerRange(int min, int max){
        return p -> p >= min && p <= max;
    }

    public static Predicate<Integer> conditionOnInteger(List<Integer> list){
        return p -> list.contains(p);
    }
}
