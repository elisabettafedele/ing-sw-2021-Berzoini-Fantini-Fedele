package it.polimi.ingsw.client.utilities;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public interface ServerObserver{
    void handleMessage(Object message);
}
