package it.polimi.ingsw.client.cli.graphical;

public abstract class GraphicalElement {
    int width;
    int height;

    final char[][] symbols;
    final Colour[][] colours;
    final BackColour[][] backGroundColours;

    public GraphicalElement(int width, int height) {
        this.width = width;
        this.height = height;
        this.symbols = new char[height][width];
        this.colours = new Colour[height][width];
        this.backGroundColours = new BackColour[height][width];
    }

    void display(){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + symbols[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    protected void reset(){
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                symbols[i][j] = ' ';
                colours[i][j] = Colour.ANSI_DEFAULT;
                backGroundColours[i][j] = BackColour.ANSI_DEFAULT;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char[][] getSymbols() {
        return symbols;
    }

    public Colour[][] getColours() {
        return colours;
    }

    public BackColour[][] getBackGroundColours() {
        return backGroundColours;
    }

    protected void drawEdges(int h, int w){
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (i == 0 && j == 0) {
                    symbols[i][j] = '╔';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i == 0 && j == w - 1) {
                    symbols[i][j] = '╗';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if ((i == 0 || i == h - 1) && j > 0 && j < w -1)
                {
                    symbols[i][j] = '═';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i == h - 1 && j == 0) {
                    symbols[i][j] = '╚';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i == h - 1 && j == w - 1) {
                    symbols[i][j] = '╝';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i > 0 && i < h-1 && (j == 0 || j == w - 1)) {
                    symbols[i][j] = '║';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
            }
        }
    }
}
