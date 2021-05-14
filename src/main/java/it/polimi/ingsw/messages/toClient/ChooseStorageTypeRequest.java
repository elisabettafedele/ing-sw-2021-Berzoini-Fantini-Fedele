package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toServer.MessageToServer;

import java.util.List;

public class ChooseStorageTypeRequest implements MessageToClient {
    private Resource resource;
    private List<String> availableDepots;
    private boolean canDiscard;
    private boolean canReorganize;

    public ChooseStorageTypeRequest(Resource resource, List<String> availableDepots, boolean canDiscard, boolean canReorganize){
        this.resource = resource;
        this.availableDepots = availableDepots;
        this.canDiscard = canDiscard;
        this.canReorganize = canReorganize;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseStorageTypeRequest(resource, availableDepots, canDiscard, canReorganize);
    }
}
