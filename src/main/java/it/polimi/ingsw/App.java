package it.polimi.ingsw;

import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.io.UnsupportedEncodingException;


/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args ) throws InvalidArgumentException, DifferentEffectTypeException, UnsupportedEncodingException {
        char[][] a = new char[5][4];
        System.out.println(a.length);
    }
}

