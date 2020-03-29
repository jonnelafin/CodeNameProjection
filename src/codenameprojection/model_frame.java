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

import JFUtils.point.Point2D;
import JFUtils.point.Point3D;
import java.util.LinkedList;

/**
 *
 * @author Elias Eskelinen
 */
public class model_frame {
    public LinkedList<Point3D> points;
    public LinkedList<Integer[]> lines;
    public LinkedList<Point2D[]> faces = new LinkedList<>();

    public model_frame(LinkedList<Point3D> p, LinkedList<Integer[]> l, LinkedList<Point2D[]> f) {
        this.points = p;
        this.lines = l;
        this.faces = f;
    }

    @Override
    protected Object clone(){
        return new model_frame((LinkedList<Point3D>) points.clone(), (LinkedList<Integer[]>) lines.clone(), (LinkedList<Point2D[]>) faces.clone());
    }
    
    
}
