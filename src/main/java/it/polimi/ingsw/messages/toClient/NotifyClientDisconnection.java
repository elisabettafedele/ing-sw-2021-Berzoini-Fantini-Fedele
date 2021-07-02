package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.ViewInterface;

/**
 * Message to notify the disconnection of a player
 */
public class NotifyClientDisconnection extends MessageToClient{
    private String nickname;
    private boolean setUp;
    private boolean gameCancelled;


    public NotifyClientDisconnection(String nickname, boolean setUp, boolean gameCancelled) {
        super(false);
        this.setUp = setUp;
        this.gameCancelled = gameCancelled;
        this.nickname = nickname;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayDisconnection(nickname, setUp, gameCancelled);
    }

    public boolean isGameCancelled() {
        return gameCancelled;
    }

    public String toString(){
        return "notifying the disconnection of " + nickname + (gameCancelled ? " and informing that the game has been cancelled" : "");
    }
}
