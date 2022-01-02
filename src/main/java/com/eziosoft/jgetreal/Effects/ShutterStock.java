package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShutterStock {

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
        InputStream streamin = ShutterStock.class.getResourceAsStream("/shutterstock.png");
        BufferedImage stock = ImageIO.read(streamin);
        streamin.close();
        // load source image
        streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // create graphics 2d instance
        Graphics2D g = source.createGraphics();
        // draw shutterstock to it
        g.drawImage(stock, null, (source.getWidth() - stock.getWidth()) / 2, (source.getHeight() - stock.getHeight()) /2);
        // dispose of graphics
        g.dispose();
        // convert to byte array
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        ImageIO.write(source, "png", temp);
        byte[] done = temp.toByteArray();
        temp.close();
        // return
        return done;
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
        // setup the output stream
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        // produce the frames
        for (GIFFrame f : cont.getFrames()){
            // reset the stream
            temp.reset();
            // write our image to it
            ImageIO.write(f.getFrame(), "png", temp);
            // add new frame to list
            ByteArrayInputStream streamin = new ByteArrayInputStream(ShutterStock.Stockify(temp.toByteArray()));
            imgs.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
            streamin.close();
        }
        // reset stream
        temp.reset();
        // write gif to it
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert to byte array and close it
        byte[] done = temp.toByteArray();
        temp.close();
        // output it
        return done;
    }
}
