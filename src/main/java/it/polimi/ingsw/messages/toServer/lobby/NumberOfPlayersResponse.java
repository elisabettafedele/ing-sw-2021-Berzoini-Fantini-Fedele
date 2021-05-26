package it.polimi.ingsw.messages.toServer.lobby;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.messages.toClient.lobby.WaitingInTheLobbyMessage;
import it.polimi.ingsw.messages.toServer.MessageToServer;

/**
 * Class to communicate the number of players desired
 */
public class NumberOfPlayersResponse implements MessageToServer {
    private final int numberOfPlayers;

    public NumberOfPlayersResponse(int numberOfPlayers){
        this.numberOfPlayers = numberOfPlayers;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        if (clientHandler.getClientHandlerPhase() == ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS) {
            clientHandler.sendMessageToClient(new WaitingInTheLobbyMessage());
            //TODO message matchmaking
            clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_IN_THE_LOBBY);
            clientHandler.setNumberOfPlayersForNextGame(numberOfPlayers);
        }
    }
}
