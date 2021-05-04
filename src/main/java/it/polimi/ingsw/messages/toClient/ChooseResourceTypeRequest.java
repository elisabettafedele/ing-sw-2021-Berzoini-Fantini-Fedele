package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public class ChooseResourceTypeRequest implements MessageToClient{

    private List<String> resourceTypes;
    private int quantity;

    public ChooseResourceTypeRequest(List<String> resourceTypes, int quantity){
        this.resourceTypes = resourceTypes;
        this.quantity = quantity;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseResourceTypeRequest(resourceTypes, quantity);
    }
}
