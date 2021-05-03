package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A development card that can be used to produce resources and faith points
 */
public class DevelopmentCard extends Card {

    private static final long serialVersionUID = -7585665187979106207L;
    private Flag flag;
    private Production production;

    /**
     *
     * @param victoryPoints the number of victory points obtained at the end of the game
     * @param cost the number and type of {@link Resource} needed to buy the card from the {@link it.polimi.ingsw.model.game.DevelopmentCardGrid}
     * @param flag the {@link Flag} representing type and level of the card
     * @param production the {@link Production} power associated to the card
     * @throws InvalidArgumentException
     */
    public DevelopmentCard(int victoryPoints, Value cost, Flag flag, Production production, String pathImageFront, String pathImageBack) throws InvalidArgumentException {
        super(victoryPoints, cost, pathImageFront, pathImageBack);
        if(flag == null || production == null){
            throw new InvalidArgumentException();
        }
        this.flag = flag;
        this.production = production;
    }

    /**
     * get the {@link Flag} of the card representing his type and level
     * @return the {@link Flag} of the card representing his type and level
     */
    public Flag getFlag() {
        return flag;
    }

    /**
     * Get the {@link Production} {@link Effect} of the card
     * @return the {@link Production} {@link Effect} of the card
     */
    public Production getProduction() {
        return production;
    }

    public Map<Resource, Integer> getDiscountedCost(List<Resource> discountedResources) {
        Map<Resource, Integer> originalCost = null;
        try {
            originalCost = this.getCost().getResourceValue();
        } catch (ValueNotPresentException e) {
            System.out.println("It is not possible to get the discounted cost of this card since it is not a development card and it does not have any resource cost\n");
            e.printStackTrace();
            return null;
        }
        Map<Resource, Integer> discountedCost = new HashMap<Resource, Integer>();
        for (Resource resource : originalCost.keySet()) {
            if (discountedResources.isEmpty() || !discountedResources.contains(resource))
                discountedCost.put(resource, originalCost.get(resource));
            else if (originalCost.get(resource) > 1 && discountedResources.contains(resource))
                discountedCost.put(resource, originalCost.get(resource)-1);
        }
        return discountedCost;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DevelopmentCard that = (DevelopmentCard) o;
        return Objects.equals(flag, that.flag) && Objects.equals(production, that.production);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flag, production);
    }
}
