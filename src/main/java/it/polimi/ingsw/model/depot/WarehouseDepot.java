package it.polimi.ingsw.model.depot;
import it.polimi.ingsw.exceptions.InsufficientQuantityException;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidDepotException;

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
     * @return the capacity of the {@link Depot}
     */
    public int getMaxResourceQuantity() {
        return maxResourceQuantity;
    }

    public void setResourceType(Resource resourceType){
        this.resourceType = resourceType;
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
    @Override
    public void addResources(int quantity) throws InvalidArgumentException, InsufficientSpaceException, InvalidDepotException {
        int available = this.getMaxResourceQuantity()-this.getResourceQuantity();
        if (quantity > available)
            throw new InsufficientSpaceException(quantity, available);
        super.addResources(quantity);
    }

    public String toString(int depotNumber){
        return "Depot " + Integer.toString(depotNumber) + ": resource=" + this.getResourceType() + ", quantity=" + this.getResourceQuantity();
    }
}
