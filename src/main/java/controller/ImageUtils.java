package controller;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by PrifizGamer on 13.01.2017.
 */
public class ImageUtils {

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        if(img == null) {
            return null;
        }
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if(img.getWidth(null) == -1 || img.getHeight(null) == -1) {
            return null;
        }

        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
