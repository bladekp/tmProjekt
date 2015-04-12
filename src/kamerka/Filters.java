/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kamerka;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author bladekp
 */
public class Filters {

    public void grayscale(BufferedImage myImage) {
        for (int i = 0; i < myImage.getHeight(); i++) {
            for (int j = 0; j < myImage.getWidth(); j++) {
                Color c = new Color(myImage.getRGB(j, i));
                int red = (int) (c.getRed());
                int green = (int) (c.getGreen());
                int blue = (int) (c.getBlue());
                int avg = (red + green + blue) / 3;
                Color newColor = new Color(avg, avg, avg);
                myImage.setRGB(j, i, newColor.getRGB());
            }
        }
    }

    public void contrast(BufferedImage myImage, int contrast) {
        int f = (259 * (contrast + 255));
        int g = (255 * (259 - contrast));
        double factor = (double) f / g;
        for (int i = 0; i < myImage.getHeight(); i++) {
            for (int j = 0; j < myImage.getWidth(); j++) {
                Color c = new Color(myImage.getRGB(j, i));
                int red = (int) (c.getRed());
                int green = (int) (c.getGreen());
                int blue = (int) (c.getBlue());
                int newRed = truncate(factor * (red - 128) + 128);
                int newGreen = truncate(factor * (green - 128) + 128);
                int newBlue = truncate(factor * (blue - 128) + 128);
                Color newColor = new Color(newRed, newGreen, newBlue);
                myImage.setRGB(j, i, newColor.getRGB());
            }
        }
    }

    public void brightness(BufferedImage myImage, int brightness) {
        for (int i = 0; i < myImage.getHeight(); i++) {
            for (int j = 0; j < myImage.getWidth(); j++) {
                Color c = new Color(myImage.getRGB(j, i));
                int red = (int) (c.getRed());
                int green = (int) (c.getGreen());
                int blue = (int) (c.getBlue());
                int newRed = truncate(red + brightness);
                int newGreen = truncate(green + brightness);
                int newBlue = truncate(blue + brightness);
                Color newColor = new Color(newRed, newGreen, newBlue);
                myImage.setRGB(j, i, newColor.getRGB());
            }
        }
    }

    public void gamma(BufferedImage myImage, int g) {
        double gamma = (double) g / 100;
        double gammaCorrection = (double) 1 / gamma;
        for (int i = 0; i < myImage.getHeight(); i++) {
            for (int j = 0; j < myImage.getWidth(); j++) {
                Color c = new Color(myImage.getRGB(j, i));
                int red = (int) (c.getRed());
                int green = (int) (c.getGreen());
                int blue = (int) (c.getBlue());
                int newRed = (int) (255 * Math.pow(((double) red / 255), gammaCorrection));
                int newGreen = (int) (255 * Math.pow(((double) green / 255), gammaCorrection));
                int newBlue = (int) (255 * Math.pow(((double) blue / 255), gammaCorrection));
                Color newColor = new Color(newRed, newGreen, newBlue);
                myImage.setRGB(j, i, newColor.getRGB());
            }
        }
    }

    public void inversion(BufferedImage myImage) {
        for (int i = 0; i < myImage.getHeight(); i++) {
            for (int j = 0; j < myImage.getWidth(); j++) {
                Color c = new Color(myImage.getRGB(j, i));
                int red = (int) (c.getRed());
                int green = (int) (c.getGreen());
                int blue = (int) (c.getBlue());
                int newRed = 255 - red;
                int newGreen = 255 - green;
                int newBlue = 255 - blue;
                Color newColor = new Color(newRed, newGreen, newBlue);
                myImage.setRGB(j, i, newColor.getRGB());
            }
        }
    }

    public void solarise(BufferedImage myImage, int threshold) {
        for (int i = 0; i < myImage.getHeight(); i++) {
            for (int j = 0; j < myImage.getWidth(); j++) {
                Color c = new Color(myImage.getRGB(j, i));
                int red = (int) (c.getRed());
                int green = (int) (c.getGreen());
                int blue = (int) (c.getBlue());
                int newRed = red;
                int newGreen = green;
                int newBlue = blue;
                if (red < threshold) newRed = 255 - red;
                if (green < threshold) newGreen = 255 - green;
                if (blue < threshold) newBlue = 255 - blue;
                Color newColor = new Color(newRed, newGreen, newBlue);
                myImage.setRGB(j, i, newColor.getRGB());
            }
        }
    }

    public void sharpness(String sourcePath, int a, int b, int g) {
        double alpha = (double) a / 100;
        double beta = (double) b / 100;
        double gamma = (double) g / 100;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat source = Highgui.imread(sourcePath, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());
        Imgproc.GaussianBlur(source, destination, new Size(0, 0), 10);
        Core.addWeighted(source, alpha, destination, beta, gamma, destination);
        Highgui.imwrite(sourcePath, destination);
    }

    public void border(String sourcePath, int s, int borderType) {
        if (borderType == -1) {
            return;
        }
        if (borderType == 5) {
            borderType = 16;
        }
        double size = (double) s / 100;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat source = Highgui.imread(sourcePath, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());
        int top, bottom, left, right;
        top = (int) (size * source.rows());
        bottom = (int) (size * source.rows());
        left = (int) (size * source.cols());
        right = (int) (size * source.cols());
        destination = source;
        Imgproc.copyMakeBorder(source, destination, top, bottom, left, right, borderType);
        Highgui.imwrite(sourcePath, destination);
    }

    public void thresholding(String sourcePath, int thesh, int type) {
        if (type == -1) {
            return;
        }
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat source = Highgui.imread(sourcePath, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());
        destination = source;
        Imgproc.threshold(source, destination, thesh, 255, type);
        Highgui.imwrite(sourcePath, destination);
    }
    
    public void flip(String sourcePath, int code){
        if (code == -1) return;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat source = Highgui.imread(sourcePath, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());
        destination = source;
        Core.flip(source, source, code);
        Highgui.imwrite(sourcePath, destination);
    }
    
    public void gaussian(String sourcePath, int size){
        try{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat source = Highgui.imread(sourcePath, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());
        destination = source;
        if (size%2==0) size++;
        Imgproc.GaussianBlur(source, destination,new Size(size, size), 0);
        Highgui.imwrite(sourcePath, destination);
        } catch (Exception e){
            System.out.println("Error in gaussian func"+ e.getMessage());
        }
    }
    
    public void erosion(String sourcePath, int size){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
         Mat source = Highgui.imread(sourcePath,  Highgui.CV_LOAD_IMAGE_COLOR);
         Mat destination = new Mat(source.rows(),source.cols(),source.type());
         destination = source;
         Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*size + 1, 2*size+1));
         Imgproc.erode(source, destination, element);
         Highgui.imwrite(sourcePath, destination);
    }

    public void dilation(String sourcePath, int size){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
         Mat source = Highgui.imread(sourcePath,  Highgui.CV_LOAD_IMAGE_COLOR);
         Mat destination = new Mat(source.rows(),source.cols(),source.type());
         destination = source;
         Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*size + 1, 2*size+1));
         Imgproc.dilate(source, destination, element);
         Highgui.imwrite(sourcePath, destination);
    }
    
    public int truncate(double x) {
        if (x > 255) {
            x = 255;
        }
        if (x < 0) {
            x = 0;
        }
        return (int) x;
    }
}
