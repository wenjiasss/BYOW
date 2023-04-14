package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class World {
    private final ArrayList<pair> hallStart = new ArrayList<>();
    private final ArrayList<pair> hallEnd = new ArrayList<>();
    private final ArrayList<Integer> hallDirection = new ArrayList<>(); //0 = up, 2 = right
    private final int stopChance = 3;

    private final ArrayList<pair> LeftBottom = new ArrayList<>();
    private final ArrayList<pair> RightTop = new ArrayList<>();
    private final Random RANDOM;
    private TETile[][] tiles;
    /* Feel free to change the width and height. */
    public int w;
    public int h;


    public World(String seed, int width, int height) {
        Long s = Long.parseLong(seed);
        this.RANDOM = new Random(s);
        this.w = width;
        this.h = height;

        int numHalls = RANDOM.nextInt(30, 50);
        int curHalls = 0;

        tiles = new TETile[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }

        while (curHalls <= numHalls) {
            generateHall();
            curHalls++;
        }

        for (int i = 0; i < hallDirection.size(); i++) {
            generateRoom(i);
        }

        breakWalls b = new breakWalls(tiles, w, h);
        this.tiles = b.getTiles();
    }


    private void generateHall() {
        // generate start point using RANDOM
        int startx = RANDOM.nextInt(7, w - 6);
        int starty = RANDOM.nextInt(7, h - 6);
        pair start = new pair(startx, starty);
        // generate direction hallway continues using RANDOM
        int direction = RANDOM.nextInt(0, 4); // 0=up, 1=down, 2=right, 3=left
        // generate end point
        int endx = startx;
        int endy = starty;
        if (direction <= 1) {
            int lenUp = h - 5 - starty;
            int lenDown = starty - 5;
            if (lenUp >= lenDown) {
                endy = h - 5;
                direction = 0;
            } else {
                endy = 5;
                direction = 1;
            }
        } else {
            int lenRight = w - 5 - startx;
            int lenLeft = startx - 5;
            if (lenRight >= lenLeft) {
                endx = w - 5;
                direction = 2;
            } else {
                endx = 5;
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

    // HALLS FUCTIONS
    // create the hallway on tiles
    private void createHall(pair start, pair end, int direction) {
        int stop = 0;
        int x = start.x();
        int y = start.y();

        // Upward Hallways
        if (direction == 0) { // up --> same x
            int a = y + 1;
            // create start wall based on condition
            if (tiles[x][y] == Tileset.NOTHING) {
                tiles[x - 1][y] = Tileset.WALL;
                tiles[x][y] = Tileset.WALL;
                tiles[x + 1][y] = Tileset.WALL;
            } else if (tiles[x][y] == Tileset.WALL && tiles[x][y + 1] == Tileset.FLOOR) {
                tiles[x][y + 2] = Tileset.FLOOR;
                a = a + 2;
            }
            // create middle section of hallway
            for (int i = a; i < end.y(); i++) {
                if (tiles[x][i] == Tileset.WALL) {
                    stop = RANDOM.nextInt(0, stopChance); //1 = stop
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
                    } else {
                        tiles[x][i] = Tileset.FLOOR;
                    }
                    if (stop == 1) {
                        end = new pair(x, i + 2);
                        break;
                    } else {
                        tiles[x][i + 2] = Tileset.FLOOR;
                        i = i + 2;
                    }
                } else {
                    tiles[x - 1][i] = Tileset.WALL;
                    tiles[x][i] = Tileset.FLOOR;
                    tiles[x + 1][i] = Tileset.WALL;
                }
            }
            // if did not stop during the middle, create an end wall
            if (stop != 1) {
                tiles[x - 1][end.y()] = Tileset.WALL;
                tiles[x][end.y()] = Tileset.WALL;
                tiles[x + 1][end.y()] = Tileset.WALL;
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
                tiles[x + 2][y] = Tileset.FLOOR;
                a = a + 2;
            }
            // create middle section of hallway
            for (int i = a; i < end.x(); i++) {
                if (tiles[i][y] == Tileset.WALL) {
                    stop = RANDOM.nextInt(0, stopChance); //1 = stop
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
                    } else {
                        tiles[i][y] = Tileset.FLOOR;
                    }
                    if (stop == 1) {
                        end = new pair(i + 2, y);
                        break;
                    } else {
                        tiles[i + 2][y] = Tileset.FLOOR;
                        i = i + 2;
                    }
                } else {
                    tiles[i][y - 1] = Tileset.WALL;
                    tiles[i][y] = Tileset.FLOOR;
                    tiles[i][y + 1] = Tileset.WALL;
                }
            }
            // if did not stop during the middle, create an end wall
            if (stop != 1) {
                tiles[end.x()][y - 1] = Tileset.WALL;
                tiles[end.x()][y] = Tileset.WALL;
                tiles[end.x()][y + 1] = Tileset.WALL;
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
        if (direction <= 1) { // check x
            for (int i = 0; i < hallDirection.size(); i++) {
                if (hallDirection.get(i) <= 1) {
                    int p = hallStart.get(i).x();
                    if (p - 3 <= start.x() && start.x() <= p + 3) {
                        result = false;
                    }
                }
            }
        } else { // check y
            for (int i = 0; i < hallDirection.size(); i++) {
                if (hallDirection.get(i) >= 2) {
                    int p = hallStart.get(i).y();
                    if (p - 3 <= start.y() && start.y() <= p + 3) {
                        result = false;
                    }
                }
            }
        }
        return result;
    }


    // ROOM FUNCTIONS
    // generate Rooms on start and end of vertical hallways
    private void generateRoom(int index) {
        pair start = hallStart.get(index);
        pair end = hallEnd.get(index);

        int yes = RANDOM.nextInt(0, 2); // 0 = don't generate room, 1 = generate room
        if (yes == 1) {
            helper(start);
        }

        yes = RANDOM.nextInt(0, 2);
        if (yes == 1) {
            helper(end);
        }
    }

    private void helper(pair original) {
        int width = RANDOM.nextInt(1, 7);
        int height = RANDOM.nextInt(1, 7);
        int xchange;
        int ychange;


        int a1 = Math.min(w - 3 - original.x(), original.x() - 3);
        if (a1 == original.x() - 3) {
            xchange = RANDOM.nextInt(0, Math.min(a1, width));
        } else {
            xchange = width - RANDOM.nextInt(0, Math.min(a1, width));
        }

        int a2 = Math.min(h - 3 - original.y(), original.y() - 3);
        if (a2 == original.y() - 3) {
            ychange = RANDOM.nextInt(0, Math.min(a2, height));
        } else {
            ychange = height - RANDOM.nextInt(0, Math.min(a2, height));
        }

        int startx = original.x() - xchange;
        int starty = original.y() - ychange;
        pair roomStart = new pair(startx, starty); // left bottom

        int endx = startx + width;
        int endy = starty + height;
        pair roomEnd = new pair(endx, endy); // top right

        //check for overlap
        if (!overlap(roomStart, roomEnd)) {
            createRoom(roomStart, roomEnd);
            LeftBottom.add(roomStart);
            RightTop.add(roomEnd);
        }
    }


    //draws it on the tiles
    private void createRoom(pair start, pair end) {
        for (int i = start.x(); i <= end.x(); i++) {
            for (int j = start.y(); j <= end.y(); j++) {
                tiles[i][j] = Tileset.FLOOR;
            }
        }
        for (int i = start.x() - 1; i <= end.x() + 1; i++) {
            tiles[i][start.y() - 1] = Tileset.WALL;
            tiles[i][end.y() + 1] = Tileset.WALL;
        }
        for (int j = start.y() - 1; j <= end.y() + 1; j++) {
            tiles[start.x() - 1][j] = Tileset.WALL;
            tiles[end.x() + 1][j] = Tileset.WALL;
        }
    }

    //check if randomly generated room overlaps with previous room in leftbottom and rightop
    public boolean overlap(pair start, pair end) {
        if (LeftBottom != null && RightTop != null) {
            //creates rectangle for randomly generated room
            Rectangle r1 = new Rectangle(start.x(), start.y(), end.x() - start.x(), end.y() - start.y());
            for (int i = 0; i < LeftBottom.size(); i++) {
                pair l = LeftBottom.get(i);
                pair r = RightTop.get(i);
                int oldLength = Math.abs(l.y - r.y);
                int oldWidth = Math.abs(l.x - r.x);
                Rectangle r2 = new Rectangle(l.x() - 1, l.y() - 1, oldWidth + 1, oldLength + 1);
                if (r2.intersects(r1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    private record pair(int x, int y) {
    }
}
