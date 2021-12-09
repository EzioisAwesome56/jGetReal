package com.eziosoft.jgetreal.utils;

import com.icafe4j.image.gif.FrameReader;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

public class GifUtils {

    public static List<BufferedImage> splitAnimatedGifEX(InputStream in){
        // first make a frame reader
        FrameReader reader = new FrameReader();
    }
}
