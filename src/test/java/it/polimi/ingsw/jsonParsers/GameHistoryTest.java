package it.polimi.ingsw.jsonParsers;


import it.polimi.ingsw.client.PopesTileState;
import it.polimi.ingsw.controller.actions.SoloActionToken;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.jsonParsers.GameHistory;
import it.polimi.ingsw.jsonParsers.SoloActionTokenParser;
import it.polimi.ingsw.model.game.DevelopmentCardGrid;
import it.polimi.ingsw.model.game.Market;
import it.polimi.ingsw.model.game.VaticanReportSection;
import it.polimi.ingsw.model.persistency.PersistentControllerPlayPhaseSingle;
import it.polimi.ingsw.model.persistency.PersistentControllerSetUpPhase;
import it.polimi.ingsw.model.persistency.PersistentGame;
import it.polimi.ingsw.model.persistency.PersistentPlayer;
import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.*;
import java.util.stream.Collectors;

public class GameHistoryTest extends TestCase {

    @BeforeClass
    public void setUpClass() throws InvalidArgumentException {
        PersistentGame persistentGame = new PersistentGame();
        List<PersistentPlayer> persistentPlayers = new ArrayList();
        PersistentPlayer persistentPlayer = new PersistentPlayer();
        persistentPlayer.setNickname("Betti");
        persistentPlayer.setOwnedLeaderCards(new HashMap<>());
        Stack<Integer>[] slots = new Stack[3];
        for (int i = 0; i < slots.length; i++)
            slots[i] = new Stack<>();
        persistentPlayer.setDevelopmentCardSlots(slots);
        persistentPlayer.setLeaderDepots(new HashMap<>());
        persistentPlayer.setFaithTrackPosition(16);
        persistentPlayer.setActive(true);
        persistentPlayer.setStrongbox(new int[]{0, 0, 0, 0});
        persistentPlayer.setPopesTileStates(new PopesTileState[]{PopesTileState.TAKEN, PopesTileState.NOT_TAKEN, PopesTileState.NOT_REACHED});
        persistentPlayer.setVictoryPoints(0);
        persistentPlayer.setWarehouse(new List[]{new ArrayList(), new ArrayList(), new ArrayList()});
        persistentPlayers.add(persistentPlayer);
        persistentGame.setPlayers(persistentPlayers);
        persistentGame.setGameMode(GameMode.SINGLE_PLAYER);
        DevelopmentCardGrid developmentCardGrid = new DevelopmentCardGrid();
        Stack<Integer>[][] developmentCardGridInteger = new Stack[3][4];
        for (int i = 0; i < developmentCardGrid.getCardGrid().length; i++){
            for (int j = 0; j < developmentCardGrid.getCardGrid()[i].length; j++){
                developmentCardGridInteger[i][j] = new Stack<>();
                for (int k = 0; k < developmentCardGrid.getCardGrid()[i][j].size(); k++){
                    developmentCardGridInteger[i][j].push(developmentCardGrid.getCardGrid()[i][j].get(k).getID());
                }
            }
        }
        persistentGame.setDevelopmentCardGrid(developmentCardGridInteger);
        Market market = new Market();
        persistentGame.setMarketTray(market.getMarketTray());
        persistentGame.setSlideMarble(market.getSlideMarble());
        persistentGame.setCurrentSection(new VaticanReportSection(5, 8, 2));

    }

    @Test
    public void testSetUp() throws InvalidArgumentException {
        PersistentGame persistentGame = new PersistentGame();
        List<PersistentPlayer> persistentPlayers = new ArrayList();
        PersistentPlayer persistentPlayer = new PersistentPlayer();
        persistentPlayer.setNickname("Betti");
        persistentPlayer.setOwnedLeaderCards(new HashMap<>());
        Stack<Integer>[] slots = new Stack[3];
        for (int i = 0; i < slots.length; i++)
            slots[i] = new Stack<>();
        persistentPlayer.setDevelopmentCardSlots(slots);
        persistentPlayer.setLeaderDepots(new HashMap<>());
        persistentPlayer.setFaithTrackPosition(16);
        persistentPlayer.setActive(true);
        persistentPlayer.setStrongbox(new int[]{0, 0, 0, 0});
        persistentPlayer.setPopesTileStates(new PopesTileState[]{PopesTileState.TAKEN, PopesTileState.NOT_TAKEN, PopesTileState.NOT_REACHED});
        persistentPlayer.setVictoryPoints(0);
        persistentPlayer.setWarehouse(new List[]{new ArrayList(), new ArrayList(), new ArrayList()});
        persistentPlayers.add(persistentPlayer);
        persistentGame.setPlayers(persistentPlayers);
        persistentGame.setGameMode(GameMode.SINGLE_PLAYER);
        DevelopmentCardGrid developmentCardGrid = new DevelopmentCardGrid();
        Stack<Integer>[][] developmentCardGridInteger = new Stack[3][4];
        for (int i = 0; i < developmentCardGrid.getCardGrid().length; i++){
            for (int j = 0; j < developmentCardGrid.getCardGrid()[i].length; j++){
                developmentCardGridInteger[i][j] = new Stack<>();
                for (int k = 0; k < developmentCardGrid.getCardGrid()[i][j].size(); k++){
                    developmentCardGridInteger[i][j].push(developmentCardGrid.getCardGrid()[i][j].get(k).getID());
                }
            }
        }
        persistentGame.setDevelopmentCardGrid(developmentCardGridInteger);
        Market market = new Market();
        persistentGame.setMarketTray(market.getMarketTray());
        persistentGame.setSlideMarble(market.getSlideMarble());
        persistentGame.setCurrentSection(new VaticanReportSection(5, 8, 2));
        Map<String, List<Resource>> resourcesToStore = new HashMap<>();
        resourcesToStore.put("Betti", new ArrayList<>());
        PersistentControllerSetUpPhase persistentControllerSetUpPhase = new PersistentControllerSetUpPhase(persistentGame, 123, new HashMap<>());
        assertEquals(persistentControllerSetUpPhase.getGame().getGameMode(), persistentGame.getGameMode());
        assertEquals(persistentControllerSetUpPhase.getGame().getSlideMarble(), persistentGame.getSlideMarble());
        assertEquals(persistentControllerSetUpPhase.getGame().getPlayers().get(0).getNickname(), persistentGame.getPlayers().get(0).getNickname());
        assertEquals(persistentControllerSetUpPhase.getResourcesToStore().size(), 0);
        for (int i = 0; i < persistentControllerSetUpPhase.getGame().getMarketTray().length; i++){
            for (int j = 0; j < persistentControllerSetUpPhase.getGame().getMarketTray()[i].length; j++){
                assertEquals(persistentControllerSetUpPhase.getGame().getMarketTray()[i][j], persistentGame.getMarketTray()[i][j]);
            }
        }

    }

