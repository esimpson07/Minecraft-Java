import java.awt.Color;

public class Cube {
    double x, y, z, width, length, height, rotation = Math.PI*0.75;
    double[] RotAdd = new double[4];
    Color c;
    double x1, x2, x3, x4, y1, y2, y3, y4;
    DPolygon[] Polys = new DPolygon[6];
    boolean[] polysToDraw;
    double[] angle;
    int id;
    
    public Cube(double x, double y, double z, double width, double length, double height, Color c, int id)
    {
        Polys[0] = new DPolygon(new double[]{x, x+width, x+width, x}, new double[]{y, y, y+length, y+length},  new double[]{z, z, z, z}, c, false, 0, id);
        Screen.DPolygons.add(Polys[0]);
        Polys[1] = new DPolygon(new double[]{x, x+width, x+width, x}, new double[]{y, y, y+length, y+length},  new double[]{z+height, z+height, z+height, z+height}, c, false, 1, id);
        Screen.DPolygons.add(Polys[1]);
        Polys[2] = new DPolygon(new double[]{x, x, x+width, x+width}, new double[]{y, y, y, y},  new double[]{z, z+height, z+height, z}, c, false, 2, id);
        Screen.DPolygons.add(Polys[2]);
        Polys[3] = new DPolygon(new double[]{x+width, x+width, x+width, x+width}, new double[]{y, y, y+length, y+length},  new double[]{z, z+height, z+height, z}, c, false, 3, id);
        Screen.DPolygons.add(Polys[3]);
        Polys[4] = new DPolygon(new double[]{x, x, x+width, x+width}, new double[]{y+length, y+length, y+length, y+length},  new double[]{z, z+height, z+height, z}, c, false, 4, id);
        Screen.DPolygons.add(Polys[4]);
        Polys[5] = new DPolygon(new double[]{x, x, x, x}, new double[]{y, y, y+length, y+length},  new double[]{z, z+height, z+height, z}, c, false, 5, id);
        Screen.DPolygons.add(Polys[5]);
        polysToDraw = new boolean[]{true,true,true,true,true,true};
        this.c = c;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.width = width;
        this.length = length;
        this.height = height;
        
        setRotAdd();
        updatePoly();
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
    
    void softAdjacencyCheck() {
        for(int i = 0; i < Screen.Cubes.size(); i ++) {
            for(int j = 0; j < 6; j ++) {
                if(Screen.Cubes.get(i).getCoords()[0] == getAdjacentCube(j)[0] && Screen.Cubes.get(i).getCoords()[1] == 
                    getAdjacentCube(j)[1] && Screen.Cubes.get(i).getCoords()[2] == getAdjacentCube(j)[2]) {
                    polysToDraw[j] = false;
                    updatePoly();
                }
            }
        }
    }
    
    void hardAdjacencyCheck() {
        for(int i = 0; i < Screen.Cubes.size(); i ++) {
            for(int j = 0; j < 6; j ++) {
                if(Screen.Cubes.get(i).getCoords()[0] == getAdjacentCube(j)[0] && Screen.Cubes.get(i).getCoords()[1] == 
                    getAdjacentCube(j)[1] && Screen.Cubes.get(i).getCoords()[2] == getAdjacentCube(j)[2]) {
                    polysToDraw[j] = false;
                    updatePoly();
                    Screen.Cubes.get(i).softAdjacencyCheck();
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
            Polys[0] = new DPolygon(new double[]{x1, x2, x3, x4}, new double[]{y1, y2, y3, y4}, new double[]{z, z, z, z}, c, false, 0, id);
        } else {
            Polys[0] = null;
        }
        if(polysToDraw[1] == true && Polys[1] != null) {
            Polys[1].setX(new double[]{x4, x3, x2, x1});
            Polys[1].setY(new double[]{y4, y3, y2, y1});
            Polys[1].setZ(new double[]{z+height, z+height, z+height, z+height});
        } else if(polysToDraw[1] == true && Polys[1] == null){
            Polys[1] = new DPolygon(new double[]{x4, x3, x2, x1}, new double[]{y4, y3, y2, y1}, new double[]{z+height, z+height, z+height, z+height}, c, false, 1, id);
        } else {    
            Polys[1] = null;
        } 
        if(polysToDraw[2] == true && Polys[2] != null) {
            Polys[2].setX(new double[]{x1, x1, x2, x2});
            Polys[2].setY(new double[]{y1, y1, y2, y2});
            Polys[2].setZ(new double[]{z, z+height, z+height, z});
        } else if(polysToDraw[2] == true && Polys[2] == null){
            Polys[2] = new DPolygon(new double[]{x1, x1, x2, x2}, new double[]{y1, y1, y2, y2}, new double[]{z, z+height, z+height, z}, c, false, 2, id);
        } else {
            Polys[2] = null;
        }
        if(polysToDraw[3] == true && Polys[3] != null) {
            Polys[3].setX(new double[]{x2, x2, x3, x3});
            Polys[3].setY(new double[]{y2, y2, y3, y3});
            Polys[3].setZ(new double[]{z, z+height, z+height, z});
        } else if(polysToDraw[3] == true && Polys[3] == null){
            Polys[3] = new DPolygon(new double[]{x2, x2, x3, x3}, new double[]{y2, y2, y3, y3},  new double[]{z, z+height, z+height, z}, c, false, 3, id);
        } else {
            Polys[3] = null;
        }
        if(polysToDraw[4] == true && Polys[4] != null) {
            Polys[4].setX(new double[]{x3, x3, x4, x4});
            Polys[4].setY(new double[]{y3, y3, y4, y4});
            Polys[4].setZ(new double[]{z, z+height, z+height, z});
        } else if(polysToDraw[4] == true && Polys[4] == null){
            Polys[4] = new DPolygon(new double[]{x3, x3, x4, x4}, new double[]{y3, y3, y4, y4},  new double[]{z, z+height, z+height, z}, c, false, 4, id);
        } else {
            Polys[4] = null;
        }
        if(polysToDraw[5] == true && Polys[5] != null) {
            Polys[5].setX(new double[]{x4, x4, x1, x1});
            Polys[5].setY(new double[]{y4, y4, y1, y1});
            Polys[5].setZ(new double[]{z, z+height, z+height, z});
        } else if(polysToDraw[5] == true && Polys[5] == null){
            Polys[5] = new DPolygon(new double[]{x4, x4, x1, x1}, new double[]{y4, y4, y1, y1},  new double[]{z, z+height, z+height, z}, c, false, 5, id);
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
    
    void changeAdjacentCubePoly(int face, boolean state) {
        for(int i = 0; i < Screen.Cubes.size(); i ++) {
            for(int j = 0; j < 6; j ++) {
                if(Screen.Cubes.get(i).getCoords()[0] == getAdjacentCube(j)[0] && Screen.Cubes.get(i).getCoords()[1] == 
                    getAdjacentCube(j)[1] && Screen.Cubes.get(i).getCoords()[2] == getAdjacentCube(j)[2]) {
                    Screen.Cubes.get(i).changeAdjacentPoly(face,state);
                    Screen.Cubes.get(i).updatePoly();
                }
            }
        }
    }

    void removeCube()
    {
        for(int i = 0; i < 6; i ++) {
            changeAdjacentCubePoly(i,true);
            Screen.DPolygons.remove(Polys[i]);
        }
        Screen.Cubes.remove(this);
    }
}
