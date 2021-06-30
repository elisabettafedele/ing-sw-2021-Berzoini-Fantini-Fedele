package it.polimi.ingsw;

import java.io.IOException;


/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args ){


        //Audio.play_song("start");
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

