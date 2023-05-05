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
import java.util.Random;

import javax.swing.JPanel;

public class Screen extends JPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
    
    //ArrayList of all the 3D polygons - each 3D polygon has a 2D 'PolygonObject' inside called 'DrawablePolygon'
    static ArrayList<DPolygon> DPolygons = new ArrayList<DPolygon>();
    
    static ArrayList<Cube> Cubes = new ArrayList<Cube>();
    
    Random random = new Random();
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
    double drawFPS = 0, MaxFPS = 60, SleepTime = 1000.0/MaxFPS, LastRefresh = 0, StartTime = System.currentTimeMillis(), LastFPSCheck = 0, Checks = 0;
    //VertLook goes from 0.999 to -0.999, minus being looking down and + looking up, HorLook takes any number and goes round in radians
    //aimSight changes the size of the center-cross. The lower HorRotSpeed or VertRotSpeed, the faster the camera will rotate in those directions
    double VertLook = -0.9, HorLook = 0, aimSight = 4, HorRotSpeed = 900, VertRotSpeed = 2200, SunPos = Math.PI / 4, zVel = 0;

    double movementFactor = 0.2, heightTol = 2, sideTol = 1.85, gravity = 0.013, jumpVel = 0.25, reachDist = 12;
    //will hold the order that the polygons in the ArrayList DPolygon should be drawn meaning DPolygon.get(NewOrder[0]) gets drawn first
    int[] NewOrder;

    static boolean OutLines = true;
    private boolean canJump = true;
    boolean[] Keys = new boolean[6];
    
    long repaintTime = 0;
    
    int color = 9;
    
    Color bgColor = new Color(153,204,255);
    
    Color red = new Color(180,0,0);
    Color green = new Color(0,180,0);
    Color blue = new Color(0,0,180);
    
    Color orange = new Color(180,120,0);
    Color yellow = new Color(180,180,0);
    Color cyan = new Color(0,180,180);
    Color magenta = new Color(180,0,180);
    
    Color black = new Color(20,20,20);
    Color gray = new Color(75,75,75);
    Color brown = new Color(133,73,45);
    
    Color[] colors = new Color[]{brown,red,green,blue,orange,yellow,cyan,magenta,black,gray};
    
    public Screen()
    {
        this.addKeyListener(this);
        setFocusable(true);        
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        
        double period = ((random.nextFloat() + 0.5) * 0.5);
        double amp = 2 * random.nextFloat();
        invisibleMouse();
        int size = 20;
        for(int i = 0; i < 20; i ++) {
            for(int j = 0; j < 20; j ++) {
                for(int h = 0; h < 20; h ++) {
                    Cubes.add(new Cube(2 * i - 20, 2 * j - 20, 2 * h, 2, 2, 2, colors[(int)(Math.random() * 10)],Cubes.size()));
                }
            }
        }
        
        for(int i = 0; i < Cubes.size(); i ++) {
            Cubes.get(i).checkAdjacency();
        }
        
        for(int i = 0; i < Cubes.size(); i ++) {
            Cubes.get(i).updatePoly();
        }
    }
    
    boolean willCollide(double[] attrs) {
        double x = attrs[0] + (attrs[3] / 2);
        double y = attrs[1] + (attrs[4] / 2);
        double z = attrs[2] + (attrs[5] / 2);
        double px = ViewFrom[0];
        double py = ViewFrom[1];
        double pz = ViewFrom[2];
        double xDiff = Math.abs(x - px);
        double yDiff = Math.abs(y - py);
        double zDiff = Math.abs(z + 1 - pz);
        if(zDiff < heightTol + 1 && xDiff < sideTol && yDiff < sideTol) {
            return(true);
        } else {
            return(false);
        }
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
        for(int i = 0; i < DPolygons.size(); i++) {
            DPolygons.get(i).updatePolygon();
        }

        //Set drawing order so closest polygons gets drawn last
        setOrder();
        
        deletePolys();
            
        //Set the polygon that the mouse is currently over
        setPolygonOver();
            
        //draw polygons in the Order that is set by the 'setOrder' function
        for(int i = 0; i < NewOrder.length; i++) {
            DPolygons.get(NewOrder[i]).getDrawablePolygon().drawPolygon(g);
        }
        //draw the cross in the center of the screen
        drawMouseAim(g);            
        
        //FPS display
        g.drawString("FPS: " + (int)drawFPS + " (Benchmark)", 40, 40);
        
        g.drawString("X Y Z: " + Calculator.roundTo(ViewFrom[0],2) + " "  + Calculator.roundTo(ViewFrom[1],2) + " "  + Calculator.roundTo(ViewFrom[2],2), 160, 40);
        
        int scale = 30;
        g.setColor(Color.WHITE);
        g.fillRect(((int)DDDTutorial.ScreenSize.getWidth() / 2) - (colors.length / 2) * scale,(int)DDDTutorial.ScreenSize.getHeight() - 2 * scale - 12,colors.length * scale - 17,16);
        for(int i = 0; i < colors.length; i ++) {
            g.setColor(colors[i]);
            g.drawString("|" + i + "|", ((int)DDDTutorial.ScreenSize.getWidth() / 2) - scale * ((colors.length / 2) - i), (int)DDDTutorial.ScreenSize.getHeight() - 2 * scale);
        }
        
        SleepAndRefresh();
    }
    
    void deletePolys() {
        
    }
    
    void setOrder()
    {
        double[] k = new double[DPolygons.size()];
        NewOrder = new int[DPolygons.size()];
        
        for(int i=0; i<DPolygons.size(); i++)
        {
            k[i] = DPolygons.get(i).getAvgDist();
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
        double adjMovementFactor = (60.0 * movementFactor) / Calculator.clamp(drawFPS,15,MaxFPS);
        
        Vector VerticalVector = new Vector (0, 0, 1);
        Vector SideViewVector = ViewVector.CrossProduct(VerticalVector);
        
        ViewFrom[2] += zVel;
        zVel -= gravity;
        zVel = Calculator.clamp(zVel,-15,jumpVel);
        
        if(Keys[0])
        {
            xMove += (adjMovementFactor * ViewVector.getX());
            yMove += (adjMovementFactor * ViewVector.getY());
        }

        if(Keys[2])
        {
            xMove -= (adjMovementFactor * ViewVector.getX());
            yMove -= (adjMovementFactor * ViewVector.getY());
        }
            
        if(Keys[1])
        {
            xMove += (adjMovementFactor * SideViewVector.getX());
            yMove += (adjMovementFactor * SideViewVector.getY());
        }

        if(Keys[3])
        {
            xMove -= (adjMovementFactor * SideViewVector.getX());
            yMove -= (adjMovementFactor * SideViewVector.getY());
        }
        
        Vector MoveVector = new Vector(xMove, yMove, zMove);
        MoveTo(ViewFrom[0] + MoveVector.getX() * adjMovementFactor, ViewFrom[1] + MoveVector.getY() * adjMovementFactor, ViewFrom[2] + MoveVector.getZ() * adjMovementFactor);
        
        for(int i = 0; i < Cubes.size(); i ++) {
            if(Cubes.get(i).getDist(ViewFrom[0],ViewFrom[1],ViewFrom[2]) < 7) {
                double[] attrs = Cubes.get(i).getAttributes();
                double x = attrs[0] + (attrs[3] / 2);
                double y = attrs[1] + (attrs[4] / 2);
                double z = attrs[2] + (attrs[5] / 2);
                double px = ViewFrom[0];
                double py = ViewFrom[1];
                double pz = ViewFrom[2];
                double xDiff = Math.abs(x - px);
                double yDiff = Math.abs(y - py);
                double zDiff = Math.abs(z + 1 - pz);
                if(zDiff <= heightTol + 1 && xDiff <= sideTol && yDiff <= sideTol) {
                    if(zDiff < heightTol + 1 && yDiff > xDiff + 0.01 && py > y + (sideTol - adjMovementFactor)) {
                        ViewFrom[1] = y + sideTol;
                    } else if(zDiff < heightTol + 1 && yDiff > xDiff + 0.01 && py < y - (sideTol - adjMovementFactor)) {
                        ViewFrom[1] = y - sideTol;
                    } else if(zDiff < heightTol + 1 && xDiff > yDiff + 0.01 && px > x + (sideTol - adjMovementFactor)) {
                        ViewFrom[0] = x + sideTol;
                    } else if(zDiff < heightTol + 1 && xDiff > yDiff + 0.01 && px < x - (sideTol - adjMovementFactor)) {
                        ViewFrom[0] = x - sideTol;
                    } else if(zDiff <= heightTol + 1 && pz > z + (1 - adjMovementFactor)) {
                        ViewFrom[2] = z + heightTol + 2;
                        canJump = true;
                        zVel = 0;
                    } else if(zDiff <= heightTol + 1 && pz < z - (1 - adjMovementFactor)) {
                        ViewFrom[2] = z - heightTol;
                    }
                }
            }
            updateView();
        }
    }

    void MoveTo(double x, double y, double z)
    {
        ViewFrom[0] = x;
        ViewFrom[1] = y;
        ViewFrom[2] = z;
    }

    void setPolygonOver()
    {
        PolygonOver = null;
        selectedCube = -1;
        for(int i = NewOrder.length-1; i >= 0; i --) {
            if(DPolygons.get(NewOrder[i]).getDrawablePolygon().MouseOver() && DPolygons.get(NewOrder[i]).getDraw() 
                    && DPolygons.get(NewOrder[i]).getDrawablePolygon().isVisible())
            {
                PolygonOver = DPolygons.get(NewOrder[i]).getDrawablePolygon();
                selectedCube = DPolygons.get(NewOrder[i]).getID();
                selectedFace = DPolygons.get(NewOrder[i]).getSide();
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
        
        VertLook = Calculator.clamp(VertLook,-0.99999,0.99999);
        
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
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            Keys[4] = true;
            if(canJump) {
                zVel = jumpVel;
                ViewFrom[2] += 0.01;
                canJump = false;
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
            Keys[5] = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_0)
            color = 0;
        if(e.getKeyCode() == KeyEvent.VK_1)
            color = 1;
        if(e.getKeyCode() == KeyEvent.VK_2)
            color = 2;
        if(e.getKeyCode() == KeyEvent.VK_3)
            color = 3;
        if(e.getKeyCode() == KeyEvent.VK_4)
            color = 4;
        if(e.getKeyCode() == KeyEvent.VK_5)
            color = 5;
        if(e.getKeyCode() == KeyEvent.VK_6)
            color = 6;
        if(e.getKeyCode() == KeyEvent.VK_7)
            color = 7;
        if(e.getKeyCode() == KeyEvent.VK_8)
            color = 8;
        if(e.getKeyCode() == KeyEvent.VK_9)
            color = 9;
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
                for(int i = 0; i < Cubes.size(); i ++) {
                    if(Cubes.get(i).getID() == selectedCube) {
                        Cubes.get(i).removeCube();
                    }
                }
            }
        }
        
        if(m.getButton() == MouseEvent.BUTTON3) {
            if(selectedCube != -1) {
                for(int i = 0; i < Cubes.size(); i ++) {
                    if(Cubes.get(i).getID() == selectedCube) {
                        double[] coords = Cubes.get(i).getAdjacentCube(selectedFace);
                        if(!willCollide(new double[]{coords[0],coords[1],coords[2],2,2,2})) {
                            Cubes.add(new Cube(coords[0],coords[1],coords[2],2,2,2,colors[color],(int)(random.nextFloat() * 999999)));
                        }
                        break;
                    }
                }
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
