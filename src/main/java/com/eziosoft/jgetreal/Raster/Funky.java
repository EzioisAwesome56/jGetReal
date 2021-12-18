package com.eziosoft.jgetreal.Raster;

import com.eziosoft.jgetreal.Utils.RasterUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Funky {

    /**
     * applies "new  funky mode" watermark to an image
     * @param in byte array of image to watermark
     * @return watermarked image
     * @throws IOException if something blows up for some reason...
     */
    public static byte[] Funk(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // load source buffered image
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // load the watermark
        streamin = Funky.class.getResourceAsStream("/funky.png");
        BufferedImage kong = ImageIO.read(streamin);
        streamin.close();
        // mirror source image
        source = RasterUtils.MirrorImage(source);
        // create graphics 2d context
        Graphics2D g = source.createGraphics();
        // apply watermark
        g.drawImage(kong, null, 0, 0);
        // dispose of graphics
        g.dispose();
        // mirror the image again
        source = RasterUtils.MirrorImage(source);
        // output to stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(source, "png", out);
        // convert to byte array, close, return
        byte[] done = out.toByteArray();
        out.close();
        return done;
    }
}
