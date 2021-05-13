package it.polimi.ingsw.client.cli.graphical;

public class GraphicalCard {

    public static final int CardWidth = 20;
    public static final int CardHeight = 10;

    private GraphicalDevelopmentCardGrid graphicalGrid;
    private int ID;
    private String description;

    public GraphicalCard(GraphicalDevelopmentCardGrid graphicalGrid, String description) {
        this.graphicalGrid = graphicalGrid;
        this.description = description;
        parseDescription();
    }

    private void parseDescription() {

    }

    public void drawOnScreen(int x_coord, int y_coord) {
        for (int i = 0; i < CardHeight; ++i) {
            for (int j = 0; j < CardWidth; ++j) {
                if (i == 0 && j == 0) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_RED, '╭');
                else if (i == 0 && j == CardWidth - 1) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_RED, '╮');
                else if (i == CardHeight - 1 && j == 0) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_RED, '╰');
                else if (i == CardHeight - 1 && j == CardWidth - 1) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_RED, '╯');
                //else graphicalGrid.addPixel(i + x_coord, j + y_coord);
            }
        }
    }
}