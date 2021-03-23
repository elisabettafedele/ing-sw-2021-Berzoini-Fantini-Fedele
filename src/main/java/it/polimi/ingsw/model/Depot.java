package it.polimi.ingsw.model;

import it.polimi.ingsw.enumerations.Resource;

/**
 * This class represents a depot of Warehouse, Strongbox and Extra Depot effect TODO links
 */

public abstract class Depot {
    
    private Resource resourceType;
    private int resourceQuantity;

    public Resource getResourceType() {
        return resourceType;
    }

    public int getResourceQty() {
        return resourceQuantity;
    }

    /**
     * Increments the number of {@link Resource} available in the {@link Depot}
     * @param quantity number of {@link Resource} to add to the {@link Depot}
     */
    public void addResources(int quantity) {
        this.resourceQuantity += quantity;
    }

    /**
     * Decrements the number of {@link Resource} available in the {@link Depot}
     * @param quantity number of {@link Resource} to remove from the {@link Depot}
     */
    public void removeResources(int quantity) {
        this.resourceQuantity -= quantity;
    }

}
