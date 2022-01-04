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

public class Invert extends ImageEffect {

    /**
     * detects image format and inverts it
     * @param in image to invert
     * @return inverted image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Invert(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? InvertGif(in) : InvertColors(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * inverts colors of an image
     * @param in image to invert
     * @return inverted image; png format
     * @throws IOException if something blows up in the process
     */
    private static byte[] InvertColors(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // load source image
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // create new image with same size as source image
        BufferedImage out = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        // loop thru every pixel of source
        for (int y = 0; y < source.getHeight(); y++){
            for (int x = 0; x < source.getWidth(); x++){
                // read color of pixel
                Color col = new Color(source.getRGB(x, y), true);
                // create new color, but invert it
                Color outcol = new Color(Math.abs(col.getRed() - 255), Math.abs(col.getGreen() - 255), Math.abs(col.getBlue() - 255), col.getAlpha());
                // output color to output buffered image
                out.setRGB(x, y, outcol.getRGB());
            }
        }
        // create stream and write output to it
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(out, "png", stream);
        // convert, close, return
        byte[] done = stream.toByteArray();
        stream.close();
        return done;
    }

    /**
     * inverts every frame of an animated gif
     * @param in byte array of animated gif to invert
     * @return inverted gif
     * @throws IOException throws if something blows up in the process
     */
    private static byte[] InvertGif(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif frames as container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // create new list for processes frames
        List<GIFFrame> imgs = new ArrayList<>();
        // create reusable stream
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        // process every frame
        for (GIFFrame f : cont.getFrames()){
            // reset stream
            temp.reset();
            // write frame to stream
            ImageIO.write(f.getFrame(), "png", temp);
            // add frame once processed
            ByteArrayInputStream streamin = new ByteArrayInputStream(InvertColors(temp.toByteArray()));
            imgs.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() * 10, f.getDisposalMethod()));
            streamin.close();
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

    /**
     * i am not copying the same goddamn text again; go read my other javadoc comments
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something explodes
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Invert(input);
    }

    public Invert(){
        this.name = "invert";
    }
}
