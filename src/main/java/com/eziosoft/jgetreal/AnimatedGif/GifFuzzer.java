package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.Raster.ImageFuzzer;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GifFuzzer {

    /**
     * fuzzes every frame of an animated gif
     * @param in byte array of gif you wish to fuz
     * @return byte array of fuzzed gif
     * @throws IOException if something blows up in the process
     */
    public static byte[] FuzzGif(byte[] in) throws IOException {
        ImageIO.setUseCache(false);
        // first we need to get all the frames of the gif
        ByteArrayInputStream streamin = new ByteArrayInputStream(in);
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(streamin);
        streamin.close();
        // make new list of gifFrames
        List<GIFFrame> processed = new ArrayList<>();
        // process each frame
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        for (GIFFrame f : cont.getFrames()){
            // flush old streams
            temp.reset();
            // write to output stream
            ImageIO.write(f.getFrame(), "png", temp);
            // put new frame into the list
            streamin = new ByteArrayInputStream(ImageFuzzer.FuzzImage(temp.toByteArray()));
            processed.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() * 10, f.getDisposalMethod()));
            streamin.close();
        }
        // flush old data out of temp
        temp.reset();
        // create the animated gif
        try {
            GIFTweaker.writeAnimatedGIF(processed.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert to byte array
        byte[] finish = temp.toByteArray();
        // close stream
        temp.close();
        // return
        return finish;
    }
}
