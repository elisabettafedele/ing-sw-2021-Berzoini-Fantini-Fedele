package client.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputParser {

    private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public static String getLine(){
        String line;

        do {
            try {
                //Reset buffer
                while(InputParser.input.ready())
                    InputParser.input.readLine(); //Flush
                //Wait for user
                while (!InputParser.input.ready()) {
                    Thread.sleep(200);
                }
                line = InputParser.input.readLine();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        } while ("".equals(input));
        return line;
    }

    public static Integer getInt(String errorMessage){
        String numString;
        Integer num = null;
        boolean done = false;

        try {
            do {
                //Reset buffer
                while(input.ready())
                    input.readLine(); //Flush
                //Wait for user
                while (!input.ready()) {
                    Thread.sleep(200);
                }
                numString = input.readLine();
                try {
                    num = Integer.parseInt(numString);
                    done = true;
                } catch (NumberFormatException e) {
                    System.out.print(errorMessage);
                }
            }while(!done);

        }catch (InterruptedException | IOException e){
            Thread.currentThread().interrupt();
            return null;
        }
        return num;
    }

}
