package it.polimi.ingsw.model.depot;
import it.polimi.ingsw.exceptions.InsufficientQuantityException;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidDepotException;

import java.io.Serializable;

/**
 * This class represents a single Warehouse Depot. It has a limited capacity, reported in maxResourceQuantity
 */
public class WarehouseDepot extends Depot {
    private final int maxResourceQuantity;

    /**
     * Class constructor
     * @param maxResourceQuantity reports the capacity of the new depot
     * @throws InvalidArgumentException if maxResourceQuantity is null or negative
     */
    public WarehouseDepot(int maxResourceQuantity) throws InvalidArgumentException {
        super();
        if (maxResourceQuantity <= 0)
            throw new InvalidArgumentException();
        this.maxResourceQuantity = maxResourceQuantity;
    }

    /**
     * Setter of ResourceQuantity
     * @param quantity
     * @throws InsufficientSpaceException
     */

    public void setResourceQuantity(int quantity) throws InsufficientSpaceException {
        if (this.getMaxResourceQuantity() < quantity)
            throw new InsufficientSpaceException(quantity, this.getMaxResourceQuantity());
        this.resourceQuantity = quantity;
    }

    /**
     * Setter of ResourceType
     * @param type
     */
    public void setResourceType(Resource type)
    {
        this.resourceType = type;
    }
    public int spaceAvailable(){
        return this.getMaxResourceQuantity()-this.getResourceQuantity();
    }


    /**
     * @return the capacity of the {@link Depot}
     */
    public int getMaxResourceQuantity() {
        return maxResourceQuantity;
    }


    /**
     * Method that verifies if the depot is empty
     * @return true when the depot is empty
     */
    public boolean isEmpty(){
        return resourceQuantity == 0;
    }

    /**
     * Method that remove resources from a depot
     * @param quantity number of {@link Resource} to remove from the {@link WarehouseDepot}
     * @throws InvalidArgumentException if {@param quantity} is null or negative
     * @throws InsufficientQuantityException if the number of resources to remove exceed the number of resources present in the depot
     */
    @Override
    public void removeResources(int quantity) throws InvalidArgumentException, InsufficientQuantityException {
        super.removeResources(quantity);
        if (this.isEmpty()){
            setResourceType(Resource.ANY);
        }
    }

    /**
     * Method that add resources to a depot
     * @param quantity number of {@link Resource} to add to the {@link WarehouseDepot}
     * @throws InvalidArgumentException if {@param quantity} is null or negative
     * @throws InsufficientSpaceException if there is not enough space to add {@param quantity} resources
     * @throws InvalidDepotException if the depot has not a specific resourceType
     */

    public void addResources(int quantity) throws InvalidArgumentException, InsufficientSpaceException, InvalidDepotException {
        if (quantity < 0){
            throw new InvalidArgumentException();
        }
        if (this.resourceType.equals(Resource.ANY)) {
            throw new InvalidDepotException("Invalid depot: the depot has no resource type, select it before adding resources");
        }
        if (!enoughSpace(quantity))
            throw new InsufficientSpaceException(quantity, this.spaceAvailable());
        this.resourceQuantity += quantity;
    }


    /**
     * Method that check that a certain number of resources can be added to the depot
     * @param toAdd number of resources that you wish to add
     * @return true only if the number of resources after the add do not exceed the capacity of the depot
     */
    public boolean enoughSpace(int toAdd){
        return this.spaceAvailable() >= toAdd;
    }


    public String toString(int depotNumber){
        return "Depot " + Integer.toString(depotNumber) + ": resource=" + this.getResourceType() + ", quantity=" + this.getResourceQuantity();
    }
}
