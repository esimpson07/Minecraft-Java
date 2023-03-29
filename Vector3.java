public class Vector3 {
    double x;
    double y;
    double z;
    
    public Vector3(double X, double Y, double Z) {
        x = X;
        y = Y;
        z = Z;
    }
    
    public void setX(double X) {x = X;}
    public void setY(double Y) {y = Y;}
    public void setZ(double Z) {z = Z;}
    public double X() {return(x);}
    public double Y() {return(y);}
    public double Z() {return(z);}
    
    public double[] toMatrix() {
        double [] vectorMatrix = new double[]{x,y,z};
        return(vectorMatrix);
    }
}