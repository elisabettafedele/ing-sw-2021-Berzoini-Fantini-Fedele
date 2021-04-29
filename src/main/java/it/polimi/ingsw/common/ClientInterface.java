package it.polimi.ingsw.common;

import java.io.Serializable;

public interface ClientInterface {
    void sendMessageToServer(Serializable message);
}
