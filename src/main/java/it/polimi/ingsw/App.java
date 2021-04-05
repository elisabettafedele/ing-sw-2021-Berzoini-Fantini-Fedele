package it.polimi.ingsw;

import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.Production;
import it.polimi.ingsw.model.cards.Value;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args ) throws InvalidArgumentException, DifferentEffectTypeException {
        System.out.println(EffectType.DISCOUNT);
        Production productionEffect = new Production(new Value(new ArrayList<>(), new HashMap<>(), 5), new Value(new ArrayList<>(), new HashMap<>(), 5));
        Effect exceptionEffect = new Effect(productionEffect);
        exceptionEffect.getDiscountEffect();
    }
}
