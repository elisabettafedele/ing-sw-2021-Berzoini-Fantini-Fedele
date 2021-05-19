package it.polimi.ingsw.jsonParsers;

import it.polimi.ingsw.controller.actions.BlackCrossMoveToken;
import it.polimi.ingsw.controller.actions.DiscardToken;
import it.polimi.ingsw.controller.actions.SoloActionToken;
import it.polimi.ingsw.enumerations.FlagColor;
import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.*;

public class SoloActionTypeTokenParserTest {

    @Test
    public void TestParseTokens() {
        Queue<SoloActionToken> tokens = SoloActionTokenParser.parseTokens();

        SoloActionToken t1 = new BlackCrossMoveToken("/img/SoloActionTokens/front/cerchio6.png", "/img/SoloActionTokens/back/retro cerchi.png", 2, false);
        assertTrue(tokens.contains(t1));

        SoloActionToken t2 = new DiscardToken("/img/SoloActionTokens/front/cerchio1.png", "/img/SoloActionTokens/back/retro cerchi.png", 2, FlagColor.BLUE);

        assertEquals(t2, tokens.remove());

    }
}