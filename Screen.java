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
import java.io.IOException;
import java.io.File;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Screen extends JPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
    
    //ArrayList of all the 3D polygons - each 3D polygon has a 2D 'PolygonObject' inside called 'DrawablePolygon'
    static ArrayList<DPolygon> DPolygons = new ArrayList<DPolygon>();

    static Chunk[] Chunks;
    
    //The polygon that the mouse is currently over
    static PolygonObject PolygonOver = null;
    
    private Font minecraftFont;
    private BufferedImage hotbar;
    private BufferedImage selector;
    
    private int selectedCube = -1;
    private int selectedFace = -1;

    //Used for keeping mouse in center
    Robot r;

    static double[] ViewFrom = new double[] {0, 0, 0},    
                    ViewTo = new double[] {0, 0, 0},
                    LightDir = new double[] {1, 1, 1};

    //The smaller the zoom the more zoomed out you are and vice versa, although altering too far from 1000 will make it look pretty weird
    static double zoom = 1000, minZoom = 500, maxZoom = 2500, MouseX = 0, MouseY = 0, MovementSpeed = 0.5;
    
    //FPS is a bit primitive, you can set the MaxFPS as high as you want
    private double drawFPS = 0, maxFPS = 60, sleepTime = 1000.0/maxFPS, lastRefresh = 0, startTime = System.currentTimeMillis(), lastFPSCheck = 0, checks = 0;
    //VertLook goes from 0.999 to -0.999, minus being looking down and + looking up, HorLook takes any number and goes round in radians
    //aimSight changes the size of the center-cross. The lower HorRotSpeed or VertRotSpeed, the faster the camera will rotate in those directions
    private double VertLook = -0.9, HorLook = 0, aimSight = 4, HorRotSpeed = 900, VertRotSpeed = 2200, SunPos = Math.PI / 4, zVel = 0;

    private double movementFactor = 0.085, heightTol = 1.4, sideTol = 0.8, gravity = 0.007, jumpVel = 0.13, reachDist = 12, daylightCycle = 1, scroll = 0;
    //will hold the order that the polygons in the ArrayList DPolygon should be drawn meaning DPolygon.get(newPolygonOrder[0]) gets drawn first
    private int[] newPolygonOrder;
    
    private boolean canJump = false;
    
    boolean[] Keys = new boolean[8];
    
    int numberKey = 1;
    
    long time = 0;
    long pastTime = 0;
    
    static final int worldSize = 32;
    static final int chunkSize = 1;
    static final int worldHeight = 32;
    static final double renderDistance = 16;
    static final double renderDistanceInChunks = renderDistance / chunkSize;
    
    /*
     * Stone is ID 0
     * Cobblestone is ID 1
     * Dirt is ID 2
     * Grass is ID 3
     * Planks are ID 4
     * Logs are ID 5
     * Leaves are ID 6
     * Sand is ID 7
     * Gravel is ID 8
     * Water is ID 9
     */
    
    static String[] colorNames = new String[]{"stone","cobblestone","dirt","grass","planks","logs","leaves","sand","glass","bedrock"};
    
    static final int stone = 0;
    static final int cobblestone = 1;
    static final int dirt = 2;
    static final int grass = 3;
    static final int planks = 4;
    static final int logs = 5;
    static final int leaves = 6;
    static final int sand = 7;
    static final int glass = 8;
    static final int water = 9;
    static final int bedrock = 10;
    
    static Color darkGreen = new Color(0,170,0);
    static Color lightGreen = new Color(0,210,0);
    static Color waterBlue = new Color(0,0,180,120);
    
    static Color black = new Color(20,20,20);
    static Color darkGray = new Color(65,65,65);
    static Color lightGray = new Color(100,100,100);
    static Color darkBrown = new Color(77,47,18);
    static Color midBrown = new Color(133,73,45);
    static Color lightBrown = new Color(175,125,77);
    static Color beige = new Color(232,214,158);
    static Color translucent = new Color(200, 200, 230, 40);
    
    static Color bgColor = new Color(50,150,255);
    
    private void resourceLoader() {
        try {
            minecraftFont = Font.createFont(Font.TRUETYPE_FONT, new File("Resources/Fonts/font.ttf")).deriveFont(32f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(minecraftFont);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(FontFormatException e) {
            e.printStackTrace();
        }
        
        try {
            hotbar = ImageIO.read(new File("Resources/Images/hotbar.png"));
            selector = ImageIO.read(new File("Resources/Images/selector.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void cubeLoader() {
        final float persistence = 0.17f; //The persistence (connection in the concavity of the ground) in %
        final float treeDensity = 0.03f; //3% of blocks will have trees -> statistically speaking only
        final int octaveCount = 4; // amounts of passes the generator does on the ground for smoothing
        final int minHeight = 14; //minimum height for ground
        final int maxHeight = 25; //highest peak for ground
        final int minDirtDepth = 2; //at minimum there will be 1 grass and 1 dirt block on the surface
        final int maxDirtDepth = 3; //at maximum there will be 1 grass and 2 dirt blocks on the surface
        final int waterDepth = 9; //the z height the water generates at
        final int treeCount = (int)(treeDensity * Math.pow(worldSize, 2));
        
        Chunks = new Chunk[(worldSize / chunkSize) * (worldSize / chunkSize)];
        
        System.out.println("Starting map generation!");
        
        int[][][] map = NoiseGenerator.generatePerlinVolume(worldSize, worldSize, octaveCount, persistence, worldHeight, minHeight, maxHeight, minDirtDepth, maxDirtDepth, waterDepth, treeCount);
        for(int x = 0; x < worldSize / chunkSize; x ++) {
            for(int y = 0; y < worldSize / chunkSize; y ++) {
                Chunks[x + (y * worldSize / chunkSize)] = new Chunk(map,chunkSize,worldHeight,x,y);
            }
        }
        
        System.out.println("Map generated.");
        
        ViewFrom[0] = worldSize / 2 + 0.5;
        ViewFrom[1] = worldSize / 2 + 0.5;
        for(int i = 0; i < map[worldSize / 2][worldSize / 2].length; i ++) {
            if(map[worldSize / 2][worldSize / 2][i] == -1) {
                ViewFrom[2] = i + 1;
                break;
            }
        }
        
        System.out.println("Initially drawing chunks.");
        
        for(int i = 0; i < Chunks.length; i ++) {
            drawChunk(i);
            System.out.println(100 * (double)(i + 1) / (double)Chunks.length + "%");
        }
        
        System.out.println("Done drawing chunks.");
    }
    
    public Screen()
    {
        this.addKeyListener(this);
        setFocusable(true);        
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        
        invisibleMouse();
        
        resourceLoader();
        
        cubeLoader();
        
        refreshCubes();
    }
    
    public void refreshCubes() {
        System.out.println("Checking cube adjacencies & deleting faces");
        for(int i = 0; i < Chunks.length; i ++) {
            if(Chunks[i] != null) {
                for(int j = 0; j < Chunks[i].getCubeArray().size(); j ++) {
                    Chunks[i].getCubeArray().get(j).softAdjacencyCheck();
                }
            }
            System.out.println(100 * (double)(i + 1) / Chunks.length + "%");
        }
        System.out.println("Done checking adjacencies. Finally updating polygons.");
        for(int i = 0; i < Chunks.length; i ++) {
            if(Chunks[i] != null) {
                for(int j = 0; j < Chunks[i].getCubeArray().size(); j ++) {
                    Chunks[i].getCubeArray().get(j).updatePoly();
                }
            }
            System.out.println(100 * (double)(i + 1) / Chunks.length + "%");
        }
        
        System.out.println("Done!");
    }
    
    public void drawChunk(int i) {
        if(!Chunks[i].isAlreadyInMap()) {
            for(int j = 0; j < Chunks[i].getCubeArray().size(); j ++) {
                Chunks[i].getCubeArray().get(j).updatePoly();
            }
            Chunks[i].setAlreadyInMap(true);
        }
    }
    
    public void undrawChunk(int i) {
        if(Chunks[i].isAlreadyInMap()) {
            for(int j = 0; j < Chunks[i].getCubeArray().size(); j ++) {
                Chunks[i].getCubeArray().get(j).removeCubeInChunk();
            }
            Chunks[i].setAlreadyInMap(false);
        }
    }
    
    public void determineChunksToDraw() {
        for(int i = 0; i < Chunks.length; i ++) {
            if(Chunks[i].getDistFromCenter(ViewFrom[0] / (double)chunkSize, ViewFrom[1] / (double)chunkSize) <= renderDistanceInChunks) {
                drawChunk(i);
            } else {
                undrawChunk(i);
            }
        }
    }
    
    public int[] getChunkCoordsIn(int x, int y) {
        return new int[]{x / chunkSize,y / chunkSize};
    }
    
    static int getChunkNumberIn(int x, int y) {
        for(int i = 0; i < Chunks.length; i ++) {
            if(Chunks[i].getX() == x / chunkSize && Chunks[i].getY() == y / chunkSize) {
                return i;
            }
        } 
        return -1;
    }
    
    private boolean withinRange(double val, double min, double max) {
        return val <= max && val >= min;
    }
    
    private boolean willCollide(double[] attrs) {
        if(withinRange(attrs[0],0,worldSize) && withinRange(attrs[1],0,worldSize) && withinRange(attrs[2],0,worldHeight)) { //checking if the coordinate is within the world limits
            double x = attrs[0] + (attrs[3] / 2);
            double y = attrs[1] + (attrs[4] / 2);
            double z = attrs[2] + (attrs[5] / 2);
            double px = ViewFrom[0];
            double py = ViewFrom[1];
            double pz = ViewFrom[2];
            double xDiff = Math.abs(x - px);
            double yDiff = Math.abs(y - py);
            double zDiff = Math.abs(z - pz);
            if(zDiff <= heightTol + 0.5 - 0.005 && xDiff <= sideTol - 0.005 && yDiff <= sideTol - 0.005) { //checking if the coordinate is inside of the player
                return(true);
            } else {
                return false;
            }
        }
        return true;
    }
    
    public void paintComponent(Graphics g)
    {
        //Clear screen and draw background color
        time = System.currentTimeMillis();
        daylightCycle = 0.8 * Math.pow(Math.sin((double)time / 360000.0),2) + 0.2;
        Color fillColor = new Color((int)((double)bgColor.getRed() * daylightCycle), (int)((double)bgColor.getGreen() * daylightCycle), (int)((double)bgColor.getBlue() * daylightCycle));
        g.setColor(fillColor);
        g.fillRect(0, 0, (int)DDDTutorial.ScreenSize.getWidth(), (int)DDDTutorial.ScreenSize.getHeight());

        CameraMovement();
        
        determineChunksToDraw();
        
        //Calculated all that is general for this camera position
        Calculator.setPredeterminedInfo();

        ControlSunAndLight();

        //Updates each polygon for this camera position
        for(int i = 0; i < DPolygons.size(); i ++) {
            DPolygons.get(i).updatePolygon();
        }

        //Set drawing order so closest polygons gets drawn last
        setOrder();
            
        //Set the polygon that the mouse is currently over
        setPolygonOver();
            
        //draw polygons in the Order that is set by the 'setOrder' function
        
        for(int i = 0; i < newPolygonOrder.length; i++) {
            DPolygons.get(newPolygonOrder[i]).getDrawablePolygon().drawPolygon(g);
        }
        //draw the cross in the center of the screen
        drawMouseAim(g);            
        
        //FPS display
        g.setFont(minecraftFont);
        
        g.drawString("FPS: " + (int)drawFPS, 40, 40);
        
        g.drawString("X Y Z: " + Calculator.roundTo(ViewFrom[0] - worldSize / 2,2) + " "  + Calculator.roundTo(ViewFrom[1] - worldSize / 2,2) + " "  + Calculator.roundTo(ViewFrom[2],2), 40, 80);
        
        g.drawImage(hotbar,((int)DDDTutorial.ScreenSize.getWidth() - hotbar.getWidth()) / 2, (int)DDDTutorial.ScreenSize.getHeight() - hotbar.getHeight(), null);
        
        //0 at coordinate 121,28 -> increments of 92.2 pixels
        
        g.drawImage(selector,((int)DDDTutorial.ScreenSize.getWidth() - hotbar.getWidth()) / 2 + 121 + (int)(92.2 * (numberKey - 1)),(int)DDDTutorial.ScreenSize.getHeight() - hotbar.getHeight() - 5, null);
        
        for(int i = 0; i < 9; i ++) {
            int xCoord = ((int)DDDTutorial.ScreenSize.getWidth() - hotbar.getWidth()) / 2 + 175 + (int)(92.2 * i);
            int yCoord = (int)DDDTutorial.ScreenSize.getHeight() - (hotbar.getHeight() / 2);
            int xOffset = 25;
            int yOffset = 25;
            
            g.setColor(Cube.getColor(i)[2]);
            g.fillRect(xCoord - xOffset, yCoord - yOffset, xOffset * 2, yOffset * 2);
            if(i == 3) {
                g.setColor(Cube.getColor(i)[1]);
                g.fillRect(xCoord - xOffset, yCoord - yOffset, xOffset * 2, (int)(yOffset * 0.25));
            } else if(i == 5) {
                g.setColor(Cube.getColor(i)[1]);
                g.fillRect(xCoord - xOffset, yCoord - yOffset, xOffset * 2, (int)(yOffset * 0.25));
            }
        }
        
        drawSelectedItemText(g, colorNames[numberKey - 1], minecraftFont);
        
        SleepAndRefresh();
    }
    
    private void drawSelectedItemText(Graphics g, String text, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = ((int)DDDTutorial.ScreenSize.getWidth() - metrics.stringWidth(text)) / 2;
        int pxDist = 10;
        int y = (int)DDDTutorial.ScreenSize.getHeight() - hotbar.getHeight() - metrics.getHeight() - pxDist;
        g.setColor(Color.BLACK);
        g.setFont(font);
        g.drawString(text, x, y);
    }
    
    void setOrder()
    {
        double[] k = new double[DPolygons.size()];
        newPolygonOrder = new int[DPolygons.size()];
        
        for(int i=0; i<DPolygons.size(); i++)
        {
            k[i] = DPolygons.get(i).getAvgDist();
            newPolygonOrder[i] = i;
        }
        
        double temp;
        int tempr;        
        for (int a = 0; a < k.length-1; a++) {
            for (int b = 0; b < k.length-1; b++) {
                if(k[b] < k[b + 1])
                {
                    temp = k[b];
                    tempr = newPolygonOrder[b];
                    newPolygonOrder[b] = newPolygonOrder[b + 1];
                    k[b] = k[b + 1];
                       
                    newPolygonOrder[b + 1] = tempr;
                    k[b + 1] = temp;
                }
            }
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
        long timeSLU = (long) (System.currentTimeMillis() - lastRefresh); 

        checks ++;            
        if(checks >= 15)
        {
            drawFPS = checks/((System.currentTimeMillis() - lastFPSCheck)/1000.0);
            lastFPSCheck = System.currentTimeMillis();
            checks = 0;
        }
        
        if(timeSLU < 1000.0/maxFPS)
        {
            try {
                Thread.sleep((long) (1000.0/maxFPS - timeSLU));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }    
        }
        
        lastRefresh = System.currentTimeMillis();
        
        repaint();
    }
    
    void ControlSunAndLight()
    {
        double mapSize = 2500;
        LightDir[0] = mapSize/2 - (mapSize/2 + Math.cos(SunPos) * mapSize * 10);
        LightDir[1] = mapSize/2 - (mapSize/2 + Math.sin(SunPos) * mapSize * 10);
        LightDir[2] = -200;
    }
    
    void CameraMovement()
    {
        Vector ViewVector = new Vector(ViewTo[0] - ViewFrom[0], ViewTo[1] - ViewFrom[1], ViewTo[2] - ViewFrom[2]);
        double xMove = 0, yMove = 0, zMove = 0;
        double adjMovementFactor = (60.0 * movementFactor) / Calculator.clamp(drawFPS,15,maxFPS);
        
        if(Keys[6]) {
            adjMovementFactor *= 1.4;
        }
        
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
        
        for(int i = 0; i < Chunks.length; i ++) {
            if(Chunks[i] != null) {
                if(Chunks[i].getDistFromCenter(ViewFrom[0] / (double)chunkSize,ViewFrom[1] / (double)chunkSize) <= 1.41 * (double)chunkSize) {
                    for(int j = 0; j < Chunks[i].getCubeArray().size(); j ++) {
                        if(Chunks[i].getCubeArray().get(j).getDist(ViewFrom[0],ViewFrom[1],ViewFrom[2]) < 3 && !Chunks[i].getCubeArray().get(j).isWater()) {
                            double[] attrs = Chunks[i].getCubeArray().get(j).getAttributes();
                            double x = attrs[0] + (attrs[3] / 2);
                            double y = attrs[1] + (attrs[4] / 2);
                            double z = attrs[2] + (attrs[5] / 2);
                            double px = ViewFrom[0];
                            double py = ViewFrom[1];
                            double pz = ViewFrom[2];
                            double xDiff = Math.abs(x - px);//Calculator.roundTo(Math.abs(x - px),4);
                            double yDiff = Math.abs(y - py);//Calculator.roundTo(Math.abs(y - py),4);
                            double zDiff = Math.abs(z - pz);//Calculator.roundTo(Math.abs(z - pz),4);
                            double hzDiff = Math.abs(z + 0.5 - pz);
                            if(zDiff <= heightTol + 0.5 && xDiff <= sideTol - 0.005 && yDiff <= sideTol - 0.005) {
                                if(hzDiff < 1 && yDiff > xDiff + 0.005 && py >= y + (sideTol - adjMovementFactor)) {
                                    ViewFrom[1] = y + sideTol;
                                } else if(hzDiff < 1 && yDiff > xDiff + 0.005 && py <= y - (sideTol - adjMovementFactor)) {
                                    ViewFrom[1] = y - sideTol;
                                } else if(hzDiff < 1 && xDiff > yDiff + 0.005 && px >= x + (sideTol - adjMovementFactor)) {
                                    ViewFrom[0] = x + sideTol;
                                } else if(hzDiff < 1 && xDiff > yDiff + 0.005 && px <= x - (sideTol - adjMovementFactor)) {
                                    ViewFrom[0] = x - sideTol;
                                } else if(zDiff < heightTol + 0.5 && pz >= z + (1.5 - adjMovementFactor) && xDiff < (sideTol - adjMovementFactor) - 0.005 && yDiff < (sideTol - adjMovementFactor) - 0.005) {
                                    ViewFrom[2] = z + heightTol + 0.5;
                                    canJump = true;
                                    zVel = 0;
                                } else if(zDiff < heightTol - 0.5 && pz <= z - (0.5 - adjMovementFactor) && xDiff < (sideTol - adjMovementFactor) - 0.005 && yDiff < (sideTol - adjMovementFactor) - 0.005) {
                                    ViewFrom[2] = z - heightTol + 0.5;
                                }
                            }
                        }
                    }
                }
            }
        }
        ViewFrom[0] = Calculator.clamp(ViewFrom[0],0,worldSize);
        ViewFrom[1] = Calculator.clamp(ViewFrom[1],0,worldSize);
        
        updateView();
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
        for(int i = newPolygonOrder.length-1; i >= 0; i --) {
            if(DPolygons.get(newPolygonOrder[i]).getDist() <= 6) {
                if(DPolygons.get(newPolygonOrder[i]).getDrawablePolygon().MouseOver() && DPolygons.get(newPolygonOrder[i]).getDraw() 
                        && DPolygons.get(newPolygonOrder[i]).getDrawablePolygon().isVisible() && !DPolygons.get(newPolygonOrder[i]).isWater())
                {
                    PolygonOver = DPolygons.get(newPolygonOrder[i]).getDrawablePolygon();
                    selectedCube = DPolygons.get(newPolygonOrder[i]).getID();
                    selectedFace = DPolygons.get(newPolygonOrder[i]).getSide();
                    break;
                }
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
        if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
            Keys[6] = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_F) {
            Keys[7] = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_1) { numberKey = 1; }
        if(e.getKeyCode() == KeyEvent.VK_2) { numberKey = 2; }
        if(e.getKeyCode() == KeyEvent.VK_3) { numberKey = 3; }
        if(e.getKeyCode() == KeyEvent.VK_4) { numberKey = 4; }
        if(e.getKeyCode() == KeyEvent.VK_5) { numberKey = 5; }
        if(e.getKeyCode() == KeyEvent.VK_6) { numberKey = 6; }
        if(e.getKeyCode() == KeyEvent.VK_7) { numberKey = 7; }
        if(e.getKeyCode() == KeyEvent.VK_8) { numberKey = 8; }
        if(e.getKeyCode() == KeyEvent.VK_9) { numberKey = 9; }
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
        if(e.getKeyCode() == KeyEvent.VK_CONTROL) 
            Keys[6] = false;
        if(e.getKeyCode() == KeyEvent.VK_F) 
            Keys[7] = false;
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
                for(int i = 0; i < Chunks.length; i ++) {
                    for(int j = 0; j < Chunks[i].getCubeArray().size(); j ++) {
                        if(Chunks[i].getCubeArray().get(j).getID() == selectedCube && !Chunks[i].getCubeArray().get(j).isWater() && !Chunks[i].getCubeArray().get(j).isBedrock()) {
                            double[][] adjacentCoords = new double[7][3];
                            for(int f = 0; f < 6; f ++) {
                                adjacentCoords[f] = Chunks[i].getCubeArray().get(j).getAdjacentCube(f);
                            }
                            adjacentCoords[6] = new double[]{Chunks[i].getCubeArray().get(j).getCoords()[0],
                                Chunks[i].getCubeArray().get(j).getCoords()[1], Chunks[i].getCubeArray().get(j).getCoords()[2]};
                            Chunks[i].getCubeArray().get(j).removeCube();
                            Chunks[i].getCubeArray().get(0).hardAdjacencyCheck(adjacentCoords);
                        }
                    }
                }
            }
        }
        
        if(m.getButton() == MouseEvent.BUTTON3) {
            if(selectedCube != -1) {
                for(int i = 0; i < Chunks.length; i ++) {
                    for(int j = 0; j < Chunks[i].getCubeArray().size(); j ++) {
                        if(Chunks[i].getCubeArray().get(j).getID() == selectedCube && !Chunks[i].getCubeArray().get(j).isWater()) {
                            double[] coords = Chunks[i].getCubeArray().get(j).getAdjacentCube(selectedFace);
                            int chunkIn = getChunkNumberIn((int)coords[0],(int)coords[1]);
                            if(!willCollide(new double[]{coords[0],coords[1],coords[2],1,1,1})) {
                                Chunks[chunkIn].addCube(new Cube(coords[0],coords[1],coords[2],1,1,1,numberKey - 1));
                                Chunks[chunkIn].getCubeArray().get(Chunks[chunkIn].getCubeArray().size() - 1).hardAdjacencyCheck();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent m) {
    }

    public void mouseWheelMoved(MouseWheelEvent m) {
        if(Keys[7]) {
            zoom -= 25 * m.getUnitsToScroll();
            zoom = Calculator.clamp(zoom,minZoom,maxZoom);
        } else {
            scroll += m.getUnitsToScroll();
            if(Math.abs(scroll) > 2) {
                numberKey += scroll / Math.abs(scroll);
                scroll /= 2;
                if(numberKey > 9) {
                    numberKey = 1;
                } else if(numberKey < 1) {
                    numberKey = 9;
                }
            }
        }
    }
}
