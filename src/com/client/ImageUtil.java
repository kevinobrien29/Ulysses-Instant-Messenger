package com.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
 
public class ImageUtil {
    private static void getFiles(File folder, ArrayList<File> list) {
        folder.setReadOnly();
        File[] files = folder.listFiles();
        for(int j = 0; j < files.length; j++) {
            list.add(files[j]);
            System.out.println(files[j].toURI());
            if(files[j].isDirectory())
                getFiles(files[j], list);
        }
    }
    public static void resize(File originalFile, File resizedFile, int newWidth, float quality) throws IOException {
 
        if (quality < 0 || quality > 1) {
            throw new IllegalArgumentException("Quality has to be between 0 and 1");
        }
 
        ImageIcon ii = new ImageIcon(originalFile.getCanonicalPath());
        Image i = ii.getImage();
        Image resizedImage = null;
 
        int iWidth = i.getWidth(null);
        int iHeight = i.getHeight(null);
 
        if (iWidth > iHeight) {
            resizedImage = i.getScaledInstance(newWidth, (newWidth * iHeight) / iWidth, Image.SCALE_SMOOTH);
        } else {
            resizedImage = i.getScaledInstance((newWidth * iWidth) / iHeight, newWidth, Image.SCALE_SMOOTH);
        }
 
        // This code ensures that all the pixels in the image are loaded.
        Image temp = new ImageIcon(resizedImage).getImage();
 
        // Create the buffered image.
        BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null),
                                                        BufferedImage.TYPE_INT_RGB);
 
        // Copy image to buffered image.
        Graphics g = bufferedImage.createGraphics();
 
        // Clear background and paint the image.
        g.setColor(Color.white);
        g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
        g.drawImage(temp, 0, 0, null);
        g.dispose();
 
        // Soften.
        float softenFactor = 0.05f;
        float[] softenArray = {0, softenFactor, 0, softenFactor, 1-(softenFactor*4), softenFactor, 0, softenFactor, 0};
        Kernel kernel = new Kernel(3, 3, softenArray);
        ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        bufferedImage = cOp.filter(bufferedImage, null);
 
        // Write the jpeg to a file.
        FileOutputStream out = new FileOutputStream(resizedFile);
 
        // Encodes image as a JPEG data stream
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
 
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);
 
        param.setQuality(quality, true);
 
        encoder.setJPEGEncodeParam(param);
        encoder.encode(bufferedImage);
        out.close();
    }
    
    public static void shrinkAll()
    {
    	File folder = new File(System.getenv("ULYSSES_HOME") + "\\Library\\Images");
    	ArrayList<File> list = new ArrayList<File>();
        getFiles(folder, list);
        
        for (int i = 0; i < list.size(); i++)
        {
            try {
            	File outputFolder = new File(list.get(i).getParent() + "/thumbs");
            	outputFolder.mkdirs();
				resize(list.get(i), new File(outputFolder + "/" + list.get(i).getName()), 200, 0.2f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 
    }
 
    // Example usage
    public static void main(String[] args) throws IOException {
    	shrinkAll();
    }
 
}
