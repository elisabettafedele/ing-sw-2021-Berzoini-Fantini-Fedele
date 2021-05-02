package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.messages.toClient.NumberOfPlayersRequest;
import it.polimi.ingsw.messages.toClient.WaitingInTheLobbyMessage;

/**
 * Class to communicate the number of players desired
 */
public class NumberOfPlayersResponse implements MessageToServer {
    private final int numberOfPlayers;

    public NumberOfPlayersResponse(int numberOfPlayers){
        this.numberOfPlayers = numberOfPlayers;
    }

    public boolean isLegal(){
        return (numberOfPlayers < 5 && numberOfPlayers > 1);
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        if (clientHandler.getClientHandlerPhase() == ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS) {
            // TODO insert the port of the client in the log message
            if (isLegal()) {
                clientHandler.setNumberOfPlayersForNextGame(numberOfPlayers);
                clientHandler.sendMessageToClient(new WaitingInTheLobbyMessage());
                clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_IN_THE_LOBBY);
            } else {
                clientHandler.sendMessageToClient(new NumberOfPlayersRequest(true));
            }
        }
    }
}
