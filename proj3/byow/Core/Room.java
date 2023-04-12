package byow.Core;
import byow.TileEngine.Tileset;

import java.util.ArrayList;

public class Room {
    private int width;
    private int height;
    // point is bottom left coordinate
    private Pair point;

    private class Pair {
        private int x;
        private int y;
        public Pair(int x, int y){
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

    public Room(int w, int h, int x, int y){
        width = w;
        height = h;
        point = new Pair(x,y);
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public ArrayList<Pair> getPoint(){
        ArrayList<Pair> p = new ArrayList<Pair>();

        return p;
    }


}
