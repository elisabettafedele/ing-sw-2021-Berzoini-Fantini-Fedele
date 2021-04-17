package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enumerations.GameType;
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
    private GameType gameType;
    private List<Player> players;


    /** //TODO
     * Class constructor
     * @param gameType
     * @throws InvalidArgumentException
     * @throws UnsupportedEncodingException
     */
    public Game(GameType gameType) throws InvalidArgumentException, UnsupportedEncodingException {
        this.developmentCardGrid = new DevelopmentCardGrid();
        this.market = new Market();
        this.gameType = gameType;
        this.players = new ArrayList<Player>();
    }

    /**
     * The method assigns four leader cards to each player. He will have to choose just two of them.
     * @param nickname nickname of the {@link Player}
     * @param leaderCards a list containing the four leader cards
     * @throws InvalidArgumentException when the nickname of the player has already been inserted in the list of players
     * @throws InvalidMethodException when the game is SINGLE_PLAYER and someone tries to register more than one player
     */
    public void addPlayer(String nickname, List<LeaderCard> leaderCards) throws InvalidArgumentException, InvalidPlayerAddException {
        if (leaderCards == null)
            throw new NullPointerException("Leader Cards cannot be null\n");
        if (gameType == GameType.SINGLE_PLAYER && !players.isEmpty())
            throw new InvalidPlayerAddException("You are in SINGLE_PLAYER mode and a player is already present in the game\n");
        if (players.stream().map(Player::getNickname).collect(Collectors.toList()).contains(nickname))
            throw new InvalidArgumentException("Nickname already present in players list, the player has already received its leader cards\n");
        players.add(new Player(nickname, leaderCards));
    }

    /**
     * @return a list containing the player in the game
     * @throws InvalidMethodException when called with gameType==SINGLE_PLAYER
     */
    public List<Player> getPlayers() throws InvalidMethodException, ZeroPlayerException {
        if (players.size() == 0)
            throw new ZeroPlayerException("No player is present in the game");
        if (gameType == GameType.SINGLE_PLAYER)
            throw new InvalidMethodException("You are in single player mode, you should use the function getSinglePlayer to get the only player\n");
        return players;
    }

    /**
     * @return the only player present in the single player game
     * @throws InvalidMethodException when called with gameType==MULTI_PLAYER
     */
    public Player getSinglePlayer() throws InvalidMethodException, ZeroPlayerException {
        if (players.size() == 0)
            throw new ZeroPlayerException("No player is present in the game");
        if (gameType == GameType.MULTI_PLAYER)
            throw new InvalidMethodException("You are in multi player mode, you should use the function getPlayers to get the players in the game\n");
        return players.get(0);
    }

}
