/*
 * The MIT License
 *
 * Copyright 2019 Elias Eskelinen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package codenameprojection;

import JFUtils.Input;
import JFUtils.InputActivated;
import JFUtils.Range;
import JFUtils.point.Point2D;
import JFUtils.point.Point3D;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import JFUtils.vector.dVector3;
import JFUtils.point.Point3F;
import PBEngine.Supervisor;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author Elias Eskelinen
 */
public class CodeNameProjection {
    public static double minUtilsVer = 2.57;

    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) {
        if(JFUtils.versionCheck.version != minUtilsVer){
            throw new UnsupportedClassVersionError("cnprojection needs jfutils " + minUtilsVer + ", current version is " + JFUtils.versionCheck.version);
        }

    
        new driver();
        HashMap<String, String> param = new HashMap<>();
        param.put("nowindows", "");
        Supervisor supervisor = new PBEngine.Supervisor(0, true, new Point2D(0, 0), param);
        supervisor.run();
    }
    
}
class driver{
    Duration deltaTime = Duration.ZERO;
    Instant beginTime = Instant.now();
    
    boolean rotation = true;
    
    private int xScreenCenter = 320/2;
    private int yScreenCenter = 240/2;
    private Point3D screenPosition = new dVector3( 0, 0, 7 );
    private Point3D screenPosition_org = screenPosition.clone();
    private dVector3 viewAngle = new dVector3( 0, 90, 90 );
    private Point3D viewAngle_org = viewAngle.clone();
    
    private static final double DEG_TO_RAD = 0.017453292;
    private double modelScale = 10;
    
    double CT = Math.cos( DEG_TO_RAD * viewAngle.x );
    double ST = Math.sin( DEG_TO_RAD * viewAngle.x );
    double CP = Math.cos( DEG_TO_RAD * viewAngle.y );
    double SP = Math.sin( DEG_TO_RAD * viewAngle.y );
    
    //False: "gloabal"
    //True: "local"
    public boolean rotation_mode = true;
    
    
    
