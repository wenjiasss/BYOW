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

    private final ArrayList<pair> LeftBottom = new ArrayList<>();
    private final ArrayList<pair> RightTop = new ArrayList<>();

    /* Feel free to change the width and height. */
    public int w;
    public int h;

    TERenderer ter = new TERenderer();
    private final Random RANDOM;
    private final TETile[][] tiles;


    public World(String seed, int width, int height) {
        long s = Long.parseLong(seed);
        this.RANDOM = new Random(s);
        this.w = width;
        this.h = height;

        int numHalls = RANDOM.nextInt(20, 30);
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

        breakWalls();
        breakWalls();
        breakWalls2();
    }

    public static void main(String[] args) {
        // Change these parameters as necessary
        int a = 80;
        int b = 30;

        World knightWorld = new World("1111", a, b);

        TERenderer ter = new TERenderer();
        ter.initialize(a, b);
        ter.renderFrame(knightWorld.getTiles());
    }

    private void breakWalls() {
        for (int i = 0; i < w - 2; i++) {
            for (int j = 0; j < h - 2; j++) {
                if (tiles[i][j] == Tileset.FLOOR && tiles[i][j + 1] == Tileset.WALL && tiles[i][j + 2] == Tileset.FLOOR) {
                    tiles[i][j + 1] = Tileset.FLOOR;
                }
                if (tiles[i][j] == Tileset.FLOOR && tiles[i][j + 1] == Tileset.WALL && tiles[i][j + 2] == Tileset.WALL && tiles[i][j + 3] == Tileset.FLOOR) {
                    tiles[i][j + 1] = Tileset.FLOOR;
                    tiles[i][j + 2] = Tileset.FLOOR;
                }
                if (tiles[i][j] == Tileset.FLOOR && tiles[i + 1][j] == Tileset.WALL && tiles[i + 2][j] == Tileset.FLOOR) {
                    tiles[i + 1][j] = Tileset.FLOOR;
                }
                if (tiles[i][j] == Tileset.FLOOR && tiles[i + 1][j] == Tileset.WALL && tiles[i + 2][j] == Tileset.WALL && tiles[i + 3][j] == Tileset.FLOOR) {
                    tiles[i + 1][j] = Tileset.FLOOR;
                    tiles[i + 2][j] = Tileset.FLOOR;
                }
            }
        }
    }

    private void breakWalls2() {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (tiles[i][j] == Tileset.FLOOR) {
                    pair p1 = helper2(i, j+2, 0, 1);
                    pair p2 = helper2(i+2, j, 1, 0);
                    if (tiles[i][j+1] == Tileset.WALL && p1!=null && helper3(i, j+1, p1, true)) {
                        for (int k = j+1; k < p1.y(); k++) {
                            tiles[i][k] = Tileset.FLOOR;
                        }
                    } else if (tiles[i+1][j] == Tileset.WALL && p2!=null && helper3(i+1, j, p2, false)) {
                        for (int k = i+1; k < p2.x(); k++) {
                            tiles[k][j] = Tileset.FLOOR;
                        }
                    }
                }
            }
        }
    }

    private pair helper2(int i, int j, int a, int b) {
        if (i==w || j==h) {
            return null;
        } else {
            if (tiles[i][j] == Tileset.WALL) {
                return helper2(i+a, j+b, a, b);
            } else if (tiles[i][j]==Tileset.FLOOR) {
                return new pair(i, j);
            } else {
                return null;
            }
        }
    }

    private boolean helper3(int i, int j, pair p, boolean vertical) {
        boolean small=true;
        boolean big=true;
        if (vertical) {
            for (int g = j; g < p.y(); g++) {
                if (tiles[i-1][g] != Tileset.WALL) {
                    small = false;
                }
                if (tiles[i+1][g] != Tileset.WALL) {
                    big = false;
                }
            }
        } else {
            for (int g = i; g < p.x(); g++) {
                if (tiles[g][j-1] != Tileset.WALL) {
                    small = false;
                }
                if (tiles[g][j+1] != Tileset.WALL) {
                    big = false;
                }
            }
        }
        return (small || big);
    }

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
                Rectangle r2 = new Rectangle(l.x()-1, l.y()-1, oldWidth+1, oldLength+1);
                if (r2.intersects(r1)) {
                    return true;
                }
            }
        }
        return false;
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
                    stop = RANDOM.nextInt(0, 2); //1 = stop
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
                        end = new pair(x, i+2);
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
                    stop = RANDOM.nextInt(0, 2); //1 = stop
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
                        end = new pair(i+2, y);
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

    public TETile[][] getTiles() {
        return tiles;
    }

    private record pair(int x, int y) {
    }


    //past code

