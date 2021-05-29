package it.polimi.ingsw.model.persistency;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import it.polimi.ingsw.client.PopesTileState;
import it.polimi.ingsw.controller.actions.SoloActionToken;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.jsonParsers.JsonAdapter;
import it.polimi.ingsw.jsonParsers.SoloActionTokenParser;
import it.polimi.ingsw.model.game.DevelopmentCardGrid;
import it.polimi.ingsw.model.game.Market;
import it.polimi.ingsw.model.game.VaticanReportSection;
import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.*;
import java.util.stream.Collectors;

public class GameHistoryTest extends TestCase {
    PersistentGame persistentGame;

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
        JsonArray array = GameHistory.createJsonArraySetUp(persistentControllerSetUpPhase);
        Gson gson = JsonAdapter.getGsonBuilder();
        String json = gson.toJson(array);
        JsonArray jsonArray = new Gson().fromJson(json, JsonArray.class);
        PersistentControllerSetUpPhase reload = new Gson().fromJson(jsonArray.get(0).getAsJsonObject(), PersistentControllerSetUpPhase.class);
        assertEquals(reload.getGame().getGameMode(), persistentGame.getGameMode());
        assertEquals(reload.getGame().getCurrentSection().getStart(), persistentGame.getCurrentSection().getStart());
        assertEquals(reload.getGame().getSlideMarble(), persistentGame.getSlideMarble());
        assertEquals(reload.getGame().getPlayers().get(0).getNickname(), persistentGame.getPlayers().get(0).getNickname());
        assertEquals(reload.getResourcesToStore().size(), 0);
        for (int i = 0; i < reload.getGame().getMarketTray().length; i++){
            for (int j = 0; j < reload.getGame().getMarketTray()[i].length; j++){
                assertEquals(reload.getGame().getMarketTray()[i][j], persistentGame.getMarketTray()[i][j]);
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
        JsonArray array = GameHistory.createJsonArraySinglePlayer(persistentControllerPlayPhaseSingle);
        Gson gson = JsonAdapter.getGsonBuilder();
        String json = gson.toJson(array);
        JsonArray jsonArray = new Gson().fromJson(json, JsonArray.class);
        PersistentControllerPlayPhaseSingle reload = new Gson().fromJson(jsonArray.get(0).getAsJsonObject(), PersistentControllerPlayPhaseSingle.class);
        assertEquals(reload.getGame().getGameMode(), persistentGame.getGameMode());
        assertEquals(reload.getGame().getCurrentSection().getStart(), persistentGame.getCurrentSection().getStart());
        assertEquals(reload.getGame().getSlideMarble(), persistentGame.getSlideMarble());
        assertEquals(reload.getGame().getPlayers().get(0).getNickname(), persistentGame.getPlayers().get(0).getNickname());
        for (int i = 0; i < reload.getGame().getMarketTray().length; i++){
            for (int j = 0; j < reload.getGame().getMarketTray()[i].length; j++){
                assertEquals(reload.getGame().getMarketTray()[i][j], persistentGame.getMarketTray()[i][j]);
            }
        }
        assertFalse(reload.isEndTriggered());
        assertEquals(reload.getBlackCrossPosition(), 3);


    }

    @Test
    public void testRetrieveAnOldGame(){
        if (GameHistory.retrieveGameFromControllerId(0) != null){
            if (GameHistory.isSetUpPhase(0))
                assertTrue(GameHistory.retrieveSetUpController(0) != null);
        }
    }

}