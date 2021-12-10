package com.eziosoft.jgetreal.utils;

import com.eziosoft.jgetreal.objects.GifContainer;
import com.icafe4j.image.gif.FrameReader;
import com.icafe4j.image.gif.GIFFrame;

import java.io.*;

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
}
