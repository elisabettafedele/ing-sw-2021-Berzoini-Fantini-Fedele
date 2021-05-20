package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The class represents the Market Tray with the {@link Marble} that can be taken during the {@link TakeResourceFromMarket}
 * action
 */
public class Market implements Serializable {

    private static final int ROW = 3;
    private static final int COL = 4;
    private final Marble[][] marketTray;
    private Marble slideMarble;

    /**
     * Construct a Market with random {@link Marble} on the Market Tray
     */
    public Market() {
        ArrayList<Integer> marbles = new ArrayList<>(Arrays.asList(Marble.GREY.getValue(),Marble.GREY.getValue()
                ,Marble.PURPLE.getValue(),Marble.PURPLE.getValue(),Marble.RED.getValue(),Marble.BLUE.getValue(),
                Marble.BLUE.getValue(),Marble.YELLOW.getValue(),Marble.YELLOW.getValue(),Marble.WHITE.getValue(),
                Marble.WHITE.getValue(),Marble.WHITE.getValue(),Marble.WHITE.getValue()));
        marketTray = new Marble[3][4];
        Random rand = new Random();
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                marketTray[i][j] = Marble.valueOf(marbles.get(rand.nextInt(marbles.size())));
                marbles.remove((Integer)marketTray[i][j].getValue());
            }
        }
        slideMarble = Marble.valueOf(marbles.get(0));
    }

    /**
     * Returns the list of {@link Marble} from a chosen row or column of the Market Tray
     * @param insertionPosition a number from 0 to 6
     * @return list of {@link Marble} from a chosen row or column of the Market Tray
     * @throws InvalidArgumentException for wrong insertionPosition
     */
    public List<Marble> insertMarbleFromTheSlide(int insertionPosition) throws InvalidArgumentException {

        if(insertionPosition < 0 || insertionPosition > 6){
            throw new InvalidArgumentException();
        }
        List<Marble> marbles = new ArrayList<>();

        if(insertionPosition < ROW){
            marbles.add(marketTray[insertionPosition][0]);
            for(int j = 0; j < COL-1; j++){
                marketTray[insertionPosition][j] = marketTray[insertionPosition][j+1];
                marbles.add(marketTray[insertionPosition][j]);
            }
            marketTray[insertionPosition][COL-1] = slideMarble;
        }else{
            insertionPosition = insertionPosition*(-1) + 6; //to have the insertion position between 0 and 3
            marbles.add(marketTray[0][insertionPosition]);
            for(int i = 0; i < ROW - 1; i ++){
                marketTray[i][insertionPosition] = marketTray[i+1][insertionPosition];
                marbles.add(marketTray[i][insertionPosition]);
            }
            marketTray[ROW-1][insertionPosition] = slideMarble;
        }
        slideMarble = marbles.get(0);
        return marbles;
    }

    public Marble[][] getMarketTray(){
        return marketTray;
    }

    public Marble getSlideMarble(){
        return slideMarble;
    }

}
