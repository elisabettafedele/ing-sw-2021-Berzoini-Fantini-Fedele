package it.polimi.ingsw.client.utilities;

import it.polimi.ingsw.client.cli.CLI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Predicate;

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

    public static String getString(String errorMessage, Predicate<String> condition){
        String line;
        do{
            line = getLine();
            if (condition.test(line))
                return line;
            else
                System.out.println(errorMessage);
        } while (true);
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
                    done=true;
                } catch (NumberFormatException e) {
                    System.out.println(errorMessage);
                }
            }while(!done);

        }catch (InterruptedException | IOException e){
            Thread.currentThread().interrupt();
            return null;
        }
        return num;
    }

    public static Integer getInt(String errorMessage, Predicate<Integer> condition){
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
                    if (condition.test(num)) {
                        done = true;
                    }else{
                        System.out.println(errorMessage);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(errorMessage);
                }
            }while(!done);

        }catch (InterruptedException | IOException e){
            Thread.currentThread().interrupt();
            return null;
        }
        return num;
    }

    public static String getCommandFromList(List<String> commands){
        return commands.get(getInt("Please insert a valid command", CLI.conditionOnIntegerRange(1, commands.size()))-1);
    }

    public static String getCommandFromList(List<String> textCommands, List<String> intCommands){
        return getCommand(CLI.conditionOnIntegerRange(1, intCommands.size()), textCommands, intCommands, "Please insert a valid command");
    }

    public static String getCommand(Predicate integerPredicate, List<String> textCommands, List<String> intCommands, String errorMessage){
        if (textCommands.isEmpty())
            return getCommandFromList(intCommands);
        while (true) {
            String command = getLine();
            if (textCommands.contains(command))
                return command;
            try {
                if (integerPredicate.test(Integer.parseInt(command)))
                    return intCommands.get(Integer.parseInt(command) - 1);
                else
                    System.out.println(errorMessage);
            } catch (NumberFormatException e){ System.out.println(errorMessage);}
            //finally {
             //   System.out.println(errorMessage);
            //}
        }
    }

}
