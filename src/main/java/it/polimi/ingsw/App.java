package it.polimi.ingsw;

import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args ) throws InvalidArgumentException, DifferentEffectTypeException {
        List<Integer> test = new ArrayList<>();

        for(int i = 1; i<10; i++){
            test.add(i);
        }
        System.out.println(test);

        Iterator<Integer> i= test.iterator();

        int a;
        while(i.hasNext()){
            a = i.next();
            if(a==5){
                i.remove();
                break;
            }
        }
        System.out.println(test);
        test.add(2);
        System.out.println(test);
    }
}

