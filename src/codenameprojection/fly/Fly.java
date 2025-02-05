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

package codenameprojection.fly;

import JFUtils.Range;
import JFUtils.point.Point3D;
import JFUtils.point.Point3F;
import codenameprojection.Utils;
import codenameprojection.drawables.vertexGroup;
import codenameprojection.driver;
import codenameprojection.models.Model;
import codenameprojection.modelParser;
import codenameprojection.models.ModelFrame;
import java.awt.FlowLayout;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JSlider;

/**
 *
 * @author Jonnelafin
 */
public class Fly {
    public static void main(String[] args) {
        new Fly();
    }

    codenameprojection.driver Driver;
    JFrame frame;
    private Instant beginTime;
    private Duration deltaTime;
    public Fly() {
        Driver = new driver();
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run(); //To change body of generated methods, choose Tools | Templates.
                Driver.run();
                //Driver = new driver(null);
            }
            
        };
        modelParser.filename = "assets/models/Viper8";
        t.start();
        while(!Driver.running){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Fly.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        map Map = new map();
        Driver.s.r.extraDrawables.add(Map);
        Driver.s.r.debug = false;
        
        Driver.an_pause = false;
        //Driver.zero();
        
        Driver.s.r.usePixelRendering = false;
        Driver.s.r.drawFaces = true;
        Driver.s.r.drawPoints = true;
        Driver.s.r.drawLines = true;
        Driver.s.r.shading = false;
        Driver.rotation_mode = false;
        System.out.println("Init complete!");
        
        frame = new JFrame("\"PB3D\" Fly simulator :)");
        
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        
        JSlider step = new JSlider(0, 100, 0);
        
        frame.add(step);
        //frame.setVisible(true);
        
        float p = -1;
        
        int a = 2;
        Random r = new Random();
        float x = 0;
        float y = 0;
        int l_f = 0;
        Driver.inp.verbodose = true;
        Integer[] defaultKeys = new Integer[]{
            //74, 
            //76, 
            //73, 
            //75,
            //87,
            //a
            //65,
            //83,
            //d
            //68,
        };
        for(int i : new Range(1000)){
            Driver.ingoredInputs.add(i);
        }
        for(int i : defaultKeys){
            Driver.ingoredInputs.removeAll(Arrays.asList(defaultKeys));
        }
        //Driver.models.clear();
        int ship = (int) Driver.models.keySet().toArray()[0];
        Model shipM = Driver.models.get(ship);
        //shipM.frames.getFirst().lines = new LinkedList<>();
        //shipM.frames.getFirst().faces = new LinkedList<>();
        
        shipM.hideLines = true;
        shipM.hideFaces = true;
        shipM.hidePoints = false;
        
        LinkedList<Model> points = constructCloud(); // //new LinkedList<>();
        LinkedList<Integer> handles = new LinkedList<>();
        for(Model m : points){
            Integer handle = m.hashCode();
            Driver.models.put(handle, m);
            handles.add(handle);
        }
        LinkedList<Model> grid = constructGrid();
        LinkedList<Integer> gridHandles = new LinkedList<>();
        for(Model m : grid){
            Integer handle = m.hashCode();
            Driver.models.put(handle, m);
            gridHandles.add(handle);
        }
        Driver.shadingMultiplier = 0.5F;
        LinkedList<Integer> boltHandles = new LinkedList<>();
        float speed = 0.4F;
//        Driver.screenPosition_org = new Point3D(0, 0, 0);
//        Point3D last_sp = Driver.screenPosition_org.clone();
        int boltCooldown = 0;
        float side = 1.3F;
        float up = -2F;
        float front = -8;
        float front2 = 0;
        float shipRotY = 0;
        float shipRotZ = 0;
        Point3D c1 = new Point3D(side, up, front);
        Point3D c2 = new Point3D(side, up, front2);
        Point3D c3 = new Point3D(-side, up, front);
        Point3D c4 = new Point3D(-side, up, front2);
        int c1i = c1.identifier + 0;
        int c2i = c2.identifier + 0;
        int c3i = c3.identifier + 0;
        int c4i = c4.identifier + 0;
        
        shipM = Driver.models.get(ship);
        shipM.frames.get(0).points.add(c1);
        shipM.frames.get(0).points.add(c2);
        shipM.frames.get(0).points.add(c3);
        shipM.frames.get(0).points.add(c4);
        
        System.out.println(shipM.frames.get(0).points.indexOf(c1));
        
        float thrust = 0F;
        float v = 0F;
        Point3D vel = new Point3D(0, 0, 0);
        //float shipRotX = 0;
        
        
        
        boolean pause = false;
        while (true) {
            beginTime = Instant.now();
            
            shipM = Driver.models.get(ship);
            if(shipM.frames.get(0).points.indexOf(c1) == -1){
                shipM.frames.get(0).points.add(c1);
            }
            if(shipM.frames.get(0).points.indexOf(c2) == -1){
                shipM.frames.get(0).points.add(c2);
            }
            if(shipM.frames.get(0).points.indexOf(c3) == -1){
                shipM.frames.get(0).points.add(c3);
            }
            if(shipM.frames.get(0).points.indexOf(c4) == -1){
                shipM.frames.get(0).points.add(c4);
            }
            c1i = shipM.frames.get(0).points.indexOf(c1);
            c2i = shipM.frames.get(0).points.indexOf(c2);
            c3i = shipM.frames.get(0).points.indexOf(c3);
            c4i = shipM.frames.get(0).points.indexOf(c4);
            //shipM = Driver.models.get(ship);
            Driver.angleYM = Driver.angleYM + Driver.inp.cX * 0.0002;
            Driver.inp.cX = (int) (Driver.inp.cX * 0.5);
            Driver.angleXM = Driver.angleXM + Driver.inp.cY * 0.0002;
            Driver.inp.cY = (int) (Driver.inp.cY * 0.5);
            
            Model shipModel = Driver.models.get(ship);
            Point3D shipCenter = Utils.average(shipModel.getFrame(0).points);
            //System.out.println(Driver.inp.mouseX());
            LinkedList<Integer> torem = new LinkedList<>();
            if (!pause) {
                for (Integer bolt : boltHandles) {
                    try {
                        Model cursor = Driver.models.get(bolt);
                        //Point3D.multiply(Driver.viewAngle, new Point3D(1, 1, 1)
                        double speeds = 0.5;
                        Point3D one = cursor.frames.get(0).points.get(0);
                        Point3D two = cursor.frames.get(0).points.get(1);
                        //cursor.getFrame(0).points.get(0).z = cursor.getFrame(0).points.get(0).z - 0.5;
                        //cursor.getFrame(0).points.get(1).z = cursor.getFrame(0).points.get(1).z - 0.5;
                        Point3D newPo = Point3D.add(one, Point3D.subtract(one, two));
                        Point3D newPt = Point3D.add(two, Point3D.subtract(one, two));
                        Point3D velBolt = Point3D.subtract(one, two);
                        cursor.setX(cursor.getX() + velBolt.x);
                        cursor.setY(cursor.getY() + velBolt.y);
                        cursor.setZ(cursor.getZ() + velBolt.z);
                        if (Math.abs(Utils.getDistance(new Point3D(cursor.getX(), cursor.getY(), cursor.getZ()), new Point3D(0, 0, 0))) > 2000) {
                            //Driver.models.remove(bolt);
                            torem.add(bolt);
                        } else {
                            //System.out.println(Math.abs(Utils.getDistance(new Point3D(cursor.x, cursor.y, cursor.z), new Point3D(0, 0, 0))));
                        }
                        //cursor.getFrame(0).points.get(0).x = newPo.x;
                        //cursor.getFrame(0).points.get(0).y = newPo.y;
                        //cursor.getFrame(0).points.get(0).z = newPo.z;

                        //cursor.getFrame(0).points.get(1).x = newPt.x;
                        //cursor.getFrame(0).points.get(1).y = newPt.y;
                        // cursor.getFrame(0).points.get(1).z = newPt.z;
                    } catch (Exception e) {
                    }
                }
            }
            for(Integer bolt : torem){
                Driver.models.remove(bolt);
            }
            //space
            if(Driver.inp.keys[32] && boltCooldown < 1){
                
                LinkedList<ModelFrame> frames2 = new LinkedList<>();
                LinkedList<Point3D> points2 = new LinkedList<>();
                LinkedList<Integer[]> lines2 = new LinkedList<>();
                LinkedList<Point3D[]> faces2 = new LinkedList<>();
                LinkedList<vertexGroup> color2 = new LinkedList<>();
                Point3D c5 = shipM.getFrame(0).points.get(c1i).clone();
                Point3D c6 = shipM.getFrame(0).points.get(c2i).clone();
                if(side > 0){
                    c5 = shipM.getFrame(0).points.get(c3i).clone();
                    c6 = shipM.getFrame(0).points.get(c4i).clone();
                }
                c5.identifier = c5.hashCode();
                c6.identifier = c6.hashCode();
                points2.add(c5);
                points2.add(c6);
                lines2.add(new Integer[]{c5.identifier, c6.identifier});
                frames2.add(new ModelFrame(points2 , lines2, faces2, color2));
                Model cursor = new Model(frames2, true);
                int cursorHandle = cursor.hashCode();
                Driver.models.put(cursorHandle, cursor);
                boltHandles.add(cursorHandle);
                boltCooldown = boltCooldown + 6;
                side = side * -1;
            }
            else if(boltCooldown > -5){
                boltCooldown--;
            }
            Point3D rot = Driver.matmul(Driver.RY((float)shipRotY), new Point3F(0, 0, -speed)).toDVector3();
            rot = Driver.matmul(Driver.RZ((float)shipRotZ), rot.toFVector3()).toDVector3();
          //Driver.screenPosition_org = Point3D.add(Driver.screenPosition_org, rot);
            //vel = rot;
            //w
            if(Driver.inp.keys[87]){
                thrust++;
                /*
                try {
                    modelParser.filename = "assets/models/old/Cube";
                    LinkedList<LinkedList<Point3D>> parse = new modelParser().parse();
                    LinkedList<model_frame> cf = new LinkedList<>();
                    cf.add(new ModelFrame(parse.get(0), new LinkedList<Integer[]>(), new LinkedList<Point2D[]>()));
                    cyclone c = new cyclone(cf, true);
                    Driver.models.put(c.hashCode(), c);
                } catch (IOException ex) {
                    Logger.getLogger(Fly.class.getName()).log(Level.SEVERE, null, ex);
                }
                */
                //JFUtils.quickTools.alert("Space!");
                //Driver.screenPosition_org = Point3D.add(Driver.screenPosition_org, Point3D.multiply(Driver.viewAngle, new Point3D(1, 1, 1)));
                
                //transform.rotation * DirectionVector * 10f;
                //Driver.screenPosition_org = new Point3D(0, 0, 0);
                //m.getFrame(0).points.getFirst().x = 10;
               // m.getFrame(0).points.getFirst().y = 10;
                //m.getFrame(0).points.getFirst().z = 10;
                //Driver.inp.keys[32] = false;
            }
            //Point3D rot2 = Driver.matmul(Driver.RY((float)shipRotY), new Point3F(0, 0, -speed)).toDVector3();
            //rot2 = Driver.matmul(Driver.RZ((float)shipRotZ), rot2.toFVector3()).toDVector3();
          //Driver.screenPosition_org = Point3D.add(Driver.screenPosition_org, rot);
            //vel = rot2;
            //s
            if(Driver.inp.keys[83]){
                vel = Point3D.multiply(vel, new Point3D(0.995, 0.995, 0.995));
            //    Point3D rot3 = Driver.matmul(Driver.RX((float)Driver.angleX), new Point3F(0, 0, speed)).toDVector3();
            //    rot2 = Driver.matmul(Driver.RY((float)Driver.angleY), rot.toFVector3()).toDVector3();
                //Driver.screenPosition_org = Point3D.add(Driver.screenPosition_org, rot);
            }
            //a
            /*if(Driver.inp.keys[65] && false){
                Point3D rot = Driver.matmul(Driver.RX((float)Driver.angleX), new Point3F(speed, 0, 0)).toDVector3();
                rot = Driver.matmul(Driver.RY((float)Driver.angleY), rot.toFVector3()).toDVector3();
                Driver.screenPosition_org = Point3D.add(Driver.screenPosition_org, rot);
            }*/
            if(Driver.inp.keys[65]){
                shipRotY = shipRotY + 0.0004F * 0.12F;
                
            }
            //d
            /*if(Driver.inp.keys[68] && false){
                Point3D rot = Driver.matmul(Driver.RX((float)Driver.angleX), new Point3F(-speed, 0, 0)).toDVector3();
                rot = Driver.matmul(Driver.RY((float)Driver.angleY), rot.toFVector3()).toDVector3();
                Driver.screenPosition_org = Point3D.add(Driver.screenPosition_org, rot);
            }*/
            if(Driver.inp.keys[68]){
                shipRotY = shipRotY - 0.0004F * 0.12F;
            }
            //q
            if(Driver.inp.keys[81]){
                shipRotZ = shipRotZ + 0.0004F;;
            }
            //e
            if(Driver.inp.keys[69]){
                shipRotZ = shipRotZ - 0.0004F;;
            }
            //b
            if(Driver.inp.keys[66]){
                Driver.s.r.debug = !Driver.s.r.debug;
                Driver.inp.keys[66] = false;
            }
            //p
            if(Driver.inp.keys[80]){
                pause = !pause;
                Driver.inp.keys[80] = false;
            }
            
            //System.out.println(shipM.rotation_X);
            shipM.rotation_X = shipRotZ * 20;
            //Driver.viewAngle.x = Driver.viewAngle.x + shipRotX;
            shipRotY = shipRotY * 0.992F;
            shipRotZ = shipRotZ * 0.992F;
            
            //Handle Thrust
            //AAA
            
            thrust = thrust * 0.01F;
            v = v + thrust;
            Point3D rotVec = Driver.matmul(Driver.RX((float)shipM.rotation_X), new Point3F(0, 0, -thrust)).toDVector3();
            rotVec = Driver.matmul(Driver.RY((float)shipM.rotation_Y), rotVec.toFVector3()).toDVector3();
            vel = Point3D.add(vel, rotVec);
            shipM.setX(shipM.getX() + vel.x);
            shipM.setY(shipM.getY() + vel.y);
            shipM.setZ(shipM.getZ() + vel.z);
            
            //Driver.screenPosition_org = shipCenter;
            //Driver.screenPosition_org = Utils.average(shipModel.getFrame(0).points);
            //Driver.screenPosition_org.x = shipModel.getFrame(0).points.get(0).clone().x;
            //Driver.screenPosition_org.y = shipModel.getFrame(0).points.get(0).clone().y;
            //Driver.screenPosition_org.z = shipModel.getFrame(0).points.get(0).clone().z;
            int camIndex = shipM.getByColor(0.0F, 1.0F, 0.0F).get(2);
            
            if(camIndex == -1){
            //    camIndex = 0;
            }
            //camIndex = 0;
            Point3D camPoint = shipM.getFrame(0, true, true, true).points.get(camIndex).clone();
                                            //-shipM.getFrame(0, false, true).points.get(0).clone().x;
//            Driver.screenPosition_org_next.x = -camPoint.x;
//            Driver.screenPosition.x = -camPoint.x;
//            Driver.screenPosition_org_next.y = -camPoint.y;
 //           Driver.screenPosition.y = -camPoint.y;
//            Driver.screenPosition_org_next.z = camPoint.z;
            
//            Driver.screenPosition_org_next.identifier = -2;
//            Driver.screenPosition.z = camPoint.z;
            //Driver.screenPosition_org.y = Driver.screenPosition_org.y + 2.5;
            Driver.angleY = Driver.angleY - shipRotY;
            //Driver.angleX = Driver.angleX - shipRotZ;
            Driver.angleZ = Driver.angleZ - shipRotZ;
            for(Point3D i : shipModel.getFrame(0).points){
                //Point3D newLoc = Point3D.add(Driver.screenPosition_org, Point3D.subtract(i, last_sp));
                //Point3D newLoc = i.clone();
               // newLoc = Driver.matmul(driver.RY(shipRotY), newLoc.toFVector3()).toDVector3();
                //newLoc = Driver.matmul(driver.RZ(shipRotZ), newLoc.toFVector3()).toDVector3();
                
//                i.x = newLoc.x + vel.x;
//                i.y = newLoc.y + vel.y;
//                i.z = newLoc.z + vel.z;
                
            }
            //System.out.println(shipM.rotation_Y);
            shipM.rotation_Y = shipM.rotation_Y + shipRotY;
            //System.out.println(shipRotY);
            //shipModel.x = shipModel.x + vel.x;
            //shipModel.y = shipModel.y + vel.y;
            //shipModel.z = shipModel.z + vel.z;
            for(Integer handle : handles){
                Model m = Driver.models.get(handle);
                if(m.getFrame(0).points.getFirst().z < size){
                    //m.getFrame(0).points.getFirst().z = m.getFrame(0).points.getFirst().z + 0.03*100;
                }
                else{
                   // m.getFrame(0).points.getFirst().z = -size;
                }
            }
//            last_sp = Driver.screenPosition_org.clone();
            //Sleep
            try {
                Thread.sleep((long) 0.00001);
            } catch (InterruptedException ex) {
                Logger.getLogger(Fly.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            deltaTime = Duration.between(beginTime, Instant.now());
//            System.out.println("Fly.java excecution time: " + deltaTime.getNano());
        }
    }
    
    int size = 1400*10;
    
    int rx = 5;
    int ry = 5;
    int rz = 5;
    LinkedList<Model> constructCloud(){
        Random rnd = new Random();
        LinkedList<Model> out = new LinkedList<Model>();
        for(int x : new Range(rx)){
            for (int y : new Range(ry)) {
                for (int z : new Range(rz)){
                    LinkedList<ModelFrame> frames = new LinkedList<>();
                    LinkedList<Point3D> points = new LinkedList<>();
                    LinkedList<Integer[]> lines = new LinkedList<>();
                    LinkedList<Point3D[]> faces = new LinkedList<>();
                    LinkedList<vertexGroup> color = new LinkedList<>();
                    
                    int rndX = rnd.nextInt(size*2) - size;
                    int rndY = rnd.nextInt(size*2) - size;
                    int rndZ = rnd.nextInt(size*2) - size;
                    
                    boolean hasConnections = rnd.nextBoolean();
                    if(hasConnections){
                        hasConnections = rnd.nextBoolean();
                    }
                    if(hasConnections){
                        hasConnections = rnd.nextBoolean();
                    }
                    if(hasConnections){
                        hasConnections = rnd.nextBoolean();
                    }
                    if(hasConnections){
                        hasConnections = rnd.nextBoolean();
                    }
                    if(hasConnections){
                        hasConnections = rnd.nextBoolean();
                    }
                    
                    
                    points.add(new Point3D(rndX, rndY, rndZ));
                    
                    if(hasConnections && out.size() > 2){
                        //lines.add(new Integer[]{points.getFirst().identifier, out.get(rnd.nextInt(out.size()-1)).getFrame(0).points.getFirst().identifier});
                    }
                    
                    
                    
                    frames.add(new ModelFrame(points , lines, faces, color));
                    Model m = new Model(frames, true);
                    m.hidePoints = false;
                    out.add(m);
                }
            }
        }
        
        return out;
    }
    
    int rx2 = 15 * 2;
    int ry2 = 15 * 2;
    LinkedList<Model> constructGrid(){
        int ind = 0;
        Random rnd = new Random();
        LinkedList<Model> out = new LinkedList<Model>();
        for(int x : new Range(rx2)){
            for (int y : new Range(ry2)) {
                LinkedList<ModelFrame> frames = new LinkedList<>();
                LinkedList<Point3D> points = new LinkedList<>();
                LinkedList<Integer[]> lines = new LinkedList<>();
                LinkedList<Point3D[]> faces = new LinkedList<>();
                LinkedList<vertexGroup> color = new LinkedList<>();

                int rndX = (x - (rx2 / 2) )*3;
                int rndY = (y - (ry2 / 2) )*3;
                int rndZ = Math.abs((int) Math.sin(rndX + rndY)) * 3;
                


                points.add(new Point3D(-rndX, -4, -rndY));

                
                if(out.size() > 2){
                    try {
                        lines.add(new Integer[]{points.getFirst().identifier, out.get(ind - 1).getFrame(0).points.getFirst().identifier});
                        lines.add(new Integer[]{points.getFirst().identifier, out.get(ind - ry2).getFrame(0).points.getFirst().identifier});
                        //faces.add(new Point2D[]{
                        
                        //});
                    } catch (Exception e) {
                    }
                }
                //if(y == ry2){
                //    lines.add(new Integer[]{points.getFirst().identifier, out.get(ind - ry2).getFrame(0).points.getFirst().identifier});
                //}
                

                frames.add(new ModelFrame(points , lines, faces, color));
                Model m = new Model(frames, true);
                m.hidePoints = false;
                m.hideLines = false;
                out.add(m);
                ind = ind + 1;
            }
        }
        
        return out;
    }
    
}
