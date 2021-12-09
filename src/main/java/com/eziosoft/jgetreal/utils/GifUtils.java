package com.eziosoft.jgetreal.utils;

import com.eziosoft.jgetreal.objects.GifContainer;
import com.icafe4j.image.gif.FrameReader;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.reader.GIFReader;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GifUtils {

    public static GifContainer splitAnimatedGifEX(InputStream in) throws Exception {
        // first make a frame reader
        FrameReader reader = new FrameReader();
        GIFFrame frame = reader.getGIFFrameEx(in);
        GifContainer cont = new GifContainer();
        while (frame != null){
            cont.addFrame(frame.getFrame());
            cont.addDelay(frame.getDelay());
            frame = reader.getGIFFrameEx(in);
        }
        return cont;
    }
}
