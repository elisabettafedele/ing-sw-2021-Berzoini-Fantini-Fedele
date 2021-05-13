package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.common.LightDevelopmentCard;

public class GraphicalCard {

    public static final int CardWidth = 15;
    public static final int CardHeight = 8;

    private GraphicalDevelopmentCardGrid graphicalGrid;
    private int ID;
    private LightDevelopmentCard lightDevelopmentCard;

    public GraphicalCard(GraphicalDevelopmentCardGrid graphicalGrid, LightDevelopmentCard ldc) {
        this.graphicalGrid = graphicalGrid;
        this.lightDevelopmentCard = ldc;
        parseDescription();
    }

    private void parseDescription() {

    }

    public void drawOnScreen(int x_coord, int y_coord) {
        int h_line = 196;
        for (int i = 0; i < CardHeight; i++) {
            for (int j = 0; j < CardWidth; j++) {
                if (i == 0 && j == 0) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '╭');
                else if (i == 0 && j == CardWidth - 1) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '╮');
                else if (i == CardHeight - 1 && j == 0) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '╰');
                else if (i == CardHeight - 1 && j == CardWidth - 1) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '╯');
                //else if (i == 0 || i == CardHeight - 1) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_RED, (char) 95);
            }
        }
        //graphicalGrid.addPixel(x_coord+(CardHeight)/2, y_coord+(CardWidth)/2, Colour.ANSI_BRIGHT_RED, '@');
        Colour flagColor = getColor(lightDevelopmentCard.getFlagColor());
        graphicalGrid.addPixel(x_coord, y_coord+1, flagColor,'#');
    }

    private Colour getColor(String flagColor) {
        if(flagColor == "YELLOW"){
            return Colour.ANSI_BRIGHT_YELLOW;
        }else if(flagColor == "BLUE"){
            return Colour.ANSI_BRIGHT_BLUE;
        }else if(flagColor == "PURPLE"){
            return Colour.ANSI_BRIGHT_PURPLE;
        }else if(flagColor == "GREEN"){
            return Colour.ANSI_BRIGHT_GREEN;
        }
        return Colour.ANSI_BRIGHT_WHITE;
    }
}