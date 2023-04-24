package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class World {

    private static final int LENGTH_HALL = 20;
    private static final int MIN_LENGTH = 15;
    private final ArrayList<coordinate> leftEnds = new ArrayList<>();
    private final ArrayList<coordinate> rightEnds = new ArrayList<>();
    private final ArrayList<coordinate> upEnds = new ArrayList<>();
    private final ArrayList<coordinate> downEnds = new ArrayList<>();
    private final int w;
    private final int h;
    private final Random RANDOM;
    private final TETile[][] tiles;

    public World(String seed, int width, int height) {
        Long s = Long.parseLong(seed);
        this.RANDOM = new Random(s);
        this.w = width;
        this.h = height;

        int numHalls = RANDOM.nextInt(50, 80);
        int numRooms = RANDOM.nextInt(10, 20);
        int curHall = 0;
        int curRooms = 0;

        tiles = new TETile[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = Tileset.WALL;
            }
        }

        int firstX = RANDOM.nextInt(5, w - 5);
        int firstY = RANDOM.nextInt(5, h - 5);
        coordinate coordinate = new coordinate(firstX, firstY);
        leftEnds.add(coordinate);
        rightEnds.add(coordinate);
        upEnds.add(coordinate);
        downEnds.add(coordinate);


        while (curHall <= numHalls) {
            int d = RANDOM.nextInt(4);
            if (d == 0 && rightEnds.size() != 0) {
                coordinate start = rightEnds.get(RANDOM.nextInt(rightEnds.size()));
                if (HorizontalHall(1, start, RANDOM.nextInt(6, LENGTH_HALL))) {
                    rightEnds.remove(start);
                    curHall++;
                }
            } else if (d == 1 && leftEnds.size() != 0) {
                coordinate start = leftEnds.get(RANDOM.nextInt(leftEnds.size()));
                if (HorizontalHall(-1, start, RANDOM.nextInt(6, LENGTH_HALL))) {
                    leftEnds.remove(start);
                    curHall++;
                }
            } else if (d == 2 && upEnds.size() != 0) {
                coordinate start = upEnds.get(RANDOM.nextInt(upEnds.size()));
                if (VerticalHall(1, start, RANDOM.nextInt(6, LENGTH_HALL))) {
                    upEnds.remove(start);
                    curHall++;
                }
            } else if (d == 3 && downEnds.size() != 0) {
                coordinate start = downEnds.get(RANDOM.nextInt(downEnds.size()));
                if (VerticalHall(-1, start, RANDOM.nextInt(6, LENGTH_HALL))) {
                    downEnds.remove(start);
                    curHall++;
                }
            }
        }

        while (curRooms <= numRooms) {
            int d = RANDOM.nextInt(4);
            coordinate p = null;
            if (d == 0 && rightEnds.size() != 0) {
                p = rightEnds.get(RANDOM.nextInt(rightEnds.size()));
                if (createRoom(p)) {
                    rightEnds.remove(p);
                    curRooms++;
                }
            } else if (d == 1 && leftEnds.size() != 0) {
                p = leftEnds.get(RANDOM.nextInt(leftEnds.size()));
                if (createRoom(p)) {
                    leftEnds.remove(p);
                    curRooms++;
                }
            } else if (d == 2 && upEnds.size() != 0) {
                p = upEnds.get(RANDOM.nextInt(upEnds.size()));
                if (createRoom(p)) {
                    upEnds.remove(p);
                    curRooms++;
                }
            } else if (d == 3 && downEnds.size() != 0) {
                p = downEnds.get(RANDOM.nextInt(downEnds.size()));
                if (createRoom(p)) {
                    downEnds.remove(p);
                    curRooms++;
                }
            }
        }
        breakWalls();
    }

    private void breakWalls() {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (uselessWall(i, j)) {
                    tiles[i][j] = Tileset.NOTHING;
                }
            }
        }
    }

    private boolean uselessWall(int x, int y) {
        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if (isValid(i, j) && tiles[i][j] == Tileset.FLOOR) {
                    return false;
                }
            }
        }
        return true;
    }

    private Boolean createRoom(coordinate p) {
        int roomWidth1 = RANDOM.nextInt(1, 5);
        int roomHeight1 = RANDOM.nextInt(1, 4);
        int roomWidth2 = RANDOM.nextInt(1, 5);
        int roomHeight2 = RANDOM.nextInt(1, 4);

        coordinate start = new coordinate(p.x() - roomWidth1, p.y() - roomHeight1);
        coordinate end = new coordinate(p.x() + roomWidth2, p.y() + roomHeight2);

        if (!isValid(start) || !isValid(end)) {
            return false;
        }

        for (int i = start.x(); i < end.x(); i++) {
            for (int j = end.y(); j < start.y(); j++) {
                tiles[i][j] = Tileset.FLOOR;
            }
        }

        return true;
    }

    private Boolean HorizontalHall(int direction, coordinate start, int length) {
        int x = start.x();
        int y = start.y();
        if (!isValid(new coordinate(x + direction * length, y))) {
            return false;
        }

        if (direction == 1 && !isValidEnd(x, y, "right")) {
            rightEnds.remove(start);
            return false;
        } else if (direction == -1 && !isValidEnd(x, y, "left")) {
            leftEnds.remove(start);
            return false;
        }

        for (int i = 0; i < length; i++) {
            tiles[x][y] = Tileset.FLOOR;
            x += direction;
        }

        int diverge = RANDOM.nextInt(2);
        if (length >= MIN_LENGTH && diverge == 1) {
            int midX = start.x() + direction * RANDOM.nextInt(3, length - 3);
            if (isValidEnd(midX, y, "down")) {
                downEnds.add(new coordinate(midX, y));
            }
            if (isValidEnd(midX, y, "up")) {
                upEnds.add(new coordinate(midX, y));
            }
        }

        x = x - direction;
        if (direction == 1) {
            if (isValidEnd(x, y, "right")) {
                rightEnds.add(new coordinate(x, y));
            }
        } else {
            if (isValidEnd(x, y, "left")) {
                leftEnds.add(new coordinate(x, y));
            }
        }
        if (isValidEnd(x, y, "up")) {
            upEnds.add(new coordinate(x, y));
        }
        if (isValidEnd(x, y, "down")) {
            downEnds.add(new coordinate(x, y));
        }

        return true;
    }

    private Boolean VerticalHall(int direction, coordinate start, int length) {
        int x = start.x();
        int y = start.y();

        if (!isValid(new coordinate(x, y + direction * length))) {
            return false;
        }

        if (direction == 1 && !isValidEnd(x, y, "up")) {
            upEnds.remove(start);
            return false;
        } else if (!isValidEnd(x, y, "down")) {
            downEnds.remove(start);
            return false;
        }

        for (int i = 0; i < length; i++) {
            tiles[x][y] = Tileset.FLOOR;
            y += direction;
        }

        int midornot = RANDOM.nextInt(2);
        if (length >= MIN_LENGTH && midornot == 1) {
            int midY = start.y() + direction * RANDOM.nextInt(3, length - 3);
            if (isValidEnd(x, midY, "left")) {
                leftEnds.add(new coordinate(x, midY));
            }
            if (isValidEnd(x, midY, "right")) {
                rightEnds.add(new coordinate(x, midY));
            }
        }

        y = y - direction;
        if (direction == 1) {
            if (isValidEnd(x, y, "up")) {
                upEnds.add(new coordinate(x, y));
            }
        } else {
            if (isValidEnd(x, y, "down")) {
                downEnds.add(new coordinate(x, y));
            }
        }
        if (isValidEnd(x, y, "left")) {
            leftEnds.add(new coordinate(x, y));
        }
        if (isValidEnd(x, y, "right")) {
            rightEnds.add(new coordinate(x, y));
        }
        return true;
    }

    private Boolean isValid(int x, int y) {
        return 0 < x && x < w - 1 && 0 < y && y < h - 1;
    }

    private Boolean isValid(coordinate p) {
        return isValid(p.x(), p.y());
    }

    private Boolean isValidEnd(int x, int y, String direction) {
        int changeX = 0;
        int changeY = 0;
        if (direction.equals("left")) {
            if (tiles[x - 1][y + 1] == Tileset.FLOOR && tiles[x - 2][y + 1] == Tileset.FLOOR) {
                return false;
            }
            return tiles[x - 1][y - 1] != Tileset.FLOOR || tiles[x - 2][y - 1] != Tileset.FLOOR;
        } else if (direction.equals("right")) {
            if (tiles[x + 1][y + 1] == Tileset.FLOOR && tiles[x + 2][y + 1] == Tileset.FLOOR) {
                return false;
            }
            return tiles[x + 1][y - 1] != Tileset.FLOOR || tiles[x + 2][y - 1] != Tileset.FLOOR;
        } else if (direction.equals("up")) {
            if (tiles[x - 1][y + 1] == Tileset.FLOOR && tiles[x - 1][y + 2] == Tileset.FLOOR) {
                return false;
            }
            return tiles[x + 1][y + 1] != Tileset.FLOOR || tiles[x + 1][y + 2] != Tileset.FLOOR;
        } else if (direction.equals("down")) {
            if (tiles[x - 1][y - 1] == Tileset.FLOOR && tiles[x - 1][y - 2] == Tileset.FLOOR) {
                return false;
            }
            return tiles[x + 1][y - 1] != Tileset.FLOOR || tiles[x + 1][y - 2] != Tileset.FLOOR;
        }
        return true;
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    private record coordinate(int x, int y) {
    }
}
