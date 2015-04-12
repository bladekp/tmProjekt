/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kamerka;

import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author bladekp
 */
public class Fps {
    
    private Timer t;
    
    public Fps(){
    }

    public void startCamera(ActionListener task, int fps){
        t = new Timer(1000/fps, task);
        t.start();
    }
    
    public void stopCamera(){
        t.stop();
    }
    
}
