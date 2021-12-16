package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GifShutterStock {

    public static byte[] StockifyGif(byte[] in) throws Exception {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // get gif in container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // make new gif frame list
        List<GIFFrame> imgs = new ArrayList<>();
        // setup the output stream
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
    }
}
