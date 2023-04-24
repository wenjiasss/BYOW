package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class Sight {

    private int distance = 5;
    private coordinate p;
    private TETile[][] sight;

    public Sight(TETile[][] t, coordinate p) {
        this.sight = t;
        this.p = p;
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[0].length; j++) {
                if (outOfSight(i, j)) {
                    sight[i][j] = Tileset.NOTHING;
                }
            }
        }
    }

    public TETile[][] getSight() {
        return sight;
    }

    private boolean outOfSight(int x, int y) {
        if (p.x() - distance < x && x < p.x() + distance && p.y() - distance < y && y < p.y() + distance) {
            return false;
        }
        return true;
    }

    private record coordinate(int x, int y) {
    }
}