    public void addCube(dVector3 center, double size, boolean Addlines, boolean addFaces){
        double s = size;
        dVector3 dlu = new dVector3(center.x +s, center.y +s, center.z -s);
        dVector3 dld = new dVector3(center.x +s, center.y -s, center.z -s);
        dVector3 dru = new dVector3(center.x -s, center.y +s, center.z -s);
        dVector3 drd = new dVector3(center.x -s, center.y -s, center.z -s);
        dVector3 ulu = new dVector3(center.x +s, center.y +s, center.z +s);
        dVector3 uld = new dVector3(center.x +s, center.y -s, center.z +s);
        dVector3 uru = new dVector3(center.x -s, center.y +s, center.z +s);
        dVector3 urd = new dVector3(center.x -s, center.y -s, center.z +s);
        points.add(dlu);
        points.add(dld);
        points.add(dru);
        points.add(drd);
        points.add(ulu);
        points.add(uld);
        points.add(uru);
        points.add(urd);
        if (Addlines) {
            lines.add(new Integer[]{dlu.identifier, dld.identifier});
            lines.add(new Integer[]{dlu.identifier, dru.identifier});
            lines.add(new Integer[]{dld.identifier, drd.identifier});
            lines.add(new Integer[]{dru.identifier, drd.identifier});
            
            lines.add(new Integer[]{ulu.identifier, uld.identifier});
            lines.add(new Integer[]{ulu.identifier, uru.identifier});
            lines.add(new Integer[]{uld.identifier, urd.identifier});
            lines.add(new Integer[]{uru.identifier, urd.identifier});
            
            lines.add(new Integer[]{dlu.identifier, ulu.identifier});
            lines.add(new Integer[]{dld.identifier, uld.identifier});
            lines.add(new Integer[]{dru.identifier, uru.identifier});
            lines.add(new Integer[]{drd.identifier, urd.identifier});
        }
        if(addFaces){
            faces.add(new Integer[]{dlu.identifier, dld.identifier, dru.identifier});
        }
    }
    public void addCube(dVector3 center, double size){
        addCube(center, size, true, true);
    }
    LinkedList<dVector3> points;
    LinkedList<Integer[]> lines;
    LinkedList<Integer[]> faces = new LinkedList<>();
    public driver(){
        //dVector3 point = new dVector3(0, 0, 0);
        InputActivated refI = new InputActivated();
        Screen s = new Screen();
        Input inp = new Input(refI);
        inp.verbodose = false;
        s.addKeyListener(inp);
        s.addMouseListener(inp);
        points = new LinkedList<>();
        lines = new LinkedList<>();
        try {
            //addCube(new dVector3(0, 0, 0), 0.5);
            //File err = new File("err.txt");
            points = new modelParser().parse();
            lines = new modelParser().parseLines(points);
            faces = new modelParser().parseFaces(points);
        } catch (Exception ex) {
            int r = 3;
            int r2 = 3;
            int r3 = 3;
            for (int i : new Range(r)) {
                for (int j : new Range(r2)) {
                    for (int z : new Range(r3)) {
                        addCube(new dVector3(i, j, z), 0.5, true, true);
                    }
                }
            }
        }
        
        
        
        double angleY = 0;
        double angleYM = 0;
        double angleX = 0;
        double angleXM = 0;
        int sleep = 0;
        boolean running = true;
        
        //Graph grapher = new Graph();
        int tickC = 0;
        while(running){
            beginTime = Instant.now();
            //Init
            LinkedList<Point2D> set = new LinkedList<>();
            LinkedList<Point2D> sizes = new LinkedList<>();
            LinkedList<Point2D[]> lines_set = new LinkedList<>();
            LinkedList<Point2D[]> lines_sizes = new LinkedList<>();
            LinkedList<Color> lines_color = new LinkedList<>();
            LinkedList<Color> faces_color = new LinkedList<>();
            HashMap<Integer, Float> dist = new HashMap<>();
            lines_color = new LinkedList<>();
            faces_color = new LinkedList<>();
            
            HashMap<Integer, Point2D> idVSserial = s.r.getIDMap();
            
            
            CT = Math.cos( DEG_TO_RAD * viewAngle.x );//CT=0;
            ST = Math.sin( DEG_TO_RAD * viewAngle.x );//ST=0;
            CP = Math.cos( DEG_TO_RAD * viewAngle.y );//CP=0;
            SP = Math.sin( DEG_TO_RAD * viewAngle.y );//SP=0;
            
            xScreenCenter = s.r.w / 2;
            yScreenCenter = s.r.h / 2;
            
            screenPosition = screenPosition_org.clone();
            if(!rotation_mode){
                screenPosition = matmul(RX((float) angleY), screenPosition.toFVector3()).toDVector3();
                screenPosition = matmul(RY((float) -angleX), screenPosition.toFVector3()).toDVector3();
            }
            
            //Check input   -0.025D*0.05
            double factor_rotation = -0.025D*0.05;
            double factor = -0.025D*0.05*4;
            double boost = 1;
            //space
            if(inp.keys[32] == true){
                //viewAngle.y += factor*15;
                factor = factor * 7;
                factor_rotation = factor_rotation * 15;
                boost = 5;
                
            }
            if(inp.keys[68] == true){
                screenPosition_org.x = screenPosition_org.x + factor;
            }
            if(inp.keys[65] == true){
                screenPosition_org.x = screenPosition_org.x - factor;
            }
            if(inp.keys[87] == true){
                screenPosition_org.y = screenPosition_org.y + factor;
            }
            if(inp.keys[83] == true){
                screenPosition_org.y = screenPosition_org.y - factor;
            }
            if(inp.keys[81] == true){
                screenPosition_org.z = screenPosition_org.z - factor*5;
            }
            if(inp.keys[69] == true){
                screenPosition_org.z = screenPosition_org.z + factor*5;
            }
            //c
            if(inp.keys[67] == true){
                viewAngle.z += factor_rotation*15;
            }
            //z
            if(inp.keys[90] == true){
                viewAngle.z -= factor_rotation*15;
            }
            
            //z
            if(inp.keys[88] == true){
                //viewAngle.y -= factor*15;
            }
            //j
            if(inp.keys[74] == true){
                angleXM = angleXM - 0.0004D * 0.3 * boost;
            }
            //l
            if(inp.keys[76] == true){
                angleXM = angleXM + 0.0004D * 0.3 * boost;
            }
            //i
            if(inp.keys[73] == true){
                angleYM = angleYM + 0.0004D * 0.3 * boost;
            }
            //k
            if(inp.keys[75] == true){
                angleYM = angleYM - 0.0004D * 0.3 * boost;
            }
            if(inp.keys[86] == true){
                inp.verbodose = !inp.verbodose;
            }
            //R
            if(inp.keys[82] == true){
                rotation = !rotation;
            }
            //1
            if(inp.keys[49] == true){
                rotation_mode = false;
            }
            //2
            if(inp.keys[50] == true){
                rotation_mode = true;
            }
            else{
            }
            s.r.speed = (float) factor;
            
            //screenPosition.z = screenPosition_org.z;
            //Calc
            for(dVector3 i : points){
                if(i.z > screenPosition.z){
                    //continue;
                }
                //System.out.println("Original[" +i.hashCode() + "] :" + i);
                Point3F rotated = i.toFVector3();
                
                if(rotation){
                    
                }
                
                if(rotation){
                    rotated = matmul(RX((float) -angleY ), rotated);
                    //rotated.z = rotated.z - rotated.x;
                    rotated = matmul(RY((float) angleX ), rotated);
                }
                /*rotated = matmul(RX((float) 0 ), rotated);
                rotated = matmul(RX((float) 0 ), rotated);
                rotated = matmul(RX((float) 0 ), rotated);*/
                //rotated.z = (float) i.z;
                //fVector3 rotated = i.toFVector3();
                //rotated = matmul(RZ(0), rotated);
                //rotated = matmul(RY(-0.06F), rotated);
                
                /*float dist = 0.005F;
                float z = 1;
                if(dist - rotated.z != 0){
                z = 1 / (dist - rotated.z);
                }
                float[][] projection = {
                {z, 0, 0},
                {0, z, 0}
                };
                
                fVector3 projected = null;
                try {
                projected = matmul(projection, rotated);
                } catch (Exception e) {
                }*/
                //projected = fVector3.multiply(projected, new fVector3(20, 20, 20));
                //projected = fVector3.add(projected, new fVector3(s.r.w/2, s.r.h/2, 0));
                Point3F projected = new Point3F(0, 0, 0);
                /*float[][] projection = {
                {z, 0, 0},
                {0, z, 0}
                };
                dVector3 projected = new dVector3(0, 0, 0);*/
                projectPoint(rotated, projected);
                /*projected = matmul(projection, rotated).toDVector3();
                projected = dVector3.multiply(projected, new dVector3(200, 200, 200));
                projected = dVector3.add(projected, new dVector3(200, 200, 20));*/
                Point2D point2D = new Point2D(projected.x, projected.y);
                point2D.identifier = i.identifier;
                int size = (int) (25 - (screenPosition.z - rotated.z) * 2);
                float distP = (float) (screenPosition.z - rotated.z);
                dist.put(i.identifier, distP);
                if(size < 0){
                    size = 0;
                }
                //System.out.println("Projected: " + point2);
                if(rotated.z < screenPosition.z){
                    sizes.add(new Point2D(size, size));
                    set.add(point2D);
                }
            }
            /*for(Integer[] l : lines){
            //Every point must exist in both lists
            try {
            dVector new_start = set.get(points.indexOf(l[0]));
            dVector new_end = set.get(points.indexOf(l[1]));
            lines_set.add( new dVector[]{new_start, new_end} );
            lines_sizes.add(new dVector[]{ sizes.get(points.indexOf(l[0])) , sizes.get(points.indexOf(l[1]))} );
            } catch (Exception e) {
            e.printStackTrace();
            }
            }*/
            
            for(Integer[] line : lines){
                Point2D point = null;
                try {
                    point = idVSserial.get(line[0]);
                } catch (Exception e) {
                    try {
                        point = idVSserial.get(line[1]);
                    } catch (Exception ez) {
                        //throw ez;
                        lines_color.add(Color.pink);
                    }
                }
                
                if(!Objects.isNull(point)){
                    int distP = (int) (float) (255 - dist.get(point.identifier) * 25.5*0.5);
                    //System.out.println(distP);
                    if(distP > 255){
                        distP = 255;
                    }
                    if(distP < 0){
                        distP = 0;
                    }
                    lines_color.add(new Color(distP, distP, distP));
                }
            }
            
            LinkedList<Float> face_dists = new LinkedList<>();
            for(Integer[] face : faces){
                Point2D point = null;
                try {
                    point = idVSserial.get(face[0]);
                } catch (Exception e) {
                    try {
                        point = idVSserial.get(face[1]);
                    } catch (Exception ez) {
                        //throw ez;
                        try {
                            point = idVSserial.get(face[2]);
                        } catch (Exception ezz) {
                            //throw ez;
                            faces_color.add(Color.pink);
                        }
                    }
                }
                
                if(!Objects.isNull(point)){
                    int distP = (int) (float) (255 - dist.get(point.identifier) * 25);
                    //System.out.println(distP);
                    if(distP > 255){
                        distP = 255;
                    }
                    if(distP < 0){
                        distP = 0;
                    }
                    faces_color.add(new Color(distP, distP, distP));
                    face_dists.add((float)distP);
                    //faces_color.add(Color.pink);
                }
            }
            
            //Rendering
            s.r.updatePoints(set, sizes);
            s.r.updateLines(lines, lines_color);
            s.r.updateFaces(faces, faces_color, face_dists);
            //System.out.println("orighinal: ");
            //System.out.println("projected: " + point2);
            
            angleYM = angleYM * 0.95D;
            angleY = (float) (angleY + angleYM);
            angleXM = angleXM * 0.95D;
            angleX = (float) (angleX + angleXM);
            deltaTime = Duration.between(beginTime, Instant.now());
            s.r.nano = JFUtils.math.Conversions.toCPNS(deltaTime.getNano());
            int value = (int) (JFUtils.math.Conversions.toFPS(deltaTime.getNano()));
            if(value < 0) {
                value = 0;
            }
            //System.out.println(value);
            try {
                //grapher.update(value, tickC);
            } catch (Exception e) {
                System.out.println(e);
            }
            tickC++;
            //System.out.println(deltaTime.getNano() + " nano passed");
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException ex) {
                Logger.getLogger(CodeNameProjection.class.getName()).log(Level.SEVERE, null, ex);
            }
            //point.x = point.x + 0.1;
            //point.z--;
            //points.get(0).x = points.get(0).x + 0.1;
            //points.get(0).z--;
        }
    }
    
    public void projectPoint( Point3F input, Point3F output )
    {
        float sx = (float) screenPosition.x;
        float sy = (float) screenPosition.y;
     float x = (float) (sx + input.x * CT - input.y * ST);
     float y = (float) (sy + input.x * ST * SP + input.y * CT * SP
             + input.z * CP);
     float temp = (float) (viewAngle.z / (screenPosition.z + input.x * ST * CP
             + input.y * CT * CP - input.z * SP ));
    
     //x= ((input.x - sx) * (100/input.z)) + sx;
     //y = ((input.y - sy) * (100/input.z)) + sy;
     //temp = 10;
     
     float F = (float) (input.z-screenPosition.z * 0.2);
     
     //x = (input.x - sx) * (F/input.z) + sx;
     //y = (input.y - sy) * (F/input.z) + sy;
     
     output.x = (float) (xScreenCenter + modelScale * temp * x);
     output.y = (float) (yScreenCenter - modelScale * temp * y);
     
     //output.x = ((input.x - sx) * (100/input.z)) + sx;
     //output.y = ((input.y - sy) * (100/input.z)) + sy;
     output.z = 0;
    }
    public static float[][] RX (float o){
        float d = (float) o;
        float c = (float) Math.cos(d);
        float s = (float) Math.sin(d);
        float ns = (float) -Math.sin(d);
        return new float[][]{
            new float[]{1, 0, 0},
            new float[]{0, c, ns},
            new float[]{0, s, c}
        };
    }
    public static float[][] RY (float o){
        float d = (float) o;
        float c = (float) Math.cos(d);
        float s = (float) Math.sin(d);
        float ns = (float) -Math.sin(d);
        return new float[][]{
            new float[]{c, 0, s},
            new float[]{0, 1, 0},
            new float[]{ns, 0, c}
        };
    }
    public static float[][] RZ (float o){
        float d = (float) o;
        float c = (float) Math.cos(d);
        float s = (float) Math.sin(d);
        float ns = (float) -Math.sin(d);
        return new float[][]{
            new float[]{c, ns, 0},
            new float[]{s, c, 0},
            new float[]{0, 0, 1}
        };
    }
    
    //The following is copied (edited to suit JFTools) from Daniel Shiffmans code, at: https://github.com/CodingTrain/website/blob/master/CodingChallenges/CC_112_3D_Rendering/Processing/CC_112_3D_Rendering/matrix.pde#L50
    //Why? becouse i do not know how multiplication matricies work! :P
    float[][] vecToMatrix(Point3F v) {
        float[][] m = new float[3][1];
        m[0][0] = (float) v.x;
        m[1][0] = (float) v.y;
        m[2][0] = (float) v.z;
        return m;
      }

    Point3F matrixToVec(float[][] m) {
        Point3F v = new Point3F(0,0,0);
        v.x = m[0][0];
        v.y = m[1][0];
        if (m.length > 2) {
          v.z = m[2][0];
        }
        return v;
      }
    Point3F matmul(float[][] a, Point3F b) {
        float[][] m = vecToMatrix(b);
        return matrixToVec(matmul(a,m));
    }
    float[][] matmul(float[][] a, float[][] b) {
        int colsA = a[0].length;
        int rowsA = a.length;
        int colsB = b[0].length;
        int rowsB = b.length;

        if (colsA != rowsB) {
          throw new Error("Columns of A must match rows of B");
        }

        float result[][] = new float[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
          for (int j = 0; j < colsB; j++) {
            float sum = 0;
            for (int k = 0; k < colsA; k++) {
              sum += a[i][k] * b[k][j];
            }
            result[i][j] = sum;
          }
        }
        return result;
    }
}
/*class driver{
public double x = 20;
public double y = 20;
public double z = 0;
public driver(){
// TODO code application logic here
// convert to screen space
Screen screen = new Screen();
dVector P_screen = new dVector(0, 0);
dVector3 P_camera = new dVector3(0, 0, 20);
double near = -2;
double r = 100; //Right side of the screen
double l = 0; //Left side of the screen
double t = 0; //Top of the screen
double b = 100; //Bottom of the screen







//Lets do this!!!

int sleep = 200;

while(true){
//Init
LinkedList<dVector> set = new LinkedList<>();
dVector3 newVertex = new dVector3(x, y, z);
System.out.println("Original: "+newVertex);

//Translate
newVertex.x /= (newVertex.z + 100D) * 0.01D;
newVertex.y /= (newVertex.z + 100D) * 0.01D;
System.out.println("Translated: "+newVertex);
//Convert to pixels
newVertex.x *= (screen.r.w/80D);
newVertex.y *= (screen.r.h/80D);
System.out.println("Pixelated: "+newVertex);
//Zero-ify
newVertex.x += screen.r.w/2D;
newVertex.y += screen.r.h/2D;
System.out.println("Zero'd: "+newVertex);
//test?
System.out.println("Final: "+newVertex);

//Render
set.add(new dVector(x, y));
screen.r.updatePoints(set);
try {
Thread.sleep(sleep);
} catch (InterruptedException ex) {
Logger.getLogger(CodeNameProjection.class.getName()).log(Level.SEVERE, null, ex);
}
x = x + 1;
}


}
}*/