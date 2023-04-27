import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Screen extends JPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
    
    //ArrayList of all the 3D polygons - each 3D polygon has a 2D 'PolygonObject' inside called 'DrawablePolygon'
    static ArrayList<DPolygon> DPolygons = new ArrayList<DPolygon>();
    
    static ArrayList<Cube> Cubes = new ArrayList<Cube>();
    
    //The polygon that the mouse is currently over
    static PolygonObject PolygonOver = null;
    private int selectedCube = -1;
    private int selectedFace = -1;

    //Used for keeping mouse in center
    Robot r;

    static double[] ViewFrom = new double[] { 0, 0, 4},    
                    ViewTo = new double[] {0, 0, 0},
                    LightDir = new double[] {1, 1, 1};

    //The smaller the zoom the more zoomed out you are and visa versa, although altering too far from 1000 will make it look pretty weird
    static double zoom = 1000, MinZoom = 500, MaxZoom = 2500, MouseX = 0, MouseY = 0, MovementSpeed = 0.5;
    
    //FPS is a bit primitive, you can set the MaxFPS as high as u want
    double drawFPS = 0, MaxFPS = 120, SleepTime = 1000.0/MaxFPS, LastRefresh = 0, StartTime = System.currentTimeMillis(), LastFPSCheck = 0, Checks = 0;
    //VertLook goes from 0.999 to -0.999, minus being looking down and + looking up, HorLook takes any number and goes round in radians
    //aimSight changes the size of the center-cross. The lower HorRotSpeed or VertRotSpeed, the faster the camera will rotate in those directions
    double VertLook = -0.9, HorLook = 0, aimSight = 4, HorRotSpeed = 900, VertRotSpeed = 2200, SunPos = Math.PI / 4;

    double movementFactor = 0.05, heightTol = 4, sideTol = 2;
    //will hold the order that the polygons in the ArrayList DPolygon should be drawn meaning DPolygon.get(NewOrder[0]) gets drawn first
    int[] NewOrder;

    static boolean OutLines = true;
    boolean[] Keys = new boolean[6];
    
    long repaintTime = 0;
    
    Color gray = new Color(50,50,50);
    Color bgColor = new Color(153,204,255);
    
    public Screen()
    {
        this.addKeyListener(this);
        setFocusable(true);        
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        
        invisibleMouse();
        
        Cubes.add(new Cube(0, 0, 0, 2, 2, 2, gray));
        Cubes.add(new Cube(2, 2, 0, 2, 2, 2, gray));
        Cubes.add(new Cube(2, 0, 0, 2, 2, 2, gray));
        Cubes.add(new Cube(0, 2, 0, 2, 2, 2, gray));
        Cubes.add(new Cube(4, 4, 0, 2, 2, 2, gray));
        Cubes.add(new Cube(4, 2, 0, 2, 2, 2, gray));
        Cubes.add(new Cube(4, 0, 0, 2, 2, 2, gray));
        Cubes.add(new Cube(2, 4, 0, 2, 2, 2, gray));
        Cubes.add(new Cube(0, 4, 0, 2, 2, 2, gray));
    }    
    
    public void paintComponent(Graphics g)
    {
        //Clear screen and draw background color
        g.setColor(bgColor);
        g.fillRect(0, 0, (int)DDDTutorial.ScreenSize.getWidth(), (int)DDDTutorial.ScreenSize.getHeight());

        CameraMovement();
        
        //Calculated all that is general for this camera position
        Calculator.SetPrederterminedInfo();

        ControlSunAndLight();

        //Updates each polygon for this camera position
        for(int i = 0; i < DPolygons.size(); i++)
            DPolygons.get(i).updatePolygon();

        //Set drawing order so closest polygons gets drawn last
        setOrder();
            
        //Set the polygon that the mouse is currently over
        setPolygonOver();
            
        //draw polygons in the Order that is set by the 'setOrder' function
        for(int i = 0; i < NewOrder.length; i++)
            DPolygons.get(NewOrder[i]).DrawablePolygon.drawPolygon(g);
            
        //draw the cross in the center of the screen
        drawMouseAim(g);            
        
        //FPS display
        g.drawString("FPS: " + (int)drawFPS + " (Benchmark)", 40, 40);
        
        SleepAndRefresh();
    }
    
    void setOrder()
    {
        double[] k = new double[DPolygons.size()];
        NewOrder = new int[DPolygons.size()];
        
        for(int i=0; i<DPolygons.size(); i++)
        {
            k[i] = DPolygons.get(i).AvgDist;
            NewOrder[i] = i;
        }
        
        double temp;
        int tempr;        
        for (int a = 0; a < k.length-1; a++)
            for (int b = 0; b < k.length-1; b++)
                if(k[b] < k[b + 1])
                {
                    temp = k[b];
                    tempr = NewOrder[b];
                    NewOrder[b] = NewOrder[b + 1];
                    k[b] = k[b + 1];
                       
                    NewOrder[b + 1] = tempr;
                    k[b + 1] = temp;
                }
    }
        
    void invisibleMouse()
    {
         Toolkit toolkit = Toolkit.getDefaultToolkit();
         BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT); 
         Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, new Point(0,0), "InvisibleCursor");        
         setCursor(invisibleCursor);
    }
    
    void drawMouseAim(Graphics g)
    {
        g.setColor(Color.black);
        g.drawLine((int)(DDDTutorial.ScreenSize.getWidth()/2 - aimSight), (int)(DDDTutorial.ScreenSize.getHeight()/2), (int)(DDDTutorial.ScreenSize.getWidth()/2 + aimSight), (int)(DDDTutorial.ScreenSize.getHeight()/2));
        g.drawLine((int)(DDDTutorial.ScreenSize.getWidth()/2), (int)(DDDTutorial.ScreenSize.getHeight()/2 - aimSight), (int)(DDDTutorial.ScreenSize.getWidth()/2), (int)(DDDTutorial.ScreenSize.getHeight()/2 + aimSight));            
    }

    void SleepAndRefresh()
    {
        long timeSLU = (long) (System.currentTimeMillis() - LastRefresh); 

        Checks ++;            
        if(Checks >= 15)
        {
            drawFPS = Checks/((System.currentTimeMillis() - LastFPSCheck)/1000.0);
            LastFPSCheck = System.currentTimeMillis();
            Checks = 0;
        }
        
        if(timeSLU < 1000.0/MaxFPS)
        {
            try {
                Thread.sleep((long) (1000.0/MaxFPS - timeSLU));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }    
        }
                
        LastRefresh = System.currentTimeMillis();
        
        repaint();
    }
    
    void ControlSunAndLight()
    {
        double mapSize = 2500; //50^2
        LightDir[0] = mapSize/2 - (mapSize/2 + Math.cos(SunPos) * mapSize * 10);
        LightDir[1] = mapSize/2 - (mapSize/2 + Math.sin(SunPos) * mapSize * 10);
        LightDir[2] = -200;
    }
    
    void CameraMovement()
    {
        Vector ViewVector = new Vector(ViewTo[0] - ViewFrom[0], ViewTo[1] - ViewFrom[1], ViewTo[2] - ViewFrom[2]);
        double xMove = 0, yMove = 0, zMove = 0;
        Vector VerticalVector = new Vector (0, 0, 1);
        Vector SideViewVector = ViewVector.CrossProduct(VerticalVector);
        
        if(Keys[0])
        {
            xMove += (movementFactor * ViewVector.x);
            yMove += (movementFactor * ViewVector.y);
        }

        if(Keys[2])
        {
            xMove -= (movementFactor * ViewVector.x);
            yMove -= (movementFactor * ViewVector.y);
        }
            
        if(Keys[1])
        {
            xMove += (movementFactor * SideViewVector.x);
            yMove += (movementFactor * SideViewVector.y);
        }

        if(Keys[3])
        {
            xMove -= (movementFactor * SideViewVector.x);
            yMove -= (movementFactor * SideViewVector.y);
        }
        
        if(Keys[4])
        {
            zMove += movementFactor;
        }
        
        if(Keys[5])
        {
            zMove -= movementFactor;
        }

        for(int i = 0; i < Cubes.size(); i ++) {
            double[] attrs = Cubes.get(i).getAttributes();
            double x = attrs[0] + (attrs[3] / 2);
            double y = attrs[1] + (attrs[4] / 2);
            double z = attrs[2] + (attrs[5] / 2);
            double px = ViewFrom[0];
            double py = ViewFrom[1];
            double pz = ViewFrom[2];
            double xDiff = Math.abs(x - px);
            double yDiff = Math.abs(y - py);
            double zDiff = Math.abs(z - pz);
            if(zDiff < heightTol && xDiff < sideTol && yDiff < sideTol) {
                System.out.println(px - x);
                System.out.println(py - y);
                if(yDiff > xDiff && py > y + (sideTol - movementFactor)) {
                    ViewFrom[1] = y + sideTol;
                } else if(yDiff > xDiff && py < y - (sideTol - movementFactor)) {
                    ViewFrom[1] = y - sideTol;
                } else if(xDiff > yDiff && px > x + (sideTol - movementFactor)) {
                    ViewFrom[0] = x + sideTol;
                } else if(xDiff > yDiff && px < x - (sideTol - movementFactor)) {
                    ViewFrom[0] = x - sideTol;
                } else if(zDiff < heightTol && pz > z) {
                    ViewFrom[2] = z + heightTol;
                } else if(zDiff < heightTol && pz < z) {
                    ViewFrom[2] = z - heightTol;
                }
            }
        }
        Vector MoveVector = new Vector(xMove, yMove, zMove);
        MoveTo(ViewFrom[0] + MoveVector.x * movementFactor, ViewFrom[1] + MoveVector.y * movementFactor, ViewFrom[2] + MoveVector.z * movementFactor);
    }

    void MoveTo(double x, double y, double z)
    {
        ViewFrom[0] = x;
        ViewFrom[1] = y;
        ViewFrom[2] = z;
        updateView();
    }

    void setPolygonOver()
    {
        PolygonOver = null;
        selectedCube = -1;
        for(int i = NewOrder.length-1; i >= 0; i --) {
            if(DPolygons.get(NewOrder[i]).DrawablePolygon.MouseOver() && DPolygons.get(NewOrder[i]).draw 
                    && DPolygons.get(NewOrder[i]).DrawablePolygon.visible)
            {
                PolygonOver = DPolygons.get(NewOrder[i]).DrawablePolygon;
                selectedCube = NewOrder[i] / 6;
                selectedFace = NewOrder[i] % 6;
                break;
            }
        }
    }

    void MouseMovement(double NewMouseX, double NewMouseY)
    {        
            double difX = (NewMouseX - DDDTutorial.ScreenSize.getWidth()/2);
            double difY = (NewMouseY - DDDTutorial.ScreenSize.getHeight()/2);
            difY *= 6 - Math.abs(VertLook) * 5;
            VertLook -= difY  / VertRotSpeed;
            HorLook += difX / HorRotSpeed;
            
            VertLook = Calculator.clamp(VertLook,-0.999,0.999);
            
            updateView();
    }
    
    void updateView()
    {
        double r = Math.sqrt(1 - (VertLook * VertLook));
        ViewTo[0] = ViewFrom[0] + r * Math.cos(HorLook);
        ViewTo[1] = ViewFrom[1] + r * Math.sin(HorLook);        
        ViewTo[2] = ViewFrom[2] + VertLook;
    }
    
    void CenterMouse() 
    {
            try {
                r = new Robot();
                r.mouseMove((int)DDDTutorial.ScreenSize.getWidth()/2, (int)DDDTutorial.ScreenSize.getHeight()/2);
            } catch (AWTException e) {
                e.printStackTrace();
            }
    }
    
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_W)
            Keys[0] = true;
        if(e.getKeyCode() == KeyEvent.VK_A)
            Keys[1] = true;
        if(e.getKeyCode() == KeyEvent.VK_S)
            Keys[2] = true;
        if(e.getKeyCode() == KeyEvent.VK_D)
            Keys[3] = true;
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
            Keys[4] = true;
        if(e.getKeyCode() == KeyEvent.VK_SHIFT)
            Keys[5] = true;
        if(e.getKeyCode() == KeyEvent.VK_O)
            OutLines = !OutLines;
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_W)
            Keys[0] = false;
        if(e.getKeyCode() == KeyEvent.VK_A)
            Keys[1] = false;
        if(e.getKeyCode() == KeyEvent.VK_S)
            Keys[2] = false;
        if(e.getKeyCode() == KeyEvent.VK_D)
            Keys[3] = false;
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
            Keys[4] = false;
        if(e.getKeyCode() == KeyEvent.VK_SHIFT)
            Keys[5] = false;
    }

    public void keyTyped(KeyEvent e) {
    
    }

    public void mouseDragged(MouseEvent m) {
        MouseMovement(m.getX(), m.getY());
        MouseX = m.getX();
        MouseY = m.getY();
        CenterMouse();
    }
    
    public void mouseMoved(MouseEvent m) {
        MouseMovement(m.getX(), m.getY());
        MouseX = m.getX();
        MouseY = m.getY();
        CenterMouse();
    }
    
    public void mouseClicked(MouseEvent m) {
    }

    public void mouseEntered(MouseEvent m) {
    }

    public void mouseExited(MouseEvent m) {
    }

    public void mousePressed(MouseEvent m) {
        if(m.getButton() == MouseEvent.BUTTON1) {
            if(selectedCube != -1) {
                Cubes.get(selectedCube).removeCube();
            }
        }
        
        if(m.getButton() == MouseEvent.BUTTON3) {
            if(selectedCube != -1) {
                double[] coords = Cubes.get(selectedCube).getAdjacentCube(selectedFace);
                Cubes.add(new Cube(coords[0],coords[1],coords[2],2,2,2,gray));
            }
        }
    }

    public void mouseReleased(MouseEvent m) {
    }

    public void mouseWheelMoved(MouseWheelEvent m) {
        zoom -= 25 * m.getUnitsToScroll();
        zoom = Calculator.clamp(zoom,MinZoom,MaxZoom);
    }
}
