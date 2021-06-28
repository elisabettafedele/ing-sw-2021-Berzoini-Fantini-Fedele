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

