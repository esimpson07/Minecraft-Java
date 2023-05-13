import java.awt.Color;

public class Cube {
    private double x, y, z, width, length, height, rotation = Math.PI*0.75;
    private double[] RotAdd = new double[4];
    private double x1, x2, x3, x4, y1, y2, y3, y4;
    private DPolygon[] Polys = new DPolygon[6];
    private Color[] c;
    private boolean[] polysToDraw;
    private double[] angle;
    private int id;
    private int type;
    private boolean normal;
    
    public Cube(double x, double y, double z, double width, double length, double height, int type)
    {
        this.id = (int)(Math.random() * 2147483647);
        this.c = setValuesForType(type);
        this.polysToDraw = new boolean[]{true,true,true,true,true,true};
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.type = type;
        this.width = width;
        this.length = length;
        this.height = height;
        
        setRotAdd();
        updatePoly();
    }
    
    Color[] setValuesForType(int type) {
        normal = true;
        if(type == Screen.stone) {
            return(new Color[]{Screen.lightGray,Screen.lightGray,Screen.lightGray,Screen.lightGray,Screen.lightGray,Screen.lightGray});
        } else if(type == Screen.cobblestone) {
            return(new Color[]{Screen.darkGray,Screen.darkGray,Screen.darkGray,Screen.darkGray,Screen.darkGray,Screen.darkGray});
        } else if(type == Screen.dirt) {
            return(new Color[]{Screen.darkBrown,Screen.darkBrown,Screen.darkBrown,Screen.darkBrown,Screen.darkBrown,Screen.darkBrown});
        } else if(type == Screen.grass) {
            return(new Color[]{Screen.darkBrown,Screen.lightGreen,Screen.darkBrown,Screen.darkBrown,Screen.darkBrown,Screen.darkBrown});
        } else if(type == Screen.planks) {
            return(new Color[]{Screen.lightBrown,Screen.lightBrown,Screen.lightBrown,Screen.lightBrown,Screen.lightBrown,Screen.lightBrown});
        } else if(type == Screen.logs) {
            return(new Color[]{Screen.lightBrown,Screen.lightBrown,Screen.darkBrown,Screen.darkBrown,Screen.darkBrown,Screen.darkBrown});
        } else if(type == Screen.leaves) {
            return(new Color[]{Screen.darkGreen,Screen.darkGreen,Screen.darkGreen,Screen.darkGreen,Screen.darkGreen,Screen.darkGreen});
        } else if(type == Screen.sand) {
            return(new Color[]{Screen.beige,Screen.beige,Screen.beige,Screen.beige,Screen.beige,Screen.beige});
        } else if(type == Screen.gravel) {
            return(new Color[]{Screen.midGray,Screen.midGray,Screen.midGray,Screen.midGray,Screen.midGray,Screen.midGray});
        } else if(type == Screen.water) {
            normal = false;
            return(new Color[]{Screen.waterBlue,Screen.waterBlue,Screen.waterBlue,Screen.waterBlue,Screen.waterBlue,Screen.waterBlue});
        } else if(type == Screen.bedrock) {
            return(new Color[]{Screen.black,Screen.black,Screen.black,Screen.black,Screen.black,Screen.black});
        } else {
            return(new Color[]{});
        }
    }
    
    double[] getAttributes() {
        return new double[]{x,y,z,width,length,height};
    }
    
    double[] getCoords() {
        return new double[]{x,y,z};
    }
    
    double[] getAdjacentCube(int face) {
        if(face == 0) {
            return new double[]{x, y, z - height};
        } else if(face == 1) {
            return new double[]{x, y, z + height};
        } else if(face == 2) {
            return new double[]{x, y + length, z};
        } else if(face == 3) {
            return new double[]{x - width, y, z};
        } else if(face == 4) {
            return new double[]{x, y - length, z};
        } else if(face == 5) {
            return new double[]{x + width, y, z};
        } else {
            return new double[]{0,0,0};
        }
    }
    
    int getID() {
        return id;
    }
    
    double getDist(double x, double y, double z) {
        return Math.sqrt(Math.pow(this.x - x,2) + Math.pow(this.y - y,2) + Math.pow(this.z - z,2));
    }
    
