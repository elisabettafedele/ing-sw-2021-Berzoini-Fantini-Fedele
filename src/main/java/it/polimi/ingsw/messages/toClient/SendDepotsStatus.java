package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public class SendDepotsStatus implements MessageToClient{
    private List<Resource>[] warehouseDepots;
    private List<Resource>[] strongboxDepots;
    private List<List<Resource>> leaderDepots;
    public SendDepotsStatus(List<Resource>[] warehouseDepots, List<Resource>[] strongboxDepots, List<List<Resource>> leaderDepots){

    }

    @Override
    public void handleMessage(VirtualView view) {

    }

    public List<Resource>[] getWarehouseDepots() {
        return warehouseDepots;
    }
}
