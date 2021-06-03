package it.polimi.ingsw.messages.toServer.lobby;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.messages.toClient.lobby.NicknameRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;

import java.util.regex.Pattern;

public class NicknameResponse implements MessageToServer {
    private final String nickname;
    private static final String NICKNAME_REGEXP = "^([a-zA-Z0-9._\\-]{1,20})$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEXP);

    public NicknameResponse(String nickname){
        this.nickname = nickname;
    }

    public String getNickname(){
        return nickname;
    }

    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler){
        if (nickname == null || clientHandler.getClientHandlerPhase() != ClientHandlerPhase.WAITING_NICKNAME)
            return;
        else if (!NICKNAME_PATTERN.matcher(nickname).matches()){
            clientHandler.sendMessageToClient(new NicknameRequest(true, false));
            return;
        }
        //The nickname is valid, the server will make another check later
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_IN_THE_LOBBY);
        clientHandler.setNickname(nickname);
    }

    public String toString(){
        return "received nickname";
    }


}
