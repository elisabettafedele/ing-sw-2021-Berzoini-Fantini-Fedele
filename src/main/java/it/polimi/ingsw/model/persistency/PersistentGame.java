package it.polimi.ingsw.model.persistency;

import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.VaticanReportSection;
import it.polimi.ingsw.model.player.Player;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PersistentGame implements Serializable {
    private GameMode gameMode;
    private Marble[][] marketTray;
    private Marble slideMarble;
    private Stack<Integer>[][] developmentCardGrid;
    private List<PersistentPlayer> players;
    private VaticanReportSection currentSection;

    /**
     * Constructor of a light version of the {@link Game} used to save the status of the game and to retrieve it when needed
     * @param game the {@link Game} to be converted
     */
    public PersistentGame(Game game){
        gameMode = game.getGameMode();
        marketTray = new Marble[3][4];
        for (int i = 0; i < marketTray.length; i++)
            for(int j = 0; j < marketTray[i].length; j++)
                marketTray[i][j] = game.getMarket().getMarketTray()[i][j];
        slideMarble = game.getMarket().getSlideMarble();
        developmentCardGrid = new Stack[3][4];
        for (int i = 0; i < game.getDevelopmentCardGrid().getCardGrid().length; i++){
            for (int j = 0; j < game.getDevelopmentCardGrid().getCardGrid()[i].length; j++){
                developmentCardGrid[i][j] = new Stack<>();
                for (int k = 0; k < game.getDevelopmentCardGrid().getCardGrid()[i][j].size(); k++){
                    developmentCardGrid[i][j].push(game.getDevelopmentCardGrid().getCardGrid()[i][j].get(k).getID());
                }
            }
        }

        players = new ArrayList<>();
        try {
            for (Player player : game.getPlayers()){
                players.add(new PersistentPlayer(player));
            }
        } catch (InvalidMethodException | ZeroPlayerException e) {
            e.printStackTrace();
        }

        currentSection = game.getFaithTrack().getCurrentSection(false);
    }

    public PersistentGame (){ }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Marble[][] getMarketTray() {
        return marketTray;
    }

    public Marble getSlideMarble() {
        return slideMarble;
    }

    public Stack<Integer>[][] getDevelopmentCardGrid() {
        return developmentCardGrid;
    }

    public List<PersistentPlayer> getPlayers() {
        return players;
    }

    public VaticanReportSection getCurrentSection() {
        return currentSection;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setMarketTray(Marble[][] marketTray) {
        this.marketTray = marketTray;
    }

    public void setSlideMarble(Marble slideMarble) {
        this.slideMarble = slideMarble;
    }

    public void setDevelopmentCardGrid(Stack<Integer>[][] developmentCardGrid) {
        this.developmentCardGrid = developmentCardGrid;
    }

    public void setPlayers(List<PersistentPlayer> players) {
        this.players = players;
    }

    public void setCurrentSection(VaticanReportSection currentSection) {
        this.currentSection = currentSection;
    }
}
