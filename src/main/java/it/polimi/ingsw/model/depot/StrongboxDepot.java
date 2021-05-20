package it.polimi.ingsw.model.depot;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidDepotException;

import java.io.Serializable;

public class StrongboxDepot extends Depot implements Serializable {
    public StrongboxDepot(Resource res) {
        super(res);
    }

    public StrongboxDepot (){
        super();
    }

    /**
     * Increments the number of {@link Resource} available in the {@link Depot}
     * @param quantity number of {@link Resource} to add to the {@link Depot}
     */
    public void addResources(int quantity) throws InvalidDepotException, InvalidArgumentException {
        if (quantity < 0){
            throw new InvalidArgumentException();
        }
        if (this.resourceType.equals(Resource.ANY)) {
            throw new InvalidDepotException("Invalid depot: the depot has no resource type, select it before adding resources");
        }
        this.resourceQuantity += quantity;
    }
}
