public class Plane {
    private Vector v1, v2, ret;
    private double[] p = new double[3];
    
    public Plane(DPolygon DP)
    {
        p[0] = DP.getX()[0]; 
        p[1] = DP.getY()[0]; 
        p[2] = DP.getZ()[0]; 
        
        v1 = new Vector(DP.getX()[1] - DP.getX()[0], 
                        DP.getY()[1] - DP.getY()[0], 
                        DP.getZ()[1] - DP.getZ()[0]);

        v2 = new Vector(DP.getX()[2] - DP.getX()[0], 
                        DP.getY()[2] - DP.getY()[0], 
                        DP.getZ()[2] - DP.getZ()[0]);
        
        ret = v1.CrossProduct(v2);
    }
    
    public Plane(Vector ve1, Vector ve2, double[] Z)
    {
        p = Z;
        
        v1 = ve1;
        
        v2 = ve2;
        
        ret = v1.CrossProduct(v2);
    }
    
    public Vector getRetVector() {
        return ret;
    }
    
    public Vector getV1() {
        return v1;
    }
    
    public Vector getV2() {
        return v2;
    }
    
    public double[] getP() {
        return p;
    }
}
