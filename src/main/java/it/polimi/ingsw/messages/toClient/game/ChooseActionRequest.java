package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.Map;

public class ChooseActionRequest extends MessageToClient
{
    /**
     * Message used to ask the client which action he desires to perform
     */
    Map<ActionType, Boolean> executableActions;
    boolean standardActionDone;
    public ChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone) {
        super(true);
        this.executableActions=executableActions;
        this.standardActionDone = standardActionDone;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseActionRequest(executableActions, standardActionDone);
    }

    public String toString(){
        return "sending next possible actions";
    }
}
