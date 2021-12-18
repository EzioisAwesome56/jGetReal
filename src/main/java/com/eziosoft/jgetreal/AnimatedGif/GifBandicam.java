package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.Raster.Bandicam;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.objects.GifContainer;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GifBandicam {

    /**
     * Applies bandicam.com watermark to an animated gif
     * @param in gif to watermark
     * @return watermarked gif
     * @throws IOException if something blows up in the process
     */
    public static byte[] Watermark(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // create list of new frames
        List<GIFFrame> imgs = new ArrayList<>();
        // create streams
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        InputStream streamin;
        // process frames
        for (GIFFrame f : cont.getFrames()){
            // reset
            stream.reset();
            // first, right current frame to output array
            ImageIO.write(f.getFrame(), "png", stream);
            // then, apply watermark to image
            streamin = new ByteArrayInputStream(Bandicam.Watermark(stream.toByteArray()));
            // add result to the list
            imgs.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
            // close stream
            streamin.close();
        }
        // reset output stream
        stream.reset();
        // write finished gif to stream
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), stream);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert, close, return
        byte[] done = stream.toByteArray();
        stream.close();
        return done;
    }
}
