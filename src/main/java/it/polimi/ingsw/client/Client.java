package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.ConnectionMessage;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private final int SOCKET_TIMEOUT = 40000;
    public static final int PING_PERIOD = 80000; //PING_PERIOD = TIMEOUT/2

    private View view;

    private Socket socket;

    private final String IPAddress;
    private final int port;

    private ObjectOutputStream os;
    private ObjectInputStream is;

    private final AtomicBoolean connected = new AtomicBoolean(false);

    private final Thread pinger;

    public Client(String IPAddress, int port, View view) {
        this.IPAddress = IPAddress;
        this.port = port;
        this.view = view;
        this.pinger = new Thread(() -> {
            while (connected.get()){
                try{
                    Thread.sleep(PING_PERIOD);
                    sendMessageToServer(ConnectionMessage.PING);
                }catch (InterruptedException e){
                    break;
                }
            }
        });

    }

    public void start(){
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(IPAddress, port), SOCKET_TIMEOUT);
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeSocket();
        }

        connected.set(true);

        while(connected.get()){
            Object message = null;
            try {
                message = is.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                closeSocket();
            }
            if (message == ConnectionMessage.CONNECTION_CLOSED)
                closeSocket();
            else if(message != null && !(message == ConnectionMessage.PING)) {
                ((MessageToClient) message).handleMessage(view, this);
            }
        }

    }

    public void sendMessageToServer(Serializable message){
        if (connected.get()){
            try {
                os.writeObject(message);
                os.flush();
            } catch (IOException e) {
                closeSocket();
            }
        }
    }

    private void closeSocket(){
        connected.set(false);
        //TODO message
        try{
            is.close();
        } catch(IOException e){ }
        try{
            os.close();
        } catch(IOException e){ }
        try{
            socket.close();
        } catch(IOException e){ }
    }
}
