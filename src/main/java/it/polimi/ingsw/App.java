package it.polimi.ingsw;

import it.polimi.ingsw.enumerations.ValueType;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.Depot;
import it.polimi.ingsw.model.FaithValue;
import it.polimi.ingsw.model.Value;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args )
    {

        ValueType a = null;
        System.out.println( "Hello World!" );
        try {
            Value v = new FaithValue(5);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
}
