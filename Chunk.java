import java.util.ArrayList;

public class Chunk {
    private int size;
    private int height;
    private int[][][] cubeTypes;
    private ArrayList<Cube> cubes;
    private int x, y;
    private boolean inMap = false, alreadyChecked = false;
    
    public Chunk(int[][][] array, int size, int height, int startX, int startY) {
        this.cubeTypes = new int[size][size][height];
        this.size = size;
        this.x = startX;
        this.y = startY;
        cubes = new ArrayList<Cube>();
        for(int x = 0; x < size; x ++) {
            for(int y = 0; y < size; y ++) {
                for(int z = 0; z < height; z ++) {
                    cubeTypes[x][y][z] = array[startX * size + x][startY * size + y][z];
                    if(cubeTypes[x][y][z] != -1) {
                        cubes.add(new Cube(startX * size + x, startY * size + y, z, 1, 1, 1, cubeTypes[x][y][z]));
                    }
                }
            }
        }
    }
    
    public void setCube(int x, int y, int z, int cubeType) {
        cubeTypes[x][y][z] = cubeType;
    }
    
    public boolean isAlreadyInMap() {
        return inMap;
    }
    
    public void setAlreadyInMap(boolean state) {
        inMap = state;
    }
    
    public boolean isAlreadyChecked() {
        return alreadyChecked;
    }
    
    public void setAlreadyChecked(boolean state) {
        alreadyChecked = state;
    }
    
    public double getDist(double x, double y) {
        return(Math.sqrt(Math.pow(x - this.x,2) + Math.pow(y - this.y,2)));
    }
    
    public int[][][] getChunk() {
        return cubeTypes;
    }
    
    public ArrayList<Cube> getCubeArray() {
        return cubes;
    }
    
    public int getX() {return x;}
    public int getY() {return y;}
    public int getSize() {return size;}
}
