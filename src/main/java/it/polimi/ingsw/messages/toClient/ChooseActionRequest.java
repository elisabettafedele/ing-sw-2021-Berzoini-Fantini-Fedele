package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.ActionType;

import java.util.Map;

public class ChooseActionRequest implements  MessageToClient
{
    Map<ActionType, Boolean> executableActions;
    public ChooseActionRequest(Map<ActionType, Boolean> executableActions) {
        this.executableActions=executableActions;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseActionRequest(executableActions);
    }
}
