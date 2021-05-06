package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public class ChooseResourceTypeRequest implements MessageToClient{

    private List<Resource> resourceTypes;
    private int quantity;


    public ChooseResourceTypeRequest(List<Resource> resourceTypes, int quantity){
        this.resourceTypes = resourceTypes;
        this.quantity = quantity;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseResourceTypeRequest(resourceTypes, quantity);
    }
}
