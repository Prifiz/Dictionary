package utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        if(image == null) {
            return null;
        }
        int currentWidth = image.getWidth();
        int currentHeight = image.getHeight();
        BufferedImage result = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D graphics = result.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(image, 0, 0, newWidth, newHeight, 0, 0, currentWidth, currentHeight, null);
        graphics.dispose();
        return result;
    }

    public static BufferedImage toBufferedImage(Image image)
    {
        if(image.getWidth(null) == -1 || image.getHeight(null) == -1) {
            return null;
        }

        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();

        return bufferedImage;
    }
}
