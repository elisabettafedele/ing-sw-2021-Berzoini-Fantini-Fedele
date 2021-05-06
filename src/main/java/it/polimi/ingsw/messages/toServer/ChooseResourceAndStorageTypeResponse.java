package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;

import java.util.Map;
import java.util.logging.Level;

public class ChooseResourceAndStorageTypeResponse implements MessageToServer{

    private Map<String, String> storage;

    public ChooseResourceAndStorageTypeResponse(Map<String, String> storage){
        this.storage = storage;
    }

    public Map<String, String> getStorage() {
        return storage;
    }
    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has chosen and stored his resources");
        clientHandler.getController().handleMessage(this, clientHandler);
    }
}
