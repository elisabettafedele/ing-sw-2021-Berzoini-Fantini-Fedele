package it.polimi.ingsw.controller;

import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.game.Game;

import java.io.UnsupportedEncodingException;

public class Controller {
    private Game game;
    private GamePhase gamePhase;
    private int controllerID;

    public Controller(int id, GameMode gameMode) throws InvalidArgumentException, UnsupportedEncodingException {
        this.controllerID = id;
        this.game = new Game(gameMode);
        //TODO this.gamePhase = new GamePhase() link the setup phase

    }

    public Game getGame(){
        return this.game;
    }

    public GamePhase getGamePhase(){
        return this.gamePhase;
    }

}
