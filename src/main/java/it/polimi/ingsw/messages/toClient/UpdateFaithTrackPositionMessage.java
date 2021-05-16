package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

public class UpdateFaithTrackPositionMessage implements MessageToClient{

    String nickname;
    int steps;

    public UpdateFaithTrackPositionMessage(String nickname, int steps) {
        this.nickname = nickname;
        this.steps = steps;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.updateFaithTrackInfo(nickname, steps);
    }
}
