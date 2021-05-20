package it.polimi.ingsw.model.depot;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InsufficientQuantityException;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidDepotException;

import java.io.Serializable;

/**
 * Abstract class extended by all the possible kind of depots in the game, that are: WarehouseDepot, StrongboxDepot and LeaderDepot
 */

public abstract class Depot implements Serializable {
    
    protected Resource resourceType;
    protected int resourceQuantity;

    public Depot (){
        this.resourceType = Resource.ANY;
        this.resourceQuantity = 0;
    }

    /**
     * Class constructor specifying the type of resources that will be contained in the depot
     */
    public Depot (Resource resourceType){
        this.resourceType = resourceType;
        this.resourceQuantity = 0;
    }

    /**
     * @return the type of the {@link Resource} contained in the {@link Depot}
     */
    public Resource getResourceType() {
        return this.resourceType;
    }

    /**
     * @return the number of resources contained in the {@link Depot}
     */
    public int getResourceQuantity() {
        return this.resourceQuantity;
    }

    /**
     * Decrements the number of {@link Resource} available in the {@link Depot}
     * @param quantity number of {@link Resource} to remove from the {@link Depot}
     * @throws InsufficientQuantityException if the number of resources to remove exceed the number of resources present in the depot
     * @throws InvalidArgumentException if {@param quantity} is null or negative
     */
    public void removeResources(int quantity) throws InsufficientQuantityException, InvalidArgumentException {
        if (quantity < 0){
            throw new InvalidArgumentException();
        }
        if(this.resourceQuantity-quantity<0) {
            throw new InsufficientQuantityException(quantity, this.getResourceQuantity());
        }
        this.resourceQuantity -= quantity;
    }

    public String toString(){
        return "Depot: resource=" + this.getResourceType() + ", quantity=" + Integer.toString(this.getResourceQuantity());
    }

}
