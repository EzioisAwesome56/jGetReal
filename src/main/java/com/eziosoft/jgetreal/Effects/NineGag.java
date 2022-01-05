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

public class NineGag  extends ImageEffect {

    /**
     * detects image format and processes it
     * @param in image to process
     * @return processes image
     * @throws IOException if something blows up
     */
    public static EffectResult Watermark(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? Gif(in) : Raster(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * fuck you baltimore!
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something goes kaboom
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Watermark(input);
    }

    public NineGag(){
        this.name = "9gag";
    }

    /**
     * applies 9gag watermark to a gif
     * @param in gif to watermark
     * @return watermarked gif
     * @throws IOException if something blows up during the process
     */
    private static byte[] Gif(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // get gif in container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // make new gif frame list
        List<GIFFrame> imgs = new ArrayList<>();
        // produce the frames
        for (GIFFrame f : cont.getFrames()){
            imgs.add(new GIFFrame(RasterUtils.ConvertToImage(Raster(RasterUtils.ConvertToBytes(f.getFrame()))), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
        }
        // write gif to it
        return GifUtils.ConvertToBytes(imgs);
    }

    /**
     * applies 9gag watermark to a raster image
     * @param in image to watermark
     * @return watermarked image
     * @throws IOException if something blows up
     */
    private static byte[] Raster(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // create buffered image of the source
        BufferedImage source = RasterUtils.ConvertToImage(in);
        // load watermark
        BufferedImage watermark = RasterUtils.loadResource("/9gag.png");
        // flip source
        source = RasterUtils.MirrorImage(source);
        // create graphics 2d
        Graphics2D g = source.createGraphics();
        // draw watermark
        g.drawImage(watermark, null, 0, (source.getHeight() - watermark.getHeight()) / 2);
        // dipose of graphics2d
        g.dispose();
        // flip image again
        source = RasterUtils.MirrorImage(source);
        // write output to stream
        return RasterUtils.ConvertToBytes(source);
    }
}
