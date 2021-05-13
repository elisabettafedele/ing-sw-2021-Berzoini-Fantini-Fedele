package it.polimi.ingsw;

import it.polimi.ingsw.client.cli.graphical.GraphicalDevelopmentCardGrid;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.util.*;


/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args ) throws InvalidArgumentException, DifferentEffectTypeException {

        List<Integer> list = new ArrayList<Integer>();

        list.add(4);
        list.add(4);
        list.add(4);
        list.add(4);



        GraphicalDevelopmentCardGrid gdc = new GraphicalDevelopmentCardGrid();

        gdc.setCardsToDisplay(list);
        gdc.drawDevelopmentCardGrid();

    }
}