    /*void softAdjacencyCheck() {
        for(int i = 0; i < Screen.Cubes.length; i ++) {
            if(Screen.Cubes[i] != null) {
                for(int j = 0; j < Screen.Cubes[i].size(); j ++) {
                    for(int f = 0; f < 6; f ++) {
                        if(Screen.Cubes[i].get(j).getCoords()[0] == getAdjacentCube(f)[0] && Screen.Cubes[i].get(j).getCoords()[1] == 
                            getAdjacentCube(f)[1] && Screen.Cubes[i].get(j).getCoords()[2] == getAdjacentCube(f)[2] && 
                            ((Screen.Cubes[i].get(j).isNormal() == normal) || !normal)) {
                            polysToDraw[f] = false;
                            updatePoly();
                        }
                    }
                }
            }
        }
    }*/
    
    void softAdjacencyCheck() {
        int chunk = Screen.getChunkNumberIn((int)x,(int)y);
        int sideLength = Screen.size / Screen.chunkSize;
        int[] adjacentChunks = new int[5]; //left (chunk - 1) right (chunk + 1) above (chunk + sideLength) below (chunk - sideLength)
        if(chunk % sideLength != 0 && (int)x % Screen.chunkSize == 0) {
            adjacentChunks[0] = chunk - 1;
        } else {
            adjacentChunks[0] = -1;
        }
        if(chunk % sideLength != sideLength - 1 && (int)x % Screen.chunkSize == Screen.chunkSize - 1) {
            adjacentChunks[1] = chunk + 1;
        } else {
            adjacentChunks[1] = -1;
        }
        if(chunk >= sideLength && (int)y % Screen.chunkSize == 0) {
            adjacentChunks[2] = chunk - sideLength;
        } else {
            adjacentChunks[2] = -1;
        }
        if(chunk < Math.pow(sideLength,2) - sideLength && (int)y % Screen.chunkSize == Screen.chunkSize - 1) {
            adjacentChunks[3] = chunk + sideLength;
        } else {
            adjacentChunks[3] = -1;
        }
        adjacentChunks[4] = chunk;
        
        for(int i = 0; i < adjacentChunks.length; i ++) {
            for(int f = 0; f < 6; f ++) {
                if(adjacentChunks[i] != -1) {
                    for(int j = 0; j < Screen.Chunks[adjacentChunks[i]].getCubeArray().size(); j ++) {
                        if(Screen.Chunks[adjacentChunks[i]].getCubeArray().get(j).getCoords()[0] == getAdjacentCube(f)[0] && Screen.Chunks[adjacentChunks[i]].getCubeArray().get(j).getCoords()[1] == getAdjacentCube(f)[1]
                            && Screen.Chunks[adjacentChunks[i]].getCubeArray().get(j).getCoords()[2] == getAdjacentCube(f)[2] && ((Screen.Chunks[adjacentChunks[i]].getCubeArray().get(j).isNormal() == normal) || !normal)){
                            polysToDraw[f] = false;
                            updatePoly();
                        }
                    }
                }
            }
        }
    }
    
    void chunkOnlyAdjacencyCheck(int i) {
        if(Screen.Chunks[i] != null) {
            for(int j = 0; j < Screen.Chunks[i].getCubeArray().size(); j ++) {
                for(int f = 0; f < 6; f ++) {
                    if(Screen.Chunks[i].getCubeArray().get(j).getCoords()[0] == getAdjacentCube(f)[0] && Screen.Chunks[i].getCubeArray().get(j).getCoords()[1] == 
                        getAdjacentCube(f)[1] && Screen.Chunks[i].getCubeArray().get(j).getCoords()[2] == getAdjacentCube(f)[2] && 
                        ((Screen.Chunks[i].getCubeArray().get(j).isNormal() == normal) || !normal)) {
                        polysToDraw[f] = false;
                        updatePoly();
                    }
                }
            }
        }
    }
    
    void hardAdjacencyCheck() {
        for(int i = 0; i < Screen.Chunks.length; i ++) {
            if(Screen.Chunks[i] != null) {
                for(int j = 0; j < Screen.Chunks[i].getCubeArray().size(); j ++) {
                    for(int f = 0; f < 6; f ++) {
                        if(Screen.Chunks[i].getCubeArray().get(j).getCoords()[0] == getAdjacentCube(f)[0] && Screen.Chunks[i].getCubeArray().get(j).getCoords()[1] == 
                            getAdjacentCube(f)[1] && Screen.Chunks[i].getCubeArray().get(j).getCoords()[2] == getAdjacentCube(f)[2] && 
                            ((Screen.Chunks[i].getCubeArray().get(j).isNormal() == normal) || !normal)) {
                            polysToDraw[f] = false;
                            updatePoly();
                            Screen.Chunks[i].getCubeArray().get(j).softAdjacencyCheck();
                        }
                    }
                }
            }
        }
    }
    
