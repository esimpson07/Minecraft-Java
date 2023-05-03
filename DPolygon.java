import java.awt.Color;

public class DPolygon {
    private PolygonObject DrawablePolygon;
    private Color c;
    
    private double[] calcPos, newX, newY;
    private double[] x, y, z;
    private double avgDist;
    private int side, id;
    
    private boolean draw = true, seeThrough = false;
    
    public DPolygon(double[] x, double[] y,  double[] z, Color c, boolean seeThrough, int side, int id)
    {
        this.x = x;
        this.y = y;
        this.z = z;        
        this.c = c;
        this.id = id;
        this.side = side;
        this.seeThrough = seeThrough;
        createPolygon();
    }
    
    void createPolygon()
    {
        DrawablePolygon = new PolygonObject(new double[x.length], new double[x.length], c, Screen.DPolygons.size(), seeThrough);
    }
    
    void updatePolygon()
    {        
        newX = new double[x.length];
        newY = new double[x.length];
        draw = true;
        for(int i=0; i<x.length; i++)
        {
            calcPos = Calculator.calculatePositionP(Screen.ViewFrom, Screen.ViewTo, x[i], y[i], z[i]);
            newX[i] = (DDDTutorial.ScreenSize.getWidth()/2 - Calculator.calcFocusPos[0]) + calcPos[0] * Screen.zoom;
            newY[i] = (DDDTutorial.ScreenSize.getHeight()/2 - Calculator.calcFocusPos[1]) + calcPos[1] * Screen.zoom;            
            if(Calculator.t < 0) {
                draw = false;
            }
        }
        
        calcLighting();
        
        DrawablePolygon.setDraw(draw);
        DrawablePolygon.updatePolygon(newX, newY);
        avgDist = GetDist();
    }
    
    int getSide() {
        return side;
    }
    
    int getID() {
        return id;
    }
    
    void calcLighting()
    {
        Plane lightingPlane = new Plane(this);
        double angle = Math.acos(((lightingPlane.getRetVector().getX() * Screen.LightDir[0]) + 
              (lightingPlane.getRetVector().getY() * Screen.LightDir[1]) + (lightingPlane.getRetVector().getZ() * Screen.LightDir[2]))
              /(Math.sqrt(Screen.LightDir[0] * Screen.LightDir[0] + Screen.LightDir[1] * Screen.LightDir[1] + Screen.LightDir[2] * Screen.LightDir[2])));
        
        DrawablePolygon.setLighting(Calculator.clamp(0.2 + 1 - Math.sqrt(Math.toDegrees(angle)/180),0,1));

    }
        
    double GetDist()
    {
        double total = 0;
        for(int i=0; i<x.length; i++) {
            total += GetDistanceToP(i);
        }
        return total / x.length;
    }
    
    PolygonObject getDrawablePolygon() {
        return DrawablePolygon;
    }
    
    void setDraw(boolean value) {
        draw = value;
    }
    
    boolean getDraw() {
        return draw;
    }
    
    double getAvgDist() {
        return avgDist;
    }
    
    double[] getX() { return x; }
    double[] getY() { return y; }
    double[] getZ() { return z; }
    void setX(double[] value) { x = value; }
    void setY(double[] value) { y = value; }
    void setZ(double[] value) { z = value; } 
    
    double GetDistanceToP(int i)
    {
        return Math.sqrt((Screen.ViewFrom[0]-x[i])*(Screen.ViewFrom[0]-x[i]) + 
                         (Screen.ViewFrom[1]-y[i])*(Screen.ViewFrom[1]-y[i]) +
                         (Screen.ViewFrom[2]-z[i])*(Screen.ViewFrom[2]-z[i]));
    }
}
