package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.depot.Depot;
import it.polimi.ingsw.model.depot.WarehouseDepot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represent a Warehouse composed of three depot of capacity of 1, 2 and 3 resources, respectively
 * The smallest depot has index 0
 */

public class Warehouse {
    private final WarehouseDepot[] depots;
    private final int numberOfDepots = 3;

    /**
     * Class constructor: creates an Array of three depots where:
     * depots[0] can contain maximum one resource
     * depots[1] can contain maximum two resources
     * depots[2] can contain maximum three resources
     * and each depot contains the same {@link Resource}, that cannot be placed in two different depots
     * @throws InvalidArgumentException
     */
    public Warehouse() throws InvalidArgumentException {
        depots = new WarehouseDepot[numberOfDepots];
        for (int i = 0; i < numberOfDepots; i++){
            depots[i] = new WarehouseDepot(i+1);
        }
    }

    /**
     * Method used to get the {@link Resource} type of a certain depot of the warehouse
     * @param row identifies the specific depot of the warehouse to consider
     * @return the type of the resources contained in the depot
     * @throws InvalidArgumentException when the parameter row does not identify a depot of the warehouse
     */
    public Resource getResourceTypeOfDepot(int row) throws InvalidArgumentException{
        if (row < 0 || row >= numberOfDepots)
            throw new InvalidArgumentException();
        return depots[row].getResourceType();
    }

    /**
     * Method used to get the available {@link ResourceStorageType} of the {@link Warehouse} for a certain type of {@link Resource}
     * @param resource the {@link Resource} to store
     * @return a list of the available {@link ResourceStorageType} of the {@link Warehouse}
     */
    public List<ResourceStorageType> getAvailableWarehouseDepotsForResourceType(Resource resource){
        int row;
        List<ResourceStorageType> depots= new ArrayList<ResourceStorageType>();
        row = getRowIndexFromResource(resource);

        //If a depot is already used for the resource
        if (row != -1){
            if (this.depots[row].enoughSpace(1))
                depots.add(ResourceStorageType.valueOf(row));
            return depots;
        } else {
            for (row = 0; row < numberOfDepots; row++) {
                if (this.depots[row].getResourceType() == Resource.ANY)
                    depots.add(ResourceStorageType.valueOf(row));
            }
        }

        return depots;

    }

    /**
     * @return the number of depot of the warehouse
     */
    public int getNumberOfDepots(){
        return this.numberOfDepots;
    }


    /**
     * @return the {@link Resource} type of resources contained in the depots
     */
    public List<Resource> getResourceTypes(){
        List<Resource> res;
        res = Arrays.stream(depots)
                .filter(x->x.getResourceType()!=Resource.ANY)
                .map(Depot::getResourceType)
                .collect(Collectors.toList());
        return res;
    }

    /**
     * Method used to get the {@link Resource} quantity of a certain depot of the warehouse
     * @param row identifies the specific depot of the warehouse to consider
     * @return the number of resources contained in the depot
     * @throws InvalidArgumentException
     */
    public int getResourceQuantityOfDepot(int row) throws InvalidArgumentException{
        if (row < 0 || row >= numberOfDepots)
            throw new InvalidArgumentException();
        return depots[row].getResourceQuantity();
    }

    public Depot getWarehouseDepot(int row){
        return depots[row];
    }

    /**
     * Method used to find where a specific resource type is located in the Warehouse
     * @param res the type of the resource searched
     * @return the row of the depot which contains the {@link Resource} of type {@param res} if it is present, -1 otherwise
     */
    private int getRowIndexFromResource(Resource res) {
        int i=0;
        if (!this.getResourceTypes().contains(res))
            return -1;
        while(depots[i].getResourceType()!=res){
            i++;
        }
        return i;
    }

    /**
     * Method to add a number of resources of a certain type to a specific {@link Depot} of the {@link Warehouse}
     * @param row the row of the depot the resources should be added to
     * @param type identifies the type of the resources to add
     * @param quantity identifies the quantity of the resources to add
     * @throws InsufficientSpaceException when the depot has not a sufficient capacity to store the new resources
     * @throws InvalidResourceTypeException when another type of resource is stored in the depot identified by {@param row} or the type of resource is already stored
     * @throws InvalidDepotException never thrown in this case
     * @throws InvalidArgumentException when the {@param quantity} is null or negative
     */

    public void addResourcesToDepot(int row, Resource type, int quantity) throws InsufficientSpaceException, InvalidResourceTypeException, InvalidDepotException, InvalidArgumentException {
        Resource depotType = this.getResourceTypeOfDepot(row);
        if ((!depotType.equals(Resource.ANY) && !depotType.equals(type)) || (this.getResourceTypes().contains(type) && row != getRowIndexFromResource(type)))
            throw new InvalidResourceTypeException();
        if (!depots[row].enoughSpace(quantity)) {
            throw new InsufficientSpaceException(quantity, depots[row].spaceAvailable());
        }
        depots[row].setResourceType(type);
        depots[row].addResources(quantity);
    }

    /**
     * Method to remove a number of {@link Resource} of a certain type from a specific depot of the warehouse
     * @param type type of the resources to remove
     * @param quantity how many resources should be removed
     * @throws InvalidResourceTypeException when another type of resource is stored in the depot identified by {@param row}
     * @throws InsufficientQuantityException when the number of {@link Resource} to remove exceed the number of those stored
     * @throws InvalidArgumentException when the {@param quantity} is null or negative
     */
    public void removeResourcesFromDepot(Resource type, int quantity) throws InvalidResourceTypeException, InsufficientQuantityException, InvalidArgumentException {
        if (getRowIndexFromResource(type) == -1)
            throw new InvalidResourceTypeException();
        depots[getRowIndexFromResource(type)].removeResources(quantity);
    }

    /**
     * Method used to switch the {@link Resource} stored in two different {@link Depot}
     * @param one row of one {@link Depot}
     * @param other row of the other {@link Depot}
     * @throws UnswitchableDepotsException when the switch is not allowed by the capacity of the smallest {@link Depot}
     * @throws InsufficientSpaceException thrown when a depot cannot contain a certain number of {@link Resource}
     */
    public void switchRows(int one, int other) throws UnswitchableDepotsException, InsufficientSpaceException, InvalidArgumentException {
        Resource tmpType;
        int tmpQuantity;
        if (one == other || one < 0 || one >= numberOfDepots || other >= numberOfDepots || other < 0)
            throw new InvalidArgumentException("invalid row number\n");
        if (!switchable(one, other))
            throw new UnswitchableDepotsException();
        tmpType = depots[one].getResourceType();
        tmpQuantity = depots[one].getResourceQuantity();
        depots[one].setResourceQuantity(depots[other].getResourceQuantity());
        depots[one].setResourceType(depots[other].getResourceType());
        depots[other].setResourceQuantity(tmpQuantity);
        depots[other].setResourceType(tmpType);
    }

    /**
     * Method that check whether two {@link Depot} are switchable
     * @param one the row of one {@link Depot}
     * @param other the row of the other {@link Depot}
     * @return true if two {@link Depot} are switchable
     */
    private boolean switchable(int one, int other){
        return (depots[one].getResourceQuantity() <= depots[other].getMaxResourceQuantity()) && (depots[other].getResourceQuantity() <= depots[one].getMaxResourceQuantity());
    }
}