//    private void createHall(pair start, pair end, int direction) {
//        int stop = 0;
//        int x = start.getX();
//        int y = start.getY();
//
//        // Upward Hallways
//        if (direction == 0) { // up --> same x
//            int a = y + 1;
//            // create start wall based on condition
//            if (tiles[x][y] == Tileset.NOTHING) {
//                tiles[x - 1][y] = Tileset.WALL;
//                tiles[x][y] = Tileset.WALL;
//                tiles[x + 1][y] = Tileset.WALL;
//            } else if (tiles[x][y] == Tileset.WALL && tiles[x][y + 1] == Tileset.FLOOR) {
//                tiles[x][y+2] = Tileset.FLOOR;
//                a = a + 2;
//            }
//            // create middle section of hallway
//            for (int i = a; i < end.getY(); i++) {
//                if (tiles[x][i] == Tileset.WALL) {
//                    stop = RANDOM.nextInt(0, 5); //1 = stop
//                    if (tiles[x][i + 1] == Tileset.WALL) {
//                        if (tiles[x - 1][i] == Tileset.NOTHING) {
//                            tiles[x - 1][i] = Tileset.WALL;
//                            tiles[x - 1][i + 1] = Tileset.WALL;
//                            tiles[x - 1][i + 2] = Tileset.WALL;
//                        } else if (tiles[x + 1][i] == Tileset.NOTHING) {
//                            tiles[x + 1][i] = Tileset.WALL;
//                            tiles[x + 1][i + 1] = Tileset.WALL;
//                            tiles[x + 1][i + 2] = Tileset.WALL;
//                        }
//                        tiles[x][i] = Tileset.FLOOR;
//                        tiles[x][i + 1] = Tileset.FLOOR;
//                        if (stop == 1) {
//                            break;
//                        } else {
//                            tiles[x][i + 2] = Tileset.FLOOR;
//                            i = i + 2;
//                        }
//                    } else {
//                        tiles[x][i] = Tileset.FLOOR;
//                        if (stop == 1) {
//                            break;
//                        } else {
//                            tiles[x][i + 2] = Tileset.FLOOR;
//                            i = i + 2;
//                        }
//                    }
//                } else {
//                    tiles[x - 1][i] = Tileset.WALL;
//                    tiles[x][i] = Tileset.FLOOR;
//                    tiles[x + 1][i] = Tileset.WALL;
//                }
//            }
//            // if did not stop during the middle, create an end wall
//            if (stop != 1) {
//                tiles[x - 1][end.getY()] = Tileset.WALL;
//                tiles[x][end.getY()] = Tileset.WALL;
//                tiles[x + 1][end.getY()] = Tileset.WALL;
//            }
//        }
//
//        // Rightward Hallways
//        else if (direction == 2) { // right --> same y
//            int a = x + 1;
//            // create start wall based on condition
//            if (tiles[x][y] == Tileset.NOTHING) {
//                tiles[x][y - 1] = Tileset.WALL;
//                tiles[x][y] = Tileset.WALL;
//                tiles[x][y + 1] = Tileset.WALL;
//            } else if (tiles[x][y] == Tileset.WALL && tiles[x + 1][y] == Tileset.FLOOR) {
//                tiles[x+2][y] = Tileset.FLOOR;
//                a = a + 2;
//            }
//            // create middle section of hallway
//            for (int i = a; i < end.getX(); i++) {
//                if (tiles[i][y] == Tileset.WALL) {
//                    stop = RANDOM.nextInt(0, 5); //1 = stop
//                    if (tiles[i + 1][y] == Tileset.WALL) {
//                        if (tiles[i][y - 1] == Tileset.NOTHING) {
//                            tiles[i][y - 1] = Tileset.WALL;
//                            tiles[i + 1][y - 1] = Tileset.WALL;
//                            tiles[i + 2][y - 1] = Tileset.WALL;
//                        } else if (tiles[i][y + 1] == Tileset.NOTHING) {
//                            tiles[i][y + 1] = Tileset.WALL;
//                            tiles[i + 1][y + 1] = Tileset.WALL;
//                            tiles[i + 2][y + 1] = Tileset.WALL;
//                        }
//                        tiles[i][y] = Tileset.FLOOR;
//                        tiles[i + 1][y] = Tileset.FLOOR;
//                        if (stop == 1) {
//                            break;
//                        } else {
//                            tiles[i + 2][y] = Tileset.FLOOR;
//                            i = i + 2;
//                        }
//                    } else {
//                        tiles[i][y] = Tileset.FLOOR;
//                        if (stop == 1) {
//                            break;
//                        } else {
//                            tiles[i + 2][y] = Tileset.FLOOR;
//                            i = i + 2;
//                        }
//                    }
//                } else {
//                    tiles[i][y - 1] = Tileset.WALL;
//                    tiles[i][y] = Tileset.FLOOR;
//                    tiles[i][y + 1] = Tileset.WALL;
//                }
//            }
//            // if did not stop during the middle, create an end wall
//            if (stop != 1) {
//                tiles[end.getX()][y - 1] = Tileset.WALL;
//                tiles[end.getX()][y] = Tileset.WALL;
//                tiles[end.getX()][y + 1] = Tileset.WALL;
//            }
//        }
//        // add parameters into ArrayLists
//        hallStart.add(start);
//        hallEnd.add(end);
//        hallDirection.add(direction);
//    }


    // generate Rooms on start and end of vertical hallways
//    private void generateRoom1(int index) {
//        int startx = RANDOM.nextInt(3, w - 5);
//        int starty = RANDOM.nextInt(3, h - 5);
//        pair start = new pair(startx, starty); // left bottom
//
//        int endx = RANDOM.nextInt(startx + 2, w - 3);
//        int endy = RANDOM.nextInt(starty + 2, h - 3);
//        pair end = new pair(endx, endy); //top right
//
//        int centerx = (startx + endx) / 2;
//        int centery = (starty + endy) / 2;
//        pair center = new pair(centerx, centery); //center
//
//        //check for overlap
//        if (!overlap(roomStart, roomEnd)) {
//            createRoom(roomStart, roomEnd);
//            LeftBottom.add(roomStart);
//            RightTop.add(roomEnd);
//        }
//
//    }
}
