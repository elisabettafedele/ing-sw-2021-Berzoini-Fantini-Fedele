package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.toClient.MessageToClient;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.game.Game;

import java.util.concurrent.BlockingQueue;


public class GameMessageManager {

    private Game game;
    private Controller controller;
    private BlockingQueue<Object> incomingPackets;
    //private final Thread packetReceiver;


    public synchronized void addMessage(MessageToServer message){
        incomingPackets.add(message);
    }

    public void manageIncomingPackets(){
        while (true){
            Object message;
            try {
                message = incomingPackets.take();
            } catch (InterruptedException e) {
                break;
            }
            handleMessage((MessageToServer) message);
        }
    }

    public void handleMessage(MessageToServer message){
        //message.handleMessage();
    }






}
