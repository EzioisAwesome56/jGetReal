package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Objects.ImageEffect;
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

public class Hypercam2 extends ImageEffect {

    /**
     * to be used by the effect gui/cli
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something blows up somewhere
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Watermark(input);
    }

    public Hypercam2(){
        this.name = "hypercam2";
    }

    /**
     * applies the hypercam2 watermark to provided image
     * @param in image to watermark
     * @return watermarked image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Watermark(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? UnregisterGif(in) : Unregister(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * applies the unregistered hypercam2 watermark to an image
     * @param in byte array of image to apply watermark too
     * @return byte array of watermarked image; png format
     * @throws IOException if something blows up
     */
    private static byte[] Unregister(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // load source image from byte array
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // load hypercam2 watermark from resources
        streamin = Hypercam2.class.getResourceAsStream("/hypercam.png");
        BufferedImage hypercam = ImageIO.read(streamin);
        streamin.close();
        // create graphics 2d context of the source image
        Graphics2D g = source.createGraphics();
        // draw hypercam onto the source image at 0,0
        g.drawImage(hypercam, null, 0, 0);
        // throw out graphics 2d
        g.dispose();
        // create new stream to write too
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // write to said stream
        ImageIO.write(source, "png", out);
        // convert to byte array
        byte[] done = out.toByteArray();
        // close stream
        out.close();
        // return
        return done;
    }

    /**
     * applies unregistered hypercam 2 watermark to an animated gif
     * @param in source animated gif to watermark
     * @return anmimated gif with watermark applied
     * @throws IOException in case something blows up
     */
    private static byte[] UnregisterGif(byte[] in) throws IOException {
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
            streamin = new ByteArrayInputStream(Hypercam2.Unregister(temp.toByteArray()));
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
