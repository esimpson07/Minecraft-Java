public class Plane {
    Vector v1, v2, ret;
    double[] p = new double[3];
    
    public Plane(DPolygon DP)
    {
        p[0] = DP.x[0]; 
        p[1] = DP.y[0]; 
        p[2] = DP.z[0]; 
        
        v1 = new Vector(DP.x[1] - DP.x[0], 
                        DP.y[1] - DP.y[0], 
                        DP.z[1] - DP.z[0]);

        v2 = new Vector(DP.x[2] - DP.x[0], 
                        DP.y[2] - DP.y[0], 
                        DP.z[2] - DP.z[0]);
        
        ret = v1.CrossProduct(v2);
    }
    
    public Plane(Vector ve1, Vector ve2, double[] Z)
    {
        p = Z;
        
        v1 = ve1;
        
        v2 = ve2;
        
        ret = v1.CrossProduct(v2);
    }
}