package com.eziosoft.jgetreal.Raster;

import com.eziosoft.jgetreal.Utils.RasterUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Hypercam2 {

    /**
     * applies the unregistered hypercam2 watermark to an image
     * @param in byte array of image to apply watermark too
     * @return byte array of watermarked image; png format
     * @throws IOException if something blows up
     */
    public static byte[] Unregister(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // load source image from byte array
        BufferedImage source = ImageIO.read(new ByteArrayInputStream(in));
        // load hypercam2 watermark from resources
        BufferedImage hypercam = ImageIO.read(Hypercam2.class.getResourceAsStream("/hypercam.png"));
        // create graphics 2d context of the source image
        Graphics2D g = source.createGraphics();
        // draw hypercam onto the source image at 0,0
        g.drawImage(hypercam, null, 0, 0);
        // throw out graphics 2d
        g.dispose();
        // create new stream to write too
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // write to said stream
        ImageIO.write(source, "png", out);
        // convert to byte array
        byte[] done = out.toByteArray();
        // close stream
        out.close();
        // return
        return done;
    }

    /**
     * applies unregistered hypercam 2 watermark to image
     * @param in bytearray of image to apply water mark too
     * @return watermarked image; jpeg format
     * @throws IOException if something blows up
     */
    public static byte[] UnregisterJpeg(byte[] in) throws IOException{
        return RasterUtils.ConvertToJpeg(Unregister(in));
    }
}
