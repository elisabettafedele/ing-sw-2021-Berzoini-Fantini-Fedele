package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.game.Market;
import junit.framework.TestCase;
import org.junit.Test;

public class GraphicalMarketTest extends TestCase {

    @Test
    public void testMarketPrint() throws InvalidArgumentException {
        Market market = new Market();
        GraphicalMarket.printMarket(market.getMarketTray(), market.getSlideMarble());
        market.insertMarbleFromTheSlide(1);
        GraphicalMarket.printMarket(market.getMarketTray(), market.getSlideMarble());
        System.out.println("CIao");

    }

}