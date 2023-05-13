public class Calculator {
    static double t = 0;
    static Vector W1, W2, ViewVector, RotationVector, DirectionVector, PlaneVector1, PlaneVector2;
    static Plane p;
    static double[] calcFocusPos = new double[2];
    
    public static double[] calculatePositionP(double[] ViewFrom, double[] ViewTo, double x, double y, double z)
    {        
        double[] projP = getProj(ViewFrom, ViewTo, x, y, z, p);
        double[] drawP = getDrawP(projP[0], projP[1], projP[2]);
        return drawP;
    }

    public static double[] getProj(double[] ViewFrom, double[] ViewTo, double x, double y, double z, Plane P)
    {
        Vector ViewToPoint = new Vector(x - ViewFrom[0], y - ViewFrom[1], z - ViewFrom[2]);

               t = (P.getRetVector().getX() *P.getP()[0] + P.getRetVector().getY() * P.getP()[1] +  P.getRetVector().getZ() * P.getP()[2]
                 - (P.getRetVector().getX() *ViewFrom[0] + P.getRetVector().getY() *ViewFrom[1] + P.getRetVector().getZ() * ViewFrom[2]))
                 / (P.getRetVector().getX() *ViewToPoint.getX() + P.getRetVector().getY() * ViewToPoint.getY() + P.getRetVector().getZ() * ViewToPoint.getZ());

        x = ViewFrom[0] + ViewToPoint.getX() * t;
        y = ViewFrom[1] + ViewToPoint.getY() * t;
        z = ViewFrom[2] + ViewToPoint.getZ() * t;
        
        return new double[] {x, y, z};
    }
    
    public static double[] getDrawP(double x, double y, double z)
    {
        double DrawX = W2.getX() * x + W2.getY() * y + W2.getZ() * z;
        double DrawY = W1.getX() * x + W1.getY() * y + W1.getZ() * z;        
        return new double[]{DrawX, DrawY};
    }
    
    public static double clamp(double val, double min, double max) {
        if(val > max) {
            return(max);
        } else if(val < min) {
            return(min);
        } else {
            return(val);
        }
    }
    
    public static boolean withinRange(double val, double min, double max) {
        return val >= min && val <= max;
    }
    
    public static double roundTo(double val, double places) {
        double scale = Math.pow(10,places);
        return Math.round(val * scale) / scale;
    }
    
    public static Vector getRotationVector(double[] ViewFrom, double[] ViewTo)
    {
        double dx = Math.abs(ViewFrom[0]-ViewTo[0]);
        double dy = Math.abs(ViewFrom[1]-ViewTo[1]);
        double xRot, yRot;
        xRot=dy/(dx+dy);        
        yRot=dx/(dx+dy);

        if(ViewFrom[1]>ViewTo[1]) {
            xRot = -xRot;
        }
        
        if(ViewFrom[0]<ViewTo[0]) {
            yRot = -yRot;
        }
            Vector V = new Vector(xRot, yRot, 0);
        return V;
    }
    
    public static void setPredeterminedInfo()
    {
        ViewVector = new Vector(Screen.ViewTo[0] - Screen.ViewFrom[0], Screen.ViewTo[1] - Screen.ViewFrom[1], Screen.ViewTo[2] - Screen.ViewFrom[2]);            
        DirectionVector = new Vector(1, 1, 1);                
        PlaneVector1 = ViewVector.CrossProduct(DirectionVector);
        PlaneVector2 = ViewVector.CrossProduct(PlaneVector1);
        p = new Plane(PlaneVector1, PlaneVector2, Screen.ViewTo);

        RotationVector = getRotationVector(Screen.ViewFrom, Screen.ViewTo);
        W1 = ViewVector.CrossProduct(RotationVector);
        W2 = ViewVector.CrossProduct(W1);

        calcFocusPos = calculatePositionP(Screen.ViewFrom, Screen.ViewTo, Screen.ViewTo[0], Screen.ViewTo[1], Screen.ViewTo[2]);
        calcFocusPos[0] = Screen.zoom * calcFocusPos[0];
        calcFocusPos[1] = Screen.zoom * calcFocusPos[1];
    }
}
