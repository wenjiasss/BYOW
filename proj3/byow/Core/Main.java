package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

/**
 * This is the main entry point for the program. This class simply parses
 * the command line inputs, and lets the byow.Core.Engine class take over
 * in either keyboard or input string mode.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Can only have two arguments - the flag and input string");
            System.exit(0);
        } else if (args.length == 2 && args[0].equals("-s")) {
            Engine engine = new Engine();
            TETile[][] t = engine.interactWithInputString(args[1]);
            //System.out.println(engine.toString());
            TERenderer ter = new TERenderer();
            ter.initialize(80, 40);
            ter.renderFrame(t, "");
        } else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}