    void setRotAdd()
    {
        angle = new double[4];
        
        double xdif = - width/2 + 0.00001;
        double ydif = - length/2 + 0.00001;
        
        angle[0] = Math.atan(ydif/xdif);
        
        if(xdif<0)
            angle[0] += Math.PI;
        
        xdif = width/2 + 0.00001;
        ydif = - length/2 + 0.00001;
        
        angle[1] = Math.atan(ydif/xdif);
        
        if(xdif<0)
            angle[1] += Math.PI;
            
        xdif = width/2 + 0.00001;
        ydif = length/2 + 0.00001;
        
        angle[2] = Math.atan(ydif/xdif);
        
        if(xdif<0)
            angle[2] += Math.PI;
        
        xdif = - width/2 + 0.00001;
        ydif = length/2 + 0.00001;
        
        angle[3] = Math.atan(ydif/xdif);
        
        if(xdif<0)
            angle[3] += Math.PI;    
        
        RotAdd[0] = angle[0] + 0.25 * Math.PI;
        RotAdd[1] =    angle[1] + 0.25 * Math.PI;
        RotAdd[2] = angle[2] + 0.25 * Math.PI;
        RotAdd[3] = angle[3] + 0.25 * Math.PI;
    }
    
    void updateDirection(double toX, double toY)
    {
        double xdif = toX - (x + width/2) + 0.00001;
        double ydif = toY - (y + length/2) + 0.00001;
        
        double anglet = Math.atan(ydif/xdif) + 0.75 * Math.PI;

        if(xdif<0)
            anglet += Math.PI;

        rotation = anglet;
        updatePoly();        
    }

    void updatePoly()
    {
        for(int i = 0; i < 6; i ++) {
            Screen.DPolygons.remove(Polys[i]);
        }
        
        double radius = Math.sqrt(width*width + length*length);
        
        x1 = x+width*0.5+radius*0.5*Math.cos(rotation + RotAdd[0]);
        x2 = x+width*0.5+radius*0.5*Math.cos(rotation + RotAdd[1]);
        x3 = x+width*0.5+radius*0.5*Math.cos(rotation + RotAdd[2]);
        x4 = x+width*0.5+radius*0.5*Math.cos(rotation + RotAdd[3]);
           
        y1 = y+length*0.5+radius*0.5*Math.sin(rotation + RotAdd[0]);
        y2 = y+length*0.5+radius*0.5*Math.sin(rotation + RotAdd[1]);
        y3 = y+length*0.5+radius*0.5*Math.sin(rotation + RotAdd[2]);
        y4 = y+length*0.5+radius*0.5*Math.sin(rotation + RotAdd[3]);
   
        if(polysToDraw[0] == true && Polys[0] != null) {
            Polys[0].setX(new double[]{x1, x2, x3, x4});
            Polys[0].setY(new double[]{y1, y2, y3, y4});;
            Polys[0].setZ(new double[]{z, z, z, z});
        } else  if(polysToDraw[0] == true && Polys[0] == null){
            Polys[0] = new DPolygon(new double[]{x1, x2, x3, x4}, new double[]{y1, y2, y3, y4}, new double[]{z, z, z, z}, c[0], normal, 0, id);
        } else {
            Polys[0] = null;
        }
        if(polysToDraw[1] == true && Polys[1] != null) {
            Polys[1].setX(new double[]{x4, x3, x2, x1});
            Polys[1].setY(new double[]{y4, y3, y2, y1});
            Polys[1].setZ(new double[]{z+height, z+height, z+height, z+height});
        } else if(polysToDraw[1] == true && Polys[1] == null){
            Polys[1] = new DPolygon(new double[]{x4, x3, x2, x1}, new double[]{y4, y3, y2, y1}, new double[]{z+height, z+height, z+height, z+height}, c[1], normal, 1, id);
        } else {    
            Polys[1] = null;
        } 
        if(polysToDraw[2] == true && Polys[2] != null) {
            Polys[2].setX(new double[]{x1, x1, x2, x2});
            Polys[2].setY(new double[]{y1, y1, y2, y2});
            Polys[2].setZ(new double[]{z, z+height, z+height, z});
        } else if(polysToDraw[2] == true && Polys[2] == null){
            Polys[2] = new DPolygon(new double[]{x1, x1, x2, x2}, new double[]{y1, y1, y2, y2}, new double[]{z, z+height, z+height, z}, c[2], normal, 2, id);
        } else {
            Polys[2] = null;
        }
        if(polysToDraw[3] == true && Polys[3] != null) {
            Polys[3].setX(new double[]{x2, x2, x3, x3});
            Polys[3].setY(new double[]{y2, y2, y3, y3});
            Polys[3].setZ(new double[]{z, z+height, z+height, z});
        } else if(polysToDraw[3] == true && Polys[3] == null){
            Polys[3] = new DPolygon(new double[]{x2, x2, x3, x3}, new double[]{y2, y2, y3, y3},  new double[]{z, z+height, z+height, z}, c[3], normal, 3, id);
        } else {
            Polys[3] = null;
        }
        if(polysToDraw[4] == true && Polys[4] != null) {
            Polys[4].setX(new double[]{x3, x3, x4, x4});
            Polys[4].setY(new double[]{y3, y3, y4, y4});
            Polys[4].setZ(new double[]{z, z+height, z+height, z});
        } else if(polysToDraw[4] == true && Polys[4] == null){
            Polys[4] = new DPolygon(new double[]{x3, x3, x4, x4}, new double[]{y3, y3, y4, y4},  new double[]{z, z+height, z+height, z}, c[4], normal, 4, id);
        } else {
            Polys[4] = null;
        }
        if(polysToDraw[5] == true && Polys[5] != null) {
            Polys[5].setX(new double[]{x4, x4, x1, x1});
            Polys[5].setY(new double[]{y4, y4, y1, y1});
            Polys[5].setZ(new double[]{z, z+height, z+height, z});
        } else if(polysToDraw[5] == true && Polys[5] == null){
            Polys[5] = new DPolygon(new double[]{x4, x4, x1, x1}, new double[]{y4, y4, y1, y1},  new double[]{z, z+height, z+height, z}, c[5], normal, 5, id);
        } else {
            Polys[5] = null;
        }
        
        for(int i = 0; i < 6; i++)
        {
            if(Polys[i] != null) {
                Screen.DPolygons.add(Polys[i]);
            }
        }
    }
    
