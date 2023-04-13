package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import java.util.*;

public class World {
    /* Feel free to change the width and height. */
    public int w;
    public int h;
    TERenderer ter = new TERenderer();
    private final Random RANDOM;
    private final ArrayList<pair> hallStart = new ArrayList<>();
    private final ArrayList<pair> hallEnd = new ArrayList<>();
    private final ArrayList<Integer> hallDirection = new ArrayList<>();

    private final ArrayList<pair> LeftBottom = new ArrayList<>();
    private final ArrayList<pair> RightTop = new ArrayList<>();
    private final ArrayList<pair> Center = new ArrayList<>();


    private final TETile[][] tiles;
    private final int X = 0;
    private final int Y = 0;


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

        int numRooms = RANDOM.nextInt(5, 15);
        int numHalls = RANDOM.nextInt(30, 50);
        int curRooms = 0;
        int curHalls = 0;

        tiles = new TETile[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }

<<<<<<< HEAD
        while (curHalls <= numHalls) {
            generateHall();
            curHalls++;
        }





        /*while (curRooms <= numRooms) {
            int startx = RANDOM.nextInt(3, w-5);
            int starty = RANDOM.nextInt(3, h-5);
=======
        while (curRooms <= numRooms) {
            int startx = RANDOM.nextInt(3, w - 5);
            int starty = RANDOM.nextInt(3, h - 5);
>>>>>>> a4d4457a256b80af1f63ebfc1a16f7e6b323e77d
            pair start = new pair(startx, starty); // left bottom

            int endx = RANDOM.nextInt(startx + 2, w - 3);
            int endy = RANDOM.nextInt(starty + 2, h - 3);
            pair end = new pair(endx, endy); //top right

            int centerx = (startx + endx) / 2;
            int centery = (starty + endy) / 2;
            pair center = new pair(centerx, centery); //center

            //check for overlap
            if (!overlap(start, end)) {
                createRoom(start, end);
                LeftBottom.add(start);
                RightTop.add(end);
                Center.add(center);
            }

            curRooms++;
        }*/
    }

    private void generateHall() {
        // generate start point using RANDOM
        int startx = RANDOM.nextInt(5, w - 4);
        int starty = RANDOM.nextInt(5, h - 4);
        pair start = new pair(startx, starty);
        // generate direction hallway continues using RANDOM
        int direction = RANDOM.nextInt(0, 4); // 0=up, 1=down, 2=right, 3=left
        // generate end point
        int endx = startx;
        int endy = starty;
        if (direction <= 1) {
            int lenUp = h - 3 - starty;
            int lenDown = starty - 3;
            if (lenUp >= lenDown) {
                endy = h - 3;
                direction = 0;
            } else {
                endy = 3;
                direction = 1;
            }
        } else if (direction >= 2) {
            int lenRight = w - 3 - startx;
            int lenLeft = startx - 3;
            if (lenRight >= lenLeft) {
                endx = w - 3;
                direction = 2;
            } else {
                endx = 3;
                direction = 3;
            }
        }
        pair end = new pair(endx, endy);
        // Turn downward/leftward hallways into upward/rightward hallways
        if (direction == 1 || direction == 3) {
            pair a = start;
            start = end;
            end = a;
            direction = direction - 1;
        }
        // create this hallway on tiles if this hallway is seperate from other hallways
        if (seperateHall(start, direction)) {
            createHall(start, end, direction);
        }
    }

    // create the hallway on tiles
    private void createHall(pair start, pair end, int direction) {
        int stop = 0;
        int x = start.getX();
        int y = start.getY();

        // Upward Hallways
        if (direction == 0) { // up --> same x
            int a = y + 1;
            // create start wall based on condition
            if (tiles[x][y] == Tileset.NOTHING) {
                tiles[x - 1][y] = Tileset.WALL;
                tiles[x][y] = Tileset.WALL;
                tiles[x + 1][y] = Tileset.WALL;
            } else if (tiles[x][y] == Tileset.WALL && tiles[x][y + 1] == Tileset.FLOOR) {
                a = y + 2;
            }
            // create middle section of hallway
            for (int i = a; i < end.getY(); i++) {
                if (tiles[x][i] == Tileset.WALL) {
                    stop = RANDOM.nextInt(0, 5); //1 = stop
                    if (tiles[x][i + 1] == Tileset.WALL) {
                        if (tiles[x - 1][i] == Tileset.NOTHING) {
                            tiles[x - 1][i] = Tileset.WALL;
                            tiles[x - 1][i + 1] = Tileset.WALL;
                            tiles[x - 1][i + 2] = Tileset.WALL;
                        } else if (tiles[x + 1][i] == Tileset.NOTHING) {
                            tiles[x + 1][i] = Tileset.WALL;
                            tiles[x + 1][i + 1] = Tileset.WALL;
                            tiles[x + 1][i + 2] = Tileset.WALL;
                        }
                        tiles[x][i] = Tileset.FLOOR;
                        tiles[x][i + 1] = Tileset.FLOOR;
                        if (stop == 1) {
                            break;
                        } else {
                            tiles[x][i + 2] = Tileset.FLOOR;
                            i = i + 2;
                        }
                    } else {
                        tiles[x][i] = Tileset.FLOOR;
                        if (stop == 1) {
                            break;
                        } else {
                            tiles[x][i + 2] = Tileset.FLOOR;
                            i = i + 2;
                        }
                    }
                } else {
                    tiles[x - 1][i] = Tileset.WALL;
                    tiles[x][i] = Tileset.FLOOR;
                    tiles[x + 1][i] = Tileset.WALL;
                }
            }
            // if did not stop during the middle, create an end wall
            if (stop != 1) {
                tiles[x - 1][end.getY()] = Tileset.WALL;
                tiles[x][end.getY()] = Tileset.WALL;
                tiles[x + 1][end.getY()] = Tileset.WALL;
            }
        }

        // Rightward Hallways
        else if (direction == 2) { // right --> same y
            int a = x + 1;
            // create start wall based on condition
            if (tiles[x][y] == Tileset.NOTHING) {
                tiles[x][y - 1] = Tileset.WALL;
                tiles[x][y] = Tileset.WALL;
                tiles[x][y + 1] = Tileset.WALL;
            } else if (tiles[x][y] == Tileset.WALL && tiles[x + 1][y] == Tileset.FLOOR) {
                a = a + 1;
            }
            // create middle section of hallway
            for (int i = a; i < end.getX(); i++) {
                if (tiles[i][y] == Tileset.WALL) {
                    stop = RANDOM.nextInt(0, 5); //1 = stop
                    if (tiles[i + 1][y] == Tileset.WALL) {
                        if (tiles[i][y - 1] == Tileset.NOTHING) {
                            tiles[i][y - 1] = Tileset.WALL;
                            tiles[i + 1][y - 1] = Tileset.WALL;
                            tiles[i + 2][y - 1] = Tileset.WALL;
                        } else if (tiles[i][y + 1] == Tileset.NOTHING) {
                            tiles[i][y + 1] = Tileset.WALL;
                            tiles[i + 1][y + 1] = Tileset.WALL;
                            tiles[i + 2][y + 1] = Tileset.WALL;
                        }
                        tiles[i][y] = Tileset.FLOOR;
                        tiles[i + 1][y] = Tileset.FLOOR;
                        if (stop == 1) {
                            break;
                        } else {
                            tiles[i + 2][y] = Tileset.FLOOR;
                            i = i + 2;
                        }
                    } else {
                        tiles[i][y] = Tileset.FLOOR;
                        if (stop == 1) {
                            break;
                        } else {
                            tiles[i + 2][y] = Tileset.FLOOR;
                            i = i + 2;
                        }
                    }
                } else {
                    tiles[i][y - 1] = Tileset.WALL;
                    tiles[i][y] = Tileset.FLOOR;
                    tiles[i][y + 1] = Tileset.WALL;
                }
            }
            // if did not stop during the middle, create an end wall
            if (stop != 1) {
                tiles[end.getX()][y - 1] = Tileset.WALL;
                tiles[end.getX()][y] = Tileset.WALL;
                tiles[end.getX()][y + 1] = Tileset.WALL;
            }
        }
        // add parameters into ArrayLists
        hallStart.add(start);
        hallEnd.add(end);
        hallDirection.add(direction);
    }

    // Check if this hallway is seperate from all existing hallways that go in same direction
    private boolean seperateHall(pair start, int direction) {
        boolean result = true;
        if (direction == 0) { // check x
            for (int i = 0; i < hallDirection.size(); i++) {
                if (hallDirection.get(i) == 0) {
                    int p = hallStart.get(i).getX();
                    if (p - 3 <= start.getX() && start.getX() <= p + 3) {
                        result = false;
                    }
                }
            }
        } else if (direction == 2) { // check y
            for (int i = 0; i < hallDirection.size(); i++) {
                if (hallDirection.get(i) == 2) {
                    int p = hallStart.get(i).getY();
                    if (p - 3 <= start.getY() && start.getY() <= p + 3) {
                        result = false;
                    }
                }
            }
        }
        return result;
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
