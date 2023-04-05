import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics;
import javax.imageio.ImageIO;

public class SpatialCalc {
    private double angleX;
    private double angleY;
    private double angleZ;
    private double kx;
    private double ky;
    private double kz;
    private double trueX;
    private double trueY;
    private double trueZ;
    private final BufferedImage inputImage;
    ArrayList<Vector3> points = new ArrayList<Vector3>();
    ArrayList<Vector3> faces = new ArrayList<Vector3>();

    public SpatialCalc(double startX, double startY, double startZ, double xAngle, double yAngle, double zAngle, String filepath) {      
        try
        {
            inputImage = ImageIO.read(new File("cat.jpg"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            while(true) {}
        }
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

    private double dotProduct(Vector3 vector1, Vector3 vector2) {
        return vector1.getX()*vector2.getX() + 
        vector1.getY()*vector2.getY() + vector1.getZ()*vector2.getZ();
    }

    private double findMagnitude(Vector3 vector) {
        return(Math.sqrt(Math.pow(vector.getX(),2) + 
                Math.pow(vector.getY(),2) + Math.pow(vector.getZ(),2)));
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
    
    private double[] sortArray(double[] array) {
        for (int j = 0; j < 5; j++) {
            for (int i = 5; i > j; i--) {
                if (array[i - 1] < array[i]) {
                    double swp = array[i];
                    array[i] = array[i - 1];
                    array[i - 1] = swp;
                }
            }
        }
        return(array);
    }
    
    private int findIndex(double array[], double val)
    {
        for(int i = 0; i < array.length; i ++) {
            if (array[i] == val) {
                return i;
            }
        }
        return -1;
    }

    public double getOGX() {return(trueX);}

    public double getOGY() {return(trueY);}

    public double getOGZ() {return(trueZ);}

    public double getX() {return(kx);}

    public double getY() {return(ky);}

    public double getZ() {return(kz);}

    public double angleBetween(Vector3 vector1, Vector3 vector2) {
        return((180 / Math.PI) * Math.acos(dotProduct(vector1,vector2) / (findMagnitude(vector1) * findMagnitude(vector2))));
    }

    public void setPosition(double x, double y, double z) {
        kx = x;
        ky = y;
        kz = z;
        points.clear();
        points.add(new Vector3(-0.5 + kx, -0.5 + ky, -0.5 + kz));//0
        points.add(new Vector3(0.5 + kx, -0.5 + ky, -0.5 + kz));//1
        points.add(new Vector3(0.5 + kx, 0.5 + ky, -0.5 + kz));//2
        points.add(new Vector3(-0.5 + kx, 0.5 + ky, -0.5 + kz));//3
        points.add(new Vector3(-0.5 + kx, -0.5 + ky, 0.5 + kz));//4
        points.add(new Vector3(0.5 + kx, -0.5 + ky, 0.5 + kz));//5
        points.add(new Vector3(0.5 + kx, 0.5 + ky, 0.5 + kz));//6
        points.add(new Vector3(-0.5 + kx, 0.5 + ky, 0.5 + kz));//7
        faces.clear();
        faces.add(new Vector3((points.get(0).getX() + points.get(3).getX() + points.get(4).getX() + points.get(7).getX()) / 4,
                (points.get(0).getY() + points.get(3).getY() + points.get(4).getY() + points.get(7).getY()) / 4,
                (points.get(0).getZ() + points.get(3).getZ() + points.get(4).getZ() + points.get(7).getZ()) / 4));
        faces.add(new Vector3((points.get(1).getX() + points.get(2).getX() + points.get(5).getX() + points.get(6).getX()) / 4,
                (points.get(1).getY() + points.get(2).getY() + points.get(5).getY() + points.get(6).getY()) / 4,
                (points.get(1).getZ() + points.get(2).getZ() + points.get(5).getZ() + points.get(6).getZ()) / 4));
        faces.add(new Vector3((points.get(0).getX() + points.get(1).getX() + points.get(4).getX() + points.get(5).getX()) / 4,
                (points.get(0).getY() + points.get(1).getY() + points.get(4).getY() + points.get(5).getY()) / 4,
                (points.get(0).getZ() + points.get(1).getZ() + points.get(4).getZ() + points.get(5).getZ()) / 4));
        faces.add(new Vector3((points.get(2).getX() + points.get(3).getX() + points.get(6).getX() + points.get(7).getX()) / 4,
                (points.get(2).getY() + points.get(3).getY() + points.get(6).getY() + points.get(7).getY()) / 4,
                (points.get(2).getZ() + points.get(3).getZ() + points.get(6).getZ() + points.get(7).getZ()) / 4));
        faces.add(new Vector3((points.get(0).getX() + points.get(1).getX() + points.get(2).getX() + points.get(3).getX()) / 4,
                (points.get(0).getY() + points.get(1).getY() + points.get(2).getY() + points.get(3).getY()) / 4,
                (points.get(0).getZ() + points.get(1).getZ() + points.get(2).getZ() + points.get(3).getZ()) / 4));
        faces.add(new Vector3((points.get(4).getX() + points.get(5).getX() + points.get(6).getX() + points.get(7).getX()) / 4,
                (points.get(4).getY() + points.get(5).getY() + points.get(6).getY() + points.get(7).getY()) / 4,
                (points.get(4).getZ() + points.get(5).getZ() + points.get(6).getZ() + points.get(7).getZ()) / 4));
    }

    public void setAngle(double xAngle, double yAngle, double zAngle) {
        angleX = xAngle;
        angleY = yAngle;
        angleZ = zAngle;
    }

    public void refreshFaceArray() {
        faces.clear();
        faces.add(new Vector3((points.get(0).getX() + points.get(3).getX() + points.get(4).getX() + points.get(7).getX()) / 4,
                (points.get(0).getY() + points.get(3).getY() + points.get(4).getY() + points.get(7).getY()) / 4,
                (points.get(0).getZ() + points.get(3).getZ() + points.get(4).getZ() + points.get(7).getZ()) / 4));
        faces.add(new Vector3((points.get(1).getX() + points.get(2).getX() + points.get(5).getX() + points.get(6).getX()) / 4,
                (points.get(1).getY() + points.get(2).getY() + points.get(5).getY() + points.get(6).getY()) / 4,
                (points.get(1).getZ() + points.get(2).getZ() + points.get(5).getZ() + points.get(6).getZ()) / 4));
        faces.add(new Vector3((points.get(0).getX() + points.get(1).getX() + points.get(4).getX() + points.get(5).getX()) / 4,
                (points.get(0).getY() + points.get(1).getY() + points.get(4).getY() + points.get(5).getY()) / 4,
                (points.get(0).getZ() + points.get(1).getZ() + points.get(4).getZ() + points.get(5).getZ()) / 4));
        faces.add(new Vector3((points.get(2).getX() + points.get(3).getX() + points.get(6).getX() + points.get(7).getX()) / 4,
                (points.get(2).getY() + points.get(3).getY() + points.get(6).getY() + points.get(7).getY()) / 4,
                (points.get(2).getZ() + points.get(3).getZ() + points.get(6).getZ() + points.get(7).getZ()) / 4));
        faces.add(new Vector3((points.get(0).getX() + points.get(1).getX() + points.get(2).getX() + points.get(3).getX()) / 4,
                (points.get(0).getY() + points.get(1).getY() + points.get(2).getY() + points.get(3).getY()) / 4,
                (points.get(0).getZ() + points.get(1).getZ() + points.get(2).getZ() + points.get(3).getZ()) / 4));
        faces.add(new Vector3((points.get(4).getX() + points.get(5).getX() + points.get(6).getX() + points.get(7).getX()) / 4,
                (points.get(4).getY() + points.get(5).getY() + points.get(6).getY() + points.get(7).getY()) / 4,
                (points.get(4).getZ() + points.get(5).getZ() + points.get(6).getZ() + points.get(7).getZ()) / 4));
    }
    
    public int[] getFacePointsAssociated(int face) {
        if(face == 0) {
            return new int[]{0,3,4,7};
        } else if(face == 1) {
            return new int[]{1,2,5,6};
        } else if(face == 2) {
            return new int[]{0,1,4,5};
        } else if(face == 3) {
            return new int[]{2,3,6,7};
        } else if(face == 4) {
            return new int[]{0,1,2,3};
        } else if(face == 5) {
            return new int[]{4,5,6,7};
        } else {
            return new int[]{0,0,0,0};
        }
    }
    
    public double[][] getNearestFacePoints(double[][] projectedPoints) {
        double[][] retPoints = new double[12][2];
        double f1 = Math.sqrt(Math.pow(faces.get(0).getX(),2) + Math.pow(faces.get(0).getY(),2) + Math.pow(faces.get(0).getZ(),2));
        double f2 = Math.sqrt(Math.pow(faces.get(1).getX(),2) + Math.pow(faces.get(1).getY(),2) + Math.pow(faces.get(1).getZ(),2));
        double f3 = Math.sqrt(Math.pow(faces.get(2).getX(),2) + Math.pow(faces.get(2).getY(),2) + Math.pow(faces.get(2).getZ(),2));
        double f4 = Math.sqrt(Math.pow(faces.get(3).getX(),2) + Math.pow(faces.get(3).getY(),2) + Math.pow(faces.get(3).getZ(),2));
        double f5 = Math.sqrt(Math.pow(faces.get(4).getX(),2) + Math.pow(faces.get(4).getY(),2) + Math.pow(faces.get(4).getZ(),2));
        double f6 = Math.sqrt(Math.pow(faces.get(5).getX(),2) + Math.pow(faces.get(5).getY(),2) + Math.pow(faces.get(5).getZ(),2));
        double[] nums = new double[]{f1,f2,f3,f4,f5,f6};
        double[] sorted = sortArray(nums);
        int in1 = findIndex(nums,sorted[0]);
        int in2 = findIndex(nums,sorted[1]);
        int in3 = findIndex(nums,sorted[2]);
        for(int i = 0; i < 6; i ++) {
            if(in3 == i) {
                int[] cornerPoints = getFacePointsAssociated(i);
                for(int h = 0; h < 4; h ++) {
                    retPoints[h][0] = projectedPoints[cornerPoints[h]][0];
                    retPoints[h][1] = projectedPoints[cornerPoints[h]][1];
                }
            }
            if(in2 == i) {
                int[] cornerPoints = getFacePointsAssociated(i);
                for(int h = 0; h < 4; h ++) {
                    retPoints[h + 4][0] = projectedPoints[cornerPoints[h]][0];
                    retPoints[h + 4][1] = projectedPoints[cornerPoints[h]][1];
                }
            }
            if(in1 == i) {
                int[] cornerPoints = getFacePointsAssociated(i);
                for(int h = 0; h < 4; h ++) {
                    retPoints[h + 8][0] = projectedPoints[cornerPoints[h]][0];
                    retPoints[h + 8][1] = projectedPoints[cornerPoints[h]][1];
                }
            }
        }
        printMatrix(retPoints);
        return(retPoints);
    }

    public ArrayList<Vector3> getPointArray() {
        return(points);
    }

    public ArrayList<Vector3> getFaceArray() {
        return(faces);
    }

    public double[][] draw(Graphics g) {
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
        double[][] truePoints = new double[8][3];
        for(int i = 0; i < points.size(); i ++) {
            double[] rotated = multiplyMatrices(rotationY, points.get(i).toMatrix());
            rotated = multiplyMatrices(rotationX, rotated);
            rotated = multiplyMatrices(rotationZ, rotated);
            truePoints[i][0] = rotated[0];
            truePoints[i][1] = rotated[1];
            truePoints[i][2] = rotated[2];
            double z = 1 / rotated[2];

            double[][] projection = new double[][]{
                    {z, 0, 0},
                    {0, z, 0},
                };

            double[] projected2d = multiplyMatrices(projection, rotated);

            projected2d = scaleMatrix(projected2d, 300);

            projected[i] = projected2d;
        }
        printMatrix(projected);
        
        refreshFaceArray();
        double[][] pointsToDraw = getNearestFacePoints(projected);
        for(int i = 0; i < 3; i ++) {
            BufferedImage image = Pseudo3D.computeImage(inputImage, new Point2D.Double(pointsToDraw[(4 * i)][0],pointsToDraw[(4 * i)][1]), new Point2D.Double(pointsToDraw[(4 * i) + 1][0],pointsToDraw[(4 * i) + 1][1]),
            new Point2D.Double(pointsToDraw[(4 * i) + 2][0],pointsToDraw[(4 * i) + 2][1]), new Point2D.Double(pointsToDraw[(4 * i) + 3][0],pointsToDraw[(4 * i) + 3][1]));
            g.drawImage(image, 0, 0, null);
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

    public double getX() {return(x);}

    public double getY() {return(y);}

    public double getZ() {return(z);}

    public double[] toMatrix() {
        double [] vectorMatrix = new double[]{x,y,z};
        return(vectorMatrix);
    }
}

class Pseudo3D
{
    static BufferedImage computeImage(
        BufferedImage image,
        Point2D p0, Point2D p1, Point2D p2, Point2D p3)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        BufferedImage result =
            new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Point2D ip0 = new Point2D.Double(0,0);
        Point2D ip1 = new Point2D.Double(0,h);
        Point2D ip2 = new Point2D.Double(w,h);
        Point2D ip3 = new Point2D.Double(w,0);

        Matrix3D m = computeProjectionMatrix(
            new Point2D[] {  p0,  p1,  p2,  p3 },
            new Point2D[] { ip0, ip1, ip2, ip3 });
        Matrix3D mInv = new Matrix3D(m);
        mInv.invert();

        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                Point2D p = new Point2D.Double(x,y);
                mInv.transform(p);
                int ix = (int)p.getX();
                int iy = (int)p.getY();
                if (ix >= 0 && ix < w && iy >= 0 && iy < h)
                {
                    int rgb = image.getRGB(ix, iy);
                    result.setRGB(x, y, rgb);
                }
            }
        }
        return result;
    }

    // From https://math.stackexchange.com/questions/296794
    private static Matrix3D computeProjectionMatrix(Point2D p0[], Point2D p1[])
    {
        Matrix3D m0 = computeProjectionMatrix(p0);
        Matrix3D m1 = computeProjectionMatrix(p1);
        m1.invert();
        m0.mul(m1);
        return m0;
    }

    // From https://math.stackexchange.com/questions/296794
    private static Matrix3D computeProjectionMatrix(Point2D p[])
    {
        Matrix3D m = new Matrix3D(
            p[0].getX(), p[1].getX(), p[2].getX(),
            p[0].getY(), p[1].getY(), p[2].getY(),
            1, 1, 1);
        Point3D p3 = new Point3D(p[3].getX(), p[3].getY(), 1);
        Matrix3D mInv = new Matrix3D(m);
        mInv.invert();
        mInv.transform(p3);
        m.m00 *= p3.x;
        m.m01 *= p3.y;
        m.m02 *= p3.z;
        m.m10 *= p3.x;
        m.m11 *= p3.y;
        m.m12 *= p3.z;
        m.m20 *= p3.x;
        m.m21 *= p3.y;
        m.m22 *= p3.z;
        return m;
    }

    private static class Point3D
    {
        double x;
        double y;
        double z;

        Point3D(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static class Matrix3D
    {
        double m00;
        double m01;
        double m02;
        double m10;
        double m11;
        double m12;
        double m20;
        double m21;
        double m22;

        Matrix3D(
            double m00, double m01, double m02,
            double m10, double m11, double m12,
            double m20, double m21, double m22)
        {
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }

        Matrix3D(Matrix3D m)
        {
            this.m00 = m.m00;
            this.m01 = m.m01;
            this.m02 = m.m02;
            this.m10 = m.m10;
            this.m11 = m.m11;
            this.m12 = m.m12;
            this.m20 = m.m20;
            this.m21 = m.m21;
            this.m22 = m.m22;
        }

        // From http://www.dr-lex.be/random/matrix_inv.html
        void invert()
        {
            double invDet = 1.0 / determinant();
            double nm00 = m22 * m11 - m21 * m12;
            double nm01 = -(m22 * m01 - m21 * m02);
            double nm02 = m12 * m01 - m11 * m02;
            double nm10 = -(m22 * m10 - m20 * m12);
            double nm11 = m22 * m00 - m20 * m02;
            double nm12 = -(m12 * m00 - m10 * m02);
            double nm20 = m21 * m10 - m20 * m11;
            double nm21 = -(m21 * m00 - m20 * m01);
            double nm22 = m11 * m00 - m10 * m01;
            m00 = nm00 * invDet;
            m01 = nm01 * invDet;
            m02 = nm02 * invDet;
            m10 = nm10 * invDet;
            m11 = nm11 * invDet;
            m12 = nm12 * invDet;
            m20 = nm20 * invDet;
            m21 = nm21 * invDet;
            m22 = nm22 * invDet;
        }

        // From http://www.dr-lex.be/random/matrix_inv.html
        double determinant()
        {
            return
                m00 * (m11 * m22 - m12 * m21) +
                m01 * (m12 * m20 - m10 * m22) +
                m02 * (m10 * m21 - m11 * m20);
        }

        final void mul(double factor)
        {
            m00 *= factor;
            m01 *= factor;
            m02 *= factor;

            m10 *= factor;
            m11 *= factor;
            m12 *= factor;

            m20 *= factor;
            m21 *= factor;
            m22 *= factor;
        }

        void transform(Point3D p)
        {
            double x = m00 * p.x + m01 * p.y + m02 * p.z;
            double y = m10 * p.x + m11 * p.y + m12 * p.z;
            double z = m20 * p.x + m21 * p.y + m22 * p.z;
            p.x = x;
            p.y = y;
            p.z = z;
        }

        void transform(Point2D pp)
        {
            Point3D p = new Point3D(pp.getX(), pp.getY(), 1.0);
            transform(p);
            pp.setLocation(p.x / p.z, p.y / p.z);
        }

        void mul(Matrix3D m)
        {
            double nm00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20;
            double nm01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21;
            double nm02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22;

            double nm10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20;
            double nm11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21;
            double nm12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22;

            double nm20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20;
            double nm21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21;
            double nm22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22;

            m00 = nm00;
            m01 = nm01;
            m02 = nm02;
            m10 = nm10;
            m11 = nm11;
            m12 = nm12;
            m20 = nm20;
            m21 = nm21;
            m22 = nm22;
        }
    }

}
