package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.utility.DevelopmentCardParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DevelopmentCardGridTest {


    @Test
    public void test_Constructor_has_all_the_cards() throws UnsupportedEncodingException, InvalidArgumentException {
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
    public void test_getAvailableCards() throws UnsupportedEncodingException, InvalidArgumentException {
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
    public void test_checkEmptyColumn() throws UnsupportedEncodingException, InvalidArgumentException {
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
    public void test_checkEmptyColumn_returnFalse() throws UnsupportedEncodingException, InvalidArgumentException {
        DevelopmentCardGrid grid = new DevelopmentCardGrid();
        assertFalse(grid.checkEmptyColumn());
    }
}