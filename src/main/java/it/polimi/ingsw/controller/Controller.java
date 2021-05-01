package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.player.Player;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private Game game;
    private GamePhase gamePhase;
    private int controllerID;
    private List<Player> players;
    private List<ClientHandler> connections;

    public Controller(int id, GameMode gameMode) throws InvalidArgumentException, UnsupportedEncodingException {
        this.controllerID = id;
        this.game = new Game(gameMode);
        this.connections = new ArrayList<>();
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
        this.connections.add(connection);
    }

}
