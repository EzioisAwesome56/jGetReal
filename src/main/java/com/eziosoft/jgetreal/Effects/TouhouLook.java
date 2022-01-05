package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TouhouLook extends ImageEffect {

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
        return new EffectResult(type.contains("gif") ? TouhoulookGif(in) : Look(in), type.contains("gif") ? "gif" : "png");
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
        BufferedImage source = RasterUtils.ConvertToImage(in);
        // load watermark
        BufferedImage watermark = RasterUtils.loadResource("/2hulook.png");
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
        return RasterUtils.ConvertToBytes(source);
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
        // process every frame
        for (GIFFrame f : cont.getFrames()) {
            // add frame once processed
            imgs.add(new GIFFrame(RasterUtils.ConvertToImage(Look(RasterUtils.ConvertToBytes(f.getFrame()))), f.getDelay() * 10, f.getDisposalMethod()));
        }
        return GifUtils.ConvertToBytes(imgs);
    }

    /**
     * did you know: x86 assembly is a curse to man. why does it exist in the first place
     * @param input image to process
     * @param caption caption text if required
     * @return watermarked image
     * @throws IOException
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Watermark(input);
    }

    public TouhouLook(){
        this.name = "touhoulook";
    }
}
