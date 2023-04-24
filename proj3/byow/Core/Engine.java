package byow.Core;

import java.awt.*;
import java.io.BufferedReader;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.In;


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
    public static final TETile PERSON = new TETile('@', Color.white, Color.black, "you", Paths.get("byow", "img", "person.png").toString());

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
                if (c == 'q') { //quit
                    System.exit(0);
                } else if (c == 'l') { //load
                    load();
                } else if (c == 'c') { //change avatar

                } else { // new game
                    drawFrame("Enter a seed ending with s: " + input);
                    if (c == 's') {
                        break;
                    }
                    input = input + c;
                }
            }
        }

        //game
        gameStart = true;
        tiles = interactWithInputString(input);
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(tiles);

        input = "";
        //game start
        while (gameStart) {
            if (StdDraw.hasNextKeyTyped()) {
                c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'w' || c == 'a' || c == 's' || c == 'd') {
                    // AvatarMove(c);
                    ter.renderFrame(tiles);
                } else if (c == 'l') {
                    load();
                } else {
                    input += c;
                }
                if (input.equals(":q")) {
                    saveAndQuit();
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

        //String block = ""；
        //TERenderer r = new TERenderer();
        //r.initialize(82, 32, 1, 1);
        //r.renderFrame(...);
        person.updateTiles(tiles);
        person.initialize();
        tiles = person.getTiles();
        while (inputSource.possibleNextInput()) {
            //block = blockAt(r);
            //r.renderFrame1(...);
            char c = inputSource.getNextKey();
            AvatarMove(c);
        }
        return tiles;
    }


    public void load() {
        Path path = Paths.get("savegame.txt");

        if (!Files.exists(path)) {
            System.exit(0);
        }
        In in = new In(path.toFile());
        String line = in.readLine();
        String[] input = line.split(",");
        if (line != null) {
            input = line.split(",");
            //   SEED = Long.parseLong(input[1]); // seed
            tiles = interactWithInputString(input[1]);
            ter.renderFrame(tiles);
            // tiles = person.move(); person position
        }

    }


    public void saveAndQuit() {
        Out out = new Out("savegame.txt");
        //seed, avatarX, avatarY
        out.print("1267,20,30");
        // out.print(SEED + "" + "," + "person.getPositionX()" + "," + "person.getPositionY()");

        System.exit(0);
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        //engine.interactWithKeyboard();

        TETile[][] t = engine.interactWithInputString("N92054114S");
        TERenderer ter = new TERenderer();
        ter.initialize(80, 40);
        ter.renderFrame(t);
    }

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

    //doesn't work
    public void displayHUD() {
        StdDraw.clear(Color.BLACK);
        //      StdDraw.filledRectangle(WIDTH, HEIGHT, WIDTH - 10, HEIGHT - 10);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontBig);
        // String s = "Tile: " + blockAt(ter);
        String s = "Tile: ";
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2, s);
        StdDraw.show();
    }

    private String blockAt(TERenderer r) {
        int x = r.mouseX();
        int y = r.mouseY();

//        int x = (int) StdDraw.mouseX();
//        int y = (int) StdDraw.mouseY();

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

    public void resetWorld() {
        tiles = new TETile[WIDTH][HEIGHT];
    }

    private record pair(int x, int y) {
    }

}
