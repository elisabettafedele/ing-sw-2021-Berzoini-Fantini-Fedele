package Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerMain {
    private static final int DEFAULT_PORT = 1234;

    public static void main(String[] args){
        int port = DEFAULT_PORT;
        List<String> arguments = new ArrayList<String>(Arrays.asList(args));
        Server server = new Server(port);
        server.startServer();
    }
}
