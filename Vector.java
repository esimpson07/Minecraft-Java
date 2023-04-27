
public class Vector {
    double x, y, z;
    public Vector(double x, double y, double z) {
        double l = Math.sqrt(x*x + y*y + z*z);
        if(l>0) {
            this.x = x/l;
            this.y = y/l;
            this.z = z/l;
        }
    }
    
    Vector CrossProduct(Vector v) {
        return new Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }
}