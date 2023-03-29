import java.awt.*;  
import javax.swing.JFrame;  

public class DisplayGraphics extends Canvas { 
    private int width = 1000;
    private int height = 600;
    private double radius = 2;
    private double angle = 45; //degrees
    //rotation radius from 0,0,0 is 2 to the camera
    
    SpatialCalc cube1 = new SpatialCalc(0,0,2,0,angle,0);
    SpatialCalc cube2 = new SpatialCalc(-1,0,2,0,angle,0);
    SpatialCalc cube3 = new SpatialCalc(-2,0,2,0,angle,0);
    SpatialCalc cube4 = new SpatialCalc(-1,1,2,0,angle,0);
    SpatialCalc[] allCubes = new SpatialCalc[]{cube1,cube2,cube3,cube4};

    public void paint(Graphics g) {
        for(int i = 0; i < 360; i ++) {
            System.out.println("X = " + radius * sin(i) + ", Y = " + radius * cos(i));
        }
        for(int j = 0; j < allCubes.length; j ++) {
            SpatialCalc cube = allCubes[j];
            double[][] allPoints = cube.draw();

            for (int i = 0; i < 4; i++) {
                connect(i, (i + 1) % 4, allPoints, g);
                connect(i + 4, ((i + 1) % 4) + 4, allPoints, g);
                connect(i, i + 4, allPoints, g);
            }
        }
    }
    
    private double cos(double angle) {return(Math.cos((Math.PI * angle) / 180.0));}

    private double sin(double angle) {return(Math.sin((Math.PI * angle) / 180.0));}

    private void connect(int a, int b, double[][] points, Graphics g) {
        g.drawLine(round(points[a][0]) + (width / 2),round(points[a][1]) + (height / 2),
            round(points[b][0]) + (width / 2),round(points[b][1]) + (height / 2));
    }

    public int round(double val) {
        return((int)(val + 0.5));
    }

    public static void main(String[] args) {  
        DisplayGraphics m=new DisplayGraphics();  
        JFrame f=new JFrame();  
        f.add(m);  
        f.setSize(1000,600);  
        f.setVisible(true);  
    }  

}  