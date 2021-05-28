package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Flag;
import it.polimi.ingsw.model.cards.Production;
import it.polimi.ingsw.model.cards.Value;
import it.polimi.ingsw.jsonParsers.DevelopmentCardParser;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DevelopmentCardGridTest {


    @Test
    public void testConstructorHasAllTheCards() throws UnsupportedEncodingException, InvalidArgumentException {
        List<DevelopmentCard> cards = DevelopmentCardParser.parseCards();
        DevelopmentCardGrid grid = new DevelopmentCardGrid();
        int cont = 0;
        for(int i = 0; i < 4; i++){
            List<DevelopmentCard> availableCards = grid.getAvailableCards();
            for(int j = 0; j < availableCards.size(); j++){
                grid.removeCard(availableCards.get(j));
                cards.remove(availableCards.get(j));
            }
        }
        assertTrue(cards.isEmpty());
    }

    @Test
    public void testGetAvailableCards() throws UnsupportedEncodingException, InvalidArgumentException {
        DevelopmentCardGrid grid = new DevelopmentCardGrid();
        for(int j = 0; j < 4; j++) {
            List<DevelopmentCard> availableCards = grid.getAvailableCards();
            for (int i = 0; i < 12; i++) {
                assertEquals(availableCards.get(i).getFlag().getFlagColor(), FlagColor.valueOf(i % 4));
                assertEquals(availableCards.get(i).getFlag().getFlagLevel(), Level.valueOf((i / 4) * (-1) + 2));
            }
        }
    }

    @Test
    public void testCheckEmptyColumn() throws UnsupportedEncodingException, InvalidArgumentException {
        DevelopmentCardGrid grid = new DevelopmentCardGrid();
        for(int i = 0; i < 12; i++){
            List<DevelopmentCard> availableCards = grid.getAvailableCards();
            int finalI = i;
            List<DevelopmentCard> cards = availableCards.stream().filter(c ->
                    c.getFlag().getFlagColor().getValue() == 0).filter(c ->
                    c.getFlag().getFlagLevel().getValue() == (finalI/4) *(-1)+2).
                    collect(Collectors.toList());
            grid.removeCard(cards.get(0));
        }
        assertTrue(grid.checkEmptyColumn());
    }

    @Test
    public void testCheckEmptyColumnReturnFalse() throws UnsupportedEncodingException, InvalidArgumentException {
        DevelopmentCardGrid grid = new DevelopmentCardGrid();
        assertFalse(grid.checkEmptyColumn());
    }

    @Test (expected = InvalidArgumentException.class)
    public void testRemoveCardInvalidArgumentException() throws UnsupportedEncodingException, InvalidArgumentException {
        DevelopmentCardGrid grid = new DevelopmentCardGrid();
        List<DevelopmentCard> availableCards;
        List<DevelopmentCard> cards;
        for(int i = 0; i < 3; i++) {
            availableCards = grid.getAvailableCards();
            cards = availableCards.stream().filter(c ->
                    c.getFlag().getFlagColor().getValue() == 0).filter(c ->
                    c.getFlag().getFlagLevel().getValue() == 0).
                    collect(Collectors.toList());
            grid.removeCard(cards.get(0));
        }
        DevelopmentCard testCard = new DevelopmentCard(5, new Value(null,null, 5),
                new Flag(FlagColor.GREEN, Level.ONE), new Production(new Value(null,null,3), new
                Value(null,null,2)), "a", "b");
        grid.removeCard(testCard);
    }

    @Test (expected = EmptyStackException.class)
    public void testRemoveCardEmptyStackException() throws InvalidArgumentException, UnsupportedEncodingException {
        DevelopmentCardGrid grid = new DevelopmentCardGrid();
        List<DevelopmentCard> availableCards;
        List<DevelopmentCard> cards;
        for(int i = 0; i < 4; i++) {
            availableCards = grid.getAvailableCards();
            cards = availableCards.stream().filter(c ->
                    c.getFlag().getFlagColor().getValue() == 0).filter(c ->
                    c.getFlag().getFlagLevel().getValue() == 0).
                    collect(Collectors.toList());
            grid.removeCard(cards.get(0));
        }
        DevelopmentCard testCard = new DevelopmentCard(5, new Value(null,null, 5),
                new Flag(FlagColor.GREEN, Level.ONE), new Production(new Value(null,null,3), new
                Value(null,null,2)), "a", "b");
        grid.removeCard(testCard);
    }

    @Test
    public void testCardGridOfInteger(){
        Stack<Integer>[][] oldCardsGrid = new Stack[3][4];
        Random random = new Random();
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 4; j++){
                oldCardsGrid[i][j] = new Stack<>();
                if (j != 1){
                    oldCardsGrid[i][j].push(1 + random.nextInt(30));
                }
            }
        }
        DevelopmentCardGrid grid = new DevelopmentCardGrid(oldCardsGrid);
        for (int i = 0; i < 3; i++){
            for(int j = 0; j < 4; j++){
                assertEquals(grid.getCardGrid()[i][j].size(), oldCardsGrid[i][j].size());
                for (int k = 0; k < oldCardsGrid[i][j].size(); k++){
                    assertEquals(grid.getCardGrid()[i][j].get(k).getID(), (int) oldCardsGrid[i][j].get(k));
                }
            }
        }
    }
}