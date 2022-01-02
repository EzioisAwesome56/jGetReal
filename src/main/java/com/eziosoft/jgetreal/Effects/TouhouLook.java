package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
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

public class TouhouLook {

    /**
     * detects image format and applies touhou watermark to it
     * @param in image to watermark
     * @return watermarked image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Watermark(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? TouhoulookGif(in) : Look(in), "gif");
    }

    /**
     * makes 2 touhou characters look at an image
     * @param in image to watermark
     * @return watermarked image, png format
     * @throws IOException if something blows up during the process
     */
    private static byte[] Look(byte[] in) throws IOException {
        // set imageio cache to OFF
        ImageIO.setUseCache(false);
        // make input stream
        InputStream streamin;
        // load source image
        streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // load watermark
        streamin = TouhouLook.class.getResourceAsStream("/2hulook.png");
        BufferedImage watermark = ImageIO.read(streamin);
        streamin.close();
        // rotate the source image
        source = RasterUtils.VerticalFlip(source);
        // create graphics instance
        Graphics2D g = source.createGraphics();
        // draw watermark
        g.drawImage(watermark, null, 0, 0);
        // delete graphics instance
        g.dispose();
        // flip again
        source = RasterUtils.VerticalFlip(source);
        // output result
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(source, "png", out);
        byte[] done = out.toByteArray();
        out.close();
        return done;
    }

    /**
     * applies touhou looking watermark to animated gif
     * @param in byte array of animated gif to watermark
     * @return watermarked image
     * @throws IOException throws if something blows up in the process
     */
    private static byte[] TouhoulookGif(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif frames as container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // create new list for processes frames
        List<GIFFrame> imgs = new ArrayList<>();
        // create reusable stream
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        // process every frame
        for (GIFFrame f : cont.getFrames()) {
            // reset stream
            temp.reset();
            // write frame to stream
            ImageIO.write(f.getFrame(), "png", temp);
            // add frame once processed
            ByteArrayInputStream stream = new ByteArrayInputStream(TouhouLook.Look(temp.toByteArray()));
            imgs.add(new GIFFrame(ImageIO.read(stream), f.getDelay() * 10, f.getDisposalMethod()));
            stream.close();
        }
        // reset stream
        temp.reset();
        // output gif to the stream
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert to array, close, return
        byte[] done = temp.toByteArray();
        temp.close();
        return done;
    }
}
