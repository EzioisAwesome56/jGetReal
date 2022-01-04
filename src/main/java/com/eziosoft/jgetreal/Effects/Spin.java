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

public class Spin extends ImageEffect {

    /**
     * detects image format and spins it 360 degrees
     * @param in image to spin
     * @return spinned image; always returns a gif
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult spinImage(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? spinGif(in) : spinRaster(in), "gif");
    }

    /**
     * creates an animated gif of the provided raster image spinning 360 degrees
     * @param in byte array of raster image to spin
     * @return byte array of animated gif
     * @throws IOException if something blows up during generation
     */
    private static byte[] spinRaster(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // create list of buffered images to be filled later
        List<GIFFrame> imgs = new ArrayList<>();
        // read source image from provided byte array
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // get dimensions of source
        int h = source.getHeight();
        int w = source.getWidth();
        // create all the frames
        for (int a = 0; a <= 360; a += 15){
            // create new buffered image
            BufferedImage temp = new BufferedImage(w, h, source.getType());
            // get graphics 2d for this image
            Graphics2D g = temp.createGraphics();
            // rotate graphics
            g.rotate(Math.toRadians(a), w/2, h/2);
            // draw source to it
            g.drawImage(source, null, 0, 0);
            // dispose of graphics
            g.dispose();
            // add temp to the arraylist
            imgs.add(new GIFFrame(temp, 50, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
        }
        // create new byteoutputstream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // write gif to stream
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), out);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert tp byte array
        byte[] done = out.toByteArray();
        // close stream
        out.close();
        // return
        return done;
    }

    /**
     * spins a gif 360 degrees
     * @param in byte array of animated gif
     * @return byte array of processed gif
     * @throws IOException if something blows up
     */
    private static byte[] spinGif(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // first, we need to get every frame of the gif. Do that
        ByteArrayInputStream streamin = new ByteArrayInputStream(in);
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(streamin);
        streamin.close();
        // determine step for each spin frame
        double step = Math.ceil(360D / cont.getFrames().size());
        // make list to store process frames
        List<GIFFrame> imgs = new ArrayList<>();
        // setup counter
        int count = 0;
        // process each frame of the gif
        for (double x = 0; x <= 360D; x += step){
            // load the current frame into a buffered image
            /* jan 2nd 2022 fix: if the count goes over the total number of frames, reuse the last frame
                in theory this shouldnt happen, however it did in my testing so now i have to fix it like this
                cant really fix it any other way as its caused by rounding errors; so its a moot point
             */
            if (count > cont.getFrames().size() - 1) count -= 1;
            BufferedImage source = cont.getFrames().get(count).getFrame();
            // create new buffered image
            BufferedImage temp = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
            // get graphics 2d for this image
            Graphics2D g = temp.createGraphics();
            // rotate graphics
            g.rotate(Math.toRadians(x), source.getWidth()/2, source.getHeight()/2);
            // draw source to it
            g.drawImage(source, null, 0, 0);
            // dispose of graphics
            g.dispose();
            // add temp to the arraylist
            imgs.add(new GIFFrame(temp, cont.getFrames().get(count).getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_BACKGROUND));
            // add 1 to the counter
            count++;
        }
        // create output stream to store gif in
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // write gif to stream
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), out);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert tp byte array
        byte[] done = out.toByteArray();
        // close stream
        out.close();
        // return
        return done;
    }

    /**
     * did you know: Windows 98 is pretty cool
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something blows up during the process
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return spinImage(input);
    }

    public Spin(){
        this.name = "spin";
    }
}
