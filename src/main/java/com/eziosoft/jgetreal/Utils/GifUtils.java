package com.eziosoft.jgetreal.Utils;

import com.eziosoft.jgetreal.Objects.GifContainer;
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


    public static byte[] ConvertToBytes(List<GIFFrame> in) throws IOException{
        ImageIO.setUseCache(false);
        // make output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // write gif
        try {
            GIFTweaker.writeAnimatedGIF(in.toArray(new GIFFrame[]{}), out);
        }  catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // do the normal shit to ensure there are no memory leaks
        byte[] done = out.toByteArray();
        out.close();
        return done;
    }
}
