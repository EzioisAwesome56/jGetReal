package com.eziosoft.jgetreal.Raster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Bandicam {

    /**
     * Applies bandicam watermark to image
     * @param in image to watermark
     * @return watermarked image; png format
     * @throws IOException if something goes boom
     */
    public static byte[] Watermark(byte[] in) throws IOException {
        // turn caching off
        ImageIO.setUseCache(false);
        // create stream
        InputStream streamin;
        // load source image
        streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // read the watermark
        streamin = Bandicam.class.getResourceAsStream("/bandicam.png");
        BufferedImage watermark = ImageIO.read(streamin);
        streamin.close();
        // create graphics context
        Graphics2D g = source.createGraphics();
        // draw watermark
        g.drawImage(watermark, null, (source.getWidth() - watermark.getWidth()) / 2, 0);
        // dipose of graphics
        g.dispose();
        // output image via the normal ways
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(source, "png", out);
        byte[] done = out.toByteArray();
        out.close();
        return done;
    }
}
