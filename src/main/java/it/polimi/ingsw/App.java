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
        Map<Resource, Integer> availableResources = new HashMap<>();
        availableResources.put(Resource.COIN, 2);
        availableResources.put(Resource.STONE, 1);
        availableResources.put(Resource.SERVANT, 3);

        System.out.println(availableResources.values().stream().mapToInt(Integer::intValue).sum() >= 2);

        Set<Resource> set = new HashSet<>();
        set.add(Resource.COIN);
        set.add(Resource.STONE);
        set.add(Resource.COIN);
        System.out.println(set);

        for(int i = 0; i < set.size(); i++){
            System.out.printf("%d" + set +"\n", i+1);
        }
    }
}

