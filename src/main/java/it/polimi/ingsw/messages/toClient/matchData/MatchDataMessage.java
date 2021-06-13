package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public abstract class MatchDataMessage extends MessageToClient {
    private String nickname;

    public MatchDataMessage(String nickname){
        super(false);
        this.nickname = nickname;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.update(this);
    }

    public String getNickname(){
        return nickname;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }
}
