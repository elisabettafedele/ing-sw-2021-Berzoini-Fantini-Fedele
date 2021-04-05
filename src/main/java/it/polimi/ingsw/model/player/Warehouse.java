package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.depot.WarehouseDepot;

public class Warehouse {
    private WarehouseDepot depots [];
    private final int numberOfDepots = 3;

    public Warehouse() throws InvalidArgumentException {
        depots = new WarehouseDepot[numberOfDepots];
        for (int i = 0; i < numberOfDepots; i++){
            depots[i] = new WarehouseDepot(i+1);
        }
    }

    private WarehouseDepot[] getDepots(){
        return depots.clone();
    }

    public Resource getResourceTypeOfDepot(int row) throws InvalidArgumentException{
        if (row<0 || row>2)
            throw new InvalidArgumentException();
        return depots[row].getResourceType();
    }

    public int getResourceQuantityOfDepot(int row) throws InvalidArgumentException{
        if (row<0 || row>2)
            throw new InvalidArgumentException();
        return depots[row].getResourceQuantity();
    }

}
