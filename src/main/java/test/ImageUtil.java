package test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {

    public static Color[][] loadPixelsFromImage(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        return loadPixelsFromImage(image);
    }
    
    public static Color[][] loadPixelsFromImage(BufferedImage bi) {
    	
        Color[][] colors = new Color[bi.getWidth()][bi.getHeight()];
        
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                colors[x][y] = new Color(bi.getRGB(x, y));
            }
        }
        
        return colors;
    }

    public static void main(String[] args) throws IOException {
        Color[][] colors = loadPixelsFromImage(new File("image.png"));
        System.out.println("Color[0][0] = " + colors[0][0]);
    }
}