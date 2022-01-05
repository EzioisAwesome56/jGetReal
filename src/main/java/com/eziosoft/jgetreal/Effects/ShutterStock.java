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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShutterStock extends ImageEffect {

    /**
     * detects image format and applies shutterstock watermark to it
     * @param in image to watermark
     * @return watermarked image image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Watermark(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? StockifyGif(in) : Stockify(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * Applies shutterstock watermark to image
     * @param in bytearray of image to
     * @return byte array of watermarked image; png format
     * @throws IOException if something blows uo
     */
    private static byte[] Stockify(byte[] in) throws IOException {
        // set imageIO cache to false
        ImageIO.setUseCache(false);
        // load shutterstock
       BufferedImage stock = RasterUtils.loadResource("/shutterstock.png");
        // load source image
        BufferedImage source = RasterUtils.ConvertToImage(in);
        // create graphics 2d instance
        Graphics2D g = source.createGraphics();
        // draw shutterstock to it
        g.drawImage(stock, null, (source.getWidth() - stock.getWidth()) / 2, (source.getHeight() - stock.getHeight()) /2);
        // dispose of graphics
        g.dispose();
        // convert to byte array
        return RasterUtils.ConvertToBytes(source);
    }

    /**
     * Applies shutterstock watermark to animated gifs
     * @param in gif to apply watermark to
     * @return gif with watermark applied
     * @throws IOException if something blows up
     */
    private static byte[] StockifyGif(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // get gif in container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // make new gif frame list
        List<GIFFrame> imgs = new ArrayList<>();
        // produce the frames
        for (GIFFrame f : cont.getFrames()){
            imgs.add(new GIFFrame(RasterUtils.ConvertToImage(Stockify(RasterUtils.ConvertToBytes(f.getFrame()))), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
        }
        return GifUtils.ConvertToBytes(imgs);
    }

    /**
     * mayo is bad for you yknow
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something blows up
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Watermark(input);
    }

    public ShutterStock(){
        this.name = "shutterstock";
    }
}