    void changeAdjacentPoly(int face, boolean state) {
        if(face == 0) {
            polysToDraw[1] = state;
        } else if(face == 1) {
            polysToDraw[0] = state;
        } else if(face == 2) {
            polysToDraw[3] = state;
        } else if(face == 3) {
            polysToDraw[2] = state;
        } else if(face == 4) {
            polysToDraw[5] = state;
        } else if(face == 5) {
            polysToDraw[4] = state;
        }
    }
    
    void changePoly(int face, boolean state) {
        polysToDraw[face] = state;
    }
    
    int getType() {
        return type;
    }
    
    int getAdjacentPoly(int face) {
        if(face == 0) {
            return 1;
        } else if(face == 1) {
            return 0;
        } else if(face == 2) {
            return 3;
        } else if(face == 3) {
            return 2;
        } else if(face == 4) {
            return 5;
        } else if(face == 5) {
            return 4;
        } else {
            return -1;
        }
    }
    
    boolean[] getPolysToDraw() {
        return polysToDraw;
    }
    
    boolean isNormal() {
        return normal;
    }
    
    boolean isBedrock() {
        return type == Screen.bedrock;
    }
    
    void changeAdjacentCubePoly(int face, boolean state) {
        for(int i = 0; i < Screen.Chunks.length; i ++) {
            if(Screen.Chunks[i] != null) {
                for(int j = 0; j < Screen.Chunks[i].getCubeArray().size(); j ++) {
                    for(int f = 0; f < 6; f ++) {
                        if(Screen.Chunks[i].getCubeArray().get(j).getCoords()[0] == getAdjacentCube(f)[0] && Screen.Chunks[i].getCubeArray().get(j).getCoords()[1] == 
                            getAdjacentCube(f)[1] && Screen.Chunks[i].getCubeArray().get(j).getCoords()[2] == getAdjacentCube(f)[2] && Screen.Chunks[i].getCubeArray().get(j).isNormal()) {
                            Screen.Chunks[i].getCubeArray().get(j).changeAdjacentPoly(face,state);
                            Screen.Chunks[i].getCubeArray().get(j).updatePoly();
                        }
                    }
                }
            }
        }
    }

    void removeCubeInChunk()
    {
        for(int i = 0; i < 6; i ++) {
            Screen.DPolygons.remove(Polys[i]);
        }
    }
    
    void removeCube()
    {
        for(int i = 0; i < 6; i ++) {
            changeAdjacentCubePoly(i,true);
            Screen.DPolygons.remove(Polys[i]);
        }
        for(int i = 0; i < Screen.Chunks.length; i ++) {
            Screen.Chunks[i].removeCube(this);
        }
    }
}
