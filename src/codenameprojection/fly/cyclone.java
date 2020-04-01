/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package codenameprojection.fly;

import codenameprojection.driver;
import codenameprojection.model;
import codenameprojection.model_frame;
import java.util.LinkedList;

/**
 *
 * @author Jonnelafin
 */
public class cyclone extends model{

    public cyclone(LinkedList<model_frame> frames, boolean singleFrame) {
        super(frames, singleFrame);
    }

    @Override
    public void executeLogic(driver parent_) {
        super.executeLogic(parent_);
        try {
            model player = (model) parent.models.values().toArray()[0];
            setZ(player.getZ() -10);
            setY(player.getY());
            setX(player.getX());
            rotation_X = player.rotation_X;
            rotation_Y = player.rotation_Y;
            rotation_Z = player.rotation_Z;
        } catch (Exception e) {
        }
        setZ(getZ()-1);
    }

    
}
