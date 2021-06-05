package it.polimi.ingsw;

import it.polimi.ingsw.client.cli.graphical.SubscriptNumbers;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args ) throws InvalidArgumentException, DifferentEffectTypeException, UnsupportedEncodingException {
        Stack<Integer> stack = new Stack<>();

        stack.push(5);
        stack.push(3);
        stack.push(9);
        for(int i = 0; i < stack.size(); i++){
            System.out.println(stack.get(i));
        }
        System.out.println(stack);

        System.out.print(SubscriptNumbers.values());

    }
}

