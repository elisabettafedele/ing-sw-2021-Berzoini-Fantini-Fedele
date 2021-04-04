package it.polimi.ingsw.model;

import it.polimi.ingsw.enumerations.Resource;

public class LeaderDepot extends Depot{

    private final int maxResourceQty = 2;

    public LeaderDepot(Resource resourceType) {
        super(resourceType);
    }
}
