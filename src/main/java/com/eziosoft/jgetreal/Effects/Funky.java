package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.icafe4j.image.gif.GIFFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Funky extends ImageEffect {

    /**
     * to be used by the gui; run from an array of effects
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something blows up during the process
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Watermark(input);
    }

    public Funky(){
        this.name = "funky";
        this.needscaption = false;
    }

    /**
     * detects what format the image is and automatically runs it thru the correct parser
     * @param in image to watermark with new funky mode
     * @return watermarked image
     * @throws IOException if something blows up
     */
    public static EffectResult Watermark(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? FunkGif(in) : Funk(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * applies "new  funky mode" watermark to an image
     * @param in byte array of image to watermark
     * @return watermarked image
     * @throws IOException if something blows up for some reason...
     */
    private static byte[] Funk(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // load source buffered image
        BufferedImage source = RasterUtils.ConvertToImage(in);
        // load the watermark
        BufferedImage kong = RasterUtils.loadResource("/funky.png");
        // mirror source image
        source = RasterUtils.MirrorImage(source);
        // create graphics 2d context
        Graphics2D g = source.createGraphics();
        // apply watermark
        g.drawImage(kong, null, 0, 0);
        // dispose of graphics
        g.dispose();
        // mirror the image again
        source = RasterUtils.MirrorImage(source);
        // output to stream
        return RasterUtils.ConvertToBytes(source);

    }

    /**
     * applies "new funky mode" watermark to animated gifs
     * @param in byte array of animated gif to watermark
     * @return watermarked image
     * @throws IOException throws if something blows up in the process
     */
    private static byte[] FunkGif(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif frames as container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // create new list for processes frames
        List<GIFFrame> imgs = new ArrayList<>();
        // process every frame
        for (GIFFrame f : cont.getFrames()) {
            imgs.add(new GIFFrame(RasterUtils.ConvertToImage(Funk(RasterUtils.ConvertToBytes(f.getFrame()))), f.getDelay() * 10, f.getDisposalMethod()));
        }
        // output gif to the stream
        return GifUtils.ConvertToBytes(imgs);
    }
}
