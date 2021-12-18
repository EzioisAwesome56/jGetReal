package com.eziosoft.jgetreal.Utils;

import com.eziosoft.jgetreal.objects.GifContainer;
import com.icafe4j.image.gif.FrameReader;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GifUtils {

    /**
     * Takes in an animated gif, splits it into frames, and outputs as a container
     * @param in gif (but as an input stream) to split
     * @return gif container of input gif
     * @throws IOException if something blows up
     */
    public static GifContainer splitAnimatedGifToContainer(InputStream in) throws IOException {
        GifContainer cont = new GifContainer();
        FrameReader reader = new FrameReader();
        try {
            GIFFrame frame = reader.getGIFFrameEx(in);
            while (frame != null) {
                cont.addFrame(frame);
                frame = reader.getGIFFrameEx(in);
            }
            return cont;
        } catch (Exception e){
            throw new IOException("Error during gif-related operation!", e);
        }
    }

    /**
     * overload for splitanimated gif that takes a raw byte[]
     * @param in as stated previously, takes byte[] instead of input stream
     * @return container for gif
     * @throws IOException if something blows up somewhere
     */
    public static GifContainer splitAnimatedGifToContainer(byte[] in) throws IOException{
        ByteArrayInputStream stream = new ByteArrayInputStream(in);
        GifContainer cont = splitAnimatedGifToContainer(stream);
        stream.close();
        return cont;
    }

    /**
     * crapifies a gif
     * @param in gif to crapify
     * @return crapifyed gif
     * @throws IOException if something blows up in the process
     */
    public static byte[] CrapifyGif(byte[] in) throws IOException {
        // set imageio caching to false
        ImageIO.setUseCache(false);
        // get gif container
        ByteArrayInputStream streamin = new ByteArrayInputStream(in);
        GifContainer cont = splitAnimatedGifToContainer(streamin);
        streamin.close();
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
            streamin = new ByteArrayInputStream(RasterUtils.CrapifyImage(temp.toByteArray()));
            imgs.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() *  10, f.getDisposalMethod()));
            streamin.close();
        }
        // reset stream
        temp.reset();
        // write our animated gif to it
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw new IOException("Error occurred during GIF writing!", e);
        }
        // convert to byte array
        byte[] done = temp.toByteArray();
        // close the stream
        temp.close();
        // return
        return done;
    }
}