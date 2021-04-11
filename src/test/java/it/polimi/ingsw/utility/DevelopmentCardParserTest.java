package it.polimi.ingsw.utility;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Flag;
import it.polimi.ingsw.model.cards.Production;
import it.polimi.ingsw.model.cards.Value;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DevelopmentCardParserTest {

    @Test
    public void TestDevelopmentCardParse() throws UnsupportedEncodingException, InvalidArgumentException {
        List<DevelopmentCard> cards;
        cards = DevelopmentCardParser.parseCards();
        DevelopmentCard card = cards.get(0);


        Map<Resource, Integer> prodCostValue = new LinkedHashMap<>();
        prodCostValue.put(Resource.COIN, 1);


        Value productionCost = new Value(null, prodCostValue, 0);
        Value productionOutput = new Value(null, null, 1);
        Production p = new Production(productionCost, productionOutput);

        Map<Resource, Integer> cost = new LinkedHashMap<>();
        cost.put(Resource.SHIELD, 2);
        assertEquals(card.getPathImageBack(), "/img/Cards/DevelopmentCards/back/Masters of Renaissance_Cards_BACK_3mmBleed_1-1");
        assertEquals(card.getPathImageFront(), "/img/Cards/DevelopmentCards/front/Masters of Renaissance_Cards_FRONT_3mmBleed_1-1");
        assertEquals(card.getFlag(), new Flag(FlagColor.GREEN, Level.ONE));
        assertEquals(card.getProduction(), p);
        assertEquals(card.getVictoryPoints(), 1);
        assertEquals(card.getCost(), new Value(null, cost, 0));


        assertTrue(true);

    }
}