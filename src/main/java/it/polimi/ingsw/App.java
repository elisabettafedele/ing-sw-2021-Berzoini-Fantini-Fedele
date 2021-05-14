package it.polimi.ingsw;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.cli.graphical.GraphicalDevelopmentCardGrid;
import it.polimi.ingsw.client.cli.graphical.GraphicalLogo;
import it.polimi.ingsw.client.cli.graphical.SubscriptNumbers;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Value;
import it.polimi.ingsw.model.game.DevelopmentCardGrid;
import it.polimi.ingsw.utility.DevelopmentCardParser;

import java.io.UnsupportedEncodingException;
import java.util.*;


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

