package com.eziosoft.jgetreal.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RasterUtils {

    /**
     * converts image to jpg format
     * @param in byte array of image to convert
     * @return converted image in jpg format
     * @throws IOException if something blows up
     */
    public static byte[] ConvertToJpeg(byte[] in) throws IOException {
        ImageIO.setUseCache(false);
        BufferedImage tojpeg = ImageIO.read(new ByteArrayInputStream(in));
        // create output buffered image
        BufferedImage jpeg = new BufferedImage(tojpeg.getWidth(), tojpeg.getHeight(), BufferedImage.TYPE_INT_RGB);
        // make graphics 2d
        Graphics2D g = jpeg.createGraphics();
        // draw image
        g.drawImage(tojpeg, 0, 0, Color.white, null);
        // dispose of graphics 2d
        g.dispose();
        // write to output stream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(jpeg, "jpg", stream);
        // convert to byte array
        byte[] finish = stream.toByteArray();
        // close stream
        stream.flush();
        stream.close();
        // return
        return finish;
    }
}
