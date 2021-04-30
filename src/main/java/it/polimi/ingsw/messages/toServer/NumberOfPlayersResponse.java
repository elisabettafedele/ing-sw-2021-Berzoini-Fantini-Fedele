package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.messages.toClient.NumberOfPlayersRequest;

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
    public void handleMessage(Server server, ClientHandler clientHandler) {
        if (clientHandler.getClientHandlerPhase() == ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS) {
            // TODO insert the port of the client in the log message
            Server.SERVER_LOGGER.log(Level.INFO, "New message from "+ clientHandler.getNickname() + " that has chosen the number of players: "+ numberOfPlayers);
            if (isLegal()) {
                server.setNumberOfPlayersForNextGame(numberOfPlayers);
                //TODO send a waiting in the lobby message
                clientHandler.setClientHandlerPhase(ClientHandlerPhase.READY_TO_START);
            } else {
                clientHandler.sendMessageToClient(new NumberOfPlayersRequest(true));
            }
        }
    }
}
