package it.polimi.ingsw.client.cli.graphical;

/**
 * Class and static method to print the logo at the beginning of the game
 */
public class GraphicalLogo {
    public static void printLogo(){
        System.out.println("\033[H\033[2J");
        System.out.println("\033[H\033[3J");
        System.out.println(Colour.ANSI_BLUE.getCode() + "███╗   ███╗ █████╗ ███████╗████████╗███████╗██████╗      ██████╗ ███████╗    ██████╗ ███████╗███╗   ██╗ █████╗ ██╗███████╗███████╗ █████╗ ███╗   ██╗ ██████╗███████╗    \n" +
                "████╗ ████║██╔══██╗██╔════╝╚══██╔══╝██╔════╝██╔══██╗    ██╔═══██╗██╔════╝    ██╔══██╗██╔════╝████╗  ██║██╔══██╗██║██╔════╝██╔════╝██╔══██╗████╗  ██║██╔════╝██╔════╝    \n" +
                "██╔████╔██║███████║███████╗   ██║   █████╗  ██████╔╝    ██║   ██║█████╗      ██████╔╝█████╗  ██╔██╗ ██║███████║██║███████╗███████╗███████║██╔██╗ ██║██║     █████╗      \n" +
                "██║╚██╔╝██║██╔══██║╚════██║   ██║   ██╔══╝  ██╔══██╗    ██║   ██║██╔══╝      ██╔══██╗██╔══╝  ██║╚██╗██║██╔══██║██║╚════██║╚════██║██╔══██║██║╚██╗██║██║     ██╔══╝      \n" +
                "██║ ╚═╝ ██║██║  ██║███████║   ██║   ███████╗██║  ██║    ╚██████╔╝██║         ██║  ██║███████╗██║ ╚████║██║  ██║██║███████║███████║██║  ██║██║ ╚████║╚██████╗███████╗    \n" +
                "╚═╝     ╚═╝╚═╝  ╚═╝╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝     ╚═════╝ ╚═╝         ╚═╝  ╚═╝╚══════╝╚═╝  ╚═══╝╚═╝  ╚═╝╚═╝╚══════╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝╚══════╝    \n" +
                "                                                                                                                                                                        " + Colour.ANSI_BLUE.getCode() + "\nWelcome to Master Of Renaissance Board-Game \nThis video-game adaption was created by Raffaele Berzoini, Elia Fantini and Elisabetta Fedele\n\n" + Colour.ANSI_RESET +
                "Before starting playing you need to setup some things:\n" );
    }
}
