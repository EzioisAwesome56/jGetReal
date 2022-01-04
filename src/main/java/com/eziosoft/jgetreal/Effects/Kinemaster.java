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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Kinemaster extends ImageEffect {

    /**
     * applies kinemaster watermark to an image
     * @param in image to process
     * @return processed image
     * @throws IOException if something goes skerpow bang kablam
     */
    public static EffectResult Watermark(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? Gif(in) : Raster(in), type.contains("gif") ? "gif" : "png");
    }


    /**
     * you get the point
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something blows up during the process lmao
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Watermark(input);
    }

    public Kinemaster(){
        this.name = "kinemaster";
    }

    /**
     * applies kinemaster watermark to an image
     * @param in image to watermark
     * @return watermarked image
     * @throws IOException if something blows up during the process
     */
    private static byte[] Raster(byte[] in) throws IOException{
        // turn imageio caching off
        ImageIO.setUseCache(false);
        // load source image, and mirror it
        InputStream tempin = new ByteArrayInputStream(in);
        BufferedImage source = RasterUtils.MirrorImage(ImageIO.read(tempin));
        tempin.close();
        // create graphics context
        Graphics2D g = source.createGraphics();
        // load watermark
        tempin = Kinemaster.class.getResourceAsStream("/kinemaster.png");
        BufferedImage watermark = ImageIO.read(tempin);
        tempin.close();
        // draw watermark
        g.drawImage(watermark, null, 0, 0);
        // get rid of graphics 2d
        g.dispose();
        // unmirror the image
        source = RasterUtils.MirrorImage(source);
        // convert to byte array and return that
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(source, "png", out);
        byte[] done = out.toByteArray();
        out.close();
        return done;
    }

    /**
     * applies kinemaster watermark to gif
     * @param in gif to watermark
     * @return watermarked gif
     * @throws IOException if something blows up somewhere during this operation
     */
    private static byte[] Gif(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif container
        ByteArrayInputStream streamin = new ByteArrayInputStream(in);
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(streamin);
        streamin.close();
        // make array to hold processed frames
        List<GIFFrame> imgs = new ArrayList<>();
        // make byte array that will be reused over and over
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        //process every frame
        for (GIFFrame f : cont.getFrames()){
            // clear stream
            temp.reset();
            // use imageio to write current frame to it
            ImageIO.write(f.getFrame(), "png", temp);
            // create new GIF Frame from this data
            streamin = new ByteArrayInputStream(Raster(temp.toByteArray()));
            imgs.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_PREVIOUS));
            streamin.close();
        }
        // reset the stream
        temp.reset();
        // write animated gif to it
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert stream to byte array
        byte[] done = temp.toByteArray();
        // close stream
        temp.close();
        // and we're done
        return done;
    }
}
