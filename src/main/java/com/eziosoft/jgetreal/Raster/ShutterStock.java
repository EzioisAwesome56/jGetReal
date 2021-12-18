package com.eziosoft.jgetreal.Raster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ShutterStock {

    /**
     * Applies shutterstock watermark to image
     * @param in bytearray of image to
     * @return byte array of watermarked image; png format
     * @throws IOException if something blows uo
     */
    public static byte[] Stockify(byte[] in) throws IOException {
        // set imageIO cache to false
        ImageIO.setUseCache(false);
        // load shutterstock
        InputStream streamin = ShutterStock.class.getResourceAsStream("/shutterstock.png");
        BufferedImage stock = ImageIO.read(streamin);
        streamin.close();
        // load source image
        streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // create graphics 2d instance
        Graphics2D g = source.createGraphics();
        // draw shutterstock to it
        g.drawImage(stock, null, (source.getWidth() - stock.getWidth()) / 2, (source.getHeight() - stock.getHeight()) /2);
        // dispose of graphics
        g.dispose();
        // convert to byte array
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        ImageIO.write(source, "png", temp);
        byte[] done = temp.toByteArray();
        temp.close();
        // return
        return done;
    }
}
