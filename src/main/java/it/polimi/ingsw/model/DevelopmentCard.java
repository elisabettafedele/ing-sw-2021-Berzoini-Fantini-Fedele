package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.enumerations.Resource;

/**
 * A development card that can be used to produce resources and faith points
 */
public class DevelopmentCard extends Card{

    private Flag flag;
    private Production production;

    /**
     *
     * @param victoryPoints the number of victory points obtained at the end of the game
     * @param ID the unique ID of the card
     * @param cost the number and type of {@link Resource} needed to buy the card from the {@link DevelopmentCardGrid}
     * @param flag the {@link Flag} representing type and level of the card
     * @param production the {@link Production} power associated to the card
     * @throws InvalidArgumentException
     */
    public DevelopmentCard(int victoryPoints, int ID, Value cost, Flag flag, Production production) throws InvalidArgumentException {
        super(victoryPoints, ID, cost);
        if(flag == null || production == null){
            throw new InvalidArgumentException();
        }
        this.flag = flag;
        this.production = production;
    }

    public Flag getFlag() {
        return flag;
    }

    public Production getProduction() {
        return production;
    }
}
