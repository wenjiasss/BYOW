package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;


public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private final Menu menu;
    private final boolean fullView = false;
    TERenderer ter = new TERenderer();
    private long SEED;
    private Random RANDOM;
    private TETile[][] tiles;
    private Avatar person;
    private String userInput;
    private boolean gameStart;

    public Engine() {
        tiles = new TETile[WIDTH][HEIGHT];
        person = new Avatar(Tileset.AVATAR, tiles);
        menu = new Menu(WIDTH, HEIGHT);
        gameStart = false;
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        //engine.interactWithKeyboard();

        // TETile[][] t = engine.interactWithInputString("N92054114S");
        //   TETile[][] t = engine.interactWithInputString("N6647S");
        TETile[][] t = engine.interactWithInputString("LWWWDDD");
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(t, "");
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
                    person = new Avatar(Tileset.FLOWER, tiles);
                } else if (c == 'r') {
                    replayLastSave();
                } else { // new game
                    drawFrame("Enter a seed ending with s: " + input + c);
                    if (c == 's') {
                        break;
                    }
                    input = input + c;
                }
            }
        }

        //game
        gameStart = true;
        input = input.toLowerCase();
        userInput = input;
        InputSource inputSource = new StringInputDevice(input);
        String seeds = "";
        while (inputSource.possibleNextInput()) {
            char c1 = inputSource.getNextKey();
            if (c1 == 'n') {
                seeds = "";
            } else if (c1 == 's') {
                break;
            } else {
                seeds = seeds + c1;
            }
        }
        SEED = Long.parseLong(seeds);
        World w = new World(seeds, WIDTH, HEIGHT);
        tiles = w.getTiles();

        String block = "";
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(tiles, block);
        tiles = person.initialize(tiles);

        input = "";
        //game start
        while (gameStart) {
            block = blockAt(ter);
            if (!fullView) {
                ter.renderFrame(getSight(), block);
            } else {
                ter.renderFrame(tiles, block);
            }
            if (StdDraw.hasNextKeyTyped()) {
                c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'w' || c == 'a' || c == 's' || c == 'd') {
                    avatarMove(c);
                    if (!fullView) {
                        ter.renderFrame(getSight(), block);
                    } else {
                        ter.renderFrame(tiles, block);
                    }
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
        userInput += input;
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
        userInput = input;
        String seeds = "";
        String movement = "";
        char firstChar = input.charAt(0);
        if (firstChar == 'l') { //load
            load(); //gets seed
            movement = input;
        } else if (firstChar == 'n') { //new game
            int seedEnd = input.indexOf('s');
            seeds = input.substring(1, seedEnd - 1);
            movement = input.substring(seedEnd);
            SEED = Long.parseLong(seeds);
        }

        World w = new World(Long.toString(SEED), WIDTH, HEIGHT);
        tiles = w.getTiles();

        String block = "";
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(tiles, block);
        tiles = person.initialize(tiles);

        String notValid = "";
        InputSource inputSource = new StringInputDevice(movement);
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            block = blockAt(ter);
            ter.renderFrame(tiles, block);
            if (c == 'w' || c == 'a' || c == 's' || c == 'd') {
                avatarMove(c);
                ter.renderFrame(tiles, block);
            } else {
                notValid = notValid + c;
            }
            if (notValid.equals(":q")) {
                saveAndQuitForInputString();
            }
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
        String[] lineArray;
        if (line != null) {
            lineArray = line.split(",");
            SEED = Long.parseLong(lineArray[0]);
            int avatarX = Integer.parseInt(lineArray[1]);
            int avatarY = Integer.parseInt(lineArray[2]);
            String savedUserInput = lineArray[3];
            //  person.changePosition(avatarX, avatarY);
            tiles = interactWithInputString(savedUserInput);
            ter.renderFrame(tiles, "");
            // tiles = person.move(); person position
        }
    }

    public void replayLastSave() {
        Path path = Paths.get("savegame.txt");
        if (!Files.exists(path)) {
            System.exit(0);
        }
        In in = new In(path.toFile());
        String line = in.readLine();
        String[] lineArray;

        if (line != null) {
            lineArray = line.split(",");
            SEED = Long.parseLong(lineArray[0]);
            int avatarX = Integer.parseInt(lineArray[1]);
            int avatarY = Integer.parseInt(lineArray[2]);
            String input = lineArray[3];
            input = input.toLowerCase();
            userInput = input;
            String seeds = "";
            String movement = "";

            char firstChar = input.charAt(0);
            if (firstChar == 'l') { //load
                load(); //gets seed
                movement = input;
            } else if (firstChar == 'n') { //new game
                int seedEnd = input.indexOf('s');
                seeds = input.substring(1, seedEnd - 1);
                movement = input.substring(seedEnd);
                SEED = Long.parseLong(seeds);
            }

            World w = new World(Long.toString(SEED), WIDTH, HEIGHT);
            tiles = w.getTiles();

            String block = "";
            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(tiles, block);
            tiles = person.initialize(tiles);

            String notValid = "";
            InputSource inputSource = new StringInputDevice(movement);
            while (inputSource.possibleNextInput()) {
                char c = inputSource.getNextKey();
                block = blockAt(ter);
                ter.renderFrame(tiles, block);
                if (c == 'w' || c == 'a' || c == 's' || c == 'd') {
                    avatarMove(c);
                    ter.renderFrame(tiles, block);
                    //  StdDraw.pause(1000);
                } else {
                    notValid = notValid + c;
                }
            }
            ter.renderFrame(tiles, "");
            interactWithKeyboard();
        }
    }

    public void saveAndQuitForInputString() {
        Out out = new Out("savegame.txt");
        //seed, avatarX, avatarY, userInput
        String saved = SEED + "," + person.getPositionX() + "," + person.getPositionY() + "," + userInput;
        out.print(saved);
    }

    public void saveAndQuit() {
        Out out = new Out("savegame.txt");
        //seed, avatarX, avatarY, userInput
        String saved = SEED + "," + person.getPositionX() + "," + person.getPositionY() + "," + userInput;
        out.print(saved);
        System.exit(0);
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        //engine.interactWithKeyboard();

        //  TETile[][] t = engine.interactWithInputString("N92054114S:Q");
        //   TETile[][] t = engine.interactWithInputString("N6647S");
        TETile[][] t = engine.interactWithInputString("LWWWDDDSSAA");
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(t, "");
    }

    //@source lab13
    public void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, HEIGHT);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    private String blockAt(TERenderer r) {
        int x = r.mouseX();
        int y = r.mouseY();

        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
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

    private void avatarMove(char c) {
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


    private TETile[][] getSight() {
        TETile[][] sight = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (!(Math.abs(person.getPosition1() - i) < 6 && Math.abs(person.getPosition2() - j) < 6)) {
                    sight[i][j] = Tileset.NOTHING;
                } else {
                    sight[i][j] = tiles[i][j];
                }
            }
        }
        return sight;
    }

}
