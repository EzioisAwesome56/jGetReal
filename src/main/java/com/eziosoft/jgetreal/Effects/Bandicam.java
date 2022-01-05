package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Bandicam extends ImageEffect {

    /**
     * applies the bandicam watermark to provided image
     * @param in image to watermark
     * @return watermarked image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Watermark(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? Gif(in) : Raster(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * Applies bandicam watermark to image
     * @param in image to watermark
     * @return watermarked image; png format
     * @throws IOException if something goes boom
     */
    private static byte[] Raster(byte[] in) throws IOException {
        // turn caching off
        ImageIO.setUseCache(false);
        // load source image
        BufferedImage source = RasterUtils.ConvertToImage(in);
        // read the watermark
        BufferedImage watermark = RasterUtils.loadResource("/bandicam.png");
        // create graphics context
        Graphics2D g = source.createGraphics();
        // draw watermark
        g.drawImage(watermark, null, (source.getWidth() - watermark.getWidth()) / 2, 0);
        // dipose of graphics
        g.dispose();
        // output image via the normal ways
        return RasterUtils.ConvertToBytes(source);
    }

    /**
     * Applies bandicam.com watermark to an animated gif
     * @param in gif to watermark
     * @return watermarked gif
     * @throws IOException if something blows up in the process
     */
    private static byte[] Gif(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // create list of new frames
        List<GIFFrame> imgs = new ArrayList<>();
        // process frames
        for (GIFFrame f : cont.getFrames()){
            // add result to the list
            imgs.add(new GIFFrame(RasterUtils.ConvertToImage(Raster(RasterUtils.ConvertToBytes(f.getFrame()))), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
        }
        // write finished gif to stream
        return GifUtils.ConvertToBytes(imgs);
    }

    /**
     * to be used for the gui; runs the effect
     * @param input image to process
     * @param fuck caption text
     * @return processed image
     * @throws IOException if something blows up
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... fuck) throws IOException {
        return Watermark(input);
    }

    public Bandicam(){
        this.name = "bandicam";
        this.needscaption = false;
    }
}
