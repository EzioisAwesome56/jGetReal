package com.eziosoft.jgetreal.Raster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ShutterStock {

    public static byte[] Stockify(byte[] in) throws IOException {
        // set imageIO cache to false
        ImageIO.setUseCache(false);
        // load shutterstock
        BufferedImage stock = ImageIO.read(ShutterStock.class.getResourceAsStream("/shutterstock.png"));
        // load source image
        BufferedImage source = ImageIO.read(new ByteArrayInputStream(in));
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
