package com.eziosoft.jgetreal.Raster;

import com.eziosoft.jgetreal.Utils.RasterUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TouhouLook {

    /**
     * makes 2 touhou characters look at an image
     * @param in image to watermark
     * @return watermarked image, png format
     * @throws IOException if something blows up during the process
     */
    public static byte[] Look(byte[] in) throws IOException {
        // set imageio cache to OFF
        ImageIO.setUseCache(false);
        // make input stream
        InputStream streamin;
        // load source image
        streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // load watermark
        streamin = TouhouLook.class.getResourceAsStream("/2hulook.png");
        BufferedImage watermark = ImageIO.read(streamin);
        streamin.close();
        // rotate the source image
        source = RasterUtils.VerticalFlip(source);
        // create graphics instance
        Graphics2D g = source.createGraphics();
        // draw watermark
        g.drawImage(watermark, null, 0, 0);
        // delete graphics instance
        g.dispose();
        // flip again
        source = RasterUtils.VerticalFlip(source);
        // output result
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(source, "png", out);
        byte[] done = out.toByteArray();
        out.close();
        return done;
    }
}
