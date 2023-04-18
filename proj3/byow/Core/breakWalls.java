package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class breakWalls {

    private final TETile[][] t;
    public int w;
    public int h;

    public breakWalls(TETile[][] t) {
        this.t = t;
        this.w = t.length;
        this.h = t[0].length;
        breakIT();
        breakIT();
    }

    private void breakIT() {
        for (int i = 0; i < w - 2; i++) {
            for (int j = 0; j < h - 2; j++) {
                if (t[i][j] == Tileset.FLOOR) {
                    bw1(i, j);
                    bw2(i, j);
                    bw3(i, j);
                }
            }
        }
    }

    private void bw1(int i, int j) {
        if (t[i][j + 1] == Tileset.WALL) {
            if (t[i][j + 2] == Tileset.FLOOR) {
                t[i][j + 1] = Tileset.FLOOR;
            }
            if (t[i][j + 2] == Tileset.WALL && t[i][j + 3] == Tileset.FLOOR) {
                t[i][j + 1] = Tileset.FLOOR;
                t[i][j + 2] = Tileset.FLOOR;
            }
        }
        if (t[i + 1][j] == Tileset.WALL) {
            if (t[i + 2][j] == Tileset.FLOOR) {
                t[i + 1][j] = Tileset.FLOOR;
            }
            if (t[i + 2][j] == Tileset.WALL && t[i + 3][j] == Tileset.FLOOR) {
                t[i + 1][j] = Tileset.FLOOR;
                t[i + 2][j] = Tileset.FLOOR;
            }
        }
    }

    private void bw2(int i, int j) {
        pair p1 = helper2(i, j + 2, 0, 1);
        pair p2 = helper2(i + 2, j, 1, 0);
        if (t[i][j + 1] == Tileset.WALL && p1 != null && helper3(i, j + 1, p1, true)) {
            for (int k = j + 1; k < p1.y(); k++) {
                t[i][k] = Tileset.FLOOR;
            }
        } else if (t[i + 1][j] == Tileset.WALL && p2 != null && helper3(i + 1, j, p2, false)) {
            for (int k = i + 1; k < p2.x(); k++) {
                t[k][j] = Tileset.FLOOR;
            }
        }
    }

    private void bw3(int i, int j) {
        pair p1 = helper4(i, j + 2, 0, 1);
        pair p2 = helper4(i + 2, j, 1, 0);
        if (t[i][j + 1] == Tileset.WALL && p1 != null && helper3(i, j + 1, p1, true)) {
            for (int k = j + 1; k < p1.y()-1; k++) {
                t[i][k] = Tileset.FLOOR;
            }
        } else if (t[i + 1][j] == Tileset.WALL && p2 != null && helper3(i + 1, j, p2, false)) {
            for (int k = i + 1; k < p2.x()-1; k++) {
                t[k][j] = Tileset.FLOOR;
            }
        }
    }

    private pair helper2(int i, int j, int a, int b) {
        if (i == w || j == h) {
            return null;
        } else {
            if (t[i][j] == Tileset.WALL) {
                return helper2(i + a, j + b, a, b);
            } else if (t[i][j] == Tileset.FLOOR) {
                return new pair(i, j);
            } else {
                return null;
            }
        }
    }

    private boolean helper3(int i, int j, pair p, boolean vertical) {
        boolean small = true;
        boolean big = true;
        if (vertical) {
            for (int g = j; g < p.y(); g++) {
                if (t[i - 1][g] != Tileset.WALL) {
                    small = false;
                }
                if (t[i + 1][g] != Tileset.WALL) {
                    big = false;
                }
            }
        } else {
            for (int g = i; g < p.x(); g++) {
                if (t[g][j - 1] != Tileset.WALL) {
                    small = false;
                }
                if (t[g][j + 1] != Tileset.WALL) {
                    big = false;
                }
            }
        }
        return (small || big);
    }

    private pair helper4(int i, int j, int a, int b) {
        if (i == w || j == h) {
            return null;
        } else {
            if (t[i][j] == Tileset.WALL) {
                return helper4(i + a, j + b, a, b);
            } else if (t[i][j] == Tileset.NOTHING) {
                return new pair(i, j);
            } else {
                return null;
            }
        }
    }

    public TETile[][] getTiles() {
        return t;
    }

    private record pair(int x, int y) {
    }
}
