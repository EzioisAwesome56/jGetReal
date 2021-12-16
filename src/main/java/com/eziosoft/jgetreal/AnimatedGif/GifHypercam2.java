package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.Raster.Hypercam2;
import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GifHypercam2 {

    /**
     * applies unregistered hypercam 2 watermark to an animated gif
     * @param in source animated gif to watermark
     * @return anmimated gif with watermark applied
     * @throws Exception in case something blows up
     */
    public static byte[] UnregisterGif(byte[] in) throws Exception {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(new ByteArrayInputStream(in));
        // make array to hold processed frames
        List<GIFFrame> imgs = new ArrayList<>();
        // make byte array that will be reused over and over
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        //process every frame
        for (GIFFrame f : cont.getFrames()){
            // clear stream
            temp.reset();
            // use imageio to write current frame to it
            ImageIO.write(f.getFrame(), "png", temp);
            // create new GIF Frame from this data
            imgs.add(new GIFFrame(ImageIO.read(new ByteArrayInputStream(Hypercam2.Unregister(temp.toByteArray()))), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
        }
        // reset the stream
        temp.reset();
        // write animated gif to it
        GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        // convert stream to byte array
        byte[] done = temp.toByteArray();
        // close stream
        temp.close();
        // and we're done
        return done;
    }
}