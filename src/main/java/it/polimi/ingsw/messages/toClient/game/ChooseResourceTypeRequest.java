package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class ChooseResourceTypeRequest implements MessageToClient {

    /**
     * Message used to ask the client which initial resource type(s) he desires
     */
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

    public String toString(){
        return "asking to choose " +  (quantity == 1 ? "an initial resource" : "two initial resources");
    }
}
