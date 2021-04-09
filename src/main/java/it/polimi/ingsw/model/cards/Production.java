package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;
import it.polimi.ingsw.model.player.PersonalBoard;

/**
 * The class represents the production power of {@link DevelopmentCard}, {@link LeaderCard} that has this effect and
 * the default production of {@link PersonalBoard}.
 *
 */
public class Production{

    private Value productionCost;
    private Value productionOutput;

    /**
     * Constructs a production effect made of a Cost and an Output.
     * @param productionCost the {@link Value} needed in order to activate this production
     * @param productionOutput the {@link Value} produced when the production is activated
     * @throws InvalidArgumentException if productionCost or productionOutput are null
     */
    public Production(Value productionCost, Value productionOutput) throws InvalidArgumentException {
        if(productionCost == null || productionOutput == null){
            throw new InvalidArgumentException();
        }
        this.productionCost = productionCost;
        this.productionOutput = productionOutput;
    }

    /**
     * Get the production power of the {@link Effect} associated with a {@link Card}
     * @return a List composed of production Cost and production Output
     */
    public List<Value> getProductionPower() {
        List<Value> effect = new ArrayList<>(2);
        effect.add(this.productionCost);
        effect.add(this.productionOutput);
        return effect;
    }
}
