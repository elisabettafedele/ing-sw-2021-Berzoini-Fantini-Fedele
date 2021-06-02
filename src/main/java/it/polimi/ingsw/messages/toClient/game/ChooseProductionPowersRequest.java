package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toClient.MessageToClient;
import it.polimi.ingsw.model.cards.Value;

import java.util.List;
import java.util.Map;

public class ChooseProductionPowersRequest implements MessageToClient {

    /**
     * Message used to ask the client which productions he wants to use
     */

    Map<Resource, Integer> availableResources;
    Map<Integer, List<Value>> availableProductionPowers;

    public ChooseProductionPowersRequest(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources) {
        this.availableProductionPowers = availableProductionPowers;
        this.availableResources = availableResources;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseProductionPowersRequest(this.availableProductionPowers, this.availableResources);
    }
}
