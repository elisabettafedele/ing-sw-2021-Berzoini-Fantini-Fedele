package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.InvalidPlayerAddException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.Player;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private DevelopmentCardGrid developmentCardGrid;
    private Market market;
    private GameMode gameMode;
    private List<Player> players;
    private FaithTrack faithTrack;


    /** //TODO
     * Class constructor
     * @param gameMode
     * @throws InvalidArgumentException
     * @throws UnsupportedEncodingException
     */
    public Game(GameMode gameMode) throws InvalidArgumentException, UnsupportedEncodingException {
        this.developmentCardGrid = new DevelopmentCardGrid();
        this.market = new Market();
        this.gameMode = gameMode;
        this.players = new ArrayList<Player>();
        this.faithTrack = new FaithTrack();
    }

    /**
     * The method assigns four leader cards to each player. He will have to choose just two of them.
     * @param nickname nickname of the {@link Player}
     * @param leaderCards a list containing the four leader cards
     * @throws InvalidArgumentException when the nickname of the player has already been inserted in the list of players
     * @throws InvalidMethodException when the game is SINGLE_PLAYER and someone tries to register more than one player
     */
    public void addPlayer(String nickname, List<LeaderCard> leaderCards, int initialFaithPoints, boolean hasInkwell) throws InvalidArgumentException, InvalidPlayerAddException {
        if (leaderCards == null)
            throw new NullPointerException("Leader Cards cannot be null\n");
        if (gameMode == gameMode.SINGLE_PLAYER && !players.isEmpty())
            throw new InvalidPlayerAddException("You are in SINGLE_PLAYER mode and a player is already present in the game\n");
        if (players.stream().map(Player::getNickname).collect(Collectors.toList()).contains(nickname))
            throw new InvalidArgumentException("Nickname already present in players list, the player has already received its leader cards\n");
        Player player = new Player(nickname, leaderCards);
        player.setInkwell(hasInkwell);
        player.getPersonalBoard().moveMarker(initialFaithPoints);
        players.add(player);
    }

    /**
     * @return a list containing the player in the game
     * @throws InvalidMethodException when called with gameType==SINGLE_PLAYER
     */
    public List<Player> getPlayers() throws InvalidMethodException, ZeroPlayerException {
        if (players.size() == 0)
            throw new ZeroPlayerException("No player is present in the game");
        //if (gameMode == GameMode.SINGLE_PLAYER)
           // throw new InvalidMethodException("You are in single player mode, you should use the function getSinglePlayer to get the only player\n");
        return players;
    }

    /**
     * @return the only player present in the single player game
     * @throws InvalidMethodException when called with gameType==MULTI_PLAYER
     */
    public Player getSinglePlayer() throws InvalidMethodException, ZeroPlayerException {
        if (players.size() == 0)
            throw new ZeroPlayerException("No player is present in the game");
        if (gameMode == GameMode.MULTI_PLAYER)
            throw new InvalidMethodException("You are in multi player mode, you should use the function getPlayers to get the players in the game\n");
        return players.get(0);
    }

    public GameMode getGameMode(){
        return this.gameMode;
    }

    public Player getPlayerByNickname(String nickname){
        for (Player player : players)
            if (player.getNickname().equals(nickname))
                return player;
    return null;
    }

    public DevelopmentCardGrid getDevelopmentCardGrid() {
        return developmentCardGrid;
    }

    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    public Market getMarket() {
        return market;
    }
}
