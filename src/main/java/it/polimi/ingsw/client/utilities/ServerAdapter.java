package it.polimi.ingsw.client.utilities;

import it.polimi.ingsw.messages.ConnectionMessage;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerAdapter  implements Runnable{
    private final Socket serverSocket;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    private final List<ServerObserver> observers = new ArrayList<>();


    public ServerAdapter(Socket serverSocket) {
        this.serverSocket = serverSocket;

    }

    public void addObserver(ServerObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void removeObserver(ServerObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    @Override
    public void run() {
        try {
            os = new ObjectOutputStream(serverSocket.getOutputStream());
            is = new ObjectInputStream(serverSocket.getInputStream());
            handleServerConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException  e) {
            notifyServerLost();
        }
    }

    /**
     * Listens for incoming messages from the server
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void handleServerConnection() throws IOException, ClassNotFoundException {
        while (true) {
            Object message = is.readObject();


            /* copy the list of observers in case some observers changes it from inside
             * the notification method */
            List<ServerObserver> observersCpy;
            synchronized (observers) {
                observersCpy = new ArrayList<>(observers);
            }

            for (ServerObserver observer : observersCpy) {
                observer.handleMessage(message);
            }
        }
    }

    private void notifyServerLost(){
        List<ServerObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<>(observers);
        }

        /* notify the observers */
        for (ServerObserver observer : observersCpy) {
            observer.handleMessage(ConnectionMessage.CONNECTION_CLOSED);
        }
    }


}
