package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class GifSpin {

    public static byte[] spinGif(byte[] in) throws Exception {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // first, we need to get every frame of the gif. Do that
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(new ByteArrayInputStream(in));
        // determine how many times we need to do a 360 spin, based on number of frames / 24
        // (which is how many times 15 goes into 360, meaning 24 frames per each 360 deg rotation)
        int totalspins = 1;
        int leftovers = 0;
        System.err.println("Total frames: " + cont.getFrames().size());
        System.err.println("how many times 24 goes into it: " + cont.getFrames().size() / 24);
        System.err.println("leftover frames: " + cont.getFrames().size() % 24);
        if (cont.getFrames().size() / 24 > 1){
            System.err.println("in if statement");
            totalspins = cont.getFrames().size() / 24;
        }
        // get how many leftover frames we have
        if (cont.getFrames().size() % 24 > 0){
            leftovers = cont.getFrames().size() % 24;
        }
        // list of frames to use as they're processed
        List<GIFFrame> frames = new ArrayList<>();
        // counter for what frame we're on
        int frame = 0;
        // for loop for all main frames
        for (int x = 0; x <= totalspins; x++){
            for (int a = 0; a <= 360; a += 15){
                // first we load the current frame
                BufferedImage source = cont.getFrames().get(frame).getFrame();
                // create new buffered image
                BufferedImage temp = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
                // get graphics 2d for this image
                Graphics2D g = temp.createGraphics();
                // rotate graphics
                g.rotate(Math.toRadians(a), source.getWidth()/2, source.getHeight()/2);
                // draw source to it
                g.drawImage(source, null, 0, 0);
                // dispose of graphics
                g.dispose();
                // add temp to the arraylist
                frames.add(new GIFFrame(temp, 50, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
                // incriment counter by 1
                frame++;
            }
        }
        return null;
    }
}
