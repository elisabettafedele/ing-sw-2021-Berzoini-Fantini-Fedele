package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

public class WelcomeBackMessage implements MessageToClient{
    private String nickname;
    private boolean gameFinished;


    public WelcomeBackMessage(String nickname, boolean gameFinished) {
        this.nickname = nickname;
        this.gameFinished = gameFinished;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayWelcomeBackMessage(nickname, gameFinished);
    }

    public String toString(){
        return "sending welcome back message" + (gameFinished ? " and notifying that the game is finished" : "");
    }
}
