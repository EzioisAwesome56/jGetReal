package com.eziosoft.jgetreal.Raster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Invert {

    /**
     * inverts colors of an image
     * @param in image to invert
     * @return inverted image; png format
     * @throws IOException if something blows up in the process
     */
    public static byte[] InvertColors(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // load source image
        BufferedImage source = ImageIO.read(new ByteArrayInputStream(in));
        // create new image with same size as source image
        BufferedImage out = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        // loop thru every pixel of source
        for (int y = 0; y < source.getHeight(); y++){
            for (int x = 0; x < source.getWidth(); x++){
                // read color of pixel
                Color col = new Color(source.getRGB(x, y));
                // create new color, but invert it
                Color outcol = new Color(Math.abs(col.getRed() - 255), Math.abs(col.getGreen() - 255), Math.abs(col.getBlue() - 255));
                // output color to output buffered image
                out.setRGB(x, y, outcol.getRGB());
            }
        }
        // create stream and write output to it
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(out, "png", stream);
        // convert, close, return
        byte[] done = stream.toByteArray();
        stream.close();
        return done;
    }
}
