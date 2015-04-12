/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kamerka;

/**
 *
 * @author bladekp
 */
import java.lang.reflect.Field;

public class Main {

    public static void init32bEnviroment() throws Exception {
        System.setProperty("java.library.path", "lib\\lti-civil\\native\\win32-x86");
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }

    public static void main(String args[]){
        try{
            init32bEnviroment();
            Formatka view = new Formatka();
            Camera c = new Camera(view);
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }
}
