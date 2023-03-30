import java.util.ArrayList;

public class SpatialCalc {
    double angleX;
    double angleY;
    double angleZ;
    double kx;
    double ky;
    double kz;
    double trueX;
    double trueY;
    double trueZ;
    ArrayList<Vector3> points = new ArrayList<Vector3>();

    public SpatialCalc(double startX, double startY, double startZ, double xAngle, double yAngle, double zAngle) {
        kx = startX;
        ky = startY;
        kz = startZ;
        trueX = startX;
        trueY = startY;
        trueZ = startZ;
        angleX = xAngle;
        angleY = yAngle;
        angleZ = zAngle;
        points.add(new Vector3(-0.5 + kx, -0.5 + ky, -0.5 + kz));
        points.add(new Vector3(0.5 + kx, -0.5 + ky, -0.5 + kz));
        points.add(new Vector3(0.5 + kx, 0.5 + ky, -0.5 + kz));
        points.add(new Vector3(-0.5 + kx, 0.5 + ky, -0.5 + kz));
        points.add(new Vector3(-0.5 + kx, -0.5 + ky, 0.5 + kz));
        points.add(new Vector3(0.5 + kx, -0.5 + ky, 0.5 + kz));
        points.add(new Vector3(0.5 + kx, 0.5 + ky, 0.5 + kz));
        points.add(new Vector3(-0.5 + kx, 0.5 + ky, 0.5 + kz));
    }

    private double cos(double angle) {return(Math.cos((Math.PI * angle) / 180.0));}

    private double sin(double angle) {return(Math.sin((Math.PI * angle) / 180.0));}
    
    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    private double[][] multiplyMatrices(double A[][], double B[][])
    {
        int row1 = A.length;
        int col1 = A[0].length;
        int row2 = B.length;
        int col2 = B[0].length;

        double C[][] = new double[row1][col2];

        for (int i = 0; i < row1; i++) {
            for (int j = 0; j < col2; j++) {
                for (int k = 0; k < row2; k++)
                    C[i][j] += A[i][k] * B[k][j];
            }
        }

        return(C);
    }

    private double[] multiplyMatrices(double A[][], double B[])
    {
        int colsA = A[0].length;
        int rowsA = A.length;
        int rowsB = B.length;

        double[] result = new double[rowsA];
        for (int j = 0; j < rowsA; j ++) {
                double sum = 0;
                for (int n = 0; n < colsA; n ++) {
                    sum += A[j][n] * B[n];
                }
                result[j] = sum;
            }
        return result;
    }

    private double[][] scaleMatrix(double[][] matrix, double scale) {
        for(int row = 0; row < matrix.length; row ++) {
            for (int col = 0; col < matrix[row].length; col++) {
                matrix[row][col] *= scale;
            }
        }
        return(matrix);
    }
    
    private double[] scaleMatrix(double[] matrix, double scale) {
        for(int i = 0; i < matrix.length; i ++) {
            matrix[i] *= scale;
        }
        return(matrix);
    }

    private void printMatrix(double[][] matrix) {
        for(int row = 0; row < matrix.length; row ++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.println("[" + row + "][" + col + "] = " + matrix[row][col]);
            }
        }
    }
    
    private void printMatrix(double[] matrix) {
        for(int i = 0; i < matrix.length; i ++) {
            System.out.println("[" + i + "] = " + matrix[i]);
        }
    }
    
    public double getOGX() {return(trueX);}
    public double getOGY() {return(trueY);}
    public double getOGZ() {return(trueZ);}

    public void setPosition(double x, double y, double z) {
        kx = x;
        ky = y;
        kz = z;
        points.clear();
        points.add(new Vector3(-0.5 + kx, -0.5 + ky, -0.5 + kz));
        points.add(new Vector3(0.5 + kx, -0.5 + ky, -0.5 + kz));
        points.add(new Vector3(0.5 + kx, 0.5 + ky, -0.5 + kz));
        points.add(new Vector3(-0.5 + kx, 0.5 + ky, -0.5 + kz));
        points.add(new Vector3(-0.5 + kx, -0.5 + ky, 0.5 + kz));
        points.add(new Vector3(0.5 + kx, -0.5 + ky, 0.5 + kz));
        points.add(new Vector3(0.5 + kx, 0.5 + ky, 0.5 + kz));
        points.add(new Vector3(-0.5 + kx, 0.5 + ky, 0.5 + kz));
    }
    
    public void setAngle(double xAngle, double yAngle, double zAngle) {
        angleX = xAngle;
        angleY = yAngle;
        angleZ = zAngle;
    }
    
    public double[][] draw() {
        double [][] rotationZ = new double [][]{
            {cos(angleZ), -sin(angleZ), 0},
            {sin(angleZ), cos(angleZ), 0},
            {0, 0, 1},
        };

        double [][] rotationX = new double [][]{
            {1, 0, 0},
            {0, cos(angleX), -sin(angleX)},
            {0, sin(angleX), cos(angleX)},
        };

        double [][] rotationY = new double [][]{
            {cos(angleY), 0, sin(angleY)},
            {0, 1, 0},
            {-sin(angleY), 0, cos(angleY)},
        };

        double[][] projected = new double[8][];
        for(int i = 0; i < points.size(); i ++) {
            double[] rotated = multiplyMatrices(rotationY, points.get(i).toMatrix());
            rotated = multiplyMatrices(rotationX, rotated);
            rotated = multiplyMatrices(rotationZ, rotated);
            double z = 1 / rotated[2];

            double[][] projection = new double[][]{
                    {z, 0, 0},
                    {0, z, 0},
                };

            double[] projected2d = multiplyMatrices(projection, rotated);

            projected2d = scaleMatrix(projected2d, 200);

            projected[i] = projected2d;
        }
        return(projected);
    }
}

class Vector3 {
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
