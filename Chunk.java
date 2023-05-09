import java.util.ArrayList;

public class Chunk {
    private int size;
    private int height;
    private Cube[][][] cubes;
    public Chunk(int size, int height) {
        this.size = size;
        this.height = height;
        this.cubes = new Cube[size][size][height];
        Screen.Chunks.add(this);
    }
    
    public void setCube(int x, int y, int z, Cube cube) {
        cubes[x][y][z] = cube;
    }

    public Cube getCube(int x, int y, int z) {
        return cubes[x][y][z];
    }
    
    public Cube[][][] getCubeArray() {
        return cubes;
    }
}
