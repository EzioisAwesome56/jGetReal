package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.Raster.ImageFuzzer;
import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GifFuzzer {

    /**
     * fuzzes every frame of an animated gif
     * @param in byte array of gif you wish to fuz
     * @return byte array of fuzzed gif
     * @throws Exception if something blows up in the process
     */
    public static byte[] FuzzGif(byte[] in) throws Exception{
        ImageIO.setUseCache(false);
        // first we need to get all the frames of the gif
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(new ByteArrayInputStream(in));
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
            processed.add(new GIFFrame(ImageIO.read(new ByteArrayInputStream(ImageFuzzer.FuzzImage(temp.toByteArray()))), f.getDelay() * 10, f.getDisposalMethod()));
        }
        // flush old data out of temp
        temp.reset();
        // create the animated gif
        GIFTweaker.writeAnimatedGIF(processed.toArray(new GIFFrame[]{}), temp);
        // convert to byte array
        byte[] finish = temp.toByteArray();
        // close stream
        temp.close();
        // return
        return finish;
    }
}
