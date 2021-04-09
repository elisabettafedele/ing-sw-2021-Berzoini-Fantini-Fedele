package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.enumerations.Resource;

/**
 * A development card that can be used to produce resources and faith points
 */
public class DevelopmentCard extends Card {

    private Flag flag;
    private Production production;

    /**
     *
     * @param victoryPoints the number of victory points obtained at the end of the game
     * @param cost the number and type of {@link Resource} needed to buy the card from the {@link DevelopmentCardGrid}
     * @param flag the {@link Flag} representing type and level of the card
     * @param production the {@link Production} power associated to the card
     * @throws InvalidArgumentException
     */
    public DevelopmentCard(int victoryPoints, Value cost, Flag flag, Production production) throws InvalidArgumentException {
        super(victoryPoints, cost);
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
}
