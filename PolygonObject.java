import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class PolygonObject {
    private Polygon poly;
    private Color color;
    private boolean draw = true, visible = true, outlines = true, normal;
    private double lighting = 1;
    
    public PolygonObject(double[] x, double[] y, Color c, int n, boolean normal)
    {
        poly = new Polygon();
        for(int i = 0; i<x.length; i++) {
            poly.addPoint((int)x[i], (int)y[i]);
        }
        this.color = c;
        this.normal = normal;
    }
    
    void updatePolygon(double[] x, double[] y)
    {
        poly.reset();
        for(int i = 0; i<x.length; i++)
        {
            poly.xpoints[i] = (int) x[i];
            poly.ypoints[i] = (int) y[i];
            poly.npoints = x.length;
        }
    }
    
    void drawPolygon(Graphics g)
    {
        if(draw && visible)
        {
            g.setColor(new Color((int)(color.getRed() * lighting), (int)(color.getGreen() * lighting), (int)(color.getBlue() * lighting), color.getAlpha()));
            g.fillPolygon(poly);
            if(outlines)
            {
                g.setColor(new Color(0, 0, 0));
                g.drawPolygon(poly);
            }

            if((Screen.PolygonOver == this) && normal) {
                g.setColor(color);
                g.fillPolygon(poly);
            }
        }
    }
    
    void setDraw(boolean value) {
        draw = value;
    }
    
    boolean getDraw() {
        return draw;
    }
    
    void setVisible(boolean value) {
        visible = value;
    }
    
    boolean isVisible() {
        return visible;
    }
    
    boolean isNormal() {
        return normal;
    }
    
    void setLighting(double value) {
        lighting = value;
    }
    
    double getLighting() {
        return lighting;
    }
    
    boolean MouseOver()
    {
        return poly.contains(DDDTutorial.ScreenSize.getWidth()/2, DDDTutorial.ScreenSize.getHeight()/2);
    }
}
