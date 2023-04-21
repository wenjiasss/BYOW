package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Avatar {

    private TETile skin;
    private pair position;
    private TETile[][] tiles;

    public Avatar(TETile s, TETile[][] t) {
        this.skin = s;
        this.tiles = t;
    }

    public void move(pair pos) {
        this.position = pos;
    }
    private boolean movable(int x, int y) {
        return isValid(x, y) && (tiles[x][y].equals(Tileset.FLOOR));
    }
    private Boolean isValid(int x, int y) {
        return x >= 1 && x < tiles.length - 1 && y >= 1 && y < tiles[0].length - 1;
    }

    public TETile[][] moveUp() {
        int newposx = this.position.x();
        int newposy = this.position.y() + 1;
        if (movable(newposx, newposy)) {
            tiles[newposx][newposy] = this.skin;
            tiles[newposx][newposy - 1] = Tileset.FLOOR;
            this.move(new pair(newposx, newposy));
        }
        return tiles;
    }
    public TETile[][] moveDown() {
        int newposx = this.position.x();
        int newposy = this.position.y() - 1;
        if (movable(newposx, newposy)) {
            tiles[newposx][newposy] = this.skin;
            tiles[newposx][newposy + 1] = Tileset.FLOOR;
            this.move(new pair(newposx, newposy));
        }
        return tiles;
    }
    public TETile[][] moveRight() {
        int newposx = this.position.x() + 1;
        int newposy = this.position.y();
        if (movable(newposx, newposy)) {
            tiles[newposx][newposy] = this.skin;
            tiles[newposx - 1][newposy] = Tileset.FLOOR;
            this.move(new pair(newposx, newposy));
        }
        return tiles;
    }
    public TETile[][] moveLeft() {
        int newposx = this.position.x() - 1;
        int newposy = this.position.y();
        if (movable(newposx, newposy)) {
            tiles[newposx][newposy] = this.skin;
            tiles[newposx + 1][newposy] = Tileset.FLOOR;
            this.move(new pair(newposx, newposy));
        }
        return tiles;
    }

    public TETile getSkin() {
        return skin;
    }

    private record pair(int x, int y) {
    }
}
