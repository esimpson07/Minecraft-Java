import java.util.ArrayList;

public class SpatialCalc {
    double angleX;
    double angleY;
    double angleZ;
    double kx;
    double ky;
    double kz;
    ArrayList<Vector3> points = new ArrayList<Vector3>();

    public SpatialCalc(double startX, double startY, double startZ, double xAngle, double yAngle, double zAngle) {
        kx = startX;
        ky = startY;
        kz = startZ;
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

    private void setPosition(double x, double y, double z) {
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
    
    private void setAngle(double xAngle, double yAngle, double zAngle) {
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
            System.out.print("rotZ: ");
            printMatrix(rotationZ);
            double[] rotated = multiplyMatrices(rotationY, points.get(i).toMatrix());
            rotated = multiplyMatrices(rotationX, rotated);
            System.out.print("Pre mult: ");
            printMatrix(rotated);
            rotated = multiplyMatrices(rotationZ, rotated);
            System.out.print("Post mult: ");
            printMatrix(rotated);
            double z = 1 / rotated[2];

            double[][] projection = new double[][]{
                    {z, 0, 0},
                    {0, z, 0},
                };

            double[] projected2d = multiplyMatrices(projection, rotated);

            projected2d = scaleMatrix(projected2d, 100);

            projected[i] = projected2d;
        }
        printMatrix(projected);

        return(projected);
    }
}