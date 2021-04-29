package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.messages.toClient.NumberOfPlayersRequest;

import java.io.Serializable;

/**
 * Class to communicate the number of players desired
 */
public class NumberOfPlayersResponse implements MessageToServer {
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
