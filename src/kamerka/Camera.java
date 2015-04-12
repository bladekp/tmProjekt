/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kamerka;

import com.lti.civil.CaptureDeviceInfo;
import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;
import com.lti.civil.Image;
import com.lti.civil.awt.AWTImageConverter;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

/**
 *
 * @author bladekp
 */
public class Camera implements CaptureObserver {

    private Fps fps;
    private Formatka view;
    private Filters filters;
    private int f;

    private CaptureStream captureStream = null;

    public Camera(Formatka view) {
        this.filters = new Filters();
        this.fps = new Fps();
        this.view = view;

        CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton.instance();
        CaptureSystem system;
        try {
            system = factory.createCaptureSystem();
            system.init();
            List list = system.getCaptureDeviceInfoList();
            int i = 0;
            if (i < list.size()) {
                CaptureDeviceInfo info = (CaptureDeviceInfo) list.get(i);
                System.out.println((new StringBuilder()).append("Device ID ").append(i).append(": ").append(info.getDeviceID()).toString());
                System.out.println((new StringBuilder()).append("Description ").append(i).append(": ").append(info.getDescription()).toString());
                captureStream = system.openCaptureDeviceStream(info.getDeviceID());
                captureStream.setObserver(Camera.this);
                captureStream.start();
            }
        } catch (CaptureException ex) {
            ex.printStackTrace();
        }

        fps.startCamera(createEvent(), 4);
        this.f = 4;
    }

    public ActionListener createEvent() {
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.setTakeShot(true);
            }
        };
        return taskPerformer;
    }

    @Override
    public void onNewImage(CaptureStream stream, Image image) {
        if (this.f != view.getjSlider1().getValue()) {
            this.f = view.getjSlider1().getValue();
            fps.stopCamera();
            fps.startCamera(createEvent(), this.f);
        }
        if (!view.isTakeShot()) {
            return;
        }
        view.setTakeShot(false);
        //System.out.println("New Image Captured");
        byte bytes[] = null;
        try {
            if (image == null) {
                bytes = null;
                return;
            }
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(os);
                jpeg.encode(AWTImageConverter.toBufferedImage(image));
                os.close();
                bytes = os.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                bytes = null;
            } catch (Throwable t) {
                t.printStackTrace();
                bytes = null;
            }
            if (bytes == null) {
                return;
            }
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            File file = new File("/img" + Calendar.getInstance().getTimeInMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            BufferedImage myImage = ImageIO.read(file);
            
            //moje efekty
            if (view.isGrayscale()) {
                filters.grayscale(myImage);
            }
            if (view.isContrast()) {
                filters.contrast(myImage, view.getContrastSlider().getValue());
            }
            if (view.isBrightness()) {
                filters.brightness(myImage, view.getBrightness().getValue());
            }
            if (view.isGamma()) {
                filters.gamma(myImage, view.getGamma().getValue());
            }
            if (view.isInversion()) {
                filters.inversion(myImage);
            }
            if (view.isSolaris()) {
                filters.solarise(myImage, view.getSolarise().getValue());
            }
            //moje efekty
            
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            ImageIO.write(myImage, "jpg", baos2);
            baos2.flush();
            byte bytes2[] = baos2.toByteArray();
            baos2.close();
            FileOutputStream fos3 = new FileOutputStream(file);
            fos3.write(bytes2);
            fos3.close();
            //openCV efekty

            if (view.isSharpness()) filters.sharpness(file.getAbsolutePath(), view.getSharpnessAlpha().getValue(), view.getSharpnessBeta().getValue(), view.getSharpnessgamma().getValue());
            if (view.isBorder()) filters.border(file.getAbsolutePath(), view.getBorderSize().getValue(), view.getBorderType().getSelectedIndex());
            if (view.isThresholding()) filters.thresholding(file.getAbsolutePath(),view.getThresholdingLevel().getValue(), view.getThresholdingType().getSelectedIndex());
            if (view.getFlip().getSelectedIndex() != 0) filters.flip(file.getAbsolutePath(), view.getFlip().getSelectedIndex()-1);
            if (view.isGaussian()) filters.gaussian(file.getAbsolutePath(), view.getGaussianSize().getValue());
            if (view.isErosion()) filters.erosion(file.getAbsolutePath(), view.getErosionSize().getValue());
            if (view.isDilation()) filters.dilation(file.getAbsolutePath(), view.getDilationSize().getValue());
            
            //openCV efekty
            myImage = ImageIO.read(file);
            view.getShot().setText("");
            view.getShot().setIcon(new ImageIcon(myImage));
            view.getShot().revalidate();
            if (view.isSnapshotB()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(myImage, "jpg", baos);
                baos.flush();
                byte bytes3[] = baos.toByteArray();
                baos.close();
                view.setSnapshoyB(false);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.showSaveDialog(null);
                File f = fileChooser.getSelectedFile();
                if (!f.getAbsolutePath().endsWith(".jpg")) {
                    f = new File(f.getAbsolutePath() + ".jpg");
                }
                FileOutputStream fos2 = new FileOutputStream(f);
                fos2.write(bytes3);
                fos2.close();
            }
            if (file.delete()) {
                //  System.out.println(file.getName() + " is deleted!");
            } else {
                // System.out.println("Delete operation is failed.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onError(CaptureStream arg0, CaptureException arg1) {
        throw new UnsupportedOperationException("Error is coming ");

    }

}
