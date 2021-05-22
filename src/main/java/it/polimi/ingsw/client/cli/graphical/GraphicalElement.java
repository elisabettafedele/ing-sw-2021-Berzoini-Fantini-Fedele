package it.polimi.ingsw.client.cli.graphical;

public abstract class GraphicalElement {
    int width;
    int height;

    final char[][] screen;
    final Colour[][] colours;
    final BackColour[][] backGroundColours;

    public GraphicalElement(int width, int height) {
        this.width = width;
        this.height = height;
        this.screen = new char[height][width];
        this.colours = new Colour[height][width];
        this.backGroundColours = new BackColour[height][width];
    }

    void display(){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + screen[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    private void reset(){
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                screen[i][j] = ' ';
                colours[i][j] = Colour.ANSI_DEFAULT;
                backGroundColours[i][j] = BackColour.ANSI_DEFAULT;
            }
        }
    }
}
