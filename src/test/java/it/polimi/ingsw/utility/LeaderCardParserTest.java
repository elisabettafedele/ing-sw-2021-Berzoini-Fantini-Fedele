package it.polimi.ingsw.utility;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.Flag;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Value;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class LeaderCardParserTest {
    @Test
    public void TestLeaderParser() throws JsonFileNotFoundException, InvalidArgumentException, FileNotFoundException, UnsupportedEncodingException, InactiveCardException, ValueNotPresentException, DifferentEffectTypeException {
        List<LeaderCard> cards;
        cards = LeaderCardParser.parseCards();
        assertEquals(cards.get(0).getVictoryPoints(), 2);
        assertEquals(cards.get(0).getID(), 49);
        assertEquals(cards.get(0).getPathImageBack(), "/img/Cards/LeaderCards/back/Masters of Renaissance_Cards_BACK_3mmBleed_1-49.pdf");
        assertEquals(cards.get(0).getPathImageFront(), "/img/Cards/LeaderCards/front/Masters of Renaissance_Cards_FRONT_3mmBleed_1-49.pdf");
        Map<Flag, Integer> flagValue= new HashMap<Flag, Integer>();
        flagValue.put(new Flag(FlagColor.YELLOW, Level.ANY), 1);
        flagValue.put(new Flag(FlagColor.GREEN, Level.ANY), 1);
        Value cost = new Value(flagValue, null, 0);
        assertEquals(cards.get(0).getCost(), cost);
        Effect effect = new Effect(Resource.SERVANT);
        assertEquals(cards.get(0).getEffect(), effect);
    }


}