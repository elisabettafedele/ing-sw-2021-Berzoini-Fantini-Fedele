package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.Map;

public class ChooseActionRequest implements MessageToClient
{
    Map<ActionType, Boolean> executableActions;
    boolean standardActionDone;
    public ChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone) {
        this.executableActions=executableActions;
        this.standardActionDone = standardActionDone;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseActionRequest(executableActions, standardActionDone);
    }
}
