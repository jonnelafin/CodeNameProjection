/*
 * The MIT License
 *
 * Copyright 2020 Elias.
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

package saphireJump;

import fps.*;
import JFUtils.point.Point3D;
import codenameprojection.driver;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.JLabel;

/**
 *
 * @author Jonnelafin
 */
public class FPSMap2 implements UI.drawable{
    Run parent;
    public FPSMap2(Run parent) {
        this.parent = parent;
    }
    private boolean menu = false;
    public void showMenu(){
        showMenu(true);
    }
    public void showMenu(boolean on){
        menu = on;
        parent.menu = menu;
        parent.pause = menu;
    }
    @Override
    public void paint(Graphics g, int w, int h, driver logic) {
        int scale = 7;
        int box_w = w / scale;
        int box_h = h / scale;
        Color bg = new Color(0.2F, 0.25F, 0.3F, 0.4F);
        Color fg = new Color(0.6F, 0.2F, 0.2F, 1F);
        g.setColor(bg);
        int xf = w - w/scale/2 - box_w / 2;
        int yf = h - h / scale/2 - box_h / 2;
        g.fillRect(xf, yf, box_w, box_h);
        g.setClip(xf, yf, box_w, box_h);
        g.setColor(Color.CYAN);
        int wf = xf + (box_w/2);
        int hf = yf + (box_h/2);
        try {
            logic.points.forEach(l -> {
                Point3D r = logic.getScreenPosition_org();
                //r = driver.matmul(driver.RY((float) logic.angleY),logic.getScreenPosition_org().toFVector3()).toDVector3();
                int lx = (int) ((r.x - l.x)*0.2);
                int ly = (int) ((r.z - l.z)*0.2);
                // lx = (int) (logic.screenPosition.x - l.x);
                // ly = (int) (logic.screenPosition.z - l.z);
                if (lx < 1000 && ly < 1000 && lx > -1000 && ly > -1000) {
                    //g.fillRect(wf+(int) -ly*10, hf+(int) lx*10, 4, 4);
                }
            });
        } catch (Exception e) {
        //    e.printStackTrace();
        }
        
        g.setClip(null);
        g.setColor(fg);
        //int wf2 = wf + wf / 10;
        //int hf2 = hf + hf / 10;
        //int wt = wf - wf / 10;
        //int ht = hf - hf / 10;
        //g.fillRect(wf-wt, hf-ht, wt, ht);
        g.fillRect(wf, hf, box_w/12, box_h/12);
        if(parent.Driver.inp.parentInFocus && parent.Driver.inp.mouseDown && menu){
            showMenu(false);
            parent.Driver.inp.mouseDown = false;
        }
        if(menu){
            paintMenu(g,w,h);
        }
    }
    void paintMenu(Graphics g, int w, int h){
        g.setColor(new Color(0F, 0F, 0F, .6F));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(1F, 1F, 1F, .9F));
        g.fillRect(w / 3, h/3, (w/3)*1, (h/3)*1);
        g.setColor(Color.black);
        Font FONT = g.getFont();
        //g.drawString("Back to the game", w/2, h/2);
        centerString(g, new Rectangle(w, h), "Continue The Game!", FONT);
    }
    
    
    public void centerString(Graphics g, Rectangle r, String s, Font font) {
        FontRenderContext frc = new FontRenderContext(null, true, true);

        Rectangle2D r2D = font.getStringBounds(s, frc);
        int rWidth = (int) Math.round(r2D.getWidth());
        int rHeight = (int) Math.round(r2D.getHeight());
        int rX = (int) Math.round(r2D.getX());
        int rY = (int) Math.round(r2D.getY());

        int a = (r.width / 2) - (rWidth / 2) - rX;
        int b = (r.height / 2) - (rHeight / 2) - rY;

        g.setFont(font);
        g.drawString(s, r.x + a, r.y + b);
    }
}
