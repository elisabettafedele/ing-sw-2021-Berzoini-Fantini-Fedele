package it.polimi.ingsw.client.cli.specificCLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.cli.graphical.Screen;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.client.utilities.UtilityPrinter;
import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.messages.toServer.game.ChooseActionResponse;
import it.polimi.ingsw.messages.toServer.game.EndTurnRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChooseActionCLI {

    public static void displayChooseActionRequest(Client client, Map<ActionType, Boolean> executableActions, boolean standardActionDone) {
        List<String> availableActions = executableActions.keySet().stream().filter(executableActions::get).map(Enum::name).collect(Collectors.toList());
        List<String> textCommands = new ArrayList<>();
        System.out.println("Choose your next action: ");
        UtilityPrinter.printNumericList(availableActions);
        if (standardActionDone) {
            System.out.println("Otherwise, you can end your turn now, just typing \"end\"");
            textCommands.add("end");
        }
        //TODO gestire la possibilità di vedere gli altri giocatori la grid e il market prima di scegliere la action
        int selection=0;//per esempio, corrisponde al value della actionType (nella enum) scelta
        String selectionString = null;
        try {
            selectionString = InputParser.getCommandFromList(textCommands, availableActions);
        } catch (IOException e) {
            return;
        }
        if (selectionString == null)
            return;
        if (selectionString.equals("end"))
            client.sendMessageToServer(new EndTurnRequest());
        else {
            selection = ActionType.valueOf(selectionString).getValue();
            client.sendMessageToServer(new ChooseActionResponse(selection));
        }

    }
}
