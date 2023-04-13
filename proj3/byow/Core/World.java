package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.*;
import java.util.Random;

import byow.Core.RandomUtils;
import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;

public class World {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public int w;
    public int h;
    private Random RANDOM;
    private ArrayList<pair> LeftBottom = new ArrayList<>();
    private ArrayList<pair> RightTop = new ArrayList<>();

    private TETile[][] tiles;
    private int X = 0;
    private int Y = 0;


    private class pair {
        private int x;
        private int y;

        public pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }
    }

    public World(String seed, int width, int height) {
        Long s = Long.parseLong(seed);
        this.RANDOM = new Random(s);
        this.w = width;
        this.h = height;

        int numRooms = RANDOM.nextInt(10, 20);
        int numHalls = RANDOM.nextInt(30, 50);
        int curRooms = 0;
        int curHalls = 0;

        tiles = new TETile[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }

        while (curRooms <= numRooms) {
            int startx = RANDOM.nextInt(3, w - 5);
            int starty = RANDOM.nextInt(3, h - 5);
            pair start = new pair(startx, starty); // left bottom

            int endx = RANDOM.nextInt(startx + 2, w - 3);
            int endy = RANDOM.nextInt(starty + 2, h - 3);
            pair end = new pair(endx, endy); //top right

            //check for overlap
            if (!overlap(start, end)) {
                createRoom(start, end);
                LeftBottom.add(start);
                RightTop.add(end);
            }

            curRooms++;
        }
    }

    private void createRoom(pair start, pair end) {
        for (int i = start.getX() + 1; i < end.getX(); i++) {
            for (int j = start.getY() + 1; j < end.getY(); j++) {
                tiles[i][j] = Tileset.FLOOR;
            }
        }
        for (int i = start.getX(); i <= end.getX(); i++) {
            tiles[i][start.getY()] = Tileset.WALL;
            tiles[i][end.getY()] = Tileset.WALL;
        }
        for (int j = start.getY(); j <= end.getY(); j++) {
            tiles[start.getX()][j] = Tileset.WALL;
            tiles[end.getX()][j] = Tileset.WALL;
        }
    }


    //check if randomly generated room overlaps with previous room in leftbottom and rightop
    public boolean overlap(pair start, pair end) {
        if (LeftBottom != null && RightTop != null) {
            //creates rectangle for randomly generated room
            Rectangle r1 = new Rectangle(start.getX(), start.getY(), end.getX() - start.getX(), end.getY() - start.getY());
            for (int i = 0; i < LeftBottom.size(); i++) {
                pair l = LeftBottom.get(i);
                pair r = RightTop.get(i);
                int oldLength = Math.abs(l.y - r.y);
                int oldWidth = Math.abs(l.x - r.x);
                Rectangle r2 = new Rectangle(l.getX(), l.getY(), oldWidth, oldLength);
                if (r2.intersects(r1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void connectRoom() {

    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public static void main(String[] args) {
        // Change these parameters as necessary
        int a = 50;
        int b = 30;

        World knightWorld = new World("1221", a, b);

        TERenderer ter = new TERenderer();
        ter.initialize(a, b);
        ter.renderFrame(knightWorld.getTiles());

    }
}
