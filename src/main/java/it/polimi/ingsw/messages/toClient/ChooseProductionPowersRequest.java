package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;
import java.util.Map;

public class ChooseProductionPowersRequest implements MessageToClient {

    List<Integer> productionCardsIDs;
    Map<Resource, Integer> availableResources;

    public ChooseProductionPowersRequest(List<Integer> productionCardsIDs, Map<Resource, Integer> availableResources) {
        this.productionCardsIDs = productionCardsIDs;
        this.availableResources = availableResources;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseProductionPowersRequest(this.productionCardsIDs, this.availableResources);
    }
}
