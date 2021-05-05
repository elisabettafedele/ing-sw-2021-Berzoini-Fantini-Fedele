package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public class ChooseResourceAndStorageTypeRequest implements MessageToClient{

    private List<String> resourceTypes;
    List<String> storageTypes;
    private int quantity;

    public ChooseResourceAndStorageTypeRequest(List<String> resourceTypes, List<String> storageTypes, int quantity){
        this.resourceTypes = resourceTypes;
        this.storageTypes = storageTypes;
        this.quantity = quantity;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseResourceTypeRequest(resourceTypes, storageTypes, quantity);
    }
}
