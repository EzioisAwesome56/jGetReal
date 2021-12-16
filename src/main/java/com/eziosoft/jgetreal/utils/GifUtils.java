package com.eziosoft.jgetreal.utils;

import com.eziosoft.jgetreal.objects.GifContainer;
import com.icafe4j.image.gif.FrameReader;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GifUtils {

    public static GifContainer splitAnimatedGifToContainer(InputStream in) throws Exception {
        GifContainer cont = new GifContainer();
        FrameReader reader = new FrameReader();
        GIFFrame frame = reader.getGIFFrameEx(in);
        while (frame != null){
            cont.addFrame(frame);
            frame = reader.getGIFFrameEx(in);
        }
        return cont;
    }

    /**
     * crapifies a gif
     * @param in gif to crapify
     * @return crapifyed gif
     * @throws Exception if something blows up in the process
     */
    public static byte[] CrapifyGif(byte[] in) throws Exception {
        // set imageio caching to false
        ImageIO.setUseCache(false);
        // get gif container
        GifContainer cont = splitAnimatedGifToContainer(new ByteArrayInputStream(in));
        // make list of processed frames
        List<GIFFrame> imgs = new ArrayList<>();
        // make new bytearray output stream for temp space
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        // process every frame
        for (GIFFrame f : cont.getFrames()){
            // reset stream
            temp.reset();
            // use imageIO to write to the stream
            ImageIO.write(f.getFrame(), "png", temp);
            // create new gifframe and add it to the list
            imgs.add(new GIFFrame(ImageIO.read(new ByteArrayInputStream(RasterUtils.CrapifyImage(temp.toByteArray()))), f.getDelay() *  10, f.getDisposalMethod()));
        }
        // reset stream
        temp.reset();
        // write our animated gif to it
        GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        // convert to byte array
        byte[] done = temp.toByteArray();
        // close the stream
        temp.close();
        // return
        return done;
    }
}
