package it.polimi.ingsw.model.depot;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidDepotException;

/**
 * This class represents an extra depot contained in a Leader Card. It has a limited capacity of maximum 2 resources of a set type.
 */

public class LeaderDepot extends Depot {

    private final int maxResourceQuantity = 2;

    public LeaderDepot(Resource resourceType) {
        super(resourceType);
    }

    /**
     * Method that add resources to a depot
     * @param quantity number of {@link Resource} to add to the {@link LeaderDepot}
     * @throws InvalidArgumentException if {@param quantity} is null or negative
     * @throws InsufficientSpaceException if there is not enough space to add {@param quantity} resources
     * @throws InvalidDepotException if the depot has not a specific resourceType
     */
    public void addResources(int quantity) throws InvalidArgumentException, InsufficientSpaceException, InvalidDepotException {
        int available = maxResourceQuantity - this.getResourceQuantity();

        if (quantity < 0)
            throw new InvalidArgumentException();

        if (quantity > available)
            throw new InsufficientSpaceException(quantity, available);

        this.resourceQuantity += quantity;
    }
}
