package it.polimi.ingsw.model.player;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.LeaderCard;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PersonalBoardTest {
    PersonalBoard personalBoard;
    @Before
    public void setUp() throws Exception {
        ArrayList<LeaderCard> leaderCardList=new ArrayList<>();
        personalBoard=new PersonalBoard(leaderCardList);
    }

    @Test(expected = InvalidArgumentException.class)
    public void personalBoard_constructorCorrectlyThrowsException() throws InvalidArgumentException {
        PersonalBoard pb=new PersonalBoard(null);
    }


}
