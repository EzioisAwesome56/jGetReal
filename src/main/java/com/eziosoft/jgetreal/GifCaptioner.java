package com.eziosoft.jgetreal;

import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.utils.GifUtils;
import com.icafe4j.image.gif.GIFTweaker;

import java.io.ByteArrayInputStream;

public class GifCaptioner {

    public static void CaptionGif(byte[] in, String text) throws Exception {
        // todo: stuff
        GifContainer cont = GifUtils.splitAnimatedGifEX(new ByteArrayInputStream(in));
    }
}
