package it.polimi.ingsw;

import it.polimi.ingsw.enumerations.Resource;
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
        Queue<Integer> fifo = new LinkedList<>();

        fifo.add(5);
        fifo.add(7);
        fifo.add(12);

        System.out.println(fifo.remove());

    }
}