    @Test
    public void testPlayPhase() throws InvalidArgumentException {
        PersistentGame persistentGame = new PersistentGame();
        List<PersistentPlayer> persistentPlayers = new ArrayList();
        PersistentPlayer persistentPlayer = new PersistentPlayer();
        persistentPlayer.setNickname("Betti");
        persistentPlayer.setOwnedLeaderCards(new HashMap<>());
        Stack<Integer>[] slots = new Stack[3];
        for (int i = 0; i < slots.length; i++)
            slots[i] = new Stack<>();
        persistentPlayer.setDevelopmentCardSlots(slots);
        persistentPlayer.setLeaderDepots(new HashMap<>());
        persistentPlayer.setFaithTrackPosition(16);
        persistentPlayer.setActive(true);
        persistentPlayer.setStrongbox(new int[]{0, 0, 0, 0});
        persistentPlayer.setPopesTileStates(new PopesTileState[]{PopesTileState.TAKEN, PopesTileState.NOT_TAKEN, PopesTileState.NOT_REACHED});
        persistentPlayer.setVictoryPoints(0);
        persistentPlayer.setWarehouse(new List[]{new ArrayList(), new ArrayList(), new ArrayList()});
        persistentPlayers.add(persistentPlayer);
        persistentGame.setPlayers(persistentPlayers);
        persistentGame.setGameMode(GameMode.SINGLE_PLAYER);
        DevelopmentCardGrid developmentCardGrid = new DevelopmentCardGrid();
        Stack<Integer>[][] developmentCardGridInteger = new Stack[3][4];
        for (int i = 0; i < developmentCardGrid.getCardGrid().length; i++){
            for (int j = 0; j < developmentCardGrid.getCardGrid()[i].length; j++){
                developmentCardGridInteger[i][j] = new Stack<>();
                for (int k = 0; k < developmentCardGrid.getCardGrid()[i][j].size(); k++){
                    developmentCardGridInteger[i][j].push(developmentCardGrid.getCardGrid()[i][j].get(k).getID());
                }
            }
        }
        persistentGame.setDevelopmentCardGrid(developmentCardGridInteger);
        Market market = new Market();
        persistentGame.setMarketTray(market.getMarketTray());
        persistentGame.setSlideMarble(market.getSlideMarble());
        persistentGame.setCurrentSection(new VaticanReportSection(5, 8, 2));


        PersistentControllerPlayPhaseSingle persistentControllerPlayPhaseSingle = new PersistentControllerPlayPhaseSingle(persistentGame, "Betti", 123, false, SoloActionTokenParser.parseTokens().stream().map(SoloActionToken::getId).collect(Collectors.toList()), 3);
        assertEquals(persistentControllerPlayPhaseSingle.getGame().getGameMode(), persistentGame.getGameMode());
        assertEquals(persistentControllerPlayPhaseSingle.getGame().getCurrentSection().getStart(), persistentGame.getCurrentSection().getStart());
        assertEquals(persistentControllerPlayPhaseSingle.getGame().getSlideMarble(), persistentGame.getSlideMarble());
        assertEquals(persistentControllerPlayPhaseSingle.getGame().getPlayers().get(0).getNickname(), persistentGame.getPlayers().get(0).getNickname());
        for (int i = 0; i < persistentControllerPlayPhaseSingle.getGame().getMarketTray().length; i++){
            for (int j = 0; j < persistentControllerPlayPhaseSingle.getGame().getMarketTray()[i].length; j++){
                assertEquals(persistentControllerPlayPhaseSingle.getGame().getMarketTray()[i][j], persistentGame.getMarketTray()[i][j]);
            }
        }
        assertFalse(persistentControllerPlayPhaseSingle.isEndTriggered());
        assertEquals(persistentControllerPlayPhaseSingle.getBlackCrossPosition(), 3);


    }

    @Test
    public void testRetrieveAnOldGame(){
        if (GameHistory.retrieveGameFromControllerId(0) != null){
            if (GameHistory.isSetUpPhase(0))
                assertTrue(GameHistory.retrieveSetUpController(0) != null);
        }
    }

}