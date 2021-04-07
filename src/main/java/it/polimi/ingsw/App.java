package it.polimi.ingsw;

import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.Production;
import it.polimi.ingsw.model.cards.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Integer.valueOf;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args ) throws InvalidArgumentException, DifferentEffectTypeException {
        Marble mar = Marble.PURPLE;
        System.out.println(mar);
        System.out.println(Marble.valueOf(3));

    }
}
