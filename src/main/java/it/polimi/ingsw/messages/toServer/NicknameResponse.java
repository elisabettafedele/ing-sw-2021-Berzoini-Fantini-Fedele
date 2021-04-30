package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.messages.toClient.NicknameRequest;

import java.util.logging.Level;
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

    public void handleMessage(Server server, ClientHandler clientHandler){
        if (nickname == null || clientHandler.getClientHandlerPhase() != ClientHandlerPhase.WAITING_NICKNAME)
            return;
        else if (!NICKNAME_PATTERN.matcher(nickname).matches()){
            clientHandler.sendMessageToClient(new NicknameRequest(true, false));
            return;
        }
        //The nickname is valid, the server will make another check later
        clientHandler.setNickname(nickname);
        // TODO insert the port of the client in the log message
        Server.SERVER_LOGGER.log(Level.INFO, "New message from client that has chosen his nickname: "+ nickname);
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_IN_THE_LOBBY);
        server.handleNicknameChoice(clientHandler);
    }

}
