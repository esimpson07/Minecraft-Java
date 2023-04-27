public class Calculator {
    static double t = 0;
    static Vector W1, W2, ViewVector, RotationVector, DirectionVector, PlaneVector1, PlaneVector2;
    static Plane p;
    static double[] CalcFocusPos = new double[2];
    
    static double[] CalculatePositionP(double[] ViewFrom, double[] ViewTo, double x, double y, double z)
    {        
        double[] projP = getProj(ViewFrom, ViewTo, x, y, z, p);
        double[] drawP = getDrawP(projP[0], projP[1], projP[2]);
        return drawP;
    }

    static double[] getProj(double[] ViewFrom, double[] ViewTo, double x, double y, double z, Plane P)
    {
        Vector ViewToPoint = new Vector(x - ViewFrom[0], y - ViewFrom[1], z - ViewFrom[2]);

               t = (P.ret.x*P.p[0] + P.ret.y*P.p[1] +  P.ret.z*P.p[2]
                 - (P.ret.x*ViewFrom[0] + P.ret.y*ViewFrom[1] + P.ret.z*ViewFrom[2]))
                 / (P.ret.x*ViewToPoint.x + P.ret.y*ViewToPoint.y + P.ret.z*ViewToPoint.z);

        x = ViewFrom[0] + ViewToPoint.x * t;
        y = ViewFrom[1] + ViewToPoint.y * t;
        z = ViewFrom[2] + ViewToPoint.z * t;
        
        return new double[] {x, y, z};
    }
    
    static double[] getDrawP(double x, double y, double z)
    {
        double DrawX = W2.x * x + W2.y * y + W2.z * z;
        double DrawY = W1.x * x + W1.y * y + W1.z * z;        
        return new double[]{DrawX, DrawY};
    }
    
    static double clamp(double val, double min, double max) {
        if(val > max) {
            return(max);
        } else if(val < min) {
            return(min);
        } else {
            return(val);
        }
    }
    
    static Vector getRotationVector(double[] ViewFrom, double[] ViewTo)
    {
        double dx = Math.abs(ViewFrom[0]-ViewTo[0]);
        double dy = Math.abs(ViewFrom[1]-ViewTo[1]);
        double xRot, yRot;
        xRot=dy/(dx+dy);        
        yRot=dx/(dx+dy);

        if(ViewFrom[1]>ViewTo[1])
            xRot = -xRot;
        if(ViewFrom[0]<ViewTo[0])
            yRot = -yRot;

            Vector V = new Vector(xRot, yRot, 0);
        return V;
    }
    
    static void SetPrederterminedInfo()
    {
        ViewVector = new Vector(Screen.ViewTo[0] - Screen.ViewFrom[0], Screen.ViewTo[1] - Screen.ViewFrom[1], Screen.ViewTo[2] - Screen.ViewFrom[2]);            
        DirectionVector = new Vector(1, 1, 1);                
        PlaneVector1 = ViewVector.CrossProduct(DirectionVector);
        PlaneVector2 = ViewVector.CrossProduct(PlaneVector1);
        p = new Plane(PlaneVector1, PlaneVector2, Screen.ViewTo);

        RotationVector = Calculator.getRotationVector(Screen.ViewFrom, Screen.ViewTo);
        W1 = ViewVector.CrossProduct(RotationVector);
        W2 = ViewVector.CrossProduct(W1);

        CalcFocusPos = Calculator.CalculatePositionP(Screen.ViewFrom, Screen.ViewTo, Screen.ViewTo[0], Screen.ViewTo[1], Screen.ViewTo[2]);
        CalcFocusPos[0] = Screen.zoom * CalcFocusPos[0];
        CalcFocusPos[1] = Screen.zoom * CalcFocusPos[1];
    }
}