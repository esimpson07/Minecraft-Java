import java.awt.*;  
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;  
import javax.swing.JTextField;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class DisplayGraphics extends Canvas implements KeyListener, MouseListener, MouseMotionListener { 
    private int width = 1000;
    private int height = 600;
    private int mXOG = width/2;
    private int mYOG = height/2;
    private int mXDist;
    private int mYDist;
    
    private double tickDist = 0.1;
    private double dpi = 1;
    private double px = 0;
    private double py = 0;
    
    private double radius = 2;
    private double angleY = 0;
    private double angleZ = 0;
    //x, z, y (side to side, height, forward to back)
    SpatialCalc cube1 = new SpatialCalc(0 + px,0,2 + py,0,angleY,0);
    SpatialCalc cube2 = new SpatialCalc(-1 + px,0,2 + py,0,angleY,0);
    SpatialCalc cube3 = new SpatialCalc(-2 + px,0,2 + py,0,angleY,0);
    SpatialCalc cube4 = new SpatialCalc(-1 + px,1,2 + py,0,angleY,0);
    SpatialCalc[] allCubes = new SpatialCalc[]{cube1,cube2,cube3,cube4}; 
    
    public DisplayGraphics() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    public void mouseMoved(MouseEvent e) {
        System.out.println("moved");
        mXDist = mXOG - e.getX();
        mYDist = mYOG - e.getY();
        angleY += dpi * 180 * (2 * (double)(mXDist) / (double)(width));
        mXOG = e.getX();
        mYOG = e.getY();
        changePlayerAngle(0,angleY,angleZ);
        revalidate();
        repaint();
    }
    public void mouseDragged(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}  
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}  
    public void mouseReleased(MouseEvent e) {}  
    @Override
    public void keyPressed(KeyEvent event) {
        String key = KeyEvent.getKeyText(event.getKeyCode());
        if(key.equals("W")) {
            px += tickDist * sin(angleY + 180);
            py += tickDist * cos(angleY + 180);
            changePlayerPosition(px,py,0);
        } else if(key.equals("S")) {
            px += tickDist * sin(angleY);
            py += tickDist * cos(angleY);
            changePlayerPosition(px,py,0);
        } else if(key.equals("D")) {
            px += tickDist * sin(angleY - 90);
            py += tickDist * cos(angleY - 90);
            changePlayerPosition(px,py,0);
        } else if(key.equals("A")) {
            px += tickDist * sin(angleY + 90);
            py += tickDist * cos(angleY + 90);
            changePlayerPosition(px,py,0);
        } 
        revalidate();
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent event) {
        System.out.println("Key Released: " + KeyEvent.getKeyText(event.getKeyCode()));
    }

    @Override
    public void keyTyped(KeyEvent event) {

    }
        
    public void paint(Graphics g) {
        super.paint(g);
        System.out.println("painting");
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

    public void changePlayerPosition(double x, double y, double z) {
        cube1.setPosition(0 + x, 0 + z, 2 + y);
        cube2.setPosition(-1 + x, 0 + z, 2 + y);
        cube3.setPosition(-2 + x, 0 + z, 2 + y);
        cube4.setPosition(-1 + x, 1 + z, 2 + y);
        allCubes[0] = cube1;
        allCubes[1] = cube2;
        allCubes[2] = cube3;
        allCubes[3] = cube4;
    }
    
    public void changePlayerAngle(double xAngle, double yAngle, double zAngle) {
        cube1.setAngle(xAngle,yAngle,zAngle);
        cube2.setAngle(xAngle,yAngle,zAngle);
        cube3.setAngle(xAngle,yAngle,zAngle);
        cube4.setAngle(xAngle,yAngle,zAngle);
        allCubes[0] = cube1;
        allCubes[1] = cube2;
        allCubes[2] = cube3;
        allCubes[3] = cube4;
    }

    public int round(double val) {
        return((int)(val + 0.5));
    }

    private void connect(int a, int b, double[][] points, Graphics g) {
        g.drawLine(round(points[a][0]) + (width / 2),round(points[a][1]) + (height / 2),
            round(points[b][0]) + (width / 2),round(points[b][1]) + (height / 2));
    }

    public static void main(String args[]) {  
        DisplayGraphics m=new DisplayGraphics();  
        JFrame f=new JFrame();
        f.add(m);
        Container contentPane = f.getContentPane();
        JTextField textField = new JTextField();
        textField.addKeyListener(m);
        contentPane.add(textField, BorderLayout.NORTH);
        f.setSize(1000,600);  
        f.setVisible(true);  
    }  

}  
