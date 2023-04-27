import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class PolygonObject {
    Polygon p;
    Color c;
    boolean draw = true, visible = true, seeThrough;
    double lighting = 1;
    
    public PolygonObject(double[] x, double[] y, Color c, int n, boolean seeThrough)
    {
        p = new Polygon();
        for(int i = 0; i<x.length; i++)
            p.addPoint((int)x[i], (int)y[i]);
        this.c = c;
        this.seeThrough = seeThrough;
    }
    
    void updatePolygon(double[] x, double[] y)
    {
        p.reset();
        for(int i = 0; i<x.length; i++)
        {
            p.xpoints[i] = (int) x[i];
            p.ypoints[i] = (int) y[i];
            p.npoints = x.length;
        }
    }
    
    void drawPolygon(Graphics g)
    {
        if(draw && visible)
        {
            g.setColor(new Color((int)(c.getRed() * lighting), (int)(c.getGreen() * lighting), (int)(c.getBlue() * lighting)));
            if(seeThrough)
                g.drawPolygon(p);
            else
                g.fillPolygon(p);
            if(Screen.OutLines)
            {
                g.setColor(new Color(0, 0, 0));
                g.drawPolygon(p);
            }

            if(Screen.PolygonOver == this)
            {
                g.setColor(new Color(255, 255, 255, 100));
                g.fillPolygon(p);
            }
        }
    }
    
    boolean MouseOver()
    {
        return p.contains(DDDTutorial.ScreenSize.getWidth()/2, DDDTutorial.ScreenSize.getHeight()/2);
    }
}