package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.player.Player;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Controller {
    private Game game;
    private GamePhase gamePhase;
    private List<Player> players;
    private List<ClientHandler> clientHandlers;

    public Controller(GameMode gameMode) throws InvalidArgumentException, UnsupportedEncodingException {
        this.game = new Game(gameMode);
        this.clientHandlers = new LinkedList<>();
        //TODO this.gamePhase = new GamePhase() link the setup phase

    }

    public Game getGame(){
        return this.game;
    }

    public GamePhase getGamePhase(){
        return this.gamePhase;
    }

    public Player getPlayerByNickname(String nickname){
        for (Player p : players){
            if (p.getNickname().equals(nickname))
                return p;
        }
        return null;
    }

    public void addConnection(ClientHandler connection){
        this.clientHandlers.add(connection);
    }

    public void removeConnection(ClientHandler connection){
        this.clientHandlers.remove(connection);
    }

    public void startSetUp(){
        this.gamePhase = new SetUpPhase();
        this.gamePhase.executePhase(this);
    }

}
