package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;

public class SelectStorageRequest implements MessageToClient {
Resource resource;
boolean isInWarehouse;
boolean isInStrongbox;
boolean isInLeaderDepot;
    public SelectStorageRequest(Resource resource, boolean isInWarehouse,boolean isInStrongbox, boolean isInLeaderDepot) {
        this.resource=resource;
        this.isInWarehouse=isInWarehouse;
        this.isInStrongbox=isInStrongbox;
        this.isInLeaderDepot=isInLeaderDepot;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displaySelectStorageRequest(resource, isInWarehouse, isInStrongbox, isInLeaderDepot);
    }
}
