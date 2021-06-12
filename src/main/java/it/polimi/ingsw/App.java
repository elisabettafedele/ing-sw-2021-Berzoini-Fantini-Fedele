package it.polimi.ingsw;

import it.polimi.ingsw.enumerations.Resource;

import java.io.IOException;
import java.util.*;


/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args ){
        Stack<Integer> stack = new Stack<>();

        stack.push(5);
        stack.push(3);
        stack.push(9);
        for(int i = 0; i < stack.size(); i++){
            System.out.println(stack.get(i));
        }
        System.out.println(stack);

        clrscr();
        String str = "\uD83C\uDD73\uD83C\uDD74\uD83C\uDD85\uD83C\uDD74\uD83C\uDD7B\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD7C\uD83C\uDD74\uD83C\uDD7D\uD83C\uDD83 \uD83C\uDD72\uD83C\uDD70\uD83C\uDD81\uD83C\uDD73 \uD83C\uDD76\uD83C\uDD81\uD83C\uDD78\uD83C\uDD73";
        char[] strr = new char[str.length()];
        //System.out.println("\uD83C\uDD73\uD83C\uDD74\uD83C\uDD85\uD83C\uDD74\uD83C\uDD7B\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD7C\uD83C\uDD74\uD83C\uDD7D\uD83C\uDD83 \uD83C\uDD72\uD83C\uDD70\uD83C\uDD81\uD83C\uDD73 \uD83C\uDD76\uD83C\uDD81\uD83C\uDD78\uD83C\uDD73");
        for(int i = 0; i < str.length(); i++){
            strr[i] = str.charAt(i);
            System.out.print(strr[i]);
        }

        for(Resource r : Resource.realValues()){
            System.out.println(r.toString());
        }
    }

    public static void clrscr(){
        //Clears Screen in java
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }
}

