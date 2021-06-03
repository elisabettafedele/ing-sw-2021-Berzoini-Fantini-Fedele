package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class NotifyResourcesToStore implements MessageToClient {
    private List<Resource> resourcesToStore;

    public NotifyResourcesToStore(List<Resource> resourcesToStore) {
        this.resourcesToStore = resourcesToStore;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayResourcesToStore(resourcesToStore);
    }

    public String toString(){
        return "notifying the resources to store";
    }
}
