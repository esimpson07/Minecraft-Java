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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class DisplayGraphics extends Canvas implements KeyListener, MouseListener, MouseMotionListener { 
    private int width = 600;
    private int height = 600;
    private int mXOG = width/2;
    private int mYOG = height/2;
    private int mXDist;
    private int mYDist;
    private byte w = 0;
    private byte a = 0;
    private byte s = 0;
    private byte d = 0;
    
    private double tickDist = 0.1;
    private double dpi = 1;
    private double acceptedFOV = 89.5;
    private double px = 0;
    private double py = 0;
    
    private double angleY = 0;
    private double angleX = 0;
    //x, z, y (side to side, height, forward to back)
    SpatialCalc cube1 = new SpatialCalc(0 + px,0,2 + py,0,angleY,0);
    SpatialCalc cube2 = new SpatialCalc(-3 + px,0,0 + py,0,angleY,0);
    SpatialCalc[] allCubes = new SpatialCalc[]{cube1,cube2};
    
    public DisplayGraphics() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public void mouseMoved(MouseEvent e) {
        mXDist = mXOG - e.getX();
        mYDist = mYOG - e.getY();
        angleY += dpi * 180 * (2 * (double)(mXDist) / (double)(width));
        angleX -= dpi * 180 * (2 * (double)(mYDist) / (double)(width));
        angleY = angleY % 180;
        angleX = angleX % 180;
        mXOG = e.getX();
        mYOG = e.getY();
        setPlayerAngle(angleX,angleY,0);
    }
    public void mouseDragged(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}  
    public void mouseEntered(MouseEvent e) {
        mXOG = e.getX();
        mYOG = e.getY();
    }
    public void mouseExited(MouseEvent e) {}  
    public void mouseReleased(MouseEvent e) {}  
    @Override
    public void keyPressed(KeyEvent event) {
        String key = KeyEvent.getKeyText(event.getKeyCode());
        if(key.equals("W")) {
            w = 1;
        } else if(key.equals("S")) {
            s = 1;
        } else if(key.equals("D")) {
            d = 1;
        } else if(key.equals("A")) {
            a = 1;
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        String key = KeyEvent.getKeyText(event.getKeyCode());
        if(key.equals("W")) {
            w = 0;
        } else if(key.equals("S")) {
            s = 0;
        } else if(key.equals("D")) {
            d = 0;
        } else if(key.equals("A")) {
            a = 0;
        }
    }
    @Override
    public void keyTyped(KeyEvent event) {}
    
    public void paint(Graphics g) {
        super.paint(g);
        for(int j = 0; j < allCubes.length; j ++) {
            double[][] drawPoints = allCubes[j].draw();
            double relativeAngle = 0;
            ArrayList<Vector3> cubePoints = allCubes[j].getPointArray();
            for(int i = 0; i < 8; i++) {
                relativeAngle = allCubes[j].angleBetween(cubePoints.get(i),new 
                    Vector3(sin(angleY)* cos(angleX),sin(angleX),cos(angleY) * cos(angleX)));
                //System.out.println(relativeAngle);
            }
            
            for(int i = 0; i < 4; i++) {
                connect(i, (i + 1) % 4, drawPoints, g);
                connect(i + 4, ((i + 1) % 4) + 4, drawPoints, g);
                connect(i, i + 4, drawPoints, g);
            }
        }
    }

    private double cos(double angle) {return(Math.cos((Math.PI * angle) / 180.0));}

    private double sin(double angle) {return(Math.sin((Math.PI * angle) / 180.0));}

    public void setPlayerPosition(double x, double y, double z) {
        for(int i = 0; i < allCubes.length; i ++) {
            allCubes[i].setPosition(allCubes[i].getOGX() - x, allCubes[i].getOGY() - y, allCubes[i].getOGZ() - z);
        }
    }
    
    public void setPlayerAngle(double xAngle, double yAngle, double zAngle) {
        for(int i = 0; i < allCubes.length; i ++) {
            allCubes[i].setAngle(xAngle,yAngle,zAngle);
        }
    }

    public int round(double val) {
        return((int)(val + 0.5));
    }

    private void connect(int a, int b, double[][] points, Graphics g) {
        if((points[a][0] != 0 && points[a][1] != 0) && (points[b][0] != 0 && points[b][1] != 0)) {
            g.drawLine(round(points[a][0]) + (width / 2),round(points[a][1]) + (height / 2),
                round(points[b][0]) + (width / 2),round(points[b][1]) + (height / 2));
        }
    }
    
    private void movePlayer(int fwd, int side) {  
        if(fwd == 1) {
            px += tickDist * cos(angleY + 90);
            py += tickDist * sin(angleY + 90);
        } else if(fwd == -1) {
            px += tickDist * cos(angleY - 90);
            py += tickDist * sin(angleY - 90);
        } else if(side == 1) {
            px += tickDist * cos(angleY);
            py += tickDist * sin(angleY);
        } else if(side == -1) {
            px += tickDist * cos(angleY + 180);
            py += tickDist * sin(angleY + 180);
        }
        setPlayerPosition(px,0,py);
    }
    
    private void gameLoop() {
        repaint();
        movePlayer(w - s, d - a);
    }

    public static void main(String args[]) {  
        DisplayGraphics m=new DisplayGraphics();  
        JFrame f=new JFrame();
        f.add(m);
        Container contentPane = f.getContentPane();
        JTextField textField = new JTextField();
        textField.addKeyListener(m);
        contentPane.add(textField, BorderLayout.NORTH);
        f.setSize(m.width,m.height);  
        f.setVisible(true);  
        new javax.swing.Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m.gameLoop();
            }
        }).start();
    }  

}  
