package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Avatar {

    private final TETile skin;
    private coordinate position;
    private TETile[][] tiles;

    public Avatar(TETile s, TETile[][] t) {
        this.skin = s;
        this.tiles = t;
    }

    public TETile[][] initialize(TETile[][] t) {
        tiles = t;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (movable(i, j)) {
                    tiles[i][j] = this.skin;
                    this.position = new coordinate(i, j);
                    break;
                }
            }
            if (position != null) {
                break;
            }
        }
        return tiles;
    }

    private void move(coordinate p) {
        int x = position.x();
        int y = position.y();
        tiles[x][y] = Tileset.FLOOR;
        this.position = p;
        int newX = position.x();
        int newY = position.y();
        tiles[newX][newY] = this.skin;
    }

    private boolean movable(int x, int y) {
        return 0 < x && x < tiles.length - 1 && 0 < y && y < tiles[0].length - 1 && (tiles[x][y].equals(Tileset.FLOOR));
    }

    public TETile[][] moving(int v, int h) {
        int newX = position.x();
        int newY = position.y();
        if (v != 0) {
            newY = newY + v;
        } else if (h != 0) {
            newX = newX + h;
        }
        if (movable(newX, newY)) {
            move(new coordinate(newX, newY));
        }
        return tiles;
    }

    public TETile[][] moveUp() {
        return moving(1, 0);
    }

    public TETile[][] moveDown() {
        return moving(-1, 0);
    }

    public TETile[][] moveRight() {
        return moving(0, 1);
    }

    public TETile[][] moveLeft() {
        return moving(0, -1);
    }

    public TETile getSkin() {
        return skin;
    }

    public String getPositionX() {
        return Integer.toString(position.x());
    }

    public String getPositionY() {
        return Integer.toString(position.y());
    }

    public int getPosition1() {
        return position.x();
    }

    public int getPosition2() {
        return position.y();
    }

    public void changePosition(int x, int y) {
        coordinate c = new coordinate(x, y);
        if (movable(x, y)) {
            this.position = c;
            tiles[x][y] = this.skin;
        }
    }

    private record coordinate(int x, int y) {
    }
}
