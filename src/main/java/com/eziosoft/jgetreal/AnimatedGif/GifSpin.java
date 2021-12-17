package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GifSpin {

    /**
     * spins a gif 360 degrees
     * @param in byte array of animated gif
     * @return byte array of processed gif
     * @throws Exception if something blows up
     */
    public static byte[] spinGif(byte[] in) throws Exception {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // first, we need to get every frame of the gif. Do that
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(new ByteArrayInputStream(in));
        // determine step for each spin frame
        double step = Math.ceil(360D / cont.getFrames().size());
        // make list to store process frames
        List<GIFFrame> imgs = new ArrayList<>();
        // setup counter
        int count = 0;
        // process each frame of the gif
        for (double x = 0; x <= 360D; x += step){
            // load the current frame into a buffered image
            BufferedImage source = cont.getFrames().get(count).getFrame();
            // create new buffered image
            BufferedImage temp = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
            // get graphics 2d for this image
            Graphics2D g = temp.createGraphics();
            // rotate graphics
            g.rotate(Math.toRadians(x), source.getWidth()/2, source.getHeight()/2);
            // draw source to it
            g.drawImage(source, null, 0, 0);
            // dispose of graphics
            g.dispose();
            // add temp to the arraylist
            imgs.add(new GIFFrame(temp, cont.getFrames().get(count).getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
            // add 1 to the counter
            count++;
        }
        // create output stream to store gif in
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
