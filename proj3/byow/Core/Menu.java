package byow.Core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class Menu {
    private int width;
    private int height;

    public Menu(int width, int height) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        //title
        Font fontBig = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.width / 2, this.height / 2, "The Game");

        //menu
        Font fontSmall = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontSmall);
        StdDraw.text(this.width / 2, this.height / 2 + 10, " \"New Game (N)\" \"Load Game (L)\" \"Quit (Q)\"");

        StdDraw.show();
    }
}
