package it.polimi.ingsw.messages.toClient.matchData;

import java.util.Stack;

public class LoadDevelopmentCardSlots extends MatchDataMessage{
    private Stack[] slots;

    public LoadDevelopmentCardSlots(String nickname, Stack[] slots) {
        super(nickname);
        this.slots = slots;
    }

    public Stack[] getSlots() {
        return slots;
    }
}
