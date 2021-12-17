package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.Raster.Funky;
import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GifFunky {

    /**
     * applies "new funky mode" watermark to animated gifs
     * @param in byte array of animated gif to watermark
     * @return watermarked image
     * @throws Exception throws if something blows up in the process
     */
    public static byte[] FunkGif(byte[] in) throws Exception {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif frames as container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // create new list for processes frames
        List<GIFFrame> imgs = new ArrayList<>();
        // create reusable stream
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        // process every frame
        for (GIFFrame f : cont.getFrames()){
            // reset stream
            temp.reset();
            // write frame to stream
            ImageIO.write(f.getFrame(), "png", temp);
            // add frame once processed
            imgs.add(new GIFFrame(ImageIO.read(new ByteArrayInputStream(Funky.Funk(temp.toByteArray()))), f.getDelay() * 10, f.getDisposalMethod()));
        }
        // reset stream
        temp.reset();
        // output gif to the stream
        GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        // convert to array, close, return
        byte[] done = temp.toByteArray();
        temp.close();
        return done;
    }
}
