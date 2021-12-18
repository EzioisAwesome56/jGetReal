package com.eziosoft.jgetreal.Raster;

import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Spin {

    /**
     * creates an animated gif of the provided raster image spinning 360 degrees
     * @param in byte array of raster image to spin
     * @return byte array of animated gif
     * @throws Exception if something blows up during generation
     */
    public static byte[] spinRaster(byte[] in) throws Exception {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // create list of buffered images to be filled later
        List<GIFFrame> imgs = new ArrayList<>();
        // read source image from provided byte array
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // get dimensions of source
        int h = source.getHeight();
        int w = source.getWidth();
        // create all the frames
        for (int a = 0; a <= 360; a += 15){
            // create new buffered image
            BufferedImage temp = new BufferedImage(w, h, source.getType());
            // get graphics 2d for this image
            Graphics2D g = temp.createGraphics();
            // rotate graphics
            g.rotate(Math.toRadians(a), w/2, h/2);
            // draw source to it
            g.drawImage(source, null, 0, 0);
            // dispose of graphics
            g.dispose();
            // add temp to the arraylist
            imgs.add(new GIFFrame(temp, 50, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
        }
        // create new byteoutputstream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // write gif to stream
        GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), out);
        // convert tp byte array
        byte[] done = out.toByteArray();
        // close stream
        out.close();
        // return
        return done;
    }
}
