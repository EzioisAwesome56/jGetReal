package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.Raster.ShutterStock;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GifShutterStock {

    /**
     * Applies shutterstock watermark to animated gifs
     * @param in gif to apply watermark to
     * @return gif with watermark applied
     * @throws IOException if something blows up
     */
    public static byte[] StockifyGif(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // get gif in container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // make new gif frame list
        List<GIFFrame> imgs = new ArrayList<>();
        // setup the output stream
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        // produce the frames
        for (GIFFrame f : cont.getFrames()){
            // reset the stream
            temp.reset();
            // write our image to it
            ImageIO.write(f.getFrame(), "png", temp);
            // add new frame to list
            ByteArrayInputStream streamin = new ByteArrayInputStream(ShutterStock.Stockify(temp.toByteArray()));
            imgs.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
            streamin.close();
        }
        // reset stream
        temp.reset();
        // write gif to it
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert to byte array and close it
        byte[] done = temp.toByteArray();
        temp.close();
        // output it
        return done;
    }
}
