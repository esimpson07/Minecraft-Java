
public class Vector {
    private double x, y, z;
    public Vector(double x, double y, double z) {
        double l = Math.sqrt(x*x + y*y + z*z);
        if(l > 0) {
            this.x = x/l;
            this.y = y/l;
            this.z = z/l;
        }
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public void setX(double val) { x = val; }
    public void setY(double val) { y = val; }
    public void setZ(double val) { z = val; }
    
    public Vector CrossProduct(Vector v) {
        return new Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }
}
