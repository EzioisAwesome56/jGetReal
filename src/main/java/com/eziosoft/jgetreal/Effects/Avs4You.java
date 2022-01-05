package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Avs4You extends ImageEffect {

    /**
     * applies the avs4you watermark to provided image
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
     * what the fuck is a hedgehog, is this supposed to be a money laundering operation
     * @param input image to process
     * @param caption caption text if required
     * @return promsdkl;fjsdl;kjfsal
     * @throws IOException if something goes boom
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Watermark(input);
    }

    public Avs4You(){
        this.name = "avs4you";
    }


    /**
     * applies avs4you watermark to an image
     * @param in image to watermark
     * @return watermarked;ljsdf;
     * @throws IOException i dont even
     */
    private static byte[] Raster(byte[] in) throws IOException{
        // turn caching off because hjlkfsafpwhpf
        ImageIO.setUseCache(false);
        // load source image
        BufferedImage source = RasterUtils.ConvertToImage(in);
        // load watermark
        BufferedImage watermark = RasterUtils.loadResource("/avs4you.png");
        // create graphics context
        Graphics2D g = source.createGraphics();
        // draw
        g.drawImage(watermark, null, (source.getWidth() - watermark.getWidth()) /2, (source.getHeight() - watermark.getHeight()) / 2);
        // throw it out
        g.dispose();
        // return it
        return RasterUtils.ConvertToBytes(source);
    }

    /**
     * like raster, but for gifs (wow)!
     * @param in gif to watermark
     * @return watermarked gif
     * @throws IOException crashboombang oof
     */
    private static byte[] Gif(byte[] in) throws IOException{
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif container
        ByteArrayInputStream streamin = new ByteArrayInputStream(in);
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(streamin);
        streamin.close();
        // make array to hold processed frames
        List<GIFFrame> imgs = new ArrayList<>();
        //process every frame
        for (GIFFrame f : cont.getFrames()){
            // create new GIF Frame from this data
            imgs.add(new GIFFrame(RasterUtils.ConvertToImage(Raster(RasterUtils.ConvertToBytes(f.getFrame()))), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_PREVIOUS));
        }
        // write animated gif to it
        return GifUtils.ConvertToBytes(imgs);
    }
}
