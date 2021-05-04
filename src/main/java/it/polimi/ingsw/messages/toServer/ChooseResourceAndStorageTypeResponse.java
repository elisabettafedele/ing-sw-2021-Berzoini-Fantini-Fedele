package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;

import java.util.Map;

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
        clientHandler.getController().handleMessage(this, clientHandler);
    }
}
