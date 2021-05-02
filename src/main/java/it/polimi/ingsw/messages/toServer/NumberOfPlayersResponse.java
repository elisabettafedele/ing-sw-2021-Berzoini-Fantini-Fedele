package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.messages.toClient.NumberOfPlayersRequest;
import it.polimi.ingsw.messages.toClient.WaitingInTheLobbyMessage;

import java.util.logging.Level;

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
            Server.SERVER_LOGGER.log(Level.INFO, "New message from "+ clientHandler.getNickname() + " that has chosen the number of players: "+ numberOfPlayers);
            if (isLegal()) {
                server.setNumberOfPlayersForNextGame(numberOfPlayers);
                clientHandler.sendMessageToClient(new WaitingInTheLobbyMessage());
                clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_IN_THE_LOBBY);
            } else {
                clientHandler.sendMessageToClient(new NumberOfPlayersRequest(true));
            }
        }
    }
}
