package messages.toServer;

import Server.ClientHandler;
import Server.Server;
import enumerations.ClientHandlerPhase;
import messages.toClient.NumberOfPlayersRequest;

import java.io.Serializable;

/**
 * Class to communicate the number of players desired
 */
public class NumberOfPlayersResponse implements Serializable, MessageToServer {
    private final int numberOfPlayers;

    public NumberOfPlayersResponse(int numberOfPlayers){
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getNumberOfPlayers(){
        return numberOfPlayers;
    }

    public boolean isLegal(){
        return (numberOfPlayers < 5 && numberOfPlayers > 1);
    }

    @Override
    public void handleMessage(Server server, ClientHandler clientHandler) {
        if (clientHandler.getClientHandlerPhase() == ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS) {
            if (isLegal()) {
                server.setNumberOfPlayersForNextGame(numberOfPlayers);
                clientHandler.setClientHandlerPhase(ClientHandlerPhase.READY_TO_START);
            } else {
                clientHandler.sendMessageToClient(new NumberOfPlayersRequest(true));
            }
            //TODO move this call in the setNumberOfPlayers of the server and test again!
            server.NewGameManager();
        }
    }
}
