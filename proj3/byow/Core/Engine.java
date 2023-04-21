package byow.Core;

import java.awt.*;
import java.util.Random;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import byow.Core.Avatar;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private long SEED;
    private Random RANDOM;
    private TETile[][] tiles;
    private World world;
    private Menu menu;
    private Avatar person;

    private boolean gameStart;

    public Engine() {
        tiles = new TETile[WIDTH][HEIGHT];
        person = new Avatar(Tileset.AVATAR, tiles);
        menu = new Menu(WIDTH, HEIGHT);
        gameStart = false;
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     * n = new world
     * l = load old world
     * q = quit game
     */
    public void interactWithKeyboard() {
        //menu
        menu.drawMenu();

        //random seed
        String input = "";
        char c;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'q' || c == 's' || c == 'l') {
                    drawFrame("Enter a random seed: " + input + c);
                    break;
                }
                input = input + c;
                drawFrame("Enter a random seed: " + input);
            }
        }

        //game
        gameStart = true;
        tiles = interactWithInputString(input);
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(tiles);


        //game start
        while (gameStart) {
            if (StdDraw.hasNextKeyTyped()) {
                c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'w' || c == 'a' || c == 's' || c == 'd') {
                    tiles = interactWithInputString(input);
                    ter.renderFrame(tiles);
                    break;
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        input = input.toLowerCase();
        InputSource inputSource = new StringInputDevice(input);
        String seeds = "";
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c == 'n') {
                seeds = "";
            } else if (c == 's') {
                break;
            } else {
                seeds = seeds + c;
            }
        }
        World w = new World(seeds, WIDTH, HEIGHT);
        tiles = w.getTiles();
        while (inputSource.possibleNextInput()) {
            block = showBlockAt(r);
            char c = inputSource.getNextKey();
            AvatarMove(c);
        }
        return tiles;
    }


    public TETile[][] loadGame(String input) {
        return tiles;
    }


//    public static void main(String[] args) {
//        Engine engine = new Engine();
//        engine.interactWithKeyboard();
//
//        TETile[][] t = engine.interactWithInputString("N87536S");
//        TERenderer ter = new TERenderer();
//        ter.initialize(80, 40);
//        ter.renderFrame(t);
//        }

    //@source lab13
    public void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2, s);
        StdDraw.show();
    }


    public void resetWorld() {
        tiles = new TETile[WIDTH][HEIGHT];
    }

    private String showBlockAt(TERenderer r) {
        int x = r.mouseX();
        int y = r.mouseY();
        if (x < 0 || x >= 80 || y < 0 || y >= 30) {
            return "nothing";
        }
        TETile tile = tiles[x][y];
        if (tile.equals(Tileset.FLOOR)) {
            return "floor";
        } else if (tile.equals(Tileset.WALL)) {
            return "solid walls";
        } else if (tile.equals(person.getSkin())) {
            return "yourself";
        }
        return "nothing";
    }

    private void AvatarMove(char c) {
        if (c == 'w' || c == 'W') {
            tiles = person.moveUp();
        }
        if (c == 'a' || c == 'A') {
            tiles = person.moveLeft();
        }
        if (c == 's' || c == 'S') {
            tiles = person.moveDown();
        }
        if (c == 'd' || c == 'D') {
            tiles = person.moveRight();
        }
    }

    private record pair(int x, int y) {
    }

}
